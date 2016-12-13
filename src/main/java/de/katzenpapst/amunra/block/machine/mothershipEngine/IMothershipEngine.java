package de.katzenpapst.amunra.block.machine.mothershipEngine;

import de.katzenpapst.amunra.item.ItemDamagePair;
import net.minecraft.world.World;

public interface IMothershipEngine {
    /**
     * This should return the mass this engine can move in actual kg, depending on it's fuel status etc.
     * If the engine has no fuel, return 0 here rather than in getSpeed
     *
     * @param w
     * @param x
     * @param y
     * @param z
     * @param meta
     * @return
     */
    public double getActualThrust(World w, int x, int y, int z, int meta);

    /**
     * This should return the mass this engine theoretically could move, if given enough fuel.
     * @param w
     * @param x
     * @param y
     * @param z
     * @param meta
     * @return
     */
    public double getPotentialThrust(World w, int x, int y, int z, int meta);

    /**
     * This should return this engine's speed in AU/t. This value should be independent of fuel level etc, (see getActualThrust)
     * but can be dependent of installed upgrades etc. This can return 0 if this engine as a multiblock has not enough blocks
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
     * Should return the direction in which the engine is pointing, TODO add definition
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @param meta
     * @return
     */
    public int getDirection(World world, int x, int y, int z, int meta);

}
