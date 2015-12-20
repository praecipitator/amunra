package de.katzenpapst.amunra.world.mapgen.village;

import de.katzenpapst.amunra.mob.entity.EntityRobotVillager;
import de.katzenpapst.amunra.world.CoordHelper;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityCreature;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class BoxHouseComponent extends GridVillageComponent {

	protected int houseHeight = 5;


	@Override
	public boolean generateChunk(int chunkX, int chunkZ, Block[] blocks, byte[] metas) {

		// now, how to get the height?
		StructureBoundingBox chunkBB = CoordHelper.getChunkBB(chunkX, chunkZ);//new StructureBoundingBox((chunkX << 4), (chunkX<< 4), (chunkX+1 << 4)-1, (chunkX+1 << 4)-1);
		int fallbackGround = this.parent.getWorldGroundLevel();
		if(groundLevel == -1) {
			groundLevel = getAverageGroundLevel(blocks, metas, getStructureBoundingBox(), chunkBB, fallbackGround);
			if(groundLevel == -1) {
				groundLevel = fallbackGround; // but this shouldn't even happen...
			}
		}

		StructureBoundingBox myBB = this.getStructureBoundingBox();
		BlockMetaPair mat 		= ((GridVillageStart)this.parent).getWallMaterial();
		BlockMetaPair floor 	= ((GridVillageStart)this.parent).getFloorMaterial();
		BlockMetaPair padding 	= ((GridVillageStart)this.parent).getFillMaterial();
		BlockMetaPair path 	    = ((GridVillageStart)this.parent).getPathMaterial();
		BlockMetaPair glassPane = new BlockMetaPair(Blocks.glass_pane, (byte) 0);
		BlockMetaPair air = new BlockMetaPair(Blocks.air, (byte) 0);

		// draw floor first
		int startX = 1;
		int stopX = myBB.getXSize() - 2;
		int startZ = 1;
		int stopZ = myBB.getZSize() - 2;



		int xCenter = (int)Math.ceil((stopX-startX)/2+startX);
		int zCenter = (int)Math.ceil((stopZ-startZ)/2+startZ);
		for(int x = startX; x <= stopX; x++) {
			for(int z = startZ; z <= stopZ; z++) {

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

				// now try spawing villagers...
				if(x == xCenter && z == zCenter) {
					spawnVillager(x, groundLevel, z);
					/*
					EntityCreature villager = new EntityRobotVillager(this.parent.getWorld());
	                villager.onSpawnWithEgg(null);// NO IDEA
	                int xOffset = getXWithOffset(x, z);
					//y = getYWithOffset(y);
					int zOffset = getZWithOffset(x, z);
	                this.parent.spawnLater(villager, xOffset, groundLevel, zOffset);*/
				}

				// now walls, most complex part
				for(int y=0;y<houseHeight-1;y++) {
					// wall check
					if(x == startX || x == stopX || z == startZ || z == stopZ) {

						if(
								// this should just continue working...
							this.shouldGenerateWindowHere(x, y, z, xCenter, startX, stopX, startZ, stopZ)
						) {

							placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel+y, z, glassPane);
						} else if(z == startZ && x == xCenter && (y == 0 || y == 1)) {
							// TODO figure out how to do doors
							placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel+y, z, air);
						}  else {
							// just place a wall, for now
							placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel+y, z, mat);
						}
						// if(x == Math.fstopX-startX)
					} else { // end of wall check
						// this is interior
						placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel+y, z, air);



						// maybe place torches?
						if(x == startX+1 && z == zCenter && y == 2) {
							placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel+y, z, GCBlocks.glowstoneTorch, rotateTorchMetadata(1, this.coordMode));
						} else if(x == stopX-1 && z == zCenter && y == 2) {
							placeBlockRel2BB(blocks, metas,chunkX, chunkZ, x, groundLevel+y, z, GCBlocks.glowstoneTorch, rotateTorchMetadata(2, this.coordMode));
							//
						} else if(z == startZ+1 && x == xCenter && y == 2) {
							placeBlockRel2BB(blocks, metas,chunkX, chunkZ, x, groundLevel+y, z, GCBlocks.glowstoneTorch, rotateTorchMetadata(3, this.coordMode));
							// rotate to -z?
						} else if(z == stopZ-1 && x == xCenter && y == 2) {
							placeBlockRel2BB(blocks, metas,chunkX, chunkZ, x, groundLevel+y, z, GCBlocks.glowstoneTorch, rotateTorchMetadata(4, this.coordMode));
							// rotate to -z?
						}
						/*if(y==0 && x == startX+1 && z == startZ+1) {
							// random crafting table
							placeBlockRel2BB(blocks, metas,chunkX, chunkZ, x, groundLevel+y, z, Blocks.crafting_table, 0);
						}*/
					}
				}
				// finally, roof
				placeBlockRel2BB(blocks, metas,chunkX, chunkZ, x, groundLevel+houseHeight-1, z, mat);



			}
		}
		int highestGroundBlock = getHighestSolidBlockInBB(blocks, metas, chunkX, chunkZ, xCenter, startZ-1);
		// stuff before the door
		if(highestGroundBlock != -1) {
			//groundLevel and groundLevel +1 should be free, and potentially place
			//a block at groundLevel-1
			if(highestGroundBlock >= groundLevel) {
				placeBlockRel2BB(blocks, metas, chunkX, chunkZ, xCenter, groundLevel, startZ-1, air);
				placeBlockRel2BB(blocks, metas, chunkX, chunkZ, xCenter, groundLevel+1, startZ-1, air);
			}
			// place the other stuff anyway...
			placeBlockRel2BB(blocks, metas, chunkX, chunkZ, xCenter, groundLevel-1, startZ-1, path);
			placeBlockRel2BB(blocks, metas, chunkX, chunkZ, xCenter, groundLevel-2, startZ-1, padding);
			//int highestBlock = getHighestSolidBlockInBB(blocks, metas, chunkX, chunkZ, x, z);

		}


		return true;

	}

	protected void spawnVillager(int x, int y, int z) {
		EntityCreature villager = new EntityRobotVillager(this.parent.getWorld());
        villager.onSpawnWithEgg(null);// NO IDEA
        int xOffset = getXWithOffset(x, z);
		//y = getYWithOffset(y);
		int zOffset = getZWithOffset(x, z);
        this.parent.spawnLater(villager, xOffset, y, zOffset);
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
