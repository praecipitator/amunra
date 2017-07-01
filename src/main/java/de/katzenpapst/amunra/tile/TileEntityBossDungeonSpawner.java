package de.katzenpapst.amunra.tile;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import de.katzenpapst.amunra.helper.NbtHelper;
import de.katzenpapst.amunra.mob.entity.EntityMummyBoss;
import de.katzenpapst.amunra.mob.entity.IAmunRaBoss;
import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedCreeper;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSkeleton;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSpider;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedZombie;
import micdoodle8.mods.galacticraft.core.tile.TileEntityAdvanced;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;

public class TileEntityBossDungeonSpawner extends TileEntityAdvanced implements ITileDungeonSpawner, ITickable {

    protected Class<? extends IAmunRaBoss> bossClass;
    protected IAmunRaBoss boss;
    protected boolean spawned = false;
    protected boolean isBossDefeated = false;

    protected AxisAlignedBB roomArea = null;

    public TileEntityBossDungeonSpawner() {
        super();

        bossClass =  EntityMummyBoss.class;

        // test
        //this.setRoom(new Vector3(), size);
    }

    public List<Class<? extends EntityLiving>> getDisabledCreatures()
    {
        List<Class<? extends EntityLiving>> list = new ArrayList<>();
        list.add(EntityEvolvedSkeleton.class);
        list.add(EntityEvolvedZombie.class);
        list.add(EntityEvolvedSpider.class);
        list.add(EntityEvolvedCreeper.class);
        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void update()
    {
        super.update();

        if (this.roomArea == null)
        {
            return;
        }

        if (!this.worldObj.isRemote)
        {
            if(this.boss != null && ((Entity)this.boss).isDead) {
                this.boss = null;
                this.spawned = false;
            }

            List<Entity> entitiesInRoom = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.roomArea);
            int numPlayers = 0;
            boolean isBossInRoom = false;
            for(Entity ent : entitiesInRoom) {
                if(ent instanceof EntityPlayer) {
                    numPlayers++;
                } else if(bossClass.isInstance(ent)) {
                    IAmunRaBoss curBoss = (IAmunRaBoss)ent;
                    if(this.boss == null && curBoss.getSpawner() == this) {
                        this.boss = curBoss;
                        isBossInRoom = true;
                    } else {
                        if(boss != null && boss.equals(curBoss)) {
                            isBossInRoom = true;
                        }
                    }
                } else if (this.getDisabledCreatures().contains(ent.getClass())) {
                    ent.setDead();
                }
            }


            if(numPlayers > 0) {

                if (this.boss == null && !this.isBossDefeated && !this.spawned)
                {
                    // try spawning the boss
                    try
                    {
                        Constructor<?> c = this.bossClass.getConstructor(new Class[] { World.class });
                        this.boss = (IAmunRaBoss) c.newInstance(new Object[] { this.worldObj });
                        BlockPos pos = this.getPos();
                        ((Entity) this.boss).setPosition(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
                        this.boss.setRoomArea(roomArea);
                        this.boss.setSpawner(this);
                        this.spawned = true;
                        isBossInRoom = true;
                        this.worldObj.spawnEntityInWorld((Entity) this.boss);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            } else {
                // check if we have a boss and the player walked out
                if(this.boss != null && !this.isBossDefeated && this.spawned) {
                    // despawn boss

                    this.boss.despawnBoss();
                    this.boss = null;
                    this.spawned = false;
                }
            }

            if(!isBossInRoom && this.spawned && this.boss != null) {
                // do something?
                this.boss.despawnBoss();
                this.boss = null;
                this.spawned = false;
            }
        }
    }

    public void playSpawnSound(Entity entity)
    {
        this.worldObj.playSoundAtEntity(entity, Constants.TEXTURE_PREFIX + "ambience.scaryscape", 9.0F, 1.4F);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);

        this.spawned = nbt.getBoolean("spawned");
        //this.playerInRange = this.lastPlayerInRange = nbt.getBoolean("playerInRange");
        this.isBossDefeated = nbt.getBoolean("defeated");

        try
        {
            this.bossClass = (Class<? extends IAmunRaBoss>) Class.forName(nbt.getString("bossClass"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if(nbt.hasKey("roomArea")) {
            roomArea = NbtHelper.readAABB(nbt.getCompoundTag("roomArea"));
        }
        /*if(nbt.hasKey("spawnedBoss")) {
            Entity ent = this.worldObj.getEntityByID(nbt.getInteger("spawnedBoss"));
            if(ent != null && ent instanceof IAmunRaBoss) {
                boss = (IAmunRaBoss)ent;
            }
        }*/

    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);

        nbt.setBoolean("spawned", spawned);
        nbt.setBoolean("defeated", isBossDefeated);

        if(roomArea != null) {
            nbt.setTag("roomArea", NbtHelper.getAsNBT(roomArea));
        }

        /*if(boss != null) {
            int id = ((Entity)boss).getEntityId();
            nbt.setInteger("spawnedBoss", id);
        }*/
    }

    @Override
    public void setSpawnedBoss(IAmunRaBoss boss) {
        this.boss = boss;
    }

    @Override
    public IAmunRaBoss getSpawnedBoss() {
        return this.boss;
    }

    @Override
    public BlockPos getBlockPosition() {
        return this.getPos();//new Vector3int(xCoord, yCoord, zCoord);
    }

    @Override
    public double getPacketRange() {
        return 0;
    }

    @Override
    public int getPacketCooldown() {
        return 0;
    }

    @Override
    public boolean isNetworkedTile() {
        return false;
    }

    @Override
    public AxisAlignedBB getRoomArea() {
        return roomArea;
    }

    @Override
    public void setRoomArea(AxisAlignedBB aabb) {
        roomArea = new AxisAlignedBB(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
    }

    @Override
    public void onBossDefeated() {
        this.isBossDefeated = true;
        this.spawned = false;
        this.boss = null;
        // attempt selfdestruction
        this.worldObj.setBlockToAir(this.getPos());
    }

    @Override
    public void setBossClass(Class<? extends IAmunRaBoss> theClass) {
        bossClass = theClass;
    }

}
