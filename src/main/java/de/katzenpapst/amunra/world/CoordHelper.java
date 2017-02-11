package de.katzenpapst.amunra.world;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.common.util.ForgeDirection;

public class CoordHelper {

    /**
     * Convert a block coordinate to the coordinate of the chunk it is in
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

    public static AxisAlignedBB cloneAABB(AxisAlignedBB box)
    {
        return AxisAlignedBB.getBoundingBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
    }




    /**
     * This should transform a ForgeDirection according to rotation metadata
     * @param dir
     * @param rotationMetadata	this should be ONLY the rotation metadata, ANDed and byteshifted, if necessary:
     *	 0
     * 2-+-3
     *   1
     * @return
     */
    public static ForgeDirection rotateForgeDirection(ForgeDirection dir, int rotationMetadata)
    {
        int dirOrdinal = dir.ordinal();
        if(dirOrdinal < 2 || dirOrdinal > 5) {
            return dir;
        }

        dirOrdinal = rotateForgeDirectionOrdinal(dirOrdinal, rotationMetadata);
        return ForgeDirection.getOrientation(dirOrdinal);
    }

    private static int rotateForgeDirectionOrdinal(int dirOrdinal, int rotationMeta) {
        switch(rotationMeta) {
        case 0:	// identity
            return dirOrdinal;
        case 1:	// rotate 180°
            switch(dirOrdinal) {
            case 2: //N
                return 3; // S
            case 3:	// S
                return 2; // N
            case 4: // W
                return 5; // E
            case 5:	// E
                return 4; //W
            }
            return -1;
        case 2: // rotate 270°
            switch(dirOrdinal) {
            case 2: //N
                return 4; // W
            case 3:	// S
                return 5; // E
            case 4: // W
                return 3; // S
            case 5:	// E
                return 2; //N
            }
            return -1;
        case 3: // rotate 90°
            switch(dirOrdinal) {
            case 2: //N
                return 5; // E
            case 3:	// S
                return 4; // W
            case 4: // W
                return 2; // N
            case 5:	// E
                return 3; //S
            }
            return -1;

        }
        return dirOrdinal;
    }




}
