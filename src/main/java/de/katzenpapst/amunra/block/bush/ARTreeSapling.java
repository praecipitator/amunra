package de.katzenpapst.amunra.block.bush;

import java.util.Random;

import de.katzenpapst.amunra.block.ARBlocks;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class ARTreeSapling extends AbstractSapling {



    protected int canopyWidth = 7;
    protected int canopyHeight = 9;
    protected int canopyCenter = 5;
    protected int minStemHeight = 2;


    public ARTreeSapling(String name, String texture) {
        super(name, texture);
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



    protected boolean canGenerateHere(World world, Random rand, int x, int y, int z, int stemHeight, int halfCanopyWidth, int canopyHeight)
    {
        // check stem first
        for (int curY = y; curY <= y + stemHeight; ++curY)
        {
            if (!this.canReplaceBlock(world, x, curY, z))
            {
                return false;
            }
        }
        // just check the boundingbox for now
        for (int curY = y+stemHeight+1; curY <= y + 1 + stemHeight + canopyHeight; ++curY)
        {

            for (int curX = x - halfCanopyWidth; curX <= x + halfCanopyWidth; ++curX)
            {
                for (int curZ = z - halfCanopyWidth; curZ <= z + halfCanopyWidth; ++curZ)
                {
                    if (curY >= 0 && curY < 256)
                    {
                        // Block block = world.getBlock(curX, curY, curZ);

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
    @Override
    public boolean generate(World world, Random rand, int x, int y, int z, boolean notify)
    {
        int curTreeHeight = rand.nextInt(3)+ this.minStemHeight + this.canopyHeight;

        int halfWidth = (int)Math.ceil((this.canopyWidth-1)/2);
        int halfHeight = (int)Math.ceil((this.canopyHeight-1)/2);
        int stemHeight = curTreeHeight - this.canopyHeight;


        int highestYLeaf = 0;

        if (y >= 1 && y + curTreeHeight + 1 <= 256)
        {
            Block block;

            if(!canGenerateHere(world, rand, x, y, z, stemHeight, halfWidth, canopyHeight)) {
                return false;
            }

            Block block2 = world.getBlock(x, y - 1, z);
            int meta2 = world.getBlockMetadata(x, y - 1, z);

            boolean isSoil = this.canPlaceOn(new BlockMetaPair(block2, (byte) meta2), 0);//block2.canSustainPlant(world, x, y - 1, z, ForgeDirection.UP, (IPlantable)this);
            if (isSoil && y < 256 - curTreeHeight - 1)
            {
                block2.onPlantGrow(world, x, y - 1, z, x, y, z);

                // int width = (int)Math.ceil((this.canopyWidth-1)/2);

                // generate the leaves first
                for (int curY = y + stemHeight; curY <= y + stemHeight + this.canopyHeight; ++curY)
                {
                    for (int curX = x - halfWidth; curX <= x + halfWidth; ++curX)
                    {

                        for (int curZ = z - halfWidth; curZ <= z + halfWidth; ++curZ)
                        {
                            // check the ellipsoid stuff
                            //if(Math.pow((curX-x)/halfWidth,2) + Math.pow((curY1-y-canopyCenter)/halfHeight, 2) + Math.pow((curZ-z)/halfWidth, 2) <= 1) {
                            double eFactor = getEllipsoidFactor(curX-x, curY-y-canopyCenter, curZ-z, halfWidth, halfHeight);
                            if(eFactor <= 1.0D) {
                                if(eFactor > 0.90D) {
                                    // randomly don't
                                    if(rand.nextDouble() < 0.5D) {
                                        continue;
                                    }
                                }
                                // draw them
                                Block block1 = world.getBlock(curX, curY, curZ);

                                if (block1.isAir(world, curX, curY, curZ) || block1.isLeaves(world, curX, curY, curZ) || isBlockReplaceable(block1))
                                {
                                    this.setBlockAndNotifyAdequately(world, curX, curY, curZ, this.leaves.getBlock(), this.leaves.getMetadata(), notify);
                                }

                                if(highestYLeaf < curY) {
                                    highestYLeaf = curY;
                                }
                            }

                        }
                    }
                }

                // stem

                for (int curY = 0; curY < highestYLeaf-y; ++curY)
                {
                    block = world.getBlock(x, y + curY, z);

                    if (block.isAir(world, x, y + curY, z) || block.isLeaves(world, x, y + curY, z) || isBlockReplaceable(block))
                    {
                        this.setBlockAndNotifyAdequately(world, x, y + curY, z, this.wood.getBlock(), this.wood.getMetadata(), notify);

                    }
                }


                return true;
            }
            return false;

        }
        return false;
    }

    protected double getEllipsoidFactor(float x, float y, float z, float widthZX, float height) {
        return Math.pow(x,2)/(widthZX*widthZX) + Math.pow(y, 2)/(height*height) + Math.pow(z, 2)/(widthZX*widthZX);
    }


}
