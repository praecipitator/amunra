package de.katzenpapst.amunra.world;

import net.minecraft.world.World;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;

public class WorldHelper {

	public static BlockMetaPair getBlockMetaPair(World world, int x, int y, int z) {
		return new BlockMetaPair(world.getBlock(x, y, z), (byte) world.getBlockMetadata(x, y, z));
	}

	public static boolean isBlockMetaPair(World world, int x, int y, int z, BlockMetaPair bmp) {
		return world.getBlock(x, y, z) == bmp.getBlock() && world.getBlockMetadata(x, y, z) == bmp.getMetadata();
	}

}
