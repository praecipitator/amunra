package de.katzenpapst.amunra.block.machine;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.GuiIds;
import de.katzenpapst.amunra.block.BlockMachineMeta;
import de.katzenpapst.amunra.block.SubBlockMachine;
import de.katzenpapst.amunra.helper.CoordHelper;
import de.katzenpapst.amunra.mothership.Mothership;
import de.katzenpapst.amunra.mothership.MothershipWorldProvider;
import de.katzenpapst.amunra.tile.TileEntityMothershipSettings;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockMothershipSettings extends SubBlockMachine {

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
    public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        if(world.provider instanceof MothershipWorldProvider) {
            // check if the current player is allowed
            if(!((Mothership)((MothershipWorldProvider)world.provider).getCelestialBody()).getOwnerUUID().equals(entityPlayer.getUniqueID())) {
                return false;
            }
            entityPlayer.openGui(AmunRa.instance, GuiIds.GUI_MS_SETTINGS, world, x, y, z);
            return true;
        }
        return false;
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
