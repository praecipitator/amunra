package de.katzenpapst.amunra.world;

import micdoodle8.mods.galacticraft.api.prefab.world.gen.ChunkProviderSpace;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

abstract public class AmunraChunkProvider extends ChunkProviderSpace {
	
	public static final int CHUNK_SIZE_X = 16;
	public static final int CHUNK_SIZE_Y = 256;
	public static final int CHUNK_SIZE_Z = 16;

	public AmunraChunkProvider(World par1World, long seed,
			boolean mapFeaturesEnabled) {
		super(par1World, seed, mapFeaturesEnabled);
	}

	@Override
	public int getCraterProbability() {
		// vestigial
        return 2000;
    }
	
	/**
	 * I failed fixing this. I might do this as mapgen instead
	 */
	@Override
	public void makeCrater(int craterX, int craterZ, int chunkX, int chunkZ, int size, Block[] chunkArray, byte[] metaArray)
    {
		/*
		final double centerFalloff = 0.01;
		final double borderFalloff = 0.02;
		final double centerHeightFactor = 0.5;
		final double borderHeightFactor = 1;
		final double craterHeight = 9;
		final double borderStartLimit = 0.75;
		double height = this.getSeaLevel();
		double sizeSq = size * size;
        for (int x = 0; x < AmunraChunkProvider.CHUNK_SIZE_X; x++)
        {
            for (int z = 0; z < AmunraChunkProvider.CHUNK_SIZE_Z; z++)
            {
                double xDev = craterX - (chunkX + x);
                double zDev = craterZ - (chunkZ + z);
                if (xDev * xDev + zDev * zDev < sizeSq)
                {
                    xDev /= size;
                    zDev /= size;
                    // this is the distance from the crater's center, normed to size, squared
                    final double radiusSq = xDev * xDev + zDev * zDev;
                    //final double borderFactor = radiusSq/sizeSq;
                    final double radius = Math.sqrt(radiusSq);
                    double yDev = 0;
                    // 0.2/((x^2+0.2))  +  0.2/(((x-3)^2+0.2))
                    
                    // center
                    // yDev += centerHeightFactor*centerFalloff/(radiusSq+centerFalloff);
                    // border
                    yDev += borderHeightFactor*borderFalloff/(Math.pow(borderStartLimit-radius, 2)+borderFalloff);
                    
                    yDev *= craterHeight;
                    
                    yDev = height-(craterHeight-yDev);
                    
                    int highestY = this.getHighestNonAir(chunkArray, x, z);
                    
                    //if(radius > borderStartLimit && yDev < highestY) {
                    //	yDev = this.fuckYouLerp(yDev, highestY, radius-borderStartLimit);
                    //}
                    
                    if(yDev>127) {
                    	yDev = 127;
                    }
                    if(yDev > highestY) {
                    	for(int y=(int)yDev;y>highestY;y--) {
                    		if (Blocks.air == chunkArray[this.getIndex(x, y, z)])
	                        {
	                            chunkArray[this.getIndex(x, y, z)] = getStoneBlock().getBlock();
	                            metaArray[this.getIndex(x, y, z)] = getStoneBlock().getMetadata();
	                        }
                    	}
                    } else {
                    	
	                    for (int y = highestY; y > yDev; y--)
	                    {
	                        if (Blocks.air != chunkArray[this.getIndex(x, y, z)])
	                        {
	                            chunkArray[this.getIndex(x, y, z)] = Blocks.air;
	                            metaArray[this.getIndex(x, y, z)] = 0;
	                        }
	                    }
                    }
                }
            }
        }
        */
    }
	
	/**
	 * Because private...
	 * @param d1
	 * @param d2
	 * @param t
	 * @return
	 */
	protected double fuckYouLerp(double d1, double d2, double t)
    {
        if (t < 0.0)
        {
            return d1;
        }
        else if (t > 1.0)
        {
            return d2;
        }
        else
        {
            return d1 + (d2 - d1) * t;
        }
    }
	
	private int getHighestNonAir(Block[] blocks, int x, int z) {
		for(int y=127;y>1;y--) {
			if(blocks[this.getIndex(x, y, z)] != Blocks.air) {
				return y;
			}
		}
		return 1;
	}
	
	protected int getIndex(int x, int y, int z)
    {
        return (x * 16 + z) * 256 + y;
    }

}
