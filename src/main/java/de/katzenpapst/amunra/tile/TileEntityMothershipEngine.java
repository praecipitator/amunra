package de.katzenpapst.amunra.tile;

import cpw.mods.fml.relauncher.Side;
import de.katzenpapst.amunra.world.CoordHelper;
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlock;
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlockWithInventory;
import micdoodle8.mods.galacticraft.core.items.GCItems;
import micdoodle8.mods.galacticraft.core.util.FluidUtil;
import micdoodle8.mods.miccore.Annotations.NetworkedField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileEntityMothershipEngine extends TileBaseElectricBlockWithInventory implements IFluidHandler, ISidedInventory, IInventory {

    private final int tankCapacity = 12000;
    @NetworkedField(targetSide = Side.CLIENT)
    public FluidTank fuelTank = new FluidTank(this.tankCapacity);
    private ItemStack[] containingItems = new ItemStack[1];

    public TileEntityMothershipEngine() {
        // TODO Auto-generated constructor stub
    }

    public int getScaledFuelLevel(int i)
    {
        final double fuelLevel = this.fuelTank.getFluid() == null ? 0 : this.fuelTank.getFluid().amount;

        return (int) (fuelLevel * i / this.tankCapacity);
    }

    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        this.containingItems = this.readStandardItemsFromNBT(par1NBTTagCompound);
        if (par1NBTTagCompound.hasKey("fuelTank"))
        {
            this.fuelTank.readFromNBT(par1NBTTagCompound.getCompoundTag("fuelTank"));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        this.writeStandardItemsToNBT(par1NBTTagCompound);
        if (this.fuelTank.getFluid() != null)
        {
            par1NBTTagCompound.setTag("fuelTank", this.fuelTank.writeToNBT(new NBTTagCompound()));
        }
    }


    @Override
    public boolean shouldUseEnergy() {
        return false;
    }

    @Override
    public ForgeDirection getElectricInputDirection() {
        return null;
    }

    @Override
    public ItemStack getBatteryInSlot() {
        return null;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        int used = 0;



        if (canFill(from, resource.getFluid()))
        {
            if (FluidUtil.testFuel(FluidRegistry.getFluidName(resource)))
            {
                used = this.fuelTank.fill(resource, doFill);
            }
        }

        return used;
    }

    @Override
    public boolean canUpdate()
    {
        return true;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        // can't drain
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        // can't drain
        return null;
    }

    public int getRotationMeta(int meta) {
        return (meta & 12) >> 2;
    }

    public int getRotationMeta() {
        return (this.getBlockMetadata() & 12) >> 2;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        // can fill from everywhere except back
        int metadata = getRotationMeta();

        if(CoordHelper.rotateForgeDirection(ForgeDirection.NORTH, metadata).equals(from)) {
            return false;
        }

        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        // can't drain
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        int metadata = getRotationMeta();
        if(CoordHelper.rotateForgeDirection(ForgeDirection.NORTH, metadata).equals(from)) {
            return null;
        }
        // TODO Auto-generated method stub
        return new FluidTankInfo[] { new FluidTankInfo(this.fuelTank) };
    }

    @Override
    public String getInventoryName() {
        return "Muhtest";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int slotID, ItemStack itemstack) {

        return (slotID == 0 && itemstack != null && itemstack.getItem() == GCItems.fuelCanister);
    }

    @Override
    protected ItemStack[] getContainingItems() {
        return this.containingItems;
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
    public boolean canExtractItem(int slotID, ItemStack itemstack, int side) {
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
    public AxisAlignedBB getRenderBoundingBox()
    {

        switch(this.getRotationMeta()) {
        case 0:
            return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord-1, xCoord + 1, yCoord + 1, zCoord + 1);
            // rotation = 0;// -> -Z
        case 1:
            return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 2);
            //rotation = 180.0F;// -> Z
        case 2:
            return AxisAlignedBB.getBoundingBox(xCoord-1, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
            //rotation = 90.0F;// -> -X
        case 3:
            return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 2, yCoord + 1, zCoord + 1);
            //rotation = 270.0F;// -> X
        }
        // I wonder if I should take rotation into account here
        return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
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

}
