package de.katzenpapst.amunra.world.mapgen.pyramid;

import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.world.mapgen.populator.SetBossSpawnerRoomSize;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;

public class BossRoom extends PyramidRoom {

    public BossRoom() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean generateChunk(int chunkX, int chunkZ, Block[] arrayOfIDs, byte[] arrayOfMeta) {

        super.generateChunk(chunkX, chunkZ, arrayOfIDs, arrayOfMeta);

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
/*
        placeBlockAbs(arrayOfIDs, arrayOfMeta,
                this.roomBB.getCenterX(),
                this.floorLevel+3,
                this.roomBB.getCenterZ(),
                chunkX, chunkZ, ARBlocks.osirisBossSpawner);
*/
        placeBossSpawner(this.roomBB.getCenterX(),
                this.floorLevel+2,
                this.roomBB.getCenterZ(),
                chunkX, chunkZ,
                arrayOfIDs, arrayOfMeta,
                ARBlocks.osirisBossSpawner
        );
        // spawner
        /*
        placeBlockAbs(arrayOfIDs, arrayOfMeta,
                this.roomBB.getCenterX(),
                this.floorLevel-6,
                this.roomBB.getCenterZ()-2,
                chunkX, chunkZ, Blocks.ladder, (byte) 2);
*/
        return true;
    }

    protected void placeBossSpawner(int x, int y, int z, int chunkX, int chunkZ, Block[] arrayOfIDs, byte[] arrayOfMeta, BlockMetaPair spawner) {
        if(placeBlockAbs(arrayOfIDs, arrayOfMeta,
                x,
                y,
                z,
                chunkX, chunkZ, spawner)) {
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
            AxisAlignedBB areaBB = AxisAlignedBB.getBoundingBox(roomBB.minX, roomBB.minY, roomBB.minZ, roomBB.maxX+1, roomBB.maxY+1, roomBB.maxZ+1);
            this.parent.addPopulator(new SetBossSpawnerRoomSize(x, y, z, areaBB));
        }
    }

}
