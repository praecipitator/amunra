package de.katzenpapst.amunra.world;

import java.util.ArrayList;
import java.util.HashMap;
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
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.util.Constants.NBT;

public class ShuttleDockHandler extends WorldSavedData {

    public static final String saveDataID = "ShuttleDock";

    // map: dimensionID => (map: position => isAvailable)
    private static Map<Integer, Map<Vector3int, Boolean>> tileMap = Maps.newHashMap();

    public ShuttleDockHandler(String id) {
        super(id);
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
            NBTTagCompound dimensionNbt = tagList.getCompoundTagAt(i);

            int dimID = dimensionNbt.getInteger("DimID");
            NBTTagList posList = dimensionNbt.getTagList("PosList", NBT.TAG_COMPOUND);

            HashMap<Vector3int, Boolean> curList = new HashMap<Vector3int, Boolean>();

            for(int j=0; j<posList.tagCount(); j++) {
                NBTTagCompound posTag = posList.getCompoundTagAt(j);
                int posX = posTag.getInteger("PosX");
                int posY = posTag.getInteger("PosY");
                int posZ = posTag.getInteger("PosZ");
                boolean available = posTag.getBoolean("isAvailable");
                Vector3int pos = new Vector3int (posX, posY, posZ);

                curList.put(pos, available);
            }
            tileMap.put(dimID, curList);
        }
        // logDebugInfo();
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {

        NBTTagList totalNbtList = new NBTTagList();

        // logDebugInfo();

        for(int dimID: tileMap.keySet()) {

            NBTTagCompound dimTag = new NBTTagCompound ();
            dimTag.setInteger("DimID", dimID);

            Map<Vector3int, Boolean> curList = tileMap.get(dimID);
            NBTTagList posNbtList = new NBTTagList();
            for(Vector3int pos: curList.keySet()) {
                boolean avail = curList.get(pos);
                NBTTagCompound posTag = new NBTTagCompound ();

                posTag.setInteger("PosX", pos.x);
                posTag.setInteger("PosY", pos.y);
                posTag.setInteger("PosZ", pos.z);
                posTag.setBoolean("isAvailable", avail);

                posNbtList.appendTag(posTag);
            }
            dimTag.setTag("PosList", posNbtList);
            totalNbtList.appendTag(dimTag);
        }

        nbt.setTag("DockList", totalNbtList);
    }

    public static boolean getStoredAvailability(TileEntityShuttleDock dock) {
        if(!dock.getWorldObj().isRemote) {
            int dimID = dock.getWorldObj().provider.dimensionId;

            Vector3int pos = new Vector3int(dock.xCoord, dock.yCoord, dock.zCoord);
            if(!tileMap.containsKey(dimID)) {
                return false;
            }
            Map<Vector3int, Boolean> set = tileMap.get(dimID);
            if(!set.containsKey(pos)) {
                return false;
            }
            return set.get(pos);
        }
        return false;
    }

    protected static void logDebugInfo() {
        System.out.println("== shuttle dock helper ==");
        for(int dimID: tileMap.keySet()) {
            System.out.println("  "+dimID+": ");
            for(Vector3int pos: tileMap.get(dimID).keySet()) {
                System.out.println("    "+pos+": "+tileMap.get(dimID).get(pos));
            }
        }
    }

    public static void setStoredAvailability(TileEntityShuttleDock dock, boolean isAvailable) {
        if(!dock.getWorldObj().isRemote) {
            if(dock.isInvalid()) {
                return;
            }
            int dimID = dock.getWorldObj().provider.dimensionId;

            Vector3int pos = new Vector3int(dock.xCoord, dock.yCoord, dock.zCoord);


            if(!tileMap.containsKey(dimID)) {
                Map<Vector3int, Boolean> set = new HashMap<Vector3int, Boolean>();//pos
                set.put(pos, isAvailable);
                tileMap.put(dimID, set);
            } else {
                Map<Vector3int, Boolean> set = tileMap.get(dimID);
                set.put(pos, isAvailable);
            }
            markInstanceDirty();
        }
    }

    public static void addDock(TileEntityShuttleDock dock) {
        setStoredAvailability(dock, dock.isAvailable());
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
            Map<Vector3int, Boolean> positions = tileMap.get(dimID);
            if(positions.size() > 0) {
                // actually look up
                MinecraftServer theServer = FMLCommonHandler.instance().getMinecraftServerInstance();
                World ws = theServer.worldServerForDimension(dimID);

                for(Vector3int pos: positions.keySet()) {
                    //int chunkx = CoordHelper.blockToChunk(pos.x);
                    //int chunkz = CoordHelper.blockToChunk(pos.z);
                    //ws.checkChunksExist(p_72904_1_, p_72904_2_, p_72904_3_, p_72904_4_, p_72904_5_, p_72904_6_)
                    //if (ws.getChunkProvider().chunkExists(chunkx, chunkz)) {
                    // seems like there is no real way to figure out if a chunk has actually been really, really, loaded
                        TileEntity te = ws.getTileEntity(pos.x, pos.y, pos.z);
                        if(te != null && te instanceof TileEntityShuttleDock) {
                            if(((TileEntityShuttleDock)te).isAvailable()) {
                                return pos;
                            }
                        } else {
                            Boolean avail = positions.get(pos);
                            if(avail) {
                                return pos;
                            }
                            // now if this chunk is loaded, and te is still null or wrong, then something's bad
                            // removeDock(dimID, pos.x, pos.y, pos.z);
                        }
                    //} else {
                        // return the stored value
                        /*Boolean avail = positions.get(pos);
                        if(avail) {
                            return pos;
                        }*/
                    //}
                }
            }
        }

        return null;
    }

}
