package com.thevoxelbox.voxelborder;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.google.gson.Gson;

/**
 * Handles all currently active zones. 
 * Also handles serialization.
 * 
 * @author TheCryoknight
 */
public class ZoneManager {
	private static final File ZONE_FILE = new File("plugins/voxelborder/zones.json");
	private static ZoneManager manager;

	public static synchronized ZoneManager getManager() {
		if (manager == null) {
			manager = new ZoneManager();
		}
		return manager;
	}
	private final String bacePerm = "voxelborder.zone.";

	private Set<Zone> zones = new HashSet<Zone>();
	private ZoneManager() {
		File zoneFolder = new File("plugins/voxelborder/");
		if (!zoneFolder.exists()) {
			zoneFolder.mkdirs();
		}
		readZones(ZONE_FILE);
	}

	public void addZone(Zone newZone) {
		this.zones.add(newZone);
	}

	public boolean canMoveTo(Player player, Location startLoc, Location endLoc) {
		for(Zone zone : this.zones) {
			if (zone.getWorldID().equals(endLoc.getWorld().getUID())) {
				if (zone.inBound(endLoc)) {
					if (zone.inBound(startLoc)) {
						return true;
					}
					if (player.isOp() ? true : player.hasPermission(this.bacePerm + zone.getName().replaceAll(" ", ""))) {
						player.sendMessage("Now crossing border of " + zone.getName().trim());
						return true;
					} else {
						player.sendMessage("You can not cross the border of " + zone.getName().trim());
						return false;
					}
				}
			}
		}
		player.sendMessage("You can not access area outside of the the borders");
		return false;
	}

	public void readZones(File zoneFile) {
		Gson gson = new Gson();
		if (zoneFile.exists()) {
			Scanner scan;
			try {
				scan = new Scanner(zoneFile);
			} catch (Exception e) {
				VoxelBorder.log.severe("Can not open config files");
				e.printStackTrace();
				return;
			}
			try {
				while(scan.hasNext()) {
					Zone zone = gson.fromJson(scan.nextLine(), Zone.class);
					zones.add(zone);
				}
			} catch (Exception e) {
				VoxelBorder.log.severe("Can not read config files");
				e.printStackTrace();
				return;
			} finally {
				scan.close();
			}
		} else {
			
		}
	}
	
	public List<String> lookupZone(String str) {
		List<String> matches = new ArrayList<String>();
		for(Zone z : zones) {
			if(z.getName().toLowerCase().startsWith(str)) {
				matches.add(z.getName());
			}
		}
		return matches.isEmpty() ? null : matches;
	}
	public Zone getZone(String zoneName) {
		for(Zone z : zones) {
			if(z.getName().equalsIgnoreCase(zoneName)) {
				return z;
			}
		}
		return null;
	}

	public boolean removeZone(Zone oldZone) {
		return this.zones.remove(oldZone);
	}
	public void saveZones(File zonefile) {
		Gson gson = new Gson();
		if(zonefile.exists()) {
			try {
				zonefile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		} else {
			if (!zonefile.delete()) {
				VoxelBorder.log.severe("Can not save config files");
			}
			try {
				zonefile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
		PrintWriter pw;
		try {
			pw = new PrintWriter(zonefile);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		for(Zone z : this.zones) {
			try {
				pw.print(gson.toJson(z) + "\n");
			} finally {
				pw.close(); 
			}
		}
	}

	public static File getZoneFile() {
		return ZONE_FILE;
	}
}