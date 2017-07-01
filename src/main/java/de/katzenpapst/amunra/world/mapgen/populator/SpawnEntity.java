package de.katzenpapst.amunra.world.mapgen.populator;

import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class SpawnEntity extends AbstractPopulator {

    private Entity entity = null;

    public SpawnEntity(BlockPos pos, Entity ent) {
        super(pos);
        entity = ent;
    }

    @Override
    public boolean populate(World world) {
        if(entity == null)
            return false;

        // otherwise try to spawn it now
        entity.setLocationAndAngles(this.pos.getX() + 0.5D, this.pos.getY(), this.pos.getZ() + 0.5D, 0.0F, 0.0F);
        return world.spawnEntityInWorld(entity);
    }

}
