package de.katzenpapst.amunra;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.relauncher.Side;
import de.katzenpapst.amunra.client.gui.GuiAtomBattery;
import de.katzenpapst.amunra.inventory.ContainerAtomBattery;
import de.katzenpapst.amunra.tile.TileEntityIsotopeGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {



	public GuiHandler() {
		// TODO Auto-generated constructor stub
	}


	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {

		if(FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER) {
			return null;
		}

		TileEntity tile = world.getTileEntity(x, y, z);

		switch(ID) {
		case GuiIds.GUI_ATOMBATTERY:
			return new ContainerAtomBattery(player.inventory, (TileEntityIsotopeGenerator)tile);
		}
		return null;
	}

	@Override
	// @SideOnly(Side.CLIENT)
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(FMLCommonHandler.instance().getEffectiveSide() != Side.CLIENT) {
			return null;
		}

		TileEntity tile = world.getTileEntity(x, y, z);

		switch(ID) {
		case GuiIds.GUI_ATOMBATTERY:
			return new GuiAtomBattery(player.inventory, (TileEntityIsotopeGenerator)tile);
		}

		return null;
	}

}
