package de.katzenpapst.amunra.proxy;

import org.lwjgl.util.vector.Vector3f;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.client.fx.EntityFXMotehrshipJetSmoke;
import de.katzenpapst.amunra.client.fx.EntityFXMothershipJetFire;
import de.katzenpapst.amunra.client.renderer.BlockRendererDummy;
import de.katzenpapst.amunra.client.renderer.RenderLaserArrow;
import de.katzenpapst.amunra.client.renderer.RenderMothershipJet;
import de.katzenpapst.amunra.client.renderer.RenderShuttle;
import de.katzenpapst.amunra.client.renderer.BlockRendererMultiOre;
import de.katzenpapst.amunra.client.renderer.item.ItemRendererShuttle;
import de.katzenpapst.amunra.entity.EntityBaseLaserArrow;
import de.katzenpapst.amunra.entity.spaceship.EntityShuttle;
import de.katzenpapst.amunra.event.SystemRenderEventHandler;
import de.katzenpapst.amunra.item.ARItems;
import de.katzenpapst.amunra.mob.entity.EntityARVillager;
import de.katzenpapst.amunra.mob.entity.EntityPorcodon;
import de.katzenpapst.amunra.mob.entity.EntityRobotVillager;
import de.katzenpapst.amunra.mob.render.RenderARVillager;
import de.katzenpapst.amunra.mob.render.RenderPorcodon;
import de.katzenpapst.amunra.mob.render.RenderRobotVillager;
import de.katzenpapst.amunra.mothership.MothershipWorldProvider;
import de.katzenpapst.amunra.mothership.SkyProviderMothership;
import de.katzenpapst.amunra.proxy.ARSidedProxy.ParticleType;
import de.katzenpapst.amunra.tile.TileEntityMothershipEngineJet;
import de.katzenpapst.amunra.world.AmunraWorldProvider;
import de.katzenpapst.amunra.world.SkyProviderDynamic;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.client.CloudRenderer;
import micdoodle8.mods.galacticraft.core.client.SkyProviderOrbit;
import micdoodle8.mods.galacticraft.planets.asteroids.AsteroidsModule;
import micdoodle8.mods.galacticraft.planets.mars.MarsModule;
import micdoodle8.mods.galacticraft.planets.mars.MarsModuleClient;
import micdoodle8.mods.galacticraft.planets.mars.client.render.block.BlockRendererMachine;
import micdoodle8.mods.galacticraft.planets.mars.client.render.tile.TileEntityCryogenicChamberRenderer;
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityCryogenicChamber;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends ARSidedProxy {

    private static IModelCustom rocketModel = null;
    private static IModelCustom engineModel = null;

    public static Minecraft mc = FMLClientHandler.instance().getClient();

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
        ISimpleBlockRenderingHandler myISBRH = new BlockRendererMultiOre();
        RenderingRegistry.registerBlockHandler(myISBRH.getRenderId(), myISBRH);
        ISimpleBlockRenderingHandler dummyRenderer = new BlockRendererDummy();
        RenderingRegistry.registerBlockHandler(dummyRenderer.getRenderId(), dummyRenderer);
        //RenderingRegistry.registerBlockHandler()

        SystemRenderEventHandler clientEventHandler = new SystemRenderEventHandler();
        FMLCommonHandler.instance().bus().register(clientEventHandler);
        MinecraftForge.EVENT_BUS.register(clientEventHandler);

        FMLCommonHandler.instance().bus().register(new TickHandlerClient());
    }

    @Override
    public void postInit(FMLPostInitializationEvent event)
    {
        rocketModel = AdvancedModelLoader.loadModel(new ResourceLocation(AmunRa.ASSETPREFIX, "models/rocket.obj"));
        engineModel = AdvancedModelLoader.loadModel(new ResourceLocation(AmunRa.ASSETPREFIX, "models/jet.obj"));
        ClientProxy.registerEntityRenderers();
        ClientProxy.registerItemRenderers();
    }

    public static void registerItemRenderers()
    {
        MinecraftForgeClient.registerItemRenderer(ARItems.shuttleItem, new ItemRendererShuttle(rocketModel));
        // MinecraftForgeClient.registerItemRenderer(ARItems.jetItem, new ItemRendererJet(engineModel, new ResourceLocation(AmunRa.ASSETPREFIX, "textures/model/rocket-textest.png")));
    }

    public static void registerEntityRenderers()
    {

        // here entity and renderer come together, as it seems
        RenderingRegistry.registerEntityRenderingHandler(EntityPorcodon.class, new RenderPorcodon());
        RenderingRegistry.registerEntityRenderingHandler(EntityARVillager.class, new RenderARVillager());
        RenderingRegistry.registerEntityRenderingHandler(EntityRobotVillager.class, new RenderRobotVillager());
        RenderingRegistry.registerEntityRenderingHandler(EntityBaseLaserArrow.class, new RenderLaserArrow());
        RenderingRegistry.registerEntityRenderingHandler(EntityShuttle.class, new RenderShuttle(rocketModel, AmunRa.ASSETPREFIX, "rocket-textest"));
        //RenderingRegistry.registerEntityRenderingHandler(TileEntityMothershipEngine.class, new RenderMothershipEngine(engineModel));
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMothershipEngineJet.class, new RenderMothershipJet(engineModel));




        // RenderingRegistry.registerEntityRenderingHandler(EntityBaseLaserArrow.class, new RenderLaserArrow());

        //RenderingRegistry.registerEntityRenderingHandler(LaserArrow.class, new RenderArrow());
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
                    }
                    //((AmunraWorldProvider)world.provider).hasBreathableAtmosphere()
                    /*if(!((AmunraWorldProvider) world.provider).hasClouds()) {
                        if (world.provider.getCloudRenderer() == null)
                        {
                            world.provider.setCloudRenderer(new CloudRenderer()); // dummy cloud renderer
                        }
                    }*/
                }
            }
        }
    }

    @Override
    public void spawnParticles(ParticleType type, World world, Vector3 pos, Vector3 motion) {
        /*double motionX = world.rand.nextGaussian() * 0.02D;
        double motionY = world.rand.nextGaussian() * 0.02D;
        double motionZ = world.rand.nextGaussian() * 0.02D;*/
        EntityFX resultEntity = null;
        switch(type) {
        case PT_MOTHERSHIP_JET_FLAME:
            resultEntity = new EntityFXMothershipJetFire(world, pos, motion);
            break;
        case PT_MOTHERSHIP_JET_SMOKE:
            resultEntity = new EntityFXMotehrshipJetSmoke(world, pos, motion, 2.5F);
            break;
        default:
            return;
        }
        Minecraft.getMinecraft().effectRenderer.addEffect(resultEntity);
    }
}
