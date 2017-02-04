package de.katzenpapst.amunra.world.maahes;

import java.util.ArrayList;
import java.util.List;

import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.mob.entity.EntityAlienBug;
import de.katzenpapst.amunra.mob.entity.EntityPorcodon;
import de.katzenpapst.amunra.world.AmunraChunkProvider;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.chunk.IChunkProvider;


public class MaahesChunkProvider extends AmunraChunkProvider {


    public MaahesChunkProvider(World par1World, long seed,
            boolean mapFeaturesEnabled) {
        super(par1World, seed, mapFeaturesEnabled);
    }


    @Override
    protected BiomeDecoratorSpace getBiomeGenerator() {
        // TODO Auto-generated method stub
        return new MaahesBiomeDecorator();
    }

    //This should be a custom biome for your mod, but I'm opting to go desert instead out of quickness
    //and the fact that biomes are outside the scope of this tutorial
    @Override
    protected BiomeGenBase[] getBiomesForGeneration() {
        return new BiomeGenBase[]{BiomeGenBase.desert};
    }

    @Override
    protected SpawnListEntry[] getCreatures() {

        // entityClass, weightedProbability, minGroupCount, maxGroupCount
        SpawnListEntry pig = new SpawnListEntry(EntityPorcodon.class, 50, 4, 10);
        return new SpawnListEntry[]{pig};

        //SpawnListEntry villager = new SpawnListEntry(EntityAlienVillager.class, 1, 0, 2);
        //return new SpawnListEntry[]{villager};
    }

    @Override
    protected BlockMetaPair getDirtBlock() {
        return ARBlocks.blockMethaneDirt;
    }

    @Override
    protected BlockMetaPair getGrassBlock() {
        return ARBlocks.blockMethaneGrass;
    }

    @Override
    protected BlockMetaPair getStoneBlock() {
        return ARBlocks.blockBasalt;
    }

    /**
     * Seems to affect the baseheight
     * doesn't affect the bedrock holes
     */
    @Override
    public double getHeightModifier() {
        return 10;
    }

    @Override
    protected SpawnListEntry[] getMonsters() {
        SpawnListEntry bug = new SpawnListEntry(EntityAlienBug.class, 100, 4, 4);

        return new SpawnListEntry[]{bug};
    }

    @Override
    public double getMountainHeightModifier() {
        return 0;
    }

    /**
     * medium terrain height, doesn't affect the bedrock holes
     */
    @Override
    protected int getSeaLevel() {
        return 56;
    }

    /**
     * doesn't affect the bedrock holes
     */
    @Override
    public double getSmallFeatureHeightModifier() {
        return 0;
    }



    /**
     * doesn't affect the bedrock holes
     */
    @Override
    public double getValleyHeightModifier() {
        return 0;
    }

    @Override
    protected List<MapGenBaseMeta> getWorldGenerators() {
        // TODO fill in with caves and villages
        return new ArrayList<MapGenBaseMeta>();
    }

    @Override
    public void onChunkProvide(int arg0, int arg1, Block[] arg2, byte[] arg3) {
    }

    @Override
    public void onPopulate(IChunkProvider arg0, int arg1, int arg2){
    }

    @Override
    public boolean chunkExists(int x, int y){
        return false;
    }

}