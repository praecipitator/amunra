package de.katzenpapst.amunra.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;

public class WorldHelper {

    public static BlockMetaPair getBlockMetaPair(World world, int x, int y, int z) {
        return new BlockMetaPair(world.getBlock(x, y, z), (byte) world.getBlockMetadata(x, y, z));
    }

    public static boolean isBlockMetaPair(World world, int x, int y, int z, BlockMetaPair bmp) {
        return world.getBlock(x, y, z) == bmp.getBlock() && world.getBlockMetadata(x, y, z) == bmp.getMetadata();
    }

    /**
     * Drop entity in world, copy over tag compound, too
     * @param world
     * @param stack
     * @param x
     * @param y
     * @param z
     * @param motionX
     * @param motionY
     * @param motionZ
     */
    public static void dropItemInWorld(World world, ItemStack stack, double x, double y, double z, double motionX, double motionY, double motionZ) {
        EntityItem itemEntity = new EntityItem(world, x, y, z, new ItemStack(stack.getItem(), stack.stackSize, stack.getItemDamage()));

        if (stack.hasTagCompound())
        {
            itemEntity.getEntityItem().setTagCompound((NBTTagCompound) stack.getTagCompound().copy());
        }

        itemEntity.motionX = motionX;
        itemEntity.motionY = motionY;
        itemEntity.motionZ = motionZ;
        world.spawnEntityInWorld(itemEntity);
    }

    public static void dropItemInWorld(World world, ItemStack stack, double x, double y, double z) {
        dropItemInWorld(world, stack, x, y, z, 0, 0, 0);
    }

    public static void dropItemInWorld(World world, ItemStack stack, Entity atEntity) {
        dropItemInWorld(world, stack, atEntity.posX, atEntity.posY, atEntity.posZ, 0, 0, 0);
    }

}
