package de.katzenpapst.amunra.block;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;

public class MethaneGrass extends SubBlock {
	
	//blockIcon = 0 = top
	@SideOnly(Side.CLIENT)
	protected IIcon blockIconSide;
	@SideOnly(Side.CLIENT)
	protected IIcon blockIconBottom;

	public MethaneGrass(String name) {
		// blockRegistry.addObject(3, "dirt", (new BlockDirt()).setHardness(0.5F).setStepSound(soundTypeGravel).setBlockName("dirt").setBlockTextureName("dirt"));
		super(Material.ground, name, "amunra:methanegrass", "shovel", 1, 0.5F, 5.0F);
		this.setStepSound(Block.soundTypeGrass);
	}
	
	@Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
		blockIcon = par1IconRegister.registerIcon(getTextureName());
		blockIconSide = par1IconRegister.registerIcon("amunra:methanegrassside");
		blockIconBottom = par1IconRegister.registerIcon("amunra:methanedirt");
		
    }
	
	@SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta)
    {
		switch(side) {
		case 0:
			return blockIconBottom;
		case 1:
			return blockIcon;
		default:
			return blockIconSide;
		}
    }
	
	@Override
	public boolean dropsSelf() {
		return false;
	}
	
	@Override
    public Item getItemDropped(int meta, Random random, int fortune)
    {
		return Item.getItemFromBlock(parent);	
    }
	
	@Override
    public int damageDropped(int meta)
    {
		return 1; // this is the dirt. TODO add some kind of id by name lookup
    }

}
