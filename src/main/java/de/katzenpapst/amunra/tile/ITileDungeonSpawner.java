package de.katzenpapst.amunra.tile;

import de.katzenpapst.amunra.mob.entity.IAmunRaBoss;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public interface ITileDungeonSpawner {
    public void setSpawnedBoss(IAmunRaBoss boss);

    public IAmunRaBoss getSpawnedBoss();

    public BlockPos getBlockPosition();

    public AxisAlignedBB getRoomArea();

    public void setRoomArea(AxisAlignedBB aabb);

    public void setBossClass(Class <? extends IAmunRaBoss> theClass);

    public void onBossDefeated();
}
