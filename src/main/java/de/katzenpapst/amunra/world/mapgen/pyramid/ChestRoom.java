package de.katzenpapst.amunra.world.mapgen.pyramid;

import java.util.Random;

import de.katzenpapst.amunra.world.mapgen.populator.FillChest;
import de.katzenpapst.amunra.world.mapgen.populator.SetSpawnerEntity;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class ChestRoom extends PyramidRoom {

    protected BlockMetaPair chest = new BlockMetaPair(Blocks.chest, (byte) 0);

    protected BlockMetaPair spawner = new BlockMetaPair(Blocks.mob_spawner, (byte) 0);

    @Override
    public boolean generateChunk(int chunkX, int chunkZ, Block[] arrayOfIDs, byte[] arrayOfMeta) {

        super.generateChunk(chunkX, chunkZ, arrayOfIDs, arrayOfMeta);


        this.generateBoxWithChest(this.roomBB.minX+5, this.roomBB.minZ+5, chunkX, chunkZ, arrayOfIDs, arrayOfMeta);
        this.generateBoxWithChest(this.roomBB.minX+5, this.roomBB.maxZ-5, chunkX, chunkZ, arrayOfIDs, arrayOfMeta);
        this.generateBoxWithChest(this.roomBB.maxX-5, this.roomBB.minZ+5, chunkX, chunkZ, arrayOfIDs, arrayOfMeta);
        this.generateBoxWithChest(this.roomBB.maxX-5, this.roomBB.maxZ-5, chunkX, chunkZ, arrayOfIDs, arrayOfMeta);

        return true;
    }

    protected void generateBoxWithChest(int centerX, int centerZ, int chunkX, int chunkZ, Block[] arrayOfIDs, byte[] arrayOfMeta) {
        int startY = this.floorLevel;
        int stopY = this.roomBB.maxY;
        BlockMetaPair floorMat = ((Pyramid) this.parent).getFloorMaterial();
        for(int x=centerX-1; x<=centerX+1; x++) {
            for(int z=centerZ-1; z<=centerZ+1; z++) {
                for(int y = startY; y <= stopY; y++) {
                    if(y == startY || y == stopY) {
                        placeBlockAbs(arrayOfIDs, arrayOfMeta, x, y, z, chunkX, chunkZ, floorMat);
                    } else {
                        // corners
                        if(x !=centerX && z != centerZ) {
                            placeBlockAbs(arrayOfIDs, arrayOfMeta, x, y, z, chunkX, chunkZ, floorMat);
                        } else if(x == centerX && z == centerZ) {
                            if(y==startY+1) {
                                // chest
                                placeChest(Pyramid.LOOT_CATEGORY_BASIC, x, y, z, chunkX, chunkZ, arrayOfIDs, arrayOfMeta);
                            } else if (y == startY+2) {
                                placeSpawner(getMob(this.parent.getWorld().rand), x, y, z, chunkX, chunkZ, arrayOfIDs, arrayOfMeta);
                            } else {
                                placeBlockAbs(arrayOfIDs, arrayOfMeta, x, y, z, chunkX, chunkZ, floorMat);
                            }
                        } else {
                            // walls
                            placeBlockAbs(arrayOfIDs, arrayOfMeta, x, y, z, chunkX, chunkZ, Blocks.iron_bars, 0);
                        }
                    }
                }
            }

        }
    }

    protected void placeChest(String lootCat, int x, int y, int z, int chunkX, int chunkZ, Block[] arrayOfIDs, byte[] arrayOfMeta) {
        if(this.placeBlockAbs(arrayOfIDs, arrayOfMeta, x, y, z,
                chunkX, chunkZ, chest)) {
            this.parent.addPopulator(new FillChest(x, y, z, chest, lootCat));
        }
    }

    protected void placeSpawner(String entityName, int x, int y, int z, int chunkX, int chunkZ, Block[] arrayOfIDs, byte[] arrayOfMeta) {
        if(this.placeBlockAbs(arrayOfIDs, arrayOfMeta, x, y, z,
                chunkX, chunkZ, spawner)) {
            this.parent.addPopulator(new SetSpawnerEntity(x, y, z, entityName));
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
