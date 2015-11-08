package de.katzenpapst.amunra.world;

import java.util.Random;

import de.katzenpapst.amunra.block.ARTreeSapling;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class WorldGenTree extends WorldGenAbstractTree
{
    /** The minimum height of a generated tree. */
    private final int minTreeHeight;
    /** True if this tree should grow Vines. */
    //private final boolean vinesGrow;
    /** The metadata value of the wood to use in tree generation. */
    //private final int metaWood;
    /** The metadata value of the leaves to use in tree generation. */
    //private final int metaLeaves;
    private static final String __OBFID = "CL_00000438";

    protected ARTreeSapling sapling;

    //protected final BlockMetaPair wood;
    //protected final BlockMetaPair leaves;
    //protected final BlockMetaPair vines;
    // protected final Block


    public WorldGenTree(boolean doBlockNotify, int minTreeHeight, ARTreeSapling sapling)
    {
        super(doBlockNotify);
        this.minTreeHeight = minTreeHeight;
        this.sapling = sapling;
        //this.wood = wood;
        //this.leaves = leaves;
        //this.vines = vines;
        //this.vinesGrow = vines != null;
    }

    @Override
	public boolean generate(World world, Random rand, int x, int y, int z)
    {
        return this.sapling.generate(world, rand, x, y, z, false);
    }
}
