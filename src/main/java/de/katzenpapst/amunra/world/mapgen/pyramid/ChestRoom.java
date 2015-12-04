package de.katzenpapst.amunra.world.mapgen.pyramid;

import de.katzenpapst.amunra.world.mapgen.populator.FillChest;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class ChestRoom extends PyramidRoom {

	@Override
	public boolean generateChunk(int chunkX, int chunkZ, Block[] arrayOfIDs, byte[] arrayOfMeta) {

		super.generateChunk(chunkX, chunkZ, arrayOfIDs, arrayOfMeta);

		BlockMetaPair chest = new BlockMetaPair(Blocks.chest, (byte) 0);
		BlockMetaPair floorMat = ((Pyramid) this.parent).getFloorMaterial();

		int size = 3;

		for(int x = -size; x<=size; x++) {
			for(int z = -size; z<=size; z++) {
				placeBlockAbs(arrayOfIDs, arrayOfMeta,
						this.roomBB.getCenterX()+x,
						this.floorLevel,
						this.roomBB.getCenterZ()+z,
						chunkX, chunkZ, floorMat);

				if(
					(x == -size && z == -size) ||
					(x == -size && z == size) ||
					(x == size && z == -size) ||
					(x == size && z == size))
				{
					// corners
					for(int y = this.floorLevel+1; y <= this.roomBB.maxY; y++) {
						placeBlockAbs(arrayOfIDs, arrayOfMeta,
								this.roomBB.getCenterX()+x,
								y,
								this.roomBB.getCenterZ()+z,
								chunkX, chunkZ, floorMat);
					}
				}


			}
		}

		// meh just place it in the center for the time being

		if(this.placeBlockAbs(arrayOfIDs, arrayOfMeta, roomBB.getCenterX(), floorLevel+1, roomBB.getCenterZ(),
				chunkX, chunkZ, chest)) {
			this.parent.addPopulator(new FillChest(roomBB.getCenterX(), floorLevel+1, roomBB.getCenterZ(), chest, Pyramid.LOOT_CATEGORY_BASIC));
		}

		return true;
	}



}
