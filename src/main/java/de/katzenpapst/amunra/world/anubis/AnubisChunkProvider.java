package de.katzenpapst.amunra.world.anubis;

import java.util.ArrayList;
import java.util.List;

import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedCreeper;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSkeleton;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedZombie;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.chunk.IChunkProvider;
import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.world.AmunraChunkProvider;
import de.katzenpapst.amunra.world.mapgen.village.BoxHouseComponent;
import de.katzenpapst.amunra.world.mapgen.village.DomedHouseComponent;
import de.katzenpapst.amunra.world.mapgen.village.GridVillageGenerator;
import de.katzenpapst.amunra.world.mapgen.village.SolarField;

public class AnubisChunkProvider extends AmunraChunkProvider {

    protected GridVillageGenerator gVillage = new GridVillageGenerator();

    //PyramidGenerator pyramid = new PyramidGenerator();

    //Pyramid testPyramid = new Pyramid();

    public AnubisChunkProvider(World par1World, long seed,
            boolean mapFeaturesEnabled) {
        super(par1World, seed, mapFeaturesEnabled);

        gVillage.addComponentType(BoxHouseComponent.class, 0.9F, 2, 4);
        gVillage.addComponentType(SolarField.class, 0.7F, 2, 6);
        gVillage.addComponentType(DomedHouseComponent.class, 0.7F, 2, 4);
        //gVillage.addComponentType(PyramidHouseComponent.class, 0.7F, 2, 4);
    }

    @Override
    protected BiomeDecoratorSpace getBiomeGenerator() {
        // TODO Auto-generated method stub
        return new AnubisBiomeDecorator();
    }

    //This should be a custom biome for your mod, but I'm opting to go desert instead out of quickness
    //and the fact that biomes are outside the scope of this tutorial
    @Override
    protected BiomeGenBase[] getBiomesForGeneration() {
        return new BiomeGenBase[]{BiomeGenBase.desert};
    }

    @Override
    protected SpawnListEntry[] getCreatures() {
        // SpawnListEntry villager = new SpawnListEntry(EntityAlienVillager.class, 10, 2, 2);
        return new SpawnListEntry[]{};
    }

    @Override
    protected BlockMetaPair getDirtBlock() {
        return ARBlocks.blockBasaltRegolith;
    }

    @Override
    protected BlockMetaPair getGrassBlock() {
        return ARBlocks.blockDust;
    }

    @Override
    protected BlockMetaPair getStoneBlock() {
        return ARBlocks.blockBasalt;
    }

    @Override
    public double getHeightModifier() {
        return 12;
    }

    @Override
    protected SpawnListEntry[] getMonsters() {
        SpawnListEntry skele = new SpawnListEntry(EntityEvolvedSkeleton.class, 100, 4, 4);
        SpawnListEntry creeper = new SpawnListEntry(EntityEvolvedCreeper.class, 100, 4, 4);
        SpawnListEntry zombie = new SpawnListEntry(EntityEvolvedZombie.class, 100, 4, 4);

        return new SpawnListEntry[]{skele, creeper, zombie};
    }

    @Override
    public double getMountainHeightModifier() {
        return 95;
    }

    @Override
    protected int getSeaLevel() {
        return 93;// taken from mars
    }

    @Override
    public double getSmallFeatureHeightModifier() {
        return 26;
    }



    @Override
    public double getValleyHeightModifier() {
        return 60;
    }

    @Override
    protected List<MapGenBaseMeta> getWorldGenerators() {
        // TODO fill in with caves and villages
        ArrayList<MapGenBaseMeta> list = new ArrayList<MapGenBaseMeta>();
        list.add(gVillage);
        // list.add(pyramid);
        return list;
    }

    @Override
    public void onChunkProvide(int arg0, int arg1, Block[] arg2, byte[] arg3) {
    }

    @Override
    public void onPopulate(IChunkProvider arg0, int arg1, int arg2){
    }

    @Override
    public boolean chunkExists(int x, int y){
        return true; //?
    }

    @Override
    public void populate(IChunkProvider par1IChunkProvider, int chunkX, int chunkZ) {
        super.populate(par1IChunkProvider, chunkX, chunkZ);

        this.gVillage.populate(this, worldObj, chunkX, chunkZ);
        // this.pyramid.populate(this, worldObj, chunkX, chunkZ);

        //this.villageTest.generateStructuresInChunk(this.worldObj, this.rand, par2, par3);
    }

    @Override
    public void recreateStructures(int par1, int par2)
    {
        //this.villageTest.func_151539_a(this, this.worldObj, par1, par2, (Block[]) null);
    }

}
