package de.katzenpapst.amunra.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class EntityLaserArrow extends EntityBaseLaserArrow {

	public EntityLaserArrow(World world) {
		super(world);
		// TODO Auto-generated constructor stub
	}

	public EntityLaserArrow(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	public EntityLaserArrow(World world, EntityLivingBase shootingEntity,
			EntityLivingBase target, float randMod) {
		super(world, shootingEntity, target, randMod);
	}

	public EntityLaserArrow(World par1World,
			EntityLivingBase par2EntityLivingBase) {
		super(par1World, par2EntityLivingBase);
	}

	@Override
	protected float getSpeed() {
		return 3.0F;
	}

	@Override
	protected float getDamage() {
		return 1.5F;
	}

	@Override
	protected boolean doesFireDamage() {
		return true;
	}



}
