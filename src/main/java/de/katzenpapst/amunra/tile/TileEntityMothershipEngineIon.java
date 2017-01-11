package de.katzenpapst.amunra.tile;

import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.item.MothershipFuel;
import de.katzenpapst.amunra.item.MothershipFuelRequirements;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
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
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class TileEntityMothershipEngineIon extends TileEntityMothershipEngineAbstract {



    public TileEntityMothershipEngineIon() {
        this.boosterBlock = ARBlocks.blockMsEngineIonBooster;
        this.containingItems = new ItemStack[2];
        this.fuel = AsteroidsModule.fluidLiquidNitrogen;
        //AsteroidsItems.canisterLN2
        this.fuelType = new MothershipFuel(ARBlocks.getBlockItemDamagePair(GCBlocks.crudeOil, 0), "B");

        // AsteroidsModule
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
        // TODO fix
        return new int[] { 0, 1 };

    }

    @Override
    public String getInventoryName() {
        return GCCoreUtil.translate("tile.mothershipEngineRocket.name");
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
            // TODO do battery here
            break;
        }
        return false;

        // return (slotID == 0 && itemstack != null && itemstack.getItem() == GCItems.fuelCanister);
    }

    @Override
    public int getFuelUsagePerAU() {
        return 1;
    }

    @Override
    public boolean shouldUseEnergy() {
        return true;
    }

    @Override
    public double getSpeed() {
        return 2.0D * this.getNumBoosters();
    }

    @Override
    public boolean canTravelDistance(double distance) {
        // TODO add power
        return super.canTravelDistance(distance);
    }

    @Override
    public MothershipFuelRequirements getFuelRequirements(double distance) {
        int totalFuelNeed = (int) Math.ceil(this.getFuelUsagePerAU() * distance);

        MothershipFuelRequirements result = new MothershipFuelRequirements();

        result.add(fuelType, totalFuelNeed);

        // TODO add power

        return result;
    }

    @Override
    protected int getTankCapacity() {
        return 10000 * this.numBoosters;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {

        // ARItems
        // GCItems
        if(AsteroidsModule.fluidLiquidNitrogen != fluid) {
            // other stuff?
            if(!FluidRegistry.getFluidName(fluid).equals("liquidNitrogen")) {
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
    public ForgeDirection getElectricInputDirection() {
        // TODO FIX
        return null;
    }

    @Override
    public ItemStack getBatteryInSlot() {
        // TODO fix
        return null;
    }

    @Override
    public boolean canExtractItem(int slotID, ItemStack itemstack, int side) {
        return slotID == 0 || slotID == 1;
    }


}
