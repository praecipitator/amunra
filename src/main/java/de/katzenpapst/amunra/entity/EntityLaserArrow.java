package de.katzenpapst.amunra.entity;

import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.mob.DamageSourceAR;
import de.katzenpapst.amunra.world.WorldHelper;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.core.util.OxygenUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityLaserArrow extends EntityBaseLaserArrow {

    protected float damage = 2.0F;

    protected boolean doesFireDamage = true;


    private static final ResourceLocation arrowTextures = new ResourceLocation(AmunRa.ASSETPREFIX, "textures/entity/laserarrow.png");

    public EntityLaserArrow(World world, EntityLivingBase shooter, Vector3 startVec, EntityLivingBase target) {
        super(world, shooter, startVec, target);
    }

    public EntityLaserArrow(World world) {
        super(world);
        // TODO Auto-generated constructor stub
    }

    public EntityLaserArrow(World world, EntityLivingBase shooter, double startX, double startY, double startZ)
    {
        super(world, shooter, startX, startY, startZ);
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
        return damage;
    }

    public void setDoesFireDamage(boolean set)
    {
        this.doesFireDamage = set;
    }

    public void setDamage(float newDmg) {
        damage = newDmg;
    }

    @Override
    protected boolean doesFireDamage() {
        return doesFireDamage;
    }

    @Override
    public ResourceLocation getTexture() {
        return arrowTextures;
    }

    @Override
    protected int getEntityDependentDamage(Entity ent, int regularDamage) {
        if(ent instanceof EntityBlaze) {
            return Math.max(regularDamage / 2, 1);
        }
        return regularDamage;
    }

    @Override
    protected void onImpactBlock(World worldObj, BlockPos pos) {
        IBlockState state =  worldObj.getBlockState(pos);
        Block block = state.getBlock();//worldObj.getBlock(pos);
        int meta = block.getMetaFromState(state);//state.get//worldObj.getBlockMetadata(x, y, z);

        // first tests first

        if(block == Blocks.ice) {
            worldObj.setBlockState(pos, Blocks.water.getDefaultState());
            // worldObj.setBlock(x, y, z, Blocks.water, 0, 3);
            return;
        }

        if(block == Blocks.snow || block == Blocks.snow_layer) {
            worldObj.setBlockState(pos, Blocks.air.getDefaultState());
            //worldObj.setBlock(x, y, z, Blocks.air, 0, 3);
            return;
        }


        ItemStack smeltResult = FurnaceRecipes.instance().getSmeltingResult(new ItemStack(block, 1, meta));
        if(smeltResult != null) {
            int blockId = Item.getIdFromItem(smeltResult.getItem());
            if(blockId > 0) {
                Block b = Block.getBlockById(blockId);
                if(b != Blocks.air) {
                    /**
                     * Sets the block ID and metadata at a given location. Args: X, Y, Z, new block ID, new metadata, flags. Flag 1 will
                     * cause a block update. Flag 2 will send the change to clients (you almost always want this). Flag 4 prevents the
                     * block from being re-rendered, if this is a client world. Flags can be added together.
                     */
                    worldObj.setBlockState(pos, b.getStateFromMeta(smeltResult.getItemDamage()));
                    //worldObj.setBlock(x, y, z, b, smeltResult.getItemDamage(), 3);
                    return;
                }
            }
        }
        if(OxygenUtil.noAtmosphericCombustion(worldObj.provider)) {

            if(OxygenUtil.isAABBInBreathableAirBlock(worldObj, new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(),
                    pos.getX()+1, pos.getY()+1, pos.getZ()+1))) {
                WorldHelper.setFireToBlock(worldObj, pos, posX, posY, posZ);
            }
        } else {
            WorldHelper.setFireToBlock(worldObj, pos, posX, posY, posZ);
        }
            //OxygenUtil.isInOxygenBlock(world, bb)
            //if(Blocks.fire.getFlammability(world, x, y, z, face))
            //if(block.isFlammable(world, x, y, z, face))

        /*if(worldObj.getBlock(x, y+1, z) == Blocks.air) {
			//OxygenUtil.isAABBInBreathableAirBlock(world, bb)
			// no oxygen check for now
			worldObj.setBlock(x, y+1, z, Blocks.fire, 0, 3);
		}*/

    }
/*
    protected void setFireToBlock(World worldObj, int x, int y, int z) {
        // omg

        double deltaX = x+0.5 - posX;
        double deltaY = y+0.5 - posY;
        double deltaZ = z+0.5 - posZ;

        double deltaXabs = Math.abs(deltaX);
        double deltaYabs = Math.abs(deltaY);
        double deltaZabs = Math.abs(deltaZ);

        if(deltaXabs > deltaYabs) {
            if(deltaXabs > deltaZabs) {
                if(deltaX < 0) {
                    worldObj.setBlockState(new BlockPos(x+1, y, z), Blocks.fire.getDefaultState());
                } else {
                    worldObj.setBlockState(new BlockPos(x-1, y, z), Blocks.fire.getDefaultState());
                }
            } else {
                if(deltaZ < 0) {
                    worldObj.setBlockState(new BlockPos(x, y, z+1), Blocks.fire.getDefaultState());
                } else {
                    worldObj.setBlockState(new BlockPos(x, y, z-1), Blocks.fire.getDefaultState());
                }
            }
        } else {
            if(deltaYabs > deltaZabs) {
                if(deltaY < 0) {
                    worldObj.setBlockState(new BlockPos(x, y+1, z), Blocks.fire.getDefaultState());
                } else {
                    // is there even fire from below?
                    worldObj.setBlockState(new BlockPos(x, y-1, z), Blocks.fire.getDefaultState());
                }
            } else {
                if(deltaZ < 0) {
                    worldObj.setBlockState(new BlockPos(x, y, z+1), Blocks.fire.getDefaultState());
                } else {
                    worldObj.setBlockState(new BlockPos(x, y, z-1), Blocks.fire.getDefaultState());
                }
            }
        }

    }
    */

    @Override
    protected void onPassThrough(BlockPos pos) {
        IBlockState state = worldObj.getBlockState(pos);
        Block b = state.getBlock();//worldObj.getBlock(x, y, z);

        if(b == Blocks.water) {
            this.worldObj.setBlockState(pos, Blocks.air.getDefaultState());
            this.playSound("random.fizz", 0.7F, 1.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
            inWater = false;
        }
    }

    @Override
    protected DamageSource getDamageSource() {
        if (this.shootingEntity == null)
        {
            return DamageSourceAR.causeLaserDamage("ar_heatray", this, this);// ("laserArrow", this, this).setProjectile();
        }
        return DamageSourceAR.causeLaserDamage("ar_heatray", this, this.shootingEntity);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readEntityFromNBT(nbttagcompound);
        damage = nbttagcompound.getFloat("damage");
        doesFireDamage = nbttagcompound.getBoolean("fireDmg");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeEntityToNBT(nbttagcompound);
        nbttagcompound.setFloat("damage", damage);
        nbttagcompound.setBoolean("fireDmg", doesFireDamage);
    }

}
