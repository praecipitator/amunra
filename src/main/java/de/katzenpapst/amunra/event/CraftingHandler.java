package de.katzenpapst.amunra.event;

import micdoodle8.mods.galacticraft.core.items.GCItems;
import micdoodle8.mods.galacticraft.core.items.ItemBattery;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import de.katzenpapst.amunra.item.ARItems;
import de.katzenpapst.amunra.item.ItemRaygun;

public class CraftingHandler {

public static CraftingHandler INSTANCE = new CraftingHandler();

	public CraftingHandler(){
	}

	@SubscribeEvent
	public void onCrafting(ItemCraftedEvent event){

		if(event.crafting.getItem() instanceof ItemRaygun) {
			int indexGun = -1;
			int indexBattery = -1;
			// check if there is another raygun in the educts
			for(int i=0;i<event.craftMatrix.getSizeInventory();i++) {
				ItemStack curItem = event.craftMatrix.getStackInSlot(i);
				if(curItem == null) continue;
				if(curItem.getItem() instanceof ItemRaygun) {
					indexGun = i;
				} else if(curItem.getItem() instanceof ItemBattery) {
					indexBattery = i;
				} else {
					return; // wrong recipe
				}
			}
			if(indexGun != -1 && indexBattery != -1) {

				float energyInOldGun = ARItems.raygun.getElectricityStored(event.craftMatrix.getStackInSlot(indexGun));
				float energyInOldBattery = ((ItemBattery)GCItems.battery).getElectricityStored(event.craftMatrix.getStackInSlot(indexBattery));
				// set the energy to the new gun
				ARItems.raygun.setElectricity(event.crafting, energyInOldBattery);
				// new battery
				ItemStack newBattery = new ItemStack(GCItems.battery, 1);
				// give it the energy of the old gun
				((ItemBattery)GCItems.battery).setElectricity(newBattery, energyInOldGun);
				event.player.inventory.addItemStackToInventory(newBattery);
			}
		}
	}
}
