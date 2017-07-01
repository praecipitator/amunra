package de.katzenpapst.amunra.world.mapgen;

import de.katzenpapst.amunra.block.BlockMetaPairHashable;
import de.katzenpapst.amunra.helper.CoordHelper;
import de.katzenpapst.amunra.world.mapgen.populator.SetSignText;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3i;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.structure.StructureBoundingBox;

abstract public class BaseStructureComponent {
    protected int                  groundLevel = -1;
    protected BaseStructureStart   parent      = null;

    protected int                  coordMode   = 0;
    protected StructureBoundingBox structBB;

    public BaseStructureComponent() {
        // TODO Auto-generated constructor stub
    }

    public int getGroundLevel() {
        return groundLevel;
    }

    public void setStructureBoundingBox(StructureBoundingBox structBB) {
        this.structBB = structBB;
    }

    public StructureBoundingBox getStructureBoundingBox() {
        return this.structBB;
    }

    public boolean generateChunk(int chunkX, int chunkZ, ChunkPrimer primer) {
        return true;
    }

    public void setParent(BaseStructureStart parent) {
        this.parent = parent;
    }

    public void setCoordMode(int coordMode) {
        this.coordMode = coordMode;
    }

    /**
     * "Spawns" an entity. Does not check
     *
     * @param entityToSpawn
     * @param x
     * @param z
     */
    /*
     * protected void spawnEntity(Class<? extends EntityLiving> entityToSpawn,
     * int x, int z) { EntityLiving ent = null; try { ent =
     * entityToSpawn.getConstructor(World.class).newInstance(this.parent.
     * getWorld()); } catch (Throwable e) { e.printStackTrace(); return; }
     *
     * ent.onSpawnWithEgg(null);// NO IDEA int xOffset = getXWithOffset(x, z);
     * //y = getYWithOffset(y); int zOffset = getZWithOffset(x, z);
     * this.parent.spawnLater(ent, xOffset, groundLevel, zOffset); }
     */

    protected int translateX(int x, int z) {
        switch (this.coordMode) {
        case 0:
        case 2:
            return x; // keep them as-is
        case 1:
            // translate z to "relative to bb", then do what getXWithOffset did
            return this.structBB.maxX - (z - this.structBB.minZ);
        case 3:
            // similar to above
            return this.structBB.minX + (z - this.structBB.minZ);
        }

        return x;
    }

    protected int translateZ(int x, int z) {
        switch (this.coordMode) {
        case 0:
            return z;
        case 1:
        case 3:
            return this.structBB.minZ + (x - this.structBB.minX);
        case 2:
            return this.structBB.maxZ - (z - this.structBB.minZ);
        default:
            return z;
        }
    }

    protected int getHighestSolidBlockInBB(ChunkPrimer primer, int chunkX, int chunkZ, int x, int z) {
        int xOffset = getXWithOffset(x, z);
        // y = getYWithOffset(y);
        int zOffset = getZWithOffset(x, z);

        int relX = CoordHelper.abs2rel(xOffset, chunkX);
        int relZ = CoordHelper.abs2rel(zOffset, chunkZ);
        if (relX < 0 || relX >= 16 || relZ < 0 || relZ >= 16) {
            return -1;
        }

        return getHighestSolidBlock(primer, relX, relZ);
    }

    /**
     * Fill an area with blocks
     *
     * @param blocks
     * @param metas
     * @param chunkBB
     * @param box
     * @param block
     *            / protected boolean drawArea(Block[] blocks, byte[] metas,
     *            StructureBoundingBox chunkBB, StructureBoundingBox box,
     *            BlockMetaPair block) {
     *
     *            StructureBoundingBox actualBox =
     *            intersectBoundingBoxes(chunkBB, box); if(actualBox == null) {
     *            return false; } for(int x=actualBox.minX; x<=actualBox.maxX;
     *            x++) { for(int y=actualBox.minY; y<=actualBox.maxY; y++) {
     *            for(int z=actualBox.minZ; z<=actualBox.maxZ; z++) { int
     *            xOffset = getXWithOffset(x, z); int zOffset =
     *            getZWithOffset(x, z); int relX = CoordHelper.abs2rel(xOffset);
     *            int relZ = CoordHelper.abs2rel(zOffset); placeBlockRel(blocks,
     *            metas, relX, y, relZ, block); } } }
     *
     *            return true; }
     */

    protected void fillBox(ChunkPrimer primer, StructureBoundingBox box, Block block, byte meta) {

        this.fillBox(primer, box, new BlockMetaPairHashable(block, meta));
    }

    protected void fillBox(ChunkPrimer primer, StructureBoundingBox box, BlockMetaPairHashable bmp) {

        for (int x = box.minX; x <= box.maxX; x++) {
            for (int y = box.minY; y <= box.maxY; y++) {
                for (int z = box.minZ; z <= box.maxZ; z++) {
                    int chunkX = CoordHelper.blockToChunk(x);
                    int chunkZ = CoordHelper.blockToChunk(z);
                    placeBlockAbs(primer, new BlockPos(x, y, z), chunkX, chunkZ, bmp);
                }
            }
        }
    }

    protected void fillBox(ChunkPrimer primer, StructureBoundingBox box, BlockMetaPair bmp) {
        this.fillBox(primer, box, new BlockMetaPairHashable(bmp));
    }

    public static StructureBoundingBox intersectBoundingBoxesXZ(StructureBoundingBox box1, StructureBoundingBox box2) {
        StructureBoundingBox result = new StructureBoundingBox();

        result.minX = Math.max(box1.minX, box2.minX);
        result.minZ = Math.max(box1.minZ, box2.minZ);

        result.maxX = Math.min(box1.maxX, box2.maxX);
        result.maxZ = Math.min(box1.maxZ, box2.maxZ);

        if (result.minX > result.maxX || result.minZ > result.maxZ) {
            return null;
        }

        return result;
    }

    public static StructureBoundingBox intersectBoundingBoxes(StructureBoundingBox box1, StructureBoundingBox box2) {
        StructureBoundingBox result = new StructureBoundingBox();

        result.minX = Math.max(box1.minX, box2.minX);
        result.minY = Math.max(box1.minY, box2.minY);
        result.minZ = Math.max(box1.minZ, box2.minZ);

        result.maxX = Math.min(box1.maxX, box2.maxX);
        result.maxY = Math.min(box1.maxY, box2.maxY);
        result.maxZ = Math.min(box1.maxZ, box2.maxZ);

        if (result.minX > result.maxX || result.minY > result.maxY || result.minZ > result.maxZ) {
            return null;
        }

        return result;
    }

    protected boolean placeBlockRel2BB(ChunkPrimer primer, int chunkX, int chunkZ, BlockPos pos, BlockMetaPair block) {
        int xOffset = getXWithOffset(pos.getX(), pos.getZ());
        int zOffset = getZWithOffset(pos.getX(), pos.getZ());

        int relX = CoordHelper.abs2rel(xOffset, chunkX);
        int relZ = CoordHelper.abs2rel(zOffset, chunkZ);
        /*
         * if(relX < 0 || relX >= 16 || relZ < 0 || relZ >= 16) { return false;
         * }
         */
        return placeBlockRel(primer, new BlockPos(relX, pos.getY(), relZ), block);
    }

    protected boolean
            placeBlockRel2BB(ChunkPrimer primer, int chunkX, int chunkZ, BlockPos pos, Block block, int meta) {
        return placeBlockRel2BB(primer, chunkX, chunkZ, pos, new BlockMetaPairHashable(block, meta));
    }

    protected BlockMetaPair getBlockRel2BB(ChunkPrimer primer, int chunkX, int chunkZ, BlockPos pos) {
        int xOffset = getXWithOffset(pos.getX(), pos.getZ());
        int zOffset = getZWithOffset(pos.getX(), pos.getZ());

        int relX = CoordHelper.abs2rel(xOffset, chunkX);
        int relZ = CoordHelper.abs2rel(zOffset, chunkZ);

        return getBlockRel(primer, new BlockPos(relX, pos.getY(), relZ));
    }

    protected int getXWithOffset(int x, int z) {
        switch (this.coordMode) {
        case 0:
        case 2:
            return this.structBB.minX + x;
        case 1:
            return this.structBB.maxX - z;
        case 3:
            return this.structBB.minX + z;
        default:
            return x;
        }
    }

    protected int getZWithOffset(int x, int z) {
        switch (this.coordMode) {
        case 0:
            return this.structBB.minZ + z;
        case 1:
        case 3:
            return this.structBB.minZ + x;
        case 2:
            return this.structBB.maxZ - z;
        default:
            return z;
        }
    }

    protected BlockPos getPosWithOffset(BlockPos in) {
        return new BlockPos(getXWithOffset(in.getX(), in.getZ()), in.getY(), getZWithOffset(in.getX(), in.getZ()));
    }

    protected void placeStandingSign(ChunkPrimer primer, int chunkX, int chunkZ, BlockPos pos, String text) {

        if (placeBlockRel2BB(primer, chunkX, chunkZ, pos, new BlockMetaPair(Blocks.standing_sign, (byte) 0))) {
            int xOffset = getXWithOffset(pos.getX(), pos.getZ());
            // y = getYWithOffset(y);
            int zOffset = getZWithOffset(pos.getX(), pos.getZ());
            SetSignText sst = new SetSignText(new BlockPos(xOffset, pos.getY(), zOffset), text);
            this.parent.addPopulator(sst);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////// STATIC HELPERS
    //////////////////////////////////////////////////////////////////////////////////////////////////// //////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * For doors, in a sense, and furnaces
     *
     * 1 0-+-2 3
     *
     * @param unrotated
     * @param coordMode
     * @return
     */
    public static int rotateDoorlikeMetadata(int unrotated, int coordMode) {
        return rotateUniversalMetadata(unrotated, coordMode, 1, 3, 2, 0);
    }

    public static int rotateTorchMetadata(int unrotated, int coordMode) {
        // error with coordMode=1, everything is just the wrong way round

        return rotateStairlikeMetadata(unrotated - 1, coordMode) + 1;
    }

    /**
     * This should work for stairs 0 = E 1 = W 2 = S 3 = N for torches, add +1;
     * 0 means nothing and 5 means "on the ground", and the interpretation is
     * "torch FACING dir"
     *
     * 3 1-+-0 2
     *
     *
     * @param unrotated
     * @param coordMode
     * @return
     */
    public static int rotateStairlikeMetadata(int unrotated, int coordMode) {
        return rotateUniversalMetadata(unrotated, coordMode, 3, 2, 0, 1);
    }

    /**
     * Universal function for metadata rotation, based on what I found using
     * trial&error with torches
     *
     * @param unrotated
     * @param coordMode
     * @param n
     * @param s
     * @param e
     * @param w
     * @return
     *
     *         n w-+-e s
     */
    public static int rotateUniversalMetadata(int unrotated, int coordMode, int n, int s, int e, int w) {
        switch (coordMode) {
        /*
         * case 0: return unrotated;
         */
        case 1:
            if (unrotated == n) return e;
            if (unrotated == e) return s;
            if (unrotated == w) return n;
            if (unrotated == s) return w;
            break;
        case 2:
            if (unrotated == n) return s;
            if (unrotated == s) return n;
            break; // unrotated will be returned anyway
        case 3:
            if (unrotated == e) return s;
            if (unrotated == w) return n;
            if (unrotated == s) return e;
            if (unrotated == n) return w;
            break;
        }
        return unrotated;
    }

    /**
     * Rotates metadata for the 2 4-+-5 3 model, aka rotateStandardMetadata +2
     *
     * @param unrotated
     * @param coordMode
     * @param offset
     * @return
     */
    public static int rotatePistonlikeMetadata(int unrotated, int coordMode) {
        return rotateStandardMetadata(unrotated - 2, coordMode) + 2;
    }

    /**
     * Rotates the metadata which most things seem to use: 0 2-+-3 1 This should
     * work for solar collectors and trapdoors, but in a reversed non-intuitive
     * way
     *
     * @param unrotated
     * @param coordMode
     * @return
     */
    public static int rotateStandardMetadata(int unrotated, int coordMode) {
        return rotateUniversalMetadata(unrotated, coordMode, 0, 1, 3, 2);
    }

    public static int getAverageGroundLevel(
            ChunkPrimer primer,
            StructureBoundingBox totalBB,
            StructureBoundingBox chunkBB,
            int minimum) {
        int sum = 0;
        int total = 0;

        int chunkX = CoordHelper.blockToChunk(chunkBB.minX);// chunkBB.minX /
                                                            // 16;
        int chunkZ = CoordHelper.blockToChunk(chunkBB.minZ);// chunkBB.minZ /
                                                            // 16;

        for (int z = totalBB.minZ; z <= totalBB.maxZ; ++z) {
            for (int x = totalBB.minX; x <= totalBB.maxX; ++x) {
                if (chunkBB.isVecInside(new Vec3i(x, 64, z))) {
                    sum += Math.max(
                            getHighestSolidBlock(
                                    primer,
                                    CoordHelper.abs2rel(x, chunkX),
                                    CoordHelper.abs2rel(z, chunkZ)
                            ),
                            minimum
                    );

                    // sum += Math.max(par1World.getTopSolidOrLiquidBlock(x, z),
                    // par1World.provider.getAverageGroundLevel());
                    ++total;
                }
            }
        }

        if (total == 0) {
            return -1;
        } else {
            return sum / total;
        }
    }

    /**
     * Get highest block in a column, chunk-relative coordinates
     *
     * @param blocks
     * @param metas
     * @param relX
     * @param relZ
     * @return
     */
    public static int getHighestSolidBlock(ChunkPrimer primer, int relX, int relZ) {

        for (int y = 255; y >= 0; y--) {
            int index = getIndex(relX, y, relZ);
            IBlockState curState = primer.getBlockState(index);
            // Block curBlock = blocks[index];
            if (curState == null) {
                continue;
            }
            // int meta = metas[index];
            if (curState.getBlock().getMaterial().blocksMovement()
                    && curState.getBlock().getMaterial() != Material.leaves) {
                return y + 1;
            }
        }
        return -1;
    }

    /**
     * Get specific block in a column, chunk-relative coordinates
     *
     * @param blocks
     * @param metas
     * @param relX
     * @param relZ
     * @param block
     * @param meta
     * @return
     */
    public static int getHighestSpecificBlock(ChunkPrimer primer, int relX, int relZ, Block block, byte meta) {

        for (int y = 255; y >= 0; y--) {
            int index = getIndex(relX, y, relZ);

            IBlockState state = primer.getBlockState(index);

            if (state.getBlock() == block && state.getBlock().getMetaFromState(state) == meta) {
                return y;
            }
        }
        return -1;
    }

    /**
     * Places a block into the arrays using coordinates relative to the current
     * chunk
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
    public static boolean placeBlockRel(ChunkPrimer primer, BlockPos pos, Block id, int meta) {
        return placeBlockRel(primer, pos, new BlockMetaPairHashable(id, meta));
    }

    public static boolean placeBlockRel(ChunkPrimer primer, BlockPos pos, BlockMetaPair block) {
        if (block instanceof BlockMetaPairHashable) {
            return placeBlockRel(primer, pos, (BlockMetaPairHashable) block);
        }
        return placeBlockRel(primer, pos, new BlockMetaPairHashable(block));
    }

    public static boolean placeBlockRel(ChunkPrimer primer, BlockPos pos, BlockMetaPairHashable block) {
        if (!isInChunk(pos)) {
            return false;
        }
        final int index = getIndex(pos);

        primer.setBlockState(index, block.getBlockState());
        return true;
    }

    public static boolean isInChunk(BlockPos pos) {
        if (pos.getX() < 0 || pos.getX() >= 16 || pos.getZ() < 0 || pos.getZ() >= 16) {
            return false;
        }
        return false;
    }

    public static BlockMetaPairHashable getBlockRel(ChunkPrimer primer, BlockPos pos) {
        if (!isInChunk(pos)) {
            return null;
        }
        final int index = getIndex(pos);

        return new BlockMetaPairHashable(primer.getBlockState(index));
    }

    /**
     * Places a block into the arrays using absolute coordinates+coordinates of
     * the current chunk. If the coordinates are not inside the given chunk,
     * nothing happens. Block/meta version
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
    public static boolean placeBlockAbs(ChunkPrimer primer, BlockPos pos, int cx, int cz, Block id, int meta) {
        return placeBlockRel(primer, CoordHelper.abs2rel(pos, new BlockPos(cx, 0, cz)), id, meta);
    }

    /**
     * Places a block into the arrays using absolute coordinates+coordinates of
     * the current chunk. If the coordinates are not inside the given chunk,
     * nothing happens. BlockMetaPair version
     *
     * @param blocks
     * @param metas
     * @param x
     * @param y
     * @param z
     * @param cx
     * @param cz
     * @param block
     * @return
     */
    public static boolean placeBlockAbs(ChunkPrimer primer, BlockPos pos, int cx, int cz, BlockMetaPair block) {
        return placeBlockRel(primer, CoordHelper.abs2rel(pos, new BlockPos(cx, 0, cz)), block);
    }

    /**
     * Places a block into the arrays using absolute coordinates. Assumes the
     * chunk the coordinates are in is to be edited. BlockMetaPair version
     *
     * @param blocks
     * @param metas
     * @param x
     * @param y
     * @param z
     * @param block
     * @return
     */
    public static boolean placeBlockAbs(ChunkPrimer primer, BlockPos pos, BlockMetaPair block) {
        return placeBlockRel(primer, CoordHelper.abs2rel(pos), block);
    }

    /**
     * Places a block into the arrays using absolute coordinates. Assumes the
     * chunk the coordinates are in is to be edited. Block/meta version
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
    public static boolean placeBlockAbs(ChunkPrimer primer, BlockPos pos, Block id, int meta) {
        return placeBlockRel(primer, CoordHelper.abs2rel(pos), id, meta);
    }

    /**
     * Converts coordinates to the index as required for the arrays
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public static int getIndex(int x, int y, int z) {
        return (x * 16 + z) * 256 + y;
    }

    public static int getIndex(BlockPos pos) {
        return getIndex(pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * lerp
     *
     * @param d1
     * @param d2
     * @param t
     * @return
     */
    public double lerp(double d1, double d2, double t) {
        if (t < 0.0) {
            return d1;
        } else if (t > 1.0) {
            return d2;
        } else {
            return d1 + (d2 - d1) * t;
        }
    }

}
