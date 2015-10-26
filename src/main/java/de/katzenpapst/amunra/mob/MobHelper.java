package de.katzenpapst.amunra.mob;

import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class MobHelper {

	public static boolean canAnimalSpawnHere(World world, EntityCreature ent, BlockMetaPair blockToSpawnOn) {
	
		int i = MathHelper.floor_double(ent.posX);
        int j = MathHelper.floor_double(ent.boundingBox.minY);
        int k = MathHelper.floor_double(ent.posZ);
        
        boolean canSpawnOnBlock = true;
        if(blockToSpawnOn != null) {
        	canSpawnOnBlock = blockToSpawnOn.getBlock() == world.getBlock(i, j - 1, k) 
        			&& blockToSpawnOn.getMetadata() == world.getBlockMetadata(i, j - 1, k);
        }
        
		return canSpawnOnBlock && 
				world.getFullBlockLightValue(i, j, k) > 8 && 
				ent.getBlockPathWeight(i, j, k) >= 0.0F &&
				world.checkNoEntityCollision(ent.boundingBox) && 
				world.getCollidingBoundingBoxes(ent, ent.boundingBox).isEmpty() && 
				!world.isAnyLiquid(ent.boundingBox);
		
	}

}
