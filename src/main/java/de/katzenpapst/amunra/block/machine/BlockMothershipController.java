package de.katzenpapst.amunra.block.machine;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.GuiIds;
import de.katzenpapst.amunra.block.BlockMachineMeta;
import de.katzenpapst.amunra.block.SubBlockMachine;
import de.katzenpapst.amunra.mothership.MothershipWorldProvider;
import de.katzenpapst.amunra.tile.TileEntityMothershipController;
import de.katzenpapst.amunra.world.CoordHelper;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockMothershipController extends SubBlockMachine {

    protected final String frontTexture;
    private IIcon iconFront = null;

    public BlockMothershipController(String name, String frontTexture, String sideTexture) {
        super(name, sideTexture);
        this.frontTexture = frontTexture;
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
       if(world.provider instanceof MothershipWorldProvider && ((MothershipWorldProvider)world.provider).isPlayerOwner(entityPlayer)) {

           entityPlayer.openGui(AmunRa.instance, GuiIds.GUI_MOTHERSHIPCONTROLLER, world, x, y, z);
           return true;
       }
       return false;
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
       return new TileEntityMothershipController();
   }
}
