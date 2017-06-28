package de.katzenpapst.amunra.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IChatComponent;

public interface IInventoryDefaultsAdvanced extends IInventory {

    @Override
    public default int getSizeInventory() {
        return getContainingItems().length;
    }

    @Override
    public default ItemStack getStackInSlot(int index) {
        return getContainingItems()[index];
    }

    @Override
    public default ItemStack decrStackSize(int index, int count)
    {
        ItemStack containingItems[] = getContainingItems();
        if (containingItems[index] != null)
        {
            ItemStack result;

            if (containingItems[index].stackSize <= count)
            {
                result = containingItems[index];
                containingItems[index] = null;
                this.markDirty();
                return result;
            }
            else
            {
                result = containingItems[index].splitStack(count);

                if (containingItems[index].stackSize == 0)
                {
                    containingItems[index] = null;
                }

                this.markDirty();
                return result;
            }
        }
        else
        {
            return null;
        }
    }

    @Override
    public default ItemStack removeStackFromSlot(int index)
    {
        ItemStack containingItems[] = this.getContainingItems();
        if (containingItems[index] != null)
        {
            final ItemStack stack = containingItems[index];
            containingItems[index] = null;
            this.markDirty();
            return stack;
        }
        else
        {
            return null;
        }
    }

    @Override
    public default void setInventorySlotContents(int index, ItemStack stack)
    {
        ItemStack containingItems[] = this.getContainingItems();
        containingItems[index] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
        {
            stack.stackSize = this.getInventoryStackLimit();
        }

        this.markDirty();
    }

    @Override
    public default int getInventoryStackLimit() {
        return 64;
    }

  //We don't use these because we use forge containers
    @Override
    public default void openInventory(EntityPlayer player)
    {
    }

    //We don't use these because we use forge containers
    @Override
    public default void closeInventory(EntityPlayer player)
    {
    }

    @Override
    public default int getField(int id)
    {
        return 0;
    }

    @Override
    public default void setField(int id, int value)
    {
    }

    @Override
    public default int getFieldCount()
    {
        return 0;
    }

    @Override
    public default void clear()
    {

    }

    /**
     * Override this and return true IF the inventory .getName() is
     * ALREADY a localized name e.g. by GCCoreUtil.translate()
     *
     **/
    @Override
    public default boolean hasCustomName()
    {
        return false;
    }

    @Override
    public default IChatComponent getDisplayName()
    {
        return null;
    }

    ItemStack[] getContainingItems();
}
