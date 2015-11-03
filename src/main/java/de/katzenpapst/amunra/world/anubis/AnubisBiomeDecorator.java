package de.katzenpapst.amunra.world.anubis;

import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.world.WorldGenMinableBMP;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class AnubisBiomeDecorator extends BiomeDecoratorSpace {
	protected World mWorld = null;
	private WorldGenerator cryoGemGen;
	private WorldGenerator aluGen;
	private WorldGenerator goldGen;
	private WorldGenerator lapisGen;
	@Override
	protected void setCurrentWorld(World world) {
		mWorld = world;
		// SEE: net.minecraft.world.biome.BiomeDecorator.BiomeDecorator()
		cryoGemGen = new WorldGenMinableBMP(ARBlocks.oreCryoBasalt, 6, ARBlocks.blockBasalt);
		aluGen = new WorldGenMinableBMP(ARBlocks.oreAluBasalt, 8, ARBlocks.blockBasalt);
		goldGen = new WorldGenMinableBMP(ARBlocks.oreGoldBasalt, 6, ARBlocks.blockBasalt);
		lapisGen = new WorldGenMinableBMP(ARBlocks.oreLapisBasalt, 12, ARBlocks.blockBasalt);
		// add: alu, gold, lapis
		/*public BiomeDecoratorMars()
    {
        this.copperGen = new WorldGenMinableMeta(MarsBlocks.marsBlock, 4, 0, true, MarsBlocks.marsBlock, 9);
        this.tinGen = new WorldGenMinableMeta(MarsBlocks.marsBlock, 4, 1, true, MarsBlocks.marsBlock, 9);
        this.deshGen = new WorldGenMinableMeta(MarsBlocks.marsBlock, 6, 2, true, MarsBlocks.marsBlock, 9);
        this.ironGen = new WorldGenMinableMeta(MarsBlocks.marsBlock, 8, 3, true, MarsBlocks.marsBlock, 9);
        this.dirtGen = new WorldGenMinableMeta(MarsBlocks.marsBlock, 32, 6, true, MarsBlocks.marsBlock, 9);
        this.iceGen = new WorldGenMinableMeta(Blocks.ice, 18, 0, true, MarsBlocks.marsBlock, 6);
    }

    @Override
    protected void decorate()
    {
        this.generateOre(4, this.iceGen, 60, 120);
        this.generateOre(20, this.dirtGen, 0, 200);
        this.generateOre(15, this.deshGen, 20, 64);
        this.generateOre(26, this.copperGen, 0, 60);
        this.generateOre(23, this.tinGen, 0, 60);
        this.generateOre(20, this.ironGen, 0, 64);
    }
    */

	}

	@Override
	protected World getCurrentWorld() {
		return mWorld;
	}

	@Override
	protected void decorate() {
		// SEE: micdoodle8.mods.galacticraft.planets.mars.world.gen.BiomeDecoratorMars.decorate()
		this.generateOre(8, cryoGemGen, 8, 45);
		this.generateOre(23, aluGen, 0, 60);
		this.generateOre(12, goldGen, 12, 52);
		this.generateOre(16, lapisGen, 0, 16);

	}

}