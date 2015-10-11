package de.katzenpapst.amunra.world.anubis;

import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace;
import net.minecraft.world.World;

public class AnubisBiomeDecorator extends BiomeDecoratorSpace {
	protected World mWorld = null;
	@Override
	protected void setCurrentWorld(World world) {
		mWorld = world;

	}

	@Override
	protected World getCurrentWorld() {
		return mWorld;
	}

	@Override
	protected void decorate() {
		// TODO Auto-generated method stub

	}
}