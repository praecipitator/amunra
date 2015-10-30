package de.katzenpapst.amunra.block;

import java.util.Random;

import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.item.Item;

/**
 * Subblock which drops some kind of cobble when harvested
 *
 */
public class SubBlockRock extends SubBlock {

	BlockMetaPair blockToDrop = null;

	public SubBlockRock(String name, String texture) {
		super(name, texture);
	}

	public SubBlockRock(String name, String texture, String tool, int harvestLevel) {
		super(name, texture, tool, harvestLevel);
	}

	public SubBlockRock(String name, String texture, String tool,
			int harvestLevel, float hardness, float resistance) {
		super(name, texture, tool, harvestLevel, hardness, resistance);
	}

	public SubBlockRock setBlockToDrop(BlockMetaPair block){
		blockToDrop = block;
		return this;
	}

	@Override
	public boolean dropsSelf() {
		return false;
	}

	@Override
	public Item getItemDropped(int meta, Random random, int fortune)
    {
		return Item.getItemFromBlock(blockToDrop.getBlock());
    }

	@Override
    public int damageDropped(int meta)
    {
		return blockToDrop.getMetadata();
    }

}
