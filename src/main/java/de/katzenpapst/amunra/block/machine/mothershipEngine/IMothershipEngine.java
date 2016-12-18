package de.katzenpapst.amunra.block.machine.mothershipEngine;

import de.katzenpapst.amunra.item.ItemDamagePair;
import net.minecraft.world.World;

public interface IMothershipEngine {

    /**
     * This should return the mass this engine can move. This value should be independent of fuel levels, but can be dependent on
     * the block's current configuration
     *
     * @param w
     * @param x
     * @param y
     * @param z
     * @param meta
     * @return
     */
    public double getThrust(World w, int x, int y, int z, int meta);

    /**
     * This should return this engine's speed in AU/t. This value should be independent of fuel levels, but can be dependent on
     * the block's current configuration
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @param meta
     * @return
     */
    public double getSpeed(World world, int x, int y, int z, int meta);

    /**
     * Should figure out whenever it has enough fuel or energy or whatever for that given distance
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @param meta
     * @param distance
     * @return
     */
    public boolean canTravelDistance(World world, int x, int y, int z, int meta, double distance);

    /**
     * Should return the direction in which the engine is pointing, and, by that, where it would push the ship
     *
     * value | motion direction |
     * ------+----------------- +
     *   0   |        +Z        |
     *   1   |        -X        |
     *   2   |        -Z        |
     *   3   |        +X        |
     *
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @param meta
     * @return
     */
    public int getDirection(World world, int x, int y, int z, int meta);


    /**
     * Should consume the fuel needed for the transition, on client side also start any animation or something alike.
     * This will be called for all engines which are actually being used
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @param meta
     * @param distance
     */
    public void beginTransit(World world, int x, int y, int z, int meta, double distance);


    /**
     * Will be called on all which return true from isInUse on transit end
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @param meta
     */
    public void endTransit(World world, int x, int y, int z, int meta);

    /**
     * Should return whenever beginTransit has been called on this engine, and endTransit hasn't yet
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @param meta
     * @return
     */
    public boolean isInUse(World world, int x, int y, int z, int meta);

}
