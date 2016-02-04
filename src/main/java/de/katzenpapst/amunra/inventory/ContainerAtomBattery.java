package de.katzenpapst.amunra.inventory;

import de.katzenpapst.amunra.tile.TileEntityIsotopeGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerAtomBattery extends ContainerElectric {

	public ContainerAtomBattery(InventoryPlayer par1InventoryPlayer, TileEntityIsotopeGenerator solarGen) {
		super(par1InventoryPlayer, solarGen);
		// TODO Auto-generated constructor stub
	}



    @Override
    public boolean canInteractWith(EntityPlayer var1)
    {
        return ((TileEntityIsotopeGenerator)this.tileEntity).isUseableByPlayer(var1);
    }

}
