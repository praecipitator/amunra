package de.katzenpapst.amunra.entity;

import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.mob.DamageSourceAR;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityCryoArrow extends EntityBaseLaserArrow {

    private static final ResourceLocation arrowTextures = new ResourceLocation(AmunRa.ASSETPREFIX, "textures/entity/cryoarrow.png");


    public EntityCryoArrow(World world) {
        super(world);
    }

    public EntityCryoArrow(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    protected int getEntityDependentDamage(Entity ent, int regularDamage) {
        if(ent instanceof EntityBlaze) {
            return regularDamage * 2;
        }
        return regularDamage;
    }

    public EntityCryoArrow(World world, EntityLivingBase shootingEntity,
            EntityLivingBase target, float randMod) {
        super(world, shootingEntity, target, randMod);
    }

    @Override
    protected void onPassThrough(BlockPos pos) {
        IBlockState state = worldObj.getBlockState(pos);
        Block b = state.getBlock();

        if(b == Blocks.water) {
            this.worldObj.setBlockState(pos, Blocks.ice.getDefaultState());
            inWater = false;
        }
        if(b == Blocks.lava) {
            this.playSound("random.fizz", 0.7F, 1.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
            this.worldObj.setBlockState(pos, Blocks.obsidian.getDefaultState());
        }

        //this.worldObj.setBlock(x, y, z, Blocks.ice);

        //
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
        return 1.0F;
    }

    @Override
    protected boolean doesFireDamage() {
        return false;
    }

    @Override
    public ResourceLocation getTexture() {
        return arrowTextures;
    }

    @Override
    protected void onImpactEntity(MovingObjectPosition mop) {
        if(mop.entityHit instanceof EntityLivingBase) {
            // setPotionEffect(Potion.poison.id, 30, 2, 1.0F);
            if(((EntityLivingBase)mop.entityHit).isBurning()) {
                ((EntityLivingBase)mop.entityHit).extinguish();
            }
            ((EntityLivingBase)mop.entityHit).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100, 3));

            // how?
            //((EntityLivingBase)mop).getEntityAttribute(p_110148_1_)
            //((EntityLivingBase)mop.entityHit).addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 500, 3));
        }
        //mop.entityHit
        // player.addPotionEffect(new PotionEffect(this.potionId, this.potionDuration * 20, this.potionAmplifier));
    }

    @Override
    protected void onImpactBlock(World worldObj, BlockPos pos) {
        IBlockState state = worldObj.getBlockState(pos);
        Block b = state.getBlock();


        /*if(block == Blocks.water) {
			worldObj.setBlock(x, y, z, Blocks.ice);
		} else*/ if(b == Blocks.lava) {
		    worldObj.setBlockState(pos, Blocks.obsidian.getDefaultState());
		} else if(b == Blocks.fire) {
		    worldObj.setBlockState(pos, Blocks.air.getDefaultState());
		} else {
		    BlockPos newPos = new BlockPos(pos.getX(), pos.getY()+1, pos.getZ());
		    if(worldObj.getBlockState(newPos).getBlock() == Blocks.fire) {
	            worldObj.setBlockState(newPos, Blocks.air.getDefaultState());
	        }
		}
    }

    @Override
    protected DamageSource getDamageSource() {
        if (this.shootingEntity == null)
        {
            return DamageSourceAR.causeLaserDamage("ar_coldray", this, this);// ("laserArrow", this, this).setProjectile();
        }
        return DamageSourceAR.causeLaserDamage("ar_coldray", this, this.shootingEntity);
    }

}
