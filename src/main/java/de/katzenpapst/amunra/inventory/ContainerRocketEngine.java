package de.katzenpapst.amunra.inventory;

import de.katzenpapst.amunra.tile.TileEntityMothershipEngineAbstract;
import micdoodle8.mods.galacticraft.core.inventory.SlotSpecific;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemBucket;
import net.minecraftforge.fluids.ItemFluidContainer;

public class ContainerRocketEngine extends ContainerWithPlayerInventory {

    public ContainerRocketEngine(InventoryPlayer par1InventoryPlayer, TileEntityMothershipEngineAbstract tile) {

        super(tile);

        /*this.inventory = p_i1824_1_;
        this.slotIndex = p_i1824_2_;
        this.xDisplayPosition = p_i1824_3_;
        this.yDisplayPosition = p_i1824_4_;*/
        // inv, slotIndex, x, y
        initSlots(tile);

        initPlayerInventorySlots(par1InventoryPlayer);
    }

    protected void initSlots(TileEntityMothershipEngineAbstract tile) {
        this.addSlotToContainer(new SlotSpecific(tile, 0, 8, 7, ItemFluidContainer.class, ItemBucket.class));
    }



    @Override
    public boolean canInteractWith(EntityPlayer var1)
    {
        return ((TileEntityMothershipEngineAbstract)this.tileEntity).isUseableByPlayer(var1);
    }

}
