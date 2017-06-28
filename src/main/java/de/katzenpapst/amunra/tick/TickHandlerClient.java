package de.katzenpapst.amunra.tick;

import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.mothership.MothershipWorldProvider;
import de.katzenpapst.amunra.mothership.SkyProviderMothership;
import de.katzenpapst.amunra.world.AmunraWorldProvider;
import de.katzenpapst.amunra.world.SkyProviderDynamic;
import de.katzenpapst.amunra.world.asteroidWorld.AmunRaAsteroidWorldProvider;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.core.client.CloudRenderer;
import micdoodle8.mods.galacticraft.core.client.SkyProviderOrbit;
import micdoodle8.mods.galacticraft.planets.asteroids.client.SkyProviderAsteroids;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;


public class TickHandlerClient
{
    public static int playerGravityState = 0;

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientTick(ClientTickEvent event)
    {
        final Minecraft minecraft = FMLClientHandler.instance().getClient();

        final WorldClient world = minecraft.theWorld;

        if (world != null)
        {
            if(world.provider instanceof AmunraWorldProvider) {
                if(world.provider.getSkyRenderer() == null) {
                    world.provider.setSkyRenderer(new SkyProviderDynamic((IGalacticraftWorldProvider) world.provider));
                }
                //((AmunraWorldProvider)world.provider).hasBreathableAtmosphere()
                if(!((AmunraWorldProvider) world.provider).hasClouds()) {
                    if (world.provider.getCloudRenderer() == null)
                    {
                        world.provider.setCloudRenderer(new CloudRenderer()); // dummy cloud renderer
                    }
                }
            } else if(world.provider instanceof MothershipWorldProvider) {
                if(world.provider.getSkyRenderer() == null || world.provider.getSkyRenderer() instanceof SkyProviderOrbit) {
                    world.provider.setSkyRenderer(new SkyProviderMothership((IGalacticraftWorldProvider) world.provider));
                    world.provider.setCloudRenderer(new CloudRenderer());
                }
                //((AmunraWorldProvider)world.provider).hasBreathableAtmosphere()
                /*if(!((AmunraWorldProvider) world.provider).hasClouds()) {
                    if (world.provider.getCloudRenderer() == null)
                    {
                        world.provider.setCloudRenderer(new CloudRenderer()); // dummy cloud renderer
                    }
                }*/
            } else if(world.provider instanceof AmunRaAsteroidWorldProvider) {
                if(world.provider.getSkyRenderer() == null || world.provider.getSkyRenderer() instanceof SkyProviderAsteroids) {
                    world.provider.setSkyRenderer(new SkyProviderDynamic((IGalacticraftWorldProvider) world.provider));
                }
            }

            if(world.isRemote && TickHandlerServer.mothershipData != null) {
                TickHandlerServer.mothershipData.tickAllMothershipsClient();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event) {
        if(playerGravityState > 0 && event.phase == Phase.END) {
            playerGravityState--;
        }
        /*if(event.phase == Phase.START) {
            playerWasOnGround = event.player.onGround;
        }*/
    }
}