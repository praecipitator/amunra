package de.katzenpapst.amunra.mothership;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.FMLLog;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.network.packet.PacketSimpleAR;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.planets.asteroids.dimension.ShortRangeTelepadHandler.TelepadEntry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldSavedData;

public class MothershipWorldData extends WorldSavedData {

    public static final String saveDataID = "ARMothershipData";
    private NBTTagCompound dataCompound;


    // https://github.com/Questology/Questology/blob/d125a9359e50a84ccee0c5100f04464a0d13e072/src/main/java/demonmodders/questology/handlers/event/GenericEventHandler.java
    protected HashMap<Integer, Mothership> mothershipIdList;

    public MothershipWorldData(String id) {
        super(id);
        mothershipIdList = new HashMap<Integer, Mothership>();
    }


    public HashMap<Integer, Mothership> getMotherships() {
        return (HashMap<Integer, Mothership>) mothershipIdList.clone();
    }

    int highestId = 0;

    public Mothership registerNewMothership(String player, CelestialBody currentParent) {
        int newId = ++highestId;

        Mothership ship = new Mothership(newId, player);
        ship.setParent(currentParent);

        mothershipIdList.put(newId, ship);

        this.markDirty();
        /*AmunRa.packetPipeline.sendToServer(new PacketSimpleAR(PacketSimpleAR.EnumSimplePacket.S_CREATE_MOTHERSHIP, new Object[] {
                                    Mothership.getOrbitableBodyName(this.selectedBody)
                            }));*/
        NBTTagCompound data = new NBTTagCompound();
        ship.writeToNBT(data);
        AmunRa.packetPipeline.sendToAll(new PacketSimpleAR(PacketSimpleAR.EnumSimplePacket.C_NEW_MOTHERSHIP_CREATED, new Object[]{
                data
        }));
        return ship;
    }

    public void addMothership(Mothership ship) {
        // probably got from server
        if(ship.getID() > highestId) {
            highestId = ship.getID();
        }

        if(mothershipIdList.get(ship.getID()) != null) {
            FMLLog.log(Level.INFO, "Mothership #%d is already registered, this might be weird", ship.getID());
        }
        mothershipIdList.put(ship.getID(), ship);
        this.markDirty();// not sure if needed. does the client even save this?
    }

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

    public int getNumMothershipsForPlayer(String player) {
        int num = 0;

        Iterator it = mothershipIdList.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Mothership curM = (Mothership) pair.getValue();

            if(player.equals(curM.getOwner())) {
                num++;
            }
        }

        return num;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        NBTTagList tagList = data.getTagList("MothershipList", 10);
        mothershipIdList.clear();

        for (int i = 0; i < tagList.tagCount(); i++)
        {
            NBTTagCompound nbt2 = tagList.getCompoundTagAt(i);
            Mothership m = Mothership.createFromNBT(nbt2);
            mothershipIdList.put(m.getID(), m);
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


}
