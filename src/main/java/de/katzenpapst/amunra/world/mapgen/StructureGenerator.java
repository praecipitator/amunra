package de.katzenpapst.amunra.world.mapgen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
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

	public class SubComponentData {
		public Class<? extends BaseStructureComponent> clazz;
		public float probability;
		public int minAmount;
		public int maxAmount;

		public SubComponentData(Class<? extends BaseStructureComponent> clazz, float probability, int minAmount, int maxAmount) {
			this.clazz = clazz;
			this.probability = probability;
			this.minAmount = minAmount;
			this.maxAmount = maxAmount;
		}
	}

	/**
	 * Prepares a list of components from a given array of SubComponentData
	 *
	 * @param subCompData	ArrayList of SubComponentData
	 * @param rand			a Random object
	 * @param hardLimit		if > 0, the result will not have more entries than this
	 * @return
	 */
	protected ArrayList generateSubComponents(ArrayList<SubComponentData> subCompData, Random rand, int hardLimit) {
		ArrayList compList = new ArrayList();
		// now prepare the actual component list
		for(SubComponentData entry: subCompData) {
			try {
				// generate the minimum amount
				BaseStructureComponent cmp = null;
				int nrGenerated = 0;
				boolean shouldGenerateMore = true;

				// now generate the extra
				while(shouldGenerateMore) {
					shouldGenerateMore = false;
					if(hardLimit > 0 && hardLimit <= compList.size()){
						// hard limit reached
						return compList;
					}
					if(entry.minAmount > 0 && nrGenerated < entry.minAmount) {
						shouldGenerateMore = true;
					} else {
						if(rand.nextFloat() < entry.probability) {
							shouldGenerateMore =  true;
						}
					}

					if(shouldGenerateMore) {
						cmp = entry.clazz.getConstructor().newInstance();
						compList.add(cmp);
						// start.addComponent(cmp);
						nrGenerated++;
					}
					if(nrGenerated >= entry.maxAmount) {
						break;
					}
				}

			} catch (Throwable e) {
				FMLLog.info("Instantiating "+entry.clazz.getCanonicalName()+" failed");
				e.printStackTrace();
			}
		}
		return compList;
	}

	/**
	 * Generate one single component from the list. Min and max values from SubComponentData will be ignored
	 *
	 * @param subCompData
	 * @param rand
	 * @return
	 */
	protected BaseStructureComponent generateOneComponent(ArrayList<SubComponentData> subCompData, Random rand) {

		BaseStructureComponent result = null;
		Class<? extends BaseStructureComponent> resultClass = null;

		for(SubComponentData entry: subCompData) {
			if(entry.probability < rand.nextFloat()) {
				resultClass = entry.clazz;
				break;
			}
		}
		if(resultClass == null) {
			// as fallback
			int i = MathHelper.getRandomIntegerInRange(rand, 0, subCompData.size());
			resultClass = subCompData.get(i).clazz;
		}

		try {

			result = resultClass.getConstructor().newInstance();
		}catch (Throwable e) {
			FMLLog.info("Instantiating "+resultClass.getCanonicalName()+" failed");
			e.printStackTrace();
		}

		return result;
	}

	protected IChunkProvider chunkProvider = null;

	public class BaseStructureMap extends HashMap<Long, BaseStructureStart> {};

	protected BaseStructureMap structureMap = new BaseStructureMap();

	public StructureGenerator() {
	}

	/**
	 * Return some random long for a seed
	 * @return
	 */
	abstract protected long getSalt();

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

	abstract public String getName();
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
        //this.rand.setSeed(world.getSeed());
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
        // this.rand.setSeed(world.getSeed());
        //final long r0 = this.rand.nextLong();
        //final long r1 = this.rand.nextLong();

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
			FMLLog.info("No "+this.getName()+" for population for coords "+(xChunkCoord*16)+"/"+(zChunkCoord*16)+", that's weird...");
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
