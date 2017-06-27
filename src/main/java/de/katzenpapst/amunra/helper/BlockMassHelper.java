package de.katzenpapst.amunra.helper;

import java.util.HashMap;

import de.katzenpapst.amunra.block.BlockMetaPairHashable;
import de.katzenpapst.amunra.block.IMassiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;

public class BlockMassHelper {

    private static HashMap<BlockMetaPairHashable, Float> blockMassMap = new HashMap<>();

    @Deprecated
    public static float getBlockMass(World world, Block block, int meta, int x, int y, int z) {
        return getBlockMass(world, block, meta, new BlockPos(x, y, z));
    }

    public static float getBlockMass(World world, Block block, int meta, BlockPos pos) {
        // first, the mass
        if(block.isAir(world, pos)) {
            return 0.0F;
        }
        if(block instanceof IMassiveBlock) {
            return ((IMassiveBlock)block).getMass(world, pos, meta);
        } else {
            BlockMetaPairHashable bmph = new BlockMetaPairHashable(block, (byte) meta);
            if(blockMassMap.containsKey(bmph)) {
                return blockMassMap.get(bmph);
            }
            float guessedMass = guessBlockMass(world, block, meta, pos);

            blockMassMap.put(bmph, guessedMass);

            return guessedMass;
        }
    }

    @Deprecated
    public static float guessBlockMass(World world, Block block, int meta, int x, int y, int z) {
        return guessBlockMass(world, block, meta, new BlockPos(x, y, z));
    }

    public static float guessBlockMass(World world, Block block, int meta, BlockPos pos) {

        if(block instanceof IFluidBlock) {
            return getMassForFluid(((IFluidBlock)block).getFluid());
        }
        if(block instanceof BlockLiquid) {
            // vanilla MC fluids
            if(block == Blocks.lava) {
                return getMassForFluid(FluidRegistry.LAVA);
            }
            return getMassForFluid(FluidRegistry.WATER);
        }

        // extra stuff
        if(block == Blocks.snow_layer) {
            return (meta+1) * 0.025F;
            //return 0.01F; // meta 0 => one, 1 => two, 2=>3, 3=>4, 4=>5, 5=>6, 7 => 8 => full
        }
        if(block == Blocks.vine) {
            return 0.01F;
        }

        return getMassFromHardnessAndMaterial(block.getBlockHardness(world, pos), block.getMaterial());

    }

    public static float getMassForFluid(Fluid fluid) {
        int density = fluid.getDensity();
        // assume density to be in grams until I have a better idea
        return ((float)density)/1000.0F;
    }

    public static float getMassFromHardnessAndMaterial(float hardness, Material material) {
        float m = hardness;
        if (m < 0.1F)
        {
            m = 0.1F;
        }
        else if (m > 30F)
        {
            m = 30F;
        }
        //Wood items have a high hardness compared with their presumed mass
        if (material == Material.wood)
        {
            m /= 4;
        }
        return m;
    }
}
