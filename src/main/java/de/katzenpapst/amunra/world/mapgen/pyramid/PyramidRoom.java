package de.katzenpapst.amunra.world.mapgen.pyramid;

import de.katzenpapst.amunra.world.CoordHelper;
import de.katzenpapst.amunra.world.mapgen.BaseStructureComponent;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class PyramidRoom extends BaseStructureComponent {

	protected StructureBoundingBox entranceBB;



	public StructureBoundingBox getEntranceBB() {
		return entranceBB;
	}


	public void setEntranceBB(StructureBoundingBox entranceBB) {
		this.entranceBB = entranceBB;
	}


	@Override
	public boolean generateChunk(int chunkX, int chunkZ, Block[] arrayOfIDs, byte[] arrayOfMeta) {

		StructureBoundingBox chunkBB = CoordHelper.getChunkBB(chunkX, chunkZ);

		//StructureBoundingBox actualBB = this.intersectBoundingBoxes(chunkBB, structBB);
		/*
		if(actualBB == null) {
			return false;
		}

		actualBB.minY = this.parent.getGroundLevel();
		actualBB.maxY = actualBB.minY+6;
		*/
		StructureBoundingBox myBB = new StructureBoundingBox(structBB);
		myBB.minY = this.parent.getGroundLevel()+7;
		myBB.maxY = myBB.minY+4;
		StructureBoundingBox actualBB = this.intersectBoundingBoxes(chunkBB, myBB);

		fillBox(arrayOfIDs, arrayOfMeta, actualBB, Blocks.air, (byte) 0);

		/*
		for(int x=actualBB.minX; x<=actualBB.maxX; x++) {
			for(int y=actualBB.minY; y<=actualBB.maxY; y++) {
				for(int z=actualBB.minZ; z<=actualBB.maxZ; z++) {
					placeBlockAbs(arrayOfIDs, arrayOfMeta, x, y, z, chunkX, chunkZ, ARBlocks.blockAluCrate.getBlock(), ARBlocks.blockAluCrate.getMetadata());

					//drawArea(arrayOfIDs, arrayOfMeta, chunkBB, myBB, ARBlocks.blockAluCrate);
				}
			}
		}
		*/

		//drawArea(arrayOfIDs, arrayOfMeta, chunkBB, myBB, ARBlocks.blockAluCrate);


		return true;
	}

	public boolean generateEntrance(int chunkX, int chunkZ, Block[] arrayOfIDs, byte[] arrayOfMeta) {
		StructureBoundingBox chunkBB = CoordHelper.getChunkBB(chunkX, chunkZ);

		StructureBoundingBox actualBB = this.intersectBoundingBoxes(chunkBB, entranceBB);
		int height = 3;//entranceBB.getYSize();
		int roomGroundLevel = this.parent.getGroundLevel()+7;
		actualBB.minY = roomGroundLevel;
		actualBB.maxY = roomGroundLevel+height;

		fillBox(arrayOfIDs, arrayOfMeta, actualBB, Blocks.air, (byte) 0);

		return true;
	}
}
