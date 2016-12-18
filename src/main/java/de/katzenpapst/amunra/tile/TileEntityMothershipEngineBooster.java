package de.katzenpapst.amunra.tile;

import de.katzenpapst.amunra.vec.Vector3int;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

/**
 * This is supposed to be an universal booster TileEntity, used by all booster blocks
 * @author katzenpapst
 *
 */
public class TileEntityMothershipEngineBooster extends TileEntity implements IFluidHandler, ISidedInventory, IInventory {

    protected boolean masterPresent = false;
    protected int masterX;
    protected int masterY;
    protected int masterZ;

    protected Class masterType;

    public TileEntityMothershipEngineBooster() {
        this.masterType = TileEntityMothershipEngineJet.class;
    }


    public boolean isValidMaster(TileEntity tile) {
        if(!(tile instanceof TileEntityMothershipEngineJet)) {
            return false;
        }
        return tile.getClass() == this.masterType;
    }

    public void reset() {
        masterPresent = false;
    }

    public void setMaster(int x, int y, int z) {
        masterX = x;
        masterY = y;
        masterZ = z;
        masterPresent = true;
    }

    public void clearMaster() {
        masterPresent = false;
    }

    public boolean isMaster(int x, int y, int z) {
        return masterPresent && x == masterX && y == masterY && z == masterZ;
    }

    public Vector3int getMasterPosition() {
        return new Vector3int(masterX, masterY, masterZ);
    }

    public boolean hasMaster() {
        return masterPresent;
    }

    /**
     * Reset and update the master, if I have any
     */
    public void updateMaster(boolean rightNow) {
        if(!masterPresent) return;

        TileEntity masterTile = worldObj.getTileEntity(masterX, masterY, masterZ);
        if(masterTile == null || !(masterTile instanceof TileEntityMothershipEngineJet)) {
            // apparently we just lost our master?
            this.reset();
            return;
        }
        TileEntityMothershipEngineJet jetTile = (TileEntityMothershipEngineJet)masterTile;
        if(!jetTile.isPartOfMultiBlock(xCoord, yCoord, zCoord)) {
            this.reset();
            return;
        }

        if(rightNow) {
            jetTile.updateMultiblock();
        } else {
            jetTile.scheduleUpdate();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        masterPresent = nbt.getBoolean("hasMaster");
        masterX = nbt.getInteger("masterX");
        masterY = nbt.getInteger("masterY");
        masterZ = nbt.getInteger("masterZ");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setBoolean("hasMaster", masterPresent);
        nbt.setInteger("masterX", masterX);
        nbt.setInteger("masterY", masterY);
        nbt.setInteger("masterZ", masterZ);
    }

    protected TileEntityMothershipEngineJet getMasterTile() {
        if(!this.hasMaster()) {
            return null;
        }
        TileEntity tile = this.worldObj.getTileEntity(masterX, masterY, masterZ);
        if(tile == null || !(tile instanceof TileEntityMothershipEngineJet)) {
            // oops
            this.masterPresent = false;
            return null;
        }
        return (TileEntityMothershipEngineJet)tile;
    }


    @Override
    public int getSizeInventory() {
        TileEntityMothershipEngineJet tile = this.getMasterTile();
        if(tile == null) {
            return 0;
        }
        return tile.getSizeInventory();
    }


    @Override
    public ItemStack getStackInSlot(int slot) {
        TileEntityMothershipEngineJet tile = this.getMasterTile();
        if(tile == null) {
            return null;
        }
        return tile.getStackInSlot(slot);
    }


    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        TileEntityMothershipEngineJet tile = this.getMasterTile();
        if(tile == null) {
            return null;
        }
        return tile.decrStackSize(slot, amount);
    }


    @Override
    public ItemStack getStackInSlotOnClosing(int wat) {
        TileEntityMothershipEngineJet tile = this.getMasterTile();
        if(tile == null) {
            return null;
        }
        return tile.getStackInSlotOnClosing(wat);
    }


    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        TileEntityMothershipEngineJet tile = this.getMasterTile();
        if(tile == null) {
            return;
        }
        tile.setInventorySlotContents(slot, stack);
    }


    @Override
    public String getInventoryName() {
        // I'm not sure if it's even needed to do this, but...
        return GCCoreUtil.translate("tile.mothership.rocketJetEngine.name");
    }


    @Override
    public boolean hasCustomInventoryName() {
        return true;
    }


    @Override
    public int getInventoryStackLimit() {
        TileEntityMothershipEngineJet tile = this.getMasterTile();
        if(tile == null) {
            return 0;
        }
        return tile.getInventoryStackLimit();
    }


    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        TileEntityMothershipEngineJet tile = this.getMasterTile();
        if(tile == null) {
            return false;
        }
        // I think it's better to calculate this here
        return
                this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this &&
                player.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
    }


    @Override
    public void openInventory() {
    }


    @Override
    public void closeInventory() {
    }


    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        TileEntityMothershipEngineJet tile = this.getMasterTile();
        if(tile == null) {
            return false;
        }
        return tile.isItemValidForSlot(slot, stack);
    }


    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        TileEntityMothershipEngineJet tile = this.getMasterTile();
        if(tile == null) {
            return new int[] {};
        }
        return tile.getAccessibleSlotsFromSide(side);
    }


    @Override
    public boolean canInsertItem(int slotID, ItemStack itemstack, int side) {
        TileEntityMothershipEngineJet tile = this.getMasterTile();
        if(tile == null) {
            return false;
        }
        return tile.canInsertItem(slotID, itemstack, side);
    }


    @Override
    public boolean canExtractItem(int slotID, ItemStack itemstack, int side) {
        TileEntityMothershipEngineJet tile = this.getMasterTile();
        if(tile == null) {
            return false;
        }
        return tile.canExtractItem(slotID, itemstack, side);
    }


    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        TileEntityMothershipEngineJet tile = this.getMasterTile();
        if(tile == null) {
            return 0;
        }
        return tile.fill(from, resource, doFill);
    }


    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        TileEntityMothershipEngineJet tile = this.getMasterTile();
        if(tile == null) {
            return null;
        }
        return tile.drain(from, resource, doDrain);
    }


    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        TileEntityMothershipEngineJet tile = this.getMasterTile();
        if(tile == null) {
            return null;
        }
        return tile.drain(from, maxDrain, doDrain);
    }


    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        TileEntityMothershipEngineJet tile = this.getMasterTile();
        if(tile == null) {
            return false;
        }
        return tile.canFill(from, fluid);
    }


    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        TileEntityMothershipEngineJet tile = this.getMasterTile();
        if(tile == null) {
            return false;
        }
        return tile.canDrain(from, fluid);
    }


    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        TileEntityMothershipEngineJet tile = this.getMasterTile();
        if(tile == null) {
            return null;
        }
        return tile.getTankInfo(from);
    }
}
