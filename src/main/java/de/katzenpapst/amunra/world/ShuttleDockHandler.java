package de.katzenpapst.amunra.world;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import cpw.mods.fml.common.FMLCommonHandler;
import de.katzenpapst.amunra.tick.TickHandlerServer;
import de.katzenpapst.amunra.tile.TileEntityShuttleDock;
import de.katzenpapst.amunra.vec.Vector3int;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;
import micdoodle8.mods.galacticraft.planets.asteroids.dimension.ShortRangeTelepadHandler.TelepadEntry;
import micdoodle8.mods.galacticraft.planets.asteroids.tick.AsteroidsTickHandlerServer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.Constants.NBT;

public class ShuttleDockHandler extends WorldSavedData {

    public static final String saveDataID = "ShuttleDock";

    // map: dimensionID => positions of all docks in that dim
    private static Map<Integer, HashSet<Vector3int>> tileMap = Maps.newHashMap();



    public ShuttleDockHandler(String p_i2141_1_) {
        super(p_i2141_1_);
        // TODO Auto-generated constructor stub
    }

    protected static void markInstanceDirty() {
        TickHandlerServer.dockData.setDirty(true);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        // this should be a list of lists now
        NBTTagList tagList = nbt.getTagList("DockList", NBT.TAG_COMPOUND);
        tileMap.clear();

        for (int i = 0; i < tagList.tagCount(); i++)
        {
            NBTTagCompound nbt2 = tagList.getCompoundTagAt(i);

            int dimID = nbt2.getInteger("DimID");
            NBTTagList posList = nbt2.getTagList("PosList", NBT.TAG_COMPOUND);

            HashSet<Vector3int> curList = new HashSet<Vector3int>();

            for(int j=0; j<posList.tagCount(); j++) {
                NBTTagCompound posTag = posList.getCompoundTagAt(i);
                int posX = posTag.getInteger("PosX");
                int posY = posTag.getInteger("PosY");
                int posZ = posTag.getInteger("PosZ");
                Vector3int pos = new Vector3int (posX, posY, posZ);

                curList.add(pos);
            }
            tileMap.put(dimID, curList);
        }

    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {

        NBTTagList totalNbtList = new NBTTagList();

        for(int dimID: tileMap.keySet()) {

            NBTTagCompound dimTag = new NBTTagCompound ();
            dimTag.setInteger("DimID", dimID);

            HashSet<Vector3int> curList = tileMap.get(dimID);
            NBTTagList posNbtList = new NBTTagList();
            for(Vector3int pos: curList) {
                NBTTagCompound posTag = new NBTTagCompound ();
                posTag.setInteger("PosX", pos.x);
                posTag.setInteger("PosY", pos.y);
                posTag.setInteger("PosZ", pos.z);
                posNbtList.appendTag(posTag);
            }
            dimTag.setTag("PosList", posNbtList);
            totalNbtList.appendTag(dimTag);
        }

        nbt.setTag("DockList", totalNbtList);
    }

    public static void addDock(TileEntityShuttleDock dock) {
        if(!dock.getWorldObj().isRemote) {
            int dimID = dock.getWorldObj().provider.dimensionId;

            Vector3int pos = new Vector3int(dock.xCoord, dock.yCoord, dock.zCoord);

            if(!tileMap.containsKey(dimID)) {
                HashSet<Vector3int> set = new HashSet<Vector3int>();//pos
                set.add(pos);
                tileMap.put(dimID, set);
            } else {
                HashSet<Vector3int> set = tileMap.get(dimID);
                if(set.contains(pos)) {
                    // this shouldn't happen
                    return;
                }
                set.add(pos);
            }
            markInstanceDirty();
        }
    }

    protected static void removeDock(int dimID, int x, int y, int z) {
        if(tileMap.containsKey(dimID)) {
            Vector3int pos = new Vector3int(x, y, z);

            tileMap.get(dimID).remove(pos);
            markInstanceDirty();
        }
    }

    public static void removeDock(TileEntityShuttleDock dock) {
        if(!dock.getWorldObj().isRemote) {
            int dimID = dock.getWorldObj().provider.dimensionId;
            removeDock(dimID, dock.xCoord, dock.yCoord, dock.zCoord);
        }
    }

    public static Vector3int findAvailableDock(int dimID) {
        if(tileMap.containsKey(dimID)) {
            HashSet<Vector3int> positions = tileMap.get(dimID);
            if(positions.size() > 0) {
                // actually look up
                MinecraftServer theServer = FMLCommonHandler.instance().getMinecraftServerInstance();
                World ws = theServer.worldServerForDimension(dimID);

                for(Vector3int pos: positions) {
                    TileEntity te = ws.getTileEntity(pos.x, pos.y, pos.z);
                    if(te instanceof TileEntityShuttleDock) {
                        if(((TileEntityShuttleDock)te).getDockedEntity() == null) {
                            return pos;
                        }
                    } else {
                        // this is bad
                        removeDock(dimID, pos.x, pos.y, pos.z);
                    }
                }
            }
        }

        return null;
    }

}
