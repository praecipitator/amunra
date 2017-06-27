package de.katzenpapst.amunra.mob.entity;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.api.entity.IAntiGrav;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.monster.IMob;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public abstract class EntityFlyingMob extends EntityFlying implements IMob, IAntiGrav {

    public int courseChangeCooldown;
    public double waypointX;
    public double waypointY;
    public double waypointZ;
    private Entity targetedEntity;
    /** Cooldown time between target loss and new target aquirement. */
    private int aggroCooldown;
    public int prevAttackCounter;
    public int attackCounter;
    /** The explosion radius of spawned fireballs. */
    protected int explosionStrength = 1;

    protected static final float distanceToKeep = 10.0F;

    public EntityFlyingMob(World world)
    {
        super(world);
    }

    @SideOnly(Side.CLIENT)
    public boolean useShootingTexture()
    {
        // copied over from the ghast. WTF is this?
        // oh, this seems to be where the renderer decides which texture to use
        // see net.minecraft.client.renderer.entity.RenderGhast.getEntityTexture(EntityGhast)
        return this.dataWatcher.getWatchableObjectByte(16) != 0;
    }

    abstract protected float getVisionDistance();

    /**
     * Called when the entity is attacked.
     */
    @Override
    public boolean attackEntityFrom(DamageSource dmgSrc, float amount)
    {
        if (this.isEntityInvulnerable())
        {
            return false;
        }
        else
        {
            return super.attackEntityFrom(dmgSrc, amount);
        }
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(16, Byte.valueOf((byte)0));
    }



    abstract protected void performAttack(Entity target, double accelX, double accelY, double accelZ);

    protected void findWaypoint() {
        if(this.targetedEntity != null) {
            // attempt to move closer to the target
            Vector3 targetVec = new Vector3(targetedEntity);
            Vector3 myPos = new Vector3(this);
            Vector3 thisToTarget = myPos.difference(targetVec);
            // I don't get around sqrt'ing here
            double distance = thisToTarget.getMagnitude();
            thisToTarget.scale(distanceToKeep / distance); // scale the vector to distanceToKeep
            myPos = targetVec.translate(thisToTarget);
            // this should be correct now...
            if(this.isCourseTraversable(myPos.x, myPos.y, myPos.z, distance)) {
                this.waypointX = myPos.x;
                this.waypointY = myPos.y;
                this.waypointZ = myPos.z;
                return;
            }
        }
        // otherwise, get a random point
        this.waypointX = this.posX + (double)((this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F);
        this.waypointY = this.posY + (double)((this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F);
        this.waypointZ = this.posZ + (double)((this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F);
    }


    @Override
    protected void updateEntityActionState()
    {
        if (!this.worldObj.isRemote && this.worldObj.difficultySetting == EnumDifficulty.PEACEFUL)
        {
            this.setDead();
        }

        this.despawnEntity();
        this.prevAttackCounter = this.attackCounter;
        double deltaX = this.waypointX - this.posX;
        double deltaY = this.waypointY - this.posY;
        double deltaZ = this.waypointZ - this.posZ;
        double distanceSq = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;

        if (this.targetedEntity != null && this.targetedEntity.isDead)
        {
            this.targetedEntity = null;
        }

        if (distanceSq < 1.0D || distanceSq > 3600.0D)
        {
            // find next waypoint?
            findWaypoint();
        }

        if (this.courseChangeCooldown-- <= 0)
        {
            this.courseChangeCooldown += this.rand.nextInt(5) + 2;
            distanceSq = (double)MathHelper.sqrt_double(distanceSq);

            if (this.isCourseTraversable(this.waypointX, this.waypointY, this.waypointZ, distanceSq))
            {
                this.motionX += deltaX / distanceSq * 0.1D;
                this.motionY += deltaY / distanceSq * 0.1D;
                this.motionZ += deltaZ / distanceSq * 0.1D;
            }
            else
            {
                this.waypointX = this.posX;
                this.waypointY = this.posY;
                this.waypointZ = this.posZ;
            }
        }

        if (this.targetedEntity == null || this.aggroCooldown-- <= 0)
        {
            // target locked?
            this.targetedEntity = this.worldObj.getClosestVulnerablePlayerToEntity(this, getVisionDistance());

            if (this.targetedEntity != null)
            {
                this.aggroCooldown = 20;
            }
        }

        double maxTargetDistance = 64.0D;

        if (this.targetedEntity != null && this.targetedEntity.getDistanceSqToEntity(this) < maxTargetDistance * maxTargetDistance)
        {
            this.faceEntity(this.targetedEntity, 10.0F, (float)this.getVerticalFaceSpeed());
            double accelX = this.targetedEntity.posX - this.posX;
            double accelY = this.targetedEntity.boundingBox.minY + (double)(this.targetedEntity.height / 2.0F) - (this.posY + (double)(this.height / 2.0F));
            double accelZ = this.targetedEntity.posZ - this.posZ;
            this.renderYawOffset = this.rotationYaw = -((float)Math.atan2(accelX, accelZ)) * 180.0F / (float)Math.PI;

            if (this.canEntityBeSeen(this.targetedEntity))
            {
                if (this.attackCounter == 10)
                {
                    // WTF?
                    // 1007 might be some sort of an ID
                    // playSoundAtEntity(entity, GalacticraftCore.TEXTURE_PREFIX + "ambience.scaryscape", 9.0F, 1.4F)
//                    this.worldObj.playSoundAtEntity(this, AmunRa.TEXTUREPREFIX + "mob.sentryblock.fire", 1.0F, 1.0F);
//                    this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1007, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
                    // charging?
                }

                ++this.attackCounter;

                if (this.attackCounter == 20)
                {
                    // another one. WTF is this?
                    //this.worldObj.playSoundAtEntity(this, AmunRa.TEXTUREPREFIX + "mob.sentryblock.fire", getSoundVolume(), 1.0F);
                    this.worldObj.playSoundAtEntity(this, getFiringSound(), getSoundVolume(), 1.0F);
                    // this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1008, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
                    // this seems to be an actual attack
                    performAttack(targetedEntity, accelX, accelY, accelZ);


                    // actual attack end
                    this.attackCounter = -40;
                }
            }
            else if (this.attackCounter > 0)
            {
                --this.attackCounter;
            }
        }
        else
        {
            this.rotationPitch = this.defaultPitch;
            this.renderYawOffset = this.rotationYaw = -((float)Math.atan2(this.motionX, this.motionZ)) * 180.0F / (float)Math.PI;

            if (this.attackCounter > 0)
            {
                --this.attackCounter;
            }
        }

        if (!this.worldObj.isRemote)
        {
            byte b1 = this.dataWatcher.getWatchableObjectByte(16);
            byte b0 = (byte)(this.attackCounter > 10 ? 1 : 0);

            if (b1 != b0)
            {
                this.dataWatcher.updateObject(16, Byte.valueOf(b0));
            }
        }
    }

    /**
     * True if the ghast has an unobstructed line of travel to the waypoint.
     */
    protected boolean isCourseTraversable(double p_70790_1_, double p_70790_3_, double p_70790_5_, double distance)
    {
        double relDeltaX = (this.waypointX - this.posX) / distance;
        double relDeltaY = (this.waypointY - this.posY) / distance;
        double relDeltaZ = (this.waypointZ - this.posZ) / distance;
        AxisAlignedBB axisalignedbb = this.boundingBox.copy();

        for (int i = 1; (double)i < distance; ++i)
        {
            axisalignedbb.offset(relDeltaX, relDeltaY, relDeltaZ);

            if (!this.worldObj.getCollidingBoundingBoxes(this, axisalignedbb).isEmpty())
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the volume for the sounds this mob makes.
     */
    @Override
    protected float getSoundVolume()
    {
        return 1.0F;
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    @Override
    public boolean getCanSpawnHere()
    {
        return this.rand.nextInt(20) == 0 && super.getCanSpawnHere() && this.worldObj.difficultySetting != EnumDifficulty.PEACEFUL;
    }

    /**
     * Will return how many at most can spawn in a chunk at once.
     */
    @Override
    public int getMaxSpawnedInChunk()
    {
        return 1;
    }

    abstract public String getFiringSound();

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
    public void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("ExplosionPower", this.explosionStrength);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);

        if (nbt.hasKey("ExplosionPower", 99))
        {
            this.explosionStrength = nbt.getInteger("ExplosionPower");
        }
    }

}
