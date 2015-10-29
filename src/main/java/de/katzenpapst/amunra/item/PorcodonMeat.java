package de.katzenpapst.amunra.item;

import net.minecraft.potion.Potion;

public class PorcodonMeat extends SubItemFood {

	public PorcodonMeat() {
		super("porcodonMeat", "green_bacon", "item.porcodonMeat.description", 4, 0.1F);
		// itemRegistry.addObject(367, "rotten_flesh",
		// (new ItemFood(4, 0.1F, true)).setPotionEffect(Potion.hunger.id, 30, 0, 0.8F).setUnlocalizedName("rottenFlesh").setTextureName("rotten_flesh"));
		setPotionEffect(Potion.poison.id, 30, 2, 1.0F);

	}

	@Override
	public int getFuelDuration() {
		return 1600;
	}


}
