package de.katzenpapst.amunra.world;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
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

    /**
     * Attempts to ignite the block at the given position from the given direction
     *
     * @param worldObj
     * @param x
     * @param y
     * @param z
     * @param fromX
     * @param fromY
     * @param fromZ
     */
    public static void setFireToBlock(World worldObj, int x, int y, int z, double fromX, double fromY, double fromZ)
    {
        double deltaX = x+0.5 - fromX;
        double deltaY = y+0.5 - fromY;
        double deltaZ = z+0.5 - fromZ;

        double deltaXabs = Math.abs(deltaX);
        double deltaYabs = Math.abs(deltaY);
        double deltaZabs = Math.abs(deltaZ);

        if(deltaXabs > deltaYabs) {
            if(deltaXabs > deltaZabs) {
                if(deltaX < 0) {
                    setBlockIfFree(worldObj, x+1, y, z, Blocks.fire, 0);
                } else {
                    setBlockIfFree(worldObj, x-1, y, z, Blocks.fire, 0);
                }
            } else {
                if(deltaZ < 0) {
                    setBlockIfFree(worldObj, x, y, z+1, Blocks.fire, 0);
                } else {
                    setBlockIfFree(worldObj, x, y, z-1, Blocks.fire, 0);
                }
            }
        } else {
            if(deltaYabs > deltaZabs) {
                if(deltaY < 0) {
                    setBlockIfFree(worldObj, x, y+1, z, Blocks.fire, 0);
                } else {
                    setBlockIfFree(worldObj, x, y-1, z, Blocks.fire, 0);
                }
            } else {
                if(deltaZ < 0) {
                    setBlockIfFree(worldObj, x, y, z+1, Blocks.fire, 0);
                } else {
                    setBlockIfFree(worldObj, x, y, z-1, Blocks.fire, 0);
                }
            }
        }
    }

    public static void setBlockIfFree(World worldObj, int x, int y, int z, Block block, int meta) {
        Block old = worldObj.getBlock(x, y, z);
        if(old == Blocks.air) {
            System.out.println("setting "+x+"/"+y+"/"+z+" on fire");
            worldObj.setBlock(x, y, z, block, meta, 3);
        }
    }
}
