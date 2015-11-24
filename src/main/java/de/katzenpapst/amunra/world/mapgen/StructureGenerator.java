package de.katzenpapst.amunra.world.mapgen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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


		public SubComponentData copy() {
			return new SubComponentData(this.clazz, this.probability, this.minAmount, this.maxAmount);
		}
	}

	/**
	 * Clones an ArrayList of SubComponentData
	 *
	 * helper for generateSubComponents
	 *
	 * @param subCompData
	 * @return
	 */
	private ArrayList cloneSubComponentList(ArrayList<SubComponentData> subCompData) {
		ArrayList<SubComponentData> result = new ArrayList<SubComponentData>();

		for(SubComponentData entry: subCompData) {
			SubComponentData newEntry = entry.copy();
			result.add(newEntry);
		}

		return result;
	}

	/**
	 * Calculates the sum of all SubComponentData's probability values
	 *
	 * helper for generateSubComponents
	 *
	 * @param subCompData
	 * @return
	 */
	private float getProbabilityMaximum(ArrayList<SubComponentData> subCompData) {
		float sum = 0.0F;
		for(SubComponentData entry: subCompData) {
			sum += entry.probability;
		}
		return sum;
	}

	/**
	 * Just takes the "clazz" member of the entry and tries to create a new instance of it
	 *
	 * helper for generateSubComponents
	 *
	 * @param entry
	 * @return
	 */
	private BaseStructureComponent generateComponent(SubComponentData entry) {
		try {
			return entry.clazz.getConstructor().newInstance();
		} catch (Throwable e) {
			FMLLog.info("Instantiating "+entry.clazz.getCanonicalName()+" failed");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Tries to find a sensible limit (aka total maximum of components) for the given list of SubComponentData
	 *
	 * helper for generateSubComponents
	 *
	 * @param subCompData
	 * @param rand
	 * @return
	 */
	private int findComponentLimit(ArrayList<SubComponentData> subCompData, Random rand) {
		int minComponents = 0;
		int maxComponents = 0;
		boolean everythingHasMax = true;
		for(SubComponentData entry: subCompData) {
			minComponents += entry.minAmount;
			if(entry.maxAmount > 0) {
				maxComponents += entry.maxAmount;
			} else {
				everythingHasMax = false;
			}
		}
		if(everythingHasMax) {

			return MathHelper.getRandomIntegerInRange(rand, minComponents, maxComponents);
		}
		// otherwise dunno. Kinda guess something?

		return MathHelper.getRandomIntegerInRange(rand, minComponents, minComponents+subCompData.size());

	}

	/**
	 * Prepares a list of components from a given array of SubComponentData
	 *
	 * @param subCompData	ArrayList of SubComponentData
	 * @param rand			the Random object to use
	 * @param limit			the result will not have more entries than this. if 0, a random limit will be used
	 * @return
	 */
	protected ArrayList generateSubComponents(ArrayList<SubComponentData> subCompData, Random rand, int limit) {
		ArrayList compList = new ArrayList();
		HashMap<String, Integer> typeAmountMapping = new HashMap<String, Integer>();

		if(limit <= 0) {
			limit = findComponentLimit(subCompData, rand);
		}

		ArrayList<SubComponentData> curComponents = this.cloneSubComponentList(subCompData);


		while(true) {
			Iterator<SubComponentData> itr = curComponents.iterator();
			float curValue = 0.0F;

			float total = this.getProbabilityMaximum(curComponents);
			float curRandom = rand.nextFloat()*total;

			// find an entry
			while(itr.hasNext()) {
				SubComponentData entry = itr.next();
				String typeName = entry.clazz.getCanonicalName();

				if(typeAmountMapping.get(typeName) == null) {
					typeAmountMapping.put(typeName, 0);
				}

				int curAmount = typeAmountMapping.get(typeName);

				boolean isBelowMinimum = (entry.minAmount > 0 && curAmount < entry.minAmount);


				if(
					// automatically pick it if it's minimum isn't reached
					isBelowMinimum ||
					// or if it's in the current rand's range
					(curValue <= curRandom && curRandom <= entry.probability+curValue)
				) {
					// pick this
					BaseStructureComponent cmp = generateComponent(entry);
					if(cmp != null) {
						compList.add(cmp);
					}
					curAmount = curAmount+1;
					typeAmountMapping.put(typeName, curAmount);

					boolean isMaximumReached = entry.maxAmount > 0 && curAmount >= entry.maxAmount;

					if(isMaximumReached || cmp == null) {
						// enough of this one
						itr.remove();
						total = this.getProbabilityMaximum(curComponents);
					}

					break;
				}
				curValue += entry.probability;
			}// end of while(itr.hasNext())

			if(compList.size() >= limit || curComponents.isEmpty()) {
				break;
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
			int i = MathHelper.getRandomIntegerInRange(rand, 0, subCompData.size()-1);
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
	 * Return true if this structure (or any part of it) should be generated in this chunk
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
	 * @param chunkProvider		current chunk provider
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
	 * Adds stuff like mobs or tileentities, which can't be added in the step where the block and meta arrays are being filled
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
