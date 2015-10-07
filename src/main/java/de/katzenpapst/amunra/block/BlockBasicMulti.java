package de.katzenpapst.amunra.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.item.ItemBasicMulti;
import de.katzenpapst.amunra.item.ItemBasicRock;
import micdoodle8.mods.galacticraft.api.block.IDetectableResource;
import micdoodle8.mods.galacticraft.api.block.IPlantableBlock;
import micdoodle8.mods.galacticraft.api.block.ITerraformableBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockBasicMulti extends Block implements IDetectableResource, IPlantableBlock, ITerraformableBlock {

	protected ArrayList<SubBlock> subBlocks = null;
	
	public BlockBasicMulti(Material mat, int initialCapacity) {
		super(mat);	// todo replace this
		
		subBlocks = new ArrayList<SubBlock>(initialCapacity);
		setBlockName("multiBlock");
	}
	
	public void addSubBlock(int meta, SubBlock sb) {
		while(meta >= subBlocks.size()) {
			subBlocks.add(null);// fill it with dummy items
		}
		if(subBlocks.get(meta) != null) {
			throw new IllegalArgumentException("Meta "+meta+" is already in use");
		}
		subBlocks.set(meta, sb);
	}
	
	public SubBlock getSubBlock(int meta) {
		return subBlocks.get(meta);
	}
	
	public void register() {
		GameRegistry.registerBlock(this, ItemBasicMulti.class, this.getUnlocalizedName());
		
		for(int i=0;i<subBlocks.size();i++) {
			SubBlock sb = subBlocks.get(i);
			if(sb != null) {
				this.setHarvestLevel(sb.tool, sb.miningLevel, i);
			}
		}
	}
	
	@Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
		for(SubBlock sb: subBlocks) {
			sb.textureIcon = par1IconRegister.registerIcon(sb.texture);
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
		
		return subBlocks.get(metadata).resistance / 5.0F;
    }
	
	@Override
    public float getBlockHardness(World par1World, int par2, int par3, int par4)
    {
        int meta = par1World.getBlockMetadata(par2, par3, par4);


        return subBlocks.get(meta).hardness;
    }
	
	@SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta)
    {
		// TODO add stuff for sides
		/*
		 * 0 = bottom
		 * 1 = top
		 * 2 = north
		 * 3 = south
		 * 4 = west
		 * 5 = east
		 */
		return subBlocks.get(meta).textureIcon;
    }

	@Override
    public Item getItemDropped(int meta, Random random, int par3)
    {
    	return Item.getItemFromBlock(this);
    }
	
	@Override
    public int damageDropped(int meta)
    {
    	return meta;
    }
	
	@Override
    public int getDamageValue(World p_149643_1_, int p_149643_2_, int p_149643_3_, int p_149643_4_)
    {
    	return p_149643_1_.getBlockMetadata(p_149643_2_, p_149643_3_, p_149643_4_);    	
    }
	
	@Override
    public int quantityDropped(int meta, int fortune, Random random)
    {
    	return 1;
    }

	@SuppressWarnings({ "unchecked", "rawtypes" })
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List)
    {
		for(int i = 0; i < subBlocks.size(); i++) {
			if(subBlocks.get(i) != null) {
				par3List.add(new ItemStack(par1, 1, i));
			}
		}
    }
	
	@Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        /*if (metadata == 15)
        {
            return new TileEntityDungeonSpawner();
        }*/

        return null;
    }
	
	@Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
    {
        int metadata = world.getBlockMetadata(x, y, z);
        if (subBlocks.get(metadata) != null)
        {
            return new ItemStack(Item.getItemFromBlock(this), 1, metadata);
        }

        return super.getPickBlock(target, world, x, y, z);
    }
	
	public boolean getBlocksMovement(IBlockAccess par1World, int x, int y, int z)
    {
		int meta = par1World.getBlockMetadata(x, y, z);
		
		return !this.subBlocks.get(meta).material.blocksMovement();
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

}
