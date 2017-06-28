package de.katzenpapst.amunra.tile;

import java.util.Iterator;
import java.util.List;

import de.katzenpapst.amunra.block.BlockARChest;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;

public class TileEntityARChest extends TileEntity implements IInventoryDefaultsAdvanced, ITickable {

    public TileEntityARChest() {
    }

    protected BlockMetaPair chestType;

    protected ItemStack[] chestContents = new ItemStack[36];

    /**
     * Determines if the check for adjacent chests has taken place.
     */
    public boolean adjacentChestChecked = false;

    /**
     * Contains the chest tile located adjacent to this one (if any)
     */
    public TileEntityARChest adjacentChestZNeg;

    /**
     * Contains the chest tile located adjacent to this one (if any)
     */
    public TileEntityARChest adjacentChestXPos;

    /**
     * Contains the chest tile located adjacent to this one (if any)
     */
    public TileEntityARChest adjacentChestXNeg;

    /**
     * Contains the chest tile located adjacent to this one (if any)
     */
    public TileEntityARChest adjacentChestZPos;

    /**
     * The current angle of the lid (between 0 and 1)
     */
    public float lidAngle;

    /**
     * The angle of the lid last tick
     */
    public float prevLidAngle;

    /**
     * The number of players currently using this chest
     */
    public int numUsingPlayers;

    /**
     * Server sync counter (once per 20 ticks)
     */
    private int ticksSinceSync;

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getSizeInventory()
    {
        return 27;
    }

    /**
     * Reads a tile entity from NBT.
     */
    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);

        final NBTTagList nbttaglist = nbt.getTagList("Items", 10);
        this.chestContents = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            final NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            final int j = nbttagcompound1.getByte("Slot") & 255;

            if (j < this.chestContents.length)
            {
                this.chestContents[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            }
        }
    }

    /**
     * Writes a tile entity to NBT.
     */
    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);

        final NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.chestContents.length; ++i)
        {
            if (this.chestContents[i] != null)
            {
                final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte) i);
                this.chestContents[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }

        nbt.setTag("Items", nbttaglist);
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes
     * with Container
     */
    @Override
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
    {
        return this.worldObj.getTileEntity(this.getPos()) == this
                && par1EntityPlayer.getDistanceSqToCenter(this.getPos()) <= 64.0D;
    }

    /**
     * Causes the TileEntity to reset all it's cached values for it's container
     * block, blockID, metaData and in the case of chests, the adjcacent chest
     * check
     */
    @Override
    public void updateContainingBlockInfo()
    {
        super.updateContainingBlockInfo();
        this.adjacentChestChecked = false;
    }

    private void resetAdjacentChestOrSomething(TileEntityARChest par1TileEntityChest, int direction)
    {
        if (par1TileEntityChest.isInvalid())
        {
            this.adjacentChestChecked = false;
        }
        else if (this.adjacentChestChecked)
        {
            switch (direction)
            {
            case 0:
                if (this.adjacentChestZPos != par1TileEntityChest)
                {
                    this.adjacentChestChecked = false;
                }

                break;
            case 1:
                if (this.adjacentChestXNeg != par1TileEntityChest)
                {
                    this.adjacentChestChecked = false;
                }

                break;
            case 2:
                if (this.adjacentChestZNeg != par1TileEntityChest)
                {
                    this.adjacentChestChecked = false;
                }

                break;
            case 3:
                if (this.adjacentChestXPos != par1TileEntityChest)
                {
                    this.adjacentChestChecked = false;
                }
            }
        }
    }

    /**
     * Performs the check for adjacent chests to determine if this chest is
     * double or not.
     */
    public void checkForAdjacentChests()
    {
        //Block b = this.getBlockType();
        if(!canDoublechest()) {
            return;
        }
        if (!this.adjacentChestChecked)
        {
            this.adjacentChestChecked = true;
            this.adjacentChestZNeg = null;
            this.adjacentChestXPos = null;
            this.adjacentChestXNeg = null;
            this.adjacentChestZPos = null;

            if (this.isSameChestType(this.pos.add(-1, 0, 0)))
            {
                this.adjacentChestXNeg = (TileEntityARChest) this.worldObj.getTileEntity(this.pos.add(-1, 0, 0));
            }

            if (this.isSameChestType(this.pos.add(1, 0, 0)))
            {
                this.adjacentChestXPos = (TileEntityARChest) this.worldObj.getTileEntity(this.pos.add(1, 0, 0));
            }

            if (this.isSameChestType(this.pos.add(0, 0, -1)))
            {
                this.adjacentChestZNeg = (TileEntityARChest) this.worldObj.getTileEntity(this.pos.add(0, 0, -1));
            }

            if (this.isSameChestType(this.pos.add(0, 0, 1)))
            {
                this.adjacentChestZPos = (TileEntityARChest) this.worldObj.getTileEntity(this.pos.add(0, 0, 1));
            }

            if (this.adjacentChestZNeg != null)
            {
                this.adjacentChestZNeg.resetAdjacentChestOrSomething(this, 0);
            }

            if (this.adjacentChestZPos != null)
            {
                this.adjacentChestZPos.resetAdjacentChestOrSomething(this, 2);
            }

            if (this.adjacentChestXPos != null)
            {
                this.adjacentChestXPos.resetAdjacentChestOrSomething(this, 1);
            }

            if (this.adjacentChestXNeg != null)
            {
                this.adjacentChestXNeg.resetAdjacentChestOrSomething(this, 3);
            }
        }
    }

    protected boolean canDoublechest() {
        return true;
    }

    private boolean isSameChestType(BlockPos pos)
    {
        if(chestType == null) {
            IBlockState state = worldObj.getBlockState(pos);
            chestType = new BlockMetaPair(state.getBlock(), (byte)0);
        }
        final Block block = this.worldObj.getBlockState(pos).getBlock();

        return block != null && block == chestType.getBlock();
    }

    /**
     * Allows the entity to update its state. Overridden in most subclasses,
     * e.g. the mob spawner uses this to count ticks and creates a new spawn
     * inside its implementation.
     */
    @Override
    public void update()
    {
        this.checkForAdjacentChests();
        ++this.ticksSinceSync;
        float f;

        int xCoord = this.pos.getX();
        int yCoord = this.pos.getY();
        int zCoord = this.pos.getZ();

        if (!this.worldObj.isRemote && this.numUsingPlayers != 0 && (this.ticksSinceSync + xCoord + yCoord + zCoord) % 200 == 0)
        {
            this.numUsingPlayers = 0;
            f = 5.0F;
            final List<?> list = this.worldObj.getEntitiesWithinAABB(
                    EntityPlayer.class,
                    new AxisAlignedBB(
                            xCoord - f,
                            yCoord - f,
                            zCoord - f,
                            xCoord + 1 + f,
                            yCoord + 1 + f,
                            zCoord + 1 + f
                    )
            );
            final Iterator<?> iterator = list.iterator();

            while (iterator.hasNext())
            {
                final EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                if (entityplayer.openContainer instanceof ContainerChest)
                {
                    final IInventory iinventory = ((ContainerChest) entityplayer.openContainer).getLowerChestInventory();

                    if (iinventory == this || iinventory instanceof InventoryLargeChest && ((InventoryLargeChest) iinventory).isPartOfLargeChest(this))
                    {
                        ++this.numUsingPlayers;
                    }
                }
            }
        }

        this.prevLidAngle = this.lidAngle;
        f = 0.05F;
        double d0;

        if (this.numUsingPlayers > 0 && this.lidAngle == 0.0F && this.adjacentChestZNeg == null && this.adjacentChestXNeg == null)
        {
            double d1 = xCoord + 0.5D;
            d0 = zCoord + 0.5D;

            if (this.adjacentChestZPos != null)
            {
                d0 += 0.5D;
            }

            if (this.adjacentChestXPos != null)
            {
                d1 += 0.5D;
            }

            this.worldObj.playSoundEffect(d1, yCoord + 0.5D, d0, "random.chestopen", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.6F);
        }

        if (this.numUsingPlayers == 0 && this.lidAngle > 0.0F || this.numUsingPlayers > 0 && this.lidAngle < 1.0F)
        {
            final float f1 = this.lidAngle;

            if (this.numUsingPlayers > 0)
            {
                this.lidAngle += f;
            }
            else
            {
                this.lidAngle -= f;
            }

            if (this.lidAngle > 1.0F)
            {
                this.lidAngle = 1.0F;
            }

            final float f2 = 0.5F;

            if (this.lidAngle < f2 && f1 >= f2 && this.adjacentChestZNeg == null && this.adjacentChestXNeg == null)
            {
                d0 = xCoord + 0.5D;
                double d2 = zCoord + 0.5D;

                if (this.adjacentChestZPos != null)
                {
                    d2 += 0.5D;
                }

                if (this.adjacentChestXPos != null)
                {
                    d0 += 0.5D;
                }

                this.worldObj.playSoundEffect(d0, yCoord + 0.5D, d2, "random.chestclosed", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.6F);
            }

            if (this.lidAngle < 0.0F)
            {
                this.lidAngle = 0.0F;
            }
        }
    }

    /**
     * Called when a client event is received with the event number and
     * argument, see World.sendClientEvent
     */
    @Override
    public boolean receiveClientEvent(int par1, int par2)
    {
        if (par1 == 1)
        {
            this.numUsingPlayers = par2;
            return true;
        }
        else
        {
            return super.receiveClientEvent(par1, par2);
        }
    }

    @Override
    public void openInventory(EntityPlayer player)
    {
        if (this.numUsingPlayers < 0)
        {
            this.numUsingPlayers = 0;
        }

        ++this.numUsingPlayers;
        this.worldObj.addBlockEvent(this.pos, this.getBlockType(), 1, this.numUsingPlayers);

        this.worldObj.notifyNeighborsOfStateChange(pos, this.getBlockType());
        this.worldObj.notifyNeighborsOfStateChange(pos.down(), this.getBlockType());
    }

    @Override
    public void closeInventory(EntityPlayer player)
    {
        if (this.getBlockType() != null && this.getBlockType() instanceof BlockARChest)
        {
            --this.numUsingPlayers;
            this.worldObj.addBlockEvent(pos, this.getBlockType(), 1, this.numUsingPlayers);
            this.worldObj.notifyNeighborsOfStateChange(pos, this.getBlockType());
            this.worldObj.notifyNeighborsOfStateChange(pos.down(), this.getBlockType());
        }
    }

    @Override
    public boolean hasCustomName()
    {
        return true;
    }

    /**
     * invalidates a tile entity
     */
    @Override
    public void invalidate()
    {
        super.invalidate();
        this.updateContainingBlockInfo();
        this.checkForAdjacentChests();
    }

    @Override
    public String getName()
    {
        return this.getBlockType().getLocalizedName();
    }

    @Override
    public boolean isItemValidForSlot(int par1, ItemStack par2ItemStack)
    {
        return true;
    }

    @SuppressWarnings("rawtypes")
    public static boolean isOcelotBlockingChest(World par0World, int par1, int par2, int par3)
    {
        final Iterator var4 = par0World.getEntitiesWithinAABB(EntityOcelot.class, new AxisAlignedBB(par1, par2 + 1, par3, par1 + 1, par2 + 2, par3 + 1)).iterator();
        EntityOcelot var6;

        do
        {
            if (!var4.hasNext())
            {
                return false;
            }

            final EntityOcelot var5 = (EntityOcelot) var4.next();
            var6 = var5;
        }
        while (!var6.isSitting());

        return true;
    }

    @Override
    public ItemStack[] getContainingItems() {
        return chestContents;
    }

}
