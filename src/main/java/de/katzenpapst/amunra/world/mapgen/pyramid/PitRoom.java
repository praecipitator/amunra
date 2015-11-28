package de.katzenpapst.amunra.world.mapgen.pyramid;

import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class PitRoom extends PyramidRoom {

	protected int pitSize = 7;

	@Override
	public boolean generateChunk(int chunkX, int chunkZ, Block[] arrayOfIDs, byte[] arrayOfMeta) {

		super.generateChunk(chunkX, chunkZ, arrayOfIDs, arrayOfMeta);

		int size = (pitSize-1)/2;

		BlockMetaPair floorMat = ((Pyramid) this.parent).getFloorMaterial();



		for(int x = -size; x<=size; x++) {
			for(int z = -size; z<=size; z++) {
				placeBlockAbs(arrayOfIDs, arrayOfMeta,
						this.roomBB.getCenterX()+x,
						this.floorLevel-1,
						this.roomBB.getCenterZ()+z,
						chunkX, chunkZ, Blocks.air, (byte) 0);

				if(x == -size || x == size || z == -size || z == size) {

					placeBlockAbs(arrayOfIDs, arrayOfMeta,
							this.roomBB.getCenterX()+x,
							this.floorLevel-2,
							this.roomBB.getCenterZ()+z,
							chunkX, chunkZ, floorMat.getBlock(), floorMat.getMetadata());

				}

				if(x > -size && x < size) {
					if(z > -size && z < size) {
						placeBlockAbs(arrayOfIDs, arrayOfMeta,
								this.roomBB.getCenterX()+x,
								this.floorLevel-2,
								this.roomBB.getCenterZ()+z,
								chunkX, chunkZ, Blocks.lava, (byte) 0);


					}
				}
			}
		}

		return true;
	}

}
