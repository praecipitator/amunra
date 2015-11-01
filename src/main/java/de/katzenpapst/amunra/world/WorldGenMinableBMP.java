package de.katzenpapst.amunra.world;

import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.core.world.gen.WorldGenMinableMeta;

public class WorldGenMinableBMP extends WorldGenMinableMeta {


	/**
	 * I just overrode this because I hated the constructor
	 *
	 * @param minableBlock		Block to place, usually ore
	 * @param numberOfBlocks	Rarity in some way, not sure yet. I think cluster size.
	 * @param fillerBlock		Block to replace with the minable Block
	 */
	public WorldGenMinableBMP(BlockMetaPair minableBlock, int numberOfBlocks, BlockMetaPair fillerBlock) {
		super(minableBlock.getBlock(), numberOfBlocks, minableBlock.getMetadata(), true, fillerBlock.getBlock(), fillerBlock.getMetadata());

	}

}
