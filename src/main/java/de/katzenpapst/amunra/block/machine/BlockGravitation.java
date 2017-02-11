package de.katzenpapst.amunra.block.machine;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.GuiIds;
import de.katzenpapst.amunra.block.BlockMachineMeta;
import de.katzenpapst.amunra.block.SubBlockMachine;
import de.katzenpapst.amunra.helper.CoordHelper;
import de.katzenpapst.amunra.tile.TileEntityGravitation;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockGravitation extends SubBlockMachine {

    private String backTexture;
    private String sideTexture;
    private String activeTexture;

    private IIcon backIcon = null;
    private IIcon sideIcon = null;
    private IIcon activeIcon = null;


    public BlockGravitation(String name, String frontInactiveTexture, String activeTexture, String sideTexture, String backTexture) {
        super(name, frontInactiveTexture);

        this.backTexture = backTexture;
        this.sideTexture = sideTexture;
        this.activeTexture = activeTexture;
    }

    @Override
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
        super.registerBlockIcons(par1IconRegister);
        backIcon = par1IconRegister.registerIcon(backTexture);
        sideIcon = par1IconRegister.registerIcon(sideTexture);
        activeIcon = par1IconRegister.registerIcon(activeTexture);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        int realMeta = ((BlockMachineMeta)this.parent).getRotationMeta(meta);

        ForgeDirection front = CoordHelper.rotateForgeDirection(ForgeDirection.SOUTH, realMeta);
        ForgeDirection back = CoordHelper.rotateForgeDirection(ForgeDirection.NORTH, realMeta);

        if(side == front.ordinal()) {
            return this.blockIcon;
        }
        if(side == back.ordinal()) {
            return this.backIcon;
        }
        return this.sideIcon;
/*
        ForgeDirection sideDirection = CoordHelper.rotateForgeDirection(ForgeDirection.getOrientation(side), realMeta);


        switch(sideDirection) {
        case DOWN:
            return this.sideIcon;
        case EAST:
            return this.sideIcon;
        case NORTH:
            return this.backIcon;
        case SOUTH:
            return this.blockIcon;
        case UP:
            return this.sideIcon;
        case WEST:
            return this.sideIcon;
        case UNKNOWN:
        default:
            return this.sideIcon;
        }
        */
    }


    @Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        return new TileEntityGravitation();
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        entityPlayer.openGui(AmunRa.instance, GuiIds.GUI_GRAVITY, world, x, y, z);
        return true;
    }

}
