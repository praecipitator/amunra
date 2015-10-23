package de.katzenpapst.amunra.world.mapgen.pyramid;

import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta;
import micdoodle8.mods.galacticraft.planets.mars.blocks.MarsBlocks;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityCreature;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.mob.entity.EntityRobotVillager;

import java.util.Random;

import cpw.mods.fml.common.FMLLog;

public class Pyramid extends MapGenBaseMeta
{
	
	final protected BlockMetaPair material = ARBlocks.multiBlockRock.getBlockMetaPair("alucrate");
		
	
    @Override
    public void generate(IChunkProvider par1IChunkProvider, World par2World, int xOrig, int zOrig, Block[] arrayOfIDs, byte[] arrayOfMeta)
    {
        final int range = this.range;	// default = 8
        this.worldObj = par2World;
        this.rand.setSeed(par2World.getSeed());
        final long var7 = this.rand.nextLong();
        final long var9 = this.rand.nextLong();

        for (int xChunk = xOrig - range; xChunk <= xOrig + range; ++xChunk)
        {
            for (int zChunk = zOrig - range; zChunk <= zOrig + range; ++zChunk)
            {
                final long xSeed = xChunk * var7;
                final long zSeed = zChunk * var9;
                this.rand.setSeed(xSeed ^ zSeed ^ par2World.getSeed());
                this.recursiveGenerate(par2World, xChunk, zChunk, xOrig, zOrig, arrayOfIDs, arrayOfMeta);
            }
        }
    }

    @Override
    protected void recursiveGenerate(World par1World, int xChunkCoord, int zChunkCoord, int origXChunkCoord, int origZChunkCoord, Block[] arrayOfIDs, byte[] arrayOfMeta)
    {
        if (this.rand.nextInt(100) == 0) {// seems weird, and WAY too common. 
        	// and I don't get how it's supposed to work. rand seems to depend on the current coords, not 
        	// on the orig coords
        	// oh wait I think I got this the wrong way round. could it be that the chunk @ "orig" coords
        	// are actually what the two arrays are supposed to fill?
        	// that would mean, the double loop up there is searchting for the ORIGIN of the structure
        	// and here by using a seed dervied from it I just check if there is a piece of it at ORIGIN coords?!
        	// that would explain quite a bit. maybe the other branch will work using this knowledge, too
        
        	// so it seems, here I am supposed to generate one single chunk of stuff for origChunk
        	// the easiest way would be 
            final double xPos = xChunkCoord * 16 + this.rand.nextInt(16);
            final int yPos = 120;
            final double zPos = zChunkCoord * 16 + this.rand.nextInt(16);
            FMLLog.info("Doing the stuff at x="+xPos+", z="+zPos);
            
            // try some random stuff
            int caveMinX = MathHelper.floor_double(xPos - 25) - origXChunkCoord * 16 - 1;
            int caveMaxX = MathHelper.floor_double(xPos + 25) - origXChunkCoord * 16 + 1;
            //int caveMinY = MathHelper.floor_double(yPos - 25) - 1;
            //int caveMaxY = MathHelper.floor_double(yPos + 25) + 1;
            int caveMinZ = MathHelper.floor_double(zPos - 25) - origZChunkCoord * 16 - 1;
            int caveMaxZ = MathHelper.floor_double(zPos + 25) - origZChunkCoord * 16 + 1;
            
            if (caveMinX < 0)
            {
                caveMinX = 0;
            }

            if (caveMaxX > 16)
            {
                caveMaxX = 16;
            }

           /* if (caveMinY < 1)
            {
                caveMinY = 1;
            }

            if (caveMaxY > 65)
            {
                caveMaxY = 65;
            }*/

            if (caveMinZ < 0)
            {
                caveMinZ = 0;
            }

            if (caveMaxZ > 16)
            {
                caveMaxZ = 16;
            }
            
            for(int curX = caveMinX; curX < caveMaxX; curX++) {
        		for(int curZ = caveMinZ; curZ < caveMaxZ; curZ++) {
        			
        			
        			
            		int coords = coords2int(curX, yPos, curZ);
            		arrayOfIDs[coords] = material.getBlock();
            		arrayOfMeta[coords] = material.getMetadata();
            		
            		// TEST 
        			if(curX == 12 && curZ == 12) {
        				EntityCreature villager = new EntityRobotVillager(worldObj);
    	                villager.onSpawnWithEgg(null);// NO IDEA
    	                villager.setLocationAndAngles(xPos + 0.5D, yPos, zPos + 0.5D, 0.0F, 0.0F);
    	                worldObj.spawnEntityInWorld(villager);
        			}
            	}
        	}
            
            //this.generateLargeCaveNode(this.rand.nextLong(), origXChunkCoord, origZChunkCoord, arrayOfIDs, arrayOfMeta, xPos, yPos, zPos);
        }
    }
    
    

 

    protected int coords2int(int x, int y, int z) {
    	int coords = (x * 16 + z) * 256 + y;
    	return coords;
    }
    
    
}
