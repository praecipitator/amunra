package de.katzenpapst.amunra.item;

import cpw.mods.fml.common.registry.GameRegistry;
import de.katzenpapst.amunra.AmunRa;

public class ARItems {
	public static ItemBasicMulti baseItem = null;
	public static ItemRaygun raygun = null;
	public static ItemCryogun cryogun = null;

	public static ItemDamagePair waferEnder;
	public static ItemDamagePair porcodonMeat;
	public static ItemDamagePair dustMote;
	public static ItemDamagePair coldCrystal;
	public static ItemDamagePair laserDiode;
	public static ItemDamagePair cryoDiode;

	public static void initItems()
    {
		baseItem = new ItemBasicMulti("baseItem");

		waferEnder 	 	= baseItem.addSubItem(0, new SubItem("waferEnder","waferEnder"));
		porcodonMeat 	= baseItem.addSubItem(1, new PorcodonMeat());
		dustMote		= baseItem.addSubItem(2, new SubItem("dustMote","dust"));
		coldCrystal		= baseItem.addSubItem(3, new SubItem("cryoCrystal","coldcrystal"));
		laserDiode		= baseItem.addSubItem(4, new SubItem("laserDiode","laserDiode"));
		cryoDiode		= baseItem.addSubItem(5, new SubItem("cryoDiode","cryoDiode"));

		baseItem.register();

		raygun = new ItemRaygun("raygun");
		GameRegistry.registerItem(raygun, raygun.getUnlocalizedName(), AmunRa.MODID);

		cryogun = new ItemCryogun("cryogun");
		GameRegistry.registerItem(cryogun, cryogun.getUnlocalizedName(), AmunRa.MODID);

		//itemRegistry.addObject(367, "rotten_flesh", (new ItemFood(4, 0.1F, true)).setPotionEffect(Potion.hunger.id, 30, 0, 0.8F).setUnlocalizedName("rottenFlesh").setTextureName("rotten_flesh"));
		//Items.rotten_flesh
    }
}
