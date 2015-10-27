package de.katzenpapst.amunra.world.anubis;

import java.util.Random;

import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.IChunkProvider;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.world.AmunraWorldChunkManager;
import de.katzenpapst.amunra.world.AmunraWorldProvider;
import de.katzenpapst.amunra.world.maahes.MaahesChunkProvider;

public class AnubisWorldProvider extends AmunraWorldProvider {

	/**
     * Determines the rate to spawn meteors in this planet. Lower means MORE
     * meteors.
     * <p/>
     * Typical value would be about 7. Return 0 for no meteors.
     *
     * @return
     */
	@Override
	public double getMeteorFrequency() {
		return 2;
	}

	@Override
	public boolean canSpaceshipTierPass(int tier) {
		return tier >= 3;
	}

	/**
     * Changes volume of sounds on this planet. You should be using higher
     * values for thin atmospheres and high values for dense atmospheres
     *
     * @return Sound reduction divisor. Value of 10 will make sounds ten times
     * more quiet. Value of 0.1 will make sounds 10 times louder. Be
     * careful with the values you choose!
     */
	@Override
	public float getSoundVolReductionAmount() {
		return 20;
	}

	/**
     * This value will affect player's thermal level, damaging them if it
     * reaches too high or too low.
     *
     * @return Positive integer for hot celestial bodies, negative for cold.
     * Zero for neutral
     */
	@Override
	public float getThermalLevelModifier() {
		return -10;
	}

	@Override
	public float getWindLevel() {
		return 0;
	}

	@Override
	public CelestialBody getCelestialBody() {
		return AmunRa.instance.planetAnubis;
	}

	@Override
	public double getSolarEnergyMultiplier() {
		return 0.25;
	}

	@Override
	public double getYCoordinateToTeleport() {
		return 800;
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
		return 32000L;
	}

	@Override
	public boolean shouldForceRespawn() {
		return false;
	}

	@Override
	public Class<? extends IChunkProvider> getChunkProviderClass() {
		return AnubisChunkProvider.class;
	}

	@Override
	public Class<? extends WorldChunkManager> getWorldChunkManagerClass() {
		return AmunraWorldChunkManager.class;
	}

	@Override
	protected float getRelativeGravity() {
		return 0.25F;
	}
	
	@Override
    public boolean isSkyColored()
    {
        return false;
    }

}
