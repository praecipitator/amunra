package de.katzenpapst.amunra.world.mapgen.pyramid;

import java.util.Random;

import cpw.mods.fml.common.FMLLog;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.world.CoordHelper;
import de.katzenpapst.amunra.world.mapgen.BaseStructureStart;

public class Pyramid extends BaseStructureStart
{
	protected int pyramidSize = 56;
	protected BlockMetaPair wallMaterial = ARBlocks.blockAluCrate;
	protected BlockMetaPair floorMaterial = ARBlocks.blockSmoothBasalt;
	protected BlockMetaPair fillMaterial = ARBlocks.blockBasaltBrick;

	public Pyramid(World world, int chunkX, int chunkZ, Random rand) {
		super(world, chunkX, chunkZ, rand);
		int startX = CoordHelper.chunkToMinBlock(chunkX);
		int startZ = CoordHelper.chunkToMinBlock(chunkZ);
		StructureBoundingBox bb = new StructureBoundingBox(startX-56,startZ-56,startX+56,startZ+56);
		this.setStructureBoundingBox(bb);

		FMLLog.info("Generating Pyramid at "+startX+"/"+startZ);
	}


	@Override
	public boolean generateChunk(int chunkX, int chunkZ, Block[] blocks, byte[] metas) {
		super.generateChunk(chunkX, chunkZ, blocks, metas);

		// now generate the actual pyramid
		StructureBoundingBox chunkBB = CoordHelper.getChunkBB(chunkX, chunkZ);//new StructureBoundingBox((chunkX << 4), (chunkX<< 4), (chunkX+1 << 4)-1, (chunkX+1 << 4)-1);
		StructureBoundingBox myBB = this.getStructureBoundingBox();

		if(!chunkBB.intersectsWith(myBB)) {
			return false;
		}

		int fallbackGround = this.getGroundLevel();
		if(groundLevel == -1) {
			groundLevel = getAverageGroundLevel(blocks, metas, getStructureBoundingBox(), chunkBB, fallbackGround);
			if(groundLevel == -1) {
				groundLevel = fallbackGround; // but this shouldn't even happen...
			}
		}



		//BlockMetaPair glassPane = new BlockMetaPair(Blocks.glass_pane, (byte) 0);
		//BlockMetaPair air = new BlockMetaPair(Blocks.air, (byte) 0);

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
					placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, y, z, fillMaterial);
				}
				// floor
				placeBlockRel2BB(blocks, metas,chunkX, chunkZ, x, groundLevel, z, floorMaterial);

				/*if(startX == x || startZ == z || stopX == x || stopZ == z) {
					placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel, z, wallMaterial);
				}*/

				for(int y = 0; y <= radius; y++) {
					if((x >= startX+y && x <= stopX-y) && (z >= startZ+y && z <= stopZ-y)) {
						if((z == startZ+y || z == stopZ-y) || (x == startX+y || x == stopX-y)) {
							// wall
							placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel+y+1, z, wallMaterial);
						} else {
							// inner
							placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel+y+1, z, fillMaterial);
						}
					}
					// try to do the entrance
					if(y >= 5 && y <= 13) {
						if(x >= xCenter-4 && x <= xCenter+4) {

							if(z >= startZ+5 && z <= startZ+5+7) {
								// surrounding box
								if((x == xCenter-4 || x == xCenter+4) || y == 13) {
									if(z == startZ+5 || y == 13) {
										placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel+y+1, z, fillMaterial);
									}
								} else {
									if(z > startZ+5) {
										placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel+y+1, z, fillMaterial);
									}
								}
							}

							// cut in the tunnel
							if(x >= xCenter-1 && x <= xCenter+1 &&
									(y >= 6 && y <= 10) && (z >= startZ+5 && z <= startZ+5+14)) {
								placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel+y+1, z, Blocks.air, 0);
							}
						}
					}
				}




			}

		}

		return true;
	}




    protected int coords2int(int x, int y, int z) {
    	int coords = ((x << 4) + z) * 256 + y;
    	return coords;
    }


    public BlockMetaPair getWallMaterial() {
		return wallMaterial;
	}

	public void setWallMaterial(BlockMetaPair wallMaterial) {
		this.wallMaterial = wallMaterial;
	}

	public BlockMetaPair getFloorMaterial() {
		return floorMaterial;
	}

	public void setFloorMaterial(BlockMetaPair floorMaterial) {
		this.floorMaterial = floorMaterial;
	}

	public BlockMetaPair getFillMaterial() {
		return fillMaterial;
	}

	public void setFillMaterial(BlockMetaPair fillMaterial) {
		this.fillMaterial = fillMaterial;
	}
}
