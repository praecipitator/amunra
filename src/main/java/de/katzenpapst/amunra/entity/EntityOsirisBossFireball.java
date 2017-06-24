package de.katzenpapst.amunra.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityOsirisBossFireball extends EntityFireball {

    public float damage = 1.0F;

    public EntityOsirisBossFireball(World world) {
        super(world);
    }

    @Override
    protected void onImpact(MovingObjectPosition movingObjectPosition) {
        if (!this.worldObj.isRemote)
        {
            if (movingObjectPosition.entityHit != null && !(movingObjectPosition.entityHit instanceof EntityCreeper))
            {
                movingObjectPosition.entityHit.attackEntityFrom(DamageSource.causeFireballDamage(this, this.shootingEntity), damage);
                //ConfigManagerCore.hardMode ? 12.0F : 6.0F
            }


            this.worldObj.newExplosion((Entity) null, this.posX, this.posY, this.posZ, 1.0F, false, worldObj.getGameRules().getBoolean("mobGriefing"));
            this.setDead();
        }
    }

    public EntityOsirisBossFireball(World world, double x, double y, double z, double accelX, double accelY, double accelZ)
    {
        super(world);
        this.setSize(1.0F, 1.0F);
        this.setLocationAndAngles(x, y, z, this.rotationYaw, this.rotationPitch);
        this.setPosition(x, y, z);
        double d6 = (double)MathHelper.sqrt_double(accelX * accelX + accelY * accelY + accelZ * accelZ);
        this.accelerationX = accelX / d6 * 0.1D;
        this.accelerationY = accelY / d6 * 0.1D;
        this.accelerationZ = accelZ / d6 * 0.1D;
    }

    public EntityOsirisBossFireball(World world, EntityLivingBase target, double accelX, double accelY, double accelZ)
    {
        super(world);
        this.shootingEntity = target;
        this.setSize(1.0F, 1.0F);
        this.setLocationAndAngles(target.posX, target.posY, target.posZ, target.rotationYaw, target.rotationPitch);
        this.setPosition(this.posX, this.posY, this.posZ);
        //this.yOffset = 0.0F;
        this.motionX = this.motionY = this.motionZ = 0.0D;
        accelX += this.rand.nextGaussian() * 0.4D;
        accelY += this.rand.nextGaussian() * 0.4D;
        accelZ += this.rand.nextGaussian() * 0.4D;
        double d3 = (double)MathHelper.sqrt_double(accelX * accelX + accelY * accelY + accelZ * accelZ);
        this.accelerationX = accelX / d3 * 0.1D;
        this.accelerationY = accelY / d3 * 0.1D;
        this.accelerationZ = accelZ / d3 * 0.1D;
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
    public void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);

        nbt.setFloat("damage", damage);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);

        this.damage = nbt.getFloat("damage");
    }



}
