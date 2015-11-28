package de.katzenpapst.amunra.world.mapgen.pyramid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import cpw.mods.fml.common.FMLLog;
import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.world.CoordHelper;
import de.katzenpapst.amunra.world.mapgen.BaseStructureStart;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class Pyramid extends BaseStructureStart
{
	protected int pyramidSize = 56;
	protected BlockMetaPair wallMaterial = ARBlocks.blockAluCrate;
	protected BlockMetaPair floorMaterial = ARBlocks.blockSmoothBasalt;
	protected BlockMetaPair fillMaterial = ARBlocks.blockBasaltBrick;

	// how far in the inner ring is
	private int innerRingOffset = 32;
	private int tunnelWidth = 3;
	private int tunnelHeight = 4;

	private int mainRoomOffset = 3;

	private int sideRoomWidth = 13;
	private int innerRoomOffset = 13;

	private int smallRoomWidth = 17;

	private PyramidRoom[] roomList = new PyramidRoom[12];

	private PyramidRoom centralRoom = null;





	public Pyramid(World world, int chunkX, int chunkZ, Random rand) {
		super(world, chunkX, chunkZ, rand);
		int startX = CoordHelper.chunkToMinBlock(chunkX);
		int startZ = CoordHelper.chunkToMinBlock(chunkZ);
		StructureBoundingBox bb = new StructureBoundingBox(startX-56,startZ-56,startX+56-1,startZ+56-1);
		this.setStructureBoundingBox(bb);
		initRooms();

		FMLLog.info("Generating Pyramid at "+startX+"/"+startZ);
	}

	protected void initRooms() {
		for(int i=0;i<12;i++) {
			PyramidRoom room = new PyramidRoom();
			room.setParent(this);
			StructureBoundingBox roomBB = this.getSmallRoomBB(i+1);
			StructureBoundingBox entranceBB = this.getRoomEntranceBox(i+1, roomBB);
			room.setBoundingBoxes(entranceBB, roomBB);
			//room.setStructureBoundingBox(roomBB);
			//room.setEntranceBB(entranceBB);
			roomList[i] = room;


		}

		int innerRoomTotalOffset = innerRingOffset+tunnelWidth+mainRoomOffset;

		StructureBoundingBox innerRoomBB = new StructureBoundingBox(
				this.structBB.minX+innerRoomTotalOffset,
				this.structBB.minZ+innerRoomTotalOffset,
				this.structBB.maxX-innerRoomTotalOffset,
				this.structBB.maxZ-innerRoomTotalOffset
		);



		/*x >= xCenter-1 && x <= xCenter+1 &&
		(y >= 5 && y <= 6+tunnelHeight) &&
		(z <= stopZ-innerRingOffset-tunnelWidth && z >= stopZ-innerRoomTotalOffset)*/
		StructureBoundingBox mainEntranceBB = new StructureBoundingBox();

		mainEntranceBB.minX = innerRoomBB.getCenterX()-1;
		mainEntranceBB.maxX = innerRoomBB.getCenterX()+1;
		mainEntranceBB.minZ = innerRoomBB.maxZ+1;
		mainEntranceBB.maxZ = innerRoomBB.maxZ+4;

		centralRoom = new PyramidRoom();
		centralRoom.setBoundingBoxes(innerRoomBB, mainEntranceBB);
		centralRoom.setParent(this);
	}

	public void setSmallRooms(ArrayList<PyramidRoom> roomList) {
		if(roomList.size() < 12) {
			while(roomList.size() < 12) {
				PyramidRoom filler = new PyramidRoom();
				roomList.add(filler);
			}
		} else {
			while(roomList.size() > 12) {
				roomList.remove(roomList.size()-1);
			}
		}
		Collections.shuffle(roomList, this.rand);

		Object[] tempList = roomList.toArray();



		for(int i=0;i<12;i++) {
			PyramidRoom room = (PyramidRoom) tempList[i];
			room.setParent(this);
			StructureBoundingBox roomBB = this.getSmallRoomBB(i+1);
			StructureBoundingBox entranceBB = this.getRoomEntranceBox(i+1, roomBB);
			room.setBoundingBoxes(entranceBB, roomBB);
			this.roomList[i] = room;
		}
	}

	public void setMainRoom(PyramidRoom room) {
		int innerRoomTotalOffset = innerRingOffset+tunnelWidth+mainRoomOffset;

		StructureBoundingBox innerRoomBB = new StructureBoundingBox(
				this.structBB.minX+innerRoomTotalOffset,
				this.structBB.minZ+innerRoomTotalOffset,
				this.structBB.maxX-innerRoomTotalOffset,
				this.structBB.maxZ-innerRoomTotalOffset
		);



		/*x >= xCenter-1 && x <= xCenter+1 &&
		(y >= 5 && y <= 6+tunnelHeight) &&
		(z <= stopZ-innerRingOffset-tunnelWidth && z >= stopZ-innerRoomTotalOffset)*/
		StructureBoundingBox mainEntranceBB = new StructureBoundingBox();

		mainEntranceBB.minX = innerRoomBB.getCenterX()-1;
		mainEntranceBB.maxX = innerRoomBB.getCenterX()+1;
		mainEntranceBB.minZ = innerRoomBB.maxZ+1;
		mainEntranceBB.maxZ = innerRoomBB.maxZ+4;

		centralRoom = room;
		centralRoom.setBoundingBoxes(innerRoomBB, mainEntranceBB);
		centralRoom.setParent(this);
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

		int fallbackGround = this.getWorldGroundLevel();
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
		int stopX = myBB.getXSize();
		int startZ = 0;
		int stopZ = myBB.getZSize();





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

				for(int y = 0; y <= radius; y++) {

					if(y >= 10) continue; // FOR TESTING

					if((x >= startX+y && x <= stopX-y) && (z >= startZ+y && z <= stopZ-y)) {
						if((z == startZ+y || z == stopZ-y) || (x == startX+y || x == stopX-y)) {
							// wall
							placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel+y+1, z, wallMaterial);
						} else {
							// inner
							placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel+y+1, z, fillMaterial);
						}
					}
					if(y >= 5 && y <= 13) {
						// try to do the entrance
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
									z >= startZ+6 &&
								(y >= 5 && y <= 6+tunnelHeight) && (z >= startZ+5 && z <= startZ+5+innerRingOffset-tunnelWidth)

								) {
								if(y == 5) {
									placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel+y+1, z, floorMaterial);
								} else  {
									placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel+y+1, z, Blocks.air, 0);
								}
							}
						}


						// inner ring
						// check if we are fully within the range of the inner tunnel first and in the right height
						if(
							(y >= 5 && y <= 6+tunnelHeight) &&
							(x >= startX+innerRingOffset && x <= stopX-innerRingOffset) &&
							(z >= startZ+innerRingOffset && z <= stopZ-innerRingOffset)
						) {

							boolean xMinEdge = (x >= startX+innerRingOffset && x < startX+innerRingOffset+tunnelWidth);
							boolean xMaxEdge = (x <= stopX-innerRingOffset && x > stopX-innerRingOffset-tunnelWidth);
							boolean zMinEdge = (z >= startZ+innerRingOffset && z < startZ+innerRingOffset+tunnelWidth);
							boolean zMaxEdge = (z <= stopZ-innerRingOffset && z > stopZ-innerRingOffset-tunnelWidth);
							if(xMinEdge || xMaxEdge || zMinEdge || zMaxEdge) {
								// inner ring tunnel
								if(y == 5) {
									placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel+y+1, z, floorMaterial);
								} else {
									placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel+y+1, z, Blocks.air, 0);
								}
							}
						}


						/*
						int innerRoomTotalOffset = innerRingOffset+tunnelWidth+mainRoomOffset;
						// entrance to the innermost room
						// cut in the tunnel
						if(
							x >= xCenter-1 && x <= xCenter+1 &&
							(y >= 5 && y <= 6+tunnelHeight) &&
							(z <= stopZ-innerRingOffset-tunnelWidth && z >= stopZ-innerRoomTotalOffset)
						)
						{
							if(y == 5) {
								placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel+y+1, z, floorMaterial);
							} else  {
								placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel+y+1, z, Blocks.air, 0);
							}
						}



						// innermost room
						if(
							y >= 5 && y <= 12 &&
							(x > startX+innerRoomTotalOffset && x < stopX-innerRoomTotalOffset) &&
							(z > startZ+innerRoomTotalOffset && z < stopZ-innerRoomTotalOffset)
						) {
							if(y == 5) {
								placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel+y+1, z, floorMaterial);
							} else  {
								placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel+y+1, z, Blocks.air, 0);
							}
						}
						*/
					}
				}
			}
		}

		generateSmallRooms(chunkBB, blocks, metas);

		return true;
	}

	protected StructureBoundingBox getRoomEntranceBox(int position, StructureBoundingBox roomBox) {
		int direction = 0; // 0 = up, 1 = right, 2 = down, 3 = left
		switch(position) {
		case 1:
			direction = Math.random() > 0.5 ? 0 : 1;
			break;
		case 2:
		case 3:
			direction = 0;
			break;
		case 4:
			direction = Math.random() > 0.5 ? 0 : 3;
			break;
		case 5:
		case 6:
			direction = 3;
			break;
		case 7:
			direction = Math.random() > 0.5 ? 3 : 2;
			break;
		case 8:
		case 9:
			direction = 2;
			break;
		case 10:
			direction = Math.random() > 0.5 ? 1 : 2;
			break;
		case 11:
		case 12:
			direction = 1;
			break;
		}


		StructureBoundingBox doorBB = new StructureBoundingBox ();
		doorBB.minY = 0;
		doorBB.maxY = 255;
		boolean isOdd = smallRoomWidth % 2 == 1;



		switch(direction) {
		case 0: //up aka +z
			doorBB.minZ = roomBox.maxZ+1;
			doorBB.maxZ = doorBB.minZ+1;
			// hmm this is for odd
			doorBB.minX = roomBox.getCenterX()-1;
			doorBB.maxX = roomBox.getCenterX()+1;
			break;
		case 1: // right aka +x
			doorBB.minX = roomBox.maxX+1;
			doorBB.maxX = doorBB.minX+1;

			doorBB.minZ = roomBox.getCenterZ()-1;
			doorBB.maxZ = roomBox.getCenterZ()+1;
			break;
		case 2: //down aka -z
			doorBB.maxZ = roomBox.minZ-1;
			doorBB.minZ = doorBB.maxZ-1;

			doorBB.minX = roomBox.getCenterX()-1;
			doorBB.maxX = roomBox.getCenterX()+1;
			break;
		case 3:	// left aka -x
			doorBB.maxX = roomBox.minX-1;
			doorBB.minX = doorBB.maxX-1;

			doorBB.minZ = roomBox.getCenterZ()-1;
			doorBB.maxZ = roomBox.getCenterZ()+1;
		}

		return doorBB;


	}

	protected StructureBoundingBox getSmallRoomBB(int position) {

		int offsetBetweenRooms = 3;
		StructureBoundingBox myBB = this.getStructureBoundingBox();
		// now doing it like this:
		/*
		 * Z
		 * ^
		 * |
		 * +-----------+ +-----------+
		 * | +--+ +--+ | | +--+ +--+ |
		 * | |10| |9 | | | |8 | |7 | |
		 * | +--+ +--+ | | +--+ +--+ |
		 * | +--+ +----+ +----+ +--+ |
		 * | |11| |  +-----+  | |6 | |
		 * | +--+ |  |     |  | +--+ |
		 * | +--+ |  |     |  | +--+ |
		 * | |12| |  +-+ +-+  | |5 | |
		 * | +--+ +-----------+ +--+ |
		 * | +--+ +--+     +--+ +--+ |
		 * | |1 | |2 |     |3 | |4 | |
		 * | +--+ +--+     +--+ +--+ |
		 * +-------------------------+----> X
		 * */
		StructureBoundingBox bb = new StructureBoundingBox();
		bb.minY = 0;
		bb.maxY = 255;
		int tempRoomWidth = this.smallRoomWidth-1;
		switch(position) {
		case 1:
			bb.minX = myBB.minX+this.innerRoomOffset;
			bb.maxX = bb.minX+tempRoomWidth;
			bb.minZ = myBB.minZ+this.innerRoomOffset;
			bb.maxZ = bb.minZ+tempRoomWidth;
			break;
		case 2:
			bb.minX = myBB.minX+this.innerRoomOffset+offsetBetweenRooms+tempRoomWidth;
			bb.minZ = myBB.minZ+this.innerRoomOffset;
			bb.maxX = bb.minX+tempRoomWidth;
			bb.maxZ = bb.minZ+tempRoomWidth;
			break;
		case 3:
			bb.maxX = myBB.maxX-this.innerRoomOffset-offsetBetweenRooms-tempRoomWidth+1;
			bb.minX = bb.maxX-tempRoomWidth;
			bb.minZ = myBB.minZ+this.innerRoomOffset;
			bb.maxZ = bb.minZ+tempRoomWidth;
			break;
		case 4:
			bb.maxX = myBB.maxX-this.innerRoomOffset+1;
			bb.minX = bb.maxX-tempRoomWidth;
			bb.minZ = myBB.minZ+this.innerRoomOffset;
			bb.maxZ = bb.minZ+tempRoomWidth;
			break;
		case 5:
			bb.minZ = myBB.minZ+this.innerRoomOffset+offsetBetweenRooms+tempRoomWidth;
			bb.maxZ = bb.minZ+tempRoomWidth;
			bb.maxX = myBB.maxX-this.innerRoomOffset+1;
			bb.minX = bb.maxX-tempRoomWidth;
			break;
		case 6:
			bb.maxZ = myBB.maxZ-this.innerRoomOffset-(offsetBetweenRooms+tempRoomWidth)+1;
			bb.minZ = bb.maxZ-tempRoomWidth;
			bb.maxX = myBB.maxX-this.innerRoomOffset+1;
			bb.minX = bb.maxX-tempRoomWidth;
			break;
		case 7:
			bb.maxZ = myBB.maxZ-this.innerRoomOffset+1;
			bb.minZ = bb.maxZ-tempRoomWidth;
			bb.maxX = myBB.maxX-this.innerRoomOffset+1;
			bb.minX = bb.maxX-tempRoomWidth;
			break;
		case 8:
			bb.maxZ = myBB.maxZ-this.innerRoomOffset+1;
			bb.minZ = bb.maxZ-tempRoomWidth;
			bb.maxX = myBB.maxX-this.innerRoomOffset-(offsetBetweenRooms+tempRoomWidth)+1;
			bb.minX = bb.maxX-tempRoomWidth;
			break;
		case 9:
			bb.maxZ = myBB.maxZ-this.innerRoomOffset+1;
			bb.minZ = bb.maxZ-tempRoomWidth;
			bb.minX = myBB.minX+this.innerRoomOffset+offsetBetweenRooms+tempRoomWidth;
			bb.maxX = bb.minX+tempRoomWidth;
			break;
		case 10:
			bb.minX = myBB.minX+this.innerRoomOffset;
			bb.maxX = bb.minX+tempRoomWidth;
			bb.maxZ = myBB.maxZ-this.innerRoomOffset+1;
			bb.minZ = bb.maxZ-tempRoomWidth;
			break;
		case 11:
			bb.minX = myBB.minX+this.innerRoomOffset;
			bb.maxX = bb.minX+tempRoomWidth;
			bb.maxZ = myBB.maxZ-this.innerRoomOffset-(offsetBetweenRooms+tempRoomWidth)+1;
			bb.minZ = bb.maxZ-tempRoomWidth;
			break;
		case 12:
			bb.minX = myBB.minX+this.innerRoomOffset;
			bb.maxX = bb.minX+tempRoomWidth;
			bb.minZ = myBB.minZ+this.innerRoomOffset+offsetBetweenRooms+tempRoomWidth;
			bb.maxZ = bb.minZ+tempRoomWidth;
			break;
		default:
			// bad
			throw new IllegalArgumentException("Pyramid room position "+position+" is invalid");

		}

		return bb;

	}


	protected void generateSmallRooms(StructureBoundingBox chunkBB, Block[] blocks, byte[] metas) {

		int chunkX = CoordHelper.blockToChunk(chunkBB.minX);
		int chunkZ = CoordHelper.blockToChunk(chunkBB.minZ);

		for(PyramidRoom r: roomList) {
			if(r.getStructureBoundingBox().intersectsWith(chunkBB)) {
				r.generateChunk(chunkX, chunkZ, blocks, metas);
			}
		}


		if(centralRoom.getStructureBoundingBox().intersectsWith(chunkBB)) {
			centralRoom.generateChunk(chunkX, chunkZ, blocks, metas);
		}

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
