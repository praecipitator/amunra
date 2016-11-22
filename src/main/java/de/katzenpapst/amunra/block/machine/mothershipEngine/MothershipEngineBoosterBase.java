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

    /**
     * Should be called after onBlockPlacedBy or after rotations maybe? on each neighbour
     *
     * @param w
     * @param x
     * @param y
     * @param z
     */
    protected void updateNeighbour(World w, int x, int y, int z)
    {
        TileEntity leTile = w.getTileEntity(x, y, z);
        if(leTile == null) return;

        if(leTile instanceof TileEntityMothershipEngineJet) {
            ((TileEntityMothershipEngineJet)leTile).scheduleUpdate();
        } else if(leTile instanceof TileEntityMothershipEngineBooster) {
            ((TileEntityMothershipEngineBooster)leTile).updateMaster(false);
        }
    }


    @Override
    public void onNeighborBlockChange(World w, int x, int y, int z, Block block)
    {
        updateNeighbour(w, x, y, z);
        /*updateNeighbour(w, x+1, y, z);
        updateNeighbour(w, x-1, y, z);
        updateNeighbour(w, x, y, z+1);
        updateNeighbour(w, x, y, z-1);*/
    }



}
