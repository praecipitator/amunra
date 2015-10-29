package de.katzenpapst.amunra.item;

public class ARItems {
	public static ItemBasicMulti baseItem = null;

	public static ItemDamagePair waferEnder;
	public static ItemDamagePair porcodonMeat;
	public static ItemDamagePair dustMote;

	public static void initItems()
    {
		baseItem = new ItemBasicMulti("baseItem");

		waferEnder 	 	= baseItem.addSubItem(0, new SubItem("waferEnder","waferEnder"));
		porcodonMeat 	= baseItem.addSubItem(1, new PorcodonMeat());
		dustMote		= baseItem.addSubItem(2, new SubItem("dustMote","dust"));

		baseItem.register();
		//itemRegistry.addObject(367, "rotten_flesh", (new ItemFood(4, 0.1F, true)).setPotionEffect(Potion.hunger.id, 30, 0, 0.8F).setUnlocalizedName("rottenFlesh").setTextureName("rotten_flesh"));
		//Items.rotten_flesh
    }
}
