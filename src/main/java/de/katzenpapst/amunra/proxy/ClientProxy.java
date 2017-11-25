package de.katzenpapst.amunra.proxy;

import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.client.fx.EntityFXGravityDust;
import de.katzenpapst.amunra.client.fx.EntityFXMothershipIonFlame;
import de.katzenpapst.amunra.client.fx.EntityFXMothershipJetFire;
import de.katzenpapst.amunra.client.renderer.RenderShuttle;
import de.katzenpapst.amunra.client.sound.TickableLoopedSound;
import de.katzenpapst.amunra.command.CommandCelestialBodyInfo;
import de.katzenpapst.amunra.entity.EntityBaseLaserArrow;
import de.katzenpapst.amunra.entity.EntityOsirisBossFireball;
import de.katzenpapst.amunra.entity.spaceship.EntityShuttle;
import de.katzenpapst.amunra.entity.spaceship.EntityShuttleFake;
import de.katzenpapst.amunra.event.SystemRenderEventHandler;
import de.katzenpapst.amunra.item.ARItems;
import de.katzenpapst.amunra.item.SubItemToggle;
import de.katzenpapst.amunra.mob.entity.EntityAlienBug;
import de.katzenpapst.amunra.mob.entity.EntityMummyBoss;
import de.katzenpapst.amunra.mob.entity.EntityARVillager;
import de.katzenpapst.amunra.mob.entity.EntityPorcodon;
import de.katzenpapst.amunra.mob.entity.EntityRobotVillager;
import de.katzenpapst.amunra.mob.entity.EntitySentry;
import de.katzenpapst.amunra.mob.render.RenderARVillager;
import de.katzenpapst.amunra.mob.render.RenderBug;
import de.katzenpapst.amunra.mob.render.RenderFirstBoss;
import de.katzenpapst.amunra.mob.render.RenderPorcodon;
import de.katzenpapst.amunra.mob.render.RenderRobotVillager;
import de.katzenpapst.amunra.mob.render.RenderSentry;
import de.katzenpapst.amunra.tick.TickHandlerClient;
import de.katzenpapst.amunra.tile.TileEntityARChest;
import de.katzenpapst.amunra.tile.TileEntityARChestLarge;
import de.katzenpapst.amunra.tile.TileEntityBlockScale;
import de.katzenpapst.amunra.tile.TileEntityGravitation;
import de.katzenpapst.amunra.tile.TileEntityHydroponics;
import de.katzenpapst.amunra.tile.TileEntityMothershipEngineBooster;
import de.katzenpapst.amunra.tile.TileEntityMothershipEngineBoosterIon;
import de.katzenpapst.amunra.tile.TileEntityMothershipEngineIon;
import de.katzenpapst.amunra.tile.TileEntityMothershipEngineJet;
import de.katzenpapst.amunra.tile.TileEntityShuttleDock;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.api.world.IZeroGDimension;
import micdoodle8.mods.galacticraft.core.client.render.entities.RenderEntityFake;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.entity.RenderFireball;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends ARSidedProxy {

    private static ModelBase rocketModel = null;
    private static ModelBase engineModel = null;
    private static ModelBase engineModelIon = null;

    public static Minecraft mc = FMLClientHandler.instance().getClient();

    private static int jumpTimer = 0;

    /**
     * +0,08 seems to be 1g
     * -0,054 seems to be -1g
     * I'll assume that 5 = 1g
     * 5 in gui => 0,05 in vector
     *
     */
    public static final float GRAVITY_POS_FACTOR = 0.08F  / 0.05F;
    public static final float GRAVITY_NEG_FACTOR = 0.054F / 0.05F;

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
/*
        ISimpleBlockRenderingHandler myISBRH = new BlockRendererMultiOre();
        RenderingRegistry.registerBlockHandler(myISBRH.getRenderId(), myISBRH);

        ISimpleBlockRenderingHandler dummyRenderer = new BlockRendererDummy();
        RenderingRegistry.registerBlockHandler(dummyRenderer.getRenderId(), dummyRenderer);

        ISimpleBlockRenderingHandler msBoosterRenderer = new BlockRendererMothershipBooster();
        RenderingRegistry.registerBlockHandler(msBoosterRenderer.getRenderId(), msBoosterRenderer);

        ISimpleBlockRenderingHandler chestRenderer = new BlockRendererARChest();
        RenderingRegistry.registerBlockHandler(chestRenderer.getRenderId(), chestRenderer);
*/
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
        RenderingRegistry.registerEntityRenderingHandler(EntityMummyBoss.class, new RenderFirstBoss());

        RenderingRegistry.registerEntityRenderingHandler(EntityOsirisBossFireball.class, new RenderFireball((float) 2.0));
        // RenderingRegistry.registerEntityRenderingHandler(EntityProjectileTNT.class, new RenderProjectileTNT());


        RenderingRegistry.registerEntityRenderingHandler(EntityBaseLaserArrow.class, new RenderLaserArrow());
        RenderingRegistry.registerEntityRenderingHandler(EntityShuttle.class, new RenderShuttle(rocketModel, AmunRa.ASSETPREFIX, "shuttle"));

        RenderingRegistry.registerEntityRenderingHandler(EntityShuttleFake.class, new RenderEntityFake());

        //RenderingRegistry.registerEntityRenderingHandler(TileEntityMothershipEngine.class, new RenderMothershipEngine(engineModel));
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMothershipEngineJet.class, new RenderMothershipJet(engineModel, new ResourceLocation(AmunRa.ASSETPREFIX, "textures/model/jet.png")));
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMothershipEngineBooster.class, new RenderMothershipBooster(new ResourceLocation(AmunRa.instance.ASSETPREFIX, "textures/blocks/jet-base.png")));

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMothershipEngineIon.class, new RenderMothershipJet(engineModelIon, new ResourceLocation(AmunRa.ASSETPREFIX, "textures/model/jet-ion.png"))); // for now
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMothershipEngineBoosterIon.class, new RenderMothershipBooster(new ResourceLocation(AmunRa.instance.ASSETPREFIX, "textures/blocks/jet-base-ion.png")));

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBlockScale.class, new RenderBlockScale());

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityShuttleDock.class, new RenderShuttleDock());

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityHydroponics.class, new RenderHydroponics());

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGravitation.class, new RenderArtificalGravity());

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityARChest.class, new RenderARChest());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityARChestLarge.class, new RenderARChest());

    }



    @Override
    public void spawnParticles(ParticleType type, World world, Vector3 pos, Vector3 motion) {

        if(!world.isRemote) {
            return;
        }
        EntityFX resultEntity = null;
        switch(type) {
        case PT_MOTHERSHIP_JET_FLAME:
            resultEntity = new EntityFXMothershipJetFire(world, pos, motion);
            break;
        case PT_MOTHERSHIP_ION_FLAME:
            resultEntity = new EntityFXMothershipIonFlame(world, pos, motion, 2.5F);
            break;
        case PT_GRAVITY_DUST:
            resultEntity = new EntityFXGravityDust(world, pos, motion);
            break;
        default:
            return;
        }

        resultEntity.prevPosX = resultEntity.posX;
        resultEntity.prevPosY = resultEntity.posY;
        resultEntity.prevPosZ = resultEntity.posZ;

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

    private boolean hasActiveGravityDisabler(EntityPlayerSP p) {
        for(ItemStack stack: p.inventory.mainInventory) {
            if(ARItems.gravityDisabler.isSameItem(stack)) {
                SubItemToggle si = (SubItemToggle) ARItems.gravityDisabler.getSubItem();
                if(si.getState(stack)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * This should somehow mark the player as ignored
     */
    @Override
    public void handlePlayerArtificalGravity(EntityPlayer player, double gravity) {
        if(player instanceof EntityPlayerSP) {
            if(!Minecraft.getMinecraft().thePlayer.equals(player)) {
                return;
            }
            EntityPlayerSP p = (EntityPlayerSP)player;


            if(this.hasActiveGravityDisabler(p)) {
                return;
            }


            /*if(ARItems.gravityDisabler.isSameItem(p.getEquipmentInSlot(0))) {
                return;
            }*/

            TickHandlerClient.playerGravityState = 2;

            // hack against the jumping bug in 498
            if(p.worldObj.provider instanceof IZeroGDimension) {
                if(p.movementInput.jump && p.onGround && jumpTimer <= 0) {
                    p.jump();
                    jumpTimer = 10;
                } else {
                    if(jumpTimer > 0) {
                        jumpTimer--;
                    }
                }
            }

            // +0,08 seems to be 1g
            // -0,054 seems to be -1g
            // difference: 0,026??
            double fu = gravity;
            if(fu < 0) {
                fu *= GRAVITY_NEG_FACTOR;
            } else {
                fu *= GRAVITY_POS_FACTOR;
            }

            //p.addVelocity(gravity.x, gravity.y, gravity.z);
            p.motionY += fu;


        }
    }

    @Override
    public boolean doCancelGravityEvent(EntityPlayer player) {
        return TickHandlerClient.playerGravityState > 0;
    }
}
