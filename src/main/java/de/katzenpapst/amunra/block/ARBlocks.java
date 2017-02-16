package de.katzenpapst.amunra.block;

import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.block.bush.ARTreeSapling;
import de.katzenpapst.amunra.block.bush.BlockBushMulti;
import de.katzenpapst.amunra.block.bush.MethaneTallGrass;
import de.katzenpapst.amunra.block.bush.PodSapling;
import de.katzenpapst.amunra.block.machine.BlockGravitation;
import de.katzenpapst.amunra.block.machine.BlockHydroponics;
import de.katzenpapst.amunra.block.machine.BlockIsotopeGenerator;
import de.katzenpapst.amunra.block.machine.BlockMothershipController;
import de.katzenpapst.amunra.block.machine.BlockMothershipSettings;
import de.katzenpapst.amunra.block.machine.BlockScale;
import de.katzenpapst.amunra.block.machine.BlockShuttleDock;
import de.katzenpapst.amunra.block.machine.mothershipEngine.BlockMothershipBoosterMeta;
import de.katzenpapst.amunra.block.machine.mothershipEngine.BlockMothershipJetMeta;
import de.katzenpapst.amunra.block.machine.mothershipEngine.MothershipEngineBoosterBase;
import de.katzenpapst.amunra.block.machine.mothershipEngine.MothershipEngineBoosterIon;
import de.katzenpapst.amunra.block.machine.mothershipEngine.MothershipEngineJetIon;
import de.katzenpapst.amunra.block.machine.mothershipEngine.MothershipEngineJetRocket;
import de.katzenpapst.amunra.block.ore.BlockOreMulti;
import de.katzenpapst.amunra.block.ore.SubBlockOre;
import de.katzenpapst.amunra.block.ore.SubBlockOreMultidrop;
import de.katzenpapst.amunra.item.ItemDamagePair;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.planets.asteroids.AsteroidsModule;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ARBlocks {
    public static BlockBasicMeta metaBlockRock;
    public static BlockBasicMeta metaBlockCrystal;
    //public static BlockBasicMeta metaBlockMetal;
    // public static BlockBasicMulti multiBlockOre1;
    public static BlockBasicMeta metaBlockDirt;
    public static BlockBasicMeta metaBlockGrass;
    public static BlockBasicMeta metaBlockFalling;
    public static BlockBasicMeta metaBlockPlant;
    public static BlockBasicMeta metaBlockLog;
    public static BlockBasicMeta metaBlockNonRotational;
    public static BlockMachineMeta metaBlockMachine;
    public static BlockMachineMeta metaBlockMachine2;
    public static BlockMachineMeta metaBlockMothershipEngineJet;
    public static BlockMachineMeta metaBlockMothershipEngineBooster;
    public static BlockMachineMetaDummyRender metaBlockMachineSpecialRender1;

    public static BlockMetaFake metaBlockFake;

    public static BlockMetaPair blockMethanePlanks;
    public static BlockMetaPair blockPodPlanks;

    public static BlockOreMulti metaBlockAsteroidOre;
    public static BlockOreMulti metaBlockBasaltOre;
    public static BlockOreMulti metaBlockObsidianOre;
    public static BlockOreMulti metaBlockHardClayOre;
    public static BlockOreMulti metaBlockConcreteOre;

    public static IMetaBlock metaBlockLeaf;
    public static BlockBasicMeta metaBlockSapling;

    public static BlockMetaPair blockDarkMatter;
    public static BlockMetaPair blockBasalt;
    public static BlockMetaPair blockRedRock;
    public static BlockMetaPair blockYellowCobble;
    public static BlockMetaPair blockYellowRock;
    public static BlockMetaPair blockRedCobble;
    public static BlockMetaPair blockAluCrate;
    public static BlockMetaPair blockMsBase;
    public static BlockMetaPair blockWorkbench;
    public static BlockMetaPair blockDarkmatter;
    public static BlockMetaPair blockBasaltBrick;
    public static BlockMetaPair blockSmoothBasalt;
    public static BlockMetaPair blockObsidianBrick;
    public static BlockMetaPair blockUraniumBlock;


    public static BlockMetaPair blockBasaltRegolith;
    public static BlockMetaPair blockMethaneDirt;
    public static BlockMetaPair blockDust;


    public static BlockMetaPair blockMethaneGrass;
    public static BlockMetaPair blockVacuumGrass;
    public static BlockMetaPair blockUnderwaterGrass;
    public static BlockMetaPair blockObsidiSand;
    public static BlockMetaPair blockObsidiGravel;
    public static BlockMetaPair blockBasaltCobble;

    public static BlockMetaPair oreDiamondObsid;
    public static BlockMetaPair oreRubyObsid;
    public static BlockMetaPair oreEmeraldObsid;

    public static BlockMetaPair oreCryoBasalt;
    public static BlockMetaPair oreLithiumBasalt;
    public static BlockMetaPair oreAluBasalt;

    public static BlockMetaPair oreCopperBasalt;
    public static BlockMetaPair oreGoldBasalt;
    public static BlockMetaPair oreIronBasalt;
    public static BlockMetaPair oreLapisBasalt;
    public static BlockMetaPair oreSiliconBasalt;
    public static BlockMetaPair oreTinBasalt;
    public static BlockMetaPair oreTitaniumBasalt;
    public static BlockMetaPair oreUraniumBasalt;


    public static BlockMetaPair blockMethaneTGrass;
    public static BlockMetaPair blockMethaneLog;
    public static BlockMetaPair blockPodBark;

    public static BlockMetaPair blockPodLeaf;
    public static BlockMetaPair blockMethaneLeaf;
    public static BlockMetaPair blockMethaneSapling;
    public static BlockMetaPair blockPodSapling;


    public static SubBlockOre subGold;
    public static SubBlockOre subAlu;
    public static SubBlockOre subCopper;
    public static SubBlockOre subCryo;
    public static SubBlockOre subDesh;
    public static SubBlockOre subDiamond;
    public static SubBlockOre subEmerald;
    public static SubBlockOre subIron;
    public static SubBlockOre subLapis;
    public static SubBlockOre subLithium;
    public static SubBlockOre subRuby;
    public static SubBlockOre subSilicon;
    public static SubBlockOre subTin;
    public static SubBlockOre subCoal;
    public static SubBlockOre subLead;
    public static SubBlockOre subUranium;
    public static SubBlockOre subSteel;
    public static SubBlockOreMultidrop subTitanium;
    public static SubBlockOreMultidrop subBone;

    // STAIRS
    public static BlockStairsAR stairsObsidianBrick;
    public static BlockStairsAR stairsSmoothBasalt;
    public static BlockStairsAR stairsBasaltBrick;
    public static BlockStairsAR stairsBasaltBlock;
    public static BlockStairsAR stairsAluCrate;
    public static BlockStairsAR stairsMethanePlanks;
    public static BlockStairsAR stairsPodPlanks;

    // SLABS
    public static BlockSlabMeta metaSlabRock;
    public static BlockSlabMeta metaSlabWood;

    public static BlockDoubleslabMeta metaDoubleslabRock;
    public static BlockDoubleslabMeta metaDoubleslabWood;

    public static BlockMetaPair slabBasaltBlock;
    public static BlockMetaPair slabBasaltBrick;
    public static BlockMetaPair slabBasaltSmooth;
    public static BlockMetaPair slabObsidianBrick;
    public static BlockMetaPair slabAluCrate;
    public static BlockMetaPair slabPodPlanks;
    public static BlockMetaPair slabMethanePlanks;
    public static BlockMetaPair oreCoalHardClay;
    public static BlockMetaPair oreSiliconHardClay;
    public static BlockMetaPair oreAluHardClay;
    public static BlockMetaPair oreCopperHardClay;
    public static BlockMetaPair oreTinHardClay;
    public static BlockMetaPair oreIronHardClay;

    public static BlockMetaPair oreUraniumObsid;
    public static BlockMetaPair oreLeadObsid;
    public static BlockMetaPair blockOldConcrete;

    public static BlockMetaPair oreSteelConcrete;
    public static BlockMetaPair blockGlowingCoral;

    public static BlockMetaPair oreBoneConcrete;

    public static BlockMetaPair oreRubyAsteroid;
    public static BlockMetaPair oreEmeraldAsteroid;
    public static BlockMetaPair oreDiamondAsteroid;
    public static BlockMetaPair oreLithiumAsteroid;
    public static BlockMetaPair oreGoldAsteroid;
    public static BlockMetaPair oreLapisAsteroid;
    public static BlockMetaPair oreLeadAsteroid;
    public static BlockMetaPair oreUraniumAsteroid;
    public static BlockMetaPair oreCopperAsteroid;

    // MACHINES
    public static BlockMetaPair blockIsotopeGeneratorBasic;
    public static BlockMetaPair blockIsotopeGeneratorAdvanced;
    public static BlockMetaPair blockMothershipController;
    public static BlockMetaPair blockMothershipSettings;
    public static BlockMetaPair blockScale;
    public static BlockMetaPair blockHydro;
    public static BlockMetaPair blockShuttleDock;
    public static BlockMetaPair blockGravity;
    // the rocket engine, most basic one
    public static BlockMetaPair blockMsEngineRocketJet;
    // the corresponding booster
    public static BlockMetaPair blockMsEngineRocketBooster;

    // the ion engine
    public static BlockMetaPair blockMsEngineIonJet;
    // the corresponding booster
    public static BlockMetaPair blockMsEngineIonBooster;

    public static BlockMetaPair fakeBlockSealable;




    public static ItemStack getItemStack(BlockMetaPair input, int amount) {
        return new ItemStack(input.getBlock(), amount, input.getMetadata());
    }

    public static ItemDamagePair getBlockItemDamagePair(Block block, int meta) {
        return new ItemDamagePair(Item.getItemFromBlock(block), meta);
    }

    public static ItemDamagePair getBlockItemDamagePair(BlockMetaPair input) {
        return new ItemDamagePair(Item.getItemFromBlock(input.getBlock()), input.getMetadata());
    }

    public static void initBlocks()
    {
        // blast resistance: http://minecraft.gamepedia.com/Explosion#Blast_Resistance
        // hardness: http://minecraft.gamepedia.com/Breaking#Blocks_by_hardness
        // net.minecraft.init.Blocks
        // micdoodle8.mods.galacticraft.core.blocks.GCBlocks
        // micdoodle8.mods.galacticraft.planets.asteroids.blocks.AsteroidBlocks
        // micdoodle8.mods.galacticraft.planets.mars.blocks.MarsBlocks


        // MULTIORES
        // sub-ores first
        subGold = (SubBlockOre) new SubBlockOre("oreGold", "amunra:ore-gold")
                .setOredictNames("oreGold")
                .setHarvestInfo("pickaxe", 1).setHardness(3).setResistance(5);

        subAlu = (SubBlockOre) new SubBlockOre("oreAluminium", "amunra:ore-alu")
                .setOredictNames("oreAluminum", "oreAluminium", "oreNaturalAluminum")
                .setHarvestInfo("pickaxe", 2).setHardness(3).setResistance(5);

        subCopper = (SubBlockOre) new SubBlockOre("oreCopper", "amunra:ore-copper")
                .setOredictNames("oreCopper")
                .setHarvestInfo("pickaxe", 2).setHardness(3).setResistance(5);

        subCryo = (SubBlockOre) new SubBlockOre("oreCryo", "amunra:ore-cryo")
                // no idea for a oredictname for this one...
                .setHarvestInfo("pickaxe", 3).setHardness(3).setResistance(5);

        subDesh = (SubBlockOre) new SubBlockOre("oreDesh", "amunra:ore-desh")
                .setOredictNames("oreDesh")
                .setHarvestInfo("pickaxe", 3).setHardness(3).setResistance(5);

        subDiamond = (SubBlockOre) new SubBlockOre("oreDiamond", "amunra:ore-diamond")
                .setOredictNames("oreDiamond")
                .setHarvestInfo("pickaxe", 3).setHardness(3).setResistance(5);

        subEmerald = (SubBlockOre) new SubBlockOre("oreEmerald", "amunra:ore-emerald")
                .setOredictNames("oreEmerald")
                .setHarvestInfo("pickaxe", 3).setHardness(3).setResistance(5);

        subIron = (SubBlockOre) new SubBlockOre("oreIron", "amunra:ore-iron")
                .setOredictNames("oreIron")
                .setHarvestInfo("pickaxe", 2).setHardness(3).setResistance(5);

        subLapis = (SubBlockOre) new SubBlockOre("oreLapis", "amunra:ore-lapis")
                .setOredictNames("oreLapis").setHarvestInfo("pickaxe", 1).setHardness(3).setResistance(5);

        subLithium = (SubBlockOre) new SubBlockOre("oreLithium", "amunra:ore-lithium")
                .setOredictNames("oreLithium").setHarvestInfo("pickaxe", 3).setHardness(3).setResistance(5);

        subRuby = (SubBlockOre) new SubBlockOre("oreRuby", "amunra:ore-ruby")
                .setOredictNames("oreRuby").setHarvestInfo("pickaxe", 3).setHardness(3).setResistance(5);

        subSilicon = (SubBlockOre) new SubBlockOre("oreSilicon", "amunra:ore-silicon")
                .setOredictNames("oreSilicon").setHarvestInfo("pickaxe", 1).setHardness(3).setResistance(5);

        subTin = (SubBlockOre) new SubBlockOre("oreTin", "amunra:ore-tin")
                .setOredictNames("oreTin").setHarvestInfo("pickaxe", 2).setHardness(3).setResistance(5);

        subTitanium = (SubBlockOreMultidrop) new SubBlockOreMultidrop("oreTitanium", "amunra:ore-titanium")
                .setOredictNames("oreTitanium")
                .setHarvestInfo("pickaxe", 3).setHardness(3).setResistance(5);

        subCoal = (SubBlockOre) new SubBlockOre("oreCoal", "amunra:ore-coal")
                .setOredictNames("oreCoal").setHarvestInfo("pickaxe", 1).setHardness(2).setResistance(5);

        subLead = (SubBlockOre) new SubBlockOre("oreLead", "amunra:ore-lead")
                .setOredictNames("oreLead").setHarvestInfo("pickaxe", 2).setHardness(2).setResistance(5);

        subUranium = (SubBlockOre) new SubBlockOre("oreUranium", "amunra:ore-uranium")
                .setOredictNames("oreUranium").setHarvestInfo("pickaxe", 3).setHardness(3).setResistance(5);

        subSteel = (SubBlockOre) new SubBlockOre("oreSteel", "amunra:ore-steel")
                .setOredictNames("oreSteel").setHarvestInfo("pickaxe", 3).setHardness(3).setResistance(5);

        subBone = (SubBlockOreMultidrop) new SubBlockOreMultidrop("oreBone", "amunra:ore-bone")
                .setHarvestInfo("pickaxe", 1).setHardness(1).setResistance(5);


        // for the drops, see
        // de.katzenpapst.amunra.item.ARItems.initOreDrops()

        // BASALT ORE

        metaBlockBasaltOre = new BlockOreMulti("basaltMultiOre", "amunra:basalt", Material.rock);
        metaBlockBasaltOre.setMultiblockHarvestLevel(1).setHardness(2.0F).setResistance(10.0F);
        oreGoldBasalt       = metaBlockBasaltOre.addSubBlock(0, subGold);
        oreAluBasalt        = metaBlockBasaltOre.addSubBlock(1, subAlu);
        oreCopperBasalt     = metaBlockBasaltOre.addSubBlock(2, subCopper);
        oreTinBasalt        = metaBlockBasaltOre.addSubBlock(3, subTin);
        oreCryoBasalt       = metaBlockBasaltOre.addSubBlock(4, subCryo);
        oreLithiumBasalt    = metaBlockBasaltOre.addSubBlock(5, subLithium);
        oreIronBasalt       = metaBlockBasaltOre.addSubBlock(6, subIron);
        oreLapisBasalt      = metaBlockBasaltOre.addSubBlock(7, subLapis);
        oreSiliconBasalt    = metaBlockBasaltOre.addSubBlock(8, subSilicon);
        //oreEmeraldObsid        = multiBlockBasaltOre.addSubBlock(9, subEmerald);
        oreTitaniumBasalt   = metaBlockBasaltOre.addSubBlock(9, subTitanium);
        oreUraniumBasalt    = metaBlockBasaltOre.addSubBlock(10, subUranium);
        metaBlockBasaltOre.register();

        // OBSIDIAN ORE
        metaBlockObsidianOre = new BlockOreMulti("obsidianMultiOre", "obsidian", Material.rock);
        metaBlockObsidianOre.setMultiblockHarvestLevel(3).setHardness(50.0F).setResistance(6000.0F);
        oreDiamondObsid = metaBlockObsidianOre.addSubBlock(0, subDiamond);
        oreRubyObsid    = metaBlockObsidianOre.addSubBlock(1, subRuby);
        oreUraniumObsid = metaBlockObsidianOre.addSubBlock(2, subUranium);
        oreLeadObsid    = metaBlockObsidianOre.addSubBlock(3, subLead);
        oreEmeraldObsid = metaBlockObsidianOre.addSubBlock(9, subEmerald);
        metaBlockObsidianOre.register();

        // HARDENED CLAY ORE
        metaBlockHardClayOre = new BlockOreMulti("hardClayMultiOre", "hardened_clay", Material.rock);
        metaBlockHardClayOre.setMultiblockHarvestLevel(1).setHardness(1.25F).setResistance(30.0F);

        oreCoalHardClay     = metaBlockHardClayOre.addSubBlock(0, subCoal);
        oreSiliconHardClay  = metaBlockHardClayOre.addSubBlock(1, subSilicon);
        oreAluHardClay      = metaBlockHardClayOre.addSubBlock(2, subAlu);
        oreCopperHardClay   = metaBlockHardClayOre.addSubBlock(3, subCopper);
        oreTinHardClay      = metaBlockHardClayOre.addSubBlock(4, subTin);
        oreIronHardClay     = metaBlockHardClayOre.addSubBlock(5, subIron);

        metaBlockHardClayOre.register();

        // CONCRETE ORE
        metaBlockConcreteOre = new BlockOreMulti("oldConcreteOre", "amunra:concrete2", Material.rock);
        metaBlockConcreteOre.setMultiblockHarvestLevel(3).setHardness(3.0F).setResistance(30.0F);
        metaBlockConcreteOre.setPrefixOwnBlockName(true);

        oreSteelConcrete = metaBlockConcreteOre.addSubBlock(0, subSteel);
        oreBoneConcrete  = metaBlockConcreteOre.addSubBlock(1, subBone);

        metaBlockConcreteOre.register();


        metaBlockAsteroidOre = new BlockOreMulti("asteroidMultiOre1", AsteroidsModule.TEXTURE_PREFIX + "asteroid0", Material.rock);
        oreRubyAsteroid     = metaBlockAsteroidOre.addSubBlock(0, subRuby);
        oreEmeraldAsteroid  = metaBlockAsteroidOre.addSubBlock(1, subEmerald);
        oreDiamondAsteroid  = metaBlockAsteroidOre.addSubBlock(2, subDiamond);
        oreLithiumAsteroid  = metaBlockAsteroidOre.addSubBlock(3, subLithium);
        oreGoldAsteroid     = metaBlockAsteroidOre.addSubBlock(4, subGold);
        oreLapisAsteroid    = metaBlockAsteroidOre.addSubBlock(5, subLapis);
        oreLeadAsteroid     = metaBlockAsteroidOre.addSubBlock(6, subLead);
        oreUraniumAsteroid  = metaBlockAsteroidOre.addSubBlock(7, subUranium);
        oreCopperAsteroid   = metaBlockAsteroidOre.addSubBlock(8, subCopper);
        metaBlockAsteroidOre.register();

        /*
        // ICE ORE
        metaBlockIceOre = new BlockOreMulti("iceMultiOre", "ice_packed", Material.ice);
        metaBlockIceOre.setMultiblockHarvestLevel(1).setHardness(0.5F).setResistance(2.5F).setStepSound(Block.soundTypeGlass);

        //blockRegistry.addObject(174, "packed_ice", (new BlockPackedIce()).setHardness(0.5F).setStepSound(soundTypeGlass).setBlockName("icePacked").setBlockTextureName("ice_packed"));
        //Blocks.packed_ice.slipperiness;
        metaBlockIceOre.register();
         */

        //Blocks
        //        Block block = (new Block(Material.rock)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundTypePiston).setBlockName("stonebrick").setCreativeTab(CreativeTabs.tabBlock).setBlockTextureName("cobblestone");
        metaBlockRock = new BlockBasicMeta("baseBlockRock",Material.rock);
        // blockRegistry.addObject(7, "bedrock", (new Block(Material.rock)).setBlockUnbreakable().setResistance(6000000.0F).setStepSound(soundTypePiston).setBlockName("bedrock").disableStats().setCreativeTab(CreativeTabs.tabBlock).setBlockTextureName("bedrock"));
        //blockDarkMatter = multiBlockRock.addSubBlock(0, new SubBlock("darkMatter", "amunra:darkmatter", "pickaxe", 9000, 9000, 9000));
        blockBasaltCobble   = metaBlockRock.addSubBlock(0,  new SubBlock("basaltcobble", "amunra:basaltcobble", "pickaxe", 1, 2.0F, 10.0F));
        blockBasalt         = metaBlockRock.addSubBlock(1,  new SubBlockRock("basalt", "amunra:basalt", "pickaxe", 1, 2.0F, 10.0F).setBlockToDrop(blockBasaltCobble));
        blockRedCobble      = metaBlockRock.addSubBlock(2,  new SubBlock("redrockcobble", "amunra:redrockcobble", "pickaxe", 1, 2.0F, 10.0F));
        blockRedRock        = metaBlockRock.addSubBlock(3,  new SubBlockRock("redrock", "amunra:redrock", "pickaxe", 1, 2.0F, 10.0F).setBlockToDrop(blockRedCobble));
        blockYellowCobble   = metaBlockRock.addSubBlock(4,  new SubBlock("yellowcobble", "amunra:olivinebasaltcobble", "pickaxe", 1, 2.0F, 10.0F));
        blockYellowRock     = metaBlockRock.addSubBlock(5,  new SubBlockRock("yellowrock", "amunra:olivinebasalt", "pickaxe", 1, 2.0F, 10.0F).setBlockToDrop(blockYellowCobble));
        blockAluCrate       = metaBlockRock.addSubBlock(6,  new SubBlockMassive("alucrate", "amunra:alucrate", "pickaxe", 0, 1, 1).setMass(0.1F));

        blockBasaltBrick    = metaBlockRock.addSubBlock(7,  new SubBlock("basaltbrick", "amunra:basaltbrick", "pickaxe", 1, 2.0F, 10.0F));
        blockSmoothBasalt   = metaBlockRock.addSubBlock(8,  new SubBlock("smoothbasalt", "amunra:smoothbasalt", "pickaxe", 1, 2.0F, 10.0F));
        blockObsidianBrick  = metaBlockRock.addSubBlock(9,  new SubBlock("obsidianbrick", "amunra:obsidianbrick", "pickaxe", 3, 50.0F, 6000.0F));
        blockOldConcrete    = metaBlockRock.addSubBlock(10, new SubBlock("oldConcrete", "amunra:concrete2", "pickaxe", 3, 3.0F, 20.0F));

        blockUraniumBlock   = metaBlockRock.addSubBlock(11,  new SubBlockMassive("blockUranium", "amunra:deco_uranium_block", "pickaxe", 0, 1, 1).setMass(1000.0F));


        blockMsBase       = metaBlockRock.addSubBlock(12,  new SubBlockMassive("msBaseBlock", GalacticraftCore.TEXTURE_PREFIX + "deco_aluminium_4", "", -1, -1.0F, 6000000.0F).setMass(0.1F));

        blockWorkbench    = metaBlockRock.addSubBlock(13, new CraftingBlock("workbench"));

        blockDarkmatter   = metaBlockRock.addSubBlock(14, new SubBlock("darkMatter", "amunra:darkmatter", "pickaxe", 5, 50, 6000.0F));



        metaBlockRock.register();





        metaBlockCrystal = new BlockBasicMeta("baseBlockCrystal", Material.glass);
        // blockRegistry.addObject(89, "glowstone", (new BlockGlowstone(Material.glass)).setHardness(0.3F).setStepSound(soundTypeGlass).setLightLevel(1.0F).setBlockName("lightgem").setBlockTextureName("glowstone"));

        SubBlock coral = new SubBlock("glowCoral", "amunra:coral", "pickaxe", 2, 0.9F, 2.75F);
        coral.setStepSound(Block.soundTypeGlass);
        coral.setLightLevel(1.0F);

        blockGlowingCoral = metaBlockCrystal.addSubBlock(0, coral);

        metaBlockCrystal.register();


        metaBlockDirt = new BlockBasicMeta("baseBlockGround", Material.ground);
        metaBlockDirt.setStepSound(Block.soundTypeGravel);


        blockMethaneDirt    = metaBlockDirt.addSubBlock(0, new SubBlock("methanedirt", "amunra:methanedirt", "shovel", 0, 0.5F, 2.5F));
        blockDust           = metaBlockDirt.addSubBlock(1, new DustBlock("dustblock", "amunra:dust", "shovel", 0, 0, 0));

        metaBlockDirt.register();


        metaBlockGrass = new BlockGrassMeta("baseGrass", Material.grass);
        metaBlockGrass.setStepSound(Block.soundTypeGrass);
        blockMethaneGrass         = metaBlockGrass.addSubBlock(0, new MethaneGrass("methanegrass"));
        blockUnderwaterGrass      = metaBlockGrass.addSubBlock(1, new UnderwaterGrass("underwaterBlueGrass", "amunra:claygrasstop", "amunra:claygrassside", "clay"));
        blockVacuumGrass          = metaBlockGrass.addSubBlock(2, new VacuumGrass("vaccumRedGrass", "amunra:methanegrass-2", "amunra:methanegrassside-2", "amunra:methanedirt"));
        metaBlockGrass.register();

        metaBlockFalling = new BlockFallingMeta("baseFalling", Material.sand);
        metaBlockFalling.setStepSound(Block.soundTypeGravel);

        blockObsidiSand     = metaBlockFalling.addSubBlock(0, new SubBlock("obsidianSand", "amunra:obsidiansand", "shovel", 2, 20.0F, 100.0F));
        blockObsidiGravel   = metaBlockFalling.addSubBlock(1, new SubBlock("obsidianGravel", "amunra:obsidiangravel", "shovel", 2, 30.0F, 300.0F));
        blockBasaltRegolith = metaBlockFalling.addSubBlock(2, new SubBlock("basaltregolith", "amunra:black_stone", "shovel", 1, 1.0F, 3.0F));

        metaBlockFalling.register();

        //Blocks.diamond_ore

        metaBlockPlant = new BlockBushMulti("basePlant", Material.plants);
        metaBlockPlant.setStepSound(Block.soundTypeGrass);
        blockMethaneTGrass = metaBlockPlant.addSubBlock(0, (SubBlock) new MethaneTallGrass("methaneTallGrass", "amunra:methanetallgrass")
                .setHarvestInfo(null, 0).setHardness(0.0F));
        metaBlockPlant.register();

        // LOGS
        metaBlockLog = new BlockLogMeta("log1", Material.wood);
        metaBlockLog.setStepSound(Block.soundTypeWood);

        blockMethaneLog = metaBlockLog.addSubBlock(0, new SubBlockWood("methanewood", "amunra:log_methane", "amunra:log_methane_top", "axe", 1));

        metaBlockLog.register();

        // WOOD, other wood, etc
        metaBlockNonRotational  = new BlockBasicMeta("wood1", Material.wood);
        blockPodBark            = metaBlockNonRotational.addSubBlock(0, new SubBlock("podBark", "amunra:pod_bark", "axe", 0));
        blockPodLeaf            = metaBlockNonRotational.addSubBlock(1, (SubBlock) new PodMeatBlock("podleaf", "amunra:podleaves").setLightLevel(0.8F));
        blockMethanePlanks      = metaBlockNonRotational.addSubBlock(2, new SubBlock("methanePlanks", "amunra:planks_methane", "axe", 0));
        blockPodPlanks          = metaBlockNonRotational.addSubBlock(3, new SubBlock("podPlanks", "amunra:planks_pod", "axe", 0));
        metaBlockNonRotational.register();

        // LEAVES
        metaBlockLeaf = new BlockLeafMeta(Material.leaves, true);
        blockMethaneLeaf = metaBlockLeaf.addSubBlock(0, new SubBlockLeaf("methaneleaf", "amunra:leaves_methane"));

        metaBlockLeaf.register();

        // SAPLINGS
        metaBlockSapling = new BlockBushMulti("saplings", Material.grass, 7);
        metaBlockSapling.setTickRandomly(true);

        blockMethaneSapling = metaBlockSapling.addSubBlock(0, new ARTreeSapling("mTreeSapling", "amunra:methane_tree_sapling").setWood(blockMethaneLog).setLeaves(blockMethaneLeaf));
        blockPodSapling     = metaBlockSapling.addSubBlock(1, new PodSapling("podSapling", "amunra:lumipod_sapling").setWood(blockPodBark).setLeaves(blockPodLeaf));


        metaBlockSapling.register();

        // MACHINES
        metaBlockMachine = new BlockMachineMeta("machines1", Material.iron);

        blockIsotopeGeneratorBasic = metaBlockMachine.addSubBlock(0, new BlockIsotopeGenerator(
                "isotopeGeneratorBasic",
                AmunRa.TEXTUREPREFIX + "machine_nuclear", // AmunRa.TEXTUREPREFIX + "machine_nuclear"
                GalacticraftCore.TEXTURE_PREFIX + "machine_output",
                GalacticraftCore.TEXTURE_PREFIX + "machine_blank",
                0.5F
            )
        );

        blockIsotopeGeneratorAdvanced = metaBlockMachine.addSubBlock(1, new BlockIsotopeGenerator(
                "isotopeGeneratorAdvanced",
                AmunRa.TEXTUREPREFIX + "machine_nuclear_advanced", // AmunRa.TEXTUREPREFIX + "machine_nuclear"
                AsteroidsModule.TEXTURE_PREFIX + "machine_output",
                AsteroidsModule.TEXTURE_PREFIX + "machine",
                5.0F
            )
        );

        blockMothershipController = metaBlockMachine.addSubBlock(2, new BlockMothershipController(
                "mothershipController",
                AmunRa.TEXTUREPREFIX + "controller",
                AsteroidsModule.TEXTURE_PREFIX + "machine"
            )
        );

        blockMothershipSettings = metaBlockMachine.addSubBlock(3, new BlockMothershipSettings(
                "mothershipSettings",
                AmunRa.TEXTUREPREFIX + "settings-terminal",
                AsteroidsModule.TEXTURE_PREFIX + "machine"));

        metaBlockMachine.register();

        // MOTHERSHIP ENGINES
        // jets, aka, the block where the flames come out
        metaBlockMothershipEngineJet = new BlockMothershipJetMeta("machines2", Material.iron);

        // rocket engine
        blockMsEngineRocketJet = metaBlockMothershipEngineJet.addSubBlock(0, new MothershipEngineJetRocket(
                "mothershipEngineRocketJet",
                AsteroidsModule.TEXTURE_PREFIX + "machine",
                AmunRa.TEXTUREPREFIX+"mothership-jet-rocket"//AsteroidsModule.TEXTURE_PREFIX + "machine"
            )
        );

        blockMsEngineIonJet = metaBlockMothershipEngineJet.addSubBlock(1, new MothershipEngineJetIon(
                "mothershipEngineRocketJetIon",
                AsteroidsModule.TEXTURE_PREFIX + "machine",
                AmunRa.TEXTUREPREFIX+"mothership-jet-ion"
            )
        );

        metaBlockMothershipEngineJet.register();

        metaBlockMachine2 = new BlockMachineMeta("machines3", Material.iron);

        blockScale = metaBlockMachine2.addSubBlock(0, new BlockScale("blockScale",
                AmunRa.TEXTUREPREFIX+"scale_side",
                AmunRa.TEXTUREPREFIX+"scale_top",
                AmunRa.TEXTUREPREFIX+"scale",
                GalacticraftCore.TEXTURE_PREFIX+"machine"));

        blockGravity = metaBlockMachine2.addSubBlock(1, new BlockGravitation("gravity",
                AmunRa.TEXTUREPREFIX + "gravity",
                AsteroidsModule.TEXTURE_PREFIX + "machine_input",
                AsteroidsModule.TEXTURE_PREFIX + "machine",
                AsteroidsModule.TEXTURE_PREFIX + "machine_input")
                );

        metaBlockMachine2.register();

        metaBlockMachineSpecialRender1 = new BlockMachineMetaDummyRender("machines4", Material.iron);

        blockShuttleDock = metaBlockMachineSpecialRender1.addSubBlock(0, new BlockShuttleDock("shuttleDock", AsteroidsModule.TEXTURE_PREFIX + "machine"));

        blockHydro = metaBlockMachineSpecialRender1.addSubBlock(1, new BlockHydroponics(
                "hydroponics",
                GalacticraftCore.TEXTURE_PREFIX+"machine_input"));

        metaBlockMachineSpecialRender1.register();

        metaBlockFake = new BlockMetaFake("blockFake", Material.iron);
        fakeBlockSealable = metaBlockFake.addSubBlock(0, new FakeBlock("fakeBlockSealable", AsteroidsModule.TEXTURE_PREFIX + "machine"));
        metaBlockFake.register();


        // boosters, aka the blocks which are attached to the jets
        metaBlockMothershipEngineBooster = new BlockMothershipBoosterMeta("msBoosters1", Material.iron);

        blockMsEngineRocketBooster = metaBlockMothershipEngineBooster.addSubBlock(0, new MothershipEngineBoosterBase(
                "mothershipEngineRocketBooster",
                AsteroidsModule.TEXTURE_PREFIX + "machine",
                AsteroidsModule.TEXTURE_PREFIX + "machine_side"
            )
        );

        blockMsEngineIonBooster = metaBlockMothershipEngineBooster.addSubBlock(1, new MothershipEngineBoosterIon(
                "mothershipEngineRocketBoosterIon",
                AsteroidsModule.TEXTURE_PREFIX + "machine",
                AsteroidsModule.TEXTURE_PREFIX + "machine_side_warning"
            )
        );

        metaBlockMothershipEngineBooster.register();
        // MOTHERSHIP ENGINES END


        setLeafDroppingSapling(blockMethaneLeaf, blockMethaneSapling);



        // STAIRS
        stairsObsidianBrick = new BlockStairsAR(blockObsidianBrick);
        stairsObsidianBrick.register();

        stairsSmoothBasalt = new BlockStairsAR(blockSmoothBasalt);
        stairsSmoothBasalt.register();

        stairsBasaltBrick = new BlockStairsAR(blockBasaltBrick);
        stairsBasaltBrick.register();

        stairsBasaltBlock = new BlockStairsAR(blockBasalt);
        stairsBasaltBlock.register();

        stairsAluCrate = new BlockStairsAR(blockAluCrate);
        stairsAluCrate.register();

        stairsMethanePlanks = new BlockStairsAR(blockMethanePlanks);
        stairsMethanePlanks.register();

        stairsPodPlanks = new BlockStairsAR(blockPodPlanks);
        stairsPodPlanks.register();

        initSlabs();

        registerOreDict();
    }

    private static void initSlabs() {
        // SLABS

        // rock
        metaSlabRock        = new BlockSlabMeta("rockSlab", Material.rock);
        metaDoubleslabRock  = new BlockDoubleslabMeta("rockDoubleslab", Material.rock, metaSlabRock);
        slabBasaltBlock     = metaSlabRock.addSubBlock(0, blockBasalt);
        slabBasaltBrick     = metaSlabRock.addSubBlock(1, blockBasaltBrick);
        slabBasaltSmooth    = metaSlabRock.addSubBlock(2, blockSmoothBasalt);
        slabObsidianBrick   = metaSlabRock.addSubBlock(3, blockObsidianBrick);
        slabAluCrate        = metaSlabRock.addSubBlock(4, blockAluCrate);
        metaDoubleslabRock.register(); // register the doubleslab first
        metaSlabRock.register();

        // wood
        metaSlabWood        = new BlockSlabMeta("woodSlab", Material.wood);
        metaDoubleslabWood  = new BlockDoubleslabMeta("woodDoubleslab", Material.wood, metaSlabWood);
        slabPodPlanks       = metaSlabWood.addSubBlock(0, blockPodPlanks);
        slabMethanePlanks   = metaSlabWood.addSubBlock(1, blockMethanePlanks);
        metaDoubleslabWood.register();
        metaSlabWood.register();
    }

    private static void setLeafDroppingSapling(BlockMetaPair leaf, BlockMetaPair sapling) {
        ((SubBlockLeaf)((BlockLeafMeta)leaf.getBlock()).getSubBlock(leaf.getMetadata())).setSaplingDropped(sapling);
    }

    protected static void registerOreDict() {
        OreDictionary.registerOre("blockBasalt", getItemStack(blockBasalt, 1));

        OreDictionary.registerOre("logWood", getItemStack(blockMethaneLog, 1));
        OreDictionary.registerOre("logWood", getItemStack(blockPodBark, 1));

        OreDictionary.registerOre("plankWood", getItemStack(blockPodPlanks, 1));
        OreDictionary.registerOre("plankWood", getItemStack(blockMethanePlanks, 1));

        OreDictionary.registerOre("slabWood", getItemStack(slabMethanePlanks, 1));
        OreDictionary.registerOre("slabWood", getItemStack(slabPodPlanks, 1));

        OreDictionary.registerOre("treeSapling", getItemStack(blockPodSapling, 1));
        OreDictionary.registerOre("treeSapling", getItemStack(blockMethaneSapling, 1));

        // blockUraniumBlock
        OreDictionary.registerOre("blockUranium", getItemStack(blockUraniumBlock, 1));



    }


    public static SubBlockOre getSubBlockOre(BlockMetaPair bmp) {
        return (SubBlockOre) ((BlockBasicMeta)bmp.getBlock()).getSubBlock(bmp.getMetadata());
    }

}
