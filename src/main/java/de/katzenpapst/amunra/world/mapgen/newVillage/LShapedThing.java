package de.katzenpapst.amunra.world.mapgen.newVillage;

import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityCreature;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import de.katzenpapst.amunra.mob.entity.EntityRobotVillager;
import de.katzenpapst.amunra.world.mapgen.newVillage.populator.SetSignText;

public class LShapedThing extends GridVillageComponent {
	
	protected int groundLevel = -1;
	
	@Override
	public boolean generateChunk(int chunkX, int chunkZ, Block[] blocks, byte[] metas) {
		
		// now, how to get the height?
		StructureBoundingBox chunkBB = new StructureBoundingBox(chunkX*16, chunkZ*16, chunkX*16+15, chunkZ*16+15);
		int fallbackGround = this.parent.getGroundLevel();
		if(groundLevel == -1) {
			groundLevel = getAverageGroundLevel(blocks, metas, getStructureBoundingBox(), chunkBB, fallbackGround);
			if(groundLevel == -1) {
				groundLevel = fallbackGround; // but this shouldn't even happen...
			}
		}
		
		StructureBoundingBox myBB = this.getStructureBoundingBox();
		BlockMetaPair mat = this.parent.getWallMaterial();
		BlockMetaPair floor = this.parent.getFloorMaterial();
		BlockMetaPair padding = this.parent.getFillMaterial();
		
		// draw floor first
		int startX = 0;
		int stopX = myBB.getXSize();
		int startZ = 0;
		int stopZ = myBB.getZSize();
		
		// there is an offset error with coordMode=2 and 1
		if(coordMode == 1) {
			// x
			
		}

		
		
		int xCenter = (int)Math.ceil((stopX-startX)/2+startX);
		int zCenter = (int)Math.ceil((stopZ-startZ)/2+startZ);
		for(int x = startX; x < stopX; x++) {
			for(int z = startZ; z < stopZ; z++) {
				
				//int x = this.translateX(rawX, rawZ);
				//int z = this.translateZ(rawX, rawZ);
				
				
				
				int highestGroundBlock = getHighestSolidBlockInBB(blocks, metas, chunkX, chunkZ, x, z);
				if(highestGroundBlock == -1) {
					continue; // that should mean that we aren't in the right chunk
				}
				
				// now fill
				for(int y=highestGroundBlock-1;y<groundLevel; y++) {
					//padding
					placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, y, z, padding);
				}
				// floor
				placeBlockRel2BB(blocks, metas,chunkX, chunkZ, x, groundLevel-1, z, floor);
				
				if(x == startX || z == startZ) {
					placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel, z, mat);
					
					if(x == startX && z == startZ) {
						BlockMetaPair sign = new BlockMetaPair(Blocks.standing_sign, (byte) 0);
						if(placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel+1, z, sign)) {
							int xOffset = getXWithOffset(x, z);
							//y = getYWithOffset(y);
							int zOffset = getZWithOffset(x, z);
							SetSignText sst = new SetSignText(xOffset, groundLevel+1, zOffset, "cMode = "+this.coordMode+"\nOther Line");
							this.parent.addPopulator(sst);
						}
					}
				}
				
				
			}
		}
		
		
		return true;
		
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
		placeBlockRel(blocks, metas, relX, y, relZ, block);
		return true;
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

    /*protected int getYWithOffset(int y)
    {
        return this.coordMode == -1 ? y : y + this.structBB.minY;
    }*/

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
}
