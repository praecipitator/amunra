package de.katzenpapst.amunra.block.machine.mothershipEngine;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.GuiIds;
import de.katzenpapst.amunra.block.SubBlockMachine;
import de.katzenpapst.amunra.item.ItemDamagePair;
import de.katzenpapst.amunra.item.MothershipFuelRequirements;
import de.katzenpapst.amunra.tile.TileEntityMothershipEngineAbstract;
import de.katzenpapst.amunra.tile.TileEntityMothershipEngineJet;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class MothershipEngineJetBase extends SubBlockMachine implements IMothershipEngine {

    protected String iconTexture;

    public MothershipEngineJetBase(String name, String texture, String iconTexture) {
        super(name, texture);

        this.iconTexture = iconTexture;
    }

    @Override
    public String getItemIconName() {
        return iconTexture;
    }

    /**
     * Not sure why I have to do this here, but...
     */
    abstract protected ItemDamagePair getItem();



    @Override
    public int getDirection(World world, int x, int y, int z, int meta) {
        // not actually needed, my meta takes care of this
        return 0;
    }

    protected TileEntityMothershipEngineAbstract getMyTileEntity(World world, int x, int y, int z) {
        TileEntity t = world.getTileEntity(x, y, z);
        if(t == null || !(t instanceof TileEntityMothershipEngineAbstract)) {
            // TODO throw exception instead
            return null;
        }
        return (TileEntityMothershipEngineAbstract)t;
    }



    @Override
    public boolean isEnabled(World world, int x, int y, int z, int meta) {
        TileEntityMothershipEngineAbstract myTile = getMyTileEntity(world, x, y, z);
        return !myTile.getDisabled(0) && !myTile.isObstructed();
    }

    /**
     * Should consume the fuel needed for the transition, on client side also start any animation or something alike.
     * This will be called for all engines which are actually being used
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @param meta
     * @param distance
     */
    @Override
    public void beginTransit(World world, int x, int y, int z, int meta, double distance) {
        getMyTileEntity(world, x, y, z).beginTransit(distance);
    }


    /**
     * Will be called on all which return true from isInUse on transit end
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @param meta
     */
    @Override
    public void endTransit(World world, int x, int y, int z, int meta) {
        getMyTileEntity(world, x, y, z).endTransit();
    }

    /**
     * Should return whenever beginTransit has been called on this engine, and endTransit hasn't yet
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @param meta
     * @return
     */
    @Override
    public boolean isInUse(World world, int x, int y, int z, int meta) {
        return getMyTileEntity(world, x, y, z).isInUse();
    }

    @Override
    public MothershipFuelRequirements
            getFuelRequirements(World world, int x, int y, int z, int meta, double distance) {

        return getMyTileEntity(world, x, y, z).getFuelRequirements(distance);
    }

    @Override
    public void onBlockPlacedBy(World w, int x, int y, int z, EntityLivingBase user, ItemStack stack)
    {
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        TileEntity leTile = world.getTileEntity(x, y, z);
        if(leTile instanceof TileEntityMothershipEngineAbstract) {
            ((TileEntityMothershipEngineAbstract)leTile).scheduleUpdate();
            // world.markBlockForUpdate(x, y, z);
        }
    }

    /**
     * Called when a block is placed using its ItemBlock. Args: World, X, Y, Z, side, hitX, hitY, hitZ, block metadata
     *
     */
    @Override
    public int onBlockPlaced(World w, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta)
    {
        return meta;
    }

    @Override
    public boolean onUseWrench(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX,
            float hitY, float hitZ) {
        // TODO rotate the tile entity
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderType()
    {
        return AmunRa.dummyRendererId;
    }

    @Override
    public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_)
    {
        return this.getItem().getItem();
    }

    @Override
    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
    {
        /**
         * Returns whether or not this bed block is the head of the bed.
         */
        return this.getItem().getItem();
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public double getThrust(World w, int x, int y, int z, int meta) {
        return this.getMyTileEntity(w, x, y, z).getNumBoosters() * 100.0D;
    }

    @Override
    public double getSpeed(World world, int x, int y, int z, int meta) {
        return this.getMyTileEntity(world, x, y, z).getSpeed();
    }

    @Override
    public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        // do the isRemote thing here, too?
        entityPlayer.openGui(AmunRa.instance, GuiIds.GUI_MS_ROCKET_ENGINE, world, x, y, z);
        return true;
        // return false;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }


    @Override
    public int damageDropped(int meta) {
        return getItem().getDamage();
    }

    @Override
    public boolean canTravelDistance(World world, int x, int y, int z, int meta, double distance) {
        TileEntityMothershipEngineAbstract t = this.getMyTileEntity(world, x, y, z);
        return t.canTravelDistance(distance);
    }
}
