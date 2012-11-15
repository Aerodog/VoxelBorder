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
	private String zoneName;
	private UUID worldID;
	private final int lowX;
	private final int lowZ;
	private final int highX;
	private final int highZ;

	public Zone(final String zoneName, final int lowX, final int lowZ, final int highX, final int highZ, UUID worldID) {
		this.worldID = worldID;
		this.zoneName = zoneName;
		this.lowX = lowX;
		this.lowZ = lowZ;
		this.highX = highX;
		this.highZ = highZ;
	}

	public Zone(final String zoneName, final Location point1, final Location point2) {
		this.zoneName = zoneName;
		this.lowX = point1.getBlockX();
		this.lowZ = point1.getBlockZ();
		this.highX = point2.getBlockX();
		this.highZ = point2.getBlockZ();
		this.worldID = point1.getWorld().getUID();
	}

	public void finishRead() {
		VoxelBorder.log.info("[VoxelBorder] Border \"" + this.zoneName + "\" has been loaded!");
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

	public boolean inBound(Block b) {
		return (this.lowX <= b.getX()) && (this.lowZ <= b.getZ()) && (b.getX() <= this.highX) && (b.getZ() <= this.highZ);
	}

	public boolean inBound(int x, int z) {
		return (this.lowX <= x) && (this.lowZ <= z) && (x <= this.highX) && (z <= this.highZ);
	}

	public boolean inBound(Location l) {
		return (this.lowX <= l.getX()) && (this.lowZ <= l.getZ()) && (l.getX() <= this.highX) && (l.getZ() <= this.highZ);
	}
}
