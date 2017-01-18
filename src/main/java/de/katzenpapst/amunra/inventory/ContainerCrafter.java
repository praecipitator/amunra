package de.katzenpapst.amunra.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerWorkbench;
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
        return player.getDistanceSq((double)this.posXFU + 0.5D, (double)this.posYFU + 0.5D, (double)this.posZFU + 0.5D) <= 64.0D;
    }

}
