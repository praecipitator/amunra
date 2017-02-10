package de.katzenpapst.amunra.tile;

import java.util.List;

import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.world.CoordHelper;
import micdoodle8.mods.galacticraft.api.power.IEnergyHandlerGC;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase;
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlock;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityGravitation extends TileBaseElectricBlock implements IInventory, IEnergyHandlerGC {

    protected double range = 5.0;

    protected Vector3 gravityVector;

    private ItemStack[] containingItems = new ItemStack[1];

    protected AxisAlignedBB gravityBox;

    public boolean isBoxShown;

    public TileEntityGravitation() {
        isBoxShown = false;

        gravityVector =  new Vector3(0.0, -0.04D, 0.0);
        //Vector3 center = new Vector3(xCoord+0.5D, yCoord+0.5D, zCoord+0.5D);

        //gravityBox = AxisAlignedBB.getBoundingBox(center.x - range, center.y - 0.5, center.z - range, center.x + range, center.y + range, center.z + range);
        gravityBox = AxisAlignedBB.getBoundingBox( - range, 0,  - range,  + range,  + range, + range);
    }


    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        if(this.isBoxShown) {

            AxisAlignedBB box = getGravityBox();


            return AxisAlignedBB.getBoundingBox(
                    xCoord + box.minX, yCoord + box.minY, zCoord + box.minZ,
                    xCoord + box.maxX + 1, yCoord + box.maxY + 1, zCoord + box.maxZ + 1
                    );
        }
        return super.getRenderBoundingBox();
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if(!this.getDisabled(0) && this.hasEnoughEnergyToRun) {
            doGravity();
        }


    }

    public void setGravityVector(Vector3 vec)
    {
        this.gravityVector = vec;
    }

    public Vector3 getGravityVector() {
        return gravityVector;
    }

    public AxisAlignedBB getGravityBox() {
        //return AxisAlignedBB.getBoundingBox( - range, - 0.5,  - range,  + range,  + range, + range);
        return gravityBox;
    }

    public void setGravityBox(AxisAlignedBB box) {
        gravityBox = box;
    }

    protected void doGravity()
    {
        // now try stuff
        //Vector3 center = new Vector3(xCoord+0.5D, yCoord+0.5D, zCoord+0.5D);

        AxisAlignedBB box = getGravityBox();//AxisAlignedBB.getBoundingBox(center.x - range, center.y - 0.5, center.z - range, center.x + range, center.y + range, center.z + range);
        box = AxisAlignedBB.getBoundingBox(
                xCoord + box.minX, yCoord + box.minY, zCoord + box.minZ,
                xCoord + box.maxX + 1, yCoord + box.maxY + 1, zCoord + box.maxZ + 1
                );

        if(!worldObj.isRemote) {
            final List<?> list = this.worldObj.getEntitiesWithinAABB(Entity.class, box);

            for(Object e: list) {
                Entity ent = (Entity)e;
                /*if(ent.motionY <= 0) {
                    ent.motionY = 0.01;
                }
                ent.motionY *= 0.91;*/
                //ent.motionY -= 0.03999999910593033D;

                ent.addVelocity(gravityVector.x, gravityVector.y, gravityVector.z);
                /*if(ent instanceof EntityPlayerMP) {
                    GCPlayerStats stats = GCPlayerStats.get((EntityPlayerMP)ent);
                    stats.
                }*/
            }
        } else {
            // player stuff has to be done on client
            final List<?> list = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, box);
            for(Object e: list) {
                EntityPlayer p = (EntityPlayer)e;
                AmunRa.proxy.handlePlayerArtificalGravity(p, gravityVector);
            }
        }
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
    public String getInventoryName() {
        return GCCoreUtil.translate("tile.artificialgravity.name");
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
    public void openInventory() {
    }

    @Override
    public void closeInventory() {
    }

    @Override
    public boolean isItemValidForSlot(int slotNr, ItemStack stack) {

        switch(slotNr) {
        case 0: // battery
            return ItemElectricBase.isElectricItem(stack.getItem());
        default:
            return false;
        }

    }

    @Override
    public boolean shouldUseEnergy() {
        return !this.getDisabled(0);
    }

    public int getRotationMeta(int meta) {
        return (meta & 12) >> 2;
    }

    public int getRotationMeta() {
        return (this.getBlockMetadata() & 12) >> 2;
    }

    @Override
    public ForgeDirection getElectricInputDirection() {
        int metadata = getRotationMeta();
        return CoordHelper.rotateForgeDirection(ForgeDirection.NORTH, metadata);
    }

    @Override
    public ItemStack getBatteryInSlot() {
        return this.getStackInSlot(0);
    }

}
