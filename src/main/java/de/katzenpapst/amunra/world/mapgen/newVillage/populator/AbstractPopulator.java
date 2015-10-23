package de.katzenpapst.amunra.world.mapgen.newVillage.populator;

import net.minecraft.world.World;

abstract public class AbstractPopulator {
	public int x;
	public int y;
	public int z;
	
	public abstract boolean populate(World world);
	
	public boolean isInChunk(int chunkX, int chunkZ) {
		int chunkCoordX = chunkX*16;
		int chunkCoordZ = chunkZ*16;
		
		int maxChunkX = chunkCoordX+16;
		int maxChunkZ = chunkCoordZ+16;
		
		return (chunkCoordX <= x && x < maxChunkX && chunkCoordZ <= z && z < maxChunkZ);
	}
}
