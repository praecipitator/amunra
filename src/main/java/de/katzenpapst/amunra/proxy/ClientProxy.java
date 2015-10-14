package de.katzenpapst.amunra.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.common.MinecraftForge;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.core.client.CloudRenderer;
import micdoodle8.mods.galacticraft.planets.PlanetsProxy;
import micdoodle8.mods.galacticraft.planets.asteroids.AsteroidsModuleClient;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.event.SystemRenderEventHandler;
import de.katzenpapst.amunra.mob.entity.EntityARVillager;
import de.katzenpapst.amunra.mob.entity.EntityPorcodon;
import de.katzenpapst.amunra.mob.render.RenderARVillager;
import de.katzenpapst.amunra.mob.render.RenderPorcodon;
import de.katzenpapst.amunra.world.AmunraWorldProvider;
import de.katzenpapst.amunra.world.SkyProviderDynamic;
import de.katzenpapst.amunra.world.anubis.AnubisWorldProvider;

public class ClientProxy extends ARSidedProxy {
	@Override
    public void preInit(FMLPreInitializationEvent event)
    {
	
        /*GalacticraftPlanets.clientModules.put(GalacticraftPlanets.MODULE_KEY_MARS, new MarsModuleClient());
        GalacticraftPlanets.clientModules.put(GalacticraftPlanets.MODULE_KEY_ASTEROIDS, new AsteroidsModuleClient());

        super.preInit(event);

        for (IPlanetsModuleClient module : GalacticraftPlanets.clientModules.values())
        {
            module.preInit(event);
        }*/
    }

    @Override
    public void init(FMLInitializationEvent event)
    {
    	SystemRenderEventHandler clientEventHandler = new SystemRenderEventHandler();
        FMLCommonHandler.instance().bus().register(clientEventHandler);
        MinecraftForge.EVENT_BUS.register(clientEventHandler);
        
        FMLCommonHandler.instance().bus().register(new TickHandlerClient());
    }

    @Override
    public void postInit(FMLPostInitializationEvent event)
    {
        ClientProxy.registerEntityRenderers();
    }
    
    public static void registerEntityRenderers()
    {
    	// here entity and renderer come together, as it seems
    	RenderingRegistry.registerEntityRenderingHandler(EntityPorcodon.class, new RenderPorcodon());
    	RenderingRegistry.registerEntityRenderingHandler(EntityARVillager.class, new RenderARVillager());
    	/*
        RenderingRegistry.registerEntityRenderingHandler(EntityTier1Rocket.class, new RenderTier1Rocket(new ModelRocketTier1(), GalacticraftCore.ASSET_PREFIX, "rocketT1"));
        RenderingRegistry.registerEntityRenderingHandler(EntityEvolvedSpider.class, new RenderEvolvedSpider());
        RenderingRegistry.registerEntityRenderingHandler(EntityEvolvedZombie.class, new RenderEvolvedZombie());
        RenderingRegistry.registerEntityRenderingHandler(EntityEvolvedCreeper.class, new RenderEvolvedCreeper());
        RenderingRegistry.registerEntityRenderingHandler(EntityEvolvedSkeleton.class, new RenderEvolvedSkeleton());
        RenderingRegistry.registerEntityRenderingHandler(EntitySkeletonBoss.class, new RenderEvolvedSkeletonBoss());
        RenderingRegistry.registerEntityRenderingHandler(EntityMeteor.class, new RenderMeteor());
        RenderingRegistry.registerEntityRenderingHandler(EntityBuggy.class, new RenderBuggy());
        RenderingRegistry.registerEntityRenderingHandler(EntityMeteorChunk.class, new RenderMeteorChunk());
        RenderingRegistry.registerEntityRenderingHandler(EntityFlag.class, new RenderFlag());
        RenderingRegistry.registerEntityRenderingHandler(EntityParachest.class, new RenderParaChest());
        RenderingRegistry.registerEntityRenderingHandler(EntityAlienVillager.class, new RenderAlienVillager());
        RenderingRegistry.registerEntityRenderingHandler(EntityBubble.class, new RenderBubble(0.25F, 0.25F, 1.0F));
        RenderingRegistry.registerEntityRenderingHandler(EntityLander.class, new RenderLander());
        RenderingRegistry.registerEntityRenderingHandler(EntityCelestialFake.class, new RenderEntityFake());

        if (Loader.isModLoaded("RenderPlayerAPI"))
        {
            ModelPlayerAPI.register(Constants.MOD_ID_CORE, ModelPlayerBaseGC.class);
            RenderPlayerAPI.register(Constants.MOD_ID_CORE, RenderPlayerBaseGC.class);
        }
        else
        {
            RenderingRegistry.registerEntityRenderingHandler(EntityPlayer.class, new RenderPlayerGC());
        }
        */
    }
    
    public static class TickHandlerClient
    {
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
            		if(world.provider instanceof AnubisWorldProvider) {
            			if (world.provider.getCloudRenderer() == null)
                        {
                            world.provider.setCloudRenderer(new CloudRenderer());
                        }
            		}
            	}
                /*if (world.provider instanceof WorldProviderMars)
                {
                    if (world.provider.getSkyRenderer() == null)
                    {
                        world.provider.setSkyRenderer(new SkyProviderMars((IGalacticraftWorldProvider) world.provider));
                    }

                    if (world.provider.getCloudRenderer() == null)
                    {
                        world.provider.setCloudRenderer(new CloudRenderer());
                    }
                }*/
            }
        }
    }
}
