package de.katzenpapst.amunra.mothership;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.helper.PlayerID;
import de.katzenpapst.amunra.network.packet.PacketSimpleAR;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody.ScalableDistance;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.Moon;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.Satellite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.DimensionManager;

public class MothershipWorldData extends WorldSavedData {

    public static final String saveDataID = "ARMothershipData";
    private NBTTagCompound dataCompound;

    // orbit distances should stay the same
    private HashMap<CelestialBody, Float> orbitDistances;

    private int highestId = 0;
    private int numTicksWithoutSave = 0;

    // https://github.com/Questology/Questology/blob/d125a9359e50a84ccee0c5100f04464a0d13e072/src/main/java/demonmodders/questology/handlers/event/GenericEventHandler.java
    protected HashMap<Integer, Mothership> mothershipIdList;

    protected HashMap<Integer, Mothership> mothershipsByDimension;

    public MothershipWorldData(String id) {
        super(id);
        mothershipIdList = new HashMap<Integer, Mothership>();
        mothershipsByDimension = new HashMap<Integer, Mothership>();
        orbitDistances = new HashMap<CelestialBody, Float>();
    }


    public HashMap<Integer, Mothership> getMotherships() {
        return (HashMap<Integer, Mothership>) mothershipIdList.clone();
    }


    protected void updateAllOrbits() {
        HashMap<CelestialBody, Integer> bodies = this.getBodiesWithShips();
        for(CelestialBody b: bodies.keySet()) {
            this.updateOrbitsFor(b);
        }
    }

    protected void updateOrbitsFor(CelestialBody parent) {
        if(parent == null) return;

        List<Mothership> list = getMothershipsForParent(parent);
        int numShips = list.size();
        float twoPi = (float) Math.PI * 2;
        float angle = (twoPi / numShips);
        Random rand = new Random(parent.getName().hashCode());
        float phaseOffset =  rand.nextFloat()*twoPi;
        float orbitDistance = getMothershipOrbitDistanceFor(parent);

        for(Mothership ms: list) {

            if(phaseOffset > twoPi) {
                phaseOffset -= twoPi;
            }

            ms.setPhaseShift(phaseOffset);
            ms.setRelativeDistanceFromCenter(new ScalableDistance(orbitDistance, orbitDistance));
            phaseOffset += angle;
        }
        this.markDirty();
    }

    public float getMothershipOrbitDistanceFor(CelestialBody parent) {
        if(orbitDistances.get(parent) != null) {
           return orbitDistances.get(parent);
        }

        // recalc
        float orbitSize = -1;
        if(parent instanceof Planet) {
            // now try to find out what the closest thing here is
            for (Moon moon : GalaxyRegistry.getRegisteredMoons().values()) {
                if(moon.getParentPlanet() != parent)
                    continue;
                if(orbitSize == -1 || orbitSize > moon.getRelativeDistanceFromCenter().unScaledDistance) {
                    orbitSize = moon.getRelativeDistanceFromCenter().unScaledDistance;
                }
            }
            for (Satellite satellite : GalaxyRegistry.getRegisteredSatellites().values()) {
                if(satellite.getParentPlanet() != parent) {
                    continue;
                }
                if(orbitSize == -1 || orbitSize > satellite.getRelativeDistanceFromCenter().unScaledDistance) {
                    orbitSize = satellite.getRelativeDistanceFromCenter().unScaledDistance;
                }

            }
            if(orbitSize == -1) {
                orbitSize = 10.0F;
            } else {
                orbitSize -= 1.0F;
            }
        } else {
            // todo figure out
            orbitSize = 5.0F;

        }
        orbitDistances.put(parent, orbitSize);
        return orbitSize;
    }

    /**
     * Creates new mothership for given player and given parentBody, sends it to all clients and returns it.
     *
     * @param player
     * @param currentParent
     * @return
     */
    // @SideOnly(Side.SERVER)
    public Mothership registerNewMothership(EntityPlayer player, CelestialBody currentParent) {
        int newId = ++highestId;

        // failsafe
        if(mothershipIdList.get(newId) != null) {
            throw new RuntimeException("Somehow highestID is already used");
        }


        // find dimension ID
        int newDimensionID = DimensionManager.getNextFreeDimId();

        DimensionManager.registerDimension(newDimensionID, AmunRa.config.mothershipProviderID);

        Mothership ship = new Mothership(newId, new PlayerID(player));
        ship.setParent(currentParent);
        ship.setDimensionInfo(newDimensionID);

        mothershipIdList.put(newId, ship);
        mothershipsByDimension.put(newDimensionID, ship);
        this.updateOrbitsFor(currentParent);// Do I even need this on server side?

        this.markDirty();

        NBTTagCompound data = new NBTTagCompound();
        ship.writeToNBT(data);

        AmunRa.packetPipeline.sendToAll(new PacketSimpleAR(PacketSimpleAR.EnumSimplePacket.C_NEW_MOTHERSHIP_CREATED, new Object[]{
                data
        }));
        return ship;
    }

    /**
     * Add an existing mothership object, usually one which the server sent here
     *
     * @param ship
     * @return the definite mothership object as it should be used and stuff
     */
    @SideOnly(Side.CLIENT)
    public Mothership addMothership(Mothership ship) {

        if(MinecraftServer.getServer() != null && !MinecraftServer.getServer().isDedicatedServer()) {
            // don't do this on an integrated SSP server, because for these, the list is up to date already
            this.updateOrbitsFor(ship.getParent());
            // here we have a stupid case where the ship we get is a duplicate of one in the list
            return this.getByMothershipId(ship.getID());
        }
        // probably got from server
        if(ship.getID() > highestId) {
            highestId = ship.getID();
        }

        if(mothershipIdList.get(ship.getID()) != null) {
            throw new RuntimeException("Mothership "+ship.getID()+" is already registered, this shouldn't happen...");
        }

        maybeRegisterDimension(ship.getDimensionID());
        //DimensionManager.registerDimension(ship.getDimensionID(), AmunRa.instance.confMothershipProviderID);

        mothershipIdList.put(ship.getID(), ship);
        mothershipsByDimension.put(ship.getDimensionID(), ship);
        this.updateOrbitsFor(ship.getParent());
        // this.markDirty();// not sure if needed. does the client even save this?
        return ship;
    }

    /**
     * Should only be used if only the number of ships around a body is required, otherwise just get the full list
     *
     * @param parent
     * @return
     */
    public int getNumMothershipsForParent(CelestialBody parent) {
        int result = 0;

        Iterator it = mothershipIdList.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Mothership curM = (Mothership) pair.getValue();

            CelestialBody curParent = curM.getParent();
            if(curParent != null && curParent.equals(parent)) {
                result++;
            }
        }

        return result;
    }

    public boolean hasMothershipsInOrbit(CelestialBody parent) {
        Iterator it = mothershipIdList.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Mothership curM = (Mothership) pair.getValue();

            if(curM.getParent() == parent) return true;
        }
        return false;
    }

    /**
     * Get all motherships for a certain parent
     * @param parent
     * @return
     */
    public List<Mothership> getMothershipsForParent(CelestialBody parent) {
        LinkedList<Mothership> result = new LinkedList<Mothership> ();

        Iterator it = mothershipIdList.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Mothership curM = (Mothership) pair.getValue();

            CelestialBody curParent = curM.getParent();
            if(curParent != null && curParent.equals(parent)) {
                result.add(curM);
            }
        }

        return result;
    }

    /**
     * Get all motherships owned by a certain player
     * @param player
     * @return
     */
    public int getNumMothershipsForPlayer(PlayerID player) {
        int num = 0;

        Iterator it = mothershipIdList.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Mothership curM = (Mothership) pair.getValue();

            if(curM.isPlayerOwner(player)) {
                num++;
            }
        }

        return num;
    }

    public int getNumMothershipsForPlayer(EntityPlayer player) {
        return getNumMothershipsForPlayer(new PlayerID(player));
    }

    /**
     * Gets a list of CelestialBodies which have motherships.
     * @return a map where the key is the celestial body and the value is the number of motherships around it
     */
    public HashMap<CelestialBody, Integer> getBodiesWithShips() {
        HashMap<CelestialBody, Integer> result = new HashMap<CelestialBody, Integer>();

        Iterator it = mothershipIdList.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Mothership curM = (Mothership) pair.getValue();
            CelestialBody parent = curM.getParent();
            if(parent == null) continue;

            if(result.get(parent) == null) {
                result.put(parent, 1);
            } else {
                result.put(parent, result.get(parent)+1);
            }

        }

        return result;
    }

    public Mothership getByDimensionId(int dimId) {
        return mothershipsByDimension.get(dimId);
    }

    public Mothership getByMothershipId(int id) {
        return mothershipIdList.get(id);
    }

    public Mothership getByName(String name) {
        Iterator it = mothershipIdList.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Mothership curM = (Mothership) pair.getValue();
            if(curM.getName().equals(name)) {
                return curM;
            }
        }
        return null;
    }

    /**
     * This should only ever be called when the save is loaded initially
     */
    @Override
    public void readFromNBT(NBTTagCompound data) {
        NBTTagList tagList = data.getTagList("MothershipList", 10);
        mothershipIdList.clear();
        mothershipsByDimension.clear();

        for (int i = 0; i < tagList.tagCount(); i++)
        {
            NBTTagCompound nbt2 = tagList.getCompoundTagAt(i); // I think I have to unregister them on player logout.
            Mothership m = Mothership.createFromNBT(nbt2);
            if(highestId < m.getID()) {
                highestId = m.getID();
            }

            if(DimensionManager.isDimensionRegistered(m.getDimensionID())) {
                if(DimensionManager.getProviderType(m.getDimensionID()) != AmunRa.config.mothershipProviderID) {
                    // now that shouldn't happen
                    throw new RuntimeException("Dimension "+m.getDimensionID()+" should be registered for an AmunRa Mothership, registered for "+DimensionManager.getProviderType(m.getDimensionID())+" instead");
                }
                // it's fine otherwise
            } else {
                DimensionManager.registerDimension(m.getDimensionID(), AmunRa.config.mothershipProviderID);
            }

            mothershipIdList.put(m.getID(), m);
            mothershipsByDimension.put(m.getDimensionID(), m);
        }

        this.updateAllOrbits();

    }

    /**
     * This should only be called on the client if the server has sent some generic change data
     * @param data
     */
    /*public void updateFromNBT(NBTTagCompound data) {
        NBTTagList tagList = data.getTagList("MothershipList", 10);

        for (int i = 0; i < tagList.tagCount(); i++)
        {
            NBTTagCompound mothershipNBT = tagList.getCompoundTagAt(i);
            int id = mothershipNBT.getInteger("id");
            Mothership m = this.getByMothershipId(id);
            if(m != null) {
                m.updateFromNBT(mothershipNBT);
            } else {
                m = Mothership.createFromNBT(mothershipNBT);
                if(highestId < id) {
                    highestId = id;
                }
                if(DimensionManager.isDimensionRegistered(m.getDimensionID())) {
                    if(DimensionManager.getProviderType(m.getDimensionID()) != AmunRa.config.mothershipProviderID) {
                        // now that shouldn't happen
                        throw new RuntimeException("Dimension "+m.getDimensionID()+" should be registered for an AmunRa Mothership, registered for "+DimensionManager.getProviderType(m.getDimensionID())+" instead");
                    }
                    // it's fine otherwise
                } else {
                    DimensionManager.registerDimension(m.getDimensionID(), AmunRa.config.mothershipProviderID);
                }

                mothershipIdList.put(m.getID(), m);
                mothershipsByDimension.put(m.getDimensionID(), m);
            }


        }
    }*/

    /**
     * Hack for client-side dimension registration
     *
     * @param dimId
     */
    protected void maybeRegisterDimension(int dimId) {
        if(!DimensionManager.isDimensionRegistered(dimId)) {
            DimensionManager.registerDimension(dimId, AmunRa.config.mothershipProviderID);
        } else {
            // just check if it's registered the right way
            int type = DimensionManager.getProviderType(dimId);
            if(type != AmunRa.config.mothershipProviderID) {
                throw new RuntimeException("Dimension "+dimId+" could not be registered for mothership because it's already taken");
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        NBTTagList tagList = new NBTTagList();

        // HashMap<Integer, Mothership> mothershipIdList
        for (Mothership m : mothershipIdList.values())
        {
            NBTTagCompound nbt2 = new NBTTagCompound();
            m.writeToNBT(nbt2);
            tagList.appendTag(nbt2);
        }

        data.setTag("MothershipList", tagList);
    }

    public void tickAllMotherships() {
        boolean hasChanged = false;
        for (Mothership m : mothershipIdList.values())
        {
            if(!m.isInTransit()) {
                continue;
            }
            numTicksWithoutSave++;
            hasChanged = true;
            if(m.modRemainingTravelTime(-1) <= 0) {
                // arrived

                // we will need the worldprovider here
                m.getWorldProviderServer().endTransit();

                AmunRa.packetPipeline.sendToAll(new PacketSimpleAR(PacketSimpleAR.EnumSimplePacket.C_MOTHERSHIP_TRANSIT_ENDED, m.getID()));
            }
        }
        if(hasChanged || (!hasChanged && numTicksWithoutSave > 0)) {
            // if no changes, but still unsaved changes
            if(numTicksWithoutSave >= 1200) {
                numTicksWithoutSave = 0;
                this.markDirty(); //
            }
            /*if(hasChanged) {
                NBTTagCompound data = new NBTTagCompound ();
                TickHandlerServer.mothershipData.writeToNBT(data);
                AmunRa.packetPipeline.sendToAll(new PacketSimpleAR(PacketSimpleAR.EnumSimplePacket.C_UPDATE_MOTHERSHIP_LIST, data));
            }*/
        }
    }

    /**
     * This is just so that the progress bar is updated on client
     */
    public void tickAllMothershipsClient() {
        for (Mothership m : mothershipIdList.values())
        {
            if(!m.isInTransit()) {
                continue;
            }
            if(m.getRemainingTravelTime() > 0) {
                m.modRemainingTravelTime(-1);
            }
        }
    }

    public void unregisterAllMotherships() {

            for (Integer dimID : mothershipsByDimension.keySet())
            {
                DimensionManager.unregisterDimension(dimID);
            }

            mothershipIdList.clear();
            mothershipsByDimension.clear();

    }
}
