package de.katzenpapst.amunra.tile;

import net.minecraft.item.ItemStack;

public class TileEntityARChestLarge extends TileEntityARChest {

    public TileEntityARChestLarge() {
        chestContents = new ItemStack[this.getSizeInventory()];
    }

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getSizeInventory()
    {
        return 54;
    }

    @Override
    protected boolean canDoublechest() {
        return false;
    }
}
