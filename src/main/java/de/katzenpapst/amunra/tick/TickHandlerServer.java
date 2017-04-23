package de.katzenpapst.amunra.tick;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.entity.spaceship.EntityShuttle;
import de.katzenpapst.amunra.helper.ShuttleTeleportHelper;
import de.katzenpapst.amunra.mob.DamageSourceAR;
import de.katzenpapst.amunra.mothership.Mothership;
import de.katzenpapst.amunra.mothership.MothershipWorldData;
import de.katzenpapst.amunra.mothership.MothershipWorldProvider;
import de.katzenpapst.amunra.world.ShuttleDockHandler;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityAutoRocket;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase.EnumLaunchPhase;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;

public class TickHandlerServer {

    public static MothershipWorldData mothershipData;
    public static ShuttleDockHandler dockData;



    public static void restart() {
        mothershipData = null;
        dockData = null;
        /*
        if(FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            AmunRa.instance.setClientMothershipData(null);
        }*/
    }

    private boolean clientConnected;

    public TickHandlerServer() {
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event)
    {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) return;
        if (event.phase == TickEvent.Phase.START)
        {
            if (TickHandlerServer.dockData == null) {
                World world = server.worldServerForDimension(0);
                TickHandlerServer.dockData = (ShuttleDockHandler) world.mapStorage.loadData(ShuttleDockHandler.class, ShuttleDockHandler.saveDataID);
                // why am I doublechecking this?
                if (TickHandlerServer.dockData == null) {
                    TickHandlerServer.dockData = new ShuttleDockHandler(ShuttleDockHandler.saveDataID);
                    world.mapStorage.setData(ShuttleDockHandler.saveDataID, TickHandlerServer.dockData );
                }
            }
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
            } else {
                //server.worldServerForDimension(0).getTotalWorldTime()
                // this works
                // tick all the motherships
                TickHandlerServer.mothershipData.tickAllMotherships();
            }
        }
    }


    @SubscribeEvent
    public void onWorldTick(WorldTickEvent event)
    {
        if (event.phase == Phase.START)
        {
            final WorldServer world = (WorldServer) event.world;

            if (world.provider instanceof MothershipWorldProvider)
            {
                // ((MothershipWorldProvider)world.provider).update();
                final Object[] entityList = world.loadedEntityList.toArray();

                for (final Object o : entityList)
                {
                    if (o instanceof Entity)
                    {
                        final Entity e = (Entity) o;
                        // failsafe?
                        if (e.worldObj.provider instanceof MothershipWorldProvider)
                        {
                            if(e.posY < 0) {
                                CelestialBody parent = ((MothershipWorldProvider)e.worldObj.provider).getParent();
                                if(parent == null) {
                                    // jumped off mid-transit
                                    if(e instanceof EntityLivingBase) {
                                        ((EntityLivingBase)e).attackEntityFrom(DamageSourceAR.dsFallOffShip, 9001);
                                    } else {
                                        e.worldObj.removeEntity(e);
                                    }
                                } else {

                                    if(!parent.getReachable() || (parent.getTierRequirement() > AmunRa.config.mothershipMaxTier)) {
                                        // crash into
                                        if(e instanceof EntityLivingBase) {
                                            ((EntityLivingBase)e).attackEntityFrom(DamageSourceAR.getDSCrashIntoPlanet(parent), 9001);
                                        } else {
                                            e.worldObj.removeEntity(e);
                                        }
                                    } else {
                                        if(e instanceof EntityPlayerMP && e.ridingEntity instanceof EntityShuttle) {
                                            sendPlayerInShuttleToPlanet((EntityPlayerMP)e, (EntityShuttle)e.ridingEntity, world, parent.getDimensionID());
                                        } else if(e instanceof EntityShuttle && e.riddenByEntity instanceof EntityPlayerMP) {
                                            sendPlayerInShuttleToPlanet((EntityPlayerMP)e.riddenByEntity, (EntityShuttle)e, world, parent.getDimensionID());
                                        } else {
                                            // go there naked, as GC intended
                                            WorldUtil.transferEntityToDimension(e, parent.getDimensionID(), world, false, null);
                                        }
                                    }
                                }
                            }  else { // if(e.posY < 0) {
                                if(e instanceof EntityAutoRocket) {
                                    EntityAutoRocket rocket = (EntityAutoRocket)e;
                                    if(!(rocket instanceof EntityShuttle)) {
                                        // prevent them from launching, ever

                                        if(rocket.launchPhase == EnumLaunchPhase.IGNITED.ordinal()) {
                                            rocket.cancelLaunch();
                                        } else if(rocket.launchPhase == EnumLaunchPhase.LAUNCHED.ordinal()) {
                                            rocket.dropShipAsItem();
                                        }
                                    } else {
                                        Mothership ship = (Mothership) ((MothershipWorldProvider)e.worldObj.provider).getCelestialBody();
                                        if(ship.isInTransit()) {
                                            if(rocket.launchPhase == EnumLaunchPhase.IGNITED.ordinal()) {
                                                rocket.cancelLaunch();
                                            } else if(rocket.launchPhase == EnumLaunchPhase.LAUNCHED.ordinal()) {
                                                rocket.dropShipAsItem();
                                            }
                                        }
                                    }
                                }
                            }



                        } // if (e.worldObj.provider instanceof MothershipWorldProvider)
                    } // if (o instanceof Entity)
                } // for (final Object o : entityList)
            } // if (world.provider instanceof MothershipWorldProvider)
        } // (event.phase == Phase.START)
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent event)
    {
        WorldProvider provider = event.player.getEntityWorld().provider;

        if(provider instanceof MothershipWorldProvider) {
            ((MothershipWorldProvider)provider).sendPacketsToClient((EntityPlayerMP) event.player);
        }

    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerChangedDimensionEvent event)
    {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        WorldServer world = server.worldServerForDimension(event.toDim);
        if(world.provider instanceof MothershipWorldProvider) {
            ((MothershipWorldProvider)world.provider).sendPacketsToClient((EntityPlayerMP) event.player);
        }
        //event.
    }

    protected void sendPlayerInShuttleToPlanet(EntityPlayerMP player, EntityShuttle shuttle, World world, int dimensionID) {
        if(world.isRemote) {
            return;
        }
        // player.dismountEntity(shuttle);
        shuttle.riddenByEntity = null;
        player.ridingEntity = null;
        shuttle.setGCPlayerStats(player);
        shuttle.setDead();

        ShuttleTeleportHelper.transferEntityToDimension(player, dimensionID, (WorldServer) world);
    }



}
