package de.katzenpapst.amunra.tile;

import java.util.HashSet;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.entity.spaceship.EntityShuttle;
import de.katzenpapst.amunra.vec.Vector3int;
import de.katzenpapst.amunra.world.ShuttleDockHandler;
import micdoodle8.mods.galacticraft.api.entity.ICargoEntity;
import micdoodle8.mods.galacticraft.api.entity.IDockable;
import micdoodle8.mods.galacticraft.api.entity.IFuelable;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase.EnumLaunchPhase;
import micdoodle8.mods.galacticraft.api.entity.ICargoEntity.EnumCargoLoadingState;
import micdoodle8.mods.galacticraft.api.entity.ICargoEntity.RemovalResult;
import micdoodle8.mods.galacticraft.api.tile.IFuelDock;
import micdoodle8.mods.galacticraft.api.tile.ILandingPadAttachable;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.core.blocks.BlockMulti;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import micdoodle8.mods.galacticraft.core.tile.IMultiBlock;
import micdoodle8.mods.galacticraft.core.tile.TileEntityAdvanced;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.FluidStack;

public class TileEntityShuttleDock extends TileEntityAdvanced implements IFuelable, IFuelDock, ICargoEntity, IMultiBlock {

    protected ItemStack[] containingItems;
    protected IDockable dockedEntity;


    public TileEntityShuttleDock() {
        containingItems = new ItemStack[1]; // one item
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.containingItems = this.readStandardItemsFromNBT(nbt);
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
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        this.writeStandardItemsToNBT(nbt);
    }

    public Vector3 getShuttlePosition() {
        switch (this.getRotationMeta())
        {
        case 0: // -> +Z (the side which is towards the player)
            return new Vector3(xCoord, yCoord, zCoord - 2.0D);
        case 2: // -> -Z
            return new Vector3(xCoord, yCoord, zCoord + 2.0D);
        case 1: // -> -X
            return new Vector3(xCoord + 2.0D, yCoord, zCoord);
        case 3: // -> +X
            return new Vector3(xCoord - 2.0D, yCoord, zCoord);
        }
        return null;
    }

    protected void repositionEntity() {
        Vector3 pos = getShuttlePosition();

        ((Entity)this.dockedEntity).setPosition(pos.x, pos.y, pos.z);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();


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

        /*if (!this.worldObj.isRemote)
        {
            final List<?> list = this.worldObj.getEntitiesWithinAABB(IFuelable.class, AxisAlignedBB.getBoundingBox(this.xCoord - 1.5D, this.yCoord - 2.0, this.zCoord - 1.5D, this.xCoord + 1.5D, this.yCoord + 4.0, this.zCoord + 1.5D));

            boolean changed = false;

            for (final Object o : list)
            {
                if (o != null && o instanceof IDockable && !this.worldObj.isRemote)
                {
                    final IDockable fuelable = (IDockable) o;

                    if (fuelable.isDockValid(this))
                    {
                        this.dockedEntity = fuelable;

                        this.dockedEntity.setPad(this);

                        changed = true;
                    }
                }
            }

            if (!changed)
            {
                if (this.dockedEntity != null)
                {
                    this.dockedEntity.setPad(null);
                }

                this.dockedEntity = null;
            }
        }*/
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
        if(entity instanceof EntityShuttle) {
            this.dockedEntity = entity;
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
        return 12.0D;
    }

    @Override
    public int getPacketCooldown()
    {
        return 3;
    }

    @Override
    public boolean isNetworkedTile() {
        return true;
    }

    @Override
    public boolean onActivated(EntityPlayer entityPlayer) {
        // nothing yet
        return false;
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

}
