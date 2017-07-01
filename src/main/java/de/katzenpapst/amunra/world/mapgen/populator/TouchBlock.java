package de.katzenpapst.amunra.world.mapgen.populator;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

/**
 *
 * Triggers a block update
 *
 */
public class TouchBlock extends AbstractPopulator {

    public TouchBlock(BlockPos pos) {
        super(pos);
    }

    @Override
    public boolean populate(World world) {
        IBlockState state = world.getBlockState(pos);

        Chunk chunk = world.getChunkFromChunkCoords(pos.getX() >> 4, pos.getZ() >> 4);
        world.markAndNotifyBlock(pos, chunk, state, state, 3);
        //world.markAndNotifyBlock(x, y, z, chunk, block, block, 3);
        return true;
    }

}
