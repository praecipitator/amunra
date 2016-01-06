package de.katzenpapst.amunra.world;

import net.minecraft.world.gen.structure.StructureBoundingBox;

public class CoordHelper {

	/**
	 * Convert a block coordinate to the coordinate of the block it is in
	 *
	 * @param blockCoord
	 * @return
	 */
	public static int blockToChunk(int blockCoord) {
		return blockCoord >> 4;
	}

	/**
	 * Get the coordinate of the minimum block of a chunk
	 * @param chunkCoord
	 * @return
	 */
	public static int chunkToMinBlock(int chunkCoord) {
		return chunkCoord << 4;
	}

	/**
	 * Get the coordinate of the maximum block of a chunk
	 * @param chunkCoord
	 * @return
	 */
	public static int chunkToMaxBlock(int chunkCoord) {
		return ((chunkCoord+1) << 4)-1;
	}

	public static StructureBoundingBox getChunkBB(int chunkX, int chunkZ) {
		return new StructureBoundingBox(
				chunkToMinBlock(chunkX),
				chunkToMinBlock(chunkZ),
				chunkToMaxBlock(chunkX),
				chunkToMaxBlock(chunkZ));
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
		return absCoord - chunkToMinBlock(chunkCoord);
	}

	public static int abs2rel(int absCoord) {
		int chunkCoord = CoordHelper.blockToChunk(absCoord);
		return absCoord - chunkToMinBlock(chunkCoord);
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
		return relCoord + chunkToMinBlock(chunkCoord);
	}

	/**
	 * Converts the coordinates to the index for a blocks/metas array
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public static int getIndex(int x, int y, int z)
    {
        return (x * 16 + z) * 256 + y;
    }


}
