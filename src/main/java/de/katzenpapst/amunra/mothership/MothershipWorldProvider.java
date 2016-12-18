package de.katzenpapst.amunra.mothership;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Vector2d;

import codechicken.lib.vec.Vector3;
import cpw.mods.fml.common.FMLCommonHandler;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.block.IMetaBlock;
import de.katzenpapst.amunra.block.SubBlock;
import de.katzenpapst.amunra.block.machine.mothershipEngine.BlockMothershipJetMeta;
import de.katzenpapst.amunra.block.machine.mothershipEngine.IMothershipEngine;
import de.katzenpapst.amunra.block.machine.mothershipEngine.MothershipEngineJetBase;
import de.katzenpapst.amunra.network.packet.PacketSimpleAR;
import de.katzenpapst.amunra.network.packet.PacketSimpleAR.EnumSimplePacket;
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
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.util.Constants;

public class MothershipWorldProvider extends WorldProviderOrbit {

    /**
     * Just to hold some stuff for transits
     *
     */
    public class TransitData {
        // the direction in which the ship will travel, relevant for skybox rendering
        public int direction = 0;
        // the max speed the ship can reach
        public double speed = 0;
        // the max thrust the engines can reach, maybe to indicate how many more blocks you can add?
        public double thrust = 0;

        public TransitData(int direction, double speed, double thrust) {
            this.direction = direction;
            this.speed = speed;
            this.thrust = thrust;
        }

        public TransitData() {
            this.direction = 0;
            this.speed = -1;
            this.thrust = 0;
        }

        public boolean isEmpty() {
            return speed <= 0 || thrust <= 0;
        }

        public void readFromNBT(NBTTagCompound nbt)
        {
            this.direction = nbt.getInteger("direction");
            this.speed = nbt.getDouble("speed");
            this.thrust = nbt.getDouble("thrust");
        }

        public void writeToNBT(NBTTagCompound nbt)
        {
            nbt.setInteger("direction", this.direction);
            nbt.setDouble("speed", this.speed);
            nbt.setDouble("thrust", this.thrust);
        }
    }
    /**
     * On client, this is the sole authority regarding the day length
     * On server, this is changed and sent to client as needed
     */
    protected long cachedDayLength = 24000L;

    protected HashSet<Vector2int> checkedChunks = new HashSet<Vector2int>();
    protected HashSet<Vector3int> engineLocations = new HashSet<Vector3int>();

    protected float totalMass;
    protected long totalNumBlocks;

    protected TransitData potentialTransitData;

    protected boolean isAsyncUpdateRunning = false;


    protected Mothership mothershipObj;

    public MothershipWorldProvider() {
    }

    public float getTotalMass() {
        return totalMass;
    }

    public long getNumBlocks() {
        return totalNumBlocks;
    }

    public TransitData getTheoreticalTransitData() {
        return this.potentialTransitData;
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
        return cachedDayLength;
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
        // This is the NAME of the DIMENSION where to fall to
        // This shouldn't actually be ever used
        return "Overworld";
    }

    @Override
    public int getYCoordToTeleportToPlanet()
    {
        // hack.
        return -1000;
    }

    /**
     * Does the last minute MS check and starts the transit here and in the MS object. Returns true if this worked
     * This should only ever happen on server
     *
     * @return
     */
    public boolean startTransit(CelestialBody target) {
        if(this.worldObj.isRemote) {
            // client
            return false;
        }
        // first, do the check
        this.updateMothership(true);

        // now check if we can really reach the target
        TransitData td = this.getTransitDataTo(target);
        if(td.isEmpty()) {
            return false;
        }

        double distance = this.mothershipObj.getTravelDistanceTo(target);

        // now, the object
        if(!this.mothershipObj.startTransit(target, this.mothershipObj.getTravelTimeTo(distance, td.speed))) {
            return false;
        }

        // okay, seems like we can continue
        // we will need all engines
        for(Vector3int loc: this.engineLocations) {
            Block b = this.worldObj.getBlock(loc.x, loc.y, loc.z);
            int meta = this.worldObj.getBlockMetadata(loc.x, loc.y, loc.z);
            if(b instanceof IMothershipEngine) {
                IMothershipEngine engine = (IMothershipEngine)b;
                if(engine.getDirection(worldObj, loc.x, loc.y, loc.z, meta) == td.direction) {
                    double curSpeed = engine.getSpeed(worldObj, loc.x, loc.y, loc.z, meta);
                    double curThrust = engine.getThrust(worldObj, loc.x, loc.y, loc.z, meta);
                    if(curSpeed <= 0 || curThrust <= 0) {
                        continue;
                    }
                    ((IMothershipEngine) b).beginTransit(worldObj, loc.x, loc.y, loc.z, meta, distance);
                }
            }
        }

        return true;
    }

    public void endTransit() {
        this.mothershipObj.endTransit();

        if(this.worldObj.isRemote) {
            return; // I think the rest will be synched over anyway?
        }

        for(Vector3int loc: this.engineLocations) {
            Block b = this.worldObj.getBlock(loc.x, loc.y, loc.z);
            int meta = this.worldObj.getBlockMetadata(loc.x, loc.y, loc.z);
            if(b instanceof IMothershipEngine) {
                IMothershipEngine engine = (IMothershipEngine)b;
                if(engine.isInUse(worldObj, loc.x, loc.y, loc.z, meta)) {
                    engine.endTransit(worldObj, loc.x, loc.y, loc.z, meta);
                }
            }
        }
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
     * Figures out the direction of this ship, and the displayed amount of thrust, to check against the actual.
     * @return
     */
    protected TransitData calcTheoreticalTransitData() {

        TransitData[] tDatas = new TransitData[4];

        for(Vector3int loc: engineLocations) {
            Block b = this.worldObj.getBlock(loc.x, loc.y, loc.z);
            int meta = this.worldObj.getBlockMetadata(loc.x, loc.y, loc.z);
            if(b instanceof IMothershipEngine) {
                IMothershipEngine engine = (IMothershipEngine)b;

                int direction = engine.getDirection(worldObj, loc.x, loc.y, loc.z, meta);
                if(tDatas[direction] == null) {
                    tDatas[direction] = new TransitData(direction, -1, 0);
                }
                double curSpeed = engine.getSpeed(worldObj, loc.x, loc.y, loc.z, meta);
                double curThrust = engine.getThrust(worldObj, loc.x, loc.y, loc.z, meta);
                if(curSpeed <= 0 || curThrust <= 0) {
                    continue; // not sure when this could happen, but in that case, this engine doesn't count
                }
                if(tDatas[direction].speed == -1 || curSpeed < tDatas[direction].speed) {
                    tDatas[direction].speed = curSpeed;
                }
                tDatas[direction].thrust += curThrust;
            }
        }
        // now check which one will actually be relevant
        // pick the one with the highest thrust
        int resultDirection = -1;
        double maxThrust = 0;
        for(int i = 0; i<tDatas.length;i++) {
            if(tDatas[i] == null || tDatas[i].isEmpty()) {
                continue;
            }
            if(tDatas[i].thrust > maxThrust) {
                maxThrust = tDatas[i].thrust;
                resultDirection = i;
            }
        }

        if(resultDirection == -1) {
            return new TransitData();
        }
        return tDatas[resultDirection];
    }

    public TransitData getTransitDataTo(CelestialBody target) {
        if(!Mothership.canBeOrbited(target)) {
            return new TransitData();
        }

        double distance = this.mothershipObj.getTravelDistanceTo(target);

        TransitData[] tDatas = new TransitData[4];
        /*for(int i=0;i<tDatas.length;i++) {
            tDatas[i] = new TransitData(i, -1, 0);
        }*/

        for(Vector3int loc: engineLocations) {
            Block b = this.worldObj.getBlock(loc.x, loc.y, loc.z);
            int meta = this.worldObj.getBlockMetadata(loc.x, loc.y, loc.z);
            if(b instanceof IMothershipEngine) {
                IMothershipEngine engine = (IMothershipEngine)b;
                if(!engine.canTravelDistance(worldObj, loc.x, loc.y, loc.z, meta, distance)) {
                    continue;
                }

                int direction = engine.getDirection(worldObj, loc.x, loc.y, loc.z, meta);
                if(tDatas[direction] == null) {
                    tDatas[direction] = new TransitData(direction, -1, 0);
                }
                double curSpeed = engine.getSpeed(worldObj, loc.x, loc.y, loc.z, meta);
                double curThrust = engine.getThrust(worldObj, loc.x, loc.y, loc.z, meta);
                if(curSpeed <= 0 || curThrust <= 0) {
                    continue; // not sure when this could happen, but in that case, this engine doesn't count
                }
                if(tDatas[direction].speed == -1 || curSpeed < tDatas[direction].speed) {
                    tDatas[direction].speed = curSpeed;
                }
                tDatas[direction].thrust += curThrust;
                // ((BlockMothershipJetMeta)b).getSubBlock(meta).
            }
        }
        // now check which one will actually be relevant
        // pick the one with the highest thrust
        int resultDirection = -1;
        double maxThrust = 0;
        for(int i = 0; i<tDatas.length;i++) {
            if(tDatas[i] == null || tDatas[i].isEmpty()) {
                continue;
            }
            if(tDatas[i].thrust > maxThrust) {
                maxThrust = tDatas[i].thrust;
                resultDirection = i;
            }
        }

        if(resultDirection == -1) {
            return new TransitData();
        }
        return tDatas[resultDirection];
    }

    /**
     * This starts a thread with the updateMothership method
     */
    public void asyncMothershipUpdate() {
        // I hope this works...
        if(isAsyncUpdateRunning) return;
        isAsyncUpdateRunning = true;

        final MothershipWorldProvider self = this;
        Runnable r = new Runnable() {
            @Override
            public void run() {
                self.updateMothership(true);
                self.isAsyncUpdateRunning = false;
                self.asyncMothershipUpdateFinished();
            }
        };
        Thread t = new Thread(r);
        t.start();
    }

    protected void asyncMothershipUpdateFinished() {

    }

    /**
     * This should recalculate the size and mass of the ship, and find all the engines
     * @param notifyClients whenever to send a packet to notify clients afterwards
     */
    public void updateMothership(boolean notifyClients) {
        // potentially do this as async?
        // I have absolutely no idea whenever I can trust this...

        System.out.println("BEGIN updating Mothership");
        // worldObj.getChunkProvider().getLoadedChunkCount()
        checkedChunks.clear();
        engineLocations.clear();
        totalMass = 0;
        totalNumBlocks = 0;
        potentialTransitData = new TransitData();
        processChunk(0, 0);
        System.out.println("END updating Mothership");

        // also recalc transit data
        potentialTransitData = calcTheoreticalTransitData();


        if(notifyClients) {
            NBTTagCompound nbt = new NBTTagCompound ();
            this.writeToNBT(nbt);
            //AmunRa.packetPipeline.sendTo(new PacketSimpleAR(EnumSimplePacket.C_OPEN_SHUTTLE_GUI, new Object[] { player.getGameProfile().getName(), dimensionList }), player);
            AmunRa.packetPipeline.sendToDimension(new PacketSimpleAR(EnumSimplePacket.C_MOTHERSHIP_DATA, dimensionId, nbt), dimensionId);
        }
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
       // super.readFromNBT(nbt);
        this.doSpinning = false;
        //updateMothership();
        this.totalMass = nbt.getFloat("totalMass");
        this.totalNumBlocks = nbt.getLong("totalNumBlocks");


        NBTTagList list = nbt.getTagList("engineLocations", Constants.NBT.TAG_COMPOUND);

        this.engineLocations.clear();
        for(int i=0;i<list.tagCount();i++) {
            NBTTagCompound posData = list.getCompoundTagAt(i);
            Vector3int pos = new Vector3int(
                    posData.getInteger("x"),
                    posData.getInteger("y"),
                    posData.getInteger("z")
            );
            this.engineLocations.add(pos);
        }

        if(this.potentialTransitData == null) {
            this.potentialTransitData = new TransitData();
        }
        this.potentialTransitData.readFromNBT(nbt.getCompoundTag("transitData"));
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

        NBTTagCompound tData = new NBTTagCompound();
        this.potentialTransitData.writeToNBT(tData);
        nbt.setTag("transitData", tData);
    }
}
