package de.katzenpapst.amunra.tile;

import java.util.EnumSet;

import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.mothership.fueldisplay.MothershipFuelDisplay;
import de.katzenpapst.amunra.mothership.fueldisplay.MothershipFuelDisplayEnergy;
import de.katzenpapst.amunra.mothership.fueldisplay.MothershipFuelDisplayFluid;
import de.katzenpapst.amunra.mothership.fueldisplay.MothershipFuelRequirements;
import de.katzenpapst.amunra.proxy.ARSidedProxy.ParticleType;
import de.katzenpapst.amunra.world.CoordHelper;
import micdoodle8.mods.galacticraft.api.transmission.NetworkType;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase;
import micdoodle8.mods.galacticraft.core.items.GCItems;
import micdoodle8.mods.galacticraft.core.util.FluidUtil;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.planets.asteroids.AsteroidsModule;
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.AsteroidBlocks;
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems;
import micdoodle8.mods.galacticraft.planets.mars.blocks.MarsBlocks;
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class TileEntityMothershipEngineIon extends TileEntityMothershipEngineAbstract {


    protected MothershipFuelDisplay fuelType = null;
    protected MothershipFuelDisplay fuelTypeEnergy = null;

    public TileEntityMothershipEngineIon() {
        this.boosterBlock = ARBlocks.blockMsEngineIonBooster;
        this.containingItems = new ItemStack[2];
        this.fuel = AsteroidsModule.fluidLiquidNitrogen;
        //AsteroidsItems.canisterLN2

        fuelType = new MothershipFuelDisplayFluid(this.fuel);
        fuelTypeEnergy = MothershipFuelDisplayEnergy.getInstance();

        // AsteroidsModule
    }

    @Override
    protected void startSound() {
        super.startSound();
        AmunRa.proxy.playTileEntitySound(this, new ResourceLocation(GalacticraftCore.TEXTURE_PREFIX + "entity.astrominer"));
    }

    @Override
    protected void spawnParticles() {

        Vector3 particleStart = getExhaustPosition(1.8);
        Vector3 particleDirection = getExhaustDirection().scale(5);

        AmunRa.proxy.spawnParticles(ParticleType.PT_MOTHERSHIP_ION_FLAME, this.worldObj, particleStart, particleDirection);
        AmunRa.proxy.spawnParticles(ParticleType.PT_MOTHERSHIP_ION_FLAME, this.worldObj, particleStart, particleDirection);
        AmunRa.proxy.spawnParticles(ParticleType.PT_MOTHERSHIP_ION_FLAME, this.worldObj, particleStart, particleDirection);
        AmunRa.proxy.spawnParticles(ParticleType.PT_MOTHERSHIP_ION_FLAME, this.worldObj, particleStart, particleDirection);

    }

    @Override
    public int[] getAccessibleSlotsFromSide(int slotnr) {
        // T ODO fix
        return new int[] { 0, 1 };

    }

    @Override
    public void beginTransit(long duration) {

        MothershipFuelRequirements reqs = this.getFuelRequirements(duration);

        int energyReq = reqs.get(fuelTypeEnergy);
        int fuelReq = reqs.get(fuelType);

        this.storage.extractEnergyGCnoMax(energyReq, false);
        this.fuelTank.drain(fuelReq, true);

        super.beginTransit(duration);
    }


    @Override
    public String getInventoryName() {
        return GCCoreUtil.translate("tile.mothershipEngineIon.name");
    }

    @Override
    public boolean isItemValidForSlot(int slotID, ItemStack itemstack) {
        if(itemstack == null) return false;

        switch(slotID) {
        case 0:
            FluidStack containedFluid = FluidContainerRegistry.getFluidForFilledItem(itemstack);
            if(containedFluid.getFluid() == fuel) {
                return true;
            }
            break;
        case 1:
            return ItemElectricBase.isElectricItem(itemstack.getItem());
        }
        return false;

        // return (slotID == 0 && itemstack != null && itemstack.getItem() == GCItems.fuelCanister);
    }

    public float getFuelUsagePerTick() {
        return 1.0F;
    }

    public float getEnergyUsagePerTick() {
        return 15.0F;
    }

    //public int getFuelUsageForDistance

    @Override
    public boolean shouldUseEnergy() {
        return false;//!this.getDisabled(0);
    }

    @Override
    public double getThrust() {
        return this.getNumBoosters() * 25000000.0D;
    }



    @Override
    protected int getTankCapacity() {
        return 2000 * this.numBoosters;
    }

    protected float getEnergyCapacity() {
        return STANDARD_CAPACITY * this.numBoosters;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {

        // ARItems
        // GCItems
        if(fuel != fluid) {
            // other stuff?
            if(!FluidRegistry.getFluidName(fluid).equals(fuel.getName())) {
                return false;
            }
        }

        return super.canFill(from, fluid);
    }

    @Override
    protected boolean isItemFuel(ItemStack itemstack) {
        FluidStack containedFluid = FluidContainerRegistry.getFluidForFilledItem(itemstack);
        if(containedFluid != null && containedFluid.getFluid() == fuel) {
            return true;
        }
        return false;
    }

    @Override
    public int getRotationMeta(int meta) {
        return (meta & 12) >> 2;
    }


    @Override
    public ForgeDirection getElectricInputDirection() {

        int metadata = getRotationMeta(this.getBlockMetadata());

        return CoordHelper.rotateForgeDirection(ForgeDirection.SOUTH, metadata);

    }

    @Override
    public EnumSet<ForgeDirection> getElectricalInputDirections() {
        //EnumSet.
        return EnumSet.allOf(ForgeDirection.class);
    }

    @Override
    public boolean canConnect(ForgeDirection direction, NetworkType type)
    {
        if (direction == null || direction.equals(ForgeDirection.UNKNOWN) || type != NetworkType.POWER)
        {
            return false;
        }

        return true;// for now direction == this.getElectricInputDirection();
    }

    @Override
    public ItemStack getBatteryInSlot() {
        return this.containingItems[1];
    }

    @Override
    public boolean canExtractItem(int slotID, ItemStack itemstack, int side) {
        return slotID == 0 || slotID == 1;
    }

    @Override
    public void slowDischarge()
    {
        // don't
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        float capacity = nbt.getFloat("energyCapacity");
        this.storage.setCapacity(capacity);
        super.readFromNBT(nbt);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setFloat("energyCapacity", this.storage.getCapacityGC());
    }

    @Override
    protected void createMultiblockInternal(boolean notifyClient) {
        super.createMultiblockInternal(notifyClient);

        this.storage.setCapacity(getEnergyCapacity());

    }

    @Override
    public MothershipFuelRequirements getFuelRequirements(long duration) {
        int totalFuelNeed = (int) Math.ceil(this.getFuelUsagePerTick() * duration);

        float totalEnergyNeed = (this.getEnergyUsagePerTick() * duration);

        MothershipFuelRequirements result = new MothershipFuelRequirements();

        result.add(fuelType, totalFuelNeed);

        result.add(fuelTypeEnergy, (int) totalEnergyNeed);

        return result;
    }

    @Override
    public boolean canRunForDuration(long duration) {
        MothershipFuelRequirements reqs = getFuelRequirements(duration);

        int fuelNeeded = reqs.get(fuelType);
        int powerNeeded = reqs.get(fuelTypeEnergy);

        return this.storage.getEnergyStoredGC() >= powerNeeded && fuelTank.getFluidAmount() > fuelNeeded;
    }

}
