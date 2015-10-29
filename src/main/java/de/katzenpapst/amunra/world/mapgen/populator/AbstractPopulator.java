package de.katzenpapst.amunra.world.mapgen.populator;

import de.katzenpapst.amunra.world.CoordHelper;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

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

		StructureBoundingBox box = CoordHelper.getChunkBB(chunkX, chunkZ);

		return box.isVecInside(x, y, z);

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
