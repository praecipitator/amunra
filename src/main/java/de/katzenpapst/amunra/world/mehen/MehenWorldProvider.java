package de.katzenpapst.amunra.world.mehen;

import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.world.asteroidWorld.AmunRaAsteroidWorldProvider;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.planets.asteroids.world.gen.WorldChunkManagerAsteroids;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.IChunkProvider;

public class MehenWorldProvider extends AmunRaAsteroidWorldProvider {

    @Override
    public CelestialBody getCelestialBody() {
        return AmunRa.instance.asteroidBeltMehen;
    }

    @Override
    public Class<? extends IChunkProvider> getChunkProviderClass() {
        return MehenChunkProvider.class;
    }

    @Override
    public Class<? extends WorldChunkManager> getWorldChunkManagerClass() {
        return WorldChunkManagerAsteroids.class;
    }

    @Override
    public String getSaveDataID() {
        return "AsteroidDataMehen";
    }



}
