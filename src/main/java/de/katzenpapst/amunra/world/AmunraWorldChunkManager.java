package de.katzenpapst.amunra.world;

import net.minecraft.world.biome.BiomeGenBase;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldChunkManagerSpace;

public class AmunraWorldChunkManager extends WorldChunkManagerSpace {

	@Override
	public BiomeGenBase getBiome() {
		return BiomeGenBase.desert;
	}

}
