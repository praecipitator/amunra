package de.katzenpapst.amunra.entity;

import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.mob.DamageSourceAR;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityLaserArrow extends EntityBaseLaserArrow {

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
        return 2.0F;
    }

    @Override
    protected boolean doesFireDamage() {
        return true;
    }

    @Override
    public ResourceLocation getTexture() {
        return arrowTextures;
    }

    @Override
    protected void onImpactBlock(World worldObj, int x, int y, int z) {
        Block block = worldObj.getBlock(x, y, z);
        int meta = worldObj.getBlockMetadata(x, y, z);

        // first tests first
        if(block == Blocks.water) {
            worldObj.setBlock(x, y, z, Blocks.air, 0, 3);
            this.playSound("random.fizz", 1.0F, 0.5F);
            return;
        }
        if(block == Blocks.ice) {
            worldObj.setBlock(x, y, z, Blocks.water, 0, 3);
            return;
        }

        ItemStack smeltResult = FurnaceRecipes.smelting().getSmeltingResult(new ItemStack(block, 1, meta));
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
                    worldObj.setBlock(x, y, z, b, smeltResult.getItemDamage(), 3);
                    return;
                }
            }
        }
        /*if(worldObj.getBlock(x, y+1, z) == Blocks.air) {
			//OxygenUtil.isAABBInBreathableAirBlock(world, bb)
			// no oxygen check for now
			worldObj.setBlock(x, y+1, z, Blocks.fire, 0, 3);
		}*/

    }

    @Override
    protected DamageSource getDamageSource() {
        if (this.shootingEntity == null)
        {
            return DamageSourceAR.causeLaserDamage("ar_heatray", this, this);// ("laserArrow", this, this).setProjectile();
        }
        return DamageSourceAR.causeLaserDamage("ar_heatray", this, this.shootingEntity);
    }

}
