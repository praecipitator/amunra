package de.katzenpapst.amunra.item;

import micdoodle8.mods.galacticraft.core.Constants;
import cpw.mods.fml.common.registry.GameRegistry;

public class ARItems {
	public static ItemBasicMulti baseItem = null;
	public static void initItems()
    {
		baseItem = new ItemBasicMulti("baseItem");
		
		baseItem.addSubItem(0, new SubItem("waferEnder","waferEnder"));
		baseItem.addSubItem(1, new SubItem("porcodonMeat","green_bacon"));
		
		baseItem.register();
    }
}
