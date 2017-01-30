package de.katzenpapst.amunra.tile;

import java.util.EnumSet;

import cpw.mods.fml.relauncher.Side;
import de.katzenpapst.amunra.block.BlockMachineMeta;
import de.katzenpapst.amunra.block.SubBlockMachine;
import de.katzenpapst.amunra.block.machine.BlockIsotopeGenerator;
import de.katzenpapst.amunra.world.CoordHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;
import micdoodle8.mods.galacticraft.api.tile.IDisableableMachine;
import micdoodle8.mods.galacticraft.api.transmission.NetworkType;
import micdoodle8.mods.galacticraft.api.transmission.tile.IConnector;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase;
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseUniversalElectricalSource;
import micdoodle8.mods.galacticraft.core.network.IPacketReceiver;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.miccore.Annotations.NetworkedField;

public class TileEntityIsotopeGenerator extends TileBaseUniversalElectricalSource implements IPacketReceiver, IDisableableMachine, IInventory, ISidedInventory, IConnector {


    @NetworkedField(targetSide = Side.CLIENT)
    public boolean disabled = false;
    @NetworkedField(targetSide = Side.CLIENT)
    public int disableCooldown = 0;
    private ItemStack[] containingItems = new ItemStack[1];
    // energy capacity
    protected final int energyCapacity = 30000;
    // energy generated when enabled
    protected float energyGeneration = 1;
    // actual generated energy
    @NetworkedField(targetSide = Side.CLIENT)
    public float generateWatts = 0;

    public static final int MAX_GENERATE_WATTS = 200;

    private boolean initialised = false;

    private SubBlockMachine subBlock = null;

    protected float generationBoost = -1;

    public TileEntityIsotopeGenerator() {
        // init();
    }

    public int getScaledElecticalLevel(int i)
    {
        return (int) Math.floor(this.getEnergyStoredGC() * i / this.getMaxEnergyStoredGC());
    }

    protected void init()
    {
        // get generation rate
        this.energyGeneration = ((BlockIsotopeGenerator) this.getSubBlock()).energyGeneration;

        this.storage.setMaxExtract(MAX_GENERATE_WATTS);
        this.storage.setMaxReceive(MAX_GENERATE_WATTS);
        this.storage.setCapacity(energyCapacity);
        this.initialised = true;
    }

    public SubBlockMachine getSubBlock()
    {
        if(subBlock == null) {
            subBlock = (SubBlockMachine) ((BlockMachineMeta)this.getBlockType()).getSubBlock(this.getBlockMetadata());
        }
        return subBlock;
    }

    @Override
    public void updateEntity()
    {

        if (!this.initialised)
        {
            init();
        }

        // this seems to be the important line
        this.receiveEnergyGC(null, this.generateWatts, false);


        super.updateEntity();


        if (!this.worldObj.isRemote)
        {
            // recharge the item?
            this.recharge(this.containingItems[0]);
            if(this.getDisabled(0)) {
                this.generateWatts = 0;
                generationBoost = -1;
            } else {
                if(generationBoost == -1 || this.ticks % 20 == 0) {
                    generationBoost = getEnvironmentalEnergyBoost();
                }

                this.generateWatts = Math.min(energyGeneration * generationBoost, MAX_GENERATE_WATTS);


            }

            if (this.disableCooldown > 0)
            {
                this.disableCooldown--;
            }
        }

        this.produce();
    }

    public float getEnvironmentalEnergyBoost() {
        float thermalLevel = 0.0F;

        if(worldObj.provider instanceof IGalacticraftWorldProvider) {
            thermalLevel = ((IGalacticraftWorldProvider)worldObj.provider).getThermalLevelModifier();
        }
        if(thermalLevel < 0.0F) {
            return -1.0F * thermalLevel;
        }
        if(thermalLevel > 0.0F) {
            return 1.0F / thermalLevel;
        }
        return 1.0F;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.storage.setCapacity(nbt.getFloat("maxEnergy"));
        this.setDisabled(0, nbt.getBoolean("disabled"));
        this.disableCooldown = nbt.getInteger("disabledCooldown");

        final NBTTagList var2 = nbt.getTagList("Items", 10);
        this.containingItems = new ItemStack[this.getSizeInventory()];

        for (int var3 = 0; var3 < var2.tagCount(); ++var3)
        {
            final NBTTagCompound var4 = var2.getCompoundTagAt(var3);
            final int var5 = var4.getByte("Slot") & 255;

            if (var5 < this.containingItems.length)
            {
                this.containingItems[var5] = ItemStack.loadItemStackFromNBT(var4);
            }
        }

        this.initialised = false;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setFloat("maxEnergy", this.getMaxEnergyStoredGC());
        nbt.setInteger("disabledCooldown", this.disableCooldown);
        nbt.setBoolean("disabled", this.getDisabled(0));

        final NBTTagList list = new NBTTagList();

        for (int var3 = 0; var3 < this.containingItems.length; ++var3)
        {
            if (this.containingItems[var3] != null)
            {
                final NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte) var3);
                this.containingItems[var3].writeToNBT(var4);
                list.appendTag(var4);
            }
        }

        nbt.setTag("Items", list);
    }

    @Override
    public EnumSet<ForgeDirection> getElectricalInputDirections()
    {
        return EnumSet.noneOf(ForgeDirection.class);
    }

    @Override
    public EnumSet<ForgeDirection> getElectricalOutputDirections()
    {
        //int metadata = this.getBlockMetadata() & 3;
        int metadata = getRotationMeta(this.getBlockMetadata());

        return EnumSet.of(
                CoordHelper.rotateForgeDirection(ForgeDirection.EAST, metadata),
                CoordHelper.rotateForgeDirection(ForgeDirection.WEST, metadata),
                CoordHelper.rotateForgeDirection(ForgeDirection.NORTH, metadata),
                ForgeDirection.UNKNOWN);
    }

    public int getRotationMeta(int meta) {
        return (meta & 12) >> 2;
    }

    @Override
    public ForgeDirection getElectricalOutputDirectionMain()
    {
        int metadata = getRotationMeta(this.getBlockMetadata());

        return CoordHelper.rotateForgeDirection(ForgeDirection.EAST, metadata);
    }

    @Override
    public boolean canConnect(ForgeDirection direction, NetworkType type)
    {
        if (direction == null || direction.equals(ForgeDirection.UNKNOWN) || type != NetworkType.POWER)
        {
            return false;
        }

        return getElectricalOutputDirections().contains(direction);
        //return true;// just allow power cables to connect from anywhere //direction == this.getElectricalOutputDirectionMain();
    }

    @Override
    public boolean canUpdate()
    {
        return true;
    }


    @Override
    public int[] getAccessibleSlotsFromSide(int side)
    {
        return new int[] { 0 };
    }

    @Override
    public boolean canInsertItem(int slotID, ItemStack itemstack, int side)
    {
        return this.isItemValidForSlot(slotID, itemstack);
    }

    @Override
    public boolean canExtractItem(int slotID, ItemStack itemstack, int side)
    {
        return slotID == 0;
    }

    @Override
    public int getSizeInventory()
    {
        return this.containingItems.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.containingItems[slot];
    }

    @Override
    public ItemStack decrStackSize(int slotNr, int par2)
    {
        if (this.containingItems[slotNr] != null)
        {
            ItemStack var3;

            if (this.containingItems[slotNr].stackSize <= par2)
            {
                var3 = this.containingItems[slotNr];
                this.containingItems[slotNr] = null;
                return var3;
            }
            else
            {
                var3 = this.containingItems[slotNr].splitStack(par2);

                if (this.containingItems[slotNr].stackSize == 0)
                {
                    this.containingItems[slotNr] = null;
                }

                return var3;
            }
        }
        else
        {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int par1)
    {
        if (this.containingItems[par1] != null)
        {
            final ItemStack var2 = this.containingItems[par1];
            this.containingItems[par1] = null;
            return var2;
        }
        else
        {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
    {
        this.containingItems[par1] = par2ItemStack;

        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
        {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }
    }

    @Override
    public String getInventoryName()
    {
        return GCCoreUtil.translate("tile."+getSubBlock().getUnlocalizedName()+".name");
    }

    @Override
    public boolean hasCustomInventoryName() {
        return true;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer) {
        return
                this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this &&
                par1EntityPlayer.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory() {
    }

    @Override
    public void closeInventory() {
    }

    @Override
    public boolean isItemValidForSlot(int slotID, ItemStack itemstack) {
        return slotID == 0 && ItemElectricBase.isElectricItem(itemstack.getItem());
    }

    @Override
    public void setDisabled(int index, boolean disabled) {
        if (this.disableCooldown == 0)
        {
            this.disabled = disabled;
            this.disableCooldown = 20;
        }
    }

    @Override
    public boolean getDisabled(int index) {
        return this.disabled;
    }



}
