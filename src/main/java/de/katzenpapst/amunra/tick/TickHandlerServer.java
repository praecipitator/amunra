package de.katzenpapst.amunra.tick;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.mothership.Mothership;
import de.katzenpapst.amunra.mothership.MothershipWorldData;
import de.katzenpapst.amunra.network.packet.PacketSimpleAR;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class TickHandlerServer {

    public static MothershipWorldData mothershipData;

    public static void restart() {
        mothershipData = null;
        /*
        if(FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            AmunRa.instance.setClientMothershipData(null);
        }*/
    }

    public TickHandlerServer() {
        // TODO Auto-generated constructor stub
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event)
    {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) return;
        if (event.phase == TickEvent.Phase.START)
        {
            if (TickHandlerServer.mothershipData == null)
            {
                World world = server.worldServerForDimension(0);
                TickHandlerServer.mothershipData = (MothershipWorldData) world.mapStorage.loadData(MothershipWorldData.class, MothershipWorldData.saveDataID);
                //AmunRa.instance.mothershipRegistry = TickHandlerServer.mothershipData;
                if(TickHandlerServer.mothershipData == null) {
                    // TickHandlerServer.ssData = ProceduralGalaxy.instance.getSolarSystemManager();//new SolarSystemManager();
                    TickHandlerServer.mothershipData = new MothershipWorldData(MothershipWorldData.saveDataID);
                    //AmunRa.instance.mothershipRegistry = TickHandlerServer.mothershipData;
                    world.mapStorage.setData(MothershipWorldData.saveDataID, TickHandlerServer.mothershipData );
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent event)
    {
        if(FMLCommonHandler.instance().getSide() != Side.SERVER) {
            return;
        }

        if (event.player instanceof EntityPlayerMP)
        {
            EntityPlayerMP thePlayer = (EntityPlayerMP) event.player;
            // Send list of motherships to player here

            NBTTagCompound msData = new NBTTagCompound();

            TickHandlerServer.mothershipData.writeToNBT(msData);

            AmunRa.packetPipeline.sendTo(new PacketSimpleAR(PacketSimpleAR.EnumSimplePacket.C_UPDATE_MOTHERSHIP_LIST, new Object[] {
                    msData
            }), thePlayer);



            /*GCPlayerStats stats = GCPlayerStats.get(thePlayer);
            SpaceStationWorldData.checkAllStations(thePlayer, stats);
            GalacticraftCore.packetPipeline.sendTo(new PacketSimple(EnumSimplePacket.C_UPDATE_SPACESTATION_CLIENT_ID, new Object[] { WorldUtil.spaceStationDataToString(stats.spaceStationDimensionData) }), thePlayer);
            SpaceRace raceForPlayer = SpaceRaceManager.getSpaceRaceFromPlayer(thePlayer.getGameProfile().getName());
            if (raceForPlayer != null) SpaceRaceManager.sendSpaceRaceData(thePlayer, raceForPlayer);*/
        }
        /*if (event.player.worldObj.provider instanceof WorldProviderOrbit && event.player instanceof EntityPlayerMP)
        {
            ((WorldProviderOrbit) event.player.worldObj.provider).sendPacketsToClient((EntityPlayerMP) event.player);
        }*/
    }

}
