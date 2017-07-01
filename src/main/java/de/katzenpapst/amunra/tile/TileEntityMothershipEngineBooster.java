package de.katzenpapst.amunra.tile;

import java.util.EnumSet;

import de.katzenpapst.amunra.AmunRa;
import micdoodle8.mods.galacticraft.api.transmission.NetworkType;
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseUniversalElectrical;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.miccore.Annotations.RuntimeInterface;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

/**
 * This is supposed to be an universal booster TileEntity, used by all booster blocks
 * @author katzenpapst
 *
 */
public class TileEntityMothershipEngineBooster extends TileBaseUniversalElectrical implements IFluidHandler, ISidedInventory, IInventoryDefaultsAdvanced {

    public static ResourceLocation topFallback = new ResourceLocation(micdoodle8.mods.galacticraft.core.Constants.ASSET_PREFIX, "textures/blocks/machine.png");
    public static ResourceLocation sideFallback = new ResourceLocation(micdoodle8.mods.galacticraft.core.Constants.ASSET_PREFIX, "textures/blocks/machine_side.png");



    protected final String assetPrefix = AmunRa.ASSETPREFIX;
    protected final String assetPath = "textures/blocks/";

    protected boolean masterPresent = false;
    protected BlockPos masterPos;

    protected Class masterType;

    public TileEntityMothershipEngineBooster() {
        this.masterType = TileEntityMothershipEngineJet.class;
    }



    public boolean isValidMaster(TileEntity tile) {
        if(!(tile instanceof TileEntityMothershipEngineAbstract)) {
            return false;
        }
        return tile.getClass() == this.masterType;
    }

    public void reset() {
        masterPresent = false;
        this.markDirty();
        this.worldObj.markBlockForUpdate(getPos());
    }

    public void setMaster(BlockPos pos) {
        masterPos = pos;
        masterPresent = true;
    }

    public BlockPos getMasterPosition() {
        return masterPos;
    }

    public void clearMaster() {
        masterPresent = false;
    }

    public boolean isMaster(BlockPos pos) {

        return masterPresent && pos.equals(masterPos);
    }

    public boolean hasMaster() {
        // meh
        TileEntityMothershipEngineAbstract tile = this.getMasterTile();
        return tile != null;
    }

    /**
     * Reset and update the master, if I have any
     */
    public void updateMaster(boolean rightNow) {
        if(!masterPresent) return;

        TileEntity masterTile = worldObj.getTileEntity(masterPos);
        if(masterTile == null || !(masterTile instanceof TileEntityMothershipEngineAbstract)) {
            // apparently we just lost our master?
            this.reset();
            return;
        }
        TileEntityMothershipEngineAbstract jetTile = (TileEntityMothershipEngineAbstract)masterTile;
        if(!jetTile.isPartOfMultiBlock(getPos())) {
            this.reset();
            return;
        }

        if(rightNow) {
            jetTile.updateMultiblock();
        } else {
            jetTile.scheduleUpdate();
        }
    }

    /**
     * Using the master coordinates, get a position where the next booster could be
     * @return
     */
    public BlockPos getPossibleNextBooster() {
        if(!hasMaster()) {
            return null;
        }
        if(this.getPos().getX() == this.masterPos.getX()) {
            if(this.getPos().getZ() < this.masterPos.getZ()) {
                return getPos().add(0, 0, -1);//new Vector3int(xCoord, yCoord, zCoord-1);
            } else if(this.getPos().getZ() > this.masterPos.getZ()) {
                return getPos().add(0, 0, 1);
            } else {
                return null;
            }
        } else if(this.getPos().getZ() == this.masterPos.getZ()) {
            if(this.getPos().getX() < this.masterPos.getX()) {
                return getPos().add(-1, 0, 0);
            } else if(this.getPos().getX() > this.masterPos.getX()) {
                return getPos().add(1, 0, 0);
            } else {
                return null;
            }
        }
        return null;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        masterPresent = nbt.getBoolean("hasMaster");
        masterPos = new BlockPos(
                nbt.getInteger("masterX"),
                nbt.getInteger("masterY"),
                nbt.getInteger("masterZ")
        );
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setBoolean("hasMaster", masterPresent);
        nbt.setInteger("masterX", masterPos.getX());
        nbt.setInteger("masterY", masterPos.getY());
        nbt.setInteger("masterZ", masterPos.getZ());
    }

    public TileEntityMothershipEngineAbstract getMasterTile() {
        if(!this.masterPresent) {
            return null;
        }
        TileEntity tile = this.worldObj.getTileEntity(masterPos);
        if(tile == null || !(tile instanceof TileEntityMothershipEngineAbstract)) {
            // oops
            this.masterPresent = false;
            return null;
        }
        return (TileEntityMothershipEngineAbstract)tile;
    }


    @Override
    public int getSizeInventory() {
        TileEntityMothershipEngineAbstract tile = this.getMasterTile();
        if(tile == null) {
            return 0;
        }
        return tile.getSizeInventory();
    }


    @Override
    public ItemStack getStackInSlot(int slot) {
        TileEntityMothershipEngineAbstract tile = this.getMasterTile();
        if(tile == null) {
            return null;
        }
        return tile.getStackInSlot(slot);
    }


    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        TileEntityMothershipEngineAbstract tile = this.getMasterTile();
        if(tile == null) {
            return null;
        }
        return tile.decrStackSize(slot, amount);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        TileEntityMothershipEngineAbstract tile = this.getMasterTile();
        if(tile == null) {
            return;
        }
        tile.setInventorySlotContents(slot, stack);
    }


    @Override
    public String getName() {
        // I'm not sure if it's even needed to do this, but...
        return GCCoreUtil.translate("tile.mothership.rocketJetEngine.name");
    }


    @Override
    public boolean hasCustomName() {
        return true;
    }


    @Override
    public int getInventoryStackLimit() {
        TileEntityMothershipEngineAbstract tile = this.getMasterTile();
        if(tile == null) {
            return 0;
        }
        return tile.getInventoryStackLimit();
    }


    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        TileEntityMothershipEngineAbstract tile = this.getMasterTile();
        if(tile == null) {
            return false;
        }
        // I think it's better to calculate this here
        return
                this.worldObj.getTileEntity(this.getPos()) == this &&
                player.getDistanceSqToCenter(getPos()) <= 64.0D;
    }


    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        TileEntityMothershipEngineAbstract tile = this.getMasterTile();
        if(tile == null) {
            return false;
        }
        return tile.isItemValidForSlot(slot, stack);
    }




    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        TileEntityMothershipEngineAbstract tile = this.getMasterTile();
        if(tile == null) {
            return false;
        }
        return tile.canInsertItem(index, itemStackIn, direction);
    }


    @Override
    public boolean canExtractItem(int slotID, ItemStack itemstack, EnumFacing side) {
        TileEntityMothershipEngineAbstract tile = this.getMasterTile();
        if(tile == null) {
            return false;
        }
        return tile.canExtractItem(slotID, itemstack, side);
    }


    @Override
    public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
        TileEntityMothershipEngineAbstract tile = this.getMasterTile();
        if(tile == null) {
            return 0;
        }
        return tile.fill(from, resource, doFill);
    }


    @Override
    public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
        TileEntityMothershipEngineAbstract tile = this.getMasterTile();
        if(tile == null) {
            return null;
        }
        return tile.drain(from, resource, doDrain);
    }


    @Override
    public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
        TileEntityMothershipEngineAbstract tile = this.getMasterTile();
        if(tile == null) {
            return null;
        }
        return tile.drain(from, maxDrain, doDrain);
    }


    @Override
    public boolean canFill(EnumFacing from, Fluid fluid) {
        TileEntityMothershipEngineAbstract tile = this.getMasterTile();
        if(tile == null) {
            return false;
        }
        return tile.canFill(from, fluid);
    }


    @Override
    public boolean canDrain(EnumFacing from, Fluid fluid) {
        TileEntityMothershipEngineAbstract tile = this.getMasterTile();
        if(tile == null) {
            return false;
        }
        return tile.canDrain(from, fluid);
    }


    @Override
    public FluidTankInfo[] getTankInfo(EnumFacing from) {
        TileEntityMothershipEngineAbstract tile = this.getMasterTile();
        if(tile == null) {
            return null;
        }
        return tile.getTankInfo(from);
    }



    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound var1 = new NBTTagCompound();
        writeToNBT(var1);

        return new S35PacketUpdateTileEntity(getPos(), 1, var1);
        //return new Packet132TileEntityDat(this.xCoord, this.yCoord, this.zCoord, 1, var1);
    }


    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
    }


    public ResourceLocation getBlockIconFromSide(int side) {

        // fallback
        if(side > 1) {
            return sideFallback;
        } else {
            return topFallback;
        }

    }



    @Override
    public EnumSet<EnumFacing> getElectricalInputDirections() {
        TileEntityMothershipEngineAbstract tile = this.getMasterTile();
        if(tile == null) {
            return EnumSet.noneOf(EnumFacing.class);
        }
        //EnumSet.
        return tile.getElectricalInputDirections();
    }

    @Override
    public boolean canConnect(EnumFacing direction, NetworkType type)
    {
        TileEntityMothershipEngineAbstract tile = this.getMasterTile();
        if(tile == null) {
            return false;
        }
        return tile.canConnect(direction, type);
    }

    //Five methods for compatibility with basic electricity
    @Override
    public float receiveElectricity(EnumFacing from, float receive, int tier, boolean doReceive)
    {
        TileEntityMothershipEngineAbstract tile = this.getMasterTile();
        if(tile == null) {
            return 0F;
        }
        return tile.receiveElectricity(from, receive, tier, doReceive);
    }

    @Override
    public float provideElectricity(EnumFacing from, float request, boolean doProvide)
    {
        return 0.F;// do not provide
    }

    @Override
    public float getRequest(EnumFacing direction)
    {
        // not sure what this does
        TileEntityMothershipEngineAbstract tile = this.getMasterTile();
        if(tile == null) {
            return 0F;
        }
        return tile.getRequest(direction);
    }

    @Override
    public float getProvide(EnumFacing direction)
    {
        return 0;
    }

    @Override
    public int getTierGC()
    {
        return this.tierGC;
    }

    @Override
    public void setTierGC(int newTier)
    {
        this.tierGC = newTier;
    }

    @Override
    @RuntimeInterface(clazz = "cofh.api.energy.IEnergyReceiver", modID = "")
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate)
    {
        // forward this to the master, too
        TileEntityMothershipEngineAbstract tile = this.getMasterTile();
        if(tile == null) {
            return 0;
        }
        return tile.receiveEnergy(from, maxReceive, simulate);
    }




    @Override
    public ItemStack[] getContainingItems() {

        TileEntityMothershipEngineAbstract tile = this.getMasterTile();
        if(tile == null) {
            return new ItemStack[]{};
        }

        return tile.getContainingItems();
    }



    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        TileEntityMothershipEngineAbstract tile = this.getMasterTile();
        if(tile == null) {
            return new int[] {};
        }
        return tile.getSlotsForFace(side);
    }
}
