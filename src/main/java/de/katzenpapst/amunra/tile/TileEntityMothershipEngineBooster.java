package de.katzenpapst.amunra.tile;

import java.util.EnumSet;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.vec.Vector3int;
import micdoodle8.mods.galacticraft.api.transmission.NetworkType;
import micdoodle8.mods.galacticraft.core.energy.EnergyConfigHandler;
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseUniversalElectrical;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.planets.asteroids.AsteroidsModule;
import micdoodle8.mods.miccore.Annotations.RuntimeInterface;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
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
public class TileEntityMothershipEngineBooster extends TileBaseUniversalElectrical implements IFluidHandler, ISidedInventory, IInventory {

    public static ResourceLocation topFallback = new ResourceLocation(AsteroidsModule.ASSET_PREFIX, "textures/blocks/machine.png");
    public static ResourceLocation sideFallback = new ResourceLocation(AsteroidsModule.ASSET_PREFIX, "textures/blocks/machine_side.png");



    protected final String assetPrefix = AmunRa.ASSETPREFIX;
    protected final String assetPath = "textures/blocks/";

    protected boolean masterPresent = false;
    protected int masterX;
    protected int masterY;
    protected int masterZ;

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
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    }

    public void setMaster(int x, int y, int z) {
        masterX = x;
        masterY = y;
        masterZ = z;
        masterPresent = true;
    }

    public int getMasterX() {
        return masterX;
    }

    public int getMasterY() {
        return masterY;
    }

    public int getMasterZ() {
        return masterZ;
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
        // meh
        TileEntityMothershipEngineAbstract tile = this.getMasterTile();
        return tile != null;
    }

    /**
     * Reset and update the master, if I have any
     */
    public void updateMaster(boolean rightNow) {
        if(!masterPresent) return;

        TileEntity masterTile = worldObj.getTileEntity(masterX, masterY, masterZ);
        if(masterTile == null || !(masterTile instanceof TileEntityMothershipEngineAbstract)) {
            // apparently we just lost our master?
            this.reset();
            return;
        }
        TileEntityMothershipEngineAbstract jetTile = (TileEntityMothershipEngineAbstract)masterTile;
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

    /**
     * Using the master coordinates, get a position where the next booster could be
     * @return
     */
    public Vector3int getPossibleNextBooster() {
        if(!hasMaster()) {
            return null;
        }
        if(this.xCoord == this.masterX) {
            if(this.zCoord < this.masterZ) {
                return new Vector3int(xCoord, yCoord, zCoord-1);
            } else if(this.zCoord > this.masterZ) {
                return new Vector3int(xCoord, yCoord, zCoord+1);
            } else {
                return null;
            }
        } else if(this.zCoord == this.masterZ) {
            if(this.xCoord < this.masterX) {
                return new Vector3int(xCoord-1, yCoord, zCoord);
            } else if(this.xCoord > this.masterX) {
                return new Vector3int(xCoord+1, yCoord, zCoord);
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

    public TileEntityMothershipEngineAbstract getMasterTile() {
        if(!this.masterPresent) {
            return null;
        }
        TileEntity tile = this.worldObj.getTileEntity(masterX, masterY, masterZ);
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
    public ItemStack getStackInSlotOnClosing(int wat) {
        TileEntityMothershipEngineAbstract tile = this.getMasterTile();
        if(tile == null) {
            return null;
        }
        return tile.getStackInSlotOnClosing(wat);
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
        TileEntityMothershipEngineAbstract tile = this.getMasterTile();
        if(tile == null) {
            return false;
        }
        return tile.isItemValidForSlot(slot, stack);
    }


    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        TileEntityMothershipEngineAbstract tile = this.getMasterTile();
        if(tile == null) {
            return new int[] {};
        }
        return tile.getAccessibleSlotsFromSide(side);
    }


    @Override
    public boolean canInsertItem(int slotID, ItemStack itemstack, int side) {
        TileEntityMothershipEngineAbstract tile = this.getMasterTile();
        if(tile == null) {
            return false;
        }
        return tile.canInsertItem(slotID, itemstack, side);
    }


    @Override
    public boolean canExtractItem(int slotID, ItemStack itemstack, int side) {
        TileEntityMothershipEngineAbstract tile = this.getMasterTile();
        if(tile == null) {
            return false;
        }
        return tile.canExtractItem(slotID, itemstack, side);
    }


    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        TileEntityMothershipEngineAbstract tile = this.getMasterTile();
        if(tile == null) {
            return 0;
        }
        return tile.fill(from, resource, doFill);
    }


    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        TileEntityMothershipEngineAbstract tile = this.getMasterTile();
        if(tile == null) {
            return null;
        }
        return tile.drain(from, resource, doDrain);
    }


    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        TileEntityMothershipEngineAbstract tile = this.getMasterTile();
        if(tile == null) {
            return null;
        }
        return tile.drain(from, maxDrain, doDrain);
    }


    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        TileEntityMothershipEngineAbstract tile = this.getMasterTile();
        if(tile == null) {
            return false;
        }
        return tile.canFill(from, fluid);
    }


    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        TileEntityMothershipEngineAbstract tile = this.getMasterTile();
        if(tile == null) {
            return false;
        }
        return tile.canDrain(from, fluid);
    }


    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
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

        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, var1);
        //return new Packet132TileEntityDat(this.xCoord, this.yCoord, this.zCoord, 1, var1);
    }


    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        readFromNBT(packet.func_148857_g());
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
    public EnumSet<ForgeDirection> getElectricalInputDirections() {
        TileEntityMothershipEngineAbstract tile = this.getMasterTile();
        if(tile == null) {
            return EnumSet.noneOf(ForgeDirection.class);
        }
        //EnumSet.
        return tile.getElectricalInputDirections();
    }

    @Override
    public boolean canConnect(ForgeDirection direction, NetworkType type)
    {
        TileEntityMothershipEngineAbstract tile = this.getMasterTile();
        if(tile == null) {
            return false;
        }
        return tile.canConnect(direction, type);
    }

    //Five methods for compatibility with basic electricity
    @Override
    public float receiveElectricity(ForgeDirection from, float receive, int tier, boolean doReceive)
    {
        TileEntityMothershipEngineAbstract tile = this.getMasterTile();
        if(tile == null) {
            return 0F;
        }
        return tile.receiveElectricity(from, receive, tier, doReceive);
    }

    @Override
    public float provideElectricity(ForgeDirection from, float request, boolean doProvide)
    {
        return 0.F;// do not provide
    }

    @Override
    public float getRequest(ForgeDirection direction)
    {
        // not sure what this does
        TileEntityMothershipEngineAbstract tile = this.getMasterTile();
        if(tile == null) {
            return 0F;
        }
        return tile.getRequest(direction);
    }

    @Override
    public float getProvide(ForgeDirection direction)
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
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate)
    {
        // forward this to the master, too
        TileEntityMothershipEngineAbstract tile = this.getMasterTile();
        if(tile == null) {
            return 0;
        }
        return tile.receiveEnergy(from, maxReceive, simulate);
    }
}
