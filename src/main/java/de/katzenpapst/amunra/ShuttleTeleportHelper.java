package de.katzenpapst.amunra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.FMLCommonHandler;
import de.katzenpapst.amunra.mothership.Mothership;
import de.katzenpapst.amunra.mothership.MothershipWorldData;
import de.katzenpapst.amunra.tick.TickHandlerServer;
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry;
import micdoodle8.mods.galacticraft.api.entity.IWorldTransferCallback;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.Moon;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.Satellite;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityAutoRocket;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.api.world.ITeleportType;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.dimension.SpaceStationWorldData;
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderOrbit;
import micdoodle8.mods.galacticraft.core.entities.EntityCelestialFake;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerHandler;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.items.ItemParaChute;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import micdoodle8.mods.galacticraft.core.tile.TileEntityTelemetry;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.GCLog;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

public class ShuttleTeleportHelper {

    public ShuttleTeleportHelper() {
        // TODO Auto-generated constructor stub
    }

    public static Entity transferEntityToDimension(Entity entity, int dimensionID, WorldServer world)
    {
        return WorldUtil.transferEntityToDimension(entity, dimensionID, world, true, null);
    }

    public static Entity transferEntityToDimension(Entity entity, int dimensionID, WorldServer world, boolean transferInv, EntityAutoRocket ridingRocket)
    {
        if (!world.isRemote)
        {
            //GalacticraftCore.packetPipeline.sendToAll(new PacketSimple(EnumSimplePacket.C_UPDATE_PLANETS_LIST, WorldUtil.getPlanetList()));

            MinecraftServer mcServer = FMLCommonHandler.instance().getMinecraftServerInstance();

            if (mcServer != null)
            {
                final WorldServer var6 = mcServer.worldServerForDimension(dimensionID);

                if (var6 == null)
                {
                    System.err.println("Cannot Transfer Entity to Dimension: Could not get World for Dimension " + dimensionID);
                    return null;
                }

                final ITeleportType type = GalacticraftRegistry.getTeleportTypeForDimension(var6.provider.getClass());

                if (type != null)
                {
                    return teleportEntity(var6, entity, dimensionID, type, transferInv, ridingRocket);
                }
            }
        }

        return null;
    }

    private static Entity teleportEntity(World worldNew, Entity entity, int dimID, ITeleportType type, boolean transferInv, EntityAutoRocket ridingRocket)
    {
        if (entity.ridingEntity != null)
        {
            if (entity.ridingEntity instanceof EntitySpaceshipBase)
                entity.mountEntity(entity.ridingEntity);
            else if (entity.ridingEntity instanceof EntityCelestialFake)
            {
                entity.ridingEntity.setDead();
                entity.mountEntity(null);
            }
        }

        boolean dimChange = entity.worldObj != worldNew;
        entity.worldObj.updateEntityWithOptionalForce(entity, false);
        EntityPlayerMP player = null;
        Vector3 spawnPos = null;
        int oldDimID = entity.worldObj.provider.dimensionId;

        if (ridingRocket != null)
        {
            ArrayList<TileEntityTelemetry> tList = ridingRocket.getTelemetry();
            NBTTagCompound nbt = new NBTTagCompound();
            ridingRocket.isDead = false;
            ridingRocket.riddenByEntity = null;
            ridingRocket.writeToNBTOptional(nbt);

            ((WorldServer) ridingRocket.worldObj).getEntityTracker().removeEntityFromAllTrackingPlayers(ridingRocket);
            ridingRocket.worldObj.loadedEntityList.remove(ridingRocket);
            ridingRocket.worldObj.onEntityRemoved(ridingRocket);

            ridingRocket = (EntityAutoRocket) EntityList.createEntityFromNBT(nbt, worldNew);

            if (ridingRocket != null)
            {
                ridingRocket.setWaitForPlayer(true);

                if (ridingRocket instanceof IWorldTransferCallback)
                {
                    ((IWorldTransferCallback) ridingRocket).onWorldTransferred(worldNew);
                }
            }
        }

        if (dimChange)
        {
            if (entity instanceof EntityPlayerMP)
            {
                player = (EntityPlayerMP) entity;
                World worldOld = player.worldObj;
                if (ConfigManagerCore.enableDebug)
                {
                    try {
                        GCLog.info("DEBUG: Attempting to remove player from old dimension " + oldDimID);
                        ((WorldServer) worldOld).getPlayerManager().removePlayer(player);
                        GCLog.info("DEBUG: Successfully removed player from old dimension " + oldDimID);
                    } catch (Exception e) { e.printStackTrace(); }
                }
                else
                {
                    try {
                        ((WorldServer) worldOld).getPlayerManager().removePlayer(player);
                    } catch (Exception e) {  }
                }

                player.closeScreen();
                GCPlayerStats stats = GCPlayerStats.get(player);
                stats.usingPlanetSelectionGui = false;

                player.dimension = dimID;
                if (ConfigManagerCore.enableDebug)
                {
                    GCLog.info("DEBUG: Sending respawn packet to player for dim " + dimID);
                }
                player.playerNetServerHandler.sendPacket(new S07PacketRespawn(dimID, player.worldObj.difficultySetting, player.worldObj.getWorldInfo().getTerrainType(), player.theItemInWorldManager.getGameType()));

                if (worldNew.provider instanceof WorldProviderOrbit) {
                    if (WorldUtil.registeredSpaceStations.containsKey(dimID))
                        //TODO This has never been effective before due to the earlier bug - what does it actually do?
                    {
                        NBTTagCompound var2 = new NBTTagCompound();
                        SpaceStationWorldData.getStationData(worldNew, dimID, player).writeToNBT(var2);
                        GalacticraftCore.packetPipeline.sendTo(new PacketSimple(EnumSimplePacket.C_UPDATE_SPACESTATION_DATA, new Object[] { dimID, var2 }), player);
                    }
                }

                worldOld.playerEntities.remove(player);
                worldOld.updateAllPlayersSleepingFlag();
                if (player.addedToChunk && worldOld.getChunkProvider().chunkExists(player.chunkCoordX, player.chunkCoordZ))
                {
                    Chunk chunkOld = worldOld.getChunkFromChunkCoords(player.chunkCoordX, player.chunkCoordZ);
                    chunkOld.removeEntity(player);
                    chunkOld.isModified = true;
                }
                worldOld.loadedEntityList.remove(player);
                worldOld.onEntityRemoved(player);

                if (worldNew.provider instanceof WorldProviderOrbit) GalacticraftCore.packetPipeline.sendTo(new PacketSimple(EnumSimplePacket.C_RESET_THIRD_PERSON, new Object[] { }), player);
                worldNew.spawnEntityInWorld(entity);
                entity.setWorld(worldNew);

                spawnPos = type.getPlayerSpawnLocation((WorldServer) entity.worldObj, player);
                ChunkCoordIntPair pair = worldNew.getChunkFromChunkCoords(spawnPos.intX(), spawnPos.intZ()).getChunkCoordIntPair();
                if (ConfigManagerCore.enableDebug)
                {
                    GCLog.info("DEBUG: Loading first chunk in new dimension.");
                }
                ((WorldServer) worldNew).theChunkProviderServer.loadChunk(pair.chunkXPos, pair.chunkZPos);
                //entity.setLocationAndAngles(spawnPos.x, spawnPos.y, spawnPos.z, entity.rotationYaw, entity.rotationPitch);
                worldNew.updateEntityWithOptionalForce(entity, false);
                entity.setLocationAndAngles(spawnPos.x, spawnPos.y, spawnPos.z, entity.rotationYaw, entity.rotationPitch);

                player.mcServer.getConfigurationManager().func_72375_a(player, (WorldServer) worldNew);
                player.playerNetServerHandler.setPlayerLocation(spawnPos.x, spawnPos.y, spawnPos.z, entity.rotationYaw, entity.rotationPitch);
                //worldNew.updateEntityWithOptionalForce(entity, false);

                GCLog.info("Server attempting to transfer player " + player.getGameProfile().getName() + " to dimension " + worldNew.provider.dimensionId);

                player.theItemInWorldManager.setWorld((WorldServer) worldNew);
                player.mcServer.getConfigurationManager().updateTimeAndWeatherForPlayer(player, (WorldServer) worldNew);
                player.mcServer.getConfigurationManager().syncPlayerInventory(player);

                for (Object o : player.getActivePotionEffects())
                {
                    PotionEffect var10 = (PotionEffect) o;
                    player.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(player.getEntityId(), var10));
                }

                player.playerNetServerHandler.sendPacket(new S1FPacketSetExperience(player.experience, player.experienceTotal, player.experienceLevel));
            }
            else
                //Non-player entity transfer i.e. it's an EntityCargoRocket
            {
                ArrayList<TileEntityTelemetry> tList = null;
                if (entity instanceof EntitySpaceshipBase)
                {
                    tList = ((EntitySpaceshipBase)entity).getTelemetry();
                }
                removeEntityFromWorld(entity.worldObj, entity, true);

                NBTTagCompound nbt = new NBTTagCompound();
                entity.isDead = false;
                entity.writeToNBTOptional(nbt);
                entity.isDead = true;
                entity = EntityList.createEntityFromNBT(nbt, worldNew);

                if (entity == null)
                {
                    return null;
                }

                if (entity instanceof IWorldTransferCallback)
                {
                    ((IWorldTransferCallback) entity).onWorldTransferred(worldNew);
                }

                worldNew.spawnEntityInWorld(entity);
                entity.setWorld(worldNew);
                worldNew.updateEntityWithOptionalForce(entity, false);

                if (tList != null && tList.size() > 0)
                {
                    for (TileEntityTelemetry t : tList)
                    {
                        t.addTrackedEntity(entity);
                    }
                }
            }
        }
        else
        {
            //Same dimension player transfer
            if (entity instanceof EntityPlayerMP)
            {
                player = (EntityPlayerMP) entity;
                player.closeScreen();
                GCPlayerStats stats = GCPlayerStats.get(player);
                stats.usingPlanetSelectionGui = false;

                if (worldNew.provider instanceof WorldProviderOrbit) GalacticraftCore.packetPipeline.sendTo(new PacketSimple(EnumSimplePacket.C_RESET_THIRD_PERSON, new Object[] { }), player);
                worldNew.updateEntityWithOptionalForce(entity, false);

                spawnPos = type.getPlayerSpawnLocation((WorldServer) entity.worldObj, (EntityPlayerMP) entity);
                player.playerNetServerHandler.setPlayerLocation(spawnPos.x, spawnPos.y, spawnPos.z, entity.rotationYaw, entity.rotationPitch);
                entity.setLocationAndAngles(spawnPos.x, spawnPos.y, spawnPos.z, entity.rotationYaw, entity.rotationPitch);
                worldNew.updateEntityWithOptionalForce(entity, false);

                GCLog.info("Server attempting to transfer player " + player.getGameProfile().getName() + " within same dimension " + worldNew.provider.dimensionId);
            }

            //Cargo rocket does not needs its location setting here, it will do that itself
        }

        //Update PlayerStatsGC
        if (player != null)
        {
            GCPlayerStats playerStats = GCPlayerStats.get(player);
            if (ridingRocket == null && type.useParachute() && playerStats.extendedInventory.getStackInSlot(4) != null && playerStats.extendedInventory.getStackInSlot(4).getItem() instanceof ItemParaChute)
            {
                GCPlayerHandler.setUsingParachute(player, playerStats, true);
            }
            else
            {
                GCPlayerHandler.setUsingParachute(player, playerStats, false);
            }

            if (playerStats.rocketStacks != null && playerStats.rocketStacks.length > 0)
            {
                for (int stack = 0; stack < playerStats.rocketStacks.length; stack++)
                {
                    if (transferInv)
                    {
                        if (playerStats.rocketStacks[stack] == null)
                        {
                            if (stack == playerStats.rocketStacks.length - 1)
                            {
                                if (playerStats.rocketItem != null)
                                {
                                    playerStats.rocketStacks[stack] = new ItemStack(playerStats.rocketItem, 1, playerStats.rocketType);
                                }
                            }
                            else if (stack == playerStats.rocketStacks.length - 2)
                            {
                                playerStats.rocketStacks[stack] = playerStats.launchpadStack;
                                playerStats.launchpadStack = null;
                            }
                        }
                    }
                    else
                    {
                        playerStats.rocketStacks[stack] = null;
                    }
                }
            }

            if (transferInv && playerStats.chestSpawnCooldown == 0)
            {
                playerStats.chestSpawnVector = type.getParaChestSpawnLocation((WorldServer) entity.worldObj, player, new Random());
                playerStats.chestSpawnCooldown = 200;
            }
        }

        //If in a rocket (e.g. with launch controller) set the player to the rocket's position instead of the player's spawn position
        if (ridingRocket != null)
        {
            entity.setPositionAndRotation(ridingRocket.posX, ridingRocket.posY, ridingRocket.posZ, 0, 0);
            worldNew.updateEntityWithOptionalForce(entity, true);

            worldNew.spawnEntityInWorld(ridingRocket);
            ridingRocket.setWorld(worldNew);

            worldNew.updateEntityWithOptionalForce(ridingRocket, true);
            entity.mountEntity(ridingRocket);
        }
        else if (spawnPos != null)
        {
            entity.setLocationAndAngles(spawnPos.x, spawnPos.y, spawnPos.z, entity.rotationYaw, entity.rotationPitch);
        }

        //Spawn in a lander if appropriate
        if (entity instanceof EntityPlayerMP)
        {
            FMLCommonHandler.instance().firePlayerChangedDimensionEvent((EntityPlayerMP) entity, oldDimID, dimID);
            type.onSpaceDimensionChanged(worldNew, (EntityPlayerMP) entity, ridingRocket != null);
        }

        return entity;
    }

    private static void removeEntityFromWorld(World var0, Entity var1, boolean directlyRemove)
    {
        if (var1 instanceof EntityPlayer)
        {
            final EntityPlayer var2 = (EntityPlayer) var1;
            var2.closeScreen();
            var0.playerEntities.remove(var2);
            var0.updateAllPlayersSleepingFlag();
            final int var3 = var1.chunkCoordX;
            final int var4 = var1.chunkCoordZ;

            if (var1.addedToChunk && var0.getChunkProvider().chunkExists(var3, var4))
            {
                var0.getChunkFromChunkCoords(var3, var4).removeEntity(var1);
                var0.getChunkFromChunkCoords(var3, var4).isModified = true;
            }

            if (directlyRemove)
            {
                var0.loadedEntityList.remove(var1);
                var0.onEntityRemoved(var1);
            }
        }

        var1.isDead = false;
    }

    /**
     * Generates an array for usage in GuiCelestialSelection containing the given planet and all it's children
     *
     * @param playerBase
     * @param body
     * @return
     */
    private static HashMap<String, Integer> getArrayOfChildren(EntityPlayerMP playerBase, Planet body)
    {
        HashMap<String, Integer> result = new HashMap<String, Integer>();

        int planetId = body.getDimensionID();
        // check if it's the ow?
        if(body.getReachable()) {
            // add the body itself
            result.put(body.getName(), planetId);
            // seems like you can only have sats for reachable bodies
            // try the sats
            //List<Satellite> sats = GalaxyRegistry.getSatellitesForCelestialBody(body);
            for (Integer element : WorldUtil.registeredSpaceStations.keySet()) {
                final SpaceStationWorldData data = SpaceStationWorldData.getStationData(playerBase.worldObj, element, null);
                if(data.getHomePlanet() == planetId) {
                    // try to get the body? I hope this works
                    CelestialBody celestialBody = WorldUtil.getReachableCelestialBodiesForDimensionID(element);
                    if(celestialBody == null) {
                        celestialBody = GalacticraftCore.satelliteSpaceStation;
                    }
                    // weird string?
                    // map.put(celestialBody.getName() + "$" + data.getOwner() + "$" + data.getSpaceStationName() + "$" + id + "$" + data.getHomePlanet(), id);
                    result.put(celestialBody.getName() + "$" + data.getOwner() + "$" + data.getSpaceStationName() + "$" + element + "$" + data.getHomePlanet(), element);
                }
            }
        }

        // moons
        List<Moon> moons = GalaxyRegistry.getMoonsForPlanet(body);
        for(Moon m: moons) {
            if(m.getReachable()) {
                result.put(m.getName(), m.getDimensionID());
            }
        }

        // now the motherships
        MothershipWorldData msData = TickHandlerServer.mothershipData;
        List<Mothership> msList = msData.getMothershipsForParent(body);
        for(Mothership m: msList) {
            result.put(m.getName(), m.getDimensionID());
        }


        return result;
    }

    /**
     * Returns the planet the given body is orbiting, whenever directly or not.
     * Returns null if body is a star or something
     *
     * @param body
     * @return
     */
    public static CelestialBody getParentPlanet(CelestialBody body)
    {
        if(body instanceof Planet) {
            return body;
        } else if(body instanceof Moon) {
            return ((Moon) body).getParentPlanet();
        } else if (body instanceof Satellite) {
            return ((Satellite) body).getParentPlanet();
        } else if (body instanceof Mothership) {
            CelestialBody parent = ((Mothership) body).getParent();
            return getParentPlanet(parent);
        }
        return null;
    }

    /**
     * Replacement for WorldUtil.getArrayOfPossibleDimensions, for usage in GuiShuttleSelection
     *
     * @param playerBase
     * @return
     */
    public static HashMap<String, Integer> getArrayOfPossibleDimensions(EntityPlayerMP playerBase)
    {
        // playerBase.dimension // this is where the player currently is
        CelestialBody playerBody = WorldUtil.getReachableCelestialBodiesForDimensionID(playerBase.dimension);
        if(playerBody == null) {
            return new HashMap<String, Integer>();
        }

        CelestialBody parent = getParentPlanet(playerBody);

        // failsafe
        if(parent == null) {
            HashMap<String, Integer> result = new HashMap<String, Integer>();
            result.put(playerBody.getName(), playerBase.dimension);
            return result;
        }

        return getArrayOfChildren(playerBase, (Planet)getParentPlanet(playerBody));

    }

}
