package de.katzenpapst.amunra.vec;

import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.world.World;

/**
 * This is supposed to hold everything necessary to find a block
 *
 */
public class BlockVector extends Vector3int {

    public World world;

    public BlockVector(World world, int x, int y, int z) {
        super(x, y, z);
        this.world = world;
    }

    public BlockMetaPair getBlockMetaPair(World world, int x, int y, int z) {
        return new BlockMetaPair(world.getBlock(x, y, z), (byte) world.getBlockMetadata(x, y, z));
    }

    public boolean isBlockMetaPair(World world, int x, int y, int z, BlockMetaPair bmp) {
        return world.getBlock(x, y, z) == bmp.getBlock() && world.getBlockMetadata(x, y, z) == bmp.getMetadata();
    }

}
