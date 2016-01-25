package de.katzenpapst.amunra.block.machine;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.block.BlockMachineMeta;
import de.katzenpapst.amunra.block.SubBlockMachine;
import de.katzenpapst.amunra.tile.TileEntityAtomicBattery;
import de.katzenpapst.amunra.world.CoordHelper;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockAtomicBattery extends SubBlockMachine {

	private IIcon iconFront = null;
	private IIcon iconOutput = null;
	private IIcon iconBlank = null;
	/**
	 *
	 * @param side
	 * @return
	 */
	public static boolean isSideEnergyOutput(int side) {
		return false;
	}

	@Override
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
        this.iconFront = par1IconRegister.registerIcon(AmunRa.TEXTUREPREFIX + "machine_nuclear");
        iconBlank = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_blank");
        iconOutput = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_output");
        this.blockIcon = iconFront;
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
		ForgeDirection output = CoordHelper.rotateForgeDirection(ForgeDirection.EAST, realMeta);


		if(side == front.ordinal()) {
			return this.iconFront;
		}
		if(side == output.ordinal()) {
			return this.iconOutput;
		}


		return this.iconBlank;

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
        return new TileEntityAtomicBattery();
    }


	public BlockAtomicBattery(String name, String texture) {
		super(name, texture);
		// TODO Auto-generated constructor stub
	}

	public BlockAtomicBattery(String name, String texture, String tool, int harvestLevel) {
		super(name, texture, tool, harvestLevel);
		// TODO Auto-generated constructor stub
	}

	public BlockAtomicBattery(String name, String texture, String tool, int harvestLevel, float hardness,
			float resistance) {
		super(name, texture, tool, harvestLevel, hardness, resistance);
		// TODO Auto-generated constructor stub
	}

	@Override
    public String getShiftDescription(int meta)
    {
        return null;//GCCoreUtil.translate("tile.solarBasic.description");

    }

}
