package de.katzenpapst.amunra.block.machine;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.block.BlockMachineMeta;
import de.katzenpapst.amunra.block.SubBlockMachine;
import de.katzenpapst.amunra.helper.CoordHelper;
import de.katzenpapst.amunra.tile.TileEntityBlockScale;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockScale extends SubBlockMachine {

    private IIcon iconTop = null;
    private IIcon iconBottom = null;
    private IIcon iconFront = null;

    protected final String topTexture;
    protected final String bottomTexture;
    protected final String frontTexture;

    public BlockScale(String name, String sideTexture, String topTexture, String frontTexture, String bottomTexture) {
        super(name, sideTexture);
        this.topTexture = topTexture;
        this.bottomTexture = bottomTexture;
        this.frontTexture = frontTexture;
    }

    @Override
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
        super.registerBlockIcons(par1IconRegister);

        iconTop = par1IconRegister.registerIcon(topTexture);
        iconBottom = par1IconRegister.registerIcon(bottomTexture);
        iconFront = par1IconRegister.registerIcon(frontTexture);

    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        int realMeta = ((BlockMachineMeta)this.parent).getRotationMeta(meta);

        ForgeDirection sideFD = ForgeDirection.getOrientation(side);


        switch(sideFD) {
        case UP:
            return this.iconTop;
        case DOWN:
            return this.iconBottom;
        default:
            ForgeDirection front = CoordHelper.rotateForgeDirection(ForgeDirection.SOUTH, realMeta);
            if(sideFD == front) {
                return this.iconFront;
            }
            return this.blockIcon;
        }
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        return new TileEntityBlockScale();
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        TileEntity leTile = world.getTileEntity(x, y, z);
        if(leTile instanceof TileEntityBlockScale) {
            ((TileEntityBlockScale)leTile).doUpdate();
            // world.markBlockForUpdate(x, y, z);
        }
    }

}
