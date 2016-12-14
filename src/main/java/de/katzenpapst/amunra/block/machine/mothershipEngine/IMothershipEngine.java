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
