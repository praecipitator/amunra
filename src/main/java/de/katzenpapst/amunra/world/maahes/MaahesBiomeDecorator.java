package de.katzenpapst.amunra.world.maahes;

import java.util.List;

import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.world.AmunraBiomeDecorator;
import de.katzenpapst.amunra.world.WorldGenOre;
import de.katzenpapst.amunra.world.WorldGenTallgrassMeta;
import de.katzenpapst.amunra.world.WorldGenTreeBySapling;
import net.minecraft.util.BlockPos;
import net.minecraft.world.gen.feature.WorldGenerator;

public class MaahesBiomeDecorator extends AmunraBiomeDecorator {

	protected WorldGenerator grassGen = new WorldGenTallgrassMeta(ARBlocks.blockMethaneTGrass);
	protected WorldGenerator treeGen = new WorldGenTreeBySapling(false, 5, ARBlocks.blockMethaneSapling);
	protected WorldGenerator podGen = new WorldGenTreeBySapling(false, 5, ARBlocks.blockPodSapling);

	private int grassPerChunk = 5;



	@Override
	protected List<WorldGenOre> getOreGenerators()
	{
		List<WorldGenOre> list = super.getOreGenerators();

		list.add(new WorldGenOre(ARBlocks.oreAluBasalt,    8, ARBlocks.blockBasalt, 16, 23, 70));
		list.add(new WorldGenOre(ARBlocks.oreGoldBasalt,   6, ARBlocks.blockBasalt, 8, 5, 30));
		list.add(new WorldGenOre(ARBlocks.oreLapisBasalt, 12, ARBlocks.blockBasalt, 6, 2, 20));

		return list;
	}


	@Override
	protected void decorate() {
		super.decorate();



		for (int j = 0; j < this.grassPerChunk ; ++j)
        {
            int k = this.chunkX + this.mWorld.rand.nextInt(16) + 8;
            int l = this.chunkZ + this.mWorld.rand.nextInt(16) + 8;
            BlockPos pos = mWorld.getHeight(new BlockPos(k, 0, l));
            //int i1 = mWorld.rand.nextInt(this.mWorld.getHeightValue(k, l) * 2);

            grassGen.generate(this.mWorld, this.mWorld.rand, pos);
        }
		//doGen = TerrainGen.decorate(currentWorld, randomGenerator, chunk_X, chunk_Z, TREE);
		int numTrees = Math.round(this.mWorld.rand.nextInt(75)/100.0F);
        for (int j = 0; j < numTrees; ++j)
        {
            int k = this.chunkX + this.mWorld.rand.nextInt(16) + 8;
            int l = this.chunkZ + this.mWorld.rand.nextInt(16) + 8;
            //int i1 = this.mWorld.getHeightValue(k, l);
            BlockPos pos = mWorld.getHeight(new BlockPos(k, 0, l));

            treeGen.generate(this.mWorld, this.mWorld.rand, pos);
        }

        int numPods = Math.round(this.mWorld.rand.nextInt(65)/100.0F);
        for (int j = 0; j < numPods; ++j)
        {
            int k = this.chunkX + this.mWorld.rand.nextInt(16) + 8;
            int l = this.chunkZ + this.mWorld.rand.nextInt(16) + 8;
            //int i1 = this.mWorld.getHeightValue(k, l);
            BlockPos pos = mWorld.getHeight(new BlockPos(k, 0, l));

            podGen.generate(this.mWorld, this.mWorld.rand, pos);
        }
	}

}
