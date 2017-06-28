package de.katzenpapst.amunra.block.machine;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.GuiIds;
import de.katzenpapst.amunra.block.BlockMachineMeta;
import de.katzenpapst.amunra.helper.CoordHelper;
import de.katzenpapst.amunra.tile.TileEntityMothershipSettings;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockMothershipSettings extends AbstractBlockMothershipRestricted {

    protected final String frontTexture;
    private IIcon iconFront = null;

    public BlockMothershipSettings(String name, String frontTexture, String sideTexture) {
        super(name, sideTexture);

        this.frontTexture = frontTexture;
    }

    @Override
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
        super.registerBlockIcons(par1IconRegister);
        iconFront = par1IconRegister.registerIcon(frontTexture);
        //this.blockIcon = iconFront;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        int realMeta = ((BlockMachineMeta)this.parent).getRotationMeta(meta);
        // we have the front thingy at front.. but what is front?
        // east is the output
        // I think front is south
        ForgeDirection front = CoordHelper.rotateForgeDirection(ForgeDirection.SOUTH, realMeta);
        //ForgeDirection output = CoordHelper.rotateForgeDirection(ForgeDirection.EAST, realMeta);


        if(side == front.ordinal()) {
            return this.iconFront;
        }

        return this.blockIcon;

    }

    @Override
    protected void openGui(World world, int x, int y, int z, EntityPlayer entityPlayer)
    {
        entityPlayer.openGui(AmunRa.instance, GuiIds.GUI_MS_SETTINGS, world, x, y, z);
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        return new TileEntityMothershipSettings();
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }
}
