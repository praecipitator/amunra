package de.katzenpapst.amunra.proxy;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.client.fx.EntityFXMotehrshipIonFlame;
import de.katzenpapst.amunra.client.fx.EntityFXMothershipJetFire;
import de.katzenpapst.amunra.client.renderer.BlockRendererDummy;
import de.katzenpapst.amunra.client.renderer.BlockRendererMothershipBooster;
import de.katzenpapst.amunra.client.renderer.RenderLaserArrow;
import de.katzenpapst.amunra.client.renderer.RenderMothershipBooster;
import de.katzenpapst.amunra.client.renderer.RenderMothershipJet;
import de.katzenpapst.amunra.client.renderer.RenderShuttle;
import de.katzenpapst.amunra.client.renderer.RenderShuttleDock;
import de.katzenpapst.amunra.client.renderer.BlockRendererMultiOre;
import de.katzenpapst.amunra.client.renderer.RenderArtificalGravity;
import de.katzenpapst.amunra.client.renderer.RenderBlockScale;
import de.katzenpapst.amunra.client.renderer.RenderHydroponics;
import de.katzenpapst.amunra.client.renderer.item.ItemRendererSpecial1;
import de.katzenpapst.amunra.client.renderer.item.ItemRendererJet;
import de.katzenpapst.amunra.client.renderer.item.ItemRendererShuttle;
import de.katzenpapst.amunra.client.sound.TickableLoopedSound;
import de.katzenpapst.amunra.command.CommandCelestialBodyInfo;
import de.katzenpapst.amunra.entity.EntityBaseLaserArrow;
import de.katzenpapst.amunra.entity.spaceship.EntityShuttle;
import de.katzenpapst.amunra.event.SystemRenderEventHandler;
import de.katzenpapst.amunra.item.ARItems;
import de.katzenpapst.amunra.mob.entity.EntityAlienBug;
import de.katzenpapst.amunra.mob.entity.EntityARVillager;
import de.katzenpapst.amunra.mob.entity.EntityPorcodon;
import de.katzenpapst.amunra.mob.entity.EntityRobotVillager;
import de.katzenpapst.amunra.mob.entity.EntitySentry;
import de.katzenpapst.amunra.mob.render.RenderARVillager;
import de.katzenpapst.amunra.mob.render.RenderBug;
import de.katzenpapst.amunra.mob.render.RenderPorcodon;
import de.katzenpapst.amunra.mob.render.RenderRobotVillager;
import de.katzenpapst.amunra.mob.render.RenderSentry;
import de.katzenpapst.amunra.tick.TickHandlerClient;
import de.katzenpapst.amunra.tile.TileEntityBlockScale;
import de.katzenpapst.amunra.tile.TileEntityGravitation;
import de.katzenpapst.amunra.tile.TileEntityHydroponics;
import de.katzenpapst.amunra.tile.TileEntityMothershipEngineBooster;
import de.katzenpapst.amunra.tile.TileEntityMothershipEngineBoosterIon;
import de.katzenpapst.amunra.tile.TileEntityMothershipEngineIon;
import de.katzenpapst.amunra.tile.TileEntityMothershipEngineJet;
import de.katzenpapst.amunra.tile.TileEntityShuttleDock;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.core.entities.player.FreefallHandler;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStatsClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends ARSidedProxy {

    private static IModelCustom rocketModel = null;
    private static IModelCustom engineModel = null;
    private static IModelCustom engineModelIon = null;

    private int ticksSinceLastJump = 0;

    public static Minecraft mc = FMLClientHandler.instance().getClient();

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        // try stuff
        ClientCommandHandler.instance.registerCommand(new CommandCelestialBodyInfo());

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

        ISimpleBlockRenderingHandler msBoosterRenderer = new BlockRendererMothershipBooster();
        RenderingRegistry.registerBlockHandler(msBoosterRenderer.getRenderId(), msBoosterRenderer);


        SystemRenderEventHandler clientEventHandler = new SystemRenderEventHandler();
        FMLCommonHandler.instance().bus().register(clientEventHandler);
        MinecraftForge.EVENT_BUS.register(clientEventHandler);

        FMLCommonHandler.instance().bus().register(new TickHandlerClient());
    }

    @Override
    public void postInit(FMLPostInitializationEvent event)
    {
        rocketModel = AdvancedModelLoader.loadModel(new ResourceLocation(AmunRa.ASSETPREFIX, "models/shuttle2.obj"));
        engineModel = AdvancedModelLoader.loadModel(new ResourceLocation(AmunRa.ASSETPREFIX, "models/jet.obj"));
        engineModelIon = AdvancedModelLoader.loadModel(new ResourceLocation(AmunRa.ASSETPREFIX, "models/jet-ion.obj"));
        ClientProxy.registerEntityRenderers();
        ClientProxy.registerItemRenderers();
    }

    public static void registerItemRenderers()
    {
        MinecraftForgeClient.registerItemRenderer(ARItems.shuttleItem, new ItemRendererShuttle(rocketModel));

        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ARBlocks.blockShuttleDock.getBlock()), new ItemRendererSpecial1());


        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ARBlocks.metaBlockMothershipEngineJet), new ItemRendererJet(
                new IModelCustom[]{engineModel, engineModelIon},
                new ResourceLocation[] {
                        new ResourceLocation(AmunRa.instance.ASSETPREFIX, "textures/model/jet.png"),
                        new ResourceLocation(AmunRa.instance.ASSETPREFIX, "textures/model/jet-ion.png")
                        }
                ));

    }

    public static void registerEntityRenderers()
    {

        // here entity and renderer come together, as it seems
        RenderingRegistry.registerEntityRenderingHandler(EntityPorcodon.class, new RenderPorcodon());
        RenderingRegistry.registerEntityRenderingHandler(EntityARVillager.class, new RenderARVillager());
        RenderingRegistry.registerEntityRenderingHandler(EntityRobotVillager.class, new RenderRobotVillager());
        RenderingRegistry.registerEntityRenderingHandler(EntitySentry.class, new RenderSentry());
        RenderingRegistry.registerEntityRenderingHandler(EntityAlienBug.class, new RenderBug());
        RenderingRegistry.registerEntityRenderingHandler(EntityBaseLaserArrow.class, new RenderLaserArrow());
        RenderingRegistry.registerEntityRenderingHandler(EntityShuttle.class, new RenderShuttle(rocketModel, AmunRa.ASSETPREFIX, "shuttle"));
        //RenderingRegistry.registerEntityRenderingHandler(TileEntityMothershipEngine.class, new RenderMothershipEngine(engineModel));
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMothershipEngineJet.class, new RenderMothershipJet(engineModel, new ResourceLocation(AmunRa.ASSETPREFIX, "textures/model/jet.png")));
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMothershipEngineBooster.class, new RenderMothershipBooster(new ResourceLocation(AmunRa.instance.ASSETPREFIX, "textures/blocks/jet-base.png")));

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMothershipEngineIon.class, new RenderMothershipJet(engineModelIon, new ResourceLocation(AmunRa.ASSETPREFIX, "textures/model/jet-ion.png"))); // for now
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMothershipEngineBoosterIon.class, new RenderMothershipBooster(new ResourceLocation(AmunRa.instance.ASSETPREFIX, "textures/blocks/jet-base-ion.png")));

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBlockScale.class, new RenderBlockScale());

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityShuttleDock.class, new RenderShuttleDock());

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityHydroponics.class, new RenderHydroponics());

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGravitation.class, new RenderArtificalGravity());

    }



    @Override
    public void spawnParticles(ParticleType type, World world, Vector3 pos, Vector3 motion) {
        /*double motionX = world.rand.nextGaussian() * 0.02D;
        double motionY = world.rand.nextGaussian() * 0.02D;
        double motionZ = world.rand.nextGaussian() * 0.02D;*/
        if(!world.isRemote) {
            return;
        }
        EntityFX resultEntity = null;
        switch(type) {
        case PT_MOTHERSHIP_JET_FLAME:
            resultEntity = new EntityFXMothershipJetFire(world, pos, motion);
            break;
        case PT_MOTHERSHIP_ION_FLAME:
            resultEntity = new EntityFXMotehrshipIonFlame(world, pos, motion, 2.5F);
            break;
        default:
            return;
        }
        Minecraft.getMinecraft().effectRenderer.addEffect(resultEntity);
    }

    @Override
    public void playTileEntitySound(TileEntity tile, ResourceLocation resource) {
        if(!tile.getWorldObj().isRemote) {
            return;
        }
        TickableLoopedSound snd = new TickableLoopedSound(tile, resource);
        Minecraft.getMinecraft().getSoundHandler().playSound(snd);

    }

    @Override
    public void handlePlayerArtificalGravity(EntityPlayer player, Vector3 gravity) {
        if(player instanceof EntityPlayerSP) {
            if(!Minecraft.getMinecraft().thePlayer.equals(player)) {
                return;
            }
            EntityPlayerSP p = (EntityPlayerSP)player;
            // v = a * t
            //double deltaY = p.lastTickPosY-p.posY;

            GCPlayerStatsClient stats = GCPlayerStatsClient.get(p);
            stats.inFreefall = false;
            /*stats.inFreefallFirstCheck = false;
            stats.inFreefallLast = false;*/

            /*p.motionX /= 0.91F;
            p.motionZ /= 0.91F;
            p.motionY *= 0.9800000190734863D;*/

            boolean wasOnGround = TickHandlerClient.playerWasOnGround;


            if(p.movementInput.jump && wasOnGround) {
                p.motionY = -gravity.y+p.jumpMovementFactor;
                ticksSinceLastJump = 0;
            } else {

                p.addVelocity(gravity.x, gravity.y, gravity.z);
                if(!p.onGround) {
                    ticksSinceLastJump++;
                } else {
                    ticksSinceLastJump = 0;
                }
            }

            /*if(p.onGround) {
                p.motionY = 0;//.00999999910593033D;
                //stats.inFreefall = false;
            } else {
                if(!p.isOnLadder()) {
                    p.motionY -= 0.0399999;

                } else {
                    //stats.inFreefall = false;
                }
            }*/

            FreefallHandler.pPrevMotionY = p.motionY;

        }
    }
}
