package de.katzenpapst.amunra.block;

import micdoodle8.mods.galacticraft.core.items.ItemBlockDesc;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class SubBlockMachine extends SubBlock implements ItemBlockDesc.IBlockShiftDesc {

	public SubBlockMachine(String name, String texture) {
		super(name, texture);
		// TODO Auto-generated constructor stub
	}

	public SubBlockMachine(String name, String texture, String tool,
			int harvestLevel) {
		super(name, texture, tool, harvestLevel);
		// TODO Auto-generated constructor stub
	}

	public SubBlockMachine(String name, String texture, String tool,
			int harvestLevel, float hardness, float resistance) {
		super(name, texture, tool, harvestLevel, hardness, resistance);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getShiftDescription(int meta) {
		return GCCoreUtil.translate("tile." + blockNameFU + ".description");
	}

	@Override
	public boolean showDescription(int meta) {
		return true;
	}


	/**
     * Called when the machine is right clicked by the player
     *
     * @return True if something happens
     */
    public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        return false;
    }

	public boolean onSneakUseWrench(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX,
			float hitY, float hitZ) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onUseWrench(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX,
			float hitY, float hitZ) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onSneakMachineActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side,
			float hitX, float hitY, float hitZ) {
		// TODO Auto-generated method stub
		return false;
	}

}
