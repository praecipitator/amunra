package de.katzenpapst.amunra.block.helper;

import de.katzenpapst.amunra.block.IMassiveBlock;
import de.katzenpapst.amunra.block.IMetaBlock;
import de.katzenpapst.amunra.block.SubBlock;
import de.katzenpapst.amunra.block.machine.mothershipEngine.MothershipEngineJetBase;
import de.katzenpapst.amunra.vec.Vector3int;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class BlockMassHelper {

    public static float getBlockMass(World world, Block block, int meta, int x, int y, int z) {
        // first, the mass
        float m = 0;
        if(block.isAir(world, x, y, z)) {
            return 0.0F;
        }
        if(block instanceof IMassiveBlock) {
            return ((IMassiveBlock)block).getMass(world, x, y, z, meta);
        } else {

            m = 1.0F;
            //Liquids have a mass of 1, stone, metal blocks etc will be heavier
            if (!(block instanceof BlockLiquid))
            {
                //For most blocks, hardness gives a good idea of mass
                m = block.getBlockHardness(world, x, y, z);
                if (m < 0.1F)
                {
                    m = 0.1F;
                }
                else if (m > 30F)
                {
                    m = 30F;
                }
                //Wood items have a high hardness compared with their presumed mass
                if (block.getMaterial() == Material.wood)
                {
                    m /= 4;
                }

                //TODO: higher mass for future Galacticraft hi-density item like neutronium
                //Maybe also check for things in other mods by name: lead, uranium blocks?
                // my TODO: give my blocks an actual mass or density parameter?
            } else {
                // I beg to differ, lava should be way denser than water, for example
                if(block == Blocks.lava) {
                    m = 5.0F; // JUST GUESSING
                }
            }
        }
        return m;
    }
}
