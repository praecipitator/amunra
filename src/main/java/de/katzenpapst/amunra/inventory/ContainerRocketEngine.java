package de.katzenpapst.amunra.inventory;

import de.katzenpapst.amunra.tile.TileEntityIsotopeGenerator;
import de.katzenpapst.amunra.tile.TileEntityMothershipEngine;
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase;
import micdoodle8.mods.galacticraft.core.inventory.SlotSpecific;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fluids.ItemFluidContainer;

public class ContainerRocketEngine extends ContainerElectric {

    public ContainerRocketEngine(InventoryPlayer par1InventoryPlayer, TileEntityMothershipEngine solarGen) {

        super(par1InventoryPlayer, solarGen);

        /*this.inventory = p_i1824_1_;
        this.slotIndex = p_i1824_2_;
        this.xDisplayPosition = p_i1824_3_;
        this.yDisplayPosition = p_i1824_4_;*/
        // inv, slotIndex, x, y
        this.addSlotToContainer(new SlotSpecific(solarGen, 0, 8, 7, ItemFluidContainer.class));

        initPlayerInventorySlots(par1InventoryPlayer);
    }



    @Override
    public boolean canInteractWith(EntityPlayer var1)
    {
        return ((TileEntityMothershipEngine)this.tileEntity).isUseableByPlayer(var1);
    }

}
