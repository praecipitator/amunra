package de.katzenpapst.amunra.world.seth;

import java.util.List;

import net.minecraft.init.Blocks;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.world.AmunraBiomeDecorator;
import de.katzenpapst.amunra.world.WorldGenOre;

public class SethBiomeDecorator extends AmunraBiomeDecorator {


	@Override
	protected List<WorldGenOre> getOreGenerators()
	{
		List<WorldGenOre> list = super.getOreGenerators();

		BlockMetaPair packedIce = new BlockMetaPair(Blocks.packed_ice, (byte) 0);
		BlockMetaPair floorStoneBlock = new BlockMetaPair(Blocks.hardened_clay, (byte) 0);



		list.add(new WorldGenOre(new BlockMetaPair(Blocks.stone, (byte) 0), 12, packedIce, 8, 60, 120));
		list.add(new WorldGenOre(new BlockMetaPair(Blocks.clay, (byte) 0), 8, packedIce, 4, 60, 70));

		list.add(new WorldGenOre(ARBlocks.oreAluHardClay, 8, floorStoneBlock, 10, 2, 60));
		list.add(new WorldGenOre(ARBlocks.oreCoalHardClay, 20, floorStoneBlock, 14, 10, 60));
		list.add(new WorldGenOre(ARBlocks.oreSiliconHardClay, 6, floorStoneBlock, 6, 2, 18));
		list.add(new WorldGenOre(ARBlocks.oreCopperHardClay, 8, floorStoneBlock, 6, 2, 60));
		list.add(new WorldGenOre(ARBlocks.oreTinHardClay, 8, floorStoneBlock, 6, 2, 60));
		list.add(new WorldGenOre(ARBlocks.oreIronHardClay, 8, floorStoneBlock, 6, 2, 60));

		return list;
	}



}
