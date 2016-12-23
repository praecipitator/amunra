package de.katzenpapst.amunra.inventory.schematic;

import de.katzenpapst.amunra.RecipeHelper;
import de.katzenpapst.amunra.item.ARItems;
import de.katzenpapst.amunra.item.ItemDamagePair;
import de.katzenpapst.amunra.vec.Vector3int;
import micdoodle8.mods.galacticraft.api.recipe.INasaWorkbenchRecipe;
import micdoodle8.mods.galacticraft.core.inventory.SlotRocketBenchResult;
import micdoodle8.mods.galacticraft.planets.asteroids.inventory.ContainerSchematicTier3Rocket;
import micdoodle8.mods.galacticraft.planets.mars.inventory.ContainerSchematicTier2Rocket;
import micdoodle8.mods.galacticraft.planets.mars.inventory.InventorySchematicTier2Rocket;
import micdoodle8.mods.galacticraft.planets.mars.inventory.SlotSchematicTier2Rocket;
import micdoodle8.mods.galacticraft.planets.mars.util.RecipeUtilMars;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
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
    protected INasaWorkbenchRecipe mostCompleteRecipe;

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
        mostCompleteRecipe = RecipeHelper.getMostCompleteRecipeFor(craftingResult);

        final int change = 9; // ?
        this.worldObj = player.player.worldObj;

        this.addSlotToContainer(new SlotRocketBenchResult(player.player, this.craftMatrix, this.craftResult, 0, 142, 18 + 69 + change));
        int var6;
        int var7;

        Vector3int pos = new Vector3int(x, y, z);

        // at this point, the slots are assembled
        this.makeSlots(pos, player.player);

        // Player inv:

        for (var6 = 0; var6 < 3; ++var6)
        {
            for (var7 = 0; var7 < 9; ++var7)
            {
                this.addSlotToContainer(new Slot(player, var7 + var6 * 9 + 9, 8 + var7 * 18, 129 + var6 * 18 + change));
            }
        }

        // player equip, I think.
        for (var6 = 0; var6 < 9; ++var6)
        {
            this.addSlotToContainer(new Slot(player, var6, 8 + var6 * 18, 18 + 169 + change));
        }

        this.onCraftMatrixChanged(this.craftMatrix);
    }

    protected void makeSlots(Vector3int sparkPos, EntityPlayer player) {

        for(int slotI = 0;slotI < slotCoordinateMapping.length; slotI++) {
            int[] coords = slotCoordinateMapping[slotI];
            int x = coords[0];
            int y = coords[1];

            ItemStack curIngredient = mostCompleteRecipe.getRecipeInput().get(slotI+1);
            this.addSlotToContainer(new SlotSchematicShuttle(this.craftMatrix, slotI+1, x, y, sparkPos, player, new ItemDamagePair(curIngredient)));
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

            boolean done = false;
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
                        done = true;
                        break;
                    }
                }
                /* TODO check what actually happens here* /
                if (!done)
                {
                    // 19? I almost think 19, 20 and 21 are the chest slots
                    if (stack.getItem() == Item.getItemFromBlock(Blocks.chest) && !((Slot) this.inventorySlots.get(19)).getHasStack())
                    {
                        if (!this.mergeOneItem(oldStack, 19, 20, false))
                        {
                            return null;
                        }
                    }
                    else if (stack.getItem() == Item.getItemFromBlock(Blocks.chest) && !((Slot) this.inventorySlots.get(20)).getHasStack())
                    {
                        if (!this.mergeOneItem(oldStack, 20, 21, false))
                        {
                            return null;
                        }
                    }
                    else if (stack.getItem() == Item.getItemFromBlock(Blocks.chest) && !((Slot) this.inventorySlots.get(21)).getHasStack())
                    {
                        if (!this.mergeOneItem(oldStack, 21, 22, false))
                        {
                            return null;
                        }
                    }
                    else if (slotNr >= 22 && slotNr < 49)
                    {
                        // why? WTF?
                        if (!this.mergeItemStack(oldStack, 49, 58, false))
                        {
                            return null;
                        }
                    }
                    else if (slotNr >= 49 && slotNr < 58)
                    {
                        if (!this.mergeItemStack(oldStack, 22, 49, false))
                        {
                            return null;
                        }
                    }
                    else if (!this.mergeItemStack(oldStack, 22, 58, false))
                    {
                        return null;
                    }
                }/* */
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
    }

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
