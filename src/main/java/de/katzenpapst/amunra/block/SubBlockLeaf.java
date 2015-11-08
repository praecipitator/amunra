package de.katzenpapst.amunra.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SubBlockLeaf extends SubBlock {

	IIcon blockIconOpaque = null;

	public SubBlockLeaf(String name, String texture) {
		super(name, texture);
		// TODO Auto-generated constructor stub
	}

	public SubBlockLeaf(String name, String texture, String tool,
			int harvestLevel) {
		super(name, texture, tool, harvestLevel);
		// TODO Auto-generated constructor stub
	}

	public SubBlockLeaf(String name, String texture, String tool,
			int harvestLevel, float hardness, float resistance) {
		super(name, texture, tool, harvestLevel, hardness, resistance);
		// TODO Auto-generated constructor stub
	}

	@Override
	@SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconReg)
    {
		this.blockIcon = iconReg.registerIcon(this.getTextureName());
		this.blockIconOpaque = iconReg.registerIcon(this.getTextureName()+"_opaque");
    }

	public IIcon getOpaqueIcon(int side) {
		return blockIconOpaque;
	}

}
