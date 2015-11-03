package de.katzenpapst.amunra.event;

import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.IFuelHandler;
import de.katzenpapst.amunra.item.ItemBasicMulti;

public class FurnaceHandler implements IFuelHandler {



	@Override
	public int getBurnTime(ItemStack fuel) {

		if(fuel.getItem() instanceof ItemBasicMulti) {
			return ((ItemBasicMulti)fuel.getItem()).getFuelDuration(fuel.getItemDamage());
		}
		return 0;
	}

}
