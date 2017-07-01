package de.katzenpapst.amunra.tile;

import de.katzenpapst.amunra.mothership.Mothership;
import de.katzenpapst.amunra.mothership.MothershipWorldProvider;
import micdoodle8.mods.galacticraft.core.tile.TileEntityAdvanced;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class TileEntityMothershipSettings extends TileEntityAdvanced implements IInventoryDefaultsAdvanced {

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
        return 0;
    }

    @Override
    public ItemStack getStackInSlot(int p_70301_1_) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
        return null;
    }


    @Override
    public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getName() {
        // for now
        return "test";
    }

    @Override
    public boolean hasCustomName() {
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

        return
            this.worldObj.getTileEntity(this.getPos()) == this &&
                par1EntityPlayer.getDistanceSqToCenter(getPos()) <= 64.0D;
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

    @Override
    public ItemStack[] getContainingItems() {
        return null;
    }



}
