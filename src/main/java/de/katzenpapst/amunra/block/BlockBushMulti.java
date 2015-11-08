package de.katzenpapst.amunra.block;

import static net.minecraftforge.common.EnumPlantType.Plains;
import java.util.ArrayList;
import java.util.Random;

import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.IShearable;

// SEE net.minecraft.block.BlockBush
public class BlockBushMulti extends BlockBasicMulti implements IGrowable, IShearable, IPlantable {

	public BlockBushMulti(String name, Material mat) {
		super(name, mat);
		// TODO Auto-generated constructor stub
	}

	public BlockBushMulti(String name, Material mat, int numSubBlocks) {
		super(name, mat, numSubBlocks);
		// TODO Auto-generated constructor stub
	}

	@Override
	public BlockMetaPair addSubBlock(int meta, SubBlock sb) {
		if(!(sb instanceof SubBlockBush)) {
			throw new IllegalArgumentException("BlockBushMulti can only accept SubBlockBush");
		}
		return super.addSubBlock(meta, sb);
	}

	@Override
	public boolean isShearable(ItemStack item, IBlockAccess world, int x,
			int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		return ((SubBlockBush)this.getSubBlock(meta)).isShearable(item, world, x, y, z);
	}

	@Override
	public ArrayList<ItemStack> onSheared(ItemStack item, IBlockAccess world,
			int x, int y, int z, int fortune) {

		int meta = world.getBlockMetadata(x, y, z);
		return ((SubBlockBush)this.getSubBlock(meta)).onSheared(item, world, x, y, z, fortune);
	}

	/**
	 * func_149851_a is basically a stillGrowing() method.
	 * It returns (or should return) true if the growth stage is less than the max growth stage.
	 *
	 * info source: http://www.minecraftforge.net/forum/index.php?topic=22571.0
	 */
	@Override
	public boolean func_149851_a(World world, int x,
			int y, int z, boolean isWorldRemote) {
		int meta = world.getBlockMetadata(x, y, z);
		return ((SubBlockBush)this.getSubBlock(meta)).func_149851_a(world, x, y, z, isWorldRemote);
	}

	/**
	 * func_149852_a is basically a canBoneMealSpeedUpGrowth() method.
	 * I usually just return true, but depends on your crop.
	 */
	@Override
	public boolean func_149852_a(World world, Random rand,
			int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		return ((SubBlockBush)this.getSubBlock(meta)).func_149852_a(world, rand, x, y, z);
	}

	/**
	 * func_149853_b is basically an incrementGrowthStage() method.
	 * In vanilla crops the growth stage is stored in metadata so then in this method
	 * you would increment it if it wasn't already at maximum and store back in metadata.
	 *
	 */
	@Override
	public void func_149853_b(World world, Random rand,
			int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		((SubBlockBush)this.getSubBlock(meta)).func_149853_b(world, rand, x, y, z);

	}

	/**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    @Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_)
    {
        return null;
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    @Override
	public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    @Override
	public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * The type of render function that is called for this block
     */
    @Override
	public int getRenderType()
    {
        return 1;
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z)
    {
        return Plains;
    }

	@Override
	public Block getPlant(IBlockAccess world, int x, int y, int z) {
		return this;
	}

	@Override
	public int getPlantMetadata(IBlockAccess world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z);
	}

	public boolean canPlaceOn(BlockMetaPair blockToCheck, int meta) {
		return ((SubBlockBush)this.getSubBlock(meta)).canPlaceOn(blockToCheck, 0);
	}

	public boolean canPlaceOn(Block blockToCheck, int metaToCheck, int meta) {
		return ((SubBlockBush)this.getSubBlock(meta)).canPlaceOn(blockToCheck, metaToCheck, 0);
	}

}
