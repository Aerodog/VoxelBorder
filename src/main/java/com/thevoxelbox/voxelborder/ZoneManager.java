package com.thevoxelbox.voxelborder;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Handles all currently active zones.
 * 
 * @author TheCryoknight
 */
public class ZoneManager {
	private static ZoneManager manager;

	public static synchronized ZoneManager getManager() {
		if (manager == null) {
			manager = new ZoneManager(null);
		}
		return manager;
	}
	private final String bacePerm = "voxelborder.zone.";

	private Set<Zone> zones = new HashSet<Zone>();
	private ZoneManager(File zoneFile) {

	}

	public void addZone(Zone newZone) {
		this.zones.add(newZone);
	}

	public boolean canMoveTo(Player player, Location startLoc, Location endLoc) {
		for(Zone zone : this.zones) {
			if(zone.getWorldID().equals(endLoc.getWorld().getUID())) {
				if(zone.inBound(endLoc)) {
					if (zone.inBound(startLoc)) {
						return true;
					}
					if(player.hasPermission(this.bacePerm + zone.getName().replaceAll(" ", ""))) {
						player.sendMessage("Now crossing border of " + zone.getName().trim());
						return true;
					} else {
						player.sendMessage("You can not cross the border of " + zone.getName().trim());
						return false;
					}
				}
			}
		}
		return false;
	}

	public void readZones(File zoneFile) {

	}

	public void removeZone(Zone oldZone) {
		if(this.zones.contains(oldZone)) {
			this.zones.remove(oldZone);
		} else {
			VoxelBorder.log.warning("Cannot remove region, region not found.");
		}
	}
	public void saveZones(File zoneFile) {

	}
}