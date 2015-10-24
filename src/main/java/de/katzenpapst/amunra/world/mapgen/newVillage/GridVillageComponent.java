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
    	switch(coordMode) {
		case 0:
			return unrotated;
		case 1:
			// 1 -> rotate by 90° CCW, aka N turns to W, W to S, etc
			unrotated -= 1;
			if(unrotated < 0) {
				unrotated = 3;
			}
			return unrotated;
		case 2:
			switch(unrotated) {
			case 1: return 3; // swap N<-->S
			case 3: return 1;
			default:
				return unrotated;
			}
		case 3:
			switch(unrotated) { // 3 -> coordflip, swap N <--> O and S <--> W
			case 0: return 1;
			case 1: return 0;
			case 2: return 3;
			case 3: return 2;
			}
			
		}
		return unrotated;
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
		switch(coordMode) {
		case 0:
			return unrotated;
		case 1:
			switch(unrotated) {
			case 0: return 2;   // seems to be rather than that an N<-->W and E<-->S swap
			case 1: return 3;
			// found by trial&error
			case 2: return 1; //was 0
			case 3: return 0; // was 1
			}
			break;
		case 2:
			switch(unrotated) {
			case 3: return 2; // swap N<-->S
			case 2: return 3;
			default:
				return unrotated;
			}
		case 3:
			switch(unrotated) { // 3 -> coordflip, swap N <--> E and S <--> W
			// again trial&error
			case 0: return 2;
			case 1: return 3;
			case 2: return 0;
			case 3: return 1;
			/*case 0: return 3;
			case 1: return 2;
			case 2: return 1;
			case 3: return 0;*/
			}
			
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
        
    	switch(coordMode) {
    	case 0:
    		// 0 -> direct
    		return unrotated;
    	case 1:
    		// N<-->W and E<-->S swap //  1 -> rotate by 90° CCW, aka N turns to W, W to S, etc
    		switch(unrotated) {
    		case 0: return 2;
    		case 1: return 3;
    		case 2: return 0;
    		case 3: return 1; 
    		}
    		break;
    	case 2:
    		// 2 -> Z is flipped aka mirror at x, aka N <--> S
    		switch(unrotated) {
    		case 0: return 1;
    		case 1: return 0;
    		case 2:
    		case 3:
    			return unrotated;
    		}
    		break;
    	case 3:
    		// 3 -> coordflip, swap N <--> O and S <--> W
    		switch(unrotated) {
    		case 0: return 3;
    		case 1: return 2;
    		case 2: return 1;
    		case 3: return 0; 
    		}

    	}
    	return unrotated;
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
        /*cx *= 16;
        cz *= 16;
        x -= cx;
        z -= cz;*/
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
