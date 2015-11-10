package de.katzenpapst.amunra.world;


import java.util.Random;

import de.katzenpapst.amunra.block.BlockBushMulti;
import de.katzenpapst.amunra.block.SubBlockBush;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenTallgrassMeta extends WorldGenerator
{
    private Block tallGrassBlock;
    private int tallGrassMetadata;
    private SubBlockBush plant;

    public WorldGenTallgrassMeta(BlockBushMulti block, int meta)
    {
        this.tallGrassBlock = block;
        this.tallGrassMetadata = meta;

    	plant = (SubBlockBush) block.getSubBlock(meta);

    }

    public WorldGenTallgrassMeta(BlockMetaPair grass) {
    	this((BlockBushMulti) grass.getBlock(), grass.getMetadata());
    }

    @Override
	public boolean generate(World world, Random rand, int x, int y, int z)
    {
        Block block;


        do
        {
            block = world.getBlock(x, y, z);
            if (!(block.isLeaves(world, x, y, z) || block.isAir(world, x, y, z)))
            {
                break;
            }
            --y;
        } while (y > 0);

        for (int l = 0; l < 128; ++l)
        {
            int curX = x + rand.nextInt(8) - rand.nextInt(8);
            int curY = y + rand.nextInt(4) - rand.nextInt(4);
            int curZ = z + rand.nextInt(8) - rand.nextInt(8);

            if(world.isAirBlock(curX, curY, curZ) &&
            		plant.canPlaceOn(world.getBlock(curX, curY-1, curZ), world.getBlockMetadata(curX, curY-1, curZ), 0)
    		) {

                world.setBlock(curX, curY, curZ, this.tallGrassBlock, this.tallGrassMetadata, 2);
            }
        }

        return true;
    }
}