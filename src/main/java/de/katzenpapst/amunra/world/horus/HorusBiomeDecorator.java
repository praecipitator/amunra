package de.katzenpapst.amunra.world.horus;

import java.util.List;

import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.init.Blocks;
import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.world.AmunraBiomeDecorator;
import de.katzenpapst.amunra.world.WorldGenOre;

public class HorusBiomeDecorator extends AmunraBiomeDecorator {


	@Override
	protected List<WorldGenOre> getOreGenerators()
	{
		BlockMetaPair obsidian = new BlockMetaPair(Blocks.obsidian, (byte) 0);
		List<WorldGenOre> list = super.getOreGenerators();

		list.add(new WorldGenOre(ARBlocks.oreDiamondObsid, 8, obsidian, 8, 2, 45));
		list.add(new WorldGenOre(ARBlocks.oreRubyObsid,    12, obsidian, 12, 8, 50));
		list.add(new WorldGenOre(ARBlocks.oreEmeraldObsid, 12, obsidian, 14, 8, 42));
		list.add(new WorldGenOre(ARBlocks.oreLeadObsid,    16, obsidian, 16, 2, 70));
		list.add(new WorldGenOre(ARBlocks.oreUraniumObsid, 6, obsidian, 6, 2, 14));

		return list;
	}

}
