package com.thevoxelbox.voxelborder;

import java.io.Serializable;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.block.Block;


/**
 * Represents a 2D Rectangle 
 * 
 * @author TheCryoknight
 * 
 */
public class Zone implements Serializable {
	
	private static final long serialVersionUID = -640268786266506912L;
	private final String zoneName;
	private final UUID worldID;
	private final int lowX;
	private final int lowZ;
	private final int highX;
	private final int highZ;

	public Zone(final String zoneName, final int x1, final int z1, final int x2, final int z2, final UUID worldID) {
		this.worldID = worldID;
		this.zoneName = zoneName;
		if((x2 > x1) && (z2 > z1)) {
			this.lowX = x1;
			this.lowZ = z1;
			this.highX = x2;
			this.highZ = z2;
			return;
		} else if((x2 < x1) && (z2 < z1)) {
			this.highX = x1;
			this.highZ = z1;
			this.lowX = x2;
			this.lowZ = z2;
			return;
		} else if ((x2 < x1) && (z2 > z1)) {
			this.highX = x1;
			this.lowZ = z1;
			this.lowX = x2;
			this.highZ = z2;
			return;
		} else if((x2 > x1) && (z2 < z1)) {
			this.highX = x1;
			this.lowZ = z1;
			this.lowX = x2;
			this.highZ = z2;
			return;
		} else {
			this.lowX = x1;
			this.lowZ = z1;
			this.highX = x2;
			this.highZ = z2;
			return;
		}
	}

	public Zone(final String zoneName, final Location point1, final Location point2) {
		this.zoneName = zoneName;
		this.worldID = point1.getWorld().getUID();
		final int x1 = point1.getBlockX(), z1 = point1.getBlockZ(), x2 = point2.getBlockX(), z2 = point2.getBlockZ();
		if((x2 > x1) && (z2 > z1)) {
			this.lowX = x1;
			this.lowZ = z1;
			this.highX = x2;
			this.highZ = z2;
			return;
		} else if((x2 < x1) && (z2 < z1)) {
			this.highX = x1;
			this.highZ = z1;
			this.lowX = x2;
			this.lowZ = z2;
			return;
		} else if ((x2 < x1) && (z2 > z1)) {
			this.highX = x1;
			this.lowZ = z1;
			this.lowX = x2;
			this.highZ = z2;
			return;
		} else if((x2 > x1) && (z2 < z1)) {
			this.highX = x1;
			this.lowZ = z1;
			this.lowX = x2;
			this.highZ = z2;
			return;
		} else {
			this.lowX = x1;
			this.lowZ = z1;
			this.highX = x2;
			this.highZ = z2;
			return;
		}
	}

	public int getLowX() {
		return this.lowX;
	}

	public int getLowZ() {
		return this.lowZ;
	}

	public String getName() {
		return this.zoneName;
	}

	public UUID getWorldID() {
		return this.worldID;
	}

	public int highX() {
		return this.highX;
	}

	public int highZ() {
		return this.highZ;
	}

	/**
	 * Checks if a block is located inside of this region.
	 * 
	 * @param b Block to check
	 * @return True if inside region
	 */
	public boolean inBound(Block b) {
		return (this.lowX <= b.getX()) && (this.lowZ <= b.getZ()) && (b.getX() <= this.highX) && (b.getZ() <= this.highZ);
	}

	/**
	 * Checks if a X and Z coordinate is inside a region.
	 * 
	 * @param x X coordinate to check
	 * @param z Z coordinate to check
	 * @return True if inside region
	 */
	public boolean inBound(int x, int z) {
		return (this.lowX <= x) && (this.lowZ <= z) && (x <= this.highX) && (z <= this.highZ);
	}

	/**
	 * Checks if a location is inside of this region.
	 * 
	 * @param loc Location to check
	 * @return True if inside region
	 */
	public boolean inBound(Location loc) {
		return (this.lowX <= loc.getX()) && (this.lowZ <= loc.getZ()) && (loc.getX() <= this.highX) && (loc.getZ() <= this.highZ);
	}
}
