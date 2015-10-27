package de.katzenpapst.amunra.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.item.ItemBlockMulti;
import micdoodle8.mods.galacticraft.api.block.IDetectableResource;
import micdoodle8.mods.galacticraft.api.block.IPlantableBlock;
import micdoodle8.mods.galacticraft.api.block.ITerraformableBlock;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockBasicMulti extends Block implements IDetectableResource, IPlantableBlock, ITerraformableBlock {

	//protected ArrayList<SubBlock> subBlocks = null;
	protected SubBlock[] subBlocksArray = new SubBlock[16];
	protected HashMap<String, Integer> nameMetaMap = null;
	
	String blockNameFU;
	
	public BlockBasicMulti(String name, Material mat, int initialCapacity) {
		super(mat);	// todo replace this
		blockNameFU = name;
		// subBlocks = new ArrayList<SubBlock>(initialCapacity);
		nameMetaMap = new HashMap<String, Integer>();
		setBlockName(name);
	}
	
	public int getMetaByName(String name) {
		Integer i = nameMetaMap.get(name);
		if(i == null) {
			throw new IllegalArgumentException("Subblock "+name+" doesn't exist in "+blockNameFU);
		}
		return (int)i;
	}
	
	public BlockMetaPair addSubBlock(int meta, SubBlock sb) {
		if(meta > 15 || meta < 0) {
			throw new IllegalArgumentException("Meta "+meta+" must be <= 15 && >= 0");
		}
		/*while(meta >= subBlocks.size()) {
			subBlocks.add(null);// fill it with dummy items
		}*/
		if(subBlocksArray[meta] != null) {
			throw new IllegalArgumentException("Meta "+meta+" is already in use in "+blockNameFU);
		}
		if(nameMetaMap.get(sb.getUnlocalizedName()) != null) {
			throw new IllegalArgumentException("Name "+sb.getUnlocalizedName()+" is already in use in "+blockNameFU);			
		}
		sb.parent = this;
		nameMetaMap.put(sb.getUnlocalizedName(), meta);
		subBlocksArray[meta] = sb;
		return new BlockMetaPair(this, (byte) meta);
	}
	
	public SubBlock getSubBlock(int meta) {
		return subBlocksArray[meta];
	}
	
	public BlockMetaPair getBlockMetaPair(String name) {
		return new BlockMetaPair(this, (byte) getMetaByName(name));
	}
	
	/**
	 * Registers the block with the GameRegistry and sets the harvestlevels for all subblocks
	 */
	public void register() {
		GameRegistry.registerBlock(this, ItemBlockMulti.class, this.getUnlocalizedName());
		
		for(int i=0;i<16;i++) {
			SubBlock sb = subBlocksArray[i];
			if(sb != null) {
				
				this.setHarvestLevel(sb.getHarvestTool(0), sb.getHarvestLevel(0), i);
			}
		}
	}
	
	@Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
		for(SubBlock sb: subBlocksArray) {
			if(sb != null) {
				sb.registerBlockIcons(par1IconRegister);
			}
		}
    }
	
	@SideOnly(Side.CLIENT)
    @Override
    public CreativeTabs getCreativeTabToDisplayOn()
    {
        return AmunRa.arTab;
    }
	
	@Override
    public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ)
    {
		int metadata = world.getBlockMetadata(x, y, z); 
		
		return subBlocksArray[metadata].getExplosionResistance(par1Entity, world, x, y, z, explosionX, explosionY, explosionZ);
    }
	
	@Override
    public float getBlockHardness(World par1World, int par2, int par3, int par4)
    {
        int meta = par1World.getBlockMetadata(par2, par3, par4);


        return subBlocksArray[meta].getBlockHardness(par1World, par2, par3, par4);
    }
	
	@SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta)
    {
		/*Face 0 (Bottom Face) 	Face 1 (Top Face) 	Face 2 (Northern Face) 	Face 3 (Southern Face) 	Face 4 (Western Face) 	Face 5 (Eastern Face)*/
		// System.out.print("Trying to get icon for "+this.getUnlocalizedName()+":"+meta+"\n");
		return subBlocksArray[meta].getIcon(side, 0);
    }

	@Override
    public Item getItemDropped(int meta, Random random, int fortune)
    {
		SubBlock sb = subBlocksArray[meta];
		
		
		if(sb.dropsSelf()) {
			return Item.getItemFromBlock(this);
		}
		return sb.getItemDropped(0, random, fortune); 
    }
	
	@Override
    public int damageDropped(int meta)
    {
		SubBlock sb = subBlocksArray[meta];
		if(sb.dropsSelf()) {
			return meta;
		}
		return sb.damageDropped(0);
    }
	
	@Override
    public int getDamageValue(World p_149643_1_, int p_149643_2_, int p_149643_3_, int p_149643_4_)
    {
    	return p_149643_1_.getBlockMetadata(p_149643_2_, p_149643_3_, p_149643_4_);    	
    }
	
	@Override
    public int quantityDropped(int meta, int fortune, Random random)
    {
		SubBlock sb = subBlocksArray[meta];
		if(sb.dropsSelf()) {
			return 1;
		}
		return sb.quantityDropped(meta, fortune, random);
    }

	@SuppressWarnings({ "unchecked", "rawtypes" })
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List)
    {
		for(int i = 0; i < 16; i++) {
			if(subBlocksArray[i] != null) {
				par3List.add(new ItemStack(par1, 1, i));
			}
		}
    }
	
	@Override
    public TileEntity createTileEntity(World world, int meta)
    {
		SubBlock sb = subBlocksArray[meta];
		return sb.createTileEntity(world, 0);
    }
	
	@Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
    {
        int meta = world.getBlockMetadata(x, y, z);
        if (subBlocksArray[meta] != null)
        {
            return new ItemStack(Item.getItemFromBlock(this), 1, meta);
        }

        return super.getPickBlock(target, world, x, y, z);
    }
	
	public boolean getBlocksMovement(IBlockAccess par1World, int x, int y, int z)
    {
		int meta = par1World.getBlockMetadata(x, y, z);
		
		return subBlocksArray[meta].getBlocksMovement(par1World, x, y, z);
    }
	
	@Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
		int meta = world.getBlockMetadata(x, y, z);
		return subBlocksArray[meta].onBlockActivated(world, x, y, z, entityPlayer, side, hitX, hitY, hitZ);
    }
	
	@Override
	public boolean isTerraformable(World world, int x, int y, int z) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int requiredLiquidBlocksNearby() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isPlantable(int metadata) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isValueable(int metadata) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public Material getMaterial() {
		return this.blockMaterial;
	}

}
