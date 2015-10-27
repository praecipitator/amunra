package de.katzenpapst.amunra.world.mapgen.populator;

import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import net.minecraft.world.World;

abstract public class AbstractPopulator {
	protected int x;
	protected int y;
	protected int z;

	public abstract boolean populate(World world);

	public AbstractPopulator(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public boolean isInChunk(int chunkX, int chunkZ) {
		int chunkCoordX = chunkX*16;
		int chunkCoordZ = chunkZ*16;

		int maxChunkX = chunkCoordX+16;
		int maxChunkZ = chunkCoordZ+16;



		return (chunkCoordX <= x && x < maxChunkX && chunkCoordZ <= z && z < maxChunkZ);
	}

	public BlockVec3 getBlockVec3() {
		return new BlockVec3(x, y, z);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}
}
