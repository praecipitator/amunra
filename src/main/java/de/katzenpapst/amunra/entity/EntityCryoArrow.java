package de.katzenpapst.amunra.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class EntityCryoArrow extends EntityBaseLaserArrow {

	public EntityCryoArrow(World world) {
		super(world);
	}

	public EntityCryoArrow(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	public EntityCryoArrow(World world, EntityLivingBase shootingEntity,
			EntityLivingBase target, float randMod) {
		super(world, shootingEntity, target, randMod);
	}

	public EntityCryoArrow(World par1World,
			EntityLivingBase par2EntityLivingBase) {
		super(par1World, par2EntityLivingBase);
	}

	@Override
	protected float getSpeed() {
		return 3.0F;
	}

	@Override
	protected float getDamage() {
		// TODO Auto-generated method stub
		return 1.0F;
	}

	@Override
	protected boolean doesFireDamage() {
		// TODO Auto-generated method stub
		return false;
	}

}
