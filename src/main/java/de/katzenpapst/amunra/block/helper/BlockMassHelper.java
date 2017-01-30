package de.katzenpapst.amunra.block.helper;

import de.katzenpapst.amunra.block.IMassiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;

public class BlockMassHelper {

    public static float getBlockMass(World world, Block block, int meta, int x, int y, int z) {
        // first, the mass
        if(block.isAir(world, x, y, z)) {
            return 0.0F;
        }
        if(block instanceof IMassiveBlock) {
            return ((IMassiveBlock)block).getMass(world, x, y, z, meta);
        } else {

            return guessBlockMass(world, block, meta, x, y, z);
        }
    }

    public static float guessBlockMass(World world, Block block, int meta, int x, int y, int z) {

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

        return getMassFromHardnessAndMaterial(block.getBlockHardness(world, x, y, z), block.getMaterial());

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
