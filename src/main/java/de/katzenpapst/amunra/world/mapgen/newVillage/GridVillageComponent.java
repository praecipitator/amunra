package de.katzenpapst.amunra.world.mapgen.newVillage;

import de.katzenpapst.amunra.world.mapgen.newVillage.populator.SetSignText;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class GridVillageComponent {
	

	protected int groundLevel = -1;
	
	// final public static GridVillageComponent dummyComponent = new GridVillageComponent();
	
	protected GridVillageStart parent = null;
	/**
	 * I think this goes only 0-3...
	 * 
	 * From what I figured out, it is the following transformations:
	 * 
	 * 0: identity
	 * 		N
	 *    W-+-E
	 *      S
	 * 1: mirror on the NW-ES diagonal
	 * 	    W
	 *    N-+-S
	 *      E 
	 * 2: mirror on WE
	 * 		S
	 *    W-+-E
	 *      N
	 * 3: mirror on the NE-WS diagonal
	 *      W
	 *    N-+-S
	 *      E      
	 */
	protected int coordMode = 0;
	protected StructureBoundingBox structBB;
	
	
	public void setStructureBoundingBox(StructureBoundingBox structBB) {
		this.structBB = structBB;
	}
	
	public StructureBoundingBox getStructureBoundingBox() {
		return this.structBB;
	}
	
	protected boolean generateChunk(int chunkX, int chunkZ, Block[] arrayOfIDs, byte[] arrayOfMeta) {		
		return true;
	}
	
	protected void setParent(GridVillageStart parent) {
		this.parent = parent;
	}
	
	protected void setCoordMode(int coordMode) {
		this.coordMode = coordMode;
	}
	

    
    protected int translateX(int x, int z) {
    	switch(this.coordMode) {
    	case 0:
    	case 2:
    		return x; // keep them as-is
    	case 1:
    		// translate z to "relative to bb", then do what getXWithOffset did
    		return this.structBB.maxX - (z - this.structBB.minZ);
    	case 3:
    		// similar to above
    		return this.structBB.minX + (z - this.structBB.minZ);
    	}
    		
    	return x;
    }
    
    protected int translateZ (int x, int z) {
    	switch (this.coordMode)
        {
            case 0:
                return z;
            case 1:
            case 3:
                return this.structBB.minZ + (x - this.structBB.minX);
            case 2:
                return this.structBB.maxZ - (z - this.structBB.minZ);
            default:
                return z;
        }
    }
    
    protected int getHighestSolidBlockInBB(Block[] blocks, byte[] metas, int chunkX, int chunkZ, int x, int z) {
		int xOffset = getXWithOffset(x, z);
		//y = getYWithOffset(y);
		int zOffset = getZWithOffset(x, z);
		
		int relX = abs2rel(xOffset, chunkX);
		int relZ = abs2rel(zOffset, chunkZ);
		if(relX < 0 || relX >= 16 || relZ < 0 || relZ >= 16) {
			return -1;
		}
		
		return getHighestSolidBlock(blocks, metas, relX, relZ);
	}
	
	protected boolean placeBlockRel2BB(Block[] blocks, byte[] metas, int chunkX, int chunkZ, int x, int y, int z, BlockMetaPair block) {
		int xOffset = getXWithOffset(x, z);
		//y = getYWithOffset(y);
		int zOffset = getZWithOffset(x, z);
		
		int relX = abs2rel(xOffset, chunkX);
		int relZ = abs2rel(zOffset, chunkZ);
		if(relX < 0 || relX >= 16 || relZ < 0 || relZ >= 16) {
			return false;
		}
		return placeBlockRel(blocks, metas, relX, y, relZ, block);
	}
	
	protected boolean placeBlockRel2BB(Block[] blocks, byte[] metas, int chunkX, int chunkZ, int x, int y, int z, Block block, int meta) {
		int xOffset = getXWithOffset(x, z);
		//y = getYWithOffset(y);
		int zOffset = getZWithOffset(x, z);
		
		int relX = abs2rel(xOffset, chunkX);
		int relZ = abs2rel(zOffset, chunkZ);
		if(relX < 0 || relX >= 16 || relZ < 0 || relZ >= 16) {
			return false;
		}
		return placeBlockRel(blocks, metas, relX, y, relZ, block, meta);
	}
	
	protected int getXWithOffset(int x, int z)
    {
        switch (this.coordMode)
        {
            case 0:
            case 2:
                return this.structBB.minX + x;
            case 1:
                return this.structBB.maxX - z;
            case 3:
                return this.structBB.minX + z;
            default:
                return x;
        }
    }


    protected int getZWithOffset(int x, int z)
    {
        switch (this.coordMode)
        {
            case 0:
                return this.structBB.minZ + z;
            case 1:
            case 3:
                return this.structBB.minZ + x;
            case 2:
                return this.structBB.maxZ - z;
            default:
                return z;
        }
    }
    
    protected void placeStandingSign(Block[] blocks, byte[] metas, int chunkX, int chunkZ, int x, int y, int z, String text) {
    	
		if(placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, y, z, Blocks.standing_sign, (byte)0)) {
			int xOffset = getXWithOffset(x, z);
			//y = getYWithOffset(y);
			int zOffset = getZWithOffset(x, z);
			SetSignText sst = new SetSignText(xOffset, y, zOffset, text);
			this.parent.addPopulator(sst);
		}
    }
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////// STATIC HELPERS //////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
    
    /**
     * For doors, in a sense, and furnaces
     * 
     * 	 1
     * 0-+-2
     *   3
     *   
     * @param unrotated
     * @param coordMode
     * @return
     */
    public static int rotateDoorlikeMetadata(int unrotated, int coordMode) {
    	return rotateUniversalMetadata(unrotated, coordMode, 1, 3, 2, 0);
    }
    
    public static int rotateTorchMetadata(int unrotated, int coordMode) {
    	// error with coordMode=1, everything is just the wrong way round
    	
    	return rotateStairlikeMetadata(unrotated-1, coordMode)+1;
    }
	/**
	 * This should work for stairs 
	 * 0 = E
	 * 1 = W
	 * 2 = S
	 * 3 = N
	 * for torches, add +1; 0 means nothing and 5 means "on the ground", and the interpretation is "torch FACING dir"
	 * 
	 *   3
	 * 1-+-0
	 *   2
	 *
	 *   
	 * @param unrotated
	 * @param coordMode
	 * @return
	 */
	public static int rotateStairlikeMetadata(int unrotated, int coordMode) {
		return rotateUniversalMetadata(unrotated, coordMode, 3, 2, 0, 1);
	}
	
	/**
	 * Universal function for metadata rotation, based on what I found using trial&error with torches
	 * 
	 * @param unrotated
	 * @param coordMode
	 * @param n
	 * @param s
	 * @param e
	 * @param w
	 * @return
	 * 
	 *   n
	 * w-+-e
	 *   s
	 */
	public static int rotateUniversalMetadata(int unrotated, int coordMode, int n, int s, int e, int w) {
		switch(coordMode) {
		/*case 0:
			return unrotated;*/
		case 1:
			if(unrotated == n) return e;
			if(unrotated == e) return s;
			if(unrotated == w) return n;
			if(unrotated == s) return w;
			break;
		case 2:
			if(unrotated == n) return s;
			if(unrotated == s) return n;
			break; // unrotated will be returned anyway
		case 3:
			if(unrotated == e) return s;
			if(unrotated == w) return n;
			if(unrotated == s) return e;
			if(unrotated == n) return w;
			break;
		}
		return unrotated;
	}
	
	/**
	 * Rotates metadata for the
	 *   2
	 * 4-+-5
	 *   3
	 * model, aka rotateStandardMetadata +2
	 * 
	 * @param unrotated
	 * @param coordMode
	 * @param offset
	 * @return
	 */
	public static int rotatePistonlikeMetadata(int unrotated, int coordMode) {
		return rotateStandardMetadata(unrotated-2, coordMode)+2;
	}
	
	/**
	 * Rotates the metadata which most things seem to use:
	 *   0
	 * 2-+-3
	 *   1
	 * This should work for solar collectors and trapdoors, but in a reversed non-intuitive way
	 * 
	 * @param unrotated
	 * @param coordMode
	 * @return
	 */
	public static int rotateStandardMetadata(int unrotated, int coordMode) {
    	return rotateUniversalMetadata(unrotated, coordMode, 0, 1, 3, 2);
    }
	
	/**
	 * Converts an absolute coordinate to relative. 
	 * Does not validate the result
	 * 
	 * @param absCoord
	 * @param chunkCoord
	 * @return
	 */
	public static int abs2rel(int absCoord, int chunkCoord) {
		return absCoord - chunkCoord * 16;
	}
	
	/**
	 * Converts a relative chunk coordinate to an absolute one
	 * Does not validate the input
	 * 
	 * @param relCoord
	 * @param chunkCoord
	 * @return
	 */
	public static int rel2abs(int relCoord, int chunkCoord) {
		return relCoord + chunkCoord * 16;
	}
	
	public static int getAverageGroundLevel(Block[] blocks, byte[] metas, StructureBoundingBox totalBB, StructureBoundingBox chunkBB, int minimum)
    {
        int sum = 0;
        int total = 0;
        
        int chunkX = chunkBB.minX / 16;
        int chunkZ = chunkBB.minZ / 16;

        for (int z = totalBB.minZ; z <= totalBB.maxZ; ++z)
        {
            for (int x = totalBB.minX; x <= totalBB.maxX; ++x)
            {
                if (chunkBB.isVecInside(x, 64, z))
                {
                	sum += Math.max(getHighestSolidBlock(blocks, metas, abs2rel(x, chunkX), abs2rel(z, chunkZ)), minimum);
                	
                    //sum += Math.max(par1World.getTopSolidOrLiquidBlock(x, z), par1World.provider.getAverageGroundLevel());
                    ++total;
                }
            }
        }

        if (total == 0)
        {
            return -1;
        }
        else
        {
            return sum / total;
        }
    }
	
	public static int getHighestSolidBlock(Block[] blocks, byte[] metas, int relX, int relZ) {
		
		for(int y=255;y>=0;y--) {
			int index = getIndex(relX, y, relZ);
			Block curBlock = blocks[index];
			if(curBlock == null) {
				continue;
			}
			int meta = metas[index];
			if(curBlock.getMaterial().blocksMovement() && curBlock.getMaterial() != Material.leaves) {
				return y+1;
			}
		}
		return -1;
	}
	
	/**
	 * Places a block into the arrays using coordinates relative to the current chunk 
	 * 
	 * @param blocks
	 * @param metas
	 * @param x
	 * @param y
	 * @param z
	 * @param id
	 * @param meta
	 * @return
	 */
	public static boolean placeBlockRel(Block[] blocks, byte[] metas, int x, int y, int z, Block id, int meta)
    {
        if (x < 0 || x >= 16 || z < 0 || z >= 16)
        {
            return false;
        }
        final int index = getIndex(x, y, z);
        blocks[index] = id;
        metas[index] = (byte) meta;
        
        return true;
    }
	
	public static boolean placeBlockRel(Block[] blocks, byte[] metas, int x, int y, int z, BlockMetaPair block)
    {
        if (x < 0 || x >= 16 || z < 0 || z >= 16)
        {
            return false;
        }
        final int index = getIndex(x, y, z);
        blocks[index] = block.getBlock();
        metas[index] = block.getMetadata();
        
        return true;
    }
	
	
	
	/**
	 * Places a block into the arrays using absolute coordinates+coordinates of the current chunk
	 * 
	 * @param blocks
	 * @param metas
	 * @param x
	 * @param y
	 * @param z
	 * @param cx
	 * @param cz
	 * @param id
	 * @param meta
	 * @return
	 */
	public static boolean placeBlockAbs(Block[] blocks, byte[] metas, int x, int y, int z, int cx, int cz, Block id, int meta)
    {
        return placeBlockRel(blocks, metas, abs2rel(x, cx), y, abs2rel(z,cz), id, meta);
    }

    public static int getIndex(int x, int y, int z)
    {
        return (x * 16 + z) * 256 + y;
    }

    /*
    protected int getZWithOffset(int x, int z)
    {
        switch (this.coordMode)
        {
            case 0:
                return this.structBB.minZ + z;
            case 1:
            case 3:
                return this.structBB.minZ + x;
            case 2:
                return this.structBB.maxZ - z;
            default:
                return z;
        }
    }*/
}
