package de.katzenpapst.amunra.tick;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerConnectionFromClientEvent;
import de.katzenpapst.amunra.network.packet.ConnectionPacketAR;
import micdoodle8.mods.galacticraft.core.util.MapUtil;

public class ConnectionEvents {

    private boolean clientConnected;
/*
    // wtf does this do?
    static
    {
        EnumConnectionState.field_150761_f.put(PacketSimpleAR.class, EnumConnectionState.PLAY);
        EnumConnectionState.PLAY.field_150770_i.put(2595, PacketSimpleAR.class);
    }
*/

    /*
    @SubscribeEvent
    public void onPlayerLogout(PlayerLoggedOutEvent event)
    {
        ChunkLoadingCallback.onPlayerLogout(event.player);
    }
    */

    @SubscribeEvent
    public void onConnectionReceived(ServerConnectionFromClientEvent event)
    {
        event.manager.scheduleOutboundPacket(ConnectionPacketAR.createMothershipPacket());
        // config packet
        event.manager.scheduleOutboundPacket(ConnectionPacketAR.createConfigPacket());
    }

    @SubscribeEvent
    public void onConnectionOpened(ClientConnectedToServerEvent event)
    {
        // stolen from GC...
        if (!event.isLocal)
        {
            clientConnected = true;
        }
        MapUtil.resetClient();
    }


    @SubscribeEvent
    public void onConnectionClosed(ClientDisconnectionFromServerEvent event)
    {
        if (clientConnected)
        {
            clientConnected = false;
            // unregister motherships here
            if(TickHandlerServer.mothershipData != null) {
                TickHandlerServer.mothershipData.unregisterAllMotherships();
                TickHandlerServer.mothershipData = null;
            }
            /*
            WorldUtil.unregisterPlanets();
            WorldUtil.unregisterSpaceStations();
            ConfigManagerCore.restoreClientConfigOverrideable();
            */
        }
    }

}
