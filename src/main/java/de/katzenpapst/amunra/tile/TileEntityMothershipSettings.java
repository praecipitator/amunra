package de.katzenpapst.amunra.tile;

import de.katzenpapst.amunra.mothership.Mothership;
import de.katzenpapst.amunra.mothership.MothershipWorldProvider;
import micdoodle8.mods.galacticraft.core.tile.TileEntityAdvanced;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class TileEntityMothershipSettings extends TileEntityAdvanced implements IInventory {

    public TileEntityMothershipSettings() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public double getPacketRange()
    {
        return 12.0D;
    }

    @Override
    public int getPacketCooldown()
    {
        return 3;
    }

    @Override
    public boolean isNetworkedTile()
    {
        return true;
    }

    @Override
    public int getSizeInventory() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ItemStack getStackInSlot(int p_70301_1_) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getInventoryName() {
        // for now
        return "test";
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
        // hm, test
        if(!isOnMothership()) {
            return false;
        }

        if(!((MothershipWorldProvider)worldObj.provider).isPlayerOwner(par1EntityPlayer)) {
            return false;
        }
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
    public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
        return false;
    }

    public boolean isOnMothership() {
        return (this.worldObj.provider instanceof MothershipWorldProvider);
    }

    public Mothership getMothership() {
        if(!isOnMothership()) {
            return null;
        }
        return (Mothership) ((MothershipWorldProvider)worldObj.provider).getCelestialBody();
    }


}
