package de.katzenpapst.amunra.world.neper;

import java.util.List;

import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.world.AmunraBiomeDecorator;
import de.katzenpapst.amunra.world.WorldGenOre;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenerator;

public class NeperBiomeDecorator extends AmunraBiomeDecorator {

	protected WorldGenerator grassGen = new WorldGenTallGrass(Blocks.tallgrass, 1);
	private int grassPerChunk = 10;

	@Override
	protected List<WorldGenOre> getOreGenerators()
	{
		BlockMetaPair stone = new BlockMetaPair(Blocks.stone, (byte) 0);
		List<WorldGenOre> list = super.getOreGenerators();

		list.add(new WorldGenOre(new BlockMetaPair(Blocks.diamond_ore, (byte) 0), 4, stone, 8, 0, 12));
		list.add(new WorldGenOre(new BlockMetaPair(Blocks.emerald_ore, (byte) 0), 4, stone, 4, 8, 32));
		list.add(new WorldGenOre(new BlockMetaPair(Blocks.iron_ore, (byte) 0), 8, stone, 16, 2, 70));
		list.add(new WorldGenOre(new BlockMetaPair(Blocks.gold_ore, (byte) 0), 8, stone, 8, 2, 40));

		list.add(new WorldGenOre(ARBlocks.blockOldConcrete, 64, stone, 16, 30, 70));
		list.add(new WorldGenOre(ARBlocks.oreSteelConcrete, 10, ARBlocks.blockOldConcrete, 16, 30, 70));
		list.add(new WorldGenOre(ARBlocks.oreBoneConcrete,  6, ARBlocks.blockOldConcrete, 12, 30, 70));

		return list;
	}

	@Override
	protected void decorate() {
		super.decorate();
		for (int j = 0; j < this.grassPerChunk ; ++j)
        {
            int k = this.chunkX + this.mWorld.rand.nextInt(16) + 8;
            int l = this.chunkZ + this.mWorld.rand.nextInt(16) + 8;
            int i1 = mWorld.rand.nextInt(this.mWorld.getHeightValue(k, l) * 2);

            grassGen.generate(this.mWorld, this.mWorld.rand, k, i1, l);
        }
	}

}
