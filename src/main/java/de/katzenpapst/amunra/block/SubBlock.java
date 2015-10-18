package de.katzenpapst.amunra.block;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class SubBlock extends Block {
	/*
	public float hardness = 1.5F;
	public float resistance = 10.0F;
	public String name = null;
	public String texture = null;
	public String tool = "pickaxe";
	/**
	 *     Wood:    0
     *     Stone:   1
     *     Iron:    2
     *     Diamond: 3
     *     Gold:    0
	 * /
	public int miningLevel = 0;
	public boolean isOpaque = false;
	public int lightOpacity = 0;
	public int lightValue = 0;
	public Material material = Material.rock;
	public SoundType soundType = Block.soundTypeStone;*/
	
	// because blockName is private without getters...
	protected String blockNameFU;  
	
	protected IIcon textureIcon;
	
	protected BlockBasicMulti parent = null;
	
	public SubBlock(String name, String texture) {
		super(Material.rock);
		init(name, texture, "pickaxe", 1, 1.5F, 10.0F);
	}
	
	
	public SubBlock(String name, String texture, String tool, int harvestLevel) {
		super(Material.rock);
		init(name, texture, tool, harvestLevel, 1.5F, 10.0F);
	}
	
	public SubBlock(String name, String texture, String tool, int harvestLevel, float hardness, float resistance) {
		super(Material.rock);
		init(name, texture, tool, harvestLevel, hardness, resistance);
	}
	
	protected void init(String name, String texture, String harvestTool, 
			int havestLevel, float hardness, float resistance) {
		blockNameFU = name;
		this.setBlockName(name);
		setBlockTextureName(texture);
		setHarvestLevel(harvestTool, havestLevel);
		setHardness(hardness);
		setResistance(resistance);
		// this.name = name;
		// this.texture = texture;
	}
	
	/**
     * Gets the localized name of this block. Used for the statistics page.
     */
    public String getLocalizedName()
    {
        return blockNameFU; // multiblock does that
    }

    /**
     * Returns the unlocalized name of the block with "tile." appended to the front.
     */
    public String getUnlocalizedName()
    {
        return blockNameFU;
    }
	
	/**
	 * if true, multiblock does the stuff itself
	 * @return
	 */
	public boolean dropsSelf() {
		return true;
	}
	
	@Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        return null;
    }
	
	/*
	@Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
		
	
		
    }*/
}
