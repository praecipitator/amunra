package de.katzenpapst.amunra.world.mapgen.pyramid;

import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.mob.entity.EntityMummyBoss;
import de.katzenpapst.amunra.world.mapgen.populator.InitBossSpawner;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.ChunkPrimer;

public class BossRoom extends PyramidRoom {

    public BossRoom() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean generateChunk(int chunkX, int chunkZ, ChunkPrimer primer) {

        super.generateChunk(chunkX, chunkZ, primer);
/*
        // try making a room below

        int roomSize = (roomBB.getXSize()/2) - 8;
        for(int x = -roomSize; x<=roomSize; x++) {
            for(int z = -roomSize; z<=roomSize; z++) {
                for(int y = this.floorLevel-6; y < this.floorLevel-1;y++) {
                    placeBlockAbs(arrayOfIDs, arrayOfMeta,
                            this.roomBB.getCenterX()+x,
                            y,
                            this.roomBB.getCenterZ()+z,
                            chunkX, chunkZ, Blocks.air, (byte) 0);
                }
            }
        }

        // make a hole in the center
        int trapdoorMeta = 8 | 0; // 8 = on top, 0 = south, 1 = north
        placeBlockAbs(arrayOfIDs, arrayOfMeta,
                this.roomBB.getCenterX(),
                this.floorLevel-1,
                this.roomBB.getCenterZ(),
                chunkX, chunkZ, Blocks.trapdoor, (byte) trapdoorMeta);

        // ladder?
        BlockMetaPair wallMat = ((Pyramid) this.parent).getWallMaterial();
        for(int y = this.floorLevel-6; y < this.floorLevel-1;y++) {
            placeBlockAbs(arrayOfIDs, arrayOfMeta,
                    this.roomBB.getCenterX(),
                    y,
                    this.roomBB.getCenterZ()+1,
                    chunkX, chunkZ, wallMat.getBlock(), (byte) wallMat.getMetadata());

            placeBlockAbs(arrayOfIDs, arrayOfMeta,
                    this.roomBB.getCenterX(),
                    y,
                    this.roomBB.getCenterZ(),
                    chunkX, chunkZ, Blocks.ladder, (byte) 2);
        }
*/
        // for now, just this

        BlockPos centerPos = new BlockPos (this.roomBB.getCenter());

        placeBossSpawner(centerPos.add(0,2,0),
                chunkX, chunkZ,
                primer,
                ARBlocks.osirisBossSpawner
        );

        return true;
    }

    protected void placeBossSpawner(BlockPos pos, int chunkX, int chunkZ, ChunkPrimer primer, BlockMetaPair spawner) {
        if(placeBlockAbs(primer, pos, chunkX, chunkZ, spawner)) {
            /*
            List<Entity> entitiesWithin = this.worldObj.getEntitiesWithinAABB(
            EntityPlayer.class,
            AxisAlignedBB.getBoundingBox(
                this.roomCoords.intX() - 1,
                this.roomCoords.intY() - 1,
                this.roomCoords.intZ() - 1,

                this.roomCoords.intX() + this.roomSize.intX(),
                this.roomCoords.intY() + this.roomSize.intY(),
                this.roomCoords.intZ() + this.roomSize.intZ()
            )
            );
            */

            AxisAlignedBB areaBB = new AxisAlignedBB(roomBB.minX, roomBB.minY, roomBB.minZ, roomBB.maxX+1, roomBB.maxY+1, roomBB.maxZ+1);
            this.parent.addPopulator(
                    new InitBossSpawner(pos, areaBB, EntityMummyBoss.class)
            );
        }
    }

}
