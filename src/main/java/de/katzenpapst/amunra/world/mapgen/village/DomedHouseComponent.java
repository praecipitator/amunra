package de.katzenpapst.amunra.world.mapgen.village;

import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityCreature;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import de.katzenpapst.amunra.helper.CoordHelper;
import de.katzenpapst.amunra.mob.entity.EntityRobotVillager;

public class DomedHouseComponent extends GridVillageComponent {

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
		BlockMetaPair wall 		= ((GridVillageStart)this.parent).getWallMaterial();
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


		int houseRadius = xCenter-1;
		int houseRadiusSq = (int)(Math.pow(houseRadius,2));
		int wallHeight = houseHeight-houseRadius;
		for(int x = startX; x <= stopX; x++) {
			for(int z = startZ; z <= stopZ; z++) {

				int highestGroundBlock = getHighestSolidBlockInBB(blocks, metas, chunkX, chunkZ, x, z);
				if(highestGroundBlock == -1) {
					continue; // that should mean that we aren't in the right chunk
				}

				//
				//double innerRadiusSq = Math.pow(xCenter-x-1, 2)+Math.pow(zCenter-z-1, 2);
				double wallRadiusSq = Math.pow(xCenter-x, 2)+Math.pow(zCenter-z, 2);
				// this works for the outer wall
				// if( houseRadius+1 >= outerRadiusSq && houseRadius-1 <= outerRadiusSq ) {
				if( houseRadiusSq+1 >= wallRadiusSq ) {
					// now fill
					for(int y=highestGroundBlock-1;y<groundLevel; y++) {
						//padding
						placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, y, z, padding);
					}
					// floor
					placeBlockRel2BB(blocks, metas,chunkX, chunkZ, x, groundLevel-1, z, floor);

					// spawn villager
					if(x == xCenter && z == zCenter) {
						spawnVillager(x, groundLevel, z);
					}

					// now walls
					for(int y = 0; y < wallHeight;y++) {
						if(houseRadiusSq-1 <= wallRadiusSq) {
							// walls

							if(x == xCenter && z == startZ && (y==0 || y==1)) {
								//door
								placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel+y, z, air);

							} else if(
										(
											(x == startX && z == zCenter) ||
											(x == stopX && z == zCenter) ||
											//(z == startZ && x == xCenter) ||
											(z == stopZ && x == xCenter)
										)
									&& y==1
									) {
								// windows
								placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel+y, z, glassPane);
							} else {
								// wall
								placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel+y, z, wall);
							}
							//placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel+yWalls, z, wall);
						} else {
							// also torches
							/*
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
							} else {
								placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel+y, z, air);
							}*/
							placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel+y, z, air);


							/*if(y==0 && x == startX+1 && z == startZ+1) {
								// random crafting table
								placeBlockRel2BB(blocks, metas,chunkX, chunkZ, x, groundLevel+y, z, Blocks.crafting_table, 0);
							}*/
						}
					}

					// now roof
					for(int y=0;y<houseRadius;y++) {
						double roofRadiusSq = Math.pow(xCenter-x, 2)+Math.pow(zCenter-z, 2)+Math.pow(y+1, 2)-1;
						if(houseRadiusSq+1 >= roofRadiusSq && houseRadiusSq-2 <= roofRadiusSq) {
							placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel+y+wallHeight, z, wall);
						} else /*if(houseRadiusSq-3 <= roofRadiusSq)*/ {
							//placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel+y+wallHeight, z, air);


							if(x == startX+1 && z == zCenter && y == 0) {
								placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel+y+wallHeight, z, GCBlocks.glowstoneTorch, rotateTorchMetadata(1, this.coordMode));
							} else if(x == stopX-1 && z == zCenter && y == 0) {
								placeBlockRel2BB(blocks, metas,chunkX, chunkZ, x, groundLevel+y+wallHeight, z, GCBlocks.glowstoneTorch, rotateTorchMetadata(2, this.coordMode));
								//
							} else if(z == startZ+1 && x == xCenter && y == 0) {
								placeBlockRel2BB(blocks, metas,chunkX, chunkZ, x, groundLevel+y+wallHeight, z, GCBlocks.glowstoneTorch, rotateTorchMetadata(3, this.coordMode));
								// rotate to -z?
							} else if(z == stopZ-1 && x == xCenter && y == 0) {
								placeBlockRel2BB(blocks, metas,chunkX, chunkZ, x, groundLevel+y+wallHeight, z, GCBlocks.glowstoneTorch, rotateTorchMetadata(4, this.coordMode));
								// rotate to -z?
							} else {
								placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel+y+wallHeight, z, air);
							}

						}
					}

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
}
