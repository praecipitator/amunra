package de.katzenpapst.amunra.world.mapgen.newVillage;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import cpw.mods.fml.common.FMLLog;
import de.katzenpapst.amunra.mob.entity.EntityRobotVillager;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta;
import micdoodle8.mods.galacticraft.core.world.gen.dungeon.MapGenDungeon;

public class GridVillage extends MapGenBaseMeta {
	
	protected class ComponentEntry {
		public Class<? extends GridVillageComponent> clazz;
		public float probability;
		public int minAmount;
		public int maxAmount;
		
		public ComponentEntry(Class<? extends GridVillageComponent> clazz, float probability, int minAmount, int maxAmount) {
			this.clazz = clazz;
			this.probability = probability;
			this.minAmount = minAmount;
			this.maxAmount = maxAmount;
		}
	}
	
	protected ArrayList<ComponentEntry> components;
	
	protected int gridSize = 32;
	
	protected HashMap<Long, GridVillageStart> structureMap = new HashMap<Long, GridVillageStart>(); //Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(p_151538_2_, p_151538_3_)
	
	public void addComponentType(Class<? extends GridVillageComponent> clazz, float probability) {
		addComponentType(clazz, probability, 0, 0);
	}
	
	public void addComponentType(Class<? extends GridVillageComponent> clazz, float probability, int minAmount, int maxAmount) {
		ComponentEntry entry = new ComponentEntry(clazz, probability, minAmount, maxAmount);
		components.add(entry);
	}
	
	public GridVillage() {
		components = new ArrayList<ComponentEntry>(); 
	}
	
	public void populate(World world, int chunkX, int chunkZ) {
		this.worldObj = world;
        this.rand.setSeed(world.getSeed());
        final long r0 = this.rand.nextLong();
        final long r1 = this.rand.nextLong();

        for (int x0 = chunkX - this.range; x0 <= chunkX + this.range; ++x0)
        {
            for (int y0 = chunkZ - this.range; y0 <= chunkZ + this.range; ++y0)
            {
                final long randX = x0 * r0;
                final long randZ = y0 * r1;
                this.rand.setSeed(randX ^ randZ ^ world.getSeed());
                this.recursivePopulate(world, x0, y0, chunkX, chunkZ);
            }
        }
	}
	
	protected void recursivePopulate(World par1World, int xChunkCoord, int zChunkCoord, int origXChunkCoord, int origZChunkCoord) 
	{
		if (this.rand.nextInt(300) == 0) {
			Long key = Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(xChunkCoord, zChunkCoord));
			if(structureMap.containsKey(key)) {
				GridVillageStart start = structureMap.get(key);
				start.populate(par1World, origXChunkCoord, origZChunkCoord);
			} else {
				FMLLog.info("No village object for population for coords "+(xChunkCoord*16)+"/"+(zChunkCoord*16)+", that's weird...");
			}
		}
	}
	
	@Override
    protected void recursiveGenerate(World par1World, int xChunkCoord, int zChunkCoord, int origXChunkCoord, int origZChunkCoord, Block[] arrayOfIDs, byte[] arrayOfMeta)
    {
        if (this.rand.nextInt(300) == 0) {// seems weird, and WAY too common. 
        	makeVillage(par1World, xChunkCoord, zChunkCoord, origXChunkCoord, origZChunkCoord, arrayOfIDs, arrayOfMeta);
            
        }
    }
	
	protected void makeVillage(World par1World, int xChunkCoord, int zChunkCoord, int origXChunkCoord, int origZChunkCoord, Block[] arrayOfIDs, byte[] arrayOfMeta) {
		Long key = Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(xChunkCoord, zChunkCoord));
		GridVillageStart start = null;
		if(!structureMap.containsKey(key)) {
			start = createNewStart(xChunkCoord, zChunkCoord);//new GridVillageStart(xChunkCoord, zChunkCoord, this.rand);
			structureMap.put(key, start);
		} else {
			start = structureMap.get(key);
		}
		start.generateChunk(origXChunkCoord, origZChunkCoord, arrayOfIDs, arrayOfMeta);
		
	}
	
	private GridVillageStart createNewStart(int xChunkCoord, int zChunkCoord) {
		GridVillageStart start = new GridVillageStart(this.worldObj, xChunkCoord, zChunkCoord, this.rand);
		ArrayList compList = new ArrayList();
			// now prepare the actual component list
		for(ComponentEntry entry: components) {
			try {
				// generate the minimum amount
				GridVillageComponent cmp = null;
				int nrGenerated = 0;
				boolean shouldGenerateMore = true;
				/*
				for(int i=0;i<entry.minAmount;i++) {
					cmp = entry.clazz.getConstructor().newInstance();
					start.addComponent(cmp);
					nrGenerated = i;
				}*/
				// now generate the extra
				while(shouldGenerateMore) {
					shouldGenerateMore = false;
					if(entry.minAmount > 0 && nrGenerated < entry.minAmount) {
						shouldGenerateMore = true;
					} else {
						if(this.rand.nextFloat() < entry.probability) {
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
		start.setComponents(compList);
		return start;
	}
	
	
	public static int getHighestSolidBlock(Block[] blocks, byte[] metas, int relX, int relZ) {
		
		for(int y=255;y>=0;y--) {
			int index = getIndex(relX, y, relZ);
			Block curBlock = blocks[index];
			if(curBlock == null) {
				continue;
			}
			int meta = metas[index];
			if(curBlock.getMaterial().blocksMovement() && curBlock.getMaterial() != Material.leaves) {
				return y+1;
			}
		}
		return -1;
	}
	
	/**
	 * Places a block into the arrays using coordinates relative to the current chunk 
	 * 
	 * @param blocks
	 * @param metas
	 * @param x
	 * @param y
	 * @param z
	 * @param id
	 * @param meta
	 * @return
	 */
	public static boolean placeBlockRel(Block[] blocks, byte[] metas, int x, int y, int z, Block id, int meta)
    {
        if (x < 0 || x >= 16 || z < 0 || z >= 16)
        {
            return false;
        }
        final int index = getIndex(x, y, z);
        blocks[index] = id;
        metas[index] = (byte) meta;
        
        return true;
    }
	
	public static boolean placeBlockRel(Block[] blocks, byte[] metas, int x, int y, int z, BlockMetaPair block)
    {
        if (x < 0 || x >= 16 || z < 0 || z >= 16)
        {
            return false;
        }
        final int index = getIndex(x, y, z);
        blocks[index] = block.getBlock();
        metas[index] = block.getMetadata();
        
        return true;
    }
	
	/**
	 * Converts an absolute coordinate to relative. 
	 * Does not validate the result
	 * 
	 * @param absCoord
	 * @param chunkCoord
	 * @return
	 */
	public static int abs2rel(int absCoord, int chunkCoord) {
		return absCoord - chunkCoord * 16;
	}
	
	/**
	 * Converts a relative chunk coordinate to an absolute one
	 * Does not validate the input
	 * 
	 * @param relCoord
	 * @param chunkCoord
	 * @return
	 */
	public static int rel2abs(int relCoord, int chunkCoord) {
		return relCoord + chunkCoord * 16;
	}
	
	/**
	 * Places a block into the arrays using absolute coordinates+coordinates of the current chunk
	 * 
	 * @param blocks
	 * @param metas
	 * @param x
	 * @param y
	 * @param z
	 * @param cx
	 * @param cz
	 * @param id
	 * @param meta
	 * @return
	 */
	public static boolean placeBlockAbs(Block[] blocks, byte[] metas, int x, int y, int z, int cx, int cz, Block id, int meta)
    {
        /*cx *= 16;
        cz *= 16;
        x -= cx;
        z -= cz;*/
        return placeBlockRel(blocks, metas, abs2rel(x, cx), y, abs2rel(z,cz), id, meta);
    }

    public static int getIndex(int x, int y, int z)
    {
        return (x * 16 + z) * 256 + y;
    }
    
    public static int getAverageGroundLevel(Block[] blocks, byte[] metas, StructureBoundingBox totalBB, StructureBoundingBox chunkBB, int minimum)
    {
        int sum = 0;
        int total = 0;
        
        int chunkX = chunkBB.minX / 16;
        int chunkZ = chunkBB.minZ / 16;

        for (int z = totalBB.minZ; z <= totalBB.maxZ; ++z)
        {
            for (int x = totalBB.minX; x <= totalBB.maxX; ++x)
            {
                if (chunkBB.isVecInside(x, 64, z))
                {
                	sum += Math.max(getHighestSolidBlock(blocks, metas, abs2rel(x, chunkX), abs2rel(z, chunkZ)), minimum);
                	
                    //sum += Math.max(par1World.getTopSolidOrLiquidBlock(x, z), par1World.provider.getAverageGroundLevel());
                    ++total;
                }
            }
        }

        if (total == 0)
        {
            return -1;
        }
        else
        {
            return sum / total;
        }
    }
}
