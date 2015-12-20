package de.katzenpapst.amunra.world.mapgen.populator;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
/**
 *
 * Triggers a block update
 *
 */
public class TouchBlock extends AbstractPopulator {

	public TouchBlock(int x, int y, int z) {
		super(x, y, z);
	}

	@Override
	public boolean populate(World world) {
		Block block = world.getBlock(x, y, z);
		Chunk chunk = world.getChunkFromChunkCoords(x >> 4, z >> 4);
		world.markAndNotifyBlock(x, y, z, chunk, block, block, 3);
		return true;
	}

}
