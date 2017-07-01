package de.katzenpapst.amunra.world.mapgen.pyramid;

import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.ChunkPrimer;

public class PitRoom extends PyramidRoom {

    protected int pitSize = 7;

    @Override
    public boolean generateChunk(int chunkX, int chunkZ, ChunkPrimer primer) {

        super.generateChunk(chunkX, chunkZ, primer);

        int size = (pitSize-1)/2;

        BlockMetaPair floorMat = ((Pyramid) this.parent).getFloorMaterial();

        BlockPos centerPos = new BlockPos(roomBB.getCenter());


        for(int x = -size; x<=size; x++) {
            for(int z = -size; z<=size; z++) {
                placeBlockAbs(primer,
                        centerPos.add(x, -1, z),
                        chunkX, chunkZ, Blocks.air, (byte) 0);

                if(x == -size || x == size || z == -size || z == size) {

                    placeBlockAbs(primer,
                            centerPos.add(x, -2, z),
                            chunkX, chunkZ, floorMat.getBlock(), floorMat.getMetadata());

                }

                if(x > -size && x < size) {
                    if(z > -size && z < size) {
                        placeBlockAbs(primer,
                                centerPos.add(x, -2, z),
                                chunkX, chunkZ, Blocks.lava, (byte) 0);


                    }
                }
            }
        }

        return true;
    }

}
