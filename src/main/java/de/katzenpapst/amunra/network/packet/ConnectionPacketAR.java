package de.katzenpapst.amunra.network.packet;

import java.io.IOException;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.mothership.MothershipWorldData;
import de.katzenpapst.amunra.tick.TickHandlerServer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import micdoodle8.mods.galacticraft.core.network.NetworkUtil;
import micdoodle8.mods.galacticraft.core.util.GCLog;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;

public class ConnectionPacketAR
{
    public static final String CHANNEL = "amunra";
    public static FMLEventChannel bus;

    public static final byte ID_MOTHERSHIP_LIST = (byte) 150;

    public ConnectionPacketAR() {
        // TODO Auto-generated constructor stub
    }

    public void handle(ByteBuf payload, EntityPlayer player)
    {
        int packetId = payload.readByte();
        NBTTagCompound nbt;
        // now try this
        try {
            nbt = NetworkUtil.readNBTTagCompound(payload);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }


        //List<Integer> data = new ArrayList<Integer>();
        switch (packetId)
        {
        case ID_MOTHERSHIP_LIST:

            if(TickHandlerServer.mothershipData == null) {
                TickHandlerServer.mothershipData = new MothershipWorldData(MothershipWorldData.saveDataID);
            }

            TickHandlerServer.mothershipData.readFromNBT(nbt);
            //MothershipWorldData.preRegisterMothershipDimensions(data);
            //WorldUtil.decodePlanetsListClient(data);
            break;
        default:
        }
        /*if (payload.readInt() != 3519)
        {
            GCLog.severe("Packet completion problem for connection packet " + packetId + " - maybe the player's Galacticraft version does not match the server version?");
        }*/
    }

    public static FMLProxyPacket createMothershipPacket()
    {
        //ArrayList<Integer> data = new ArrayList();
        ByteBuf payload = Unpooled.buffer();

        payload.writeByte(ID_MOTHERSHIP_LIST);
        //payload.writeInt(ID_MOTHERSHIP_LIST);

        NBTTagCompound nbt = new NBTTagCompound ();
        TickHandlerServer.mothershipData.writeToNBT(nbt);

        try {
            NetworkUtil.writeNBTTagCompound(nbt, payload);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new FMLProxyPacket(payload, CHANNEL);
    }



    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onPacketData(FMLNetworkEvent.ClientCustomPacketEvent event)
    {
        FMLProxyPacket pkt = event.packet;

        onFMLProxyPacketData(event.manager, pkt, Minecraft.getMinecraft().thePlayer);
    }

    @SubscribeEvent
    public void onPacketData(FMLNetworkEvent.ServerCustomPacketEvent event)
    {
        FMLProxyPacket pkt = event.packet;

        onFMLProxyPacketData(event.manager, pkt, ((NetHandlerPlayServer)event.handler).playerEntity);
    }

    public void onFMLProxyPacketData(NetworkManager manager, FMLProxyPacket packet, EntityPlayer player)
    {
        try {
            if ((packet == null) || (packet.payload() == null)) throw new RuntimeException("Empty packet sent to Amunra channel");
            ByteBuf data = packet.payload();
            this.handle(data, player);
        } catch (Exception e) {
            GCLog.severe("Amunra login packet handler: Failed to read packet");
            GCLog.severe(e.toString());
            e.printStackTrace();
        }
    }

}
