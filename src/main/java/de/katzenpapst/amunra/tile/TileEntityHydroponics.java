package de.katzenpapst.amunra.tile;

import java.util.EnumSet;

import cpw.mods.fml.relauncher.Side;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.item.ItemDamagePair;
import de.katzenpapst.amunra.world.WorldHelper;
import micdoodle8.mods.galacticraft.api.tile.IDisableableMachine;
import micdoodle8.mods.galacticraft.api.transmission.tile.IConnector;
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase;
import micdoodle8.mods.galacticraft.core.network.IPacketReceiver;
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygen;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.miccore.Annotations.NetworkedField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityHydroponics extends TileEntityOxygen implements IPacketReceiver, IDisableableMachine, IInventory, ISidedInventory, IConnector {

    public enum OperationType {
        PLANT_SEED,
        FERTILIZE,
        HARVEST
    }

    public boolean active;
    public static final int OUTPUT_PER_TICK = 100;
    @NetworkedField(targetSide = Side.CLIENT)
    public float lastOxygenCollected;
    private ItemStack[] containingItems = new ItemStack[2];

    private boolean isInitialised = false;
    private boolean producedLastTick = false;

    // protected ItemStack plantedSeed = null;

    @NetworkedField(targetSide = Side.CLIENT)
    public float plantGrowthStatus = -1.0F;

    public static ItemDamagePair seeds    = null;
    public static ItemDamagePair bonemeal = null;

    public TileEntityHydroponics() {
        super(6000, 0);
        if(seeds == null) {
            seeds = new ItemDamagePair(Items.wheat_seeds, 0);
        }
        if(bonemeal == null) {
            bonemeal= new ItemDamagePair(Items.dye, 15);
        }
        storage.setMaxExtract(5);
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        if (!this.worldObj.isRemote)
        {
            producedLastTick = this.storedOxygen < this.maxOxygen;

            // this makes the thing output oxygen
            this.produceOxygen();

            // this actually genereates it
            this.generateOxygen();

            // grow the plant inside
            this.growPlant();
        }
    }

    /**
     * Make sure this does not exceed the oxygen stored.
     * This should return 0 if no oxygen is stored.
     * Implementing tiles must respect this or you will generate infinite oxygen.
     */
    @Override
    public float getOxygenProvide(ForgeDirection direction)
    {
        return this.getOxygenOutputDirections().contains(direction) ? Math.min(OUTPUT_PER_TICK, this.getOxygenStored()) : 0.0F;
    }

    @Override
    public boolean shouldPullOxygen()
    {
        return false;
    }

    @Override
    public String getInventoryName() {
        return GCCoreUtil.translate("tile.hydroponics.name");
    }

    @Override
    public boolean hasCustomInventoryName() {
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int slotNr, ItemStack stack) {

        switch(slotNr) {
        case 0: // battery
            return ItemElectricBase.isElectricItem(stack.getItem());
        case 1: // seeds or bonemeal
            return seeds.isSameItem(stack) || bonemeal.isSameItem(stack); //stack.getItem() == Items.wheat_seeds;
            //return stack.getItem() instanceof IPlantable;
        default:
            return false;
        }

    }

    /*@Override
    protected ItemStack[] getContainingItems() {
        // TO DO Auto-generated method stub
        return null;
    }*/

    @Override
    public boolean shouldUseEnergy() {

        return !this.getDisabled(0);
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return new int[]{ 0, 1 };
    }

    @Override
    public boolean canInsertItem(int slotID, ItemStack itemstack, int side) {
        return this.isItemValidForSlot(slotID, itemstack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return slot == 0 || slot == 1;
    }

    @Override
    public int getSizeInventory() {
        return containingItems.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return containingItems[slot];
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.containingItems = this.readStandardItemsFromNBT(nbt);
        this.plantGrowthStatus = nbt.getFloat("growthStatus");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        this.writeStandardItemsToNBT(nbt);
        nbt.setFloat("growthStatus", plantGrowthStatus);
        // plantedSeed
    }

    public ItemStack[] readStandardItemsFromNBT(NBTTagCompound nbt)
    {
        final NBTTagList itemTag = nbt.getTagList("Items", 10);
        int length = containingItems.length;
        ItemStack[] result = new ItemStack[length];

        for (int i = 0; i < itemTag.tagCount(); ++i)
        {
            final NBTTagCompound stackNbt = itemTag.getCompoundTagAt(i);
            final int slotNr = stackNbt.getByte("Slot") & 255;

            if (slotNr < length)
            {
                result[slotNr] = ItemStack.loadItemStackFromNBT(stackNbt);
            }
        }
        return result;
    }

    public void writeStandardItemsToNBT(NBTTagCompound nbt)
    {
        final NBTTagList list = new NBTTagList();
        int length = containingItems.length;


        for (int i = 0; i < length; ++i)
        {
            if (containingItems[i] != null)
            {
                final NBTTagCompound stackNbt = new NBTTagCompound();
                stackNbt.setByte("Slot", (byte) i);
                containingItems[i].writeToNBT(stackNbt);
                list.appendTag(stackNbt);
            }
        }

        nbt.setTag("Items", list);
    }

    @Override
    public ItemStack decrStackSize(int slotNr, int amount)
    {
        if (this.containingItems[slotNr] != null)
        {
            ItemStack newStack;

            if (this.containingItems[slotNr].stackSize <= amount)
            {
                newStack = this.containingItems[slotNr];
                this.containingItems[slotNr] = null;
                return newStack;
            }
            else
            {
                newStack = this.containingItems[slotNr].splitStack(amount);

                if (this.containingItems[slotNr].stackSize == 0)
                {
                    this.containingItems[slotNr] = null;
                }

                return newStack;
            }
        }
        else
        {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slotNr)
    {
        if (this.containingItems[slotNr] != null)
        {
            final ItemStack var2 = this.containingItems[slotNr];
            this.containingItems[slotNr] = null;
            return var2;
        }
        else
        {
            return null;
        }
    }

    protected void generateOxygen() {
        if (this.worldObj.rand.nextInt(10) == 0)
        {
            if (this.hasEnoughEnergyToRun && this.plantGrowthStatus > 0)
            {
                // 20 air points for 14 blocks per second!
                // 0.075F is the value from oxygen collector, or 0.075F*10.0F
                // divide by 10 for display
                // use it right away for actual generation?

                float generationRate = 0.3F * 10.0F * this.plantGrowthStatus * AmunRa.config.hydroponicsFactor; // should be 4x the regular amount
                this.lastOxygenCollected = generationRate / 10F;

                this.storedOxygen = Math.max(Math.min(this.storedOxygen + generationRate, this.maxOxygen), 0);
            }
            else
            {
                this.lastOxygenCollected = 0;
            }
        }
    }

    protected void growPlant() {
        if(this.plantGrowthStatus == -1.0F || this.plantGrowthStatus == 1.0F) {
            return; // no seed or grown
        }
        if (worldObj.getBlockLightValue(xCoord, yCoord + 1, zCoord) < 9) {
            return;
        }
        // wiki says: 5 - 35 minecraft minutes for one crop stage
        // 1 tick = 3,6 mineSeconds
        // 1 minute = 16,66.. ticks
        // 5 minutes =  ca. 83,3333333333 ticks
        // 35 minutes = 583,3333333331

        // assume: p=100% at 400 (faster)
        // check stuff every 40 ticks
        // p=100% at 10 ticks
        // check every 20 -> p=100% at 20 ticks
        // p=0.1
        if(this.ticks % 20 == 0) {
            if(this.worldObj.rand.nextFloat() < 0.1F) {
                plantGrowthStatus += 0.01F;
                if(plantGrowthStatus > 1.0F) {
                    plantGrowthStatus = 1.0F;
                }
                this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            }
        }
    }

    /*@Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound data = new NBTTagCompound();
        this.writeToNBT(data);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 2, data);
    }

    @Override
    public void onDataPacket(NetworkManager netManager, S35PacketUpdateTileEntity packet)
    {
        readFromNBT(packet.func_148857_g());
    }*/

    @Override
    public void setInventorySlotContents(int slotNr, ItemStack stack)
    {
        this.containingItems[slotNr] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
        {
            stack.stackSize = this.getInventoryStackLimit();
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void openInventory() { }

    @Override
    public void closeInventory() { }

    @Override
    public boolean shouldUseOxygen() {
        return false;
    }

    @Override
    public ForgeDirection getElectricInputDirection() {
        return ForgeDirection.DOWN;
    }

    @Override
    public EnumSet<ForgeDirection> getOxygenOutputDirections()
    {
        return EnumSet.of(ForgeDirection.EAST, ForgeDirection.WEST, ForgeDirection.NORTH, ForgeDirection.SOUTH);
    }

    @Override
    public EnumSet<ForgeDirection> getOxygenInputDirections()
    {
        return EnumSet.noneOf(ForgeDirection.class);
    }

    @Override
    public ItemStack getBatteryInSlot() {
        return this.getStackInSlot(0);
    }

    public float getPlantGrowthStatus() {
        return plantGrowthStatus;
    }

    public void plantSeed() {
        plantGrowthStatus = 0;
    }

    public void fertilize() {
        plantGrowthStatus += 0.125F;
    }

    public ItemStack[] getHarvest() {
        if(plantGrowthStatus < 0) {
            return new ItemStack[]{};
        }
        if(plantGrowthStatus < 1) {
            return new ItemStack[]{seeds.getItemStack(1)};
        }
        int numSeeds = worldObj.rand.nextInt(4);
        return new ItemStack[]{
                new ItemStack(Items.wheat, 1, 0),
                seeds.getItemStack(numSeeds)
        };
    }

    public void harvest(EntityPlayer player) {

        ItemStack[] harvest = getHarvest();
        for(ItemStack stack: harvest) {
            if(!player.inventory.addItemStackToInventory(stack)) {
                WorldHelper.dropItemInWorld(worldObj, stack, player);
            }
        }
        this.plantGrowthStatus = 0.0F;
    }

    public void performOperation(int op, EntityPlayerMP playerBase) {
        OperationType realOp = OperationType.values()[op];
        ItemStack stack ;
        switch(realOp) {
        case PLANT_SEED:
            // I hope this works..
            stack = this.containingItems[1];
            if(plantGrowthStatus == -1.0F && stack != null && stack.stackSize > 0 && seeds.isSameItem(stack)) {
                stack.stackSize--;
                if(stack.stackSize <= 0) {
                    stack = null;
                }
                this.containingItems[1] = stack;
                plantSeed();
            }
            break;
        case FERTILIZE:
            stack = this.containingItems[1];
            if(plantGrowthStatus >= 0.0F && plantGrowthStatus < 1.0F && stack != null && stack.stackSize > 0 && bonemeal.isSameItem(stack)) {
                stack.stackSize--;
                if(stack.stackSize <= 0) {
                    stack = null;
                }
                this.containingItems[1] = stack;
                fertilize();
            }
            break;
        case HARVEST:
            if(plantGrowthStatus == 1.0F) {
                harvest(playerBase);
            }
            break;
        }
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer) {
        return
                this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this &&
                par1EntityPlayer.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
    }
}
