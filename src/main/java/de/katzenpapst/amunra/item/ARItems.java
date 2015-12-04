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
	public static ItemRaygun raygun = null;
	public static ItemCryogun cryogun = null;

	public static ItemBaseBattery batteryEnder = null;
	public static ItemBaseBattery batteryLithium = null;
	public static ItemBaseBattery batteryQuantum = null;

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

	public static void initItems()
    {
		baseItem = new ItemBasicMulti("baseItem");

		waferEnder 	 	= baseItem.addSubItem(0, new SubItem("waferEnder","waferEnder"));
		porcodonMeat 	= baseItem.addSubItem(1, new PorcodonMeat());
		dustMote		= baseItem.addSubItem(2, new SubItem("dustMote","dust"));
		coldCrystal		= baseItem.addSubItem(3, new SubItem("cryoCrystal","coldcrystal"));
		laserDiode		= baseItem.addSubItem(4, new SubItem("laserDiode","laserDiode"));
		cryoDiode		= baseItem.addSubItem(5, new SubItem("cryoDiode","cryoDiode"));
		rubyGem			= baseItem.addSubItem(6, new SubItem("rubyGem","ruby"));
		lithiumGem		= baseItem.addSubItem(7, new SubItem("lithiumGem","lithiumgem"));
		lithiumMesh		= baseItem.addSubItem(8, new SubItem("lithiumMesh","lithiumMesh"));
		alienBook		= baseItem.addSubItem(9, new SubItem("alienBook","alien_book"));


		baseItem.register();

		raygun = new ItemRaygun("raygun");
		GameRegistry.registerItem(raygun, raygun.getUnlocalizedName(), AmunRa.MODID);

		cryogun = new ItemCryogun("cryogun");
		GameRegistry.registerItem(cryogun, cryogun.getUnlocalizedName(), AmunRa.MODID);

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

		//itemRegistry.addObject(367, "rotten_flesh", (new ItemFood(4, 0.1F, true)).setPotionEffect(Potion.hunger.id, 30, 0, 0.8F).setUnlocalizedName("rottenFlesh").setTextureName("rotten_flesh"));
		//Items.rotten_flesh
		initOreDrops();
		registerOreDict();
    }

	protected static void initOreDrops() {

		ARBlocks.subCryo.setDroppedItem(coldCrystal).setXpDrop(2, 4);
		ARBlocks.subDiamond.setDroppedItem(Items.diamond).setXpDrop(3, 7);
		ARBlocks.subRuby.setDroppedItem(rubyGem).setXpDrop(3, 7);
		ARBlocks.subEmerald.setDroppedItem(Items.emerald).setXpDrop(3, 7);
		ARBlocks.subLithium.setDroppedItem(lithiumGem).setXpDrop(3, 7);
		ARBlocks.subLapis.setDroppedItem(new ItemDamagePair(Items.dye, 4)).setMinDropRate(4).setXpDrop(2, 5);
		ARBlocks.subSilicon.setDroppedItem(new ItemDamagePair(GCItems.basicItem, 2));//.setXpDrop(2, 5);
		ARBlocks.subDesh.setDroppedItem(new ItemDamagePair(MarsItems.marsItemBasic, 0)).setMinDropRate(1).setBonusMultiplier(0.5F);

		ARBlocks.subGold.setSmeltItem(new ItemStack(Items.gold_ingot, 1));
		// GCCoreUtil.registerGalacticraftItem("ingotAluminum", GCItems.basicItem, 5);
		ARBlocks.subAlu.setSmeltItem(new ItemStack(GCItems.basicItem, 1, 5));
		//GCCoreUtil.registerGalacticraftItem("ingotCopper", GCItems.basicItem, 3);
		ARBlocks.subCopper.setSmeltItem(new ItemStack(GCItems.basicItem, 1, 3));
		ARBlocks.subIron.setSmeltItem(new ItemStack(Items.iron_ingot, 1));
		//GCCoreUtil.registerGalacticraftItem("ingotTin", GCItems.basicItem, 4);
		ARBlocks.subTin.setSmeltItem(new ItemStack(GCItems.basicItem, 1, 4));

		ARBlocks.subDesh.setSmeltItem(new ItemStack(MarsItems.marsItemBasic, 1, 0));


		ARBlocks.subTitanium.addDroppedItem(AsteroidsItems.basicItem, 3, 1, 2);	// iron
		ARBlocks.subTitanium.addDroppedItem(AsteroidsItems.basicItem, 4, 1, 2);	// titanium

	}

	protected static void registerOreDict() {
		// http://www.minecraftforge.net/wiki/Common_Oredict_names
		OreDictionary.registerOre("gemRuby", rubyGem.getItemStack(1));
		OreDictionary.registerOre("gemSpodumene", lithiumGem.getItemStack(1));
	}
}
