package de.katzenpapst.amunra.block;

import java.util.Random;

import de.katzenpapst.amunra.item.ARItems;
import net.minecraft.item.Item;

public class DustBlock extends SubBlock {

	public DustBlock(String name, String texture) {
		super(name, texture);
		// TODO Auto-generated constructor stub
	}

	public DustBlock(String name, String texture, String tool, int harvestLevel) {
		super(name, texture, tool, harvestLevel);
		// TODO Auto-generated constructor stub
	}

	public DustBlock(String name, String texture, String tool,
			int harvestLevel, float hardness, float resistance) {
		super(name, texture, tool, harvestLevel, hardness, resistance);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean dropsSelf() {
		return false;
	}

	@Override
	public Item getItemDropped(int meta, Random random, int fortune)
    {
		return ARItems.dustMote.getItem();
    }

	@Override
    public int damageDropped(int meta)
    {
		return ARItems.dustMote.getDamage();
    }

	@Override
	public int quantityDropped(int meta, int fortune, Random random)
	{
		return Math.min(random.nextInt(3)+random.nextInt(10)*fortune, 9);
	}
}
