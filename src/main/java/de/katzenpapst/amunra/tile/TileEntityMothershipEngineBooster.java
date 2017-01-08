package de.katzenpapst.amunra.tile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.vec.Vector3int;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.planets.asteroids.AsteroidsModule;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
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

    public static ResourceLocation texLoc = new ResourceLocation(AsteroidsModule.ASSET_PREFIX, "textures/blocks/machine.png");
    public static ResourceLocation sideFallback = new ResourceLocation(AsteroidsModule.ASSET_PREFIX, "textures/blocks/machine_side.png");
    public static ResourceLocation frontSide = new ResourceLocation(AmunRa.ASSETPREFIX, "textures/blocks/jet-front.png");


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
        if(!(tile instanceof TileEntityMothershipEngineJet)) {
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
        TileEntityMothershipEngineJet tile = this.getMasterTile();
        return tile != null;
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

    /**
     * Using the master coordinates, get a position where the next booster could be
     * @return
     */
    public Vector3int getPossibleNextBooster() {
        if(this.xCoord == this.masterX) {
            if(this.zCoord < this.masterZ) {
                return new Vector3int(xCoord, yCoord, zCoord-1);
            } else {
                return new Vector3int(xCoord, yCoord, zCoord+1);
            }
        } else if(this.zCoord == this.masterZ) {
            if(this.xCoord < this.masterX) {
                return new Vector3int(xCoord-1, yCoord, zCoord);
            } else {
                return new Vector3int(xCoord+1, yCoord, zCoord);
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

    protected TileEntityMothershipEngineJet getMasterTile() {
        if(!this.masterPresent) {
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

    protected String getSideTextureName(String prefix, boolean isActive, boolean isFirst, boolean isLast)
    {
        StringBuilder builder = new StringBuilder();
        builder.append(prefix);
        builder.append("jet-side");

        if(isActive) {
            builder.append("-active");
        }
        if(isFirst && isLast) {
            builder.append("-single");
        } else {
            if(isFirst) {
                builder.append("-start");
            }
            if(isLast) {
                builder.append("-end");
            }
        }

        builder.append(".png");

        return builder.toString();
    }

    protected String getTopTextureName(String prefix, boolean isFirst, boolean isLast)
    {
        StringBuilder builder = new StringBuilder();
        builder.append(prefix);
        builder.append("jet-top");

        if(isFirst && isLast) {
            builder.append("-single");
        } else {
            if(isFirst) {
                builder.append("-start");
            }
            if(isLast) {
                builder.append("-end");
            }
        }

        builder.append(".png");

        return builder.toString();
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

    @SideOnly(Side.CLIENT)
    public boolean doRotateTopIcon() {
        return hasMaster() && masterZ == zCoord;
    }

    public ResourceLocation getBlockIconFromSide(int side) {
        // check where we are in a metablock
        TileEntityMothershipEngineJet masterTile = this.getMasterTile();
        if(masterTile == null) {
            // fallback
            if(side > 1) {
                return sideFallback;
            } else {
                return texLoc;
            }
        }
        // now check where in the thing we are

        int nrInMultiblock = 0;
        boolean isFirst = false;
        boolean isLast = false;

        if(masterX == xCoord) {
            // we are on the same x
            nrInMultiblock = masterZ-zCoord;

        } else {
            // same z
            nrInMultiblock = masterX-xCoord;
        }
        if(nrInMultiblock == 1 || -nrInMultiblock == masterTile.getNumBoosters()) {
            isFirst = true;
        }
        if(nrInMultiblock == -1 || nrInMultiblock == masterTile.getNumBoosters()) {
            isLast = true;
        }

        //masterTile.getNumBoosters()
        /*renderFaceYNeg = 0
        renderFaceYPos = 1
        renderFaceZNeg = 2
        renderFaceZPos = 3
        renderFaceXNeg = 4
        renderFaceXPos = 5*/
        String tex;
        switch(side) {
        case 0: // bottom
        case 1: // top
            tex = getTopTextureName(assetPath, isFirst, isLast);
            return new ResourceLocation(assetPrefix, tex);
        case 2:
        case 3: // z sides
            if(masterX == xCoord) {
                return frontSide;
            } else {
                tex = getSideTextureName(assetPath, masterTile.isInUse(), isFirst, isLast);
                return new ResourceLocation(assetPrefix, tex);
            }
        case 4:
        case 5: // x sides
            if(masterX == xCoord) {
                tex = getSideTextureName(assetPath, masterTile.isInUse(), isFirst, isLast);
                return new ResourceLocation(assetPrefix, tex);
            } else {
                return frontSide;
            }
        }
        return texLoc;// fallback
    }
}
