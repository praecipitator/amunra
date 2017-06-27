package de.katzenpapst.amunra.tile;

import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults;
import micdoodle8.mods.galacticraft.core.tile.TileEntityAdvanced;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

abstract public class TileEntityAdvancedInventory extends TileEntityAdvanced implements IInventory, IInventoryDefaults
{
    @Override
    public int getSizeInventory() {
        return getContainingItems().length;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return getContainingItems()[index];
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
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
    public ItemStack removeStackFromSlot(int index)
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
    public void setInventorySlotContents(int index, ItemStack stack)
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
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.worldObj.getTileEntity(this.getPos()) == this && player.getDistanceSqToCenter(this.getPos()) <= 64.0D;
    }

    abstract protected ItemStack[] getContainingItems();
}
