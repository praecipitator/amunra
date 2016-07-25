package de.katzenpapst.amunra.network.packet;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.RecipeHelper;
import de.katzenpapst.amunra.ShuttleTeleportHelper;
import de.katzenpapst.amunra.client.gui.GuiShuttleSelection;
import de.katzenpapst.amunra.mothership.Mothership;
import de.katzenpapst.amunra.mothership.MothershipWorldData;
import de.katzenpapst.amunra.tick.TickHandlerServer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.Satellite;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStatsClient;
// import micdoodle8.mods.galacticraft.core.network.IPacket;
import micdoodle8.mods.galacticraft.core.network.NetworkUtil;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
//import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.GCLog;
import micdoodle8.mods.galacticraft.core.util.PlayerUtil;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import micdoodle8.mods.galacticraft.core.network.IPacket;

public class PacketSimpleAR extends Packet implements IPacket {

    public static enum EnumSimplePacket
    {
        // SERVER
        // S_RESPAWN_PLAYER(Side.SERVER, String.class),
        S_TELEPORT_SHUTTLE(Side.SERVER, Integer.class),
        S_CREATE_MOTHERSHIP(Side.SERVER, String.class),

        // CLIENT
        // more like "open shuttle gui"
        C_OPEN_SHUTTLE_GUI(Side.CLIENT, String.class, String.class),
        C_UPDATE_MOTHERSHIP_LIST(Side.CLIENT, NBTTagCompound.class),
        C_NEW_MOTHERSHIP_CREATED(Side.CLIENT, NBTTagCompound.class);


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

    public PacketSimpleAR() {
        // TODO Auto-generated constructor stub
    }

    public PacketSimpleAR(EnumSimplePacket packetType, Object[] data)
    {
        this(packetType, Arrays.asList(data));
    }

    public PacketSimpleAR(EnumSimplePacket packetType, List<Object> data)
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
        EntityClientPlayerMP playerBaseClient = null;
        GCPlayerStatsClient stats = null;

        if (player instanceof EntityClientPlayerMP)
        {
            playerBaseClient = (EntityClientPlayerMP) player;
            stats = GCPlayerStatsClient.get(playerBaseClient);
        } else {
            return;
        }

        NBTTagCompound nbt;

        switch(this.type) {
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
                    // TODO FIX!!!
                    CelestialBody celestialBody = ShuttleTeleportHelper.getReachableCelestialBodiesForName(str);

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
                    if (!(FMLClientHandler.instance().getClient().currentScreen instanceof GuiShuttleSelection))
                    {
                        GuiShuttleSelection gui = new GuiShuttleSelection(false, possibleCelestialBodies);
                        gui.spaceStationMap = spaceStationData;
                        FMLClientHandler.instance().getClient().displayGuiScreen(gui);
                    }
                    else
                    {
                        ((GuiShuttleSelection) FMLClientHandler.instance().getClient().currentScreen).possibleBodies = possibleCelestialBodies;
                        ((GuiShuttleSelection) FMLClientHandler.instance().getClient().currentScreen).spaceStationMap = spaceStationData;
                    }
                }
            }
            break;
        case C_UPDATE_MOTHERSHIP_LIST:
            // I think this should only be sent on login. maybe rename it to C_INITIAL_MOTHERSHIP_LIST_UPDATE or so?
            nbt = (NBTTagCompound)this.data.get(0);
            MothershipWorldData mData = TickHandlerServer.mothershipData; //AmunRa.instance.getMothershipData();
            if(mData == null) {
                mData = new MothershipWorldData(MothershipWorldData.saveDataID);
            }
            mData.readFromNBT(nbt);

            TickHandlerServer.mothershipData = mData;

            if (FMLClientHandler.instance().getClient().currentScreen instanceof GuiShuttleSelection) {
                ((GuiShuttleSelection)FMLClientHandler.instance().getClient().currentScreen).mothershipListUpdated();
            }

            break;
        case C_NEW_MOTHERSHIP_CREATED:
            nbt = (NBTTagCompound)this.data.get(0);

            Mothership newShip = Mothership.createFromNBT(nbt);

            TickHandlerServer.mothershipData.addMothership(newShip);
            if (FMLClientHandler.instance().getClient().currentScreen instanceof GuiShuttleSelection) {
                ((GuiShuttleSelection)FMLClientHandler.instance().getClient().currentScreen).newMothershipCreated(newShip);
            }

            break;
        default:
            break;
        } // end of case
    }

    @Override
    public void handleServerSide(EntityPlayer player)
    {

        EntityPlayerMP playerBase = PlayerUtil.getPlayerBaseServerFromPlayer(player, false);

        if (playerBase == null)
        {
            return;
        }

        GCPlayerStats stats = GCPlayerStats.get(playerBase);


        switch (this.type)
        {
        case S_TELEPORT_SHUTTLE:    // S_TELEPORT_ENTITY
            try
            {
                //final WorldProvider provider = WorldUtil.getProviderForNameServer((String) this.data.get(0));
                final Integer dim = ((Integer) this.data.get(0));
                GCLog.info("Will teleport to (" + dim.toString() + ")");

                if (playerBase.worldObj instanceof WorldServer)
                {
                    final WorldServer world = (WorldServer) playerBase.worldObj;
                    // replace this now
                    ShuttleTeleportHelper.transferEntityToDimension(playerBase, dim, world);
                }

                stats.teleportCooldown = 10;
                GalacticraftCore.packetPipeline.sendTo(new PacketSimple(PacketSimple.EnumSimplePacket.C_CLOSE_GUI, new Object[] { }), playerBase);
            }
            catch (final Exception e)
            {
                GCLog.severe("Error occurred when attempting to transfer entity to dimension: " + (Integer) this.data.get(0));
                e.printStackTrace();
            }
            break;
        case S_CREATE_MOTHERSHIP:

            String bodyName = (String) this.data.get(0);

            CelestialBody bodyToOrbit = Mothership.findBodyByName(bodyName);


            if (
                    Mothership.canBeOrbited(bodyToOrbit) &&
                    (
                            AmunRa.instance.confMaxMotherships < 0 ||
                            TickHandlerServer.mothershipData.getNumMothershipsForPlayer(playerBase.getUniqueID().toString()) < AmunRa.instance.confMaxMotherships)
                    )
            {
                // the matches consumes the actual items

                if (playerBase.capabilities.isCreativeMode || RecipeHelper.mothershipRecipe.matches(playerBase, true))
                {
                    Mothership newShip = TickHandlerServer.mothershipData.registerNewMothership(playerBase.getUniqueID().toString(), bodyToOrbit);

                }
            }
            break;
        default:
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
         */
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            this.handleClientSide(FMLClientHandler.instance().getClientPlayerEntity());
        }/**/
    }

}
