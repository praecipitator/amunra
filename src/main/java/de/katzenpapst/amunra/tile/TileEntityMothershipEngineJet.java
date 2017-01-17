package de.katzenpapst.amunra.tile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.client.sound.ISoundableTile;
import de.katzenpapst.amunra.mob.DamageSourceAR;
import de.katzenpapst.amunra.mothership.fueldisplay.MothershipFuelDisplay;
import de.katzenpapst.amunra.mothership.fueldisplay.MothershipFuelDisplayFluid;
import de.katzenpapst.amunra.mothership.fueldisplay.MothershipFuelRequirements;
import de.katzenpapst.amunra.proxy.ARSidedProxy;
import de.katzenpapst.amunra.proxy.ARSidedProxy.ParticleType;
import de.katzenpapst.amunra.vec.Vector3int;
import de.katzenpapst.amunra.world.CoordHelper;
import micdoodle8.mods.galacticraft.api.entity.IFuelable;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.api.tile.IDisableableMachine;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlock;
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlockWithInventory;
import micdoodle8.mods.galacticraft.core.items.GCItems;
import micdoodle8.mods.galacticraft.core.items.ItemCanisterGeneric;
import micdoodle8.mods.galacticraft.core.network.IPacketReceiver;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import micdoodle8.mods.galacticraft.core.tile.TileEntityMulti;
import micdoodle8.mods.galacticraft.core.util.FluidUtil;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.planets.asteroids.AsteroidsModule;
import micdoodle8.mods.miccore.Annotations.NetworkedField;
import net.java.games.input.Component.Identifier.Axis;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
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
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;

/**
 * This is supposed to be used for any jet blocks
 * @author katzenpapst
 *
 */
public class TileEntityMothershipEngineJet extends TileEntityMothershipEngineAbstract {


    //public static final int MAX_LENGTH = 10;
  //  protected PositionedSoundRecord leSound;


//    protected final MothershipFuel fuelType;
    protected MothershipFuelDisplay fuelType = null;

    public TileEntityMothershipEngineJet() {
        this.boosterBlock = ARBlocks.blockMsEngineRocketBooster;
        this.containingItems = new ItemStack[1];

        this.fuel = GalacticraftCore.fluidFuel;
        fuelType = new MothershipFuelDisplayFluid(this.fuel);
    }

    @Override
    public boolean shouldUseEnergy() {
        return false;
    }

    @Override
    public void beginTransit(long duration) {

        MothershipFuelRequirements reqs = this.getFuelRequirements(duration);

        int fuelReq = reqs.get(fuelType);

        this.fuelTank.drain(fuelReq, true);

        super.beginTransit(duration);

    }



    @Override
    protected boolean isItemFuel(ItemStack itemstack) {

        FluidStack containedFluid = null;
        if(itemstack.getItem() instanceof IFluidContainerItem) {
            containedFluid = ((IFluidContainerItem)itemstack.getItem()).getFluid(itemstack);
        }
        if(containedFluid == null) {
            containedFluid = FluidContainerRegistry.getFluidForFilledItem(itemstack);
        }
        if(containedFluid != null) {
            if(containedFluid.getFluid() == fuel) {
                return true;
            }
            return FluidUtil.testFuel(FluidRegistry.getFluidName(containedFluid));
        }

        return false;
    }

    /**
     * Calculates tank capacity based on the boosters
     * @return
     */
    @Override
    protected int getTankCapacity() {
        return 10000 * this.numBoosters;
    }


    @Override
    protected void startSound() {
        super.startSound();
        AmunRa.proxy.playTileEntitySound(this, new ResourceLocation(GalacticraftCore.TEXTURE_PREFIX + "shuttle.shuttle"));
    }

    @Override
    protected void spawnParticles() {

        Vector3 particleStart = getExhaustPosition(1);
        Vector3 particleDirection = getExhaustDirection().scale(5);

        AmunRa.proxy.spawnParticles(ParticleType.PT_MOTHERSHIP_JET_FLAME, this.worldObj, particleStart, particleDirection);
        AmunRa.proxy.spawnParticles(ParticleType.PT_MOTHERSHIP_JET_FLAME, this.worldObj, particleStart, particleDirection);
        AmunRa.proxy.spawnParticles(ParticleType.PT_MOTHERSHIP_JET_FLAME, this.worldObj, particleStart, particleDirection);
        AmunRa.proxy.spawnParticles(ParticleType.PT_MOTHERSHIP_JET_FLAME, this.worldObj, particleStart, particleDirection);

    }



    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {

        // here, fluid is fuel
        if(!FluidUtil.testFuel(FluidRegistry.getFluidName(fluid))) {
            return false;
        }

        return super.canFill(from, fluid);
    }


    @Override
    public String getInventoryName() {
        return GCCoreUtil.translate("tile.mothershipEngineRocket.name");
    }

    @Override
    public boolean isItemValidForSlot(int slotID, ItemStack itemstack) {
        if(slotID == 0 && itemstack != null) {
            return this.isItemFuel(itemstack);
        }
        /*FluidStack containedFluid = FluidContainerRegistry.getFluidForFilledItem(itemstack);
        if(containedFluid.getFluid() == fuel) {
            return true;
        }*/
        return false;
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
    public double getThrust() {
        return this.getNumBoosters() * 2000000.0D;
    }


    /**
     * This should return how much fuel units are consumed per AU travelled, in millibuckets
     * @return
     */
    public float getFuelUsagePerTick() {
        return 2.0F;
    }

    @Override
    public MothershipFuelRequirements getFuelRequirements(long duration) {
        int totalFuelNeed = (int) Math.ceil(this.getFuelUsagePerTick() * duration); // always consume half a bucket

        MothershipFuelRequirements result = new MothershipFuelRequirements();

        result.add(fuelType, totalFuelNeed);

        return result;
    }

    @Override
    public boolean canRunForDuration(long duration) {
        MothershipFuelRequirements reqs = getFuelRequirements(duration);

        return reqs.get(fuelType) <= fuelTank.getFluidAmount();
    }


}
