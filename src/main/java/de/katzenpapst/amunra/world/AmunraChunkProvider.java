package de.katzenpapst.amunra.world;

import micdoodle8.mods.galacticraft.api.prefab.world.gen.ChunkProviderSpace;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

abstract public class AmunraChunkProvider extends ChunkProviderSpace {

    public static final int CHUNK_SIZE_X = 16;
    public static final int CHUNK_SIZE_Y = 256;
    public static final int CHUNK_SIZE_Z = 16;

    public AmunraChunkProvider(World par1World, long seed, boolean mapFeaturesEnabled) {
        super(par1World, seed, mapFeaturesEnabled);
    }

    @Override
    public int getCraterProbability() {
        // vestigial
        return 2000;
    }

    /**
     * I failed fixing this. I might do this as mapgen instead
     */
    @Override
    public void makeCrater(int craterX, int craterZ, int chunkX, int chunkZ, int size, ChunkPrimer primer) {
    }

    /**
     * Because private...
     *
     * @param d1
     * @param d2
     * @param t
     * @return
     */
    protected double fuckYouLerp(double d1, double d2, double t) {
        if (t < 0.0) {
            return d1;
        } else if (t > 1.0) {
            return d2;
        } else {
            return d1 + (d2 - d1) * t;
        }
    }

    private int getHighestNonAir(Block[] blocks, int x, int z) {
        for (int y = 127; y > 1; y--) {
            if (blocks[this.getIndex(x, y, z)] != Blocks.air) {
                return y;
            }
        }
        return 1;
    }

    protected int getIndex(int x, int y, int z) {
        return (x * 16 + z) * 256 + y;
    }

}
