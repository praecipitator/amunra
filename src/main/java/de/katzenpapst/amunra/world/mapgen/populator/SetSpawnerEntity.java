package de.katzenpapst.amunra.world.mapgen.populator;

import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;

public class SetSpawnerEntity extends AbstractPopulator {

	String entityName;

	public SetSpawnerEntity(int x, int y, int z, String entityName) {
		super(x, y, z);
		this.entityName = entityName;
	}

	@Override
	public boolean populate(World world) {
		if (world.getBlock(x, y, z) == Blocks.mob_spawner)
        {
            final TileEntityMobSpawner spawner = (TileEntityMobSpawner) world.getTileEntity(x, y, z);
            if (spawner != null)
            {
                spawner.func_145881_a().setEntityName(entityName);
                return true;
            }
        }
		return false;
	}

}
