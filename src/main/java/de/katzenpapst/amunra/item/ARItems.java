package de.katzenpapst.amunra.item;

import micdoodle8.mods.galacticraft.core.items.GCItems;
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems;
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.registry.GameRegistry;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.block.ARBlocks;

public class ARItems {
    public static ItemBasicMulti baseItem = null;
    public static ItemSchematicMulti schematicItem = null;
    public static ItemRaygun raygun = null;
    public static ItemCryogun cryogun = null;
    public static ItemNanotool nanotool = null;
    public static ItemShuttle shuttleItem = null;
    public static ItemJet jetItemMeta = null;
    public static ItemDamagePair jetItem = null;
    public static ItemDamagePair jetItemIon = null;

    public static ItemThermalSuit advancedThermalSuit = null;

    public static ItemBaseBattery batteryEnder = null;
    public static ItemBaseBattery batteryLithium = null;
    public static ItemBaseBattery batteryQuantum = null;
    public static ItemBaseBattery batteryNuclear = null;

    public static ItemDamagePair waferEnder;
    public static ItemDamagePair porcodonMeat;
    public static ItemDamagePair dustMote;
    public static ItemDamagePair rubyGem;
    public static ItemDamagePair coldCrystal;
    public static ItemDamagePair laserDiode;
    public static ItemDamagePair cryoDiode;
    public static ItemDamagePair lithiumGem;
    public static ItemDamagePair lithiumMesh;
    public static ItemDamagePair alienBook;
    public static ItemDamagePair leadIngot;
    public static ItemDamagePair uraniumIngot;
    public static ItemDamagePair steelIngot;
    public static ItemDamagePair ancientRebar;
    public static ItemDamagePair shuttleLegs;
    public static ItemDamagePair lightPlating;
    public static ItemDamagePair noseCone;
    public static ItemDamagePair thermalControl;
    public static ItemDamagePair thermalHelm;
    public static ItemDamagePair thermalChest;
    public static ItemDamagePair thermalLegs;
    public static ItemDamagePair thermalBoots;
    public static ItemDamagePair tricorder;
    public static ItemDamagePair compressedGold;
    public static ItemDamagePair goldFoil;
    public static ItemDamagePair transformer;
    public static ItemDamagePair shuttleTank;
    public static ItemDamagePair uraniumMesh;
    public static ItemDamagePair dockDoor;
    public static ItemDamagePair dockGangway;

    public static ItemDamagePair fakeItemEnergy;

    public static ItemDamagePair shuttleSchematic;

    public static void initItems()
    {
        baseItem = new ItemBasicMulti("baseItem");

        waferEnder      = baseItem.addSubItem(0, new SubItem("waferEnder", "waferEnder"));
        porcodonMeat    = baseItem.addSubItem(1, new PorcodonMeat());
        dustMote        = baseItem.addSubItem(2, new SubItem("dustMote", "dust"));
        coldCrystal     = baseItem.addSubItem(3, new SubItem("cryoCrystal", "coldcrystal"));
        laserDiode      = baseItem.addSubItem(4, new SubItem("laserDiode", "laserDiode"));
        cryoDiode       = baseItem.addSubItem(5, new SubItem("cryoDiode", "cryoDiode"));
        rubyGem         = baseItem.addSubItem(6, new SubItem("rubyGem", "ruby"));
        lithiumGem      = baseItem.addSubItem(7, new SubItem("lithiumGem", "lithiumgem"));
        lithiumMesh     = baseItem.addSubItem(8, new SubItem("lithiumMesh", "lithiumMesh"));
        alienBook       = baseItem.addSubItem(9, new SubItem("alienBook", "alien_book"));
        leadIngot       = baseItem.addSubItem(10, new SubItem("leadIngot", "lead_ingot"));
        uraniumIngot    = baseItem.addSubItem(11, new SubItem("uraniumIngot", "uranium_ingot"));
        steelIngot      = baseItem.addSubItem(12, new SubItem("steelIngot", "steel_ingot"));
        ancientRebar    = baseItem.addSubItem(13, new SubItem("ancientRebar", "ancient_rebar"));
        shuttleLegs     = baseItem.addSubItem(14, new SubItem("shuttleLegs", "shuttle_leg"));
        lightPlating    = baseItem.addSubItem(15, new SubItem("lightPlate", "lightPlate"));
        noseCone        = baseItem.addSubItem(16, new SubItem("shuttleNoseCone", "shuttleNoseCone"));
        thermalControl  = baseItem.addSubItem(17, new SubItem("thermalController", "thermalController"));
        tricorder       = baseItem.addSubItem(18, new ItemTricorder("tricorder", "tricorder"));
        compressedGold  = baseItem.addSubItem(19, new SubItem("compressedGold", "compressedGold"));
        goldFoil        = baseItem.addSubItem(20, new SubItem("goldFoil", "goldfoil"));
        transformer     = baseItem.addSubItem(21, new SubItem("transformer", "transformer"));
        uraniumMesh     = baseItem.addSubItem(22, new SubItem("uranMesh", "lithiumUraniumMesh"));
        shuttleTank     = baseItem.addSubItem(23, new SubItem("shuttleTank", "tank"));
        dockGangway     = baseItem.addSubItem(24, new SubItem("dockGangway", "gangway"));
        dockDoor        = baseItem.addSubItem(25, new SubItem("dockDoor", "dock-door"));


        //fakeItemEnergy  = baseItem.addSubItem(Integer.MAX_VALUE, new SubItem("fakeItemEnergy", "energy"));

        baseItem.register();

        advancedThermalSuit = new ItemThermalSuit("thermalSuit", 5, "thermal_helmet", "thermal_chest", "thermal_leggings", "thermal_boots");
        thermalHelm = advancedThermalSuit.getHelmet();
        thermalChest = advancedThermalSuit.getChest();
        thermalLegs = advancedThermalSuit.getLegts();
        thermalBoots = advancedThermalSuit.getBoots();
        advancedThermalSuit.register();

        schematicItem = new ItemSchematicMulti("schematic");
        shuttleSchematic = schematicItem.addSubItem(0, new SubItem("schematicShuttle", "schematicShuttle"));
        schematicItem.register();

        shuttleItem = new ItemShuttle("itemShuttle");
        GameRegistry.registerItem(shuttleItem, shuttleItem.getUnlocalizedName(), AmunRa.MODID);

        jetItemMeta = new ItemJet(ARBlocks.metaBlockMothershipEngineJet, "mothership-jet-rocket-meta");
        jetItem = new ItemDamagePair(jetItemMeta, ARBlocks.blockMsEngineRocketJet.getMetadata());
        jetItemIon = new ItemDamagePair(jetItemMeta, ARBlocks.blockMsEngineIonJet.getMetadata());
        /*
        jetItem = new ItemJet(ARBlocks.blockMsEngineRocketJet, "mothership-jet-rocket");
        GameRegistry.registerItem(jetItem, jetItem.getUnlocalizedName(), AmunRa.MODID);

        jetItemIon = new ItemJet(ARBlocks.blockMsEngineIonJet, "mothership-ion-rocket");
        GameRegistry.registerItem(jetItemIon, jetItemIon.getUnlocalizedName(), AmunRa.MODID);
        */
        GameRegistry.registerItem(jetItemMeta, jetItemMeta.getUnlocalizedName(), AmunRa.MODID);

        raygun = new ItemRaygun("raygun");
        GameRegistry.registerItem(raygun, raygun.getUnlocalizedName(), AmunRa.MODID);

        cryogun = new ItemCryogun("cryogun");
        GameRegistry.registerItem(cryogun, cryogun.getUnlocalizedName(), AmunRa.MODID);

        nanotool =  new ItemNanotool("nanotool");
        GameRegistry.registerItem(nanotool, nanotool.getUnlocalizedName(), AmunRa.MODID);

        // 4x the capacity of the standard battery. Simple upgrade over the regular battery which can be done on earth
        batteryEnder = new ItemBaseBattery("battery-ender", 60000, 400);
        GameRegistry.registerItem(batteryEnder, batteryEnder.getUnlocalizedName(), AmunRa.MODID);

        // 16x the capacity of the standard battery
        batteryLithium = new ItemBaseBattery("battery-lithium", 240000, 800);
        GameRegistry.registerItem(batteryLithium, batteryLithium.getUnlocalizedName(), AmunRa.MODID);


        // 64x the capacity of the standard battery, "epic" uncraftable loot battery
        batteryQuantum = new ItemBaseBattery("battery-quantum", 960000, 1600);
        GameRegistry.registerItem(batteryQuantum, batteryQuantum.getUnlocalizedName(), AmunRa.MODID);
        // x128 -> 1.920.000; x256  -> 3840000
        // storage container: 500.000
        // adv. storage container: 2.500.000

        batteryNuclear = new ItemNuclearBattery("battery-nuclear", 10000, 0.01F);
        GameRegistry.registerItem(batteryNuclear, batteryNuclear.getUnlocalizedName(), AmunRa.MODID);

        //itemRegistry.addObject(367, "rotten_flesh", (new ItemFood(4, 0.1F, true)).setPotionEffect(Potion.hunger.id, 30, 0, 0.8F).setUnlocalizedName("rottenFlesh").setTextureName("rotten_flesh"));
        //Items.rotten_flesh
        initOreDrops();
        registerOreDict();
    }

    protected static void initOreDrops() {

        // cryo stuff
        ARBlocks.subCryo.setDroppedItem(coldCrystal).setXpDrop(2, 4);

        // diamond
        ARBlocks.subDiamond.setDroppedItem(Items.diamond).setXpDrop(3, 7).setSmeltItem(Items.diamond, 1);

        // ruby
        ARBlocks.subRuby.setDroppedItem(rubyGem).setXpDrop(3, 7).setSmeltItem(rubyGem.getItemStack(1));

        // coal
        ARBlocks.subCoal.setDroppedItem(Items.coal).setXpDrop(3, 7).setSmeltItem(Items.coal, 1);

        // emerald
        ARBlocks.subEmerald.setDroppedItem(Items.emerald).setXpDrop(3, 7).setSmeltItem(Items.emerald, 1);

        // lithium
        ARBlocks.subLithium.setDroppedItem(lithiumGem).setXpDrop(3, 7);

        // lapis
        ARBlocks.subLapis.setDroppedItem(new ItemDamagePair(Items.dye, 4)).setMinDropRate(4).setXpDrop(2, 5);

        ARBlocks.subSilicon.setDroppedItem(new ItemDamagePair(GCItems.basicItem, 2));

        // gold
        ARBlocks.subGold.setSmeltItem(new ItemStack(Items.gold_ingot, 1));

        // alu
        // GCCoreUtil.registerGalacticraftItem("ingotAluminum",
        // GCItems.basicItem, 5);
        ARBlocks.subAlu.setSmeltItem(new ItemStack(GCItems.basicItem, 1, 5));

        // copper
        //GCCoreUtil.registerGalacticraftItem("ingotCopper", GCItems.basicItem, 3);
        ARBlocks.subCopper.setSmeltItem(new ItemStack(GCItems.basicItem, 1, 3));

        // iron
        ARBlocks.subIron.setSmeltItem(new ItemStack(Items.iron_ingot, 1));

        // tin
        // GCCoreUtil.registerGalacticraftItem("ingotTin", GCItems.basicItem,
        // 4);
        ARBlocks.subTin.setSmeltItem(new ItemStack(GCItems.basicItem, 1, 4));

        // desh
        ARBlocks.subDesh
                .setDroppedItem(new ItemDamagePair(MarsItems.marsItemBasic, 0))
                .setMinDropRate(1)
                .setBonusMultiplier(0.5F)
                .setSmeltItem(new ItemStack(MarsItems.marsItemBasic, 1, 0));

        // titanium
        ARBlocks.subTitanium
                .addDroppedItem(AsteroidsItems.basicItem, 3, 1, 2)	// iron
                .addDroppedItem(AsteroidsItems.basicItem, 4, 1, 2);	// titanium


        // bone
        ARBlocks.subBone
                .addDroppedItem(Items.bone, 0, 0, 4)            // bone
                .addDroppedItem(Items.dye, 15, 2, 6)            // bonemeal
                .addDroppedItem(Items.skull, 0, 0, 1, 0.05F);   // skull

        ARBlocks.subSteel
                .setDroppedItem(ARItems.ancientRebar)
                .setXpDrop(3, 7)
                .setBonusMultiplier(0.5F)
                .setSmeltItem(steelIngot.getItemStack(1));

        ARBlocks.subLead
                .setSmeltItem(ARItems.leadIngot.getItemStack(1));

        ARBlocks.subUranium
                .setSmeltItem(ARItems.uraniumIngot.getItemStack(1));
    }

    protected static void registerOreDict() {
        // net.minecraftforge.oredict.OreDictionary
        // https://web.archive.org/web/20160514155630/http://www.minecraftforge.net/wiki/Common_Oredict_names
        OreDictionary.registerOre("gemRuby", rubyGem.getItemStack(1));
        OreDictionary.registerOre("gemSpodumene", lithiumGem.getItemStack(1));

        OreDictionary.registerOre("ingotSteel", steelIngot.getItemStack(1));
        OreDictionary.registerOre("ingotLead", leadIngot.getItemStack(1));
        OreDictionary.registerOre("ingotUranium", uraniumIngot.getItemStack(1));

        OreDictionary.registerOre("compressedGold", compressedGold.getItemStack(1));

    }
}
