package de.katzenpapst.amunra.block;

import net.minecraft.block.Block;

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

}
