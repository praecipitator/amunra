package de.katzenpapst.amunra.world.mapgen.pyramid;

import java.util.Random;

import de.katzenpapst.amunra.world.mapgen.populator.FillChest;
import de.katzenpapst.amunra.world.mapgen.populator.SetSpawnerEntity;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.ChunkPrimer;

public class ChestRoom extends PyramidRoom {

    protected BlockMetaPair chest = new BlockMetaPair(Blocks.chest, (byte) 0);

    protected BlockMetaPair spawner = new BlockMetaPair(Blocks.mob_spawner, (byte) 0);

    @Override
    public boolean generateChunk(int chunkX, int chunkZ, ChunkPrimer primer) {

        super.generateChunk(chunkX, chunkZ, primer);


        this.generateBoxWithChest(this.roomBB.minX+5, this.roomBB.minZ+5, chunkX, chunkZ, primer);
        this.generateBoxWithChest(this.roomBB.minX+5, this.roomBB.maxZ-5, chunkX, chunkZ, primer);
        this.generateBoxWithChest(this.roomBB.maxX-5, this.roomBB.minZ+5, chunkX, chunkZ, primer);
        this.generateBoxWithChest(this.roomBB.maxX-5, this.roomBB.maxZ-5, chunkX, chunkZ, primer);

        return true;
    }

    protected void generateBoxWithChest(int centerX, int centerZ, int chunkX, int chunkZ, ChunkPrimer primer) {

        int startY = this.floorLevel;
        int stopY = this.roomBB.maxY;
        BlockMetaPair floorMat = ((Pyramid) this.parent).getFloorMaterial();
        for(int x=centerX-1; x<=centerX+1; x++) {
            for(int z=centerZ-1; z<=centerZ+1; z++) {
                for(int y = startY; y <= stopY; y++) {

                    BlockPos curPos = new BlockPos(x, y, z);

                    if(y == startY || y == stopY) {
                        placeBlockAbs(primer, curPos, chunkX, chunkZ, floorMat);
                    } else {
                        // corners
                        if(x !=centerX && z != centerZ) {
                            placeBlockAbs(primer, curPos, chunkX, chunkZ, floorMat);
                        } else if(x == centerX && z == centerZ) {
                            if(y==startY+1) {
                                // chest
                                placeChest(Pyramid.LOOT_CATEGORY_BASIC, curPos, chunkX, chunkZ, primer);
                            } else if (y == startY+2) {
                                placeSpawner(getMob(this.parent.getWorld().rand), curPos, chunkX, chunkZ, primer);
                            } else {
                                placeBlockAbs(primer, curPos, chunkX, chunkZ, floorMat);
                            }
                        } else {
                            // walls
                            placeBlockAbs(primer, curPos, chunkX, chunkZ, Blocks.iron_bars, 0);
                        }
                    }
                }
            }

        }
    }

    protected void placeChest(String lootCat, BlockPos pos, int chunkX, int chunkZ, ChunkPrimer primer) {
        if(this.placeBlockAbs(primer, pos,
                chunkX, chunkZ, chest)) {
            this.parent.addPopulator(new FillChest(pos, chest, lootCat));
        }
    }

    protected void placeSpawner(String entityName, BlockPos pos, int chunkX, int chunkZ, ChunkPrimer primer) {
        if(this.placeBlockAbs(primer, pos,
                chunkX, chunkZ, spawner)) {
            this.parent.addPopulator(new SetSpawnerEntity(pos, entityName));
        }
    }

    private static String getMob(Random rand)
    {
        switch (rand.nextInt(6))
        {
        case 0:
            return "EvolvedSpider";
        case 1:
            return "EvolvedZombie";
        case 2:
            return "EvolvedCreeper";
        case 3:
            return "EvolvedSkeleton";
        default:
            return "EvolvedCreeper";
        }
    }




}
