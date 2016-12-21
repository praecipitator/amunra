package de.katzenpapst.amunra.tile;

import org.lwjgl.util.vector.Vector3f;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.client.sound.PositionedLoopedSound;
import de.katzenpapst.amunra.proxy.ARSidedProxy;
import de.katzenpapst.amunra.proxy.ARSidedProxy.ParticleType;
import de.katzenpapst.amunra.world.CoordHelper;
import micdoodle8.mods.galacticraft.api.entity.IFuelable;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlock;
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlockWithInventory;
import micdoodle8.mods.galacticraft.core.items.GCItems;
import micdoodle8.mods.galacticraft.core.items.ItemCanisterGeneric;
import micdoodle8.mods.galacticraft.core.tile.TileEntityMulti;
import micdoodle8.mods.galacticraft.core.util.FluidUtil;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.miccore.Annotations.NetworkedField;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

/**
 * This is supposed to be used for any jet blocks
 * @author katzenpapst
 *
 */
public class TileEntityMothershipEngineJet extends TileBaseElectricBlockWithInventory implements IFluidHandler, ISidedInventory, IInventory {

    protected int numBoosters = 0;
    protected final int tankCapacity = 12000;
    // whenever this one needs to update itself
    protected boolean needsUpdate = true;

    protected boolean loadedFuelLastTick = false;

    protected boolean isInUseForTransit = false;

    protected boolean soundStarted = false;

    @NetworkedField(targetSide = Side.CLIENT)
    public FluidTank fuelTank = new FluidTank(this.tankCapacity);
    protected ItemStack[] containingItems = new ItemStack[1];
    public static final int MAX_LENGTH = 10;
    protected BlockMetaPair boosterBlock;
    protected PositionedSoundRecord leSound;

    public TileEntityMothershipEngineJet() {
        this.boosterBlock = ARBlocks.blockMsEngineRocketBooster;

        initSound();
    }



    public int getScaledFuelLevel(int i)
    {
        final double fuelLevel = this.fuelTank.getFluid() == null ? 0 : this.fuelTank.getFluid().amount;

        return (int) (fuelLevel * i / this.fuelTank.getCapacity());
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.containingItems = this.readStandardItemsFromNBT(nbt);
        if(nbt.hasKey("numBoosters")) {
            numBoosters = nbt.getInteger("numBoosters");
            // System.out.println("Got data, numBoosters = "+numBoosters);
        }

        this.fuelTank.setCapacity(getTankCapacity());

        if (nbt.hasKey("fuelTank"))
        {
            this.fuelTank.readFromNBT(nbt.getCompoundTag("fuelTank"));
        }

        if(nbt.hasKey("needsUpdate")) {
            needsUpdate = nbt.getBoolean("needsUpdate");
        }
        if(nbt.hasKey("usedForTransit")) {
            isInUseForTransit = nbt.getBoolean("usedForTransit");
        }
    }


    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        this.writeStandardItemsToNBT(nbt);
        if (this.fuelTank.getFluid() != null)
        {
            nbt.setTag("fuelTank", this.fuelTank.writeToNBT(new NBTTagCompound()));
        }
        nbt.setInteger("numBoosters", numBoosters);
        System.out.println("Wrote data, numBoosters = "+numBoosters);
        nbt.setBoolean("needsUpdate", needsUpdate);
        nbt.setBoolean("usedForTransit", isInUseForTransit);
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


    @Override
    public boolean shouldUseEnergy() {
        return false;
    }

    @Override
    public ForgeDirection getElectricInputDirection() {
        return null;
    }

    @Override
    public ItemStack getBatteryInSlot() {
        return null;
    }

    /**
     * Calculates tank capacity based on the boosters
     * @return
     */
    protected int getTankCapacity() {
        return 5000 * this.numBoosters;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        int used = 0;



        if (canFill(from, resource.getFluid()))
        {
            if (FluidUtil.testFuel(FluidRegistry.getFluidName(resource)))
            {
                used = this.fuelTank.fill(resource, doFill);
            }
        }

        return used;
    }

    public Vector3f getCenterPosition() {
        return new Vector3f(xCoord+0.5F, yCoord+0.5F, zCoord+0.5F);
    }

    public Vector3 getExhaustDirection() {
        /*
        * -Z => 0
        * +Z => 2
         * -X => 3
         * +X => 1
         * */
        switch(this.getRotationMeta()) {
        case 0: return new Vector3(0,0,-1);
        case 1: return new Vector3(1,0,0);
        case 2: return new Vector3(0,0,1);
        case 3: return new Vector3(-1,0,0);
        }
        return new Vector3(0,0,0);
    }

    public Vector3 getExhaustPosition() {
        double random1 = worldObj.rand.nextGaussian() * 0.10F;
        double random2 = worldObj.rand.nextGaussian() * 0.10F;
        double offset = 0.40D;
        Vector3 result = new Vector3(xCoord+0.5D, yCoord+0.5D, zCoord+0.5D);

        switch(this.getRotationMeta()) {
        case 0:
            result.x += random1;
            result.y += random2;
            result.z -= offset;
            break;
        case 1:
            result.x += offset;
            result.y += random1;
            result.z += random2;
            break;
        case 2:
            result.x += random1;
            result.y += random2;
            result.z += offset;
            break;
        case 3:
            result.x -= offset;
            result.y += random1;
            result.z += random2;
            break;
        }

        return result;
    }

    protected void initSound() {
        // I hope this works
        leSound = new PositionedLoopedSound(new ResourceLocation(GalacticraftCore.TEXTURE_PREFIX + "shuttle.shuttle"),
                10.0F, // volume
                1.0F, // WTF
                xCoord,
                yCoord,
                zCoord);
    }

    protected void startSound() {
        if(this.worldObj.isRemote) return;
        if(!Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(leSound)) {
            Minecraft.getMinecraft().getSoundHandler().playSound(leSound);
        }
    }

    protected void stopSound() {
        if(this.worldObj.isRemote) return;
        if(Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(leSound)) {

            Minecraft.getMinecraft().getSoundHandler().stopSound(leSound);
        }
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if(isInUseForTransit) {
            if(!soundStarted) {
                startSound();
                soundStarted = true;
            }
            Vector3 particleStart = getExhaustPosition();
            Vector3 particleDirection = getExhaustDirection().scale(5);

            AmunRa.proxy.spawnParticles(ParticleType.PT_MOTHERSHIP_JET_FLAME, this.worldObj, particleStart, particleDirection);
            AmunRa.proxy.spawnParticles(ParticleType.PT_MOTHERSHIP_JET_FLAME, this.worldObj, particleStart, particleDirection);
            AmunRa.proxy.spawnParticles(ParticleType.PT_MOTHERSHIP_JET_FLAME, this.worldObj, particleStart, particleDirection);
            AmunRa.proxy.spawnParticles(ParticleType.PT_MOTHERSHIP_JET_FLAME, this.worldObj, particleStart, particleDirection);
        } else {
            if(soundStarted) {
                stopSound();
                soundStarted = false;
            }
        }

        // try to do the particle shit
        //if(this.ticks % 5 == 0) {

        /*AmunRa.proxy.spawnParticles(ParticleType.PT_WTFTEST, this.worldObj, particleStart, particleDirection);
        AmunRa.proxy.spawnParticles(ParticleType.PT_WTFTEST, this.worldObj, particleStart, particleDirection);
        AmunRa.proxy.spawnParticles(ParticleType.PT_WTFTEST, this.worldObj, particleStart, particleDirection);*/
        //}

        if (!worldObj.isRemote) {

            // so, on an actual server-client setup, this actually happens on the server side
            //System.out.println("Updating on server? "+FMLCommonHandler.instance().getSide());
            if(needsUpdate) {
                this.updateMultiblock();
                needsUpdate = false;
            }

            // more stuff
            this.loadedFuelLastTick = false;

            if (this.containingItems[0] != null)
            {
                if (this.containingItems[0].getItem() instanceof ItemCanisterGeneric)
                {
                    if (this.containingItems[0].getItem() == GCItems.fuelCanister)
                    {
                        int originalDamage = this.containingItems[0].getItemDamage();
                        int used = this.fuelTank.fill(new FluidStack(GalacticraftCore.fluidFuel, ItemCanisterGeneric.EMPTY - originalDamage), true);
                        if (originalDamage + used == ItemCanisterGeneric.EMPTY)
                            this.containingItems[0] = new ItemStack(GCItems.oilCanister, 1, ItemCanisterGeneric.EMPTY);
                        else
                            this.containingItems[0] = new ItemStack(GCItems.fuelCanister, 1, originalDamage + used);
                    }
                }
                else
                {
                    final FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(this.containingItems[0]);

                    if (liquid != null)
                    {
                        boolean isFuel = FluidUtil.testFuel(FluidRegistry.getFluidName(liquid));

                        if (isFuel)
                        {
                            if (this.fuelTank.getFluid() == null || this.fuelTank.getFluid().amount + liquid.amount <= this.fuelTank.getCapacity())
                            {
                                this.fuelTank.fill(new FluidStack(GalacticraftCore.fluidFuel, liquid.amount), true);

                                if (FluidContainerRegistry.isBucket(this.containingItems[0]) && FluidContainerRegistry.isFilledContainer(this.containingItems[0]))
                                {
                                    final int amount = this.containingItems[0].stackSize;
                                    if (amount > 1) {
                                        this.fuelTank.fill(new FluidStack(GalacticraftCore.fluidFuel, (amount - 1) * FluidContainerRegistry.BUCKET_VOLUME), true);
                                    }
                                    this.containingItems[0] = new ItemStack(Items.bucket, amount);
                                }
                                else
                                {
                                    this.containingItems[0].stackSize--;

                                    if (this.containingItems[0].stackSize == 0)
                                    {
                                        this.containingItems[0] = null;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


    }

    @Override
    public boolean canUpdate()
    {
        // maybe return this.needsUpdate?
        return true;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        // can't drain
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        // can't drain
        return null;
    }

    public int getRotationMeta(int meta) {
        return (meta & 12) >> 2;
    }

    public int getRotationMeta() {
        return (this.getBlockMetadata() & 12) >> 2;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        // can fill from everywhere except back
        int metadata = getRotationMeta();

        if(CoordHelper.rotateForgeDirection(ForgeDirection.NORTH, metadata).equals(from)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        // can't drain
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        int metadata = getRotationMeta();
        if(CoordHelper.rotateForgeDirection(ForgeDirection.NORTH, metadata).equals(from)) {
            return null;
        }
        return new FluidTankInfo[] { new FluidTankInfo(this.fuelTank) };
    }

    @Override
    public String getInventoryName() {
        return GCCoreUtil.translate("tile.mothershipEngineRocket.name");
    }

    @Override
    public boolean hasCustomInventoryName() {
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int slotID, ItemStack itemstack) {

        return (slotID == 0 && itemstack != null && itemstack.getItem() == GCItems.fuelCanister);
    }

    @Override
    protected ItemStack[] getContainingItems() {
        return this.containingItems;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side)
    {
        return new int[] { 0 };
    }

    @Override
    public boolean canInsertItem(int slotID, ItemStack itemstack, int side)
    {
        return this.isItemValidForSlot(slotID, itemstack);
    }

    @Override
    public boolean canExtractItem(int slotID, ItemStack itemstack, int side) {
        return slotID == 0;
    }

    @Override
    public int getSizeInventory()
    {
        return this.containingItems.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.containingItems[slot];
    }

    @Override
    public ItemStack decrStackSize(int slotNr, int par2)
    {
        if (this.containingItems[slotNr] != null)
        {
            ItemStack var3;

            if (this.containingItems[slotNr].stackSize <= par2)
            {
                var3 = this.containingItems[slotNr];
                this.containingItems[slotNr] = null;
                return var3;
            }
            else
            {
                var3 = this.containingItems[slotNr].splitStack(par2);

                if (this.containingItems[slotNr].stackSize == 0)
                {
                    this.containingItems[slotNr] = null;
                }

                return var3;
            }
        }
        else
        {
            return null;
        }
    }
/*
    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {

        switch(this.getRotationMeta()) {
        case 0:
            return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord-1, xCoord + 1, yCoord + 1, zCoord + 1);
            // rotation = 0;// -> -Z
        case 1:
            return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 2);
            //rotation = 180.0F;// -> Z
        case 2:
            return AxisAlignedBB.getBoundingBox(xCoord-1, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
            //rotation = 90.0F;// -> -X
        case 3:
            return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 2, yCoord + 1, zCoord + 1);
            //rotation = 270.0F;// -> X
        }
        // I wonder if I should take rotation into account here
        return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
    }
    */

    @Override
    public ItemStack getStackInSlotOnClosing(int par1)
    {
        if (this.containingItems[par1] != null)
        {
            final ItemStack var2 = this.containingItems[par1];
            this.containingItems[par1] = null;
            return var2;
        }
        else
        {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
    {
        this.containingItems[par1] = par2ItemStack;

        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
        {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer) {
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

    public int getNumBoosters() {
        return numBoosters;
    }

    public BlockMetaPair getBoosterBlock() {
        return this.boosterBlock;
        // return ARBlocks.blockAluCrate; // FOR NOW
    }

    protected boolean attachBooster(int x, int y, int z) {
        BlockMetaPair booster   = this.getBoosterBlock();
        Block worldBlock        = this.worldObj.getBlock(x, y, z);
        int worldMeta           = this.worldObj.getBlockMetadata(x, y, z);
        TileEntity worldTile    = this.worldObj.getTileEntity(x, y, z);
/*
        boolean dbg1 = !booster.getBlock().equals(worldBlock);
        boolean dbg2 = booster.getMetadata() != worldMeta;
        boolean dbg3 = worldTile == null;
        boolean dbg4 = !(worldTile instanceof TileEntityMothershipEngineBooster);
        boolean dbg5 = ((TileEntityMothershipEngineBooster)worldTile).hasMaster();
*/
        if(
                !booster.getBlock().equals(worldBlock) || booster.getMetadata() != worldMeta ||
                worldTile == null || !(worldTile instanceof TileEntityMothershipEngineBooster) ||
                ((TileEntityMothershipEngineBooster)worldTile).hasMaster()
        ) {
            return false;
        }

        // actually attach
        ((TileEntityMothershipEngineBooster)worldTile).setMaster(this.xCoord, this.yCoord, this.zCoord);
        numBoosters++;

        return true;
    }

    protected boolean detachBooster(int x, int y, int z) {
        BlockMetaPair booster   = this.getBoosterBlock();
        Block worldBlock        = this.worldObj.getBlock(x, y, z);
        int worldMeta           = this.worldObj.getBlockMetadata(x, y, z);
        TileEntity worldTile    = this.worldObj.getTileEntity(x, y, z);

        if(
                !booster.getBlock().equals(worldBlock) || booster.getMetadata() != worldMeta ||
                worldTile == null || !(worldTile instanceof TileEntityMothershipEngineBooster) ||
                !((TileEntityMothershipEngineBooster)worldTile).isMaster(xCoord, yCoord, zCoord)
        ) {
            return false;
        }

        ((TileEntityMothershipEngineBooster)worldTile).clearMaster();

        return true;
    }

    /**
     * Check if the block at the given position is (or should be) within the current multiblock
     * @param x
     * @param y
     * @param z
     * @return
     */
    public boolean isPartOfMultiBlock(int x, int y, int z)
    {
        // for each axis, the other two coordinates should be the same
        // and the relevant one should be within numBoosters of my coordinate, in the right direction
        switch (this.getRotationMeta())
        {
        case 0:
            //rotation = 180.0F;// -> Z
            return (xCoord == x && yCoord == y && zCoord+numBoosters >= z && zCoord+1 <= z);
        case 1:
            //rotation = 90.0F;// -> -X
            return (zCoord == z && yCoord == y && xCoord-numBoosters <= x && xCoord-1 >= x);
        case 2:
            //rotation = 0;// -> -Z
            return (xCoord == x && yCoord == y && zCoord-numBoosters <= z && zCoord-1 >= z);
        case 3:
            //rotation = 270.0F;// -> X
            return (zCoord == z && yCoord == y && xCoord+numBoosters >= x && xCoord+1 <= x);
        }
        return false;
    }

    /**
     * Resets any boosters in the current direction
     */
    public void resetMultiblock() {
        System.out.println("Resetting Multiblock");
        switch (this.getRotationMeta())
        {
        case 0:
            //rotation = 180.0F;// -> Z
            for(int i=0;i<numBoosters;i++) {
                detachBooster(this.xCoord, this.yCoord, this.zCoord+i+1);
            }
            break;
        case 1:
            //rotation = 90.0F;// -> -X
            for(int i=0;i<numBoosters;i++) {
                detachBooster(this.xCoord-i-1, this.yCoord, this.zCoord);
            }
            break;
        case 2:
            //rotation = 0;// -> -Z
            for(int i=0;i<numBoosters;i++) {
                detachBooster(this.xCoord, this.yCoord, this.zCoord-i-1);
            }
            break;
        case 3:
            //rotation = 270.0F;// -> X
            for(int i=0;i<numBoosters;i++) {
                detachBooster(this.xCoord+i+1, this.yCoord, this.zCoord);
            }
            break;
        }
        numBoosters = 0;
        this.fuelTank.setCapacity(0);
    }

    /**
     * Tell the tile that it should update the multiblock structure
     */
    public void scheduleUpdate() {
        this.needsUpdate = true;
    }

    /**
     * Reset and create in one
     */
    public void updateMultiblock() {
        //fuelTank.getCapacity()
        resetMultiblock();
        createMultiblock();
        this.markDirty();
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    }

    /**
     * Checks for boosters in the current direction, if they don't have masters yet, add them to myself
     */
    public void createMultiblock() {
        System.out.println("Creating Multiblock");
        // this should check all the stuff
        numBoosters = 0;
        //this.worldObj.isRemote
        // happens on server only, I think
        switch (this.getRotationMeta())
        {
        case 0:
            //rotation = 180.0F;// -> Z
            for(int i=0;i<MAX_LENGTH;i++) {
                if(!attachBooster(this.xCoord, this.yCoord, this.zCoord+i+1)) {
                    break;
                }
            }
            break;
        case 1:
            //rotation = 90.0F;// -> -X
            for(int i=0;i<MAX_LENGTH;i++) {
                if(!attachBooster(this.xCoord-i-1, this.yCoord, this.zCoord)) {
                    break;
                }
            }
            break;
        case 2:
            //rotation = 0;// -> -Z
            for(int i=0;i<MAX_LENGTH;i++) {
                if(!attachBooster(this.xCoord, this.yCoord, this.zCoord-i-1)) {
                    break;
                }
            }
            break;
        case 3:
            //rotation = 270.0F;// -> X
            for(int i=0;i<MAX_LENGTH;i++) {
                if(!attachBooster(this.xCoord+i+1, this.yCoord, this.zCoord)) {
                    break;
                }
            }
            break;
        }
        this.fuelTank.setCapacity(this.getTankCapacity());
        if(fuelTank.getCapacity() < fuelTank.getFluidAmount()) {
            fuelTank.drain(fuelTank.getFluidAmount() - fuelTank.getCapacity(), true);
        }
        System.out.println("Created Multiblock with "+numBoosters);
    }

    /**
     * Should consume the fuel needed for the transition, on client side also start any animation or something alike.
     * This will be called for all engines which are actually being used
     */
    public void beginTransit(double distance) {
        this.isInUseForTransit = true;

        int totalFuelNeed = (int) Math.ceil(this.getFuelUsagePerAU() * distance);

        this.fuelTank.drain(totalFuelNeed, true);
        this.markDirty();
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    }

    public boolean canTravelDistance(double distance) {
        int totalFuelNeed = (int) Math.ceil(this.getFuelUsagePerAU() * distance);
        return totalFuelNeed <= fuelTank.getFluidAmount();
    }

    /**
     * This should return how much fuel units are consumed per AU travelled, in millibuckets
     * @return
     */
    public int getFuelUsagePerAU() {
        return 200;
    }


    /**
     * Will be called on all which return true from isInUse on transit end
     */
    public void endTransit() {
        this.isInUseForTransit = false;
        this.markDirty();
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    }

    /**
     * Should return whenever beginTransit has been called on this engine, and endTransit hasn't yet
     */
    public boolean isInUse() {
        return this.isInUseForTransit;
    }

}
