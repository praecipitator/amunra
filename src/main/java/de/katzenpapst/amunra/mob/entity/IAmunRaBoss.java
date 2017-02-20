package de.katzenpapst.amunra.mob.entity;

import de.katzenpapst.amunra.tile.ITileDungeonSpawner;
import net.minecraft.util.AxisAlignedBB;

public interface IAmunRaBoss {
    public void setSpawner(ITileDungeonSpawner spawner);

    public ITileDungeonSpawner getSpawner();

    public void setRoomArea(AxisAlignedBB aabb);

    public AxisAlignedBB getRoomArea();

    public void despawnBoss();
}
