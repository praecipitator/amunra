package de.katzenpapst.amunra.block.machine.mothershipEngine;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.GuiIds;
import de.katzenpapst.amunra.block.SubBlockMachine;
import de.katzenpapst.amunra.item.ItemDamagePair;
import de.katzenpapst.amunra.mothership.fueldisplay.MothershipFuelRequirements;
import de.katzenpapst.amunra.tile.TileEntityMothershipEngineAbstract;
import de.katzenpapst.amunra.tile.TileEntityMothershipEngineJet;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class MothershipEngineJetBase extends SubBlockMachine {

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


    protected TileEntityMothershipEngineAbstract getMyTileEntity(World world, int x, int y, int z) {
        TileEntity t = world.getTileEntity(x, y, z);
        if(t == null || !(t instanceof TileEntityMothershipEngineAbstract)) {
            // TODO throw exception instead
            return null;
        }
        return (TileEntityMothershipEngineAbstract)t;
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
    public String getShiftDescription(int meta) {
        return GCCoreUtil.translate("tile.mothershipEngineRocket.description");
    }

    @Override
    public boolean canBeMoved(World world, int x, int y, int z) {
        return !this.getMyTileEntity(world, x, y, z).isInUse();
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest)
    {
        return removedByPlayer(world, player, x, y, z);
    }

    @Override
    @Deprecated
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z)
    {
        if(this.canBeMoved(world, x, y, z)) {
            return super.removedByPlayer(world, player, x, y, z);
        }
        return false;
    }
}
