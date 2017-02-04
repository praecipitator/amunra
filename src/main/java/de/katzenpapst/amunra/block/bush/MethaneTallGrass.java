package de.katzenpapst.amunra.block.bush;

import de.katzenpapst.amunra.block.ARBlocks;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class MethaneTallGrass extends SubBlockBush {

    public MethaneTallGrass(String name, String texture) {
        super(name, texture);
        // TODO Auto-generated constructor stub
    }

    public MethaneTallGrass(String name, String texture, String tool,
            int harvestLevel) {
        super(name, texture, tool, harvestLevel);
        // TODO Auto-generated constructor stub
    }

    public MethaneTallGrass(String name, String texture, String tool,
            int harvestLevel, float hardness, float resistance) {
        super(name, texture, tool, harvestLevel, hardness, resistance);
        // TODO Auto-generated constructor stub
    }


    @Override
    public boolean canPlaceOn(Block blockToCheck, int metaToCheck, int meta) {
        return (blockToCheck == ARBlocks.blockMethaneGrass.getBlock() && metaToCheck == ARBlocks.blockMethaneGrass.getMetadata());
        //return true;
    }

    @Override
    public boolean canBlockStay(World world, int x, int y, int z) {
        Block belowBlock = world.getBlock(x, y-1, z);
        int myMeta = world.getBlockMetadata(x, y, z);
        int belowMeta = world.getBlockMetadata(x, y-1, z);
        return this.canPlaceOn(belowBlock, belowMeta, myMeta);
    }

}
