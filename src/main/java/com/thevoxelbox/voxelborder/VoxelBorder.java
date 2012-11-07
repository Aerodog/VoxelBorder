package com.thevoxelbox.voxelborder;

import com.thevoxelbox.voxelborder.util.Admin;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class VoxelBorder extends JavaPlugin {

    public static final Logger log = Logger.getLogger("Minecraft");
    private BorderListener p = new BorderListener();
    private BorderVehicle bv = new BorderVehicle();
    //private BorderEntity be = new BorderEntity();

    public void onDisable() {
    }

    public void onEnable() {
        Admin.readList("plugins/admns.txt", "admns");
        Bukkit.getPluginManager().registerEvents(p, this);
        Bukkit.getPluginManager().registerEvents(bv, this);
//        Bukkit.getPluginManager().registerEvent(Event.Type.PLAYER_MOVE, p, Priority.Normal, this);
//        Bukkit.getPluginManager().registerEvent(Event.Type.PLAYER_TELEPORT, p, Priority.Normal, this);
//        Bukkit.getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, p, Priority.Normal, this);
//        Bukkit.getPluginManager().registerEvent(Event.Type.VEHICLE_MOVE, bv, Priority.Normal, this);
//        Bukkit.getPluginManager().registerEvent(Event.Type.VEHICLE_ENTER, bv, Priority.Normal, this);
        //Bukkit.getPluginManager().registerEvent(Event.Type.PROJECTILE_HIT, be, Priority.Normal, this);
        p.init();
        PluginDescriptionFile pdfFile = this.getDescription();
        log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        String[] trimmedArgs = args;
        String commandName = command.getName().toLowerCase();

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if ((commandName.equals("voxelborder") || commandName.equals("vborder"))
                    && Admin.contains(Admin.getListID("admns"), player.getName())) {
                if (args != null && args.length > 0) {
                    if (args[0].equalsIgnoreCase("create")) {
                        if (args.length == 6) {
                            Zone z = new Zone(args[1]);
                            z.setBound(player.getWorld().getBlockAt(Integer.parseInt(args[2]), 64, Integer.parseInt(args[3])), player.getWorld().getBlockAt(Integer.parseInt(args[4]), 64, Integer.parseInt(args[5])));
                            z.setWorld(player.getWorld().getName());
                            if (BorderListener.zones.containsKey(player.getWorld().getUID())) {
                                BorderListener.zones.get(player.getWorld().getUID()).add(z);
                            } else {
                                BorderListener.zones.put(player.getWorld().getUID(), new ArrayList<Zone>());
                                BorderListener.zones.get(player.getWorld().getUID()).add(z);
                            }
                            try {
                                BorderListener.saveData();
                                player.sendMessage(ChatColor.GREEN + "New border created!");
                            } catch (FileNotFoundException ex) {
                                player.sendMessage(ChatColor.RED + "Error saving borders!");
                                Logger.getLogger(VoxelBorder.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            return true;
                        } else {
                            player.sendMessage("Not enough parameters /.. create name x z x z");
                            return true;
                        }
                    }
                } else {
                    player.sendMessage("Derp herp something");
                    return true;
                }
            }
            if ((commandName.equalsIgnoreCase("btp")) && (Admin.hasList("admins") ? Admin.contains(Admin.getListID("admns"), player.getName()) : false)) {
                if (args != null && args.length > 0) {

                    List<Player> l = Bukkit.matchPlayer(trimmedArgs[0]);
                    if (l.size() > 1) {
                        player.sendMessage(ChatColor.RED + "Partial match");
                    } else if (l.isEmpty()) {
                        player.sendMessage(ChatColor.RED + "No player to match");
                    } else {
                        Player pl = l.get(0);
                        Location loc = pl.getLocation();

                        player.sendMessage(ChatColor.AQUA + "Woosh!");

                        if (args.length < 2) {
                            player.teleport(loc, TeleportCause.ENDER_PEARL);
                        } else {
                            if (args[1].matches("me")) {
                                pl.sendMessage(ChatColor.DARK_AQUA + "Woosh!");
                                pl.teleport(player.getLocation(), TeleportCause.ENDER_PEARL);
                                return true;
                            }

                            for (int i = 1; i < args.length; i++) {
                                try {
                                    if (args[i].startsWith("x")) {
                                        loc.setX(loc.getX() + Double.parseDouble(args[i].replace("x", "")));
                                        continue;
                                    } else if (args[i].startsWith("y")) {
                                        loc.setY(loc.getY() + Double.parseDouble(args[i].replace("y", "")));
                                        continue;
                                    } else if (args[i].startsWith("z")) {
                                        loc.setZ(loc.getZ() + Double.parseDouble(args[i].replace("z", "")));
                                        continue;
                                    }
                                } catch (NumberFormatException e) {
                                    player.sendMessage(ChatColor.RED + "Error parsing argument \"" + args[i] + "\"");
                                    return true;
                                }
                            }

                            player.teleport(loc, TeleportCause.ENDER_PEARL);
                        }
                    }
                    return true;
                } else {
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "Please specify the target player");
                    return true;
                }
            }
            return false;
        }
        return false;
    }
}
