package de.katzenpapst.amunra.world.seth;

import java.util.ArrayList;
import java.util.List;

import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.world.AmunraChunkProvider;
import de.katzenpapst.amunra.world.TerrainGenerator;
import de.katzenpapst.amunra.world.mapgen.volcano.VolcanoGenerator;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.chunk.IChunkProvider;

public class SethChunkProvider extends AmunraChunkProvider {

	BlockMetaPair rockBlock;
	BlockMetaPair grassBlock;
	BlockMetaPair dirtBlock;

	BlockMetaPair waterBlock;
	BlockMetaPair floorGrassBlock;
	BlockMetaPair floorDirtBlock;
	BlockMetaPair floorStoneBlock;


	protected final int floorDirtWidth = 4;

	protected final int maxWaterHeight = 60;

	private TerrainGenerator oceanFloorGen;

	protected VolcanoGenerator volcanoGen;

	public SethChunkProvider(World par1World, long seed,
			boolean mapFeaturesEnabled) {
		super(par1World, seed, mapFeaturesEnabled);
		rockBlock 	= new BlockMetaPair(Blocks.packed_ice, (byte) 0);
		grassBlock 	= new BlockMetaPair(Blocks.snow, (byte) 0);
		dirtBlock 	= new BlockMetaPair(Blocks.ice, (byte) 0);

		floorStoneBlock = ARBlocks.blockYellowRock;
		floorDirtBlock  = new BlockMetaPair(Blocks.clay, (byte) 0);
		floorGrassBlock = ARBlocks.blockUnderwaterGrass;
		waterBlock = new BlockMetaPair(Blocks.water, (byte) 0);
		//waterBlock = new BlockMetaPair(Blocks.air, (byte) 0);	// DEBUG


		oceanFloorGen = new TerrainGenerator(
				this.rand,
				floorStoneBlock,
				waterBlock,
				30,	// heightMod
				35,	// smallFeatureMod
				40,	// mountainHeightMod
				10,	// valleyHeightMod
				25,	// seaLevel
				maxWaterHeight	// maxHeight
		);

		volcanoGen = new VolcanoGenerator(
				waterBlock,
				rockBlock,
				dirtBlock,
				60
		);
	}

	@Override
	public void generateTerrain(int chunkX, int chunkZ, Block[] idArray, byte[] metaArray)
	{
		super.generateTerrain(chunkX, chunkZ, idArray, metaArray);

		oceanFloorGen.generateTerrain(chunkX, chunkZ, idArray, metaArray);
	}

	@Override
    public void replaceBlocksForBiome(int chunkX, int chunkZ, Block[] arrayOfIDs, byte[] arrayOfMeta, BiomeGenBase[] par4ArrayOfBiomeGenBase)
    {
        // generate the default stuff first
        super.replaceBlocksForBiome(chunkX, chunkZ, arrayOfIDs, arrayOfMeta, par4ArrayOfBiomeGenBase);
        // now do my stuff

        for (int curX = 0; curX < 16; ++curX)
        {
            for (int curZ = 0; curZ < 16; ++curZ)
            {
            	int surfaceHeight = -1;
            	for (int curY = maxWaterHeight-1; curY >0; curY--) {
            		final int index = this.getIndex(curX, curY, curZ);
            		Block curBlockId = arrayOfIDs[index];
            		byte curMeta = arrayOfMeta[index];

            		if(curBlockId == floorStoneBlock.getBlock() && curMeta == floorStoneBlock.getMetadata()) {

            			if(surfaceHeight == -1) {
            				surfaceHeight = curY;
            				arrayOfIDs[index] = floorGrassBlock.getBlock();
            				arrayOfMeta[index] = floorGrassBlock.getMetadata();
            			} else {
        					if(surfaceHeight-curY < floorDirtWidth) {
        						arrayOfIDs[index] = floorDirtBlock.getBlock();
                				arrayOfMeta[index] = floorDirtBlock.getMetadata();
        					}
            			}
            		}
            	}
            }
        }
    }

	@Override
	protected BiomeDecoratorSpace getBiomeGenerator() {
		return new SethBiomeDecorator();
	}

	@Override
	protected BiomeGenBase[] getBiomesForGeneration() {
		return new BiomeGenBase[]{BiomeGenBase.iceMountains};
	}

	@Override
	protected int getSeaLevel() {
		return 120;
	}

	@Override
	protected List<MapGenBaseMeta> getWorldGenerators() {
		ArrayList<MapGenBaseMeta> list = new ArrayList<MapGenBaseMeta>();
		list.add(volcanoGen);
    	return list;
	}

	@Override
	protected SpawnListEntry[] getMonsters() {
		return new SpawnListEntry[]{};
	}

	@Override
	protected SpawnListEntry[] getCreatures() {
		return new SpawnListEntry[]{};
	}

	@Override
	protected BlockMetaPair getGrassBlock() {
		return grassBlock;
	}

	@Override
	protected BlockMetaPair getDirtBlock() {
		return dirtBlock;
	}

	@Override
	protected BlockMetaPair getStoneBlock() {
		return rockBlock;
	}

	@Override
	public double getHeightModifier() {
		return 40;
	}

	@Override
	public double getSmallFeatureHeightModifier() {
		return 60;
	}

	@Override
	public double getMountainHeightModifier() {
		return 70;
	}

	@Override
	public double getValleyHeightModifier() {
		return 50;
	}

	@Override
	public void onChunkProvide(int cX, int cZ, Block[] blocks, byte[] metadata) {

	}

	@Override
	public void onPopulate(IChunkProvider provider, int cX, int cZ) {

	}


}
