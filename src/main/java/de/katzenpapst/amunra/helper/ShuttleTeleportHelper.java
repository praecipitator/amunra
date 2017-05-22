package de.katzenpapst.amunra.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.FMLCommonHandler;
import de.katzenpapst.amunra.entity.spaceship.EntityShuttle;
import de.katzenpapst.amunra.item.ItemShuttle;
import de.katzenpapst.amunra.mothership.Mothership;
import de.katzenpapst.amunra.mothership.MothershipWorldData;
import de.katzenpapst.amunra.mothership.MothershipWorldProvider;
import de.katzenpapst.amunra.tick.TickHandlerServer;
import de.katzenpapst.amunra.vec.Vector3int;
import de.katzenpapst.amunra.world.ShuttleDockHandler;
import de.katzenpapst.amunra.world.WorldHelper;
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.Moon;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.Satellite;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.api.world.IExitHeight;
import micdoodle8.mods.galacticraft.api.world.IOrbitDimension;
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
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.GCLog;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;
import net.minecraft.entity.Entity;
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
import net.minecraftforge.fluids.FluidStack;

public class ShuttleTeleportHelper {

    public ShuttleTeleportHelper() {}

    public static Entity transferEntityToDimension(Entity entity, int dimensionID, WorldServer world)
    {
        //boolean transferInv = true;
        //EntityAutoRocket ridingRocket = null;
        if (!world.isRemote)
        {
            //GalacticraftCore.packetPipeline.sendToAll(new PacketSimple(EnumSimplePacket.C_UPDATE_PLANETS_LIST, WorldUtil.getPlanetList()));

            MinecraftServer mcServer = FMLCommonHandler.instance().getMinecraftServerInstance();

            if (mcServer != null)
            {
                final WorldServer targetWorld = world.provider.dimensionId==dimensionID ? world : mcServer.worldServerForDimension(dimensionID);

                if (targetWorld == null)
                {
                    System.err.println("Cannot Transfer Entity to Dimension: Could not get World for Dimension " + dimensionID);
                    return null;
                }

                final ITeleportType type = GalacticraftRegistry.getTeleportTypeForDimension(targetWorld.provider.getClass());

                if (type != null)
                {
                    return teleportEntity(targetWorld, entity, dimensionID, type);
                }
            }
        }

        return null;
    }

    private static Entity teleportEntity(World worldNew, Entity entity, int dimID, ITeleportType type)
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

                player.closeScreen(); // redundant?
                GCPlayerStats stats = GCPlayerStats.get(player);
                stats.usingPlanetSelectionGui = false;

                player.dimension = dimID;
                if (ConfigManagerCore.enableDebug)
                {
                    GCLog.info("DEBUG: Sending respawn packet to player for dim " + dimID);
                }
                player.playerNetServerHandler.sendPacket(new S07PacketRespawn(dimID, player.worldObj.difficultySetting, player.worldObj.getWorldInfo().getTerrainType(), player.theItemInWorldManager.getGameType()));

                // I'm almost think this can be deleted
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

                // ok this is important
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
        }
        GCPlayerStats playerStats = GCPlayerStats.get(player);
        boolean usingShuttle = playerStats.rocketItem != null && (playerStats.rocketItem instanceof ItemShuttle);

        if(spawnPos == null) {
            // this should now happen
            spawnPos = new Vector3(0, 800, 0);
        }
        if(spawnPos.y < 300) {
            spawnPos.y = 300;// ?
        }
        if(spawnPos.y > 800) {
            spawnPos.y = 800;
        }


        player.setLocationAndAngles(spawnPos.x, spawnPos.y, spawnPos.z, entity.rotationYaw, entity.rotationPitch);

        // this part is relevant, I think this code should still be able to teleport just the player

        if(!usingShuttle) {
            // just the player/parachest?
            if(type.useParachute() && playerStats.extendedInventory.getStackInSlot(4) != null && playerStats.extendedInventory.getStackInSlot(4).getItem() instanceof ItemParaChute) {
                GCPlayerHandler.setUsingParachute(player, playerStats, true);
            } else {
                GCPlayerHandler.setUsingParachute(player, playerStats, false);
            }
            if(playerStats.rocketStacks != null && playerStats.rocketStacks.length > 0) {
             // I think this just puts the rocket and the launch pads into the inventory
                for (int stack = 0; stack < playerStats.rocketStacks.length; stack++)
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
                if (playerStats.chestSpawnCooldown == 0)
                {
                    playerStats.chestSpawnVector = type.getParaChestSpawnLocation((WorldServer) entity.worldObj, player, new Random());
                    playerStats.chestSpawnCooldown = 200;
                }
            }
        } else {
            // land in shuttle
            GCPlayerHandler.setUsingParachute(player, playerStats, false);

            ItemShuttle shuttle = (ItemShuttle)playerStats.rocketItem;

            if (GCPlayerStats.get(player).teleportCooldown <= 0)
            {
                if (player.capabilities.isFlying)
                {
                    player.capabilities.isFlying = false;
                }

                landInShuttle(worldNew, player, shuttle, spawnPos);

                GCPlayerStats.get(player).teleportCooldown = 10;
            }
        }

        // just the event, it's definitely necessary
        FMLCommonHandler.instance().firePlayerChangedDimensionEvent((EntityPlayerMP) entity, oldDimID, dimID);


        return entity;
    }

    private static void landInShuttle(World world, EntityPlayerMP player, ItemShuttle item, Vector3 spawnPos) {
        GCPlayerStats playerStats = GCPlayerStats.get(player);

        // failsafe! yes, this happened...
        if(world.provider instanceof IExitHeight) {
            if(((IExitHeight)world.provider).getYCoordinateToTeleport()-10 <= spawnPos.y) {
                spawnPos.y = ((IExitHeight)world.provider).getYCoordinateToTeleport()-10;
            }
        }

        // boolean landInDock = false;

        Vector3int dock = null;
        Vector3 itemDropPosition = spawnPos.clone();
        // is the world a mothership or a space station?
        // stuff here?
        if(world.provider instanceof MothershipWorldProvider || world.provider instanceof IOrbitDimension) {
            // look for a dock
            dock = ShuttleDockHandler.findAvailableDock(world.provider.dimensionId);
            if(dock != null) {
                double yTemp = spawnPos.y;
                spawnPos = dock.toVector3();
                spawnPos.y = yTemp;
                itemDropPosition = spawnPos.clone();
            }
        }

        EntityShuttle shuttle = item.spawnRocketEntity(new ItemStack(playerStats.rocketItem, 1, playerStats.rocketType),
                world, spawnPos.x, spawnPos.y, spawnPos.z);

        if(dock != null) {
            shuttle.setTargetDock(dock);
        }

        shuttle.fuelTank.setFluid(new FluidStack(GalacticraftCore.fluidFuel, playerStats.fuelLevel));

        ItemStack[] cargoStack = playerStats.rocketStacks.clone();

        if(playerStats.launchpadStack != null && playerStats.launchpadStack.stackSize > 0 && playerStats.launchpadStack.getItem() != null) {
            // the shuttle might be too small for the thing
            if(shuttle.rocketType.getInventorySpace() <= 2) {
                // The shuttle inventory is too small, try to give it to the player
                if(!player.inventory.addItemStackToInventory(playerStats.launchpadStack)) {
                    // player has no space either, just drop it
                    WorldHelper.dropItemInWorld(world, playerStats.launchpadStack, itemDropPosition.x, itemDropPosition.y, itemDropPosition.z);
                    /*EntityItem itemEntity = new EntityItem(world, itemDropPosition.x, itemDropPosition.y, itemDropPosition.z, playerStats.launchpadStack);
                    world.spawnEntityInWorld(itemEntity);*/
                }

            } else {

                for(int i=0;i<cargoStack.length;i++) {
                    if(cargoStack[i] == null) {
                        // bingo
                        cargoStack[i] = playerStats.launchpadStack;
                        playerStats.launchpadStack = null;
                        break;
                    }
                }
            }
        }

        shuttle.setCargoContents(cargoStack);
        playerStats.rocketStacks = new ItemStack[2]; // THIS MUST NEVER BE null
        shuttle.setPositionAndRotation(spawnPos.x, spawnPos.y, spawnPos.z, 0, 0);
        player.setPositionAndRotation(spawnPos.x, spawnPos.y, spawnPos.z, player.rotationYaw, player.rotationPitch);

        player.mountEntity(shuttle);

        //shuttle.landing = true;
        //shuttle.launchPhase = EnumLaunchPhase.LAUNCHED.ordinal();
        shuttle.setLanding();

        // playerStats.rocketItem = null;

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
    private static HashMap<String, Integer> getArrayOfChildren(EntityPlayerMP playerBase, CelestialBody body)
    {
        HashMap<String, Integer> result = new HashMap<String, Integer>();

        if(body.getReachable()) {
            int planetId = body.getDimensionID();
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
     * Replacement for WorldUtil.getReachableCelestialBodiesForName which works on motherships
     * @param name
     * @return
     */
    public static CelestialBody getReachableCelestialBodiesForName(String name)
    {
        CelestialBody ms = TickHandlerServer.mothershipData.getByName(name);
        if(ms != null) {
            return ms;
        }

        return WorldUtil.getReachableCelestialBodiesForName(name);
    }

    /**
     *
     * @param name
     * @return
     */
    public static CelestialBody getAnyCelestialBodyForName(String name)
    {
        CelestialBody ms = TickHandlerServer.mothershipData.getByName(name);
        if(ms != null) {
            return ms;
        }

        List<CelestialBody> celestialBodyList = Lists.newArrayList();
        celestialBodyList.addAll(GalaxyRegistry.getRegisteredMoons().values());
        celestialBodyList.addAll(GalaxyRegistry.getRegisteredPlanets().values());
        celestialBodyList.addAll(GalaxyRegistry.getRegisteredSatellites().values());

        for (CelestialBody cBody : celestialBodyList)
        {
            if (cBody.getName().equals(name))
            {
                return cBody;
            }

        }

        return null;
    }

    /**
     * Again a replacement for a WorldUtil.getReachableCelestialBodiesForDimensionID, with better name
     *
     * @param id
     * @return
     */
    public static CelestialBody getCelestialBodyForDimensionID(int id)
    {
        CelestialBody defaultBody = WorldUtil.getReachableCelestialBodiesForDimensionID(id);
        if(defaultBody != null) {
            return defaultBody;
        }

        return TickHandlerServer.mothershipData.getByDimensionId(id);
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
        CelestialBody playerBody = getCelestialBodyForDimensionID(playerBase.dimension);
        if(playerBody == null) {
            // this might be that we started from a space station
            final SpaceStationWorldData data = SpaceStationWorldData.getStationData(playerBase.worldObj, playerBase.dimension, null);
            if(data != null) {
                int parentID = data.getHomePlanet();

                CelestialBody parentBody = getCelestialBodyForDimensionID(parentID);
                if(parentBody != null) {
                    return getArrayOfChildren(playerBase, parentBody);
                }

            }

            // SpaceStationWorldData.getStationData(world, stationID, player)
            /*
            playerBody  = GalacticraftCore.satelliteSpaceStation;
            HashMap<String, Integer> map = new HashMap<String, Integer>();


            final SpaceStationWorldData data = SpaceStationWorldData.getStationData(playerBase.worldObj, playerBase.dimension, null);
            map.put(playerBody.getName() + "$" + data.getOwner() + "$" + data.getSpaceStationName() + "$" + playerBase.dimension + "$" + data.getHomePlanet(), playerBase.dimension);

            // TEMP!
            CelestialBody parent = ((Satellite)playerBody).getParentPlanet();
            map.putAll(getArrayOfChildren(playerBase, parent));

            return map;
            */
            return new HashMap<String, Integer>();
        }

        //
        if(playerBody instanceof Mothership) {
            playerBody = ((Mothership)playerBody).getParent();

        }
        return getArrayOfChildren(playerBase, playerBody);
    }

}
