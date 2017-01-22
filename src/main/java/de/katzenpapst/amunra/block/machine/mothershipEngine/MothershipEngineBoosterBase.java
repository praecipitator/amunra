package de.katzenpapst.amunra.block.machine.mothershipEngine;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.GuiIds;
import de.katzenpapst.amunra.block.SubBlockMachine;
import de.katzenpapst.amunra.tile.TileEntityMothershipEngineJet;
import de.katzenpapst.amunra.vec.Vector3int;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import de.katzenpapst.amunra.tile.TileEntityMothershipEngineAbstract;
import de.katzenpapst.amunra.tile.TileEntityMothershipEngineBooster;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
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
    public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        TileEntity leTile = world.getTileEntity(x, y, z);
        if(leTile == null || !(leTile instanceof TileEntityMothershipEngineBooster)) {
            return false;
        }
        TileEntityMothershipEngineBooster tile = (TileEntityMothershipEngineBooster)leTile;

        if(tile.hasMaster()) {
            Vector3int pos = tile.getMasterPosition();

            entityPlayer.openGui(AmunRa.instance, GuiIds.GUI_MS_ROCKET_ENGINE, world, pos.x, pos.y, pos.z);
            return true;
        }
         return false;
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
        if(side <= 1) {
            return this.blockIcon;
        }
        return activeBlockIcon;
    }


    @Override
    public void onNeighborBlockChange(World w, int x, int y, int z, Block block)
    {
        // these are MY coords
        TileEntity leTile = w.getTileEntity(x, y, z);
        if(leTile == null) return;

        if(leTile instanceof TileEntityMothershipEngineAbstract) {
            ((TileEntityMothershipEngineAbstract)leTile).scheduleUpdate();
        } else if(leTile instanceof TileEntityMothershipEngineBooster) {
            ((TileEntityMothershipEngineBooster)leTile).updateMaster(false);
            // attept to continue the process
            // find next
            Vector3int pos = ((TileEntityMothershipEngineBooster)leTile).getPossibleNextBooster();
            if(pos != null) {
                w.notifyBlockOfNeighborChange(pos.x, pos.y, pos.z, ((TileEntityMothershipEngineBooster)leTile).blockType);
            }
        }
    }

    @Override
    public String getShiftDescription(int meta)
    {
        return GCCoreUtil.translate("tile.mothershipEngineRocket.description");
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public boolean isBlockNormalCube()
    {
        return true;
    }

    @Override
    public boolean isNormalCube()
    {
        return true;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderType()
    {
        return AmunRa.msBoosterRendererId;
    }

    public ResourceLocation getBoosterTexture() {
        return new ResourceLocation(AmunRa.ASSETPREFIX, "textures/blocks/jet-base.png");
    }

    @Override
    public boolean canBeMoved(World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if(te == null || !(te instanceof TileEntityMothershipEngineBooster)) {
            return true;
        }
        TileEntityMothershipEngineAbstract master = ((TileEntityMothershipEngineBooster)te).getMasterTile();
        return (master == null || !master.isInUse());
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
