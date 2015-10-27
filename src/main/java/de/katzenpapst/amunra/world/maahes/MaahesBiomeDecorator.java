package de.katzenpapst.amunra.world.maahes;

import net.minecraft.world.World;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace;

public class MaahesBiomeDecorator extends BiomeDecoratorSpace {
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
		// hm I think villages actually go here
	}

}
