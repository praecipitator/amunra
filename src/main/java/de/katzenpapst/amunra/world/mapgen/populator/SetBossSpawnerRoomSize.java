package de.katzenpapst.amunra.world.mapgen.populator;

import de.katzenpapst.amunra.tile.ITileDungeonSpawner;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class SetBossSpawnerRoomSize extends AbstractPopulator {

    protected AxisAlignedBB aabb;

    public SetBossSpawnerRoomSize(int x, int y, int z, AxisAlignedBB aabb) {
        super(x, y, z);

        this.aabb = aabb;
    }

    @Override
    public boolean populate(World world) {

        TileEntity t = world.getTileEntity(x, y, z);

        if(t instanceof ITileDungeonSpawner) {
            ((ITileDungeonSpawner)t).setRoomArea(aabb);
            return true;
        }

        return false;
    }

}
