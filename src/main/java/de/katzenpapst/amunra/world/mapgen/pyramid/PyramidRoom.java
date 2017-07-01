package de.katzenpapst.amunra.world.mapgen.pyramid;

import de.katzenpapst.amunra.helper.CoordHelper;
import de.katzenpapst.amunra.world.mapgen.BaseStructureComponent;
import de.katzenpapst.amunra.world.mapgen.populator.TouchBlock;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class PyramidRoom extends BaseStructureComponent {

    protected StructureBoundingBox entranceBB;
    protected StructureBoundingBox roomBB;

    protected boolean placeGlowstoneInEdges = true;

    private boolean roomHeightFixed = false;

    protected int floorLevel;


    public void setBoundingBoxes(StructureBoundingBox entranceBB, StructureBoundingBox roomBB) {
        this.entranceBB = entranceBB;
        this.roomBB 	= roomBB;

        StructureBoundingBox totalBox = new StructureBoundingBox(roomBB);
        totalBox.expandTo(entranceBB);
        this.setStructureBoundingBox(totalBox);
    }

    public StructureBoundingBox getEntranceBB() {
        return entranceBB;
    }


    @Override
    public boolean generateChunk(int chunkX, int chunkZ, ChunkPrimer primer) {

        StructureBoundingBox chunkBB = CoordHelper.getChunkBB(chunkX, chunkZ);

        BlockMetaPair floorMat = ((Pyramid) this.parent).getFloorMaterial();

        //StructureBoundingBox myBB = new StructureBoundingBox(roomBB);
        //int groundLevel = this.parent.getGroundLevel()+6;
        floorLevel = this.parent.getGroundLevel()+7;
        if(!roomHeightFixed) {
            roomBB.minY += floorLevel;
            roomBB.maxY += floorLevel;
            roomHeightFixed = true;
        }
        StructureBoundingBox actualRoomBB = this.intersectBoundingBoxes(chunkBB, roomBB);
        if(actualRoomBB != null) {
            //fillBox(arrayOfIDs, arrayOfMeta, actualRoomBB, Blocks.air, (byte) 0);
            for(int x=actualRoomBB.minX; x<=actualRoomBB.maxX; x++) {
                for(int y=actualRoomBB.minY-1; y<=actualRoomBB.maxY; y++) {
                    for(int z=actualRoomBB.minZ; z<=actualRoomBB.maxZ; z++) {
                        if(y >= actualRoomBB.minY) {

                            placeBlockAbs(primer, new BlockPos(x, y, z), chunkX, chunkZ, Blocks.air, (byte) 0);

                        } else {
                            placeBlockAbs(primer, new BlockPos(x, y, z), chunkX, chunkZ, floorMat.getBlock(), floorMat.getMetadata());
                        }
                    }
                }
            }
        }


        entranceBB.minY = roomBB.minY;
        entranceBB.maxY = entranceBB.minY+3;

        makeEntrance(primer, chunkBB, chunkX, chunkZ, floorMat);

        if(this.placeGlowstoneInEdges) {
            this.drawCornerColumns(actualRoomBB.minY, actualRoomBB.maxY, chunkX, chunkZ, primer);
        }

        return true;
    }

    protected void drawCornerColumns(int yMin, int yMax, int chunkX, int chunkZ, ChunkPrimer primer) {

        for (int y=yMin; y<=yMax; y++) {



            if(placeBlockAbs(primer, new BlockPos(roomBB.minX, y, roomBB.minZ), chunkX, chunkZ, Blocks.glowstone, (byte) 0)) {
                if(y == yMin) {
                    // trigger the populator
                    this.parent.addPopulator(new TouchBlock(new BlockPos(roomBB.minX, y, roomBB.minZ)));
                }
            }

            if(placeBlockAbs(primer, new BlockPos(roomBB.maxX, y, roomBB.minZ), chunkX, chunkZ, Blocks.glowstone, (byte) 0)) {
                if(y == yMin) {
                    // trigger the populator
                    this.parent.addPopulator(new TouchBlock(new BlockPos(roomBB.maxX, y, roomBB.minZ)));
                }
            }

            if(placeBlockAbs(primer, new BlockPos(roomBB.minX, y, roomBB.maxZ), chunkX, chunkZ, Blocks.glowstone, (byte) 0)) {
                if(y == yMin) {
                    // trigger the populator
                    this.parent.addPopulator(new TouchBlock(new BlockPos(roomBB.minX, y, roomBB.maxZ)));
                }
            }

            if(placeBlockAbs(primer, new BlockPos(roomBB.maxX, y, roomBB.maxZ), chunkX, chunkZ, Blocks.glowstone, (byte) 0)) {
                if(y == yMin) {
                    // trigger the populator
                    this.parent.addPopulator(new TouchBlock(new BlockPos(roomBB.maxX, y, roomBB.maxZ)));
                }
            }
        }

    }


    protected void makeEntrance(ChunkPrimer primer, StructureBoundingBox chunkBB,  int chunkX, int chunkZ, BlockMetaPair floorMat) {
        StructureBoundingBox entrBoxIntersect = this.intersectBoundingBoxes(entranceBB, chunkBB);

        if(entrBoxIntersect  != null) {
            //fillBox(arrayOfIDs, arrayOfMeta, entrBoxIntersect, Blocks.air, (byte) 0);
            for(int x=entrBoxIntersect.minX; x<=entrBoxIntersect.maxX; x++) {
                for(int y=entrBoxIntersect.minY-1; y<=entrBoxIntersect.maxY; y++) {
                    for(int z=entrBoxIntersect.minZ; z<=entrBoxIntersect.maxZ; z++) {
                        BlockPos pos = new BlockPos(x, y, z);
                        if(y >= entrBoxIntersect.minY) {
                            placeBlockAbs(primer, pos, chunkX, chunkZ, Blocks.air, (byte) 0);
                        } else {
                            placeBlockAbs(primer, pos, chunkX, chunkZ, floorMat.getBlock(), floorMat.getMetadata());
                        }
                    }
                }
            }
        }
    }


}
