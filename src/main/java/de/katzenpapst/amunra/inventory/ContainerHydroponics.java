package de.katzenpapst.amunra.inventory;

import de.katzenpapst.amunra.tile.TileEntityHydroponics;
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase;
import micdoodle8.mods.galacticraft.core.inventory.SlotSpecific;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ContainerHydroponics extends ContainerWithPlayerInventory {

    public ContainerHydroponics(InventoryPlayer player, TileEntityHydroponics tile) {
        super(((IInventory)tile));

        this.addSlotToContainer(new SlotSpecific(tile, 0, 32, 27, ItemElectricBase.class));

        SlotSpecific secondarySlot = new SlotSpecific(tile, 1, 32, 90,
                new ItemStack(Items.wheat_seeds),
                new ItemStack(Items.dye, 1, 15)
                );
        secondarySlot.setMetadataSensitive();
        this.addSlotToContainer(secondarySlot);

        initPlayerInventorySlots(player, 5);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return ((TileEntityHydroponics)this.tileEntity).isUseableByPlayer(player);
    }

}
