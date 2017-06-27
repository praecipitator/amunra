package de.katzenpapst.amunra.inventory;

import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase;
import micdoodle8.mods.galacticraft.core.inventory.SlotSpecific;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;

public class ContainerArtificalGravity extends ContainerWithPlayerInventory {

    public ContainerArtificalGravity(InventoryPlayer playerInv, IInventory tile) {
        super(tile);

        this.addSlotToContainer(new SlotSpecific(tile, 0, 152, 132, ItemElectricBase.class));

        initPlayerInventorySlots(playerInv, 35);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        TileEntity te = (TileEntity)this.tileEntity;
        return player.getDistanceSqToCenter(te.getPos()) <= 64.0D;
    }

}
