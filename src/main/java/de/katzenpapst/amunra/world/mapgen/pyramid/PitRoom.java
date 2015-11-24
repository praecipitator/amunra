package de.katzenpapst.amunra.world.mapgen.pyramid;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class PitRoom extends PyramidRoom {

	protected int pitSize = 7;

	@Override
	public boolean generateChunk(int chunkX, int chunkZ, Block[] arrayOfIDs, byte[] arrayOfMeta) {

		super.generateChunk(chunkX, chunkZ, arrayOfIDs, arrayOfMeta);

		int size = (pitSize-1)/2;

		for(int x = -size; x<=size; x++) {
			//for(int y=0; y<5;y++) {
				for(int z = -size; z<=size; z++) {
					placeBlockAbs(arrayOfIDs, arrayOfMeta,
							this.structBB.getCenterX()+x,
							this.floorLevel-1,
							this.structBB.getCenterZ()+z,
							chunkX, chunkZ, Blocks.air, (byte) 0);
				}
			//}
		}

		return true;
	}

}
