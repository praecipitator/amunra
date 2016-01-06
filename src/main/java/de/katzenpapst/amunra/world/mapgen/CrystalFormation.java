package de.katzenpapst.amunra.world.mapgen;

import java.util.Random;

import de.katzenpapst.amunra.world.WorldHelper;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.util.ForgeDirection;

public class CrystalFormation extends WorldGenerator {

	protected BlockMetaPair material;
	protected BlockMetaPair airBlock;
	boolean allowDownward;
	boolean allowUpward;


	/**
	 *
	 * @param material		Material to generate the formations from
	 * @param airBlock		Block to be considered empty, like air or water or so. Can be null, then world.isAirBlock will be used
	 * @param allowUpward	If true, crystals can grow upwards from the floor
	 * @param allowDownward If true, crystals can grow downwards from the ceiling
	 */
	public CrystalFormation(BlockMetaPair material, BlockMetaPair airBlock, boolean allowUpward, boolean allowDownward)
	{
		this.material = material;
		this.airBlock = airBlock;
		this.allowDownward = allowDownward;
		this.allowUpward = allowUpward;
	}

	public CrystalFormation(BlockMetaPair material) {
		this(material, null, true, true);
	}

	public CrystalFormation(BlockMetaPair material, BlockMetaPair airBlock) {
		this(material, airBlock, true, true);
	}

	public CrystalFormation(BlockMetaPair material, boolean allowUpward, boolean allowDownward) {
		this(material, null, allowUpward, allowDownward);
	}

	protected boolean canPlaceHere(World world, int x, int y, int z)
	{
		if(y < 0 || y > 255) {
			return false;
		}
		if(airBlock == null) {
			return world.isAirBlock(x, y, z);
		}

		return WorldHelper.isBlockMetaPair(world, x, y, z, airBlock);
	}

	protected boolean isSolidBlock(World world, int x, int y, int z, boolean down)
	{
		ForgeDirection dir = down ? ForgeDirection.DOWN : ForgeDirection.UP;
		return world.isSideSolid(x, y, z, dir);
		//world.getBlock(x, y, z).isOpaqueCube();
		// return !world.isAirBlock(x, y, z);
	}

	protected int getLowestBlock(World world, int x, int y, int z)
	{
		for(int curY = y; curY >= 0; curY--) {
			if(!canPlaceHere(world, x, curY, z)) {
				return curY;
			}
		}
		return -1;
	}

	protected int getHighestBlock(World world, int x, int y, int z)
	{
		for(int curY = y; curY <= 255; curY++) {
			if(!canPlaceHere(world, x, curY, z)) {
				return curY;
			}
		}
		return -1;
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z)
    {
		boolean downwards = true;




        if (!this.canPlaceHere(world, x, y, z))
        {
            return false;
        }
        else
        {
        	// find lowest and highest block from here
        	int lowestY = getLowestBlock(world, x, y, z);
        	int highestY = getHighestBlock(world, x, y, z);
        	int actualY = 0;

        	if(lowestY < 0 && highestY >= 0 && allowDownward) {
        		downwards = true;
        	} else if(lowestY >= 0 && highestY < 0 && allowUpward) {
        		downwards = false;
        	} else if(lowestY >= 0 && highestY >= 0) {
        		// both seem to be set
        		if(allowDownward && allowUpward) {
        			downwards = rand.nextBoolean();
        		} else if(allowDownward) {
        			downwards = true;
        		} else if(allowUpward) {
        			downwards = false;
        		} else {
        			return false;
        		}
        	} else {
        		return false;
        	}

        	if(downwards) {
        		actualY = highestY-1; // start one below the highest
        	} else {
        		actualY = lowestY+1; // start one above the highest
        	}

        	if(!canPlaceHere(world, x, actualY, z)) {
    			return false;
    		}

            world.setBlock(x, actualY, z, material.getBlock(), material.getMetadata(), 2);

            for (int l = 0; l < 1500; ++l)
            {
                int curX = x + rand.nextInt(8) - rand.nextInt(8);
                int curY = actualY; // - rand.nextInt(12);
                int curZ = z + rand.nextInt(8) - rand.nextInt(8);

                if(downwards) {
                	curY -= rand.nextInt(12);
                } else {
                	curY += rand.nextInt(12);
                }

                if (this.canPlaceHere(world, curX, curY, curZ))
                {
                    int num = 0;

                    for (int neighbour = 0; neighbour < 6; ++neighbour)
                    {
                        Block block = null;
                        int meta = 0;

                        switch(neighbour) {
                        case 0:
                        	block = world.getBlock(curX - 1, curY, curZ);
                            meta = world.getBlockMetadata(curX - 1, curY, curZ);
                        	break;
                        case 1:
                        	block = world.getBlock(curX + 1, curY, curZ);
                            meta = world.getBlockMetadata(curX + 1, curY, curZ);
                        	break;
                        case 2:
                        	block = world.getBlock(curX, curY - 1, curZ);
                            meta = world.getBlockMetadata(curX, curY - 1, curZ);
                        	break;
                        case 3:
                            block = world.getBlock(curX, curY + 1, curZ);
                            meta = world.getBlockMetadata(curX, curY + 1, curZ);
                        	break;
                        case 4:
                        	block = world.getBlock(curX, curY, curZ - 1);
                            meta = world.getBlockMetadata(curX, curY, curZ - 1);
                        	break;
                        case 5:
                            block = world.getBlock(curX, curY, curZ + 1);
                            meta = world.getBlockMetadata(curX, curY, curZ + 1);
                        	break;
                        }

                        if (block == this.material.getBlock() && meta == this.material.getMetadata())
                        {
                            ++num;
                        }
                    }

                    if (num == 1)
                    {
                        world.setBlock(curX, curY, curZ, this.material.getBlock(), this.material.getMetadata(), 2);
                    }
                }
            }

            return true;
        }
    }



}
