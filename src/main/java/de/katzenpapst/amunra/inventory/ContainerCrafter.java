package de.katzenpapst.amunra.inventory;

import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.item.ARItems;
import de.katzenpapst.amunra.item.ItemNanotool;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ContainerCrafter extends ContainerWorkbench {

    protected World worldFU;
    protected int posXFU;
    protected int posYFU;
    protected int posZFU;

    public ContainerCrafter(InventoryPlayer playerInv, World world, int x, int y, int z) {
        super(playerInv, world, x, y, z);
        worldFU = world;
        posXFU = x;
        posYFU = y;
        posZFU = z;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        // either using a crafting block, or a crafting tool
        Block b = player.worldObj.getBlock(posXFU, posYFU, posZFU);
        int meta = player.worldObj.getBlockMetadata(posXFU, posYFU, posZFU);

        if(ARBlocks.blockWorkbench.getBlock() == b && ARBlocks.blockWorkbench.getMetadata() == meta) {
            return player.getDistanceSq((double)this.posXFU + 0.5D, (double)this.posYFU + 0.5D, (double)this.posZFU + 0.5D) <= 64.0D;
        }

        // not the block, check for item
        ItemStack stack = player.inventory.getCurrentItem();

        if(stack != null && stack.getItem() == ARItems.nanotool) {
            return ARItems.nanotool.getMode(stack) == ItemNanotool.Mode.WORKBENCH;
        } else {
            return false;
        }
    }
}
