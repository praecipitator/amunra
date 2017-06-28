package de.katzenpapst.amunra.world;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;

public class WorldHelper {

    public static BlockMetaPair getBlockMetaPair(World world, int x, int y, int z) {
        return getBlockMetaPair(world, new BlockPos(x, y, z));
    }

    public static boolean isBlockMetaPair(World world, int x, int y, int z, BlockMetaPair bmp) {
        return isBlockMetaPair(world, x,  y, z, bmp);
    }

    public static BlockMetaPair getBlockMetaPair(World world, BlockPos pos) {

        IBlockState state = world.getBlockState(pos);
        return new BlockMetaPair(state.getBlock(), (byte) state.getBlock().getMetaFromState(state));
    }

    public static boolean isBlockMetaPair(World world, BlockPos pos, BlockMetaPair bmp) {

        IBlockState state = world.getBlockState(pos);

        return state.getBlock() == bmp.getBlock() && state.getBlock().getMetaFromState(state) == bmp.getMetadata();
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

    public static void setFireToBlock(World worldObj, BlockPos pos, double fromX, double fromY, double fromZ)
    {

    }

    public static void setBlockIfFree(World worldObj, BlockPos pos, Block block, int meta) {
        IBlockState state = worldObj.getBlockState(pos);
        Block old = state.getBlock();
        if(old == Blocks.air) {
            worldObj.setBlockState(pos, block.getStateFromMeta(meta));
        }
    }

    public static void setBlockIfFree(World worldObj, int x, int y, int z, Block block, int meta) {
        setBlockIfFree(worldObj, new BlockPos(x, y, z), block, meta);
    }

    /**
     * Returns true if the given block can be walked through. Will probably return false for fluids, too
     *
     * @param worldObj
     * @param x
     * @param y
     * @param z
     * @param checkTop
     * @return
     */
    public static boolean isSolid(World worldObj, int x, int y, int z, boolean checkTop) {
        return isSolid(worldObj, new BlockPos(x, y, z), checkTop);
    }

    /**
     * Returns true if the given block can be walked through. Will probably return false for fluids, too
     *
     * @param worldObj
     * @param x
     * @param y
     * @param z
     * @param checkTop
     * @return
     */
    public static boolean isSolid(World worldObj, BlockPos pos, boolean checkTop) {
        IBlockState state = worldObj.getBlockState(pos);
        Block b = state.getBlock();
        if(checkTop) {
            return worldObj.doesBlockHaveSolidTopSurface(worldObj, pos);
        }
        return b.getMaterial().isSolid();
    }

    public static boolean isSolid(World worldObj, BlockPos pos) {
        return isSolid(worldObj, pos, false);
    }

    /**
     * Returns true if the given block can be walked through
     * @param worldObj
     * @param x
     * @param y
     * @param z
     * @return
     */
    public static boolean isSolid(World worldObj, int x, int y, int z) {
        return isSolid(worldObj, x, y, z, false);
    }

    public static boolean isNonSolid(World worldObj, BlockPos pos) {
        IBlockState state = worldObj.getBlockState(pos);
        Block b = state.getBlock();

        return b.isAir(worldObj, pos) || (!b.getMaterial().isLiquid() && !b.getMaterial().isSolid());
    }

    /**
     * Checks if given block is safe to place the player
     * @param worldObj
     * @param x
     * @param y
     * @param z
     * @return
     */
    public static boolean isNonSolid(World worldObj, int x, int y, int z) {
        return isNonSolid(worldObj, new BlockPos(x, y, z));
    }

    public static BlockPos getHighestNonEmptyBlock(World world, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {

        for(int y = maxY; y >= minY; y--) {
            for(int x=minX; x<=maxX; x++) {
                for(int z=minZ; z<=maxZ; z++) {
                    BlockPos potentialResult = new BlockPos(x, y, z);
                    if(!isNonSolid(world, potentialResult)) {
                        return potentialResult;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Shortcut for the 1.8+ stuff for block setting...
     * @param world
     * @param pos
     * @param pair
     */
    public static void setBlock(World world, BlockPos pos, BlockMetaPair pair) {
        Block b = pair.getBlock();
        IBlockState state = b.getStateFromMeta(pair.getMetadata());
        world.setBlockState(pos, state);
    }

    public static boolean isBlock(World world, BlockPos pos, BlockMetaPair pair) {
        IBlockState state = world.getBlockState(pos);
        if(state.getBlock() != pair.getBlock()) {
            return false;
        }

        return pair.getBlock().getMetaFromState(state) == pair.getMetadata();

    }
}
