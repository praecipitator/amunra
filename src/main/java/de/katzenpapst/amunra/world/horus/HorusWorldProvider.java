package de.katzenpapst.amunra.world.horus;

import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.IChunkProvider;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.world.AmunraWorldChunkManager;
import de.katzenpapst.amunra.world.AmunraWorldProvider;


public class HorusWorldProvider extends AmunraWorldProvider {

	@Override
	public double getYCoordinateToTeleport() {

		return 800;
	}

	@Override
	public double getSolarEnergyMultiplier() {
		return 1.75;
	}

	@Override
	public double getMeteorFrequency() {
		return 1.5;
	}

	@Override
	public boolean canSpaceshipTierPass(int tier) {
		return tier >= 3;
	}

	@Override
	public float getSoundVolReductionAmount() {
		return 20;
	}

	@Override
	public float getThermalLevelModifier() {
		// asteroids has a thermal modifier of -1.5
		return 3;
	}

	@Override
	public float getWindLevel() {
		return 0;
	}

	@Override
	public CelestialBody getCelestialBody() {
		return AmunRa.instance.planetHorus;
	}

	@Override
	protected float getRelativeGravity() {
		return 1.2F;
	}

	@Override
	public Vector3 getFogColor() {
		return new Vector3(0, 0, 0);
	}

	@Override
	public Vector3 getSkyColor() {

		return new Vector3(0, 0, 0);
	}

	@Override
	public boolean canRainOrSnow() {
		return false;
	}

	@Override
	public boolean hasSunset() {
		return false;
	}

	@Override
	public long getDayLength() {
		return 52000L;
	}

	@Override
	public boolean shouldForceRespawn() {
		return false;
	}

	@Override
	public Class<? extends IChunkProvider> getChunkProviderClass() {
		return HorusChunkProvider.class;
	}

	@Override
	public Class<? extends WorldChunkManager> getWorldChunkManagerClass() {
		return AmunraWorldChunkManager.class;
	}

}
