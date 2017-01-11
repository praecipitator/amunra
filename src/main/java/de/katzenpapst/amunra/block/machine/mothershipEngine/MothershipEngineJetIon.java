package de.katzenpapst.amunra.block.machine.mothershipEngine;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.GuiIds;
import de.katzenpapst.amunra.item.ARItems;
import de.katzenpapst.amunra.item.ItemDamagePair;
import de.katzenpapst.amunra.item.MothershipFuelRequirements;
import de.katzenpapst.amunra.tile.TileEntityMothershipEngineAbstract;
import de.katzenpapst.amunra.tile.TileEntityMothershipEngineIon;
import de.katzenpapst.amunra.tile.TileEntityMothershipEngineJet;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class MothershipEngineJetIon extends MothershipEngineJetBase {

    protected ItemDamagePair item = null;

    public MothershipEngineJetIon(String name, String texture, String iconTexture) {
        super(name, texture, iconTexture);
    }

    @Override
    protected TileEntityMothershipEngineIon getMyTileEntity(World world, int x, int y, int z) {
        TileEntity t = world.getTileEntity(x, y, z);
        if(t == null || !(t instanceof TileEntityMothershipEngineIon)) {
            return null;
        }
        return (TileEntityMothershipEngineIon)t;
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
    public TileEntity createTileEntity(World world, int metadata)
    {
        return new TileEntityMothershipEngineIon();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderType()
    {
        return AmunRa.dummyRendererId;
    }

    @Override
    public double getThrust(World w, int x, int y, int z, int meta) {
        return this.getMyTileEntity(w, x, y, z).getNumBoosters() * 10.0D;
    }

    @Override
    public double getSpeed(World world, int x, int y, int z, int meta) {
        return this.getMyTileEntity(world, x, y, z).getSpeed();
    }

    @Override
    public boolean canTravelDistance(World world, int x, int y, int z, int meta, double distance) {
        return this.getMyTileEntity(world, x, y, z).canTravelDistance(distance);
    }

    @Override
    public MothershipFuelRequirements getFuelRequirements(World world, int x, int y, int z, int meta, double distance) {
        return this.getMyTileEntity(world, x, y, z).getFuelRequirements(distance);
    }

    @Override
    public void beginTransit(World world, int x, int y, int z, int meta, double distance) {
        this.getMyTileEntity(world, x, y, z).beginTransit(distance);
    }

    @Override
    public void endTransit(World world, int x, int y, int z, int meta) {
        this.getMyTileEntity(world, x, y, z).endTransit();
    }

    @Override
    public boolean isInUse(World world, int x, int y, int z, int meta) {
        return this.getMyTileEntity(world, x, y, z).isInUse();
    }

    @Override
    public boolean isEnabled(World world, int x, int y, int z, int meta) {
        TileEntityMothershipEngineIon tile = this.getMyTileEntity(world, x, y, z);
        return !tile.getDisabled(0) && !tile.isObstructed();
    }

    @Override
    protected ItemDamagePair getItem() {
        if(item == null) {
            item = ARItems.jetItemIon;
        }
        return item;
    }

    @Override
    public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_)
    {
        return item.getItem();
    }

    @Override
    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
    {
        /**
         * Returns whether or not this bed block is the head of the bed.
         */
        return item.getItem();
    }


    @Override
    public int damageDropped(int meta) {
        return item.getDamage();
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

}
