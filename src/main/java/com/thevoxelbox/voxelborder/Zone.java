package com.thevoxelbox.voxelborder;

import java.io.Serializable;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.block.Block;

import com.thevoxelbox.voxelborder.util.Vector2D;

/**
 * Represents a 2D Rectangle
 * 
 * @author TheCryoknight
 * 
 */
public class Zone implements Serializable
{
    private static final long serialVersionUID = -640268786266506912L;
    
    private final String zoneName;
    private final UUID worldID;

    private final Vector2D min;
    private final Vector2D max;

    public Zone(final String zoneName, final double x1, final double z1, final double x2, final double z2, final UUID worldID)
    {
        this(zoneName, new Vector2D(x1, z1), new Vector2D(x2, z2), worldID);
    }

    public Zone(final String zoneName, final Location pointOne, final Location pointTwo)
    {
        this(zoneName, pointOne.getX(), pointOne.getZ(), pointTwo.getX(), pointTwo.getZ(), pointOne.getWorld().getUID());
    }

    public Zone(final String zoneName, final Vector2D pointOne, final Vector2D pointTwo, final UUID worldID)
    {
        this.worldID = worldID;
        this.zoneName = zoneName;

        this.min = Vector2D.getMinimum(pointOne, pointTwo);
        this.max = Vector2D.getMaximum(pointOne, pointTwo);
    }

    /**
     * @return the max
     */
    public final Vector2D getMax()
    {
        return this.max;
    }

    /**
     * @return the min
     */
    public final Vector2D getMin()
    {
        return this.min;
    }

    public String getName()
    {
        return this.zoneName;
    }

    public UUID getWorldID()
    {
        return this.worldID;
    }

    /**
     * Checks if a block is located inside of this region.
     * 
     * @param block
     *            Block to check
     * @return True if inside region
     */
    public boolean inBound(final Block block)
    {
        final Vector2D position = new Vector2D(block.getX(), block.getZ());
        return position.isInAB(this.min, this.max);
    }

    /**
     * Checks if a X and Z coordinate is inside a region.
     * 
     * @param x
     *            X coordinate to check
     * @param z
     *            Z coordinate to check
     * @return True if inside region
     */
    public boolean inBound(final int x, final int z)
    {
        final Vector2D position = new Vector2D(x, z);
        return position.isInAB(this.min, this.max);
    }

    /**
     * Checks if a location is inside of this region.
     * 
     * @param loc
     *            Location to check
     * @return True if inside region
     */
    public boolean inBound(final Location loc)
    {
        final Vector2D position = new Vector2D(loc.getX(), loc.getZ());
        return position.isInAB(this.min, this.max);
    }
}
