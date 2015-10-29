package de.katzenpapst.amunra.world.mapgen.village;

import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import de.katzenpapst.amunra.world.CoordHelper;

public class PyramidHouseComponent extends GridVillageComponent {

	protected int houseHeight = 5;

	@Override
	public boolean generateChunk(int chunkX, int chunkZ, Block[] blocks, byte[] metas) {

		// now, how to get the height?
		StructureBoundingBox chunkBB = CoordHelper.getChunkBB(chunkX, chunkZ);//new StructureBoundingBox((chunkX << 4), (chunkX<< 4), (chunkX+1 << 4)-1, (chunkX+1 << 4)-1);
		int fallbackGround = this.parent.getGroundLevel();
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
		int startX = 0;
		int stopX = myBB.getXSize() - 1;
		int startZ = 0;
		int stopZ = myBB.getZSize() - 1;


		int xCenter = (int)Math.ceil((stopX-startX)/2+startX);
		int zCenter = (int)Math.ceil((stopZ-startZ)/2+startZ);

		int radius = xCenter;


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

				if(startX == x || startZ == z || stopX == x || stopZ == z) {
					placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel, z, wall);
				}

				for(int y = 0; y <= radius; y++) {
					if(
							(x >= startX+y && x <= stopX-y) && (z == startZ+y || z == stopZ-y) ||
							(x == startX+y || x == stopX-y) && (z >= startZ+y && z <= stopZ-y)
					) {
						placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel+y+1, z, wall);
					}
/*
					if((x >= startX+y && x <= stopX-y) && (z >= startZ+y && z <= stopZ-y)) {
						placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel+y+1, z, wall);
					}
					if((x >= startX+y && x <= stopX-y-1) && (z >= startZ+y+1 && z <= stopZ-y-y)) {
						placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel, z, air);
					}*/
				}


			}

		}




		return true;

	}
}
