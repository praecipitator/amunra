package de.katzenpapst.amunra.world.mapgen.pyramid;

import de.katzenpapst.amunra.helper.CoordHelper;
import de.katzenpapst.amunra.world.mapgen.BaseStructureComponent;
import de.katzenpapst.amunra.world.mapgen.populator.TouchBlock;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class PyramidRoom extends BaseStructureComponent {

	protected StructureBoundingBox entranceBB;
	protected StructureBoundingBox roomBB;

	protected boolean placeGlowstoneInEdges = true;

	private boolean roomHeightFixed = false;

	protected int floorLevel;


	public void setBoundingBoxes(StructureBoundingBox entranceBB, StructureBoundingBox roomBB) {
		this.entranceBB = entranceBB;
		this.roomBB 	= roomBB;

		StructureBoundingBox totalBox = new StructureBoundingBox(roomBB);
		totalBox.expandTo(entranceBB);
		this.setStructureBoundingBox(totalBox);
	}

	public StructureBoundingBox getEntranceBB() {
		return entranceBB;
	}


	@Override
	public boolean generateChunk(int chunkX, int chunkZ, Block[] arrayOfIDs, byte[] arrayOfMeta) {

		StructureBoundingBox chunkBB = CoordHelper.getChunkBB(chunkX, chunkZ);

		BlockMetaPair floorMat = ((Pyramid) this.parent).getFloorMaterial();

		//StructureBoundingBox myBB = new StructureBoundingBox(roomBB);
		//int groundLevel = this.parent.getGroundLevel()+6;
		floorLevel = this.parent.getGroundLevel()+7;
		if(!roomHeightFixed) {
			roomBB.minY += floorLevel;
			roomBB.maxY += floorLevel;
			roomHeightFixed = true;
		}
		StructureBoundingBox actualRoomBB = this.intersectBoundingBoxes(chunkBB, roomBB);
		if(actualRoomBB != null) {
			//fillBox(arrayOfIDs, arrayOfMeta, actualRoomBB, Blocks.air, (byte) 0);
			for(int x=actualRoomBB.minX; x<=actualRoomBB.maxX; x++) {
				for(int y=actualRoomBB.minY-1; y<=actualRoomBB.maxY; y++) {
					for(int z=actualRoomBB.minZ; z<=actualRoomBB.maxZ; z++) {
						if(y >= actualRoomBB.minY) {

							placeBlockAbs(arrayOfIDs, arrayOfMeta, x, y, z, chunkX, chunkZ, Blocks.air, (byte) 0);

						} else {
							placeBlockAbs(arrayOfIDs, arrayOfMeta, x, y, z, chunkX, chunkZ, floorMat.getBlock(), floorMat.getMetadata());
						}
					}
				}
			}
		}


		entranceBB.minY = roomBB.minY;
		entranceBB.maxY = entranceBB.minY+3;

		makeEntrance(arrayOfIDs, arrayOfMeta, chunkBB, chunkX, chunkZ, floorMat);

		if(this.placeGlowstoneInEdges) {
			this.drawCornerColumns(actualRoomBB.minY, actualRoomBB.maxY, chunkX, chunkZ, arrayOfIDs, arrayOfMeta);
		}

		return true;
	}

	protected void drawCornerColumns(int yMin, int yMax, int chunkX, int chunkZ, Block[] arrayOfIDs, byte[] arrayOfMeta) {

		for (int y=yMin; y<=yMax; y++) {
			if(placeBlockAbs(arrayOfIDs, arrayOfMeta, roomBB.minX, y, roomBB.minZ, chunkX, chunkZ, Blocks.glowstone, (byte) 0)) {
				if(y == yMin) {
					// trigger the populator
					this.parent.addPopulator(new TouchBlock(roomBB.minX, y, roomBB.minZ));
				}
			}

			if(placeBlockAbs(arrayOfIDs, arrayOfMeta, roomBB.maxX, y, roomBB.minZ, chunkX, chunkZ, Blocks.glowstone, (byte) 0)) {
				if(y == yMin) {
					// trigger the populator
					this.parent.addPopulator(new TouchBlock(roomBB.maxX, y, roomBB.minZ));
				}
			}

			if(placeBlockAbs(arrayOfIDs, arrayOfMeta, roomBB.minX, y, roomBB.maxZ, chunkX, chunkZ, Blocks.glowstone, (byte) 0)) {
				if(y == yMin) {
					// trigger the populator
					this.parent.addPopulator(new TouchBlock(roomBB.minX, y, roomBB.maxZ));
				}
			}

			if(placeBlockAbs(arrayOfIDs, arrayOfMeta, roomBB.maxX, y, roomBB.maxZ, chunkX, chunkZ, Blocks.glowstone, (byte) 0)) {
				if(y == yMin) {
					// trigger the populator
					this.parent.addPopulator(new TouchBlock(roomBB.maxX, y, roomBB.maxZ));
				}
			}
		}

	}


	protected void makeEntrance(Block[] arrayOfIDs, byte[] arrayOfMeta, StructureBoundingBox chunkBB,  int chunkX, int chunkZ, BlockMetaPair floorMat) {
		StructureBoundingBox entrBoxIntersect = this.intersectBoundingBoxes(entranceBB, chunkBB);

		if(entrBoxIntersect  != null) {
			//fillBox(arrayOfIDs, arrayOfMeta, entrBoxIntersect, Blocks.air, (byte) 0);
			for(int x=entrBoxIntersect.minX; x<=entrBoxIntersect.maxX; x++) {
				for(int y=entrBoxIntersect.minY-1; y<=entrBoxIntersect.maxY; y++) {
					for(int z=entrBoxIntersect.minZ; z<=entrBoxIntersect.maxZ; z++) {
						if(y >= entrBoxIntersect.minY) {
							placeBlockAbs(arrayOfIDs, arrayOfMeta, x, y, z, chunkX, chunkZ, Blocks.air, (byte) 0);
						} else {
							placeBlockAbs(arrayOfIDs, arrayOfMeta, x, y, z, chunkX, chunkZ, floorMat.getBlock(), floorMat.getMetadata());
						}
					}
				}
			}
		}
	}


}
