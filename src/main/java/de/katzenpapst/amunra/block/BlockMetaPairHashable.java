package de.katzenpapst.amunra.block;

import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.block.Block;

/**
 * Like BlockMetaPair, but should work in HashMaps
 * @author katzenpapst
 *
 */
public class BlockMetaPairHashable extends BlockMetaPair {

    public BlockMetaPairHashable(Block block, byte metadata) {
        super(block, metadata);
    }

    public BlockMetaPairHashable(BlockMetaPair bmp) {
        super(bmp.getBlock(), bmp.getMetadata());
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
        return this.getBlock().hashCode() ^ getMetadata() << 28;
    }

}
