package com.thevoxelbox.voxelborder;

import com.thevoxelbox.voxelborder.util.VoxelAdminUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 *
 * @author Voxel
 */
public class BorderListener implements Listener {

    private static TreeMap<UUID, ArrayList<Zone>> zones = new TreeMap<UUID, ArrayList<Zone>>();

    public static TreeMap<UUID, ArrayList<Zone>> getZones() {
		return zones;
	}

	public static void setZones(TreeMap<UUID, ArrayList<Zone>> zones) {
		BorderListener.zones = zones;
	}
	public static void addZone(UUID worldID, Zone newZone) {
		if(zones.containsKey(worldID)) {
			zones.get(worldID).add(newZone);
		} else {
			ArrayList<Zone> newWorld = new ArrayList<Zone>();
			newWorld.add(newZone);
			zones.put(worldID, newWorld);
		}
	}

	public void init() {
        try {
            readData();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BorderListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        try {
            if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                if (event.getItem().getType().equals(Material.ENDER_PEARL)) {
                    event.setCancelled(true);
                    event.setUseItemInHand(Result.DENY);
                }
            }
        } catch (Exception e) {
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (getZones().containsKey(event.getTo().getWorld().getUID())) {
            for (Zone z : getZones().get(event.getTo().getWorld().getUID())) {
                if (z.deny(event.getFrom(), event.getTo(), event.getPlayer())) {
                    event.getPlayer().teleport(event.getFrom(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL)) {
            return;
        }
        if (event.getFrom().getWorld().equals(event.getTo().getWorld())) {
            if (getZones().containsKey(event.getFrom().getWorld().getUID())) {
                for (Zone _zone : getZones().get(event.getTo().getWorld().getUID())) {
                    if (_zone.denyTP(event.getFrom(), event.getTo(), event.getPlayer())) {
                        event.setCancelled(true);
                    }
                }
            }
        } else {
            if (getZones().containsKey(event.getTo().getWorld().getUID())) {
                for (Zone _zone : getZones().get(event.getTo().getWorld().getUID())) {
                    if (_zone.denyTPto(event.getTo(), event.getPlayer())) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    public static void saveData() throws FileNotFoundException {
        File _configFile = new File("plugins/VoxelBorder/borders.config");
        if (!_configFile.exists()) {
            _configFile.getParentFile().mkdirs();
            try {
                _configFile.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(BorderListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        PrintWriter _writer = new PrintWriter(_configFile);
        _writer.println("#VoxelBorder 2.0 Configuration");
        for (ChatColor cc : ChatColor.values()) {
            _writer.println("# " + cc.name() + "  -  " + cc.toString());
        }
        Map<String, String> _lists = VoxelAdminUtil.getNamePath();
        if (_lists.isEmpty()) {
            _writer.println("Lists:");
            _writer.println("\t*");
        } else {
            _writer.println("Lists:");
            for (Entry<String, String> en : _lists.entrySet()) {
                _writer.println("List:");
                _writer.println("\tname: " + en.getKey());
                _writer.println("\tpath: " + en.getValue());
            }
        }
        if (!getZones().isEmpty()) {
            for (ArrayList<Zone> ar : getZones().values()) {
                for (Zone _z : ar) {
                    _z.save(_writer);
                }
            }
        }
        _writer.close();
        VoxelBorder.log.info("[VoxelBorder] Configuration saved!");
    }

    private void readData() throws FileNotFoundException {
        File _configFile = new File("plugins/VoxelBorder/borders.config");
        if (!_configFile.exists()) {
            _configFile.getParentFile().mkdirs();
            try {
                _configFile.createNewFile();
                saveData();
                return;
            } catch (IOException ex) {
                Logger.getLogger(BorderListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Scanner _scan = new Scanner(_configFile);
        String _line = "";
        while (_scan.hasNext()) {
            if (_line.startsWith("#")) {
                _line = _scan.nextLine().trim();
                continue;
            } else if (_line.startsWith("Lists")) {
                _line = _scan.nextLine().trim();
                if (_line.equals("*")) {
                    _line = _scan.nextLine().trim();
                    continue;
                }
                while (!_line.startsWith("Border")) {
                    if (_line.startsWith("List")) {
                        _line = _scan.nextLine().trim();
                        String lname = null;
                        if (_line.startsWith("name")) {
                            lname = _line.split(":")[1].trim();
                            _line = _scan.nextLine().trim();
                        }
                        if (_line.startsWith("path")) {
                            VoxelAdminUtil.readList(_line.split(":")[1].trim(), lname);
                        }
                        if (!_scan.hasNext()) {
                            break;
                        }
                        _line = _scan.nextLine().trim();
                    } else {
                        break;
                    }
                }
                continue;
            } else if (_line.startsWith("Border")) {
                _line = _scan.nextLine().trim();
                Zone _zone = null;
                if (_line.startsWith("name")) {
                    _zone = new Zone(_line.split(":")[1]);
                    _line = _scan.nextLine().trim();
                }
                if (_line.startsWith("world")) {
                    String[] spl = _line.split(":");
                    _zone.read(spl[0], spl[1]);
                    _line = _scan.nextLine().trim();
                }
                if (_line.startsWith("highx")) {
                    String[] spl = _line.split(":");
                    _zone.read(spl[0], spl[1]);
                    _line = _scan.nextLine().trim();
                }
                if (_line.startsWith("highz")) {
                    String[] spl = _line.split(":");
                    _zone.read(spl[0], spl[1]);
                    _line = _scan.nextLine().trim();
                }
                if (_line.startsWith("lowx")) {
                    String[] spl = _line.split(":");
                    _zone.read(spl[0], spl[1]);
                    _line = _scan.nextLine().trim();
                }
                if (_line.startsWith("lowz")) {
                    String[] spl = _line.split(":");
                    _zone.read(spl[0], spl[1]);
                    _line = _scan.nextLine().trim();
                }
                if (_line.startsWith("inmsg")) {
                    String[] spl = _line.split(":");
                    _zone.read(spl[0], spl[1]);
                    _line = _scan.nextLine().trim();
                }
                if (_line.startsWith("indeny")) {
                    String[] spl = _line.split(":");
                    _zone.read(spl[0], spl[1]);
                    _line = _scan.nextLine().trim();
                }
                if (_line.startsWith("outmsg")) {
                    String[] spl = _line.split(":");
                    _zone.read(spl[0], spl[1]);
                    _line = _scan.nextLine().trim();
                }
                if (_line.startsWith("outdeny")) {
                    String[] spl = _line.split(":");
                    _zone.read(spl[0], spl[1]);
                    _line = _scan.nextLine().trim();
                }
                if (_line.startsWith("inTPmsg")) {
                    String[] spl = _line.split(":");
                    _zone.read(spl[0], spl[1]);
                    _line = _scan.nextLine().trim();
                }
                if (_line.startsWith("inTPdeny")) {
                    String[] spl = _line.split(":");
                    _zone.read(spl[0], spl[1]);
                    _line = _scan.nextLine().trim();
                }
                if (_line.startsWith("outTPmsg")) {
                    String[] spl = _line.split(":");
                    _zone.read(spl[0], spl[1]);
                    _line = _scan.nextLine().trim();
                }
                if (_line.startsWith("outTPdeny")) {
                    String[] spl = _line.split(":");
                    _zone.read(spl[0], spl[1]);
                    _line = _scan.nextLine().trim();
                }
                if (_line.startsWith("in")) {
                    _line = _scan.nextLine().trim();
                    if (_line.equals("*")) {
                        _zone.read("in", "*");
                        _line = _scan.nextLine().trim();
                    } else {
                        while (!_line.contains(":")) {
                            _zone.read("in", _line);
                            if (!_scan.hasNext()) {
                                break;
                            }
                            _line = _scan.nextLine().trim();
                        }
                    }
                }
                if (_line.startsWith("out")) {
                    _line = _scan.nextLine().trim();
                    if (_line.equals("*")) {
                        _zone.read("out", "*");
                        _line = _scan.nextLine().trim();
                    } else {
                        while (!_line.contains(":")) {
                            _zone.read("out", _line);
                            if (!_scan.hasNext()) {
                                break;
                            }
                            _line = _scan.nextLine().trim();
                        }
                    }
                }
                if (_line.startsWith("inTP")) {
                    _line = _scan.nextLine().trim();
                    if (_line.equals("*")) {
                        _zone.read("inTP", "*");
                        _line = _scan.nextLine().trim();
                    } else {
                        while (!_line.contains(":")) {
                            _zone.read("inTP", _line);
                            if (!_scan.hasNext()) {
                                break;
                            }
                            _line = _scan.nextLine().trim();
                        }
                    }
                }
                if (_line.startsWith("outTP")) {
                    _line = _scan.nextLine().trim();
                    if (_line.equals("*")) {
                        _zone.read("outTP", "*");
                        if (_scan.hasNext()) {
                            _line = _scan.nextLine().trim();
                        }
                    } else {
                        while (!_line.contains(":")) {
                            _zone.read("outTP", _line);
                            if (!_scan.hasNext()) {
                                break;
                            }
                            _line = _scan.nextLine().trim();
                        }
                    }
                }
                _zone.finishRead();
                World _world = null;
                for (World wor : Bukkit.getWorlds()) {
                    if (wor.getName().equals(_zone.getWorld())) {
                        _world = wor;
                    }
                }
                if (_world == null) {
                    _world = new WorldCreator(_zone.getWorld()).createWorld();
                }
                if (getZones().containsKey(_world.getUID())) {
                    getZones().get(_world.getUID()).add(_zone);
                } else {
                    getZones().put(_world.getUID(), new ArrayList<Zone>());
                    getZones().get(_world.getUID()).add(_zone);
                }
            } else {
                if (!_scan.hasNext()) {
                    break;
                }
                _line = _scan.nextLine().trim();
            }
        }
        _scan.close();
        VoxelBorder.log.info("[VoxelBorder] Configuration loaded!");
    }
}
