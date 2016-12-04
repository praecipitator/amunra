package de.katzenpapst.amunra.mothership;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Vector2d;

import codechicken.lib.vec.Vector3;
import cpw.mods.fml.common.FMLCommonHandler;
import de.katzenpapst.amunra.block.IMetaBlock;
import de.katzenpapst.amunra.block.SubBlock;
import de.katzenpapst.amunra.block.machine.mothershipEngine.BlockMothershipJetMeta;
import de.katzenpapst.amunra.block.machine.mothershipEngine.IMothershipEngine;
import de.katzenpapst.amunra.block.machine.mothershipEngine.MothershipEngineJetBase;
import de.katzenpapst.amunra.tick.TickHandlerServer;
import de.katzenpapst.amunra.vec.Vector2int;
import de.katzenpapst.amunra.vec.Vector3int;
import de.katzenpapst.amunra.world.CoordHelper;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.api.vector.Vector2;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.blocks.BlockSpinThruster;
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderOrbit;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.GCLog;
import micdoodle8.mods.galacticraft.core.util.RedstoneUtil;
import micdoodle8.mods.galacticraft.core.world.gen.ChunkProviderOrbit;
import micdoodle8.mods.galacticraft.core.world.gen.WorldChunkManagerOrbit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.util.Constants;

public class MothershipWorldProvider extends WorldProviderOrbit {

    protected long cachedDayLength = -1;

    protected HashSet<Vector2int> checkedChunks = new HashSet<Vector2int>();
    protected HashSet<Vector3int> engineLocations = new HashSet<Vector3int>();
    protected float totalMass;
    protected long totalNumBlocks;


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
        if(cachedDayLength != -1) {
            return cachedDayLength;
        }
        if(this.mothershipObj == null || this.mothershipObj.getParent() == null) {
            // dafuq
            return 24000L;
        }

        //if(parent != null) {
        //    return parent.get
        //}
        //return parent.getWorldProvider().get
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
    public int getYCoordToTeleportToPlanet()
    {
        // hack.
        return -1000;
    }



    /**
     * The currently orbited celestial body or null if in transit
     * @return
     */
    public CelestialBody getParent()
    {
        return ((Mothership)this.getCelestialBody()).getParent();
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

    @Override
    public double getHorizon()
    {
        return 0.0D;
    }


    /**
     * Checks whenever this mothership can fly to the given target.
     * If yes, returns the speed at which it could travel.
     * If no, returns -1
     *
     * @param target
     * @return
     */
    public double canTransitTo(CelestialBody target) {
        if(!Mothership.canBeOrbited(target)) {
            return -1;
        }
        // now actually calculate it
        // get the distance
        double distance = this.mothershipObj.getTravelDistanceTo(target);
        double minSpeed = -1;
        double totalThrust = 0;
        // now, for each engine, check if it can burn for the entire duration of the flight.
        // if yes, it's thrust "counts" towards the total thrust
        for(Vector3int loc: engineLocations) {
            Block b = this.worldObj.getBlock(loc.x, loc.y, loc.z);
            int meta = this.worldObj.getBlockMetadata(loc.x, loc.y, loc.z);
            if(b instanceof IMothershipEngine) {
                IMothershipEngine engine = (IMothershipEngine)b;
                if(!engine.canTravelDistance(worldObj, loc.x, loc.y, loc.z, meta, distance)) {
                    continue;
                }
                double curSpeed = engine.getSpeed(worldObj, loc.x, loc.y, loc.z, meta);
                if(curSpeed <= 0) {
                    continue; // not sure when this could happen, but in that case, this engine doesn't count
                }
                if(minSpeed == -1 || curSpeed < minSpeed) {
                    curSpeed = minSpeed;
                }
                totalThrust += engine.getStrength(worldObj, loc.x, loc.y, loc.z, meta);
                // ((BlockMothershipJetMeta)b).getSubBlock(meta).
            }
        }

        if(totalThrust >= this.totalMass) {
            return minSpeed;
        }

        return -1;
    }
    /**
     * This should recalculate the size and mass of the ship, and find all the engines
     */
    public void updateMothership() {
        // I have absolutely no idea whenever I can trust this...

        // worldObj.getChunkProvider().getLoadedChunkCount()
        checkedChunks.clear();
        engineLocations.clear();
        totalMass = 0;
        totalNumBlocks = 0;
        processChunk(0, 0);

    }

    /**
     * Processes one chunk for the mothership update, and recursively it's neighbours, until a nonexisting chunk is found.
     *
     * @param x
     * @param z
     */
    protected void processChunk(int x, int z) {
        Vector2int curCoords = new Vector2int(x, z);
        if(checkedChunks.contains(curCoords)) {
            return;
        }
        checkedChunks.add(curCoords);
        if(!worldObj.getChunkProvider().chunkExists(x, z)) {
            System.out.println("Chunk "+x+"/"+z+" does not exist, stopping");
            return;
        }
        System.out.println("Chunk "+x+"/"+z+" exists, processing");
        // actually process the chunk here
        Chunk c = worldObj.getChunkFromChunkCoords(x, z);
        ExtendedBlockStorage[] storage = c.getBlockStorageArray();
        int minY = 256;
        int maxY = -1;
        for(ExtendedBlockStorage st: storage) {
            if(st == null) continue;
            /* yLocation: Contains the bottom-most Y block represented by this ExtendedBlockStorage. Typically a multiple of 16. */
            if(st.getYLocation() < minY) {
                minY = st.getYLocation();
            }
            if(st.getYLocation() > maxY) {
                maxY = st.getYLocation();
            }
        }
        if(minY > maxY) {
            // seems this chunk is empty
            System.out.println("Chunk "+x+"/"+z+" is empty");
        } else {
            maxY += 15; //because there are 16 blocks in that storage
            System.out.println("Chunk "+x+"/"+z+" is not empty. minY = "+minY+", maxY = "+maxY);

            for(int blockX = 0; blockX < 16; blockX++) {
                for(int blockZ = 0; blockZ < 16; blockZ++) {
                    for(int blockY = minY; blockY <= maxY; blockY++) {
                        Block b = c.getBlock(blockX, blockY, blockZ);
                        int meta;
                        if(b != Blocks.air) {
                            meta = c.getBlockMetadata(blockX, blockY, blockZ);
                            // figure out it's... stuff
                            processBlock(b, meta, CoordHelper.rel2abs(blockX, x), blockY, CoordHelper.rel2abs(blockZ, z));
                        }
                    }
                }
            }
        }

        // recursion steps for the neighbours
        processChunk(x+1, z);
        processChunk(x-1, z);
        processChunk(x, z+1);
        processChunk(x, z-1);
    }

    /**
     * This should process one block for the mothership update, figuring out it's mass and stuff
     * The coordinates must be world-global, not chunk-local
     *
     * @param block
     * @param meta
     * @param x
     * @param y
     * @param z
     */
    protected void processBlock(Block block, int meta, int x, int y, int z) {
        // first, the mass
        float m = 1.0F;
        //Liquids have a mass of 1, stone, metal blocks etc will be heavier
        if (!(block instanceof BlockLiquid))
        {
            //For most blocks, hardness gives a good idea of mass
            m = block.getBlockHardness(this.worldObj, x, y, z);
            if (m < 0.1F)
            {
                m = 0.1F;
            }
            else if (m > 30F)
            {
                m = 30F;
            }
            //Wood items have a high hardness compared with their presumed mass
            if (block.getMaterial() == Material.wood)
            {
                m /= 4;
            }

            //TODO: higher mass for future Galacticraft hi-density item like neutronium
            //Maybe also check for things in other mods by name: lead, uranium blocks?
            // my TODO: give my blocks an actual mass or density parameter?
        } else {
            // I beg to differ, lava should be way denser than water, for example
            if(block == Blocks.lava) {
                m = 5.0F; // JUST GUESSING
            }
        }
        this.totalMass += m;
        this.totalNumBlocks++;
        // do I still need center of mass and such? I don't care for now.

        // now, engines
        if(block instanceof IMetaBlock) {
            SubBlock actualBlock = ((IMetaBlock)block).getSubBlock(meta);
            if(actualBlock instanceof MothershipEngineJetBase) {
                // just save their positions
                engineLocations.add(new Vector3int(x, y, z));
            }
        }
    }

    @Override
    public void updateSpinSpeed()
    {
        // noop
    }

    @Override
    public boolean checkSS(BlockVec3 baseBlock, boolean placingThruster)
    {

        // for now, don't do anything in regard of boosters
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        // oh shit this looks like GC is doing the whole nbt reading/writing manually here...
        // on one hand, I don't want to replace too much, on the other hand, the packets it is sending are unnecessary right now...
       // super.readFromNBT(nbt);
        this.doSpinning = false;
        //updateMothership();
        this.totalMass = nbt.getFloat("totalMass");
        this.totalNumBlocks = nbt.getLong("totalNumBlocks");

        // I will definitely need at least one packet to send to clients regarding that stuff
        /*
        this.angularVelocityRadians = nbt.getFloat("omegaRad");
        this.skyAngularVelocity = nbt.getFloat("omegaSky");
        this.angularVelocityTarget = nbt.getFloat("omegaTarget");
        this.angularVelocityAccel = nbt.getFloat("omegaAcc");

        NBTTagCompound oneBlock = (NBTTagCompound) nbt.getTag("oneBlock");
        if (oneBlock != null)
        {
            this.oneSSBlock = BlockVec3.readFromNBT(oneBlock);
        }
        else
        {
            this.oneSSBlock = null;
        }

        //A lot of the data can be refreshed by checkSS
        this.checkSS(this.oneSSBlock, false);

        //Send packets to clients in this dimension
        List<Object> objList = new ArrayList<Object>();
        objList.add(Float.valueOf(this.angularVelocityRadians));
        objList.add(Boolean.valueOf(this.thrustersFiring));
        GalacticraftCore.packetPipeline.sendToDimension(new PacketSimple(EnumSimplePacket.C_UPDATE_STATION_SPIN, objList), this.spaceStationDimensionID);

        objList = new ArrayList<Object>();
        objList.add(Double.valueOf(this.spinCentreX));
        objList.add(Double.valueOf(this.spinCentreZ));
        GalacticraftCore.packetPipeline.sendToDimension(new PacketSimple(EnumSimplePacket.C_UPDATE_STATION_DATA, objList), this.spaceStationDimensionID);

        objList = new ArrayList<Object>();
        objList.add(Integer.valueOf(this.ssBoundsMinX));
        objList.add(Integer.valueOf(this.ssBoundsMaxX));
        objList.add(Integer.valueOf(this.ssBoundsMinY));
        objList.add(Integer.valueOf(this.ssBoundsMaxY));
        objList.add(Integer.valueOf(this.ssBoundsMinZ));
        objList.add(Integer.valueOf(this.ssBoundsMaxZ));
        GalacticraftCore.packetPipeline.sendToDimension(new PacketSimple(EnumSimplePacket.C_UPDATE_STATION_BOX, objList), this.spaceStationDimensionID);
        */

        NBTTagList list = nbt.getTagList("engineLocations", Constants.NBT.TAG_COMPOUND);

        for(int i=0;i<list.tagCount();i++) {
            NBTTagCompound posData = list.getCompoundTagAt(i);
            Vector3int pos = new Vector3int(
                    posData.getInteger("x"),
                    posData.getInteger("y"),
                    posData.getInteger("z")
            );
            this.engineLocations.add(pos);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        nbt.setFloat("totalMass", this.totalMass);
        nbt.setLong("totalNumBlocks", this.totalNumBlocks);

        // engine locations
        NBTTagList list = new NBTTagList();
        for(Vector3int v: this.engineLocations) {
            NBTTagCompound pos = new NBTTagCompound ();
            pos.setInteger("x", v.x);
            pos.setInteger("y", v.y);
            pos.setInteger("z", v.z);

            list.appendTag(pos);
        }
        nbt.setTag("engineLocations", list);

        /*nbt.setBoolean("doSpinning", this.doSpinning);
        nbt.setFloat("omegaRad", this.angularVelocityRadians);
        nbt.setFloat("omegaSky", this.skyAngularVelocity);
        nbt.setFloat("omegaTarget", this.angularVelocityTarget);
        nbt.setFloat("omegaAcc", this.angularVelocityAccel);
        if (this.oneSSBlock != null)
        {
            NBTTagCompound oneBlock = new NBTTagCompound();
            this.oneSSBlock.writeToNBT(oneBlock);
            nbt.setTag("oneBlock", oneBlock);
        }*/
    }
}
