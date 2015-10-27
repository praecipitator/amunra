package de.katzenpapst.amunra.world.mapgen;

import java.util.HashMap;
import java.util.Random;

import cpw.mods.fml.common.FMLLog;
import de.katzenpapst.amunra.world.mapgen.populator.AbstractPopulator;
import de.katzenpapst.amunra.world.mapgen.populator.SpawnEntity;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;


abstract public class BaseStructureStart extends BaseStructureComponent {

	protected PopulatorByChunkMap populatorsByChunk;
	public class PopulatorMap extends HashMap<BlockVec3, AbstractPopulator> {}

	public class PopulatorByChunkMap extends HashMap<Long, PopulatorMap> {}

	protected int chunkX;
	protected int chunkZ;

	protected Random rand;

	protected World worldObj;

	// coords relative to the
	protected int startX;
	// protected int startY;
	protected int startZ;

	public BaseStructureStart(World world, int chunkX, int chunkZ, Random rand) {

		this.chunkX = chunkX;
		this.chunkZ = chunkZ;

		this.worldObj = world;

		this.rand = rand;

		this.startX = this.rand.nextInt(16);
		this.startZ = this.rand.nextInt(16);

		//int startBlockX = chunkX*16 + this.startX;
		//int startBlockZ = chunkZ*16 + this.startZ;

		populatorsByChunk = new PopulatorByChunkMap();
	}

	protected void preparePopulatorListForChunk(int chunkX, int chunkZ) {
		Long key = Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ));

		if(populatorsByChunk.containsKey(key)) {
			// this is bad, this shouldn't happen
			FMLLog.info("Tried to prepare populator list for chunk "+chunkX+"/"+chunkZ+". This could mean that the chunk is being generated twice.");
			return;
		}

		populatorsByChunk.put(key, new PopulatorMap());
	}

	public World getWorld() {
		return worldObj;
	}

	/**
	 * This should be overridden, but then called before anything else happens
	 */
	@Override
	public boolean generateChunk(int chunkX, int chunkZ, Block[] arrayOfIDs, byte[] arrayOfMeta) {
		preparePopulatorListForChunk(chunkX, chunkZ);

		return true;
	}

	public void populateChunk(World world, int chunkX, int chunkZ) {

		Long chunkKey = Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ));
		if(!populatorsByChunk.containsKey(chunkKey)) {
			FMLLog.info("No populator list for chunk "+chunkX+"/"+chunkZ);
			return;
		}
		PopulatorMap curMap = populatorsByChunk.get(chunkKey);
		populatorsByChunk.remove(chunkKey);// remove it already, at this point, it's too late

		for(AbstractPopulator p: curMap.values()) {
			if(!p.populate(world)) {
				FMLLog.info("Populator "+p.getClass().getCanonicalName()+" failed...");
			}
		}

		curMap.clear();// I hope that's enough of a hint to make java delete this stuff

	}

	public void addPopulator(AbstractPopulator p) {

		int chunkX = p.getX() / 16;
		int chunkZ = p.getZ() / 16;

		Long chunkKey = Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ));
		if(!populatorsByChunk.containsKey(chunkKey)) {
			FMLLog.info("Cannot add populator for "+chunkX+"/"+chunkZ+", offender: "+p.getClass().getCanonicalName()+". Probably it's the wrong chunk");
			return;
		}
		PopulatorMap curMap = populatorsByChunk.get(chunkKey);

		BlockVec3 key = p.getBlockVec3();
		if(curMap.containsKey(key)) {
			FMLLog.info("Cannot add populator for "+key.toString()+", offender: "+p.getClass().getCanonicalName());
			return;
		}
		// pack the coords
		 curMap.put(key, p);
	}

	public void spawnLater(Entity ent, int x, int y, int z) {
		SpawnEntity p = new SpawnEntity(x, y, z, ent);
		addPopulator(p);
	}

	public int getGroundLevel() {
		//((ChunkProviderSpace)worldObj.getChunkProvider()).g
		// NO IDEA
		return worldObj.provider.getAverageGroundLevel();
	}

}
