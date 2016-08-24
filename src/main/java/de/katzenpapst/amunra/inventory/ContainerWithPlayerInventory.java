package de.katzenpapst.amunra.inventory;

import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase;
import micdoodle8.mods.galacticraft.core.inventory.SlotSpecific;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

abstract public class ContainerWithPlayerInventory extends Container {

    protected IInventory tileEntity;

    public ContainerWithPlayerInventory(IInventory tile) {
        tileEntity = tile;
    }

    protected void initPlayerInventorySlots(InventoryPlayer player) {
        int y;
        int x;

        // Player inv:

        for (y = 0; y < 3; ++y)
        {
            for (x = 0; x < 9; ++x)
            {
                this.addSlotToContainer(new Slot(player, x + y * 9 + 9, 8 + x * 18, 51 + 68 + y * 18));
            }
        }

        for (y = 0; y < 9; ++y)
        {
            this.addSlotToContainer(new Slot(player, y, 8 + y * 18, 61 + 116));
        }
    }


}
