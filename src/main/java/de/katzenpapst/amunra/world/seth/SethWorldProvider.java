package de.katzenpapst.amunra.world.seth;

import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.IChunkProvider;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.world.AmunraWorldChunkManager;
import de.katzenpapst.amunra.world.AmunraWorldProvider;

public class SethWorldProvider extends AmunraWorldProvider {

	public SethWorldProvider() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public double getYCoordinateToTeleport() {
		return 800;
	}

	@Override
	public double getSolarEnergyMultiplier() {
		return 0.53;
	}

	@Override
	public double getMeteorFrequency() {
		return 7.0;
	}

	@Override
	public float getSoundVolReductionAmount() {

        return 20.0F;
	}

	@Override
	public float getThermalLevelModifier() {
		return -0.1F;
	}

	@Override
	public float getWindLevel() {
		return 0;
	}

	@Override
	public CelestialBody getCelestialBody() {
		return AmunRa.instance.moonSeth;
	}

	@Override
	protected float getRelativeGravity() {
		return 0.43F;
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
		return 36581L;
	}

	@Override
	public Class<? extends IChunkProvider> getChunkProviderClass() {
		return SethChunkProvider.class;
	}

	@Override
	public Class<? extends WorldChunkManager> getWorldChunkManagerClass() {
		return AmunraWorldChunkManager.class;
	}

}
