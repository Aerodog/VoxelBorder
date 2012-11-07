package com.thevoxelbox.voxelborder;

import com.thevoxelbox.voxelborder.util.Admin;

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

    public static TreeMap<UUID, ArrayList<Zone>> zones = new TreeMap<UUID, ArrayList<Zone>>();

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
        if (zones.containsKey(event.getTo().getWorld().getUID())) {
            for (Zone z : zones.get(event.getTo().getWorld().getUID())) {
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
            if (zones.containsKey(event.getFrom().getWorld().getUID())) {
                for (Zone z : zones.get(event.getTo().getWorld().getUID())) {
                    if (z.denyTP(event.getFrom(), event.getTo(), event.getPlayer())) {
                        event.setCancelled(true);
                    }
                }
            }
        } else {
            if (zones.containsKey(event.getTo().getWorld().getUID())) {
                for (Zone z : zones.get(event.getTo().getWorld().getUID())) {
                    if (z.denyTPto(event.getTo(), event.getPlayer())) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    public static void saveData() throws FileNotFoundException {
        File f = new File("plugins/VoxelBorder/borders.config");
        if (!f.exists()) {
            f.getParentFile().mkdirs();
            try {
                f.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(BorderListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        PrintWriter pw = new PrintWriter(f);
        pw.println("#VoxelBorder 2.0 Configuration");
        for (ChatColor cc : ChatColor.values()) {
            pw.println("# " + cc.name() + "  -  " + cc.toString());
        }
        Map<String, String> lists = Admin.getNamePath();
        if (lists.isEmpty()) {
            pw.println("Lists:");
            pw.println("\t*");
        } else {
            pw.println("Lists:");
            for (Entry<String, String> en : lists.entrySet()) {
                pw.println("List:");
                pw.println("\tname: " + en.getKey());
                pw.println("\tpath: " + en.getValue());
            }
        }
        if (!zones.isEmpty()) {
            for (ArrayList<Zone> ar : zones.values()) {
                for (Zone z : ar) {
                    z.save(pw);
                }
            }
        }
        pw.close();
        VoxelBorder.log.info("[VoxelBorder] Configuration saved!");
    }

    private void readData() throws FileNotFoundException {
        File f = new File("plugins/VoxelBorder/borders.config");
        if (!f.exists()) {
            f.getParentFile().mkdirs();
            try {
                f.createNewFile();
                saveData();
                return;
            } catch (IOException ex) {
                Logger.getLogger(BorderListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Scanner s = new Scanner(f);
        String line = "";
        while (s.hasNext()) {
            if (line.startsWith("#")) {
                line = s.nextLine().trim();
                continue;
            } else if (line.startsWith("Lists")) {
                line = s.nextLine().trim();
                if (line.equals("*")) {
                    line = s.nextLine().trim();
                    continue;
                }
                while (!line.startsWith("Border")) {
                    if (line.startsWith("List")) {
                        line = s.nextLine().trim();
                        String lname = null;
                        if (line.startsWith("name")) {
                            lname = line.split(":")[1].trim();
                            line = s.nextLine().trim();
                        }
                        if (line.startsWith("path")) {
                            Admin.readList(line.split(":")[1].trim(), lname);
                        }
                        if (!s.hasNext()) {
                            break;
                        }
                        line = s.nextLine().trim();
                    } else {
                        break;
                    }
                }
                continue;
            } else if (line.startsWith("Border")) {
                line = s.nextLine().trim();
                Zone z = null;
                if (line.startsWith("name")) {
                    z = new Zone(line.split(":")[1]);
                    line = s.nextLine().trim();
                }
                if (line.startsWith("world")) {
                    String[] spl = line.split(":");
                    z.read(spl[0], spl[1]);
                    line = s.nextLine().trim();
                }
                if (line.startsWith("highx")) {
                    String[] spl = line.split(":");
                    z.read(spl[0], spl[1]);
                    line = s.nextLine().trim();
                }
                if (line.startsWith("highz")) {
                    String[] spl = line.split(":");
                    z.read(spl[0], spl[1]);
                    line = s.nextLine().trim();
                }
                if (line.startsWith("lowx")) {
                    String[] spl = line.split(":");
                    z.read(spl[0], spl[1]);
                    line = s.nextLine().trim();
                }
                if (line.startsWith("lowz")) {
                    String[] spl = line.split(":");
                    z.read(spl[0], spl[1]);
                    line = s.nextLine().trim();
                }
                if (line.startsWith("inmsg")) {
                    String[] spl = line.split(":");
                    z.read(spl[0], spl[1]);
                    line = s.nextLine().trim();
                }
                if (line.startsWith("indeny")) {
                    String[] spl = line.split(":");
                    z.read(spl[0], spl[1]);
                    line = s.nextLine().trim();
                }
                if (line.startsWith("outmsg")) {
                    String[] spl = line.split(":");
                    z.read(spl[0], spl[1]);
                    line = s.nextLine().trim();
                }
                if (line.startsWith("outdeny")) {
                    String[] spl = line.split(":");
                    z.read(spl[0], spl[1]);
                    line = s.nextLine().trim();
                }
                if (line.startsWith("inTPmsg")) {
                    String[] spl = line.split(":");
                    z.read(spl[0], spl[1]);
                    line = s.nextLine().trim();
                }
                if (line.startsWith("inTPdeny")) {
                    String[] spl = line.split(":");
                    z.read(spl[0], spl[1]);
                    line = s.nextLine().trim();
                }
                if (line.startsWith("outTPmsg")) {
                    String[] spl = line.split(":");
                    z.read(spl[0], spl[1]);
                    line = s.nextLine().trim();
                }
                if (line.startsWith("outTPdeny")) {
                    String[] spl = line.split(":");
                    z.read(spl[0], spl[1]);
                    line = s.nextLine().trim();
                }
                if (line.startsWith("in")) {
                    line = s.nextLine().trim();
                    if (line.equals("*")) {
                        z.read("in", "*");
                        line = s.nextLine().trim();
                    } else {
                        while (!line.contains(":")) {
                            z.read("in", line);
                            if (!s.hasNext()) {
                                break;
                            }
                            line = s.nextLine().trim();
                        }
                    }
                }
                if (line.startsWith("out")) {
                    line = s.nextLine().trim();
                    if (line.equals("*")) {
                        z.read("out", "*");
                        line = s.nextLine().trim();
                    } else {
                        while (!line.contains(":")) {
                            z.read("out", line);
                            if (!s.hasNext()) {
                                break;
                            }
                            line = s.nextLine().trim();
                        }
                    }
                }
                if (line.startsWith("inTP")) {
                    line = s.nextLine().trim();
                    if (line.equals("*")) {
                        z.read("inTP", "*");
                        line = s.nextLine().trim();
                    } else {
                        while (!line.contains(":")) {
                            z.read("inTP", line);
                            if (!s.hasNext()) {
                                break;
                            }
                            line = s.nextLine().trim();
                        }
                    }
                }
                if (line.startsWith("outTP")) {
                    line = s.nextLine().trim();
                    if (line.equals("*")) {
                        z.read("outTP", "*");
                        if (s.hasNext()) {
                            line = s.nextLine().trim();
                        }
                    } else {
                        while (!line.contains(":")) {
                            z.read("outTP", line);
                            if (!s.hasNext()) {
                                break;
                            }
                            line = s.nextLine().trim();
                        }
                    }
                }
                z.finishRead();
                World w = null;
                for (World wor : Bukkit.getWorlds()) {
                    if (wor.getName().equals(z.getWorld())) {
                        w = wor;
                    }
                }
                if (w == null) {
                    w = new WorldCreator(z.getWorld()).createWorld();
                }
                if (zones.containsKey(w.getUID())) {
                    zones.get(w.getUID()).add(z);
                } else {
                    zones.put(w.getUID(), new ArrayList<Zone>());
                    zones.get(w.getUID()).add(z);
                }
            } else {
                if (!s.hasNext()) {
                    break;
                }
                line = s.nextLine().trim();
            }
        }
        s.close();
        VoxelBorder.log.info("[VoxelBorder] Configuration loaded!");
    }
}
