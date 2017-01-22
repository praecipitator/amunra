package de.katzenpapst.amunra.inventory;

import codechicken.nei.SlotOP;
import micdoodle8.mods.galacticraft.api.item.IItemElectric;
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase;
import micdoodle8.mods.galacticraft.core.inventory.SlotSpecific;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

abstract public class ContainerWithPlayerInventory extends Container {

    protected IInventory tileEntity;

    public ContainerWithPlayerInventory(IInventory tile) {
        tileEntity = tile;
    }

    protected void initPlayerInventorySlots(InventoryPlayer player) {
        initPlayerInventorySlots(player, 0);
    }
    protected void initPlayerInventorySlots(InventoryPlayer player, int yOffset) {
        int y;
        int x;

        // Player inv:

        for (y = 0; y < 3; ++y)
        {
            for (x = 0; x < 9; ++x)
            {
                this.addSlotToContainer(new Slot(player, x + y * 9 + 9, 8 + x * 18, 51 + 68 + y * 18 + yOffset));
            }
        }

        for (y = 0; y < 9; ++y)
        {
            this.addSlotToContainer(new Slot(player, y, 8 + y * 18, 61 + 116 + yOffset));
        }
    }

    // let's try to fix shiftclicking
    @Override
    protected Slot addSlotToContainer(Slot slot)
    {
        return super.addSlotToContainer(slot);
    }

    protected boolean mergeSingleSlot(ItemStack mergeFrom, Slot slotToMergeTo) {

        ItemStack targetStack = slotToMergeTo.getStack();

        if(targetStack == null) {
            // I think this means I can just put it in
            slotToMergeTo.putStack(mergeFrom.copy());
            mergeFrom.stackSize = 0;
            slotToMergeTo.onSlotChanged();
            return true;
        }

        if(targetStack.getItem() == mergeFrom.getItem() && (!mergeFrom.getHasSubtypes() || mergeFrom.getItemDamage() == targetStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(mergeFrom, targetStack)) {


            int newMax = targetStack.stackSize + mergeFrom.stackSize;

            if (newMax <= mergeFrom.getMaxStackSize())
            {
                // everything fits into targetStack
                mergeFrom.stackSize = 0;
                targetStack.stackSize = newMax;
                slotToMergeTo.onSlotChanged();
                return true;
            }

            if (targetStack.stackSize < mergeFrom.getMaxStackSize())
            {
                // something should fit
                mergeFrom.stackSize -= mergeFrom.getMaxStackSize() - targetStack.stackSize;
                targetStack.stackSize = mergeFrom.getMaxStackSize();
                slotToMergeTo.onSlotChanged();
                return true;
            }
        }
        return false;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotNr)
    {
        ItemStack resultStack = null;

        final Slot slot = (Slot) this.inventorySlots.get(slotNr);
        final int containerInvSize = this.inventorySlots.size();
        final int numSlotsAdded = containerInvSize - 36;

        // seems like inventorySlots is EVERYTHING
        // and slotNr might be relate to EVERYTHING as well

        if (slot != null && slot.getHasStack())
        {
            final ItemStack stack = slot.getStack();
            resultStack = stack.copy();

            if(slotNr < numSlotsAdded) {
                // clicked one of the container's slots
                if (!this.mergeItemStack(stack, containerInvSize - 36, containerInvSize, true))
                {
                    return null;
                }
            } else {
                // clicked one of player's slots
                // check if this works for any of my slots
                boolean found = false;
                for(int i=0;i<numSlotsAdded;i++) {
                    Slot curSlot = (Slot) this.inventorySlots.get(i);
                    if(curSlot instanceof SlotSpecific) {
                        if(((SlotSpecific)curSlot).isItemValid(stack)) {
                            // attempt merge
                            if(mergeSingleSlot(stack, curSlot)) {
                                found = true;
                                break;
                            }
                        }
                    } else if(tileEntity.isItemValidForSlot(i, stack)) {
                        // attempt merge
                        if(mergeSingleSlot(stack, curSlot)) {
                            found = true;
                            break;
                        }
                    }
                }
                if(!found) {

                    // fallback, moves between main inventory and hotbar
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
    }



}
