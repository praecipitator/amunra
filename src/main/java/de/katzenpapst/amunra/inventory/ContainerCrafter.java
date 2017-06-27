package de.katzenpapst.amunra.inventory;

import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.item.ARItems;
import de.katzenpapst.amunra.item.ItemNanotool;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class ContainerCrafter extends ContainerWorkbench {

    protected World worldFU;
    protected BlockPos posFU;

    public ContainerCrafter(InventoryPlayer playerInv, World world, BlockPos pos) {
        super(playerInv, world, pos);
        worldFU = world;
        posFU = pos;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        IBlockState bs = player.worldObj.getBlockState(posFU);
        // either using a crafting block, or a crafting tool
        Block b = bs.getBlock();
        int meta = b.getMetaFromState(bs);

        if(ARBlocks.blockWorkbench.getBlock() == b && ARBlocks.blockWorkbench.getMetadata() == meta) {
            return player.getDistanceSqToCenter(posFU) <= 64.0D;
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
