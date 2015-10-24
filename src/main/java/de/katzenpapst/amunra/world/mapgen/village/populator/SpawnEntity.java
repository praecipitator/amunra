package de.katzenpapst.amunra.world.mapgen.village.populator;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class SpawnEntity extends AbstractPopulator {
	
	private Entity entity = null;
	
	public SpawnEntity(int x, int y, int z, Entity ent) {
		super(x, y, z);
		entity = ent;
	}

	@Override
	public boolean populate(World world) {
		if(entity == null)
			return false;
		
		// otherwise try to spawn it now
		entity.setLocationAndAngles(this.x + 0.5D, this.y, this.z + 0.5D, 0.0F, 0.0F);
        return world.spawnEntityInWorld(entity);
	}
	
}
