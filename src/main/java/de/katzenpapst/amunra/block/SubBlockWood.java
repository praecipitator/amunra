package de.katzenpapst.amunra.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SubBlockWood extends SubBlock {

	protected String textureTop;
	protected IIcon topIcon;


	public SubBlockWood(String name, String textureSide, String textureTop) {
		super(name, textureSide, "axe", 0);
		this.textureTop = textureTop;
	}

	public SubBlockWood(String name, String textureSide, String textureTop, String tool,
			int harvestLevel) {
		super(name, textureSide, tool, harvestLevel);
		this.textureTop = textureTop;
	}

	public SubBlockWood(String name, String textureSide, String textureTop, String tool,
			int harvestLevel, float hardness, float resistance) {
		super(name, textureSide, tool, harvestLevel, hardness, resistance);
		this.textureTop = textureTop;
	}

	@Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
		this.blockIcon = par1IconRegister.registerIcon(this.getTextureName());
		this.topIcon = par1IconRegister.registerIcon(this.textureTop);


    }

	/**
	 * @param side
	 * @param meta	expects to be ONLY the relevant two bits which describe rotation
	 */
	@Override
	@SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
		/*Face 0 (Bottom Face) 	Face 1 (Top Face) 	Face 2 (Northern Face) 	Face 3 (Southern Face) 	Face 4 (Western Face) 	Face 5 (Eastern Face)*/
		/*
	    0:
	    Facing Up/Down
	    1:
	    Facing East/West
	    2:
	    Facing North/South
	    3:
	    Only bark
*/
		switch(meta) {
		case 0:	// Facing Up/Down
			if(side == 1 || side == 0) {
				return this.topIcon;
			}
			return this.blockIcon;
		case 1: // Facing East/West
			if(side == 4 || side == 5) {
				return this.topIcon;
			}
			return this.blockIcon;
		case 2: // Facing North/South
			if(side == 2 || side == 3) {
				return this.topIcon;
			}
			return this.blockIcon;
		case 3:	// only bark
			return this.blockIcon;
		}

        return this.blockIcon;
    }

}
