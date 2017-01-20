package de.katzenpapst.amunra.mothership;

import java.util.Random;

import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.block.BlockMachineMeta;
import de.katzenpapst.amunra.block.machine.mothershipEngine.MothershipEngineJetRocket;
import de.katzenpapst.amunra.world.WorldHelper;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class MothershipWorldGen extends WorldGenerator {

    private BlockMetaPair groundBlock;
    private BlockMetaPair decoBlock;
    private BlockMetaPair glassBlock;
    private BlockMetaPair msController;
    /*private BlockMetaPair msJet;
    private BlockMetaPair msEngine;*/

    public MothershipWorldGen() {
        groundBlock = new BlockMetaPair(GCBlocks.basicBlock, (byte) 4);
        decoBlock = new BlockMetaPair(GCBlocks.basicBlock, (byte) 3);
        glassBlock = new BlockMetaPair(Blocks.glass, (byte) 0);
        msController = ARBlocks.blockMothershipController;
        /*msJet = ARBlocks.blockMsEngineRocketJet;
        msEngine = ARBlocks.blockMsEngineRocketBooster;*/
    }
    @Override
    public boolean generate(World world, Random rand, int centerX, int centerY, int centerZ)
    {
        // for this, assume the coordinates we got are the center
        // make one big plane first
        int startX  = centerX-3;
        int stopX   = centerX+3;
        int startZ  = centerZ-3;
        int stopZ   = centerZ+3;



        for(int x=startX;x<=stopX;x++) {
            for(int z=startZ;z<=stopZ;z++) {
                if(x == startX || x == stopX || z == startZ || z == stopZ) {
                    world.setBlock(x, centerY, z, decoBlock.getBlock(), decoBlock.getMetadata(), 3);
                } else {
                    world.setBlock(x, centerY, z, groundBlock.getBlock(), groundBlock.getMetadata(), 3);
                }
            }
        }

        startX  = centerX-3;
        stopX   = centerX+3;
        startZ  = centerZ-3-7;
        stopZ   = centerZ+3-7;

        for(int x=startX;x<=stopX;x++) {
            for(int z=startZ;z<=stopZ;z++) {
                // floor
                world.setBlock(x, centerY, z, groundBlock.getBlock(), groundBlock.getMetadata(), 3);


                // sides
                if(x == startX || x == stopX || z == startZ || z == stopZ) {
                    // roof border
                    world.setBlock(x, centerY+4, z, groundBlock.getBlock(), groundBlock.getMetadata(), 3);
                    if((x > startX+1 && x < stopX-1) || (z > startZ+1 && z < stopZ-1)) {
                        continue;
                    }
                    // walls
                    for(int y=centerY+1; y<centerY+4;y++) {
                        if(y > centerY+1 && y < centerY+3) {
                            world.setBlock(x, y, z, glassBlock.getBlock(), glassBlock.getMetadata(), 3);
                        } else {
                            world.setBlock(x, y, z, decoBlock.getBlock(), decoBlock.getMetadata(), 3);
                        }
                    }
                } else {
                    // roof center
                    world.setBlock(x, centerY+4, z, glassBlock.getBlock(), glassBlock.getMetadata(), 3);
                }
            }
        }

        // "wings"
        startX  = centerX-1+5;
        stopX   = centerX+1+5;
        startZ  = centerZ-1-7;
        stopZ   = centerZ+1-7;

        for(int x=startX;x<=stopX;x++) {
            for(int z=startZ;z<=stopZ;z++) {
                world.setBlock(x, centerY, z, groundBlock.getBlock(), groundBlock.getMetadata(), 3);
            }
        }


        // machines
        // 0, -9 0 => controller
        int rotationMeta = 2;
        world.setBlock(centerX-3, centerY+1, centerZ-7, msController.getBlock(), msController.getMetadata() | (rotationMeta << 2), 3);
        /*

        // (-)5, -6 => booster
        // (-)5, -5 => engine
        world.setBlock(centerX+5, centerY+1, centerZ-7, msEngine.getBlock(), msEngine.getMetadata(), 3);
        world.setBlock(centerX-5, centerY+1, centerZ-7, msEngine.getBlock(), msEngine.getMetadata(), 3);

        int jetRotation = ARBlocks.metaBlockMachine.addRotationMeta(msJet.getMetadata(), 2);
        world.setBlock(centerX+5, centerY+1, centerZ-6, msJet.getBlock(), jetRotation, 3);
        world.setBlock(centerX-5, centerY+1, centerZ-6, msJet.getBlock(), jetRotation, 3);
*/
        return true;
    }
}
