package de.katzenpapst.amunra.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * This is supposed to be an universal booster TileEntity, used by all booster blocks
 * @author katzenpapst
 *
 */
public class TileEntityMothershipEngineBooster extends TileEntity {

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
}
