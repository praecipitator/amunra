package de.katzenpapst.amunra.world.asteroidWorld;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.astronomy.AngleDistance;
import de.katzenpapst.amunra.helper.AstronomyHelper;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.Moon;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.core.util.GCLog;
import micdoodle8.mods.galacticraft.planets.asteroids.dimension.WorldProviderAsteroids;
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntityAstroMiner;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.IChunkProvider;

abstract public class AmunRaAsteroidWorldProvider extends WorldProviderAsteroids {



  //Used to list asteroid centres to external code that needs to know them
    protected HashSet<AsteroidData> asteroids = new HashSet();
    protected boolean dataNotLoaded = true;
    protected AsteroidSaveData datafile;
    protected double solarMultiplier = -1D;

    //  @Override
//  public void registerWorldChunkManager()
//  {
//      this.worldChunkMgr = new WorldChunkManagerAsteroids(this.worldObj, 0F);
//  }


    @Override
    abstract public CelestialBody getCelestialBody();


    @Override
    abstract public Class<? extends IChunkProvider> getChunkProviderClass();

    @Override
    abstract public Class<? extends WorldChunkManager> getWorldChunkManagerClass();

    protected float getRelativeGravity() {
        return 0.1F;
    }

    @Override
    public float getGravity()
    {
        return 0.072F; // this is equivalent to 0.1
    }

    @Override
    public float getFallDamageModifier() {
        return getRelativeGravity();
    }

    @Override
    public double getMeteorFrequency()
    {
        return 10.0D;
    }

    @Override
    public double getFuelUsageMultiplier()
    {
        return getRelativeGravity();
    }

    @Override
    public float calculateCelestialAngle(long par1, float par3)
    {
        return 0.0F;
    }

    @Override
    public boolean canSpaceshipTierPass(int tier)
    {
        return tier >= AmunRa.config.planetDefaultTier;
    }

    /**
     * This is the part which makes the world brighter or dimmer
     */
    @SideOnly(Side.CLIENT)
    @Override
    public float getSunBrightness(float par1)
    {
        float factor = worldObj.getSunBrightnessBody(par1) + getAmunBrightnessFactor(par1);
        if(factor > 1.0F) {
            factor = 1.0F;
        }
        return factor;
    }

    /**
     * TODO do something
     * @param partialTicks
     * @return
     */
    protected float getAmunBrightnessFactor(float partialTicks) {
        CelestialBody curBody = this.getCelestialBody();
        if(curBody instanceof Moon) {
            curBody = ((Moon) curBody).getParentPlanet();
        }
        AngleDistance ad = AstronomyHelper.projectBodyToSky(curBody, AmunRa.instance.starAmun, partialTicks, this.worldObj.getWorldTime());
        // ad.angle is in pi

        // the angle I get is relative to celestialAngle
        float brightnessFactor = 1.0F - (MathHelper.cos((this.worldObj.getCelestialAngle(partialTicks)) * (float)Math.PI * 2.0F  + ad.angle) * 2.0F + 0.5F);

        if(brightnessFactor < 0) {
            brightnessFactor = 0;
        }
        if(brightnessFactor > 1) {
            brightnessFactor = 1;
        }

        brightnessFactor = 1.0F - brightnessFactor;

        // let's say brightnessFactor == 1 -> 0.5 of brightness
        return (float) (brightnessFactor * 0.8 / ad.distance);
    }

    @Override
    public float getSolarSize()
    {
        CelestialBody body = this.getCelestialBody();

        if(body instanceof Moon) {
            return 1.0F / ((Moon) body).getParentPlanet().getRelativeDistanceFromCenter().unScaledDistance;
        }
        return 1.0F / body.getRelativeDistanceFromCenter().unScaledDistance;
    }

    @Override
    public float getThermalLevelModifier()
    {
        return -0.5F;
    }

    @Override
    public void addAsteroid(int x, int y, int z, int size, int core)
    {
        AsteroidData coords = new AsteroidData(x, y, z, size, core);
        if (!this.asteroids.contains(coords))
        {
            if (this.dataNotLoaded)
            {
                this.loadAsteroidSavedData();
            }
            if (!this.asteroids.contains(coords))
            {
                this.addToNBT(this.datafile.datacompound, coords);
                this.asteroids.add(coords);
            }
        }
    }

    @Override
    public void removeAsteroid(int x, int y, int z)
    {
        AsteroidData coords = new AsteroidData(x, y, z);
        if (this.asteroids.contains(coords))
        {
            this.asteroids.remove(coords);

            if (this.dataNotLoaded)
            {
                this.loadAsteroidSavedData();
            }
            this.writeToNBT(this.datafile.datacompound);
        }
    }

    abstract public String getSaveDataID();

    protected void loadAsteroidSavedData()
    {
        this.datafile = (AsteroidSaveData) this.worldObj.loadItemData(AsteroidSaveData.class, getSaveDataID());

        if (this.datafile == null)
        {
            this.datafile = new AsteroidSaveData(getSaveDataID());
            this.worldObj.setItemData(getSaveDataID(), this.datafile);
            this.writeToNBT(this.datafile.datacompound);
        }
        else
        {
            this.readFromNBT(this.datafile.datacompound);
        }

        this.dataNotLoaded = false;
    }

    protected void ensureDataLoaded() {
        if(dataNotLoaded) {
            loadAsteroidSavedData();
        }
    }

    protected void readFromNBT(NBTTagCompound nbt)
    {
        NBTTagList coordList = nbt.getTagList("coords", 10);
        if (coordList.tagCount() > 0)
        {
            for (int j = 0; j < coordList.tagCount(); j++)
            {
                NBTTagCompound tag1 = coordList.getCompoundTagAt(j);

                if (tag1 != null)
                {
                    this.asteroids.add(AsteroidData.readFromNBT(tag1));
                }
            }
        }
    }

    protected void writeToNBT(NBTTagCompound nbt)
    {
        NBTTagList coordList = new NBTTagList();
        for (AsteroidData coords : this.asteroids)
        {
            NBTTagCompound tag = new NBTTagCompound();
            coords.writeToNBT(tag);
            coordList.appendTag(tag);
        }
        nbt.setTag("coords", coordList);
        this.datafile.markDirty();
    }

    protected void addToNBT(NBTTagCompound nbt, AsteroidData coords)
    {
        NBTTagList coordList = nbt.getTagList("coords", 10);
        NBTTagCompound tag = new NBTTagCompound();
        coords.writeToNBT(tag);
        coordList.appendTag(tag);
        nbt.setTag("coords", coordList);
        this.datafile.markDirty();
    }

    @Override
    public BlockVec3 getClosestAsteroidXZ(int x, int y, int z)
    {
        ensureDataLoaded();

        if (this.asteroids.size() == 0)
        {
            return null;
        }

        BlockVec3 result = null;
        AsteroidData resultRoid = null;
        int lowestDistance = Integer.MAX_VALUE;

        for (AsteroidData test : this.asteroids)
        {
            // if this flag is set, then don't?
            if ((test.sizeAndLandedFlag & 128) > 0) // wtf? It's 1 << 7, but why?
                continue;

            int dx = x - test.centre.x;
            int dz = z - test.centre.z;
            int a = dx * dx + dz * dz;
            if (a < lowestDistance)
            {
                lowestDistance = a;
                result = test.centre;
                resultRoid = test;
            }
        }

        if (result == null)
            return null;

        // set the flag?
        resultRoid.sizeAndLandedFlag |= 128; // why?
        this.writeToNBT(this.datafile.datacompound);
        return result.clone();
    }

    /**
     * This seems to be for AstroMiner
     * @param x
     * @param y
     * @param z
     * @param facing
     * @param count
     * @return
     */
    @Override
    public ArrayList<BlockVec3> getClosestAsteroidsXZ(int x, int y, int z, int facing, int count)
    {
        if (this.dataNotLoaded)
        {
            this.loadAsteroidSavedData();
        }

        if (this.asteroids.size() == 0)
        {
            return null;
        }

        TreeMap<Integer, BlockVec3> targets = new TreeMap();

        for (AsteroidData roid : this.asteroids)
        {
            BlockVec3 test = roid.centre;
            switch (facing)
            {
            case 2:
                if (z - 16 < test.z)
                    continue;
                break;
            case 3:
                if (z + 16 > test.z)
                    continue;
                break;
            case 4:
                if (x - 16 < test.x)
                    continue;
                break;
            case 5:
                if (x + 16 > test.x)
                    continue;
                break;
            }
            int dx = x - test.x;
            int dz = z - test.z;
            int a = dx * dx + dz * dz;
            if (a < 262144) targets.put(a, test);
        }

        int max = Math.max(count,  targets.size());
        if (max <= 0) return null;

        ArrayList<BlockVec3> returnValues = new ArrayList();
        int i = 0;
        int offset = EntityAstroMiner.MINE_LENGTH_AST / 2;
        for (BlockVec3 target : targets.values())
        {
            BlockVec3 coords = target.clone();
            GCLog.debug("Found nearby asteroid at "+ target.toString());
            switch (facing)
            {
            case 2:
                coords.z += offset;
                break;
            case 3:
                coords.z -= offset;
                break;
            case 4:
                coords.x += offset;
                break;
            case 5:
                coords.x -= offset;
                break;
            }
            returnValues.add(coords);
            if (++i >= count) break;
        }

        return returnValues;
    }


    @Override
    public void registerWorldChunkManager()
    {
        super.registerWorldChunkManager();
        this.hasNoSky = true;
    }

    @Override
    public double getSolarEnergyMultiplier()
    {
        if (this.solarMultiplier < 0D)
        {
            solarMultiplier = AstronomyHelper.getSolarEnergyMultiplier(getCelestialBody(), !getCelestialBody().atmosphere.isEmpty());
        }
        return this.solarMultiplier;
    }

    protected static class AsteroidData
    {
        protected BlockVec3 centre;
        protected int sizeAndLandedFlag = 15;
        protected int coreAndSpawnedFlag = -2;

        public AsteroidData(int x, int y, int z)
        {
            this.centre = new BlockVec3(x, y, z);
        }

        public AsteroidData(int x, int y, int z, int size, int core)
        {
            this.centre = new BlockVec3(x, y, z);
            this.sizeAndLandedFlag = size;
            this.coreAndSpawnedFlag = core;
        }

        public AsteroidData(BlockVec3 bv)
        {
            this.centre = bv;
        }

        @Override
        public int hashCode()
        {
            if (this.centre != null)
                return this.centre.hashCode();
            else
                return 0;
        }

        @Override
        public boolean equals(Object o)
        {
            if (o instanceof AsteroidData)
            {
                BlockVec3 vector = ((AsteroidData) o).centre;
                return this.centre.x == vector.x && this.centre.y == vector.y && this.centre.z == vector.z;
            }

            if (o instanceof BlockVec3)
            {
                BlockVec3 vector = (BlockVec3) o;
                return this.centre.x == vector.x && this.centre.y == vector.y && this.centre.z == vector.z;
            }

            return false;
        }

        public NBTTagCompound writeToNBT(NBTTagCompound tag)
        {
            tag.setInteger("x", this.centre.x);
            tag.setInteger("y", this.centre.y);
            tag.setInteger("z", this.centre.z);
            tag.setInteger("coreAndFlag", this.coreAndSpawnedFlag);
            tag.setInteger("sizeAndFlag", this.sizeAndLandedFlag);
            return tag;
        }

        public static AsteroidData readFromNBT(NBTTagCompound tag)
        {
            BlockVec3 tempVector = new BlockVec3();
            tempVector.x = tag.getInteger("x");
            tempVector.y = tag.getInteger("y");
            tempVector.z = tag.getInteger("z");

            AsteroidData roid = new AsteroidData(tempVector);
            if (tag.hasKey("coreAndFlag"))
                roid.coreAndSpawnedFlag = tag.getInteger("coreAndFlag");
            if (tag.hasKey("sizeAndFlag"))
                roid.sizeAndLandedFlag = tag.getInteger("sizeAndFlag");

            return roid;
        }
    }

    public class AsteroidSaveData extends WorldSavedData
    {
        public NBTTagCompound datacompound;

        public AsteroidSaveData(String s)
        {
            super(s);
            this.datacompound = new NBTTagCompound();
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt)
        {
            this.datacompound = nbt.getCompoundTag("asteroids");
        }

        @Override
        public void writeToNBT(NBTTagCompound nbt)
        {
            nbt.setTag("asteroids", this.datacompound);
        }
    }

}
