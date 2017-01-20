package de.katzenpapst.amunra.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemNuclearBattery extends ItemBaseBattery {

    protected float rechargeRate;

    public ItemNuclearBattery(String assetName, float capacity, float rechargeRate) {
        super(assetName, capacity);
        this.rechargeRate = rechargeRate;
    }

    public ItemNuclearBattery(String assetName, float capacity, float maxTransfer, float rechargeRate) {
        super(assetName, capacity, maxTransfer);
        this.rechargeRate = rechargeRate;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int stackNumber, boolean isBeingHeld) {
        if(this.getElectricityStored(stack) < this.getMaxElectricityStored(stack)) {
            // recharge
            this.setElectricity(stack, this.getElectricityStored(stack) + rechargeRate);
        }
    }

}
