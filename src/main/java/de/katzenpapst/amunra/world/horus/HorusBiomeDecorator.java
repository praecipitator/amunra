package de.katzenpapst.amunra.world.horus;

import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.world.AmunraBiomeDecorator;
import de.katzenpapst.amunra.world.WorldGenMinableBMP;

public class HorusBiomeDecorator extends AmunraBiomeDecorator {

	private WorldGenerator diamondGen;
	private WorldGenerator rubyGen;
	private WorldGenerator emergaldGen;


	@Override
	protected void setCurrentWorld(World world) {
		super.setCurrentWorld(world);
		BlockMetaPair obsidian = new BlockMetaPair(Blocks.obsidian, (byte) 0);
		// SEE: net.minecraft.world.biome.BiomeDecorator.BiomeDecorator()
		diamondGen 	= new WorldGenMinableBMP(ARBlocks.oreDiamondObsid, 12, obsidian);
		rubyGen 	= new WorldGenMinableBMP(ARBlocks.oreRubyObsid,    16, obsidian);
		emergaldGen = new WorldGenMinableBMP(ARBlocks.oreEmeraldObsid, 16, obsidian);

	}

	@Override
	protected void decorate() {
		// SEE: micdoodle8.mods.galacticraft.planets.mars.world.gen.BiomeDecoratorMars.decorate()
		this.generateOre(8, diamondGen, 2, 45);
		this.generateOre(12, rubyGen, 8, 60);
		this.generateOre(14, emergaldGen, 8, 52);

	}

}
