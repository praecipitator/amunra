package de.katzenpapst.amunra.block.machine.mothershipEngine;

import de.katzenpapst.amunra.item.ItemDamagePair;
import net.minecraft.world.World;

public interface IMothershipEngine {
    /**
     * This should return the mass this engine can move in actual kg
     * @param w
     * @param x
     * @param y
     * @param z
     * @param meta
     * @return
     */
    public double getStrength(World w, int x, int y, int z, int meta);

    /**
     * This should return this engine's speed in AU/t
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
     * @param world
     * @param x
     * @param y
     * @param z
     * @param meta
     * @param distance
     * @return
     */
    public boolean canTravelDistance(World world, int x, int y, int z, int meta, double distance);

}
