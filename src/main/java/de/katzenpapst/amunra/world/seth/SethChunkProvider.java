package de.katzenpapst.amunra.world.seth;

import java.util.ArrayList;
import java.util.List;

import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta;
import micdoodle8.mods.galacticraft.core.perlin.generator.Gradient;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.chunk.IChunkProvider;
import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.world.AmunraChunkProvider;

public class SethChunkProvider extends AmunraChunkProvider {

	BlockMetaPair iceBlock;
	private Gradient noiseGen4;

	private static final int CHUNK_SIZE_X = 16;
    private static final int CHUNK_SIZE_Y = 256;
    private static final int CHUNK_SIZE_Z = 16;

	public SethChunkProvider(World par1World, long seed,
			boolean mapFeaturesEnabled) {
		super(par1World, seed, mapFeaturesEnabled);
		iceBlock = new BlockMetaPair(Blocks.packed_ice, (byte) 0);
		this.noiseGen4 = new Gradient(this.rand.nextLong(), 2, 0.25F);
	}

	@Override
    public void replaceBlocksForBiome(int chunkX, int chunkZ, Block[] arrayOfIDs, byte[] arrayOfMeta, BiomeGenBase[] par4ArrayOfBiomeGenBase)
    {
        final int dirtLayerWidth = 20;
        final int waterLevelUpper = 64;
        final int waterLevelLower = 20;
        // temp
        final BlockMetaPair oceanFloor = ARBlocks.blockBasalt;
        final BlockMetaPair water = new BlockMetaPair(Blocks.water, (byte) 0);
        // generate the default stuff first
        super.replaceBlocksForBiome(chunkX, chunkZ, arrayOfIDs, arrayOfMeta, par4ArrayOfBiomeGenBase);
        // now do my stuff
        for (int curX = 0; curX < 16; ++curX)
        {
            for (int curZ = 0; curZ < 16; ++curZ)
            {
            	for (int curY = 0; curY <= waterLevelUpper; curY++) {
            		final int index = this.getIndex(curX, curY, curZ);
            		Block curBlockId = arrayOfIDs[index];
            		byte curMeta = arrayOfMeta[index];
            		if(curY < waterLevelLower) { // todo variate this
            			if(curMeta == iceBlock.getMetadata() && curBlockId == iceBlock.getBlock()) {
            				arrayOfIDs[index] = oceanFloor.getBlock();
            				arrayOfMeta[index] = oceanFloor.getMetadata();
            			}
            		} else {
            			if(curMeta == iceBlock.getMetadata() && curBlockId == iceBlock.getBlock()) {
            				arrayOfIDs[index] = water.getBlock();
            				arrayOfMeta[index] = water.getMetadata();
            			}
            		}
            	}
            }
        }
/*
        // ORIGINAL STUFF, trying to figure it out...

        final float noiseGenFreq = 0.03125F;
        this.noiseGen4.setFrequency(noiseGenFreq * 2);
        for (int curX = 0; curX < 16; ++curX)
        {
            for (int curZ = 0; curZ < 16; ++curZ)
            {
                final int curNoise = (int) (this.noiseGen4.getNoise(chunkX * 16 + curX, chunkZ * 16 + curZ) / 3.0D + 3.0D + this.rand.nextDouble() * 0.25D);
                int someFactor = -1;
                Block blockToPlace = this.getGrassBlock().getBlock();
                byte metaToPlace = this.getGrassBlock().getMetadata();
                Block stoneBlockBlock = this.getDirtBlock().getBlock();
                byte stoneBlockMeta = this.getDirtBlock().getMetadata();

                for (int curY = CHUNK_SIZE_Y - 1; curY >= 0; --curY)
                {
                    final int index = this.getIndex(curX, curY, curZ);

                    if (curY <= 0 + this.rand.nextInt(5))
                    {
                    	// this seems to make a random bedrock layer
                        arrayOfIDs[index] = Blocks.bedrock;
                    }
                    else
                    {
                        final Block curBlock = arrayOfIDs[index];

                        if (Blocks.air == curBlock)
                        {
                            someFactor = -1;
                        }
                        else if (curBlock == this.getStoneBlock().getBlock())
                        {
                            arrayOfMeta[index] = this.getStoneBlock().getMetadata();

                            if (someFactor == -1)
                            {
                                if (curNoise <= 0)
                                {
                                    blockToPlace = Blocks.air;
                                    metaToPlace = 0;
                                    stoneBlockBlock = this.getStoneBlock().getBlock();
                                    stoneBlockMeta = this.getStoneBlock().getMetadata();
                                }
                                else if (curY >= dirtLayerWidth - -16 && curY <= dirtLayerWidth + 1)
                                {
                                    blockToPlace = this.getDirtBlock().getBlock();
                                    metaToPlace = this.getDirtBlock().getMetadata();
                                }

                                someFactor = curNoise;

                                if (curY >= dirtLayerWidth - 1)
                                {
                                	// grass or dirt
                                    arrayOfIDs[index] = blockToPlace;
                                    arrayOfMeta[index] = metaToPlace;
                                }
                                else
                                {
                                    arrayOfIDs[index] = stoneBlockBlock;
                                    arrayOfMeta[index] = stoneBlockMeta;
                                }
                            }
                            else if (someFactor > 0)
                            {
                                --someFactor;
                                arrayOfIDs[index] = stoneBlockBlock;
                                arrayOfMeta[index] = stoneBlockMeta;
                            }
                        }
                    }
                }
            }
        }
        */
        // ORIGINAL STUFF end
    }

	@Override
	protected BiomeDecoratorSpace getBiomeGenerator() {
		return new SethBiomeDecorator();
	}

	@Override
	protected BiomeGenBase[] getBiomesForGeneration() {
		return new BiomeGenBase[]{BiomeGenBase.desert};
	}

	@Override
	protected int getSeaLevel() {
		return 120;
	}

	@Override
	protected List<MapGenBaseMeta> getWorldGenerators() {
		ArrayList<MapGenBaseMeta> list = new ArrayList<MapGenBaseMeta>();

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
		return iceBlock;
	}

	@Override
	protected BlockMetaPair getDirtBlock() {
		return iceBlock;
	}

	@Override
	protected BlockMetaPair getStoneBlock() {
		return iceBlock;
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
		return 70;
	}

	@Override
	public void onChunkProvide(int cX, int cZ, Block[] blocks, byte[] metadata) {

	}

	@Override
	public void onPopulate(IChunkProvider provider, int cX, int cZ) {

	}


}
