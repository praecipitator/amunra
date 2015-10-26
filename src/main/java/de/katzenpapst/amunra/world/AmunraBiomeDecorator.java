package de.katzenpapst.amunra.world;

import net.minecraft.world.World;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace;

public class AmunraBiomeDecorator extends BiomeDecoratorSpace {

	private World mWorld;

	public AmunraBiomeDecorator() {
	}

	protected void setCurrentWorld(World world) {
		mWorld = world;

	}

	@Override
	protected World getCurrentWorld() {
		return mWorld;
	}

	@Override
	protected void decorate() {
	}

}
