package de.katzenpapst.amunra.block.machine.mothershipEngine;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.block.SubBlockMachine;
import de.katzenpapst.amunra.tile.TileEntityMothershipEngineJet;
import de.katzenpapst.amunra.tile.TileEntityMothershipEngineBooster;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class MothershipEngineBoosterBase extends SubBlockMachine {

    protected String activeTextureName;
    protected IIcon activeBlockIcon;

    public MothershipEngineBoosterBase(String name, String texture, String activeTexture) {
        super(name, texture);
        activeTextureName = activeTexture;
    }

    public MothershipEngineBoosterBase(String name, String texture, String activeTexture, String tool, int harvestLevel) {
        super(name, texture, tool, harvestLevel);
        activeTextureName = activeTexture;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    public MothershipEngineBoosterBase(
            String name,
            String texture,
            String activeTexture,
            String tool,
            int harvestLevel,
            float hardness,
            float resistance) {
        super(name, texture, tool, harvestLevel, hardness, resistance);
        activeTextureName = activeTexture;
    }


    // TileEntityMothershipEngineBooster.java
    @Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        return new TileEntityMothershipEngineBooster();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg)
    {
        super.registerBlockIcons(reg);
        this.activeBlockIcon = reg.registerIcon(this.activeTextureName);
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        // ah crap I have to render this from the tileentity
        return this.blockIcon;
    }

    @Override
    public void onBlockPlacedBy(World w, int x, int y, int z, EntityLivingBase user, ItemStack stack)
    {
        // update other blocks nearby
        return;
    }

    @Override
    public void onBlockAdded(World p_149726_1_, int p_149726_2_, int p_149726_3_, int p_149726_4_) {
        return;
    }

    /**
     * Should be called after onBlockPlacedBy or after rotations maybe? on each neighbour
     *
     * @param w
     * @param x
     * @param y
     * @param z
     */
    protected void updateNeighbours(World w, int x, int y, int z) {
        /*
        TileEntity leTile = w.getTileEntity(x, y, z);
        if(leTile instanceof TileEntityMothershipEngineJet) {
            ((TileEntityMothershipEngineJet)leTile).updateMultiblock();
            return;
        }
        if(leTile instanceof TileEntityMothershipEngineBooster) {
            ((TileEntityMothershipEngineBooster)leTile).updateMaster(true);
        }*/
    }


    /**
     * Called when the block is attempted to be harvested
     */
    /*@Override
    public void onBlockHarvested(World w, int x, int y, int z, int meta, EntityPlayer player) {
        // seemingly this is only ever called on the server side
        TileEntityMothershipEngineBooster myTile = (TileEntityMothershipEngineBooster) w.getTileEntity(x, y, z);
        if(myTile.hasMaster()) {
            myTile.updateMaster(false);
        }
    }*/

    /**
     * Artificial event when the block is about to be destroyed. Hopefully.
     * @param world
     * @param x
     * @param y
     * @param z
     */
    @Override
    public void blockHasBeenDestroyed(World w, int x, int y, int z) {
        //
        /*TileEntityMothershipEngineBooster leTile = ((TileEntityMothershipEngineBooster)w.getTileEntity(x, y, z));
        if(leTile.hasMaster()) {
            leTile.updateMaster(false);
        }*/
    }

    /**
     * Artificial event after the block has been created.
     * @param world
     * @param x
     * @param y
     * @param z
     * /
    @Override
    public void blockHasBeenCreated(World w, int x, int y, int z) {
     // update other blocks nearby
        updateNeighbours(w, x+1, y, z);
        updateNeighbours(w, x-1, y, z);
        updateNeighbours(w, x, y, z+1);
        updateNeighbours(w, x, y, z-1);
    }*/

}
