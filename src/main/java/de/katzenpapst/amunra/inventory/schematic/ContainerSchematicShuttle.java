package de.katzenpapst.amunra.inventory.schematic;

import java.util.HashMap;
import java.util.HashSet;

import de.katzenpapst.amunra.crafting.RecipeHelper;
import de.katzenpapst.amunra.item.ARItems;
import de.katzenpapst.amunra.item.ItemDamagePair;
import de.katzenpapst.amunra.vec.Vector3int;
import micdoodle8.mods.galacticraft.core.inventory.SlotRocketBenchResult;
import micdoodle8.mods.galacticraft.core.inventory.SlotSpecific;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ContainerSchematicShuttle extends Container {
    // for now, just do everythig like the t2 rocket
    public InventorySchematicShuttle craftMatrix = new InventorySchematicShuttle(22, this);
    public IInventory craftResult = new InventoryCraftResult();
    protected final World worldObj;
    // protected INasaWorkbenchRecipe mostCompleteRecipe;
    protected HashMap<Integer, HashSet<ItemDamagePair>> slotTypes;

    protected final Item craftingResult = ARItems.shuttleItem;

    // where the texture slots are located
    public static final int[][] slotCoordinateMapping = {
                              {48, 18},
                     {30, 36},{48, 36},{66, 36},
                     {30, 54},{48, 54},{66, 54},
                     {30, 72},{48, 72},{66, 72},
            {12, 90},{30, 90},{48, 90},{66, 90},{84, 90},
            {12,108},         {48,108},         {84,108},

            // chests
            {93,12},{119,12},{145,12}
    };

    public ContainerSchematicShuttle(InventoryPlayer player, int x, int y, int z)
    {
        //mostCompleteRecipe = RecipeHelper.getMostCompleteRecipeFor(craftingResult);

        slotTypes = RecipeHelper.getNasaWorkbenchRecipeForContainer(craftingResult);
        final int change = 9; // ?
        this.worldObj = player.player.worldObj;

        this.addSlotToContainer(new SlotRocketBenchResult(player.player, this.craftMatrix, this.craftResult, 0, 142, 18 + 69 + change));
        int slotX;
        int slotY;

        Vector3int pos = new Vector3int(x, y, z);

        // at this point, the slots are assembled
        this.makeSlots(pos, player.player);

        // Player inv:

        for (slotX = 0; slotX < 3; ++slotX)
        {
            for (slotY = 0; slotY < 9; ++slotY)
            {
                this.addSlotToContainer(new Slot(player, slotY + slotX * 9 + 9, 8 + slotY * 18, 129 + slotX * 18 + change));
            }
        }

        // player equip, I think.
        for (slotX = 0; slotX < 9; ++slotX)
        {
            this.addSlotToContainer(new Slot(player, slotX, 8 + slotX * 18, 18 + 169 + change));
        }

        this.onCraftMatrixChanged(this.craftMatrix);
    }

    protected void makeSlots(Vector3int sparkPos, EntityPlayer player) {

        for(int slotNr = 0; slotNr < slotCoordinateMapping.length; slotNr++) {
            int[] coords = slotCoordinateMapping[slotNr];
            int x = coords[0];
            int y = coords[1];

            HashSet<ItemDamagePair> possibleItems = slotTypes.get(slotNr+1);

            if(possibleItems != null) {
                ItemDamagePair[] asArray = new ItemDamagePair[possibleItems.size()];
                possibleItems.toArray(asArray);
                this.addSlotToContainer(new SlotSchematicShuttle(this.craftMatrix, slotNr+1, x, y, sparkPos, player, asArray));
            } else {
                this.addSlotToContainer(new SlotSchematicShuttle(this.craftMatrix, slotNr+1, x, y, sparkPos, player));
            }

        }
    }

    @Override
    public void onContainerClosed(EntityPlayer par1EntityPlayer)
    {
        super.onContainerClosed(par1EntityPlayer);

        if (!this.worldObj.isRemote)
        {
            for (int i = 1; i < this.craftMatrix.getSizeInventory(); ++i)
            {
                final ItemStack curStack = this.craftMatrix.getStackInSlotOnClosing(i);

                if (curStack != null)
                {
                    par1EntityPlayer.entityDropItem(curStack, 0.0F);
                }
            }
        }
    }

    @Override
    public void onCraftMatrixChanged(IInventory par1IInventory)
    {
        // now this will require my custom recipe
        // is this the only place where I need the recipe?
        // seems like that
        this.craftResult.setInventorySlotContents(0, RecipeHelper.findMatchingRecipeFor(craftingResult, this.craftMatrix));
    }

    @Override
    public boolean canInteractWith(EntityPlayer par1EntityPlayer)
    {
        return true;
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
/*
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotNr)
    {
        // seems like slot validation happens here
        // I think this is what happens on shift-click
        ItemStack stack = null;
        final Slot slot = (Slot) this.inventorySlots.get(slotNr);

        if (slot != null && slot.getHasStack())
        {
            final ItemStack oldStack = slot.getStack();
            stack = oldStack.copy();

            if (slotNr <= 21) // 0 <= x <= 21 are "our" slots
            {
                // I think mergeItemStack attemps to merge oldStack into slots in [ 22 ; 58 [
                // the bool seems to mean "reverse", in that case, it will do 58-1 to 22
                // the la
                if (!this.mergeItemStack(oldStack, 22, 58, false))
                {
                    return null;
                }

                if (slotNr == 0)
                {
                    slot.onSlotChange(oldStack, stack);
                }
            }
            else
            {
                // seems to be the player inventory
                for (int i = 1; i < 19; i++)
                {
                    Slot testSlot = (Slot) this.inventorySlots.get(i);
                    if (!testSlot.getHasStack() && testSlot.isItemValid(stack))
                    {
                        if (!this.mergeOneItem(oldStack, i, i + 1, false))
                        {
                            return null;
                        }
                        break;
                    }
                }
            }

            if (oldStack.stackSize == 0)
            {
                slot.putStack((ItemStack) null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (oldStack.stackSize == stack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(player, oldStack);
        }

        return stack;
    }*/

    protected boolean mergeOneItem(ItemStack stack, int startSlot, int endSlotMaybe, boolean wtf)
    {
        boolean flag1 = false;
        if (stack.stackSize > 0)
        {
            Slot slot;
            ItemStack slotStack;

            for (int k = startSlot; k < endSlotMaybe; k++)
            {
                slot = (Slot) this.inventorySlots.get(k);
                slotStack = slot.getStack();

                if (slotStack == null)
                {
                    ItemStack stackOneItem = stack.copy();
                    stackOneItem.stackSize = 1;
                    stack.stackSize--;
                    slot.putStack(stackOneItem);
                    slot.onSlotChanged();
                    flag1 = true;
                    break;
                }
            }
        }

        return flag1;
    }

}
