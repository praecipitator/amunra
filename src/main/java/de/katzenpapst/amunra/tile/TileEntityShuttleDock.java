package de.katzenpapst.amunra.tile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.GuiIds;
import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.entity.spaceship.EntityShuttle;
import de.katzenpapst.amunra.item.ItemShuttle;
import de.katzenpapst.amunra.network.packet.PacketSimpleAR;
import de.katzenpapst.amunra.vec.Vector3int;
import de.katzenpapst.amunra.world.ShuttleDockHandler;
import micdoodle8.mods.galacticraft.api.entity.ICargoEntity;
import micdoodle8.mods.galacticraft.api.entity.IDockable;
import micdoodle8.mods.galacticraft.api.entity.IFuelable;
import micdoodle8.mods.galacticraft.api.entity.ILandable;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase.EnumLaunchPhase;
import micdoodle8.mods.galacticraft.api.entity.ICargoEntity.EnumCargoLoadingState;
import micdoodle8.mods.galacticraft.api.entity.ICargoEntity.RemovalResult;
import micdoodle8.mods.galacticraft.api.tile.IFuelDock;
import micdoodle8.mods.galacticraft.api.tile.ILandingPadAttachable;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.blocks.BlockMulti;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import micdoodle8.mods.galacticraft.core.network.IPacketReceiver;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.tile.IMultiBlock;
import micdoodle8.mods.galacticraft.core.tile.TileEntityAdvanced;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.miccore.Annotations.NetworkedField;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.FluidStack;

public class TileEntityShuttleDock extends TileEntityAdvanced implements IFuelable, IFuelDock, ICargoEntity, IMultiBlock, IInventory, IPacketReceiver {

    protected boolean hasShuttleDocked;
    protected int actionCooldown;

    protected ItemStack[] containingItems;
    protected IDockable dockedEntity;


    public enum DockOperation {
        DEPLOY_SHUTTLE,
        GET_SHUTTLE,
        MOUNT_SHUTTLE
    }

    public TileEntityShuttleDock() {
        containingItems = new ItemStack[1]; // one item
    }

    protected void dropItemsAtExit(List<ItemStack> cargo) {
        Vector3 pos = this.getExitPosition();
        for (final ItemStack item : cargo)
        {
            EntityItem itemEntity = new EntityItem(this.worldObj, pos.x, pos.y, pos.z, item);
            this.worldObj.spawnEntityInWorld(itemEntity);
        }
    }

    public void performDockOperation(int op, EntityPlayerMP player) {
        if(op >= DockOperation.values().length) {
            return;
        }
        performDockOperation(DockOperation.values()[op], player);
    }

    /**
     * Server-side part
     * @param op
     * @param player
     */
    public void performDockOperation(DockOperation op, EntityPlayerMP player) {
        if(actionCooldown > 0) {
            return;
        }
        actionCooldown = 20;
        ItemShuttle shuttleItem;
        EntityShuttle shuttleEntity;
        ItemStack stack;
        switch(op) {
        case DEPLOY_SHUTTLE:
            stack = this.getStackInSlot(0);
            if(stack == null || stack.stackSize == 0 || !(stack.getItem() instanceof ItemShuttle)) {
                return;
            }
            shuttleItem = ((ItemShuttle)stack.getItem());
            Vector3 pos = this.getShuttlePosition();
            shuttleEntity = shuttleItem.spawnRocketEntity(stack, worldObj, pos.x, pos.y, pos.z);
            // shuttleEntity.setPad(this);
            this.dockEntity(shuttleEntity);
            stack.stackSize--;
            if(stack.stackSize <= 0) {
                stack = null;
            }
            this.setInventorySlotContents(0, stack);
            this.hasShuttleDocked = true;
            break;
        case GET_SHUTTLE:
            if(this.dockedEntity == null) {
                return;
            }
            shuttleEntity = ((EntityShuttle)dockedEntity);
            //if(shuttleEntity.addCargo(stack, doAdd))
            stack =  shuttleEntity.getItemRepresentation();

            List<ItemStack> cargo = shuttleEntity.getCargoContents();
            dropItemsAtExit(cargo);

            this.setInventorySlotContents(0, stack);
            shuttleEntity.setDead();
            this.dockedEntity = null;
            this.hasShuttleDocked = false;

            break;
        case MOUNT_SHUTTLE:
            if(this.dockedEntity == null) {
                return;
            }
            shuttleEntity = ((EntityShuttle)dockedEntity);
            player.mountEntity(shuttleEntity);
            GalacticraftCore.packetPipeline.sendTo(new PacketSimple(PacketSimple.EnumSimplePacket.C_CLOSE_GUI, new Object[] { }), player);
            break;
        default:
            return;

        }
        this.markDirty();
        this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public void performDockOperationClient(DockOperation op) {
        int opInt = op.ordinal();

        Object[] payload = new Object[] {
                this.xCoord,
                this.yCoord,
                this.zCoord,
                opInt
        };
        AmunRa.packetPipeline.sendToServer(new PacketSimpleAR(PacketSimpleAR.EnumSimplePacket.S_DOCK_OPERATION, payload));
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.containingItems = this.readStandardItemsFromNBT(nbt);
        hasShuttleDocked = nbt.getBoolean("hasShuttle");
        actionCooldown = nbt.getInteger("actionCooldown");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        this.writeStandardItemsToNBT(nbt);
        nbt.setBoolean("hasShuttle", hasShuttleDocked);
        nbt.setInteger("actionCooldown", actionCooldown);
    }

    public ItemStack[] readStandardItemsFromNBT(NBTTagCompound nbt)
    {
        final NBTTagList itemTag = nbt.getTagList("Items", 10);
        int length = containingItems.length;
        ItemStack[] result = new ItemStack[length];

        for (int i = 0; i < itemTag.tagCount(); ++i)
        {
            final NBTTagCompound stackNbt = itemTag.getCompoundTagAt(i);
            final int slotNr = stackNbt.getByte("Slot") & 255;

            if (slotNr < length)
            {
                result[slotNr] = ItemStack.loadItemStackFromNBT(stackNbt);
            }
        }
        return result;
    }

    public boolean hasShuttle() {
        return this.hasShuttleDocked;
    }

    public void writeStandardItemsToNBT(NBTTagCompound nbt)
    {
        final NBTTagList list = new NBTTagList();
        int length = containingItems.length;


        for (int i = 0; i < length; ++i)
        {
            if (containingItems[i] != null)
            {
                final NBTTagCompound stackNbt = new NBTTagCompound();
                stackNbt.setByte("Slot", (byte) i);
                containingItems[i].writeToNBT(stackNbt);
                list.appendTag(stackNbt);
            }
        }

        nbt.setTag("Items", list);
    }

    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        writeToNBT(nbt);

        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, nbt);
        //return new Packet132TileEntityDat(this.xCoord, this.yCoord, this.zCoord, 1, var1);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        readFromNBT(packet.func_148857_g());
    }

    public Vector3 getShuttlePosition() {
        switch (this.getRotationMeta())
        {
        case 0: // -> +Z (the side which is towards the player)
            return new Vector3(xCoord + 0.5, yCoord, zCoord - 2.0D);
        case 2: // -> -Z
            return new Vector3(xCoord - 2.0D, yCoord, zCoord + 0.5D);
        case 1: // -> -X
            return new Vector3(xCoord + 0.5, yCoord, zCoord + 3.0D);
        case 3: // -> +X
            return new Vector3(xCoord + 3.0D, yCoord, zCoord+0.5D);
        }
        return null;
    }

    public float getExitRotation() {
        System.out.println("ad "+this.getRotationMeta());
        switch (this.getRotationMeta())
        {
        case 0: // -> +Z (the side which is towards the player)
            return 0.0F;
        case 2: // -> -Z
            return 270.0F;
        case 1: // -> -X
            return 180.0F;
        case 3: // -> +X
            return 90.0F;
        }
        return 0;
    }

    public Vector3 getExitPosition() {
        switch (this.getRotationMeta())
        {
        case 0: // -> +Z (the side which is towards the player)
            return new Vector3(xCoord + 0.5, yCoord, zCoord + 1.5D);
        case 2: // -> -Z
            return new Vector3(xCoord + 1.5D, yCoord, zCoord + 0.5D);
        case 1: // -> -X
            return new Vector3(xCoord + 0.5, yCoord, zCoord - 0.5D);
        case 3: // -> +X
            return new Vector3(xCoord - 0.5D, yCoord, zCoord+0.5D);
        }
        return null;
    }

    protected void repositionEntity() {
        Vector3 pos = getShuttlePosition();

        ((Entity)this.dockedEntity).setPosition(pos.x, pos.y, pos.z);
    }

    protected void dockNearbyShuttle() {
        // this is an awful hack...
        Vector3 expectedPosition = this.getShuttlePosition();
        final List<?> list = this.worldObj.getEntitiesWithinAABB(EntityShuttle.class,
                AxisAlignedBB.getBoundingBox(
                        expectedPosition.x - 0.5D, expectedPosition.y - 0.5D, expectedPosition.z - 0.5D,
                        expectedPosition.x + 0.5D, expectedPosition.y + 0.5D, expectedPosition.z + 0.5D
                        ));

        boolean docked = false;

        for (final Object o : list)
        {
            if (o instanceof EntityShuttle)
            {
                docked = true;

                final EntityShuttle fuelable = (EntityShuttle) o;
                if(fuelable.launchPhase == EnumLaunchPhase.UNIGNITED.ordinal()) {


                    //fuelable.setPad(this);
                    this.dockEntity(fuelable);

                    break;
                }
            }
        }
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if(!this.worldObj.isRemote) {
            if(actionCooldown > 0) {
                actionCooldown--;
            }
            if(this.dockedEntity != null) {

                EntityShuttle shuttle = ((EntityShuttle)this.dockedEntity);
                if(shuttle.launchPhase == EnumLaunchPhase.LAUNCHED.ordinal()) {
                    // undock
                    shuttle.setPad(null);
                    this.dockedEntity = null;
                } else {
                    // from time to time, reposition?
                    if(this.ticks % 40 == 0) {
                        repositionEntity();
                    }
                }
            }

            if(dockedEntity == null){
                // attempt to redock something
                this.dockNearbyShuttle();
            }

            // update status
            if(dockedEntity == null && hasShuttleDocked) {
                hasShuttleDocked = false;
                this.markDirty();
                this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            } else if(dockedEntity != null && !hasShuttleDocked) {
                hasShuttleDocked = true;
                this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            }
        }
    }

    public int getRotationMeta(int meta) {
        return (meta & 12) >> 2;
    }

    public int getRotationMeta() {
        return (this.getBlockMetadata() & 12) >> 2;
    }

    @Override
    public EnumCargoLoadingState addCargo(ItemStack stack, boolean doAdd) {
        if (this.dockedEntity != null)
        {
            return this.dockedEntity.addCargo(stack, doAdd);
        }

        return EnumCargoLoadingState.NOTARGET;
    }

    @Override
    public RemovalResult removeCargo(boolean doRemove) {
        if (this.dockedEntity != null)
        {
            return this.dockedEntity.removeCargo(doRemove);
        }

        return new RemovalResult(EnumCargoLoadingState.NOTARGET, null);
    }

    protected void checkTileAt(HashSet<ILandingPadAttachable> connectedTiles, int x, int y, int z) {
        final TileEntity tile = this.worldObj.getTileEntity(x, y, z);

        if (tile != null && tile instanceof ILandingPadAttachable && ((ILandingPadAttachable) tile).canAttachToLandingPad(this.worldObj, this.xCoord, this.yCoord, this.zCoord))
        {
            connectedTiles.add((ILandingPadAttachable) tile);
        }
    }

    @Override
    public HashSet<ILandingPadAttachable> getConnectedTiles() {
        HashSet<ILandingPadAttachable> connectedTiles = new HashSet<ILandingPadAttachable>();

        // check the blocks in a doorframe form around me
        // below
        checkTileAt(connectedTiles, xCoord, yCoord-1, zCoord);

        // above
        checkTileAt(connectedTiles, xCoord, yCoord+2, zCoord);

        // sides
        switch (this.getRotationMeta())
        {
        case 0: // -> +Z (the side which is towards the player)
        case 2: // -> -Z
            checkTileAt(connectedTiles, xCoord-1, yCoord, zCoord);
            checkTileAt(connectedTiles, xCoord+1, yCoord, zCoord);
            checkTileAt(connectedTiles, xCoord-1, yCoord+1, zCoord);
            checkTileAt(connectedTiles, xCoord+1, yCoord+1, zCoord);
            break;
        case 1: // -> -X
        case 3: // -> +X
            checkTileAt(connectedTiles, xCoord, yCoord, zCoord-1);
            checkTileAt(connectedTiles, xCoord, yCoord, zCoord+1);
            checkTileAt(connectedTiles, xCoord, yCoord+1, zCoord-1);
            checkTileAt(connectedTiles, xCoord, yCoord+1, zCoord+1);
            break;
        }
        // maybe do the edges, too?

        return connectedTiles;
    }

    @Override
    public boolean isBlockAttachable(IBlockAccess world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        // maybe prevent launch controllers from working here?
        if (tile != null && tile instanceof ILandingPadAttachable)
        {
            return ((ILandingPadAttachable) tile).canAttachToLandingPad(world, this.xCoord, this.yCoord, this.zCoord);
        }

        return false;
    }

    @Override
    public IDockable getDockedEntity() {
        return this.dockedEntity;
    }

    @Override
    public void dockEntity(IDockable entity) {
        if(entity == this.dockedEntity) {
            return;
        }
        if(entity instanceof EntityShuttle) {
            this.dockedEntity = entity;
            ((EntityShuttle)entity).setPad(this);
            repositionEntity();
        } else if(entity == null) {
            this.dockedEntity = null;
        }
    }

    @Override
    public int addFuel(FluidStack fluid, boolean doFill) {
        if (this.dockedEntity != null)
        {
            return this.dockedEntity.addFuel(fluid, doFill);
        }

        return 0;
    }

    @Override
    public FluidStack removeFuel(int amount) {
        if (this.dockedEntity != null)
        {
            return this.dockedEntity.removeFuel(amount);
        }

        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {

        switch(this.getRotationMeta()) {
        case 0:
            return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord-1, xCoord+1, yCoord + 2, zCoord+1);
        case 1:
            return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord+1, yCoord + 2, zCoord+2);
        case 2:
            return AxisAlignedBB.getBoundingBox(xCoord-1, yCoord, zCoord, xCoord+1, yCoord + 2, zCoord+1);
        case 3:
            return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord+2, yCoord + 2, zCoord+1);
        }
        return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord+1, yCoord + 2, zCoord+1);
    }

    @Override
    public double getPacketRange()
    {
        return 30.0D;
    }

    @Override
    public int getPacketCooldown()
    {
        return 50;
    }

    @Override
    public boolean isNetworkedTile() {
        return true;
    }

    @Override
    public boolean onActivated(EntityPlayer entityPlayer) {
        entityPlayer.openGui(AmunRa.instance, GuiIds.GUI_SHUTTLE_DOCK, this.worldObj, xCoord, yCoord, zCoord);
        return true;
    }

    @Override
    public void onCreate(BlockVec3 placedPosition) {
        ShuttleDockHandler.addDock(this);

        int buildHeight = this.worldObj.getHeight() - 1;

        if (placedPosition.y + 1 > buildHeight) return;

        final BlockVec3 vecStrut = new BlockVec3(placedPosition.x, placedPosition.y + 1, placedPosition.z);
        ARBlocks.metaBlockFake.makeFakeBlock(worldObj, vecStrut, new BlockVec3(xCoord, yCoord, zCoord), ARBlocks.fakeBlockSealable);
        /*this.worldObj.setBlock(p_147449_1_, p_147449_2_, p_147449_3_, p_147449_4_)
        ((BlockMulti) GCBlocks.fakeBlock).makeFakeBlock(this.worldObj, vecStrut, placedPosition, 0);*/
    }

    @Override
    public void onDestroy(TileEntity callingBlock) {
        ShuttleDockHandler.removeDock(this);

        Block b = this.worldObj.getBlock(xCoord, yCoord+1, zCoord);
        if(b == ARBlocks.metaBlockFake) {
            this.worldObj.setBlockToAir(xCoord, yCoord+1, zCoord);
        }
        if(callingBlock != this) {
            // someone else called this, drop my actual block, too
            this.worldObj.func_147480_a(this.xCoord, this.yCoord, this.zCoord, true);

        }
    }

    @Override
    public String getInventoryName() {
        return GCCoreUtil.translate("tile.shuttleDock.name");
    }

    @Override
    public int getSizeInventory() {
        return containingItems.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return containingItems[slot];
    }

    @Override
    public ItemStack decrStackSize(int slotNr, int quantity) {
        if (this.containingItems[slotNr] != null)
        {
            ItemStack resultStack;

            if (this.containingItems[slotNr].stackSize <= quantity)
            {
                resultStack = this.containingItems[slotNr];
                this.containingItems[slotNr] = null;
                return resultStack;
            }
            else
            {
                resultStack = this.containingItems[slotNr].splitStack(quantity);

                if (this.containingItems[slotNr].stackSize == 0)
                {
                    this.containingItems[slotNr] = null;
                }

                return resultStack;
            }
        }
        else
        {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slotNr) {
        if (this.containingItems[slotNr] != null)
        {
            final ItemStack result = this.containingItems[slotNr];
            this.containingItems[slotNr] = null;
            return result;
        }
        else
        {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int slotNr, ItemStack newStack)
    {
        this.containingItems[slotNr] = newStack;

        if (newStack != null && newStack.stackSize > this.getInventoryStackLimit())
        {
            newStack.stackSize = this.getInventoryStackLimit();
        }
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
    public boolean isUseableByPlayer(EntityPlayer player) {
        return (player.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D);

    }

    @Override
    public void openInventory() {
    }

    @Override
    public void closeInventory() {
    }

    @Override
    public boolean isItemValidForSlot(int slotNr, ItemStack item) {

        return slotNr == 0 && (item.getItem() instanceof ItemShuttle);
    }

}
