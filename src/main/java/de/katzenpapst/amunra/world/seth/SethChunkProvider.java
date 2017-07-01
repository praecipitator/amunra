package de.katzenpapst.amunra.world.seth;

import java.util.ArrayList;
import java.util.List;

import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.block.BlockMetaPairHashable;
import de.katzenpapst.amunra.helper.CoordHelper;
import de.katzenpapst.amunra.world.AmunraChunkProvider;
import de.katzenpapst.amunra.world.TerrainGenerator;
import de.katzenpapst.amunra.world.mapgen.CrystalFormation;
import de.katzenpapst.amunra.world.mapgen.volcano.VolcanoGenerator;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;

public class SethChunkProvider extends AmunraChunkProvider {

    BlockMetaPairHashable rockBlock;
    BlockMetaPairHashable grassBlock;
    BlockMetaPairHashable dirtBlock;

    BlockMetaPairHashable waterBlock;
    BlockMetaPairHashable floorGrassBlock;
    BlockMetaPairHashable floorDirtBlock;
    BlockMetaPairHashable floorStoneBlock;


    protected final int floorDirtWidth = 4;

    protected final int maxWaterHeight = 60;

    private TerrainGenerator oceanFloorGen;

    protected VolcanoGenerator volcanoGen;

    protected CrystalFormation crystalGen;

    public SethChunkProvider(World par1World, long seed,
            boolean mapFeaturesEnabled) {
        super(par1World, seed, mapFeaturesEnabled);
        rockBlock 	= new BlockMetaPairHashable(Blocks.packed_ice, (byte) 0);
        grassBlock 	= new BlockMetaPairHashable(Blocks.snow, (byte) 0);
        dirtBlock 	= new BlockMetaPairHashable(Blocks.ice, (byte) 0);

        floorStoneBlock = new BlockMetaPairHashable(Blocks.hardened_clay, (byte) 0);//ARBlocks.blockYellowRock;
        floorDirtBlock  = new BlockMetaPairHashable(Blocks.clay, (byte) 0);
        floorGrassBlock = ARBlocks.blockUnderwaterGrass;
        waterBlock = new BlockMetaPairHashable(Blocks.water, (byte) 0);
        //waterBlock = new BlockMetaPair(Blocks.air, (byte) 0); // DEBUG


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
                60,
                false
                );

        crystalGen = new CrystalFormation(ARBlocks.blockGlowingCoral, waterBlock);
    }

    @Override
    public void generateTerrain(int chunkX, int chunkZ, ChunkPrimer primer)
    {
        super.generateTerrain(chunkX, chunkZ, primer);

        oceanFloorGen.generateTerrain(chunkX, chunkZ, primer);
    }

    @Override
    public void replaceBlocksForBiome(int chunkX, int chunkZ, ChunkPrimer primer, BiomeGenBase[] par4ArrayOfBiomeGenBase)
    {
        // generate the default stuff first
        super.replaceBlocksForBiome(chunkX, chunkZ, primer, par4ArrayOfBiomeGenBase);
        // now do my stuff

        for (int curX = 0; curX < 16; ++curX)
        {
            for (int curZ = 0; curZ < 16; ++curZ)
            {
                int surfaceHeight = -1;
                for (int curY = maxWaterHeight-1; curY >0; curY--) {
                    final int index = this.getIndex(curX, curY, curZ);

                    IBlockState state = primer.getBlockState(index);

                    if(floorStoneBlock.isBlockState(state)) {

                        if(surfaceHeight == -1) {
                            surfaceHeight = curY;
                            primer.setBlockState(index, floorGrassBlock.getBlockState());
                        } else {
                            if(surfaceHeight-curY < floorDirtWidth) {
                                primer.setBlockState(index, floorDirtBlock.getBlockState());
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
        ArrayList<MapGenBaseMeta> list = new ArrayList<>();
        list.add(volcanoGen);
        return list;
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
    public void onChunkProvide(int cX, int cZ, ChunkPrimer primer) {

    }

    @Override
    public void onPopulate(IChunkProvider provider, int cX, int cZ) {

        int numToGenerate = this.rand.nextInt(this.rand.nextInt(4) + 1);

        int curChunkMinX = CoordHelper.chunkToMinBlock(cX);
        int curChunkMinZ = CoordHelper.chunkToMinBlock(cZ);

        for (int j1 = 0; j1 < numToGenerate; ++j1)
        {
            int curX = curChunkMinX + this.rand.nextInt(16) + 8;
            int curY = 35;//this.rand.nextInt(120) + 4;
            int curZ = curChunkMinZ + this.rand.nextInt(16) + 8;
            crystalGen.generate(this.worldObj, this.rand, new BlockPos(curX, curY, curZ));
        }
    }


}
