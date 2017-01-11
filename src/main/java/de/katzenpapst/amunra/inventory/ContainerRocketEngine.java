package de.katzenpapst.amunra.inventory;

import de.katzenpapst.amunra.tile.TileEntityIsotopeGenerator;
import de.katzenpapst.amunra.tile.TileEntityMothershipEngineAbstract;
import de.katzenpapst.amunra.tile.TileEntityMothershipEngineJet;
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase;
import micdoodle8.mods.galacticraft.core.inventory.SlotSpecific;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemBucket;
import net.minecraftforge.fluids.ItemFluidContainer;

public class ContainerRocketEngine extends ContainerElectric {

    public ContainerRocketEngine(InventoryPlayer par1InventoryPlayer, TileEntityMothershipEngineAbstract solarGen) {

        super(par1InventoryPlayer, solarGen);

        /*this.inventory = p_i1824_1_;
        this.slotIndex = p_i1824_2_;
        this.xDisplayPosition = p_i1824_3_;
        this.yDisplayPosition = p_i1824_4_;*/
        // inv, slotIndex, x, y
        this.addSlotToContainer(new SlotSpecific(solarGen, 0, 8, 7, ItemFluidContainer.class, ItemBucket.class));

        initPlayerInventorySlots(par1InventoryPlayer);
    }



    @Override
    public boolean canInteractWith(EntityPlayer var1)
    {
        return ((TileEntityMothershipEngineAbstract)this.tileEntity).isUseableByPlayer(var1);
    }

}
