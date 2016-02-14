package de.katzenpapst.amunra.network;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.Satellite;
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStatsClient;
import micdoodle8.mods.galacticraft.core.network.IPacket;
import micdoodle8.mods.galacticraft.core.network.NetworkUtil;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;

public class PacketSimple extends Packet implements IPacket {
    
    public static enum EnumSimplePacket
    {
        // SERVER
        // S_RESPAWN_PLAYER(Side.SERVER, String.class),
        // CLIENT
        // more like "open shuttle gui"
        C_OPEN_SHUTTLE_GUI(Side.CLIENT, String.class, String.class);
        
        private Side targetSide;
        private Class<?>[] decodeAs;

        private EnumSimplePacket(Side targetSide, Class<?>... decodeAs)
        {
            this.targetSide = targetSide;
            this.decodeAs = decodeAs;
        }

        public Side getTargetSide()
        {
            return this.targetSide;
        }

        public Class<?>[] getDecodeClasses()
        {
            return this.decodeAs;
        }
    }
    
    private EnumSimplePacket type;
    private List<Object> data;
    static private String spamCheckString;

    public PacketSimple() {
        // TODO Auto-generated constructor stub
    }
    
    public PacketSimple(EnumSimplePacket packetType, Object[] data)
    {
        this(packetType, Arrays.asList(data));
    }

    public PacketSimple(EnumSimplePacket packetType, List<Object> data)
    {
        if (packetType.getDecodeClasses().length != data.size())
        {
            // GCLog.info("Simple Packet Core found data length different than packet type");
            new RuntimeException().printStackTrace();
        }

        this.type = packetType;
        this.data = data;
    }
    
    @Override
    public void encodeInto(ChannelHandlerContext context, ByteBuf buffer)
    {
        buffer.writeInt(this.type.ordinal());

        try
        {
            NetworkUtil.encodeData(buffer, this.data);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void decodeInto(ChannelHandlerContext context, ByteBuf buffer)
    {
        this.type = EnumSimplePacket.values()[buffer.readInt()];

        try
        {
            if (this.type.getDecodeClasses().length > 0)
            {
                this.data = NetworkUtil.decodeData(this.type.getDecodeClasses(), buffer);
            }
            if (buffer.readableBytes() > 0)
            {
                // GCLog.severe("Galacticraft packet length problem for packet type " + this.type.toString());
            }
        }
        catch (Exception e)
        {
            System.err.println("[Galacticraft] Error handling simple packet type: " + this.type.toString() + " " + buffer.toString());
            e.printStackTrace();
        }
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void handleClientSide(EntityPlayer player)
    {
        
    }
    
    @Override
    public void handleServerSide(EntityPlayer player)
    {
        EntityClientPlayerMP playerBaseClient = null;
        GCPlayerStatsClient stats = null;

        if (player instanceof EntityClientPlayerMP)
        {
            playerBaseClient = (EntityClientPlayerMP) player;
            stats = GCPlayerStatsClient.get(playerBaseClient);
        } else {
            return;
        }
        
        switch (this.type)
        {
        case C_OPEN_SHUTTLE_GUI:
            if (String.valueOf(this.data.get(0)).equals(FMLClientHandler.instance().getClient().thePlayer.getGameProfile().getName()))
            {
                String dimensionList = (String) this.data.get(1);
                /*if (ConfigManagerCore.enableDebug)
                {
                    if (!dimensionList.equals(PacketSimple.spamCheckString))
                    {
                        GCLog.info("DEBUG info: " + dimensionList);
                        PacketSimple.spamCheckString = new String(dimensionList);
                    }
                }*/
                final String[] destinations = dimensionList.split("\\?");
                List<CelestialBody> possibleCelestialBodies = Lists.newArrayList();
                Map<Integer, Map<String, GuiCelestialSelection.StationDataGUI>> spaceStationData = Maps.newHashMap();
//                Map<String, String> spaceStationNames = Maps.newHashMap();
//                Map<String, Integer> spaceStationIDs = Maps.newHashMap();
//                Map<String, Integer> spaceStationHomes = Maps.newHashMap();

                for (String str : destinations)
                {
                    CelestialBody celestialBody = WorldUtil.getReachableCelestialBodiesForName(str);

                    if (celestialBody == null && str.contains("$"))
                    {
                        String[] values = str.split("\\$");

                        int homePlanetID = Integer.parseInt(values[4]);

                        for (Satellite satellite : GalaxyRegistry.getRegisteredSatellites().values())
                        {
                            if (satellite.getParentPlanet().getDimensionID() == homePlanetID)
                            {
                                celestialBody = satellite;
                                break;
                            }
                        }

                        if (!spaceStationData.containsKey(homePlanetID))
                        {
                            spaceStationData.put(homePlanetID, new HashMap<String, GuiCelestialSelection.StationDataGUI>());
                        }

                        spaceStationData.get(homePlanetID).put(values[1], new GuiCelestialSelection.StationDataGUI(values[2], Integer.parseInt(values[3])));

//                        spaceStationNames.put(values[1], values[2]);
//                        spaceStationIDs.put(values[1], Integer.parseInt(values[3]));
//                        spaceStationHomes.put(values[1], Integer.parseInt(values[4]));
                    }

                    if (celestialBody != null)
                    {
                        possibleCelestialBodies.add(celestialBody);
                    }
                }

                if (FMLClientHandler.instance().getClient().theWorld != null)
                {
                    if (!(FMLClientHandler.instance().getClient().currentScreen instanceof GuiCelestialSelection))
                    {
                        GuiCelestialSelection gui = new GuiCelestialSelection(false, possibleCelestialBodies);
                        gui.spaceStationMap = spaceStationData;
//                        gui.spaceStationNames = spaceStationNames;
//                        gui.spaceStationIDs = spaceStationIDs;
                        FMLClientHandler.instance().getClient().displayGuiScreen(gui);
                    }
                    else
                    {
                        ((GuiCelestialSelection) FMLClientHandler.instance().getClient().currentScreen).possibleBodies = possibleCelestialBodies;
                        ((GuiCelestialSelection) FMLClientHandler.instance().getClient().currentScreen).spaceStationMap = spaceStationData;
//                        ((GuiCelestialSelection) FMLClientHandler.instance().getClient().currentScreen).spaceStationNames = spaceStationNames;
//                        ((GuiCelestialSelection) FMLClientHandler.instance().getClient().currentScreen).spaceStationIDs = spaceStationIDs;
                    }
                }
            }
            break;
        }
    }

    @Override
    public void readPacketData(PacketBuffer var1)
    {
        this.decodeInto(null, var1);
    }

    @Override
    public void writePacketData(PacketBuffer var1)
    {
        this.encodeInto(null, var1);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void processPacket(INetHandler var1)
    {
        /*
        if (this.type != EnumSimplePacket.C_UPDATE_SPACESTATION_LIST && this.type != EnumSimplePacket.C_UPDATE_PLANETS_LIST && this.type != EnumSimplePacket.C_UPDATE_CONFIGS)
        {
            return;
        }

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            this.handleClientSide(FMLClientHandler.instance().getClientPlayerEntity());
        }*/
    }

}
