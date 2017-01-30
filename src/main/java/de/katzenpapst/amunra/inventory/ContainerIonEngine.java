package de.katzenpapst.amunra.inventory;

import de.katzenpapst.amunra.tile.TileEntityMothershipEngineAbstract;
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase;
import micdoodle8.mods.galacticraft.core.inventory.SlotSpecific;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerIonEngine extends ContainerRocketEngine {

    public ContainerIonEngine(InventoryPlayer par1InventoryPlayer, TileEntityMothershipEngineAbstract solarGen) {
        super(par1InventoryPlayer, solarGen);
    }

    @Override
    protected void initSlots(TileEntityMothershipEngineAbstract tile) {
        super.initSlots(tile);
        this.addSlotToContainer(new SlotSpecific(tile, 1, 152, 86, ItemElectricBase.class));
    }


}
