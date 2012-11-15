package com.thevoxelbox.voxelborder;

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

	public static final Logger log = Logger.getLogger("Minecraft");
	private BorderListener bListner = new BorderListener();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		String _commandName = command.getName().toLowerCase();

		if (sender instanceof Player) {
			Player _player = (Player) sender;

			if ((_commandName.equals("voxelborder") || _commandName.equals("vborder"))
					&& (_player.isOp() ? true : _player.hasPermission("voxelborder.editzones"))) {
				if ((args != null) && (args.length > 0)) {
					if (args[0].equalsIgnoreCase("create")) {
						if (args.length == 6) {
							final int _x1, _z1, _x2, _z2;
							try {
								_x1 = Integer.parseInt(args[2]);
								_z1 = Integer.parseInt(args[3]);
								_x2 = Integer.parseInt(args[4]);
								_z2 = Integer.parseInt(args[5]);
							} catch(Exception e) {
								sender.sendMessage("Incorrect parameters /vBorder <create:remove> [name] x z x z");
								return true;
							}
							ZoneManager.getManager().addZone(new Zone(args[1], _x1, _z1, _x2, _z2, _player.getWorld().getUID()));
						} else {
							_player.sendMessage("Not enough parameters /vBorder <create:remove> [name] x z x z");
							return true;
						}
					}
				} else {
					return true;
				}
			}
			if ((_commandName.equalsIgnoreCase("btp")) && (_player.isOp() ? true : _player.hasPermission("voxelborder.btp"))) {
				if ((args != null) && (args.length > 0)) {

					List<Player> l = Bukkit.matchPlayer(args[0]);
					if (l.size() > 1) {
						_player.sendMessage(ChatColor.RED + "Partial match");
					} else if (l.isEmpty()) {
						_player.sendMessage(ChatColor.RED + "No player to match");
					} else {
						Player pl = l.get(0);
						Location _loc = pl.getLocation();

						_player.sendMessage(ChatColor.AQUA + "Woosh!");

						if (args.length < 2) {
							_player.teleport(_loc, TeleportCause.ENDER_PEARL);
						} else {
							if (args[1].matches("me")) {
								pl.sendMessage(ChatColor.DARK_AQUA + "Woosh!");
								pl.teleport(_player.getLocation(), TeleportCause.ENDER_PEARL);
								return true;
							}

							for (int _i = 1; _i < args.length; _i++) {
								try {
									if (args[_i].startsWith("x")) {
										_loc.setX(_loc.getX() + Double.parseDouble(args[_i].replace("x", "")));
										continue;
									} else if (args[_i].startsWith("y")) {
										_loc.setY(_loc.getY() + Double.parseDouble(args[_i].replace("y", "")));
										continue;
									} else if (args[_i].startsWith("z")) {
										_loc.setZ(_loc.getZ() + Double.parseDouble(args[_i].replace("z", "")));
										continue;
									}
								} catch (NumberFormatException e) {
									_player.sendMessage(ChatColor.RED + "Error parsing argument \"" + args[_i] + "\"");
									return true;
								}
							}

							_player.teleport(_loc, TeleportCause.ENDER_PEARL);
						}
					}
					return true;
				} else {
					_player.sendMessage(ChatColor.LIGHT_PURPLE + "Please specify the target player");
					return true;
				}
			}
			return false;
		}
		return false;
	}

	@Override
	public void onDisable() {
	}

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this.bListner, this);
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
	}
}
