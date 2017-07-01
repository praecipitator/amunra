package de.katzenpapst.amunra.world;


import java.util.Random;

import de.katzenpapst.amunra.block.BlockMetaPairHashable;
import de.katzenpapst.amunra.block.bush.BlockBushMulti;
import de.katzenpapst.amunra.block.bush.SubBlockBush;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
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
	public boolean generate(World world, Random rand, BlockPos pos)
    {
        Block block;

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        do
        {
            BlockPos curPos = new BlockPos (x, y, z);
            IBlockState state = world.getBlockState(curPos);
            block = state.getBlock();
            if (!(block.isLeaves(world, curPos) || block.isAir(world, curPos)))
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

            BlockPos curPos = new BlockPos (curX, curY, curZ);

            IBlockState state = world.getBlockState(curPos);
            BlockMetaPairHashable bmp = new BlockMetaPairHashable(state);


            if(world.isAirBlock(curPos) && plant.canPlaceOn(bmp, 0)) {
                WorldHelper.setBlock(world, curPos, bmp);
            }
        }

        return true;
    }
}