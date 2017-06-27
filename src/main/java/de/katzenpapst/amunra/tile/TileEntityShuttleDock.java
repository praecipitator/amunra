package de.katzenpapst.amunra.tile;

import java.util.HashSet;
import java.util.List;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.GuiIds;
import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.entity.spaceship.EntityShuttle;
import de.katzenpapst.amunra.item.ItemShuttle;
import de.katzenpapst.amunra.network.packet.PacketSimpleAR;
import de.katzenpapst.amunra.world.ShuttleDockHandler;
import micdoodle8.mods.galacticraft.api.entity.ICargoEntity;
import micdoodle8.mods.galacticraft.api.entity.IDockable;
import micdoodle8.mods.galacticraft.api.entity.IFuelable;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase.EnumLaunchPhase;
import micdoodle8.mods.galacticraft.api.tile.IFuelDock;
import micdoodle8.mods.galacticraft.api.tile.ILandingPadAttachable;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.blocks.BlockMulti.EnumBlockMultiType;
import micdoodle8.mods.galacticraft.core.network.IPacketReceiver;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.tile.IMultiBlock;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public class TileEntityShuttleDock extends TileEntityAdvancedInventory implements IFuelable, IFuelDock, ICargoEntity, IMultiBlock, IPacketReceiver {

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
            if(this.dockedEntity != null) {
                return; // doesn't work
            }
            stack = this.getStackInSlot(0);
            if(stack == null || stack.stackSize == 0 || !(stack.getItem() instanceof ItemShuttle)) {
                return;
            }
            if(this.isObstructed()) {
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
            stack = this.getStackInSlot(0);
            if(stack != null) {
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
            if(shuttleEntity.riddenByEntity != null) {
                return;
            }
            player.mountEntity(shuttleEntity);



            GalacticraftCore.packetPipeline.sendTo(new PacketSimple(
                    PacketSimple.EnumSimplePacket.C_CLOSE_GUI,
                    GCCoreUtil.getDimensionID(player.worldObj),
                    new Object[] { }), player);
            break;
        default:
            return;

        }
        updateAvailabilityInWorldData();
        this.markDirty();
        this.worldObj.markBlockForUpdate(this.getPos());
    }

    public void performDockOperationClient(DockOperation op) {
        int opInt = op.ordinal();

        Object[] payload = new Object[] {
                this.getPos(),
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

        return new S35PacketUpdateTileEntity(this.getPos(), 1, nbt);
        //return new Packet132TileEntityDat(this.xCoord, this.yCoord, this.zCoord, 1, var1);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
    }

    public Vector3 getShuttlePosition() {
        double xCoord = this.pos.getX();
        double yCoord = this.pos.getY();
        double zCoord = this.pos.getZ();
        switch (this.getRotationMeta())
        {
        case 0:
            return new Vector3(xCoord + 0.5,  yCoord, zCoord - 1.5D);
        case 2:
            return new Vector3(xCoord - 1.5D, yCoord, zCoord + 0.5D);
        case 1:
            return new Vector3(xCoord + 0.5,  yCoord, zCoord + 2.5D);
        case 3:
            return new Vector3(xCoord + 2.5D, yCoord, zCoord + 0.5D);
        }
        return null;
    }

    public float getExitRotation() {
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
        double xCoord = this.pos.getX();
        double yCoord = this.pos.getY();
        double zCoord = this.pos.getZ();
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
                new AxisAlignedBB(
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
        if(docked) {
            updateAvailabilityInWorldData();
        }
    }

    @Override
    public void onChunkUnload() {
        // update this one last time
        //ShuttleDockHandler.setStoredAvailability(this, isAvailable());
    }

    @Override
    public void update() {
        super.update();
        if(!this.worldObj.isRemote) {
            if(actionCooldown > 0) {
                actionCooldown--;
            }
            if(this.dockedEntity != null) {

                EntityShuttle shuttle = ((EntityShuttle)this.dockedEntity);
                // before we do anything else
                if(shuttle.isDead) {
                    this.dockedEntity = null;
                } else {

                    if(shuttle.launchPhase == EnumLaunchPhase.LAUNCHED.ordinal()) {
                        // undock
                        shuttle.setPad(null);
                        this.dockedEntity = null;
                        updateAvailabilityInWorldData();
                    } else {
                        // from time to time, reposition?
                        if(this.ticks % 40 == 0) {
                            repositionEntity();
                        }
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
                this.worldObj.markBlockForUpdate(this.getPos());
            } else if(dockedEntity != null && !hasShuttleDocked) {
                hasShuttleDocked = true;
                this.worldObj.markBlockForUpdate(this.getPos());
            }

            // from time to time, update the dockdata
            if(this.ticks % 35 == 0) {
                updateAvailabilityInWorldData();
            }
        }
    }

    protected void updateAvailabilityInWorldData() {
        boolean curAvailability = this.isAvailable();
        boolean wasAvailableLastCheck = ShuttleDockHandler.getStoredAvailability(this);
        if(wasAvailableLastCheck != curAvailability) {
            ShuttleDockHandler.setStoredAvailability(this, curAvailability);
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

    @Deprecated
    protected void checkTileAt(HashSet<ILandingPadAttachable> connectedTiles, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        checkTileAt(connectedTiles, pos);
    }

    protected void checkTileAt(HashSet<ILandingPadAttachable> connectedTiles, BlockPos pos) {
        final TileEntity tile = this.worldObj.getTileEntity(pos);

        if (tile != null && tile instanceof ILandingPadAttachable && ((ILandingPadAttachable) tile).canAttachToLandingPad(this.worldObj, this.getPos()))
        {
            connectedTiles.add((ILandingPadAttachable) tile);
        }
    }

    @Override
    public HashSet<ILandingPadAttachable> getConnectedTiles() {
        HashSet<ILandingPadAttachable> connectedTiles = new HashSet<>();

        BlockPos pos = this.getPos();

        // check the blocks in a doorframe form around me
        // below
        checkTileAt(connectedTiles, pos.add(0, -1, 0));

        // above
        checkTileAt(connectedTiles, pos.add(0, +2, 0));

        // sides
        switch (this.getRotationMeta())
        {
        case 0: // -> +Z (the side which is towards the player)
        case 2: // -> -Z
            checkTileAt(connectedTiles, pos.add(-1, 0, 0));
            checkTileAt(connectedTiles, pos.add( 1, 0, 0));
            checkTileAt(connectedTiles, pos.add(-1, 1, 0));
            checkTileAt(connectedTiles, pos.add( 1, 1, 0));
            break;
        case 1: // -> -X
        case 3: // -> +X
            checkTileAt(connectedTiles, pos.add( 0, 0, -1));
            checkTileAt(connectedTiles, pos.add( 0, 0,  1));
            checkTileAt(connectedTiles, pos.add( 0, 1, -1));
            checkTileAt(connectedTiles, pos.add( 0, 1,  1));
            break;
        }
        // maybe do the edges, too?

        return connectedTiles;
    }

    @Override
    public boolean isBlockAttachable(IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        // maybe prevent launch controllers from working here?
        if (tile != null && tile instanceof ILandingPadAttachable)
        {
            return ((ILandingPadAttachable) tile).canAttachToLandingPad(world, this.getPos());
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
        updateAvailabilityInWorldData();
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

        int xCoord = this.pos.getX();
        int yCoord = this.pos.getY();
        int zCoord = this.pos.getZ();
        switch(this.getRotationMeta()) {
        case 0:
            return new AxisAlignedBB(xCoord, yCoord, zCoord-1, xCoord+1, yCoord + 2, zCoord+1);
        case 1:
            return new AxisAlignedBB(xCoord, yCoord, zCoord, xCoord+1, yCoord + 2, zCoord+2);
        case 2:
            return new AxisAlignedBB(xCoord-1, yCoord, zCoord, xCoord+1, yCoord + 2, zCoord+1);
        case 3:
            return new AxisAlignedBB(xCoord, yCoord, zCoord, xCoord+2, yCoord + 2, zCoord+1);
        }
        return new AxisAlignedBB(xCoord, yCoord, zCoord, xCoord+1, yCoord + 2, zCoord+1);
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
        entityPlayer.openGui(AmunRa.instance, GuiIds.GUI_SHUTTLE_DOCK, this.worldObj, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public void onCreate(World world, BlockPos placedPosition) {
        ShuttleDockHandler.addDock(this);

        int buildHeight = this.worldObj.getHeight() - 1;

        if (placedPosition.getY() + 1 > buildHeight) return;

        ARBlocks.metaBlockFake.makeFakeBlock(worldObj, placedPosition, this.getPos(), ARBlocks.fakeBlockSealable);
    }

    @Override
    public void onDestroy(TileEntity callingBlock) {
        ShuttleDockHandler.removeDock(this);

        BlockPos upPos = this.getPos().up();
        IBlockState upState = this.worldObj.getBlockState(upPos);



        Block b = upState.getBlock();//this.worldObj.getBlock(xCoord, yCoord+1, zCoord);
        if(b == ARBlocks.metaBlockFake) {
            this.worldObj.setBlockToAir(upPos);
        }
        if(callingBlock != this) {
            this.worldObj.destroyBlock(getPos(), true);

        }
    }

    @Override
    public String getName() {
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
    public void setInventorySlotContents(int slotNr, ItemStack newStack)
    {
        this.containingItems[slotNr] = newStack;

        if (newStack != null && newStack.stackSize > this.getInventoryStackLimit())
        {
            newStack.stackSize = this.getInventoryStackLimit();
        }
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
    public boolean isUseableByPlayer(EntityPlayer player) {
        return (player.getDistanceSqToCenter(this.getPos()) <= 64.0D);

    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int slotNr, ItemStack item) {

        return slotNr == 0 && (item.getItem() instanceof ItemShuttle);
    }

    protected boolean areBlocksWithin(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        for(int x=minX; x<=maxX; x++) {
            for(int y=minY; y<=maxY; y++) {
                for(int z=minZ; z<=maxZ; z++) {
                    if(!this.worldObj.isAirBlock(new BlockPos(x, y, z))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isObstructed() {

        int xCoord = this.pos.getX();
        int yCoord = this.pos.getY();
        int zCoord = this.pos.getZ();

        int minY = yCoord - 2;
        int maxY = yCoord + 3;
        int minX;
        int maxX;
        int minZ;
        int maxZ;
        // check

        switch (this.getRotationMeta())
        {
        case 0:
            minX = xCoord - 1;
            maxX = xCoord + 1;
            minZ = zCoord - 3;
            maxZ = zCoord - 1;
            break;
        case 2:
            minX = xCoord - 3;
            maxX = xCoord - 1;
            minZ = zCoord - 1;
            maxZ = zCoord + 1;
            break;
        case 1:
            minX = xCoord - 1;
            maxX = xCoord + 1;
            minZ = zCoord + 1;
            maxZ = zCoord + 3;
            break;
        case 3:
            minX = xCoord + 1;
            maxX = xCoord + 3;
            minZ = zCoord - 1;
            maxZ = zCoord + 1;
            break;
        default:
            return false;
        }


        return areBlocksWithin(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public boolean isAvailable() {
        if(this.dockedEntity != null || hasShuttleDocked) { // the former isn't that reliable, since dockedEntity won't be set until it has been rediscovered in the update
            return false;
        }
        return !isObstructed();
    }
    @Override
    public IChatComponent getDisplayName() {
        // apparently null is okay
        return null;
    }

    @Override
    public ItemStack removeStackFromSlot(int par1) {
        ItemStack containingItems[] = this.getContainingItems();
        if (containingItems[par1] != null)
        {
            final ItemStack var2 = containingItems[par1];
            containingItems[par1] = null;
            this.markDirty();
            return var2;
        }
        else
        {
            return null;
        }
    }


    @Override
    public void getPositions(BlockPos placedPosition, List<BlockPos> positions) {
        // TODO replace multiblock stuff
    }

    @Override
    public EnumBlockMultiType getMultiType() {
        // TODO replace multiblock stuff
        return null;
    }

    @Override
    protected ItemStack[] getContainingItems() {
        return containingItems;
    }
}
