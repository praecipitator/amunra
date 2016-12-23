package de.katzenpapst.amunra.inventory;

import de.katzenpapst.amunra.tile.TileEntityIsotopeGenerator;
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase;
import micdoodle8.mods.galacticraft.core.inventory.SlotSpecific;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerAtomBattery extends ContainerElectric {

    public ContainerAtomBattery(InventoryPlayer par1InventoryPlayer, TileEntityIsotopeGenerator solarGen) {
        super(par1InventoryPlayer, solarGen);

        this.addSlotToContainer(new SlotSpecific(solarGen, 0, 152, 83, ItemElectricBase.class));

        initPlayerInventorySlots(par1InventoryPlayer);
    }



    @Override
    public boolean canInteractWith(EntityPlayer var1)
    {
        return ((TileEntityIsotopeGenerator)this.tileEntity).isUseableByPlayer(var1);
    }

}
