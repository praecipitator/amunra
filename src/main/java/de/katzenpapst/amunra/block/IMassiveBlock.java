package de.katzenpapst.amunra.block;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/**
 * Interface for blocks to return a mass
 * @author katzenpapst
 *
 */
public interface IMassiveBlock {
    float getMass(World w, BlockPos pos, int meta);
}
