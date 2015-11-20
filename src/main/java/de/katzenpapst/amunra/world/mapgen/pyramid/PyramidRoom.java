package de.katzenpapst.amunra.world.mapgen.pyramid;

import de.katzenpapst.amunra.world.CoordHelper;
import de.katzenpapst.amunra.world.mapgen.BaseStructureComponent;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class PyramidRoom extends BaseStructureComponent {

	protected StructureBoundingBox entranceBB;
	protected StructureBoundingBox roomBB;


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

/*
	public void setEntranceBB(StructureBoundingBox entranceBB) {
		this.entranceBB = entranceBB;
	}*/


	@Override
	public boolean generateChunk(int chunkX, int chunkZ, Block[] arrayOfIDs, byte[] arrayOfMeta) {

		StructureBoundingBox chunkBB = CoordHelper.getChunkBB(chunkX, chunkZ);

		//StructureBoundingBox myBB = new StructureBoundingBox(roomBB);
		int groundLevel = this.parent.getGroundLevel()+6;
		roomBB.minY = groundLevel+1;
		roomBB.maxY = roomBB.minY+4;
		StructureBoundingBox actualRoomBB = this.intersectBoundingBoxes(chunkBB, roomBB);
		if(actualRoomBB != null) {
			fillBox(arrayOfIDs, arrayOfMeta, actualRoomBB, Blocks.air, (byte) 0);
		}


		entranceBB.minY = roomBB.minY;
		entranceBB.maxY = entranceBB.minY+3;

		StructureBoundingBox entrBoxIntersect = this.intersectBoundingBoxes(entranceBB, chunkBB);

		if(entrBoxIntersect  != null) {
			fillBox(arrayOfIDs, arrayOfMeta, entrBoxIntersect, Blocks.air, (byte) 0);
		}

		return true;
	}

	/*public boolean generateEntrance(int chunkX, int chunkZ, Block[] arrayOfIDs, byte[] arrayOfMeta) {
		StructureBoundingBox chunkBB = CoordHelper.getChunkBB(chunkX, chunkZ);

		StructureBoundingBox actualBB = this.intersectBoundingBoxes(chunkBB, entranceBB);
		int height = 3;//entranceBB.getYSize();
		int roomGroundLevel = this.parent.getGroundLevel()+7;
		actualBB.minY = roomGroundLevel;
		actualBB.maxY = roomGroundLevel+height;

		fillBox(arrayOfIDs, arrayOfMeta, actualBB, Blocks.air, (byte) 0);

		return true;
	}*/
}
