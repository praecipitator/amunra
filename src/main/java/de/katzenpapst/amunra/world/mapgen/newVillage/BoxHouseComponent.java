package de.katzenpapst.amunra.world.mapgen.newVillage;

import de.katzenpapst.amunra.mob.entity.EntityRobotVillager;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.ChunkProviderSpace;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityCreature;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class BoxHouseComponent extends GridVillageComponent {
	
	protected int groundLevel = -1;
	protected int houseHeight = 5;
	

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
		int startX = myBB.minX+1;
		int stopX = myBB.maxX-1;
		int startZ = myBB.minZ+1;
		int stopZ = myBB.maxZ-1;
		int xCenter = (int)Math.ceil((stopX-startX)/2+startX);
		int zCenter = (int)Math.ceil((stopZ-startZ)/2+startZ);
		for(int x = startX; x <= stopX; x++) {
			for(int z = startZ; z <= stopZ; z++) { 
				
				int relX = abs2rel(x, chunkX);
				int relZ = abs2rel(z, chunkZ);
				if(relX < 0 || relX >= 16 || relZ < 0 || relZ >= 16) {
					continue;
				}
				
				int highestGroundBlock = getHighestSolidBlock(blocks, metas, relX, relZ);
				
				// now fill
				for(int y=highestGroundBlock-1;y<groundLevel; y++) {
					//padding
					placeBlockRel(blocks, metas, relX, y, relZ, padding);
				}
				// floor
				placeBlockRel(blocks, metas, relX, groundLevel-1, relZ, floor);
				
				// now try spawing villagers...
				if(x == xCenter && z == zCenter) {
					EntityCreature villager = new EntityRobotVillager(this.parent.getWorld());
	                villager.onSpawnWithEgg(null);// NO IDEA
	                this.parent.spawnLater(villager, x, groundLevel, z);
				}
				
				// now walls, most complex part
				for(int y=0;y<houseHeight-1;y++) {
					// wall check
					if(x == startX || x == stopX || z == startZ || z == stopZ) {
						
						if(
							this.shouldGenerateWindowHere(x, y, z, xCenter, startX, stopX, startZ, stopZ)
						) {
							
							placeBlockRel(blocks, metas, relX, groundLevel+y, relZ, Blocks.glass_pane, 0);
						} else if(z == startZ && x == xCenter && (y == 0 || y == 1)) {
							// TODO figure out how to do doors
							placeBlockRel(blocks, metas, relX, groundLevel+y, relZ, Blocks.air, 0);
						}  else {
							// just place a wall, for now
							placeBlockRel(blocks, metas, relX, groundLevel+y, relZ, mat);
						}
						// if(x == Math.fstopX-startX)
					} else { // end of wall check
						// this is interior
						placeBlockRel(blocks, metas, relX, groundLevel+y, relZ, Blocks.air, 0);	
						// maybe place torches?
						if(x == startX+1 && z == zCenter && y == 2) {
							placeBlockRel(blocks, metas, relX, groundLevel+y, relZ, GCBlocks.glowstoneTorch, 1);	
							// rotate to negative x?
							/*0: Standing on the floor
							1: Pointing east
							2: Pointing west
							3: Pointing south
							4: Pointing north*/
						} else if(x == stopX-1 && z == zCenter && y == 2) {
							placeBlockRel(blocks, metas, relX, groundLevel+y, relZ, GCBlocks.glowstoneTorch, 2);	
							// rotate to +x?
						} else if(z == startZ+1 && x == xCenter && y == 2) {
							placeBlockRel(blocks, metas, relX, groundLevel+y, relZ, GCBlocks.glowstoneTorch, 3);	
							// rotate to -z?
						} else if(z == stopZ-1 && x == xCenter && y == 2) {
							placeBlockRel(blocks, metas, relX, groundLevel+y, relZ, GCBlocks.glowstoneTorch, 4);	
							// rotate to -z?
						}
						if(y==0 && x == startX+1 && z == startZ+1) {
							// random crafting table
							placeBlockRel(blocks, metas, relX, groundLevel+y, relZ, Blocks.crafting_table, 0);
						}
					}
				}
				// finally, roof
				placeBlockRel(blocks, metas, relX, groundLevel+houseHeight-1, relZ, mat);
				
			}
		}
		
		
		return true;
		
	}
	
	private boolean shouldGenerateWindowHere(int x, int y, int z, int doorPos, int startX, int stopX, int startZ, int stopZ) {
		if(y > this.houseHeight-3 || y < 1) {
			return false;
		}
		
		if((x == startX || x == stopX) && z > startZ+1 && z < stopZ-1 && ((z-startZ) % 2 == 0)) {
			return true;
		}
		
		if((z == startZ || z == stopZ) && x > startX+1 && x < stopX-1 && ((x-startX) % 2 == 0)) {
			if(z == startZ && (x == doorPos+1 || x == doorPos-1)) {
				return false;
			}
			return true;
		}
		
		return false;
	}
}
