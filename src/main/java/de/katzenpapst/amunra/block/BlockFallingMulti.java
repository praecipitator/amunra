package de.katzenpapst.amunra.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class BlockFallingMulti extends BlockBasicMulti {

	public BlockFallingMulti(String name, Material mat, int initialCapacity) {
		super(name, mat, initialCapacity);
	}
	

  
    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    @Override
    public void onBlockAdded(World world, int x, int y, int z)
    {
        world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor Block
     */
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block otherBlock)
    {
        world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
    }

    /**
     * Ticks the block if it's been scheduled
     */
    @Override
    public void updateTick(World world, int x, int y, int z, Random rand)
    {
        if (!world.isRemote)
        {
            this.doTheFalling(world, x, y, z);
        }
    }

    private void doTheFalling(World world, int x, int y, int z)
    {
        if (canContinueFalling(world, x, y - 1, z) && y >= 0)
        {
            byte b0 = 32;
        	// It might be a good idea to check for the value of the official falling block...
            if (!BlockFalling.fallInstantly && world.checkChunksExist(x - b0, y - b0, z - b0, x + b0, y + b0, z + b0))
            {
                if (!world.isRemote)
                {
                    EntityFallingBlock entityfallingblock = new EntityFallingBlock(world, (double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), this, world.getBlockMetadata(x, y, z));
                    this.onEntityCreated(entityfallingblock);
                    world.spawnEntityInWorld(entityfallingblock);
                }
            }
            else
            {
                world.setBlockToAir(x, y, z);

                while (canContinueFalling(world, x, y - 1, z) && y > 0)
                {
                    --y;
                }

                if (y > 0)
                {
                    world.setBlock(x, y, z, this);
                }
            }
        }
    }

    protected void onEntityCreated(EntityFallingBlock entity) {}

    /**
     * How many world ticks before ticking
     */
    public int tickRate(World world)
    {
        return 2;
    }

    public static boolean canContinueFalling(World world, int x, int y, int z)
    {
        Block block = world.getBlock(x, y, z);

        if (block.isAir(world, x, y, z))
        {
            return true;
        }
        else if (block == Blocks.fire)
        {
            return true;
        }
        else
        {
        	// TODO figure out how it works for forge fluids
            Material material = block.getMaterial();
            return material == Material.water ? true : material == Material.lava;
        }
    }

}
