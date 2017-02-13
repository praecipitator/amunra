package de.katzenpapst.amunra.world.mapgen.village;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;












import cpw.mods.fml.common.FMLLog;
import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.helper.CoordHelper;
import de.katzenpapst.amunra.world.mapgen.BaseStructureStart;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class GridVillageStart extends BaseStructureStart {

    protected BlockMetaPair pathMaterial = ARBlocks.blockBasaltRegolith;


    protected BlockMetaPair wallMaterial = ARBlocks.blockAluCrate;
    protected BlockMetaPair floorMaterial = ARBlocks.blockSmoothBasalt;
    protected BlockMetaPair fillMaterial = ARBlocks.blockBasaltBrick;

    protected int numGridElements = 0;

    protected int gridSize = 9;

    protected int gridSideLength = 0;


    protected HashMap<Integer, GridVillageComponent> componentsByGrid;


    /**
     * Instantiates the thing, the coords in here should be the START point
     * @param chunkX
     * @param chunkZ
     */
    public GridVillageStart(World world, int chunkX, int chunkZ, Random rand) {

        super(world, chunkX, chunkZ, rand);
        int startBlockX = CoordHelper.chunkToMinBlock(chunkX) + this.startX;
        int startBlockZ = CoordHelper.chunkToMinBlock(chunkZ) + this.startZ;

        FMLLog.info("Generating the Village at x="+startBlockX+", z="+startBlockZ);

        componentsByGrid = new HashMap<Integer, GridVillageComponent>();
    }




    /**
     * From what I understood, this is called first, then, after this is done, populateChunk is called
     * and after that, the chunk is done and won't be touched again
     *
     * @param chunkX
     * @param chunkZ
     * @param arrayOfIDs
     * @param arrayOfMeta
     * @return
     */
    @Override
    public boolean generateChunk(int chunkX, int chunkZ, Block[] arrayOfIDs, byte[] arrayOfMeta) {
        super.generateChunk(chunkX, chunkZ, arrayOfIDs, arrayOfMeta);
        drawGrid(chunkX, chunkZ, arrayOfIDs, arrayOfMeta);

        drawGridComponents(chunkX, chunkZ, arrayOfIDs, arrayOfMeta);
        return true;
    }



    public void setComponents(ArrayList components) {

        // byte should be enough for gridsize

        numGridElements = components.size();

        gridSideLength = (int) Math.ceil(Math.sqrt(numGridElements));

        // now the effective grid width is this.gridSize+3
        int effectiveGridSize = this.gridSize+3;

        int squareWidth = effectiveGridSize*gridSideLength;

        int startBlockX = CoordHelper.chunkToMinBlock(chunkX) + this.startX;
        int startBlockZ = CoordHelper.chunkToMinBlock(chunkZ) + this.startZ;

        // my own structBB
        structBB = new StructureBoundingBox();
        structBB.minX = startBlockX - (int)Math.floor(squareWidth/2);
        structBB.maxX = startBlockX + (int)Math.ceil(squareWidth/2);
        structBB.minZ = startBlockZ - (int)Math.floor(squareWidth/2);
        structBB.maxZ = startBlockZ + (int)Math.ceil(squareWidth/2);
        structBB.minY = 0;
        structBB.maxY = 255;

        // hack for dummy entries for shuffling
        Object dummyComponent = new Object();

        int totalGridElems = gridSideLength*gridSideLength;
        // pad the components
        for(int i=numGridElements;i<totalGridElems;i++) {
            components.add(dummyComponent);
        }

        Collections.shuffle(components, this.rand);

        byte gridX = 0;
        byte gridZ = 0;
        for(Object comp: components) {
            if(!(comp instanceof GridVillageComponent)) {
                continue;
            }
            GridVillageComponent vComp = ((GridVillageComponent)comp);
            int index = gridX + (gridZ << 8);

            StructureBoundingBox componentBox = new StructureBoundingBox(
                    structBB.minX + effectiveGridSize*gridX + 2,
                    structBB.minZ + effectiveGridSize*gridZ + 2,
                    structBB.minX + effectiveGridSize*gridX + 1 + this.gridSize,
                    structBB.minZ + effectiveGridSize*gridZ + 1 + this.gridSize
                    );

            componentBox.getXSize();
            //
            //cmp.setCoordMode(this.rand.nextInt(4));
            vComp.setStructureBoundingBox(componentBox);
            vComp.setCoordMode(this.rand.nextInt(4));
            //vComp.setCoordMode(3);
            vComp.setParent(this);
            componentsByGrid.put(index, vComp);
            gridX++;
            if(gridX >= gridSideLength) {
                gridX = 0;
                gridZ++;
            }
        }




    }

    public BlockMetaPair getPathMaterial() {
        return pathMaterial;
    }

    public void setPathMaterial(BlockMetaPair pathMaterial) {
        this.pathMaterial = pathMaterial;
    }

    public BlockMetaPair getWallMaterial() {
        return wallMaterial;
    }

    public void setWallMaterial(BlockMetaPair wallMaterial) {
        this.wallMaterial = wallMaterial;
    }

    public BlockMetaPair getFloorMaterial() {
        return floorMaterial;
    }

    public void setFloorMaterial(BlockMetaPair floorMaterial) {
        this.floorMaterial = floorMaterial;
    }

    public BlockMetaPair getFillMaterial() {
        return fillMaterial;
    }

    public void setFillMaterial(BlockMetaPair fillMaterial) {
        this.fillMaterial = fillMaterial;
    }

    protected void drawStuffInGrid(int chunkX, int chunkZ, int gridX, int gridZ, Block[] arrayOfIDs, byte[] arrayOfMeta) {
        // now how do I calculate the grid's position?
        // I think it's
        int effectiveGridSize = this.gridSize+3;
        int testX = structBB.minX + effectiveGridSize*gridX + 2;
        int testZ = structBB.minZ + effectiveGridSize*gridZ + 2;


        // now try
        for(int x=0;x<this.gridSize;x++) {
            for(int z=0;z<this.gridSize;z++) {
                int relX = CoordHelper.abs2rel(testX+x, chunkX);
                int relZ = CoordHelper.abs2rel(testZ+z, chunkZ);
                placeBlockOnGround(arrayOfIDs, arrayOfMeta, relX, relZ, wallMaterial.getBlock(), wallMaterial.getMetadata());
            }
        }
    }

    protected void drawGrid(int chunkX, int chunkZ, Block[] arrayOfIDs, byte[] arrayOfMeta) {
        // hmmm how do I do this now?
        // length of the square
        int effectiveGridSize = this.gridSize+3;

        /*StructureBoundingBox chunkBB = CoordHelper.getChunkBB(chunkX, chunkZ);
		int minGroundLevel = this.getAverageGroundLevel(arrayOfIDs, arrayOfMeta, structBB, chunkBB, -1);*/

        for(int x = structBB.minX; x < structBB.maxX;x++) {
            for(int z = structBB.minZ; z < structBB.maxZ; z++) {
                int testX = x - structBB.minX;
                int testZ = z - structBB.minZ;
                boolean drawX = false;
                boolean drawZ = false;

                if(testX != 0 && (testX % effectiveGridSize) == 0) {
                    drawX = true;
                }
                if (testZ != 0 && (testZ % effectiveGridSize) == 0) {
                    drawZ = true;
                }

                if(!drawX && !drawZ) {
                    continue;
                }
                int relX = CoordHelper.abs2rel(x, chunkX);
                int relZ = CoordHelper.abs2rel(z, chunkZ);

                if(drawX && drawZ) {
                    // crossing

                    placeBlockOnGround(arrayOfIDs, arrayOfMeta, relX-1, relZ-1, pathMaterial.getBlock(), pathMaterial.getMetadata());
                    placeBlockOnGround(arrayOfIDs, arrayOfMeta, relX,   relZ-1, pathMaterial.getBlock(), pathMaterial.getMetadata());
                    placeBlockOnGround(arrayOfIDs, arrayOfMeta, relX+1, relZ-1, pathMaterial.getBlock(), pathMaterial.getMetadata());

                    placeBlockOnGround(arrayOfIDs, arrayOfMeta, relX-1, relZ,   pathMaterial.getBlock(), pathMaterial.getMetadata());
                    placeBlockOnGround(arrayOfIDs, arrayOfMeta, relX,   relZ,   pathMaterial.getBlock(), pathMaterial.getMetadata());
                    placeBlockOnGround(arrayOfIDs, arrayOfMeta, relX+1, relZ,   pathMaterial.getBlock(), pathMaterial.getMetadata());

                    placeBlockOnGround(arrayOfIDs, arrayOfMeta, relX-1, relZ+1, pathMaterial.getBlock(), pathMaterial.getMetadata());
                    placeBlockOnGround(arrayOfIDs, arrayOfMeta, relX,   relZ+1, pathMaterial.getBlock(), pathMaterial.getMetadata());
                    placeBlockOnGround(arrayOfIDs, arrayOfMeta, relX+1, relZ+1, pathMaterial.getBlock(), pathMaterial.getMetadata());
                } else if(drawX) {
                    placeBlockOnGround(arrayOfIDs, arrayOfMeta, relX-1, relZ,   pathMaterial.getBlock(), pathMaterial.getMetadata());
                    placeBlockOnGround(arrayOfIDs, arrayOfMeta, relX,   relZ,   pathMaterial.getBlock(), pathMaterial.getMetadata());
                    placeBlockOnGround(arrayOfIDs, arrayOfMeta, relX+1, relZ,   pathMaterial.getBlock(), pathMaterial.getMetadata());
                } else if(drawZ) {
                    placeBlockOnGround(arrayOfIDs, arrayOfMeta, relX, relZ-1,   pathMaterial.getBlock(), pathMaterial.getMetadata());
                    placeBlockOnGround(arrayOfIDs, arrayOfMeta, relX, relZ,     pathMaterial.getBlock(), pathMaterial.getMetadata());
                    placeBlockOnGround(arrayOfIDs, arrayOfMeta, relX, relZ+1,   pathMaterial.getBlock(), pathMaterial.getMetadata());
                }
            }
        }
    }

    /**
     * Places a block into the topmost solid block
     *
     * @param arrayOfIDs
     * @param arrayOfMeta
     * @param relX
     * @param relZ
     * @param block
     * @param meta
     */
    protected void placeBlockOnGround(Block[] arrayOfIDs, byte[] arrayOfMeta, int relX, int relZ, Block block, int meta) {
        if(relX < 0 || relX >= 16 || relZ < 0 || relZ >= 16) {
            return;
        }
        int y = GridVillageComponent.getHighestSolidBlock(arrayOfIDs, arrayOfMeta, relX, relZ);
        GridVillageComponent.placeBlockRel(arrayOfIDs, arrayOfMeta, relX, y-1, relZ, block, meta);
    }

    protected void drawGridComponents(int chunkX, int chunkZ, Block[] arrayOfIDs, byte[] arrayOfMeta) {

        //int effectiveGridSize = this.gridSize+3;

        StructureBoundingBox chunkBox = CoordHelper.getChunkBB(chunkX, chunkZ);//new StructureBoundingBox(chunkX*16, chunkZ*16, chunkX*16+15, chunkZ*16+15);

        for(int gridX = 0;gridX < gridSideLength;gridX++) {
            for(int gridZ = 0;gridZ < gridSideLength;gridZ++) {



                int index = gridX + (gridZ << 8);

                if(!componentsByGrid.containsKey(index)) {
                    continue;
                }

                GridVillageComponent curComp = componentsByGrid.get(index);

                // fail for chunk z = -28
                // ALL components should intersect with -27
                if(curComp.getStructureBoundingBox().intersectsWith(chunkBox)) {
                    //continue; // not in this chunk

                    curComp.generateChunk(chunkX, chunkZ, arrayOfIDs, arrayOfMeta);
                }


            }
        }
    }


}
