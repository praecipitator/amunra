package de.katzenpapst.amunra.inventory.schematic;

import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

public class InventorySchematicShuttle implements IInventoryDefaults {
    protected final ItemStack[] stackList;
    protected final int inventoryWidth;
    protected final Container eventHandler;

    public InventorySchematicShuttle(int numSlots, Container par1Container)
    {
        this.stackList = new ItemStack[numSlots];
        this.eventHandler = par1Container;
        this.inventoryWidth = 5; // what for?
    }

    @Override
    public int getSizeInventory()
    {
        return this.stackList.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return slot >= this.getSizeInventory() ? null : this.stackList[slot];
    }

    public ItemStack getStackInRowAndColumn(int x, int y)
    {
        if (x >= 0 && x < this.inventoryWidth)
        {
            final int stackNr = x + y * this.inventoryWidth;
            if (stackNr >= 22)
            {
                return null;
            }
            return this.getStackInSlot(stackNr);
        }
        else
        {
            return null;
        }
    }

    @Override
    public String getName()
    {
        return "container.crafting";
    }

    @Override
    public ItemStack removeStackFromSlot(int slot)
    {
        if (this.stackList[slot] != null)
        {
            final ItemStack curStack = this.stackList[slot];
            this.stackList[slot] = null;
            return curStack;
        }
        else
        {
            return null;
        }
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount)
    {
        if (this.stackList[slot] != null)
        {
            ItemStack var3;

            if (this.stackList[slot].stackSize <= amount)
            {
                var3 = this.stackList[slot];
                this.stackList[slot] = null;
                this.eventHandler.onCraftMatrixChanged(this);
                return var3;
            }
            else
            {
                var3 = this.stackList[slot].splitStack(amount);

                if (this.stackList[slot].stackSize == 0)
                {
                    this.stackList[slot] = null;
                }

                this.eventHandler.onCraftMatrixChanged(this);
                return var3;
            }
        }
        else
        {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack par2ItemStack)
    {
        this.stackList[slot] = par2ItemStack;
        this.eventHandler.onCraftMatrixChanged(this);
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public void markDirty()
    {
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
    {
        return true;
    }


    @Override
    public boolean hasCustomName()
    {
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack)
    {
        return false; // but why?
    }

}


