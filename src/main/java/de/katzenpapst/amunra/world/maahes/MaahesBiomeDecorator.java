package de.katzenpapst.amunra.world.maahes;

import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.world.WorldGenTallgrassMeta;
import de.katzenpapst.amunra.world.WorldGenTreeBySapling;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace;

public class MaahesBiomeDecorator extends BiomeDecoratorSpace {
	protected World mWorld = null;
	protected WorldGenerator grassGen = new WorldGenTallgrassMeta(ARBlocks.blockMethaneTGrass);
	protected WorldGenerator treeGen = new WorldGenTreeBySapling(false, 5, ARBlocks.blockMethaneSapling);
	protected WorldGenerator podGen = new WorldGenTreeBySapling(false, 5, ARBlocks.blockPodSapling);
	private int grassPerChunk = 5;
	@Override
	protected void setCurrentWorld(World world) {
		mWorld = world;

	}

	@Override
	protected World getCurrentWorld() {
		return mWorld;
	}

	@Override
	protected void decorate() {

		for (int j = 0; j < this.grassPerChunk ; ++j)
        {
            int k = this.chunkX + this.mWorld.rand.nextInt(16) + 8;
            int l = this.chunkZ + this.mWorld.rand.nextInt(16) + 8;
            int i1 = mWorld.rand.nextInt(this.mWorld.getHeightValue(k, l) * 2);

            grassGen.generate(this.mWorld, this.mWorld.rand, k, i1, l);
        }
		//doGen = TerrainGen.decorate(currentWorld, randomGenerator, chunk_X, chunk_Z, TREE);
		int numTrees = Math.round(this.mWorld.rand.nextInt(75)/100.0F);
        for (int j = 0; j < numTrees; ++j)
        {
            int k = this.chunkX + this.mWorld.rand.nextInt(16) + 8;
            int l = this.chunkZ + this.mWorld.rand.nextInt(16) + 8;
            int i1 = this.mWorld.getHeightValue(k, l);


            if (treeGen.generate(this.mWorld, this.mWorld.rand, k, i1, l))
            {
                //worldgenabstracttree.func_150524_b(this.currentWorld, this.randomGenerator, k, i1, l);
            }
        }

        int numPods = Math.round(this.mWorld.rand.nextInt(65)/100.0F);
        for (int j = 0; j < numPods; ++j)
        {
            int k = this.chunkX + this.mWorld.rand.nextInt(16) + 8;
            int l = this.chunkZ + this.mWorld.rand.nextInt(16) + 8;
            int i1 = this.mWorld.getHeightValue(k, l);


            if (podGen.generate(this.mWorld, this.mWorld.rand, k, i1, l))
            {
                //worldgenabstracttree.func_150524_b(this.currentWorld, this.randomGenerator, k, i1, l);
            }
        }
	}

}
