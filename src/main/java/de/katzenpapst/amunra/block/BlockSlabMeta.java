package de.katzenpapst.amunra.block;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.item.ItemBlockMulti;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class BlockSlabMeta extends BlockSlab implements IMetaBlock {

	protected HashMap<String, Integer> nameMetaMap = null;
	protected SubBlock[] subBlocksArray = new SubBlock[8];

	public BlockSlabMeta(String name, Material material) {
		// I think the first parameter is true for doubleslabs...
		super(false, material);
		setBlockName(name);
		nameMetaMap = new HashMap<String, Integer>();
	}

	@Override
	public String getUnlocalizedSubBlockName(int meta) {
		return this.getSubBlock(meta).getUnlocalizedName()+".slab";
	}

	@Override
	public BlockMetaPair addSubBlock(int meta, SubBlock sb) {
		if(meta >= subBlocksArray.length || meta < 0) {
			throw new IllegalArgumentException("Meta "+meta+" must be <= "+(subBlocksArray.length-1)+" && >= 0");
		}

		if(subBlocksArray[meta] != null) {
			throw new IllegalArgumentException("Meta "+meta+" is already in use");
		}

		if(nameMetaMap.get(sb.getUnlocalizedName()) != null) {
			throw new IllegalArgumentException("Name "+sb.getUnlocalizedName()+" is already in use");
		}
		// sb.setParent(this);
		nameMetaMap.put(sb.getUnlocalizedName(), meta);
		subBlocksArray[meta] = sb;
		return new BlockMetaPair(this, (byte) meta);
	}

	public BlockMetaPair addSubBlock(int meta, BlockMetaPair basedOn) {

		return addSubBlock(meta, ((IMetaBlock)basedOn.getBlock()).getSubBlock(basedOn.getMetadata()));
	}


	@Override
	public int getMetaByName(String name) {
		Integer i = nameMetaMap.get(name);
		if(i == null) {
			throw new IllegalArgumentException("Subblock "+name+" doesn't exist");
		}
		return i;
	}

	@Override
	public SubBlock getSubBlock(int meta) {
		meta = meta & 7;
		return subBlocksArray[meta];
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return getSubBlock(meta).getIcon(side, 0);
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
    public Item getItemDropped(int meta, Random random, int fortune)
    {
		meta = meta & 7;
		SubBlock sb = getSubBlock(meta);

		if(sb.dropsSelf()) {
			return Item.getItemFromBlock(this);
		}
		return sb.getItemDropped(0, random, fortune);
    }

	@Override
    public int damageDropped(int meta)
    {
		meta = meta & 7;
		SubBlock sb = getSubBlock(meta);
		if(sb.dropsSelf()) {
			return meta;
		}
		return sb.damageDropped(0);
    }

	@Override
    public int getDamageValue(World world, int x, int y, int z)
    {
    	return world.getBlockMetadata(x, y, z);
    }

	@Override
    public int quantityDropped(int meta, int fortune, Random random)
    {
		SubBlock sb = getSubBlock(meta);
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
		for(int i = 0; i < this.subBlocksArray.length; i++) {
			if(subBlocksArray[i] != null) {
				par3List.add(new ItemStack(par1, 1, i));
			}
		}
    }

	@Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
    {
        int meta = world.getBlockMetadata(x, y, z) & 3;
        if (getSubBlock(meta) != null)
        {
            return new ItemStack(Item.getItemFromBlock(this), 1, meta);
        }

        return super.getPickBlock(target, world, x, y, z);
    }

	@Override
	public void register() {
		GameRegistry.registerBlock(this, ItemBlockMulti.class, this.getUnlocalizedName());

		for(int i=0;i<subBlocksArray.length;i++) {
			SubBlock sb = subBlocksArray[i];
			if(sb != null) {

				this.setHarvestLevel(sb.getHarvestTool(0), sb.getHarvestLevel(0), i);
			}
		}
	}


	@Override
	public String func_150002_b(int meta) {
		// something like getNameByMeta
		// net.minecraft.item.ItemSlab calls this
		meta = meta & 7;
		return this.getUnlocalizedName()+"."+this.getSubBlock(meta).getUnlocalizedName();
	}


	@Override
    public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ)
    {
		int metadata = world.getBlockMetadata(x, y, z);

		return getSubBlock(metadata).getExplosionResistance(par1Entity, world, x, y, z, explosionX, explosionY, explosionZ);
    }

	@Override
    public float getBlockHardness(World world, int x, int y, int z)
    {
        int meta = world.getBlockMetadata(x, y, z);


        return getSubBlock(meta).getBlockHardness(world, x, y, z);
    }

}
