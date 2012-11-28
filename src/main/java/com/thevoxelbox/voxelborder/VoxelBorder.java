package com.thevoxelbox.voxelborder;

import java.util.ArrayList;
import java.util.List;
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

	public static Logger log;
	private BorderListener bListner = new BorderListener();

	public VoxelBorder() {
		
	}
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		String commandName = command.getName().toLowerCase();
		if (commandName.equalsIgnoreCase("voxelborder") || commandName.equalsIgnoreCase("vborder")) {
			if(args.length > 0) {
				if(args.length == 1) {
					List<String> tabList = new ArrayList<String>();
					if(args[0].toLowerCase().startsWith("c")) {
						tabList.add("create");
						return tabList;
					} else if(args[0].startsWith("r")) {
						tabList.add("remove");
						return tabList;
					}
				}
				if(args.length == 2) {
					if(args[0].equalsIgnoreCase("remove")) {
						return ZoneManager.getManager().lookupZone(args[1].toLowerCase());
					}
				}
			}
		}
        return null;
    }
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		String commandName = command.getName().toLowerCase();

		if (sender instanceof Player) {
			Player player = (Player) sender;

			if ((commandName.equalsIgnoreCase("voxelborder") || commandName.equalsIgnoreCase("vborder"))
					&& (player.isOp() ? true : player.hasPermission("voxelborder.editzones"))) {
				if ((args != null) && (args.length > 0)) {
					if (args[0].equalsIgnoreCase("create")) {
						if (args.length == 6) {
							final int x1, z1, x2, z2;
							try {
								x1 = Integer.parseInt(args[2]);
								z1 = Integer.parseInt(args[3]);
								x2 = Integer.parseInt(args[4]);
								z2 = Integer.parseInt(args[5]);
							} catch(Exception e) {
								sender.sendMessage("Incorrect parameters /vBorder <create:remove> [name] x z x z");
								return true;
							}
							ZoneManager.getManager().addZone(new Zone(args[1], x1, z1, x2, z2, player.getWorld().getUID()));
						} else {
							player.sendMessage("Not enough parameters /vBorder <create:remove> [name] x z x z");
							return true;
						}
					} if (args[0].equalsIgnoreCase("remove")) {
						if(args.length == 2) {
							Zone oldZone = ZoneManager.getManager().getZone(args[1]);
							if(oldZone != null) {
								ZoneManager.getManager().removeZone(oldZone);
								return true;
							} else {
								player.sendMessage("No zone found by name: " + args[1]);
							}
						} else {
							player.sendMessage("Incorrect parameters /vBorder <create:remove> [name] x z x z");
							return true;
						}
					}
				} else {
					return true;
				}
			}
			if ((commandName.equalsIgnoreCase("btp")) && (player.isOp() ? true : player.hasPermission("voxelborder.btp"))) {
				if ((args != null) && (args.length > 0)) {

					List<Player> matches = Bukkit.matchPlayer(args[0]);
					if (matches.size() > 1) {
						player.sendMessage(ChatColor.RED + "Partial match");
					} else if (matches.isEmpty()) {
						player.sendMessage(ChatColor.RED + "No player to match");
					} else {
						Player pl = matches.get(0);
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

	@Override
	public void onDisable() {
		ZoneManager.getManager().saveZones(ZoneManager.getZoneFile());
	}

	@Override
	public void onEnable() {
		log = this.getLogger();
		Bukkit.getPluginManager().registerEvents(this.bListner, this);
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
	}
}
