package com.thevoxelbox.voxelborder.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

/**
 * Represents a mutable vector. Because the components of Vectors are mutable, storing Vectors long term may be dangerous if passing code modifies the Vector
 * later. If you want to keep around a Vector, it may be wise to call <code>clone()</code> in order to get a copy.
 */
@SerializableAs("Vector2D")
public class Vector2D implements Cloneable, ConfigurationSerializable
{

    private static Random random = new Random();

    /**
     * Threshold for fuzzy equals().
     */
    private static final double epsilon = 0.000001;

    public static Vector2D deserialize(final Map<String, Object> args)
    {
        double x = 0;
        double z = 0;

        if (args.containsKey("x"))
        {
            x = (Double) args.get("x");
        }
        if (args.containsKey("z"))
        {
            z = (Double) args.get("z");
        }

        return new Vector2D(x, z);
    }

    /**
     * Get the threshold used for equals().
     * 
     * @return The epsilon.
     */
    public static double getEpsilon()
    {
        return Vector2D.epsilon;
    }

    /**
     * Gets the maximum components of two vectors.
     * 
     * @param v1
     *            The first vector.
     * @param v2
     *            The second vector.
     * @return maximum
     */
    public static Vector2D getMaximum(final Vector2D v1, final Vector2D v2)
    {
        return new Vector2D(Math.max(v1.x, v2.x), Math.max(v1.z, v2.z));
    }

    /**
     * Gets the minimum components of two vectors.
     * 
     * @param v1
     *            The first vector.
     * @param v2
     *            The second vector.
     * @return minimum
     */
    public static Vector2D getMinimum(final Vector2D v1, final Vector2D v2)
    {
        return new Vector2D(Math.min(v1.x, v2.x), Math.min(v1.z, v2.z));
    }

    /**
     * Gets a random vector with components having a random value between 0 and 1.
     * 
     * @return A random vector.
     */
    public static Vector2D getRandom()
    {
        return new Vector2D(Vector2D.random.nextDouble(), Vector2D.random.nextDouble());
    }

    protected double x;

    protected double z;

    /**
     * Construct the vector with all components as 0.
     */
    public Vector2D()
    {
        this.x = 0;
        this.z = 0;
    }

    /**
     * Construct the vector with provided double components.
     * 
     * @param x
     *            X component
     * @param z
     *            Z component
     */
    public Vector2D(final double x, final double z)
    {
        this.x = x;
        this.z = z;
    }

    /**
     * Construct the vector with provided float components.
     * 
     * @param x
     *            X component
     * @param z
     *            Z component
     */
    public Vector2D(final float x, final float z)
    {
        this.x = x;
        this.z = z;
    }

    /**
     * Construct the vector with provided integer components.
     * 
     * @param x
     *            X component
     * @param z
     *            Z component
     */
    public Vector2D(final int x, final int z)
    {
        this.x = x;
        this.z = z;
    }

    public Vector2D(final Vector vector)
    {
        this.x = vector.getX();
        this.z = vector.getZ();
    }

    /**
     * Adds a vector to this one
     * 
     * @param vec
     *            The other vector
     * @return the same vector
     */
    public Vector2D add(final Vector2D vec)
    {
        this.x += vec.x;
        this.z += vec.z;
        return this;
    }

    /**
     * Gets the angle between this vector and another in radians.
     * 
     * @param other
     *            The other vector
     * @return angle in radians
     */
    public float angle(final Vector2D other)
    {
        final double dot = this.dot(other) / (this.length() * other.length());

        return (float) Math.acos(dot);
    }

    /**
     * Get a new vector.
     * 
     * @return vector
     */
    @Override
    public Vector2D clone()
    {
        try
        {
            return (Vector2D) super.clone();
        }
        catch (final CloneNotSupportedException e)
        {
            throw new Error(e);
        }
    }

    /**
     * Copies another vector
     * 
     * @param vec
     *            The other vector
     * @return the same vector
     */
    public Vector2D copy(final Vector2D vec)
    {
        this.x = vec.x;
        this.z = vec.z;
        return this;
    }

    /**
     * Get the distance between this vector and another. The value of this method is not cached and uses a costly square-root function, so do not repeatedly
     * call this method to get the vector's magnitude. NaN will be returned if the inner result of the sqrt() function overflows, which will be caused if the
     * distance is too long.
     * 
     * @param o
     *            The other vector
     * @return the distance
     */
    public double distance(final Vector2D o)
    {
        return Math.sqrt(Math.pow(this.x - o.x, 2) + Math.pow(this.z - o.z, 2));
    }

    /**
     * Get the squared distance between this vector and another.
     * 
     * @param o
     *            The other vector
     * @return the distance
     */
    public double distanceSquared(final Vector2D o)
    {
        return Math.pow(this.x - o.x, 2) + Math.pow(this.z - o.z, 2);
    }

    /**
     * Divides the vector by another.
     * 
     * @param vec
     *            The other vector
     * @return the same vector
     */
    public Vector2D divide(final Vector2D vec)
    {
        this.x /= vec.x;
        this.z /= vec.z;
        return this;
    }

    /**
     * Calculates the dot product of this vector with another. The dot product is defined as x1*x2+y1*y2+z1*z2. The returned value is a scalar.
     * 
     * @param other
     *            The other vector
     * @return dot product
     */
    public double dot(final Vector2D other)
    {
        return this.x * other.x + this.z * other.z;
    }

    /**
     * Checks to see if two objects are equal.
     * <p />
     * Only two Vectors can ever return true. This method uses a fuzzy match to account for floating point errors. The epsilon can be retrieved with epsilon.
     */
    @Override
    public boolean equals(final Object obj)
    {
        if (!(obj instanceof Vector2D))
        {
            return false;
        }

        final Vector2D other = (Vector2D) obj;

        return Math.abs(this.x - other.x) < Vector2D.epsilon && Math.abs(this.z - other.z) < Vector2D.epsilon && (this.getClass().equals(obj.getClass()));
    }

    /**
     * Gets the floored value of the X component, indicating the block that this vector is contained with.
     * 
     * @return block X
     */
    public int getBlockX()
    {
        return NumberConversions.floor(this.x);
    }

    /**
     * Gets the floored value of the Z component, indicating the block that this vector is contained with.
     * 
     * @return block z
     */
    public int getBlockZ()
    {
        return NumberConversions.floor(this.z);
    }

    /**
     * Gets a new midpoint vector between this vector and another.
     * 
     * @param other
     *            The other vector
     * @return a new midpoint vector
     */
    public Vector2D getMidpoint(final Vector2D other)
    {
        final double x = (this.x + other.x) / 2;
        final double z = (this.z + other.z) / 2;
        return new Vector2D(x, z);
    }

    /**
     * Gets the X component.
     * 
     * @return The X component.
     */
    public double getX()
    {
        return this.x;
    }

    /**
     * Gets the Z component.
     * 
     * @return The Z component.
     */
    public double getZ()
    {
        return this.z;
    }

    /**
     * Returns a hash code for this vector
     * 
     * @return hash code
     */
    @Override
    public int hashCode()
    {
        int hash = 7;

        hash = 79 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.z) ^ (Double.doubleToLongBits(this.z) >>> 32));
        return hash;
    }

    /**
     * Returns whether this vector is in an axis-aligned bounding box. The minimum and maximum vectors given must be truly the minimum and maximum X, Y and Z
     * components.
     * 
     * @param min
     *            Minimum vector
     * @param max
     *            Maximum vector
     * @return whether this vector is in the AB
     */
    public boolean isInAB(final Vector2D min, final Vector2D max)
    {
        return this.x >= min.x && this.x <= max.x && this.z >= min.z && this.z <= max.z;
    }

    /**
     * Returns whether this vector is within a sphere.
     * 
     * @param origin
     *            Sphere origin.
     * @param radius
     *            Sphere radius
     * @return whether this vector is in the sphere
     */
    public boolean isInSphere(final Vector2D origin, final double radius)
    {
        return (Math.pow(origin.x - this.x, 2) + Math.pow(origin.z - this.z, 2)) <= Math.pow(radius, 2);
    }

    /**
     * Gets the magnitude of the vector, defined as sqrt(x^2+y^2+z^2). The value of this method is not cached and uses a costly square-root function, so do not
     * repeatedly call this method to get the vector's magnitude. NaN will be returned if the inner result of the sqrt() function overflows, which will be
     * caused if the length is too long.
     * 
     * @return the magnitude
     */
    public double length()
    {
        return Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.z, 2));
    }

    /**
     * Gets the magnitude of the vector squared.
     * 
     * @return the magnitude
     */
    public double lengthSquared()
    {
        return Math.pow(this.x, 2) + Math.pow(this.z, 2);
    }

    /**
     * Sets this vector to the midpoint between this vector and another.
     * 
     * @param other
     *            The other vector
     * @return this same vector (now a midpoint)
     */
    public Vector2D midpoint(final Vector2D other)
    {
        this.x = (this.x + other.x) / 2;
        this.z = (this.z + other.z) / 2;
        return this;
    }

    /**
     * Performs scalar multiplication, multiplying all components with a scalar.
     * 
     * @param m
     *            The factor
     * @return the same vector
     */
    public Vector2D multiply(final double m)
    {
        this.x *= m;
        this.z *= m;
        return this;
    }

    /**
     * Performs scalar multiplication, multiplying all components with a scalar.
     * 
     * @param m
     *            The factor
     * @return the same vector
     */
    public Vector2D multiply(final float m)
    {
        this.x *= m;
        this.z *= m;
        return this;
    }

    /**
     * Performs scalar multiplication, multiplying all components with a scalar.
     * 
     * @param m
     *            The factor
     * @return the same vector
     */
    public Vector2D multiply(final int m)
    {
        this.x *= m;
        this.z *= m;
        return this;
    }

    /**
     * Multiplies the vector by another.
     * 
     * @param vec
     *            The other vector
     * @return the same vector
     */
    public Vector2D multiply(final Vector2D vec)
    {
        this.x *= vec.x;
        this.z *= vec.z;
        return this;
    }

    /**
     * Converts this vector to a unit vector (a vector with length of 1).
     * 
     * @return the same vector
     */
    public Vector2D normalize()
    {
        final double length = this.length();

        this.x /= length;
        this.z /= length;

        return this;
    }

    @Override
    public Map<String, Object> serialize()
    {
        final Map<String, Object> result = new LinkedHashMap<String, Object>();

        result.put("x", this.getX());
        result.put("z", this.getZ());

        return result;
    }

    /**
     * Set the X component.
     * 
     * @param x
     *            The new X component.
     * @return This vector.
     */
    public Vector2D setX(final double x)
    {
        this.x = x;
        return this;
    }

    /**
     * Set the X component.
     * 
     * @param x
     *            The new X component.
     * @return This vector.
     */
    public Vector2D setX(final float x)
    {
        this.x = x;
        return this;
    }

    /**
     * Set the X component.
     * 
     * @param x
     *            The new X component.
     * @return This vector.
     */
    public Vector2D setX(final int x)
    {
        this.x = x;
        return this;
    }

    /**
     * Set the Z component.
     * 
     * @param z
     *            The new Z component.
     * @return This vector.
     */
    public Vector2D setZ(final double z)
    {
        this.z = z;
        return this;
    }

    /**
     * Set the Z component.
     * 
     * @param z
     *            The new Z component.
     * @return This vector.
     */
    public Vector2D setZ(final float z)
    {
        this.z = z;
        return this;
    }

    /**
     * Set the Z component.
     * 
     * @param z
     *            The new Z component.
     * @return This vector.
     */
    public Vector2D setZ(final int z)
    {
        this.z = z;
        return this;
    }

    /**
     * Subtracts a vector from this one.
     * 
     * @param vec
     *            The other vector
     * @return the same vector
     */
    public Vector2D subtract(final Vector2D vec)
    {
        this.x -= vec.x;
        this.z -= vec.z;
        return this;
    }

    /**
     * Returns this vector's components as x,y,z.
     */
    @Override
    public String toString()
    {
        return this.x + "," + this.z;
    }

    /**
     * Zero this vector's components.
     * 
     * @return the same vector
     */
    public Vector2D zero()
    {
        this.x = 0;
        this.z = 0;
        return this;
    }
}
