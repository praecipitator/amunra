package de.katzenpapst.amunra.world.anubis;

import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.world.WorldGenMinableBMP;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class AnubisBiomeDecorator extends BiomeDecoratorSpace {
	protected World mWorld = null;
	private WorldGenerator cryoGemGen;
	@Override
	protected void setCurrentWorld(World world) {
		mWorld = world;

		cryoGemGen = new WorldGenMinableBMP(ARBlocks.oreCryoBasalt, 6, ARBlocks.blockBasalt);

	}

	@Override
	protected World getCurrentWorld() {
		return mWorld;
	}

	@Override
	protected void decorate() {
		this.generateOre(2, cryoGemGen, 8, 45);

	}

}