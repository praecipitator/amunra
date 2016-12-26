package de.katzenpapst.amunra.block;

import net.minecraft.world.World;

/**
 * Interface for blocks to return a mass
 * @author katzenpapst
 *
 */
public interface IMassiveBlock {
    float getMass(World w, int x, int y, int z, int meta);
}
