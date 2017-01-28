package de.katzenpapst.amunra.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.entity.player.InventoryPlayer;

abstract public class ContainerElectric extends ContainerWithPlayerInventory {


    public ContainerElectric(InventoryPlayer par1InventoryPlayer, IInventory solarGen)
    {
        super(solarGen);
    }

/*
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotNr)
    {
        ItemStack resultStack = null;
        final Slot slot = (Slot) this.inventorySlots.get(slotNr);
        final int containerInvSize = this.inventorySlots.size();

        if (slot != null && slot.getHasStack())
        {
            final ItemStack stack = slot.getStack();
            resultStack = stack.copy();

            if (slotNr == 0)
            {
                if (!this.mergeItemStack(stack, containerInvSize - 36, containerInvSize, true))
                {
                    return null;
                }
            }
            else
            {
                if (stack.getItem() instanceof IItemElectric)
                {
                    if (!this.mergeItemStack(stack, 0, 1, false))
                    {
                        return null;
                    }
                }
                else
                {
                    if (slotNr < containerInvSize - 9)
                    {
                        if (!this.mergeItemStack(stack, containerInvSize - 9, containerInvSize, false))
                        {
                            return null;
                        }
                    }
                    else if (!this.mergeItemStack(stack, containerInvSize - 36, containerInvSize - 9, false))
                    {
                        return null;
                    }
                }
            }

            if (stack.stackSize == 0)
            {
                slot.putStack((ItemStack) null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (stack.stackSize == resultStack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(player, stack);
        }

        return resultStack;
    }*/
}
