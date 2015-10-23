package de.katzenpapst.amunra.world.mapgen.newVillage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;






import cpw.mods.fml.common.FMLLog;
import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.world.mapgen.newVillage.populator.AbstractPopulator;
import de.katzenpapst.amunra.world.mapgen.newVillage.populator.SpawnEntity;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.ChunkProviderSpace;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class GridVillageStart {
	
	protected int chunkX;
	protected int chunkZ;
	
	protected Random rand;
	
	// coords relative to the
	protected int startX;
	// protected int startY;
	protected int startZ;
	
	protected StructureBoundingBox structBB;
	protected BlockMetaPair pathMaterial = ARBlocks.multiBlockDirt.getBlockMetaPair("basaltregolith"); 
	

	protected BlockMetaPair wallMaterial = ARBlocks.multiBlockRock.getBlockMetaPair("alucrate"); 
	protected BlockMetaPair floorMaterial = ARBlocks.multiBlockRock.getBlockMetaPair("smoothbasalt"); 
	protected BlockMetaPair fillMaterial = ARBlocks.multiBlockRock.getBlockMetaPair("basaltbrick"); 
	
	protected int numGridElements = 0;
	
	protected int gridSize = 9;
	
	protected int gridSideLength = 0;
	
	protected World worldObj;
	
	protected HashMap<Integer, GridVillageComponent> componentsByGrid;
	
	// protected LinkedList<AbstractPopulator> populators;
	
	protected HashMap<Long, AbstractPopulator> populators;
	
	/**
	 * Instantiates the thing, the coords in here should be the START point
	 * @param chunkX
	 * @param chunkZ
	 */
	public GridVillageStart(World world, int chunkX, int chunkZ, Random rand) {
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
		
		this.worldObj = world;
		
		this.rand = rand; 
		
		this.startX = this.rand.nextInt(16);
		this.startZ = this.rand.nextInt(16);
		
		int startBlockX = chunkX*16 + this.startX;
		int startBlockZ = chunkZ*16 + this.startZ;
		
		FMLLog.info("Generating the cross at x="+startBlockX+", z="+startBlockZ);
		
		componentsByGrid = new HashMap<Integer, GridVillageComponent>();
		
		populators = new HashMap<Long, AbstractPopulator>();//new LinkedList<AbstractPopulator>(); 
		/*
		numGridElements = 7;
		
		gridSideLength = (int) Math.ceil(Math.sqrt(numGridElements));
		
		// now the effective grid width is this.gridSize+3
		int effectiveGridSize = this.gridSize+3;
		
		int squareWidth = effectiveGridSize*gridSideLength;
		
		structBB = new StructureBoundingBox();
		structBB.minX = startBlockX - (int)Math.floor(squareWidth/2);
		structBB.maxX = startBlockX + (int)Math.ceil(squareWidth/2);
		structBB.minZ = startBlockZ - (int)Math.floor(squareWidth/2);
		structBB.maxZ = startBlockZ + (int)Math.ceil(squareWidth/2);
		structBB.minY = 0;
		structBB.maxY = 255;*/
	}
	
	public World getWorld() {
		return worldObj;
	}
	
	public void populate(World world, int chunkX, int chunkZ) {
		
		Iterator it = populators.entrySet().iterator();
	    while (it.hasNext()) {
	        HashMap.Entry<Long, AbstractPopulator> pair = (HashMap.Entry)it.next();
	        Long key = pair.getKey();
	        AbstractPopulator p = pair.getValue();
	        if(p.isInChunk(chunkX, chunkZ)) {
				if(!p.populate(world)) {
					FMLLog.info("Populator failed...");
				}

				it.remove(); // avoids a ConcurrentModificationException
			}
	        
	        //System.out.println(pair.getKey() + " = " + pair.getValue());
	    }
		
	    /*
		ListIterator itr = populators.listIterator();
		while(itr.hasNext()) {
			AbstractPopulator p = (AbstractPopulator) itr.next();
			if(p.isInChunk(chunkX, chunkZ)) {
				if(!p.populate(world)) {
					FMLLog.info("Populator failed...");
				}
				itr.remove();
			}
		}*/
	}
	
	public void addPopulator(AbstractPopulator p) {
		Long key = p.getPackedCoordinates();
		if(populators.containsKey(key)) {
			FMLLog.info("Cannot add populator for "+p.getX()+","+p.getY()+","+p.getZ());
			return;
		}
		// pack the coords
		populators.put(key, p);
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
	/*
	public void addComponent(GridVillageComponent component) {
		component.setParent(this);
		components.add(component);
	}*/
	
	public void setComponents(ArrayList components) {
		
		// byte should be enough for gridsize
		
		numGridElements = components.size();
		
		gridSideLength = (int) Math.ceil(Math.sqrt(numGridElements));
		
		// now the effective grid width is this.gridSize+3
		int effectiveGridSize = this.gridSize+3;
		
		int squareWidth = effectiveGridSize*gridSideLength;
		
		int startBlockX = chunkX*16 + this.startX;
		int startBlockZ = chunkZ*16 + this.startZ;
		
		// my own structBB
		structBB = new StructureBoundingBox();
		structBB.minX = startBlockX - (int)Math.floor(squareWidth/2);
		structBB.maxX = startBlockX + (int)Math.ceil(squareWidth/2);
		structBB.minZ = startBlockZ - (int)Math.floor(squareWidth/2);
		structBB.maxZ = startBlockZ + (int)Math.ceil(squareWidth/2);
		structBB.minY = 0;
		structBB.maxY = 255;
		
		// hack for dummy entries for shuffling
		Object dummyComponent = new Object();
		
		int totalGridElems = gridSideLength*gridSideLength;
		// pad the components
		for(int i=numGridElements;i<totalGridElems;i++) {
			components.add(dummyComponent);
		}
		
		Collections.shuffle(components, this.rand);
		
		byte gridX = 0;
		byte gridZ = 0;
		for(Object comp: components) {
			if(!(comp instanceof GridVillageComponent)) {
				continue;
			}
			GridVillageComponent vComp = ((GridVillageComponent)comp);
			int index = gridX + (gridZ << 8);
			
			StructureBoundingBox componentBox = new StructureBoundingBox(
					structBB.minX + effectiveGridSize*gridX + 2,
					structBB.minZ + effectiveGridSize*gridZ + 2,
					structBB.minX + effectiveGridSize*gridX + 1 + this.gridSize,
					structBB.minZ + effectiveGridSize*gridZ + 1 + this.gridSize
			);
			//
			//cmp.setCoordMode(this.rand.nextInt(4));
			vComp.setStructureBoundingBox(componentBox);
			vComp.setCoordMode(this.rand.nextInt(4));
			vComp.setParent(this);
			componentsByGrid.put(index, vComp);
			gridX++;
			if(gridX >= gridSideLength) {
				gridX = 0;
				gridZ++;
			}
		}
		
		
		
		
	}
	
	public BlockMetaPair getPathMaterial() {
		return pathMaterial;
	}

	public void setPathMaterial(BlockMetaPair pathMaterial) {
		this.pathMaterial = pathMaterial;
	}

	public BlockMetaPair getWallMaterial() {
		return wallMaterial;
	}

	public void setWallMaterial(BlockMetaPair wallMaterial) {
		this.wallMaterial = wallMaterial;
	}

	public BlockMetaPair getFloorMaterial() {
		return floorMaterial;
	}

	public void setFloorMaterial(BlockMetaPair floorMaterial) {
		this.floorMaterial = floorMaterial;
	}

	public BlockMetaPair getFillMaterial() {
		return fillMaterial;
	}

	public void setFillMaterial(BlockMetaPair fillMaterial) {
		this.fillMaterial = fillMaterial;
	}
	
	protected void drawStuffInGrid(int chunkX, int chunkZ, int gridX, int gridZ, Block[] arrayOfIDs, byte[] arrayOfMeta) {
		// now how do I calculate the grid's position?
		// I think it's
		int effectiveGridSize = this.gridSize+3;
		int testX = structBB.minX + effectiveGridSize*gridX + 2;
		int testZ = structBB.minZ + effectiveGridSize*gridZ + 2;
		
		
		// now try
		for(int x=0;x<this.gridSize;x++) {
			for(int z=0;z<this.gridSize;z++) {
				int relX = GridVillageComponent.abs2rel(testX+x, chunkX);
				int relZ = GridVillageComponent.abs2rel(testZ+z, chunkZ);
				placeBlockOnGround(arrayOfIDs, arrayOfMeta, relX, relZ, wallMaterial.getBlock(), wallMaterial.getMetadata());
			}
		}
	}
	
	protected void drawGrid(int chunkX, int chunkZ, Block[] arrayOfIDs, byte[] arrayOfMeta) {
		// hmmm how do I do this now?
		// length of the square
		int effectiveGridSize = this.gridSize+3;
		
		for(int x = structBB.minX; x < structBB.maxX;x++) {
			for(int z = structBB.minZ; z < structBB.maxZ; z++) {
				int testX = x - structBB.minX;
				int testZ = z - structBB.minZ;
				boolean drawX = false;
				boolean drawZ = false;
				
				if(testX != 0 && (testX % effectiveGridSize) == 0) {
					drawX = true;
				}
				if (testZ != 0 && (testZ % effectiveGridSize) == 0) {
					drawZ = true;
				}
				
				if(!drawX && !drawZ) {
					continue;
				}
				int relX = GridVillageComponent.abs2rel(x, chunkX);
				int relZ = GridVillageComponent.abs2rel(z, chunkZ);
				
				if(drawX && drawZ) {
					// crossing
					
					placeBlockOnGround(arrayOfIDs, arrayOfMeta, relX-1, relZ-1, pathMaterial.getBlock(), pathMaterial.getMetadata());
					placeBlockOnGround(arrayOfIDs, arrayOfMeta, relX,   relZ-1, pathMaterial.getBlock(), pathMaterial.getMetadata());
					placeBlockOnGround(arrayOfIDs, arrayOfMeta, relX+1, relZ-1, pathMaterial.getBlock(), pathMaterial.getMetadata());
					
					placeBlockOnGround(arrayOfIDs, arrayOfMeta, relX-1, relZ,   pathMaterial.getBlock(), pathMaterial.getMetadata());
					placeBlockOnGround(arrayOfIDs, arrayOfMeta, relX,   relZ,   pathMaterial.getBlock(), pathMaterial.getMetadata());
					placeBlockOnGround(arrayOfIDs, arrayOfMeta, relX+1, relZ,   pathMaterial.getBlock(), pathMaterial.getMetadata());
					
					placeBlockOnGround(arrayOfIDs, arrayOfMeta, relX-1, relZ+1, pathMaterial.getBlock(), pathMaterial.getMetadata());
					placeBlockOnGround(arrayOfIDs, arrayOfMeta, relX,   relZ+1, pathMaterial.getBlock(), pathMaterial.getMetadata());
					placeBlockOnGround(arrayOfIDs, arrayOfMeta, relX+1, relZ+1, pathMaterial.getBlock(), pathMaterial.getMetadata());
				} else if(drawX) {
					placeBlockOnGround(arrayOfIDs, arrayOfMeta, relX-1, relZ,   pathMaterial.getBlock(), pathMaterial.getMetadata());
					placeBlockOnGround(arrayOfIDs, arrayOfMeta, relX,   relZ,   pathMaterial.getBlock(), pathMaterial.getMetadata());
					placeBlockOnGround(arrayOfIDs, arrayOfMeta, relX+1, relZ,   pathMaterial.getBlock(), pathMaterial.getMetadata());
				} else if(drawZ) {
					placeBlockOnGround(arrayOfIDs, arrayOfMeta, relX, relZ-1,   pathMaterial.getBlock(), pathMaterial.getMetadata());
					placeBlockOnGround(arrayOfIDs, arrayOfMeta, relX, relZ,     pathMaterial.getBlock(), pathMaterial.getMetadata());
					placeBlockOnGround(arrayOfIDs, arrayOfMeta, relX, relZ+1,   pathMaterial.getBlock(), pathMaterial.getMetadata());
				}
			}
		}
	}
	
	/**
	 * Places a block into the topmost solid block
	 * 
	 * @param arrayOfIDs
	 * @param arrayOfMeta
	 * @param relX
	 * @param relZ
	 * @param block
	 * @param meta
	 */
	protected void placeBlockOnGround(Block[] arrayOfIDs, byte[] arrayOfMeta, int relX, int relZ, Block block, int meta) {
		if(relX < 0 || relX >= 16 || relZ < 0 || relZ >= 16) {
			return;
		}
		int y = GridVillageComponent.getHighestSolidBlock(arrayOfIDs, arrayOfMeta, relX, relZ);
		GridVillageComponent.placeBlockRel(arrayOfIDs, arrayOfMeta, relX, y-1, relZ, block, meta);
	}
	
	protected void drawGridComponents(int chunkX, int chunkZ, Block[] arrayOfIDs, byte[] arrayOfMeta) {
		
		int effectiveGridSize = this.gridSize+3;
		
		StructureBoundingBox chunkBox = new StructureBoundingBox(chunkX*16, chunkZ*16, chunkX*16+15, chunkZ*16+15); 
		
		for(int gridX = 0;gridX < gridSideLength;gridX++) {
			for(int gridZ = 0;gridZ < gridSideLength;gridZ++) {
				
				// check
			/*
				int minX = structBB.minX + effectiveGridSize*gridX + 2;
				int minZ = structBB.minZ + effectiveGridSize*gridZ + 2;
				
				int maxX = structBB.minX + effectiveGridSize*gridX + 2 + this.gridSize;
				int maxZ = structBB.minZ + effectiveGridSize*gridZ + 2 + this.gridSize;
				*/
				/*StructureBoundingBox componentBox = new StructureBoundingBox(
						structBB.minX + effectiveGridSize*gridX + 2,
						structBB.minZ + effectiveGridSize*gridZ + 2,
						structBB.minX + effectiveGridSize*gridX + 2 + this.gridSize,
						structBB.minZ + effectiveGridSize*gridZ + 2 + this.gridSize
				);*/
				
				int index = gridX + (gridZ << 8);
				
				if(!componentsByGrid.containsKey(index)) {
					continue;
				}
				
				GridVillageComponent curComp = componentsByGrid.get(index);
				
				if(!curComp.getStructureBoundingBox().intersectsWith(chunkBox)) {
					continue; // not in this chunk
				}
				
				curComp.generateChunk(chunkX, chunkZ, arrayOfIDs, arrayOfMeta);
				// now try
				/*for(int x=0;x<this.gridSize;x++) {
					for(int z=0;z<this.gridSize;z++) {
						int relX = GridVillage.abs2rel(testX+x, chunkX);
						int relZ = GridVillage.abs2rel(testZ+z, chunkZ);
						placeBlockOnGround(arrayOfIDs, arrayOfMeta, relX, relZ, pathWall.getBlock(), pathWall.getMetadata());
					}
				}*/
		
			}
		}
	}
	
	public boolean generateChunk(int chunkX, int chunkZ, Block[] arrayOfIDs, byte[] arrayOfMeta) {
		drawGrid(chunkX, chunkZ, arrayOfIDs, arrayOfMeta);
		
		drawGridComponents(chunkX, chunkZ, arrayOfIDs, arrayOfMeta);
		
		//drawStuffInGrid(chunkX, chunkZ, 0, 0, arrayOfIDs, arrayOfMeta);
		//drawStuffInGrid(chunkX, chunkZ, 1, 1, arrayOfIDs, arrayOfMeta);
		//drawStuffInGrid(chunkX, chunkZ, 2, 2, arrayOfIDs, arrayOfMeta);
		/*
		// for the first test, draw a cross
		int startBlockX = this.chunkX*16 + this.startX;
		int startBlockZ = this.chunkZ*16 + this.startZ;
		
		// X line
		for(int x = structBB.minX; x < structBB.maxX;x++) {
			int relX = GridVillage.abs2rel(x, chunkX);
			int relZ = GridVillage.abs2rel(startBlockZ, chunkZ);
			if(relX < 0 || relX >= 16 || relZ < 0 || relZ >= 16) {
				continue;
			}
			
			// 3 lines
			
			// get Y
			int y = GridVillage.getHighestSolidBlock(arrayOfIDs, arrayOfMeta, relX, relZ);
			if(y == -1) {
				continue; // ?
			}
			GridVillage.placeBlockRel(arrayOfIDs, arrayOfMeta, relX, y-1, relZ, pathMaterial.getBlock(), pathMaterial.getMetadata());
			
			y = GridVillage.getHighestSolidBlock(arrayOfIDs, arrayOfMeta, relX, relZ-1);
			if(y == -1) {
				continue; // ?
			}
			GridVillage.placeBlockRel(arrayOfIDs, arrayOfMeta, relX, y-1, relZ-1, pathMaterial.getBlock(), pathMaterial.getMetadata());
			
			y = GridVillage.getHighestSolidBlock(arrayOfIDs, arrayOfMeta, relX, relZ+1);
			if(y == -1) {
				continue; // ?
			}
			GridVillage.placeBlockRel(arrayOfIDs, arrayOfMeta, relX, y-1, relZ+1, pathMaterial.getBlock(), pathMaterial.getMetadata());
		}
		
		// Z line
		for(int z = structBB.minZ; z < structBB.maxZ; z++) {
			int relZ = GridVillage.abs2rel(z, chunkZ);
			int relX = GridVillage.abs2rel(startBlockX, chunkX);
			if(relX < 0 || relX >= 16 || relZ < 0 || relZ >= 16) {
				continue;
			}
			
			// get Y
			int y = GridVillage.getHighestSolidBlock(arrayOfIDs, arrayOfMeta, relX, relZ);
			if(y == -1) {
				continue; // ?
			}
			GridVillage.placeBlockRel(arrayOfIDs, arrayOfMeta, relX, y-1, relZ, pathMaterial.getBlock(), pathMaterial.getMetadata());
			
			y = GridVillage.getHighestSolidBlock(arrayOfIDs, arrayOfMeta, relX-1, relZ);
			if(y == -1) {
				continue; // ?
			}
			GridVillage.placeBlockRel(arrayOfIDs, arrayOfMeta, relX-1, y-1, relZ, pathMaterial.getBlock(), pathMaterial.getMetadata());
			
			y = GridVillage.getHighestSolidBlock(arrayOfIDs, arrayOfMeta, relX+1, relZ);
			if(y == -1) {
				continue; // ?
			}
			GridVillage.placeBlockRel(arrayOfIDs, arrayOfMeta, relX+1, y-1, relZ, pathMaterial.getBlock(), pathMaterial.getMetadata());
		}
		
		*/
		return true;
	}
}
