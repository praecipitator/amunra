package de.katzenpapst.amunra.world.anubis;

import java.util.ArrayList;
import java.util.List;

import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.ChunkProviderSpace;
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
import de.katzenpapst.amunra.mob.entity.EntityRobotVillager;
import de.katzenpapst.amunra.world.mapgen.pyramid.Pyramid;
import de.katzenpapst.amunra.world.mapgen.village.ARVillage;

public class AnubisChunkProvider extends ChunkProviderSpace {
	
	ARVillage villageTest = null;
	
	Pyramid testPyramid = new Pyramid();

	public AnubisChunkProvider(World par1World, long seed,
			boolean mapFeaturesEnabled) {
		super(par1World, seed, mapFeaturesEnabled);
		
		villageTest = new ARVillage(
				ARBlocks.multiBlockDirt.getBlockMetaPair("basaltregolith"),
        		ARBlocks.multiBlockRock.getBlockMetaPair("alucrate"),
        		ARBlocks.multiBlockRock.getBlockMetaPair("smoothbasalt"),
        		EntityRobotVillager.class
		);
		// TODO Auto-generated constructor stub
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
    public int getCraterProbability() {
        return 2000;
    }

    @Override
    protected SpawnListEntry[] getCreatures() {
        // SpawnListEntry villager = new SpawnListEntry(EntityAlienVillager.class, 10, 2, 2);
        return new SpawnListEntry[]{};
    }

    @Override
    protected BlockMetaPair getDirtBlock() {
        return new BlockMetaPair(ARBlocks.multiBlockDirt, (byte) ARBlocks.multiBlockDirt.getMetaByName("basaltregolith"));
    }

    @Override
    protected BlockMetaPair getGrassBlock() {
    	return new BlockMetaPair(ARBlocks.multiBlockDirt, (byte) ARBlocks.multiBlockDirt.getMetaByName("dustblock"));
    }
    
    @Override
    protected BlockMetaPair getStoneBlock() {
    	return new BlockMetaPair(ARBlocks.multiBlockRock, (byte) ARBlocks.multiBlockRock.getMetaByName("basalt"));
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
    	list.add(testPyramid);
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
    public void populate(IChunkProvider par1IChunkProvider, int par2, int par3) {
    	super.populate(par1IChunkProvider, par2, par3);
    	
    	this.villageTest.generateStructuresInChunk(this.worldObj, this.rand, par2, par3);
    }
    
    @Override
    public void recreateStructures(int par1, int par2)
    {
        this.villageTest.func_151539_a(this, this.worldObj, par1, par2, (Block[]) null);    
    }

}
