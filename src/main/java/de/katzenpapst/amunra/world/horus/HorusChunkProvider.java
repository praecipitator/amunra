package de.katzenpapst.amunra.world.horus;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.chunk.IChunkProvider;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedCreeper;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSkeleton;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedZombie;
import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.world.AmunraChunkProvider;
import de.katzenpapst.amunra.world.mapgen.pyramid.PyramidGenerator;

public class HorusChunkProvider  extends AmunraChunkProvider {

	protected final BlockMetaPair stoneBlock = new BlockMetaPair(Blocks.obsidian, (byte) 0);
	protected PyramidGenerator pyramid = new PyramidGenerator();

	public HorusChunkProvider(World par1World, long seed,
			boolean mapFeaturesEnabled) {
		super(par1World, seed, mapFeaturesEnabled);
		pyramid.setFillMaterial(ARBlocks.blockBasaltBrick);
		pyramid.setFloorMaterial(ARBlocks.blockSmoothBasalt);
		pyramid.setWallMaterial(ARBlocks.blockObsidianBrick);
	}

	@Override
	protected BiomeDecoratorSpace getBiomeGenerator() {
		return new HorusBiomeDecorator();
	}

	@Override
	protected BiomeGenBase[] getBiomesForGeneration() {
        return new BiomeGenBase[]{BiomeGenBase.desert};
	}

	@Override
	protected int getSeaLevel() {
		return 64;
	}

	@Override
	protected List<MapGenBaseMeta> getWorldGenerators() {
		ArrayList<MapGenBaseMeta> list = new ArrayList<MapGenBaseMeta>();
		list.add(pyramid);
    	return list;
	}

	@Override
	protected SpawnListEntry[] getMonsters() {
		SpawnListEntry skele = new SpawnListEntry(EntityEvolvedSkeleton.class, 100, 4, 4);
        SpawnListEntry creeper = new SpawnListEntry(EntityEvolvedCreeper.class, 100, 4, 4);
        SpawnListEntry zombie = new SpawnListEntry(EntityEvolvedZombie.class, 100, 4, 4);

        return new SpawnListEntry[]{skele, creeper, zombie};
	}

	@Override
	protected SpawnListEntry[] getCreatures() {
		return new SpawnListEntry[]{};
	}

	@Override
	protected BlockMetaPair getGrassBlock() {
		return ARBlocks.blockObsidiSand;
	}

	@Override
	protected BlockMetaPair getDirtBlock() {
		return ARBlocks.blockObsidiGravel;
	}

	@Override
	protected BlockMetaPair getStoneBlock() {
		return stoneBlock;
	}

	@Override
	public double getHeightModifier() {
		return 20;
	}

	@Override
	public double getSmallFeatureHeightModifier() {
		return 40;
	}

	@Override
	public double getMountainHeightModifier() {
		return 60;
	}

	@Override
	public double getValleyHeightModifier() {
		// TODO Auto-generated method stub
		return 60;
	}

	@Override
	public void onChunkProvide(int cX, int cZ, Block[] blocks, byte[] metadata) {

	}

	@Override
	public void onPopulate(IChunkProvider provider, int cX, int cZ) {

	}

	@Override
    public void populate(IChunkProvider par1IChunkProvider, int chunkX, int chunkZ) {
    	super.populate(par1IChunkProvider, chunkX, chunkZ);

    	this.pyramid.populate(this, worldObj, chunkX, chunkZ);
    	// this.pyramid.populate(this, worldObj, chunkX, chunkZ);

    	//this.villageTest.generateStructuresInChunk(this.worldObj, this.rand, par2, par3);
    }

}
