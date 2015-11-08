package de.katzenpapst.amunra.block;

import java.util.Random;

import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class ARTreeSapling extends SubBlockBush {

	protected int minTreeHeight = 4;
	protected final BlockMetaPair wood;
    protected final BlockMetaPair leaves;


	public ARTreeSapling(String name, String texture) {

		super(name, texture);
		wood = ARBlocks.blockMethaneLog;
		leaves = ARBlocks.blockMethaneLeaf;

	}

	/**
     * Ticks the block if it's been scheduled
     */
    @Override
	public void updateTick(World world, int x, int y, int z, Random rand)
    {

        if (world.getBlockLightValue(x, y + 1, z) >= 9 && rand.nextInt(7) == 0)
        {
            this.prepareGrowTree(world, x, y, z, rand);
        }

    }

    public void prepareGrowTree(World world, int x, int y, int z, Random rand)
    {
        int l = world.getBlockMetadata(x, y, z);

        if ((l & 8) == 0)
        {
            world.setBlockMetadataWithNotify(x, y, z, l | 8, 4);
        }
        else
        {
            this.growTree(world, x, y, z, rand);
        }
    }

    public void growTree(World world, int x, int y, int z, Random rand)
    {

        if (!net.minecraftforge.event.terraingen.TerrainGen.saplingGrowTree(world, rand, x, y, z)) return;
  /*
        int curMetadata = world.getBlockMetadata(x, y, z) & 7;
        Object worldGenObj = new WorldGenTree(true, 16, ARBlocks.blockMethaneLog, ARBlocks.blockMethaneLeaf, null);
        int xOffset = 0;
        int zOffset = 0;
        boolean flag = false;
*/

        Block block = Blocks.air;

        // self-removal before tree generation
        world.setBlock(x, y, z, block, 0, 4);
        generate(world, rand, x, y, z, true);
/*

        // self-spreading?
        if (!((WorldGenerator)worldGenObj).generate(world, rand, x + xOffset, y, z + zOffset))
        {
            if (flag)
            {
                world.setBlock(x + xOffset, y, z + zOffset, this, curMetadata, 4);
                world.setBlock(x + xOffset + 1, y, z + zOffset, this, curMetadata, 4);
                world.setBlock(x + xOffset, y, z + zOffset + 1, this, curMetadata, 4);
                world.setBlock(x + xOffset + 1, y, z + zOffset + 1, this, curMetadata, 4);
            }
            else
            {
                world.setBlock(x, y, z, this, curMetadata, 4);
            }
        }
*/
    }

    public boolean checkBlockAt(World world, int x, int y, int z, int metadata)
    {
        return world.getBlock(x, y, z) == this && (world.getBlockMetadata(x, y, z) & 7) == metadata;
    }


    /**
	 * func_149851_a is basically a stillGrowing() method.
	 * It returns (or should return) true if the growth stage is less than the max growth stage.
	 *
	 * info source: http://www.minecraftforge.net/forum/index.php?topic=22571.0
	 */
    @Override
	public boolean func_149851_a(World world, int x, int y, int z, boolean isWorldRemote)
    {
        return true;
    }

    /**
	 * func_149852_a is basically a canBoneMealSpeedUpGrowth() method.
	 * I usually just return true, but depends on your crop.
	 */
    @Override
	public boolean func_149852_a(World world, Random rand, int x, int y, int z)
    {
        return world.rand.nextFloat() < 0.45D;
    }

    /**
	 * func_149853_b is basically an incrementGrowthStage() method.
	 * In vanilla crops the growth stage is stored in metadata so then in this method
	 * you would increment it if it wasn't already at maximum and store back in metadata.
	 *
	 */
    @Override
	public void func_149853_b(World p_149853_1_, Random p_149853_2_, int p_149853_3_, int p_149853_4_, int p_149853_5_)
    {
        this.prepareGrowTree(p_149853_1_, p_149853_3_, p_149853_4_, p_149853_5_, p_149853_2_);
    }

    @Override
	public boolean canPlaceOn(BlockMetaPair blockToCheck, int meta) {
    	return canPlaceOn(blockToCheck.getBlock(), blockToCheck.getMetadata(), meta);
	}

    @Override
	public boolean canPlaceOn(Block blockToCheck, int metaToCheck, int meta) {
    	return
    			(blockToCheck == ARBlocks.blockMethaneDirt.getBlock() && metaToCheck == ARBlocks.blockMethaneDirt.getMetadata()) ||
    			(blockToCheck == ARBlocks.blockMethaneGrass.getBlock() && metaToCheck == ARBlocks.blockMethaneGrass.getMetadata())
    			;
	}

    protected boolean func_150523_a(Block p_150523_1_)
    {
        return p_150523_1_.getMaterial() == Material.air || p_150523_1_.getMaterial() == Material.leaves || p_150523_1_ == Blocks.grass || p_150523_1_ == Blocks.dirt || p_150523_1_ == Blocks.log || p_150523_1_ == Blocks.log2 || p_150523_1_ == Blocks.sapling || p_150523_1_ == Blocks.vine;
    }

    protected boolean canReplaceBlock(World world, int x, int y, int z)
    {
        Block block = world.getBlock(x, y, z);
        return block.isAir(world, x, y, z) || block.isLeaves(world, x, y, z) || block.isWood(world, x, y, z) || func_150523_a(block);
    }

    protected boolean canGenerateHere(World world, Random rand, int x, int y, int z, int l)
    {
    	byte b0;
    	int curZ;
        Block block;
    	for (int curY = y; curY <= y + 1 + l; ++curY)
        {
            b0 = 1;

            if (curY == y)
            {
                b0 = 0;
            }

            if (curY >= y + 1 + l - 2)
            {
                b0 = 2;
            }

            for (int curX = x - b0; curX <= x + b0; ++curX)
            {
                for (curZ = z - b0; curZ <= z + b0; ++curZ)
                {
                    if (curY >= 0 && curY < 256)
                    {
                        block = world.getBlock(curX, curY, curZ);

                        if (!this.canReplaceBlock(world, curX, curY, curZ))
                        {
                            return false;
                        }
                    }
                    else
                    {
                    	return false;
                    }
                }
            }
        }
    	return true;
    }

    /**
     * Actual generation function. Can be called from worldgen
     *
     * @param world
     * @param rand
     * @param x
     * @param y
     * @param z
     * @param notify	apparently I have to pass false here on worldgen and true on sapling-growth
     * @return
     */
    public boolean generate(World world, Random rand, int x, int y, int z, boolean notify)
    {
        int curTreeHeight = rand.nextInt(3) + this.minTreeHeight+5;
        boolean flag = true;

        // I think this checks the boundingbox
        if (y >= 1 && y + curTreeHeight + 1 <= 256)
        {
            byte b0;
            int curZ;
            Block block;

            if(!canGenerateHere(world, rand, x, y, z, curTreeHeight)) {
            	return false;
            }

            Block block2 = world.getBlock(x, y - 1, z);
            int meta2 = world.getBlockMetadata(x, y - 1, z);

            boolean isSoil = this.canPlaceOn(new BlockMetaPair(block2, (byte) meta2), 0);//block2.canSustainPlant(world, x, y - 1, z, ForgeDirection.UP, (IPlantable)this);
            if (isSoil && y < 256 - curTreeHeight - 1)
            {
                block2.onPlantGrow(world, x, y - 1, z, x, y, z);
                b0 = 6; // I *THINK* this is the actual leaf height?
                byte b1 = 0;
                int l1;
                int vineX;
                int vineZ;
                int i3;

                for (curZ = y - b0 + curTreeHeight; curZ <= y + curTreeHeight; ++curZ)
                {
                    i3 = curZ - (y + curTreeHeight);
                    l1 = b1 + 1 - i3 / 2;

                    for (vineX = x - l1; vineX <= x + l1; ++vineX)
                    {
                        vineZ = vineX - x;

                        for (int k2 = z - l1; k2 <= z + l1; ++k2)
                        {
                            int l2 = k2 - z;

                            if (Math.abs(vineZ) != l1 || Math.abs(l2) != l1 || rand.nextInt(2) != 0 && i3 != 0)
                            {
                                Block block1 = world.getBlock(vineX, curZ, k2);

                                if (block1.isAir(world, vineX, curZ, k2) || block1.isLeaves(world, vineX, curZ, k2))
                                {
                                    this.setBlockAndNotifyAdequately(world, vineX, curZ, k2, this.leaves.getBlock(), this.leaves.getMetadata(), notify);
                                }
                            }
                        }
                    }
                }

                for (curZ = 0; curZ < curTreeHeight; ++curZ)
                {
                    block = world.getBlock(x, y + curZ, z);

                    if (block.isAir(world, x, y + curZ, z) || block.isLeaves(world, x, y + curZ, z))
                    {
                        this.setBlockAndNotifyAdequately(world, x, y + curZ, z, this.wood.getBlock(), this.wood.getMetadata(), notify);

                    }
                }


                return true;
            }
            return false;

        }
        return false;
    }

    protected void setBlockAndNotifyAdequately(World world, int x, int y, int z, Block block, int meta, boolean notify)
    {
        if (notify)
        {
            world.setBlock(x, y, z, block, meta, 3);
        }
        else
        {
            world.setBlock(x, y, z, block, meta, 2);
        }
    }

}
