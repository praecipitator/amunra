package de.katzenpapst.amunra.item;

import de.katzenpapst.amunra.AmunRa;
import net.minecraft.item.ItemStack;
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase;

public class ItemRaygun extends ItemElectricBase {

	public ItemRaygun(String assetName) {
		this.setUnlocalizedName(assetName);
        this.setTextureName(AmunRa.TEXTUREPREFIX + assetName);
	}

	@Override
	public float getMaxElectricityStored(ItemStack theItem) {
		return 15000;
	}

}
