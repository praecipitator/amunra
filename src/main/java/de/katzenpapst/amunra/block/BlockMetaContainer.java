package de.katzenpapst.amunra.block;

import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

/**
 * Like BlockMetaPair, but should work in HashMaps
 * @author katzenpapst
 *
 */
public class BlockMetaContainer extends BlockMetaPair {

    public BlockMetaContainer(Block block, byte metadata) {
        super(block, metadata);
    }

    public BlockMetaContainer(Block block, int metadata) {
        super(block, (byte)metadata);
    }

    public BlockMetaContainer(BlockMetaPair bmp) {
        super(bmp.getBlock(), bmp.getMetadata());
    }

    public BlockMetaContainer(IBlockState state) {
        super(state.getBlock(), (byte) state.getBlock().getMetaFromState(state));
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof BlockMetaPair)) {
            return false;
        }
        BlockMetaPair otherReal = (BlockMetaPair)other;
        return otherReal.getBlock() == getBlock() && otherReal.getMetadata() == getMetadata();
    }

    @Override
    public int hashCode() {
        // the block's hash code, and the meta in the first 4 bits
        return this.getBlock().hashCode() ^ (getMetadata() << 28);
    }

    @Override
    public BlockMetaContainer clone() {
        return new BlockMetaContainer(this);
    }

    public IBlockState getBlockState() {
        return getBlock().getStateFromMeta(getMetadata());
    }

    public static IBlockState getBlockState(BlockMetaPair pair) {
        return pair.getBlock().getStateFromMeta(pair.getMetadata());
    }

    public boolean isBlockState(IBlockState state) {
        return state.getBlock() == getBlock() && getBlock().getMetaFromState(state) == getMetadata();
    }

}
