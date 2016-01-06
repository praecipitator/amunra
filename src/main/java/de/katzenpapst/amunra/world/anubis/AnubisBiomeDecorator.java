package de.katzenpapst.amunra.world.anubis;

import java.util.List;

import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.world.AmunraBiomeDecorator;
import de.katzenpapst.amunra.world.WorldGenOre;

public class AnubisBiomeDecorator extends AmunraBiomeDecorator {

	@Override
	protected List<WorldGenOre> getOreGenerators()
	{
		List<WorldGenOre> list = super.getOreGenerators();
		list.add(new WorldGenOre(ARBlocks.oreCryoBasalt, 6, ARBlocks.blockBasalt, 8, 8, 45));
		list.add(new WorldGenOre(ARBlocks.oreAluBasalt, 8, ARBlocks.blockBasalt, 23, 0, 60));
		list.add(new WorldGenOre(ARBlocks.oreGoldBasalt, 6, ARBlocks.blockBasalt, 12, 12, 52));
		list.add(new WorldGenOre(ARBlocks.oreLapisBasalt, 12, ARBlocks.blockBasalt, 16, 0, 16));
		list.add(new WorldGenOre(ARBlocks.oreUraniumBasalt, 2, ARBlocks.blockBasalt, 2, 0, 16));
		return null;
	}


}