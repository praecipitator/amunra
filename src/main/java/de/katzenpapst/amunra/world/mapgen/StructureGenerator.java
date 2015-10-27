package de.katzenpapst.amunra.world.mapgen;

import java.util.HashMap;
import java.util.Random;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.block.Block;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta;

/**
 * I'll do a subdivision now: StructureGenerator and Structure
 * For each StructureGenerator there is a subclass of BaseStructure which it generates
 *
 *
 */
abstract public class StructureGenerator extends MapGenBaseMeta {

	protected IChunkProvider chunkProvider = null;

	public class BaseStructureMap extends HashMap<Long, BaseStructureStart> {};

	protected BaseStructureMap structureMap = new BaseStructureMap();

	public StructureGenerator() {
	}

	/**
	 * Return true if this structure can be generated in this chunk
	 *
	 * @param chunkX
	 * @param chunkZ
	 * @param rand
	 * @return
	 */
	abstract protected boolean canGenerateHere(int chunkX, int chunkZ, Random rand);

	/**
	 * Create and maybe somehow init an instance of BaseStructure here
	 *
	 * @param xChunkCoord
	 * @param zChunkCoord
	 * @return
	 */
	abstract protected BaseStructureStart createNewStructure(int xChunkCoord, int zChunkCoord);


	/**
	 *
	 *
	 * @param chunkProvider		maybe I can use this
	 * @param world				the world
	 * @param origXChunkCoord	x coord of the currently generating chunk
	 * @param origZChunkCoord	z coord of the currently generating chunk
	 * @param blocks			blocks array
	 * @param metas				metas array
	 */
	@Override
	public void generate(IChunkProvider chunkProvider, World world, int origXChunkCoord, int origZChunkCoord, Block[] blocks, byte[] metadata)
    {
        this.worldObj = world;
        this.chunkProvider = chunkProvider;
        this.rand.setSeed(world.getSeed());
        //final long r0 = this.rand.nextLong();
        //final long r1 = this.rand.nextLong();

        for (int xChunkCoord = origXChunkCoord - this.range; xChunkCoord <= origXChunkCoord + this.range; ++xChunkCoord)
        {
            for (int zChunkCoord = origZChunkCoord - this.range; zChunkCoord <= origZChunkCoord + this.range; ++zChunkCoord)
            {
            	if(this.canGenerateHere(xChunkCoord, zChunkCoord, rand)) {
            		this.recursiveGenerate(world, xChunkCoord, zChunkCoord, origXChunkCoord, origZChunkCoord, blocks, metadata);
            	}
            }
        }
    }

	@Override
    protected void recursiveGenerate(World par1World, int xChunkCoord, int zChunkCoord, int origXChunkCoord, int origZChunkCoord, Block[] arrayOfIDs, byte[] arrayOfMeta)
    {
		makeStructure(par1World, xChunkCoord, zChunkCoord, origXChunkCoord, origZChunkCoord, arrayOfIDs, arrayOfMeta);


    }

	/**
	 *
	 * @param chunkProvider
	 * @param world
	 * @param origXChunkCoord
	 * @param origZChunkCoord
	 */
	public void populate(IChunkProvider chunkProvider, World world, int origXChunkCoord, int origZChunkCoord) {
		this.worldObj = world;
		this.chunkProvider = chunkProvider;
        this.rand.setSeed(world.getSeed());
        final long r0 = this.rand.nextLong();
        final long r1 = this.rand.nextLong();

        for (int xChunkCoord = origXChunkCoord - this.range; xChunkCoord <= origXChunkCoord + this.range; ++xChunkCoord)
        {
            for (int zChunkCoord = origZChunkCoord - this.range; zChunkCoord <= origZChunkCoord + this.range; ++zChunkCoord)
            {
            	if(this.canGenerateHere(xChunkCoord, zChunkCoord, rand)) {
            		this.recursivePopulate(world, xChunkCoord, zChunkCoord, origXChunkCoord, origZChunkCoord);
            	}
            }
        }
	}



	protected void recursivePopulate(World world, int xChunkCoord, int zChunkCoord, int origXChunkCoord, int origZChunkCoord)
	{
		Long key = Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(xChunkCoord, zChunkCoord));
		if(structureMap.containsKey(key)) {
			BaseStructureStart start = structureMap.get(key);
			start.populateChunk(world, origXChunkCoord, origZChunkCoord);
		} else {
			FMLLog.info("No structure for population for coords "+(xChunkCoord*16)+"/"+(zChunkCoord*16)+", that's weird...");
		}

	}


	/**
	 * Creates or gets an instance of BaseStructure, then makes it generate the current chunk
	 *
	 * @param world
	 * @param xChunkCoord
	 * @param zChunkCoord
	 * @param origXChunkCoord
	 * @param origZChunkCoord
	 * @param arrayOfIDs
	 * @param arrayOfMeta
	 */
	protected void makeStructure(World world, int xChunkCoord, int zChunkCoord, int origXChunkCoord, int origZChunkCoord, Block[] arrayOfIDs, byte[] arrayOfMeta) {
		Long key = Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(xChunkCoord, zChunkCoord));
		BaseStructureStart start = null;
		if(!structureMap.containsKey(key)) {
			start = createNewStructure(xChunkCoord, zChunkCoord);//new GridVillageStart(xChunkCoord, zChunkCoord, this.rand);
			structureMap.put(key, start);
		} else {
			start = structureMap.get(key);
		}
		start.generateChunk(origXChunkCoord, origZChunkCoord, arrayOfIDs, arrayOfMeta);

	}



}
