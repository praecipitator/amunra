package de.katzenpapst.amunra.world.mapgen.newVillage.populator;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class SpawnEntity extends AbstractPopulator {
	
	public Entity entity = null;

	@Override
	public boolean populate(World world) {
		if(entity == null)
			return false;
		
		// otherwise try to spawn it now
		entity.setLocationAndAngles(this.x + 0.5D, this.y, this.z + 0.5D, 0.0F, 0.0F);
        return world.spawnEntityInWorld(entity);
	}
	
}
