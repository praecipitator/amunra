package de.katzenpapst.amunra.tile;

import java.util.List;

import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.helper.CoordHelper;
import de.katzenpapst.amunra.helper.NbtHelper;
import micdoodle8.mods.galacticraft.api.entity.IAntiGrav;
import micdoodle8.mods.galacticraft.api.power.IEnergyHandlerGC;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase;
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlock;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;

public class TileEntityGravitation extends TileBaseElectricBlock implements IInventoryDefaultsAdvanced, IEnergyHandlerGC {

    //protected Vector3 gravityVector;

    protected double gravity;

    private ItemStack[] containingItems = new ItemStack[1];

    protected AxisAlignedBB gravityBox;

    public boolean isBoxShown;

    public TileEntityGravitation() {
        isBoxShown = false;

        //gravityVector =  new Vector3(0.0, -0.05D, 0.0);
        gravity = -0.05D;
        //Vector3 center = new Vector3(xCoord+0.5D, yCoord+0.5D, zCoord+0.5D);

        //gravityBox = AxisAlignedBB.getBoundingBox(center.x - range, center.y - 0.5, center.z - range, center.x + range, center.y + range, center.z + range);
        gravityBox = new AxisAlignedBB( - 5.0, 0,  - 5.0,  + 5.0,  + 5.0, + 5.0);

        updateEnergyConsumption();
    }

    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound data = new NBTTagCompound();
        this.writeToNBT(data);
        return new S35PacketUpdateTileEntity(this.getPos(), 2, data);
    }

    @Override
    public void onDataPacket(NetworkManager netManager, S35PacketUpdateTileEntity packet)
    {
        readFromNBT(packet.getNbtCompound());
    }


    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        if(this.isBoxShown) {

            return getActualGravityBox();
        }
        return super.getRenderBoundingBox();
    }

    protected AxisAlignedBB getActualGravityBox()
    {
        int xCoord = this.getPos().getX();
        int yCoord = this.getPos().getY();
        int zCoord = this.getPos().getZ();
        AxisAlignedBB box = getRotatedAABB();//AxisAlignedBB.getBoundingBox(center.x - range, center.y - 0.5, center.z - range, center.x + range, center.y + range, center.z + range);
        box = new AxisAlignedBB(
                xCoord + box.minX, yCoord + box.minY, zCoord + box.minZ,
                xCoord + box.maxX + 1, yCoord + box.maxY + 1, zCoord + box.maxZ + 1
                );
        return box;
    }

    public AxisAlignedBB getRotatedAABB()
    {
        int rotationMeta = this.getRotationMeta();
        AxisAlignedBB in = getGravityBox();

        /*
         * Z
         * ^
         * |            maxVec
         * |                 v
         * +-----------------+
         * |                 |
         * |                 |
         * |                 |
         * |                 |
         * |          +--+   |
         * |          |  |   |
         * |          X--+   |
         * |                 |
         * |                 |
         * +-----------------+--------------> X
         * ^
         * minVec
         */

        switch(rotationMeta) {
        case 0: // identity
            return CoordHelper.cloneAABB(in); // correct
        case 1: // rotate 180°
            // minX <- maxX
            // maxX <- minX
            // minZ <- maxZ
            // maxZ <- minZ
            return new AxisAlignedBB(in.maxX * -1, in.minY, in.maxZ * -1, in.minX * -1, in.maxY, in.minZ * -1);
            // correct
        case 2: // rotate 270° in uhrzeigersinn
            // wrong
            // minX <- maxZ
            // maxX <- minZ
            // minZ <- minX
            // maxZ <- maxX
            //return AxisAlignedBB.getBoundingBox(in.maxZ * -1, in.minY, in.minX, in.minZ * -1, in.maxY, in.maxX);
            return new AxisAlignedBB(in.minZ, in.minY, in.maxX * -1, in.maxZ, in.maxY, in.minX * -1);
        case 3: // rotate 90°
            // minX <- minZ
            // maxX <- maxZ
            // minZ <- maxX
            // maxZ <- minX
            //return AxisAlignedBB.getBoundingBox(in.minZ, in.minY, in.maxX * -1, in.maxZ, in.maxY, in.minX * -1);
            return new AxisAlignedBB(in.maxZ * -1, in.minY, in.minX, in.minZ * -1, in.maxY, in.maxX);
        }

        return in;
    }

    @Override
    public void update() {
        super.update();

        if(this.isRunning()) {
            doGravity();
        }
    }

    public boolean isRunning() {
        return !this.getDisabled(0) && this.hasEnoughEnergyToRun;
    }

    /*public void setGravityVector(Vector3 vec)
    {
        //this.gravityVector = vec;
        gravity = vec.y;
    }*/

    public void setGravityForce(double value)
    {
        gravity = value;
    }

    public double getGravityForce() {
        return gravity;
        //return gravityVector;
    }
    /*
    public Vector3 getGravityVector() {
        return new Vector3(0, gravity, 0);
        //return gravityVector;
    }
    */

    public AxisAlignedBB getGravityBox() {
        //return AxisAlignedBB.getBoundingBox( - range, - 0.5,  - range,  + range,  + range, + range);
        return gravityBox;
    }

    public void setGravityBox(AxisAlignedBB box) {
        gravityBox = box;
    }

    protected void doGravity()
    {
        AxisAlignedBB box = getActualGravityBox();

        if(!worldObj.isRemote) {
            final List<?> list = this.worldObj.getEntitiesWithinAABB(Entity.class, box);

            for(Object e: list) {
                if(e instanceof IAntiGrav) {
                    continue;
                }
                Entity ent = (Entity)e;
                if(!(ent instanceof EntityPlayer)) {
                    ent.addVelocity(0.0D, gravity , 0.0D);
                    // do something with the fall distance
                }
                ent.fallDistance -= gravity * 10.0F;
                if(ent.fallDistance < 0) {
                    ent.fallDistance = 0.0F;
                }
            }
        } else {
            // player stuff has to be done on client
            final List<?> list = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, box);
            for(Object e: list) {
                EntityPlayer p = (EntityPlayer)e;
                AmunRa.proxy.handlePlayerArtificalGravity(p, gravity);
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
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.containingItems = NbtHelper.readInventory(nbt, containingItems.length);

        if(nbt.hasKey("gravforce")) {
            double grav = nbt.getDouble("gravforce");
            if(grav == 0) {
                grav = -0.05D;
            }
            this.setGravityForce(grav);
        } else {
            // backwards compatibility
            if(nbt.hasKey("gravity")) {
                Vector3 grav = new Vector3(nbt.getCompoundTag("gravity"));
                this.setGravityForce(grav.y);
            }
        }
        if(nbt.hasKey("aabb")) {
            AxisAlignedBB box = NbtHelper.readAABB(nbt.getCompoundTag("aabb"));
            this.setGravityBox(box);
        }
        updateEnergyConsumption();
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        NbtHelper.writeInventory(nbt, containingItems);


        /*NBTTagCompound gravityVectorNBT = new NBTTagCompound();
        gravityVector.writeToNBT(gravityVectorNBT);*/

        NBTTagCompound aabbNBT = NbtHelper.getAsNBT(gravityBox);

        //nbt.setTag("gravity", gravityVectorNBT);
        nbt.setDouble("gravforce", gravity);
        nbt.setTag("aabb", aabbNBT);
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
    public String getName() {
        return GCCoreUtil.translate("tile.gravity.name");
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
    public EnumFacing getElectricInputDirection() {
        int metadata = getRotationMeta();
        return CoordHelper.rotateForgeDirection(EnumFacing.NORTH, metadata);
    }

    @Override
    public ItemStack getBatteryInSlot() {
        return this.getStackInSlot(0);
    }

    public void updateEnergyConsumption() {
        double strength = Math.abs(gravity); //getGravityVector().getMagnitude();
        AxisAlignedBB box = getGravityBox();

        Vector3 size = new Vector3(
                box.maxX - box.minX + 1,
                box.maxY - box.minY + 1,
                box.maxZ - box.minZ + 1
        );
        double numBlocks = size.x * size.y * size.z;

        float maxExtract = (float) (numBlocks * strength);
        this.storage.setMaxExtract(maxExtract);
    }

    @Override
    public ItemStack[] getContainingItems() {
        return containingItems;
    }

    @Override
    public EnumFacing getFront() {
        return EnumFacing.NORTH;
    }
}
