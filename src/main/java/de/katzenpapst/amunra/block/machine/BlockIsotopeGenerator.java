package de.katzenpapst.amunra.block.machine;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.GuiIds;
import de.katzenpapst.amunra.block.BlockMachineMeta;
import de.katzenpapst.amunra.block.SubBlockMachine;
import de.katzenpapst.amunra.tile.TileEntityIsotopeGenerator;
import de.katzenpapst.amunra.world.CoordHelper;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockIsotopeGenerator extends SubBlockMachine {

    // private IIcon iconFront = null;
    private IIcon iconOutput = null;
    private IIcon iconBlank = null;

    protected final String outputTexture;
    protected final String sideTexture;
    public final float energyGeneration;


    public BlockIsotopeGenerator(String name, String frontTexture, String outputTexture, String sideTexture, float energyGeneration) {
        super(name, frontTexture);

        this.outputTexture = outputTexture;
        this.sideTexture = sideTexture;
        this.energyGeneration = energyGeneration;
    }

    /**
     *
     * @param side
     * @return
     */
    public static boolean isSideEnergyOutput(int side) {
        // wait, wat?
        return false;
    }

    @Override
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
        super.registerBlockIcons(par1IconRegister);
        // this.blockIcon = p_149651_1_.registerIcon(this.getTextureName());
        //this.iconFront = par1IconRegister.registerIcon(AmunRa.TEXTUREPREFIX + "machine_nuclear");
        iconBlank = par1IconRegister.registerIcon(sideTexture);
        iconOutput = par1IconRegister.registerIcon(outputTexture);
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
        //ForgeDirection output = CoordHelper.rotateForgeDirection(ForgeDirection.EAST, realMeta);// also north and west

        if(side == ForgeDirection.UP.ordinal() || side == ForgeDirection.DOWN.ordinal()) {
            return this.iconBlank;
        }

        if(side == front.ordinal()) {
            return this.blockIcon;
        }
        //if(side == output.ordinal()) {
        return this.iconOutput;
        //}
        // return this.iconBlank;
    }

    @Override
    public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        entityPlayer.openGui(AmunRa.instance, GuiIds.GUI_ATOMBATTERY, world, x, y, z);
        return true;
        // return false;
    }

    /**
     * Called throughout the code as a replacement for ITileEntityProvider.createNewTileEntity
     * Return the same thing you would from that function.
     * This will fall back to ITileEntityProvider.createNewTileEntity(World) if this block is a ITileEntityProvider
     *
     * @param metadata The Metadata of the current block
     * @return A instance of a class extending TileEntity
     */
    @Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        return new TileEntityIsotopeGenerator();
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }


    @Override
    public String getShiftDescription(int meta)
    {
        return GCCoreUtil.translate("tile.isotopeGenerator.description");
    }

}
