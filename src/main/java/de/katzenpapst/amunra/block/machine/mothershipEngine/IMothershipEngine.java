package de.katzenpapst.amunra.block.machine.mothershipEngine;

import java.util.Map;

import de.katzenpapst.amunra.item.ItemDamagePair;
import de.katzenpapst.amunra.mothership.fueldisplay.MothershipFuelDisplay;
import de.katzenpapst.amunra.mothership.fueldisplay.MothershipFuelRequirements;
import net.minecraft.world.World;

public interface IMothershipEngine {

    /**
     * This should return the force this engine can provide, in Newtons. This value should be independent of fuel levels, but can be dependent on
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
     * Should figure out whenever it has enough fuel or energy or whatever to continuously work for the given duration
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @param meta
     * @param duratino
     * @return
     */
    public boolean canRunForDuration(World world, int x, int y, int z, int meta, long duration);

    /**
     * Should return a map of all the fuel types that are needed for this transit
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @param meta
     * @param distance
     * @return
     */
    public MothershipFuelRequirements getFuelRequirements(World world, int x, int y, int z, int meta, long duration);
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
    public void beginTransit(World world, int x, int y, int z, int meta, long duration);


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

    /**
     * Return false if this engine should just not be considered
     * @param world
     * @param x
     * @param y
     * @param z
     * @param meta
     * @return
     */
    public boolean isEnabled(World world, int x, int y, int z, int meta);

}
