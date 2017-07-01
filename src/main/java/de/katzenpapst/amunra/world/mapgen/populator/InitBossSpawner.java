package de.katzenpapst.amunra.world.mapgen.populator;

import de.katzenpapst.amunra.mob.entity.IAmunRaBoss;
import de.katzenpapst.amunra.tile.ITileDungeonSpawner;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class InitBossSpawner extends AbstractPopulator {

    protected AxisAlignedBB aabb;

    protected Class <? extends IAmunRaBoss> entityClass;

    public InitBossSpawner(BlockPos pos, AxisAlignedBB aabb, Class <? extends IAmunRaBoss> entityClass) {
        super(pos);

        this.aabb = aabb;

        this.entityClass = entityClass;
    }

    @Override
    public boolean populate(World world) {

        TileEntity t = world.getTileEntity(pos);

        if(t instanceof ITileDungeonSpawner) {
            ((ITileDungeonSpawner)t).setRoomArea(aabb);
            ((ITileDungeonSpawner)t).setBossClass(entityClass);
            return true;
        }

        return false;
    }

}
