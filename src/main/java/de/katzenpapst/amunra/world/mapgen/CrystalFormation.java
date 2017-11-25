package de.katzenpapst.amunra.world.mapgen;

import java.util.Random;

import de.katzenpapst.amunra.block.BlockMetaContainer;
import de.katzenpapst.amunra.world.WorldHelper;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class CrystalFormation extends WorldGenerator {

    protected BlockMetaContainer material;
    protected BlockMetaContainer airBlock;
    boolean                         allowDownward;
    boolean                         allowUpward;

    /**
     *
     * @param material
     *            Material to generate the formations from
     * @param airBlock
     *            Block to be considered empty, like air or water or so. Can be
     *            null, then world.isAirBlock will be used
     * @param allowUpward
     *            If true, crystals can grow upwards from the floor
     * @param allowDownward
     *            If true, crystals can grow downwards from the ceiling
     */
    public CrystalFormation(
            BlockMetaPair material,
            BlockMetaPair airBlock,
            boolean allowUpward,
            boolean allowDownward) {
        this.material = new BlockMetaContainer(material);
        this.airBlock = new BlockMetaContainer(airBlock);
        this.allowDownward = allowDownward;
        this.allowUpward = allowUpward;
    }

    public CrystalFormation(BlockMetaPair material) {
        this(material, null, true, true);
    }

    public CrystalFormation(BlockMetaPair material, BlockMetaPair airBlock) {
        this(material, airBlock, true, true);
    }

    public CrystalFormation(BlockMetaPair material, boolean allowUpward, boolean allowDownward) {
        this(material, null, allowUpward, allowDownward);
    }

    protected boolean canPlaceHere(World world, BlockPos pos) {
        if (pos.getY() < 0 || pos.getY() > 255) {
            return false;
        }
        if (airBlock == null) {
            return world.isAirBlock(pos);
        }

        return WorldHelper.isBlockMetaPair(world, pos, airBlock);
    }

    protected boolean isSolidBlock(World world, BlockPos pos, boolean down) {
        EnumFacing dir = down ? EnumFacing.DOWN : EnumFacing.UP;
        return world.isSideSolid(pos, dir);
        // world.getBlock(x, y, z).isOpaqueCube();
        // return !world.isAirBlock(x, y, z);
    }

    protected int getLowestBlock(World world, BlockPos pos) {
        for (int curY = pos.getY(); curY >= 0; curY--) {
            if (!canPlaceHere(world, new BlockPos(pos.getX(), curY, pos.getZ()))) {
                return curY;
            }
        }
        return -1;
    }

    protected int getHighestBlock(World world, BlockPos pos) {
        for (int curY = pos.getY(); curY <= 255; curY++) {
            if (!canPlaceHere(world, new BlockPos(pos.getX(), curY, pos.getZ()))) {
                return curY;
            }
        }
        return -1;
    }

    @Override
    public boolean generate(World world, Random rand, BlockPos pos) {
        boolean downwards = true;

        if (!this.canPlaceHere(world, pos)) {
            return false;
        } else {
            // find lowest and highest block from here
            int lowestY = getLowestBlock(world, pos);
            int highestY = getHighestBlock(world, pos);
            int actualY = 0;

            if (lowestY < 0 && highestY >= 0 && allowDownward) {
                downwards = true;
            } else if (lowestY >= 0 && highestY < 0 && allowUpward) {
                downwards = false;
            } else if (lowestY >= 0 && highestY >= 0) {
                // both seem to be set
                if (allowDownward && allowUpward) {
                    downwards = rand.nextBoolean();
                } else if (allowDownward) {
                    downwards = true;
                } else if (allowUpward) {
                    downwards = false;
                } else {
                    return false;
                }
            } else {
                return false;
            }

            if (downwards) {
                actualY = highestY - 1; // start one below the highest
            } else {
                actualY = lowestY + 1; // start one above the highest
            }

            BlockPos actualYPos = new BlockPos(pos.getX(), actualY, pos.getY());
            if (!canPlaceHere(world, actualYPos)) {
                return false;
            }

            WorldHelper.setBlock(world, actualYPos, material);

            for (int l = 0; l < 1500; ++l) {
                int curX = pos.getX() + rand.nextInt(8) - rand.nextInt(8);
                int curY = actualY; // - rand.nextInt(12);
                int curZ = pos.getZ() + rand.nextInt(8) - rand.nextInt(8);

                if (downwards) {
                    curY -= rand.nextInt(12);
                } else {
                    curY += rand.nextInt(12);
                }

                BlockPos curPos = new BlockPos(curX, curY, curZ);

                if (this.canPlaceHere(world, curPos)) {
                    int num = 0;

                    for (int neighbour = 0; neighbour < 6; ++neighbour) {
                        IBlockState state = null;

                        switch (neighbour) {
                        case 0:
                            state = world.getBlockState(curPos.add(-1, 0, 0));
                            break;
                        case 1:
                            state = world.getBlockState(curPos.add(1, 0, 0));
                            break;
                        case 2:
                            state = world.getBlockState(curPos.add(0, -1, 0));
                            break;
                        case 3:
                            state = world.getBlockState(curPos.add(0, 1, 0));
                            break;
                        case 4:
                            state = world.getBlockState(curPos.add(0, 0, -1));
                            break;
                        case 5:
                            state = world.getBlockState(curPos.add(0, 0, 1));
                            break;
                        }

                        if (state != null && material.isBlockState(state)) {
                            num++;
                        }
                    }

                    if (num == 1) {
                        WorldHelper.setBlock(world, curPos, material);
                    }
                }
            }

            return true;
        }
    }

}
