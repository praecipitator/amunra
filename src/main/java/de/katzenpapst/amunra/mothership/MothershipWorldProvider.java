package de.katzenpapst.amunra.mothership;

import de.katzenpapst.amunra.tick.TickHandlerServer;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderOrbit;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.world.gen.ChunkProviderOrbit;
import micdoodle8.mods.galacticraft.core.world.gen.WorldChunkManagerOrbit;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.IChunkProvider;

public class MothershipWorldProvider extends WorldProviderOrbit {   // TODO: DO NOT implement IOrbitDimension

    protected Mothership mothershipObj;
    // TODO override pretty much everything. Or maybe just don't extend WorldProviderOrbit at all
    public MothershipWorldProvider() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void setDimension(int var1)
    {
        this.mothershipObj = TickHandlerServer.mothershipData.getByDimensionId(var1);
        this.spaceStationDimensionID = var1;
        super.setDimension(var1);
    }

    @Override
    public CelestialBody getCelestialBody()
    {
        return mothershipObj;
    }

    @Override
    public long getDayLength()
    {
        // hmm. TODO think of something
        CelestialBody parent = this.mothershipObj.getParent();
        //if(parent != null) {
        //    return parent.get
        //}
        return 24000L;
    }

    @Override
    public Class<? extends IChunkProvider> getChunkProviderClass()
    {
        return MothershipChunkProvider.class;
    }

    @Override
    public Class<? extends WorldChunkManager> getWorldChunkManagerClass()
    {
        return MothershipWorldChunkManager.class;
    }

    @Override
    public boolean isDaytime()
    {
        // TODO investigate if this can be used to fix daylight when Amun is up
        final float a = this.worldObj.getCelestialAngle(0F);
        //TODO: adjust this according to size of planet below
        return a < 0.42F || a > 0.58F;
    }

    @Override
    public String getDimensionName()
    {
        return this.mothershipObj.getLocalizedName();
    }

    @Override
    public String getPlanetToOrbit()
    {
        // TODO fix
        // This is the NAME of the DIMENSION where to fall to
        return "Overworld";
    }

    @Override
    public String getSaveFolder()
    {
        return "DIM_MOTHERSHIP" + this.spaceStationDimensionID;
    }

    @Override
    public double getSolarEnergyMultiplier()
    {
        // this is going to be complicated, I think
        return ConfigManagerCore.spaceStationEnergyScalar;
    }

    @Override
    public float getThermalLevelModifier()
    {
        // should be definitely depending on the distance to current sun
        return 0;
    }
}
