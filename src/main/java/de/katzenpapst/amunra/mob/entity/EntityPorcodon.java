package de.katzenpapst.amunra.mob.entity;

import java.util.ArrayList;

import de.katzenpapst.amunra.item.ARItems;
import de.katzenpapst.amunra.mob.DamageSourceAR;
import micdoodle8.mods.galacticraft.api.entity.IEntityBreathable;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace;
import micdoodle8.mods.galacticraft.api.world.IAtmosphericGas;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIControlledByPlayer;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityPorcodon extends EntityAnimal implements IEntityBreathable, IEntityNonOxygenBreather {

	private World lastCheckedWorld = null; 
	private boolean canBreathInCurWorld = false;
	
	private ItemStack dropItem = null;
	
	final private int explosionRadius = 3;
	final private int fuseTime = 30;
	
	private boolean isIgnited = false;
	private int timeSinceIgnited = 0;
	
	public EntityPorcodon(World curWorld) {
		super(curWorld);
		
		this.setSize(0.9F, 0.9F);
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIPanic(this, 1.25D));
        // this.tasks.addTask(2, this.aiControlledByPlayer = new EntityAIControlledByPlayer(this, 0.3F));
        this.tasks.addTask(3, new EntityAIMate(this, 1.0D));
        //this.tasks.addTask(4, new EntityAITempt(this, 1.2D, Items.carrot_on_a_stick, false));
        // this.tasks.addTask(4, new EntityAITempt(this, 1.2D, Items.carrot, false));
        this.tasks.addTask(5, new EntityAIFollowParent(this, 1.1D));
        this.tasks.addTask(6, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        
        dropItem = ARItems.baseItem.getItemStack("porcodonMeat", 1);
	}

	
	/**
     * Returns true if the newer Entity AI code should be run
     */
	@Override
    public boolean isAIEnabled()
    {
        return true;
    }
	
	@Override
	protected void updateAITasks()
    {
        super.updateAITasks();
    }
	
	@Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(15.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
    }
	
	/**
     * returns true if all the conditions for steering the entity are met. For pigs, this is true if it is being ridden
     * by a player and the player is holding a carrot-on-a-stick
     */
	@Override
    public boolean canBeSteered()
    {
        return false;
    }
	
	/**
	 * @TODO figure out what this does o_O
	 */
	@Override
	protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(16, Byte.valueOf((byte)0));
    }
	
	/**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return "mob.pig.say";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.pig.say";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "mob.pig.death";
    }

    protected void func_145780_a(int p_145780_1_, int p_145780_2_, int p_145780_3_, Block p_145780_4_)
    {
        this.playSound("mob.pig.step", 0.15F, 1.0F);
    }
    
    protected Item getDropItem()
    {
        return null;//this.isBurning() ? Items.cooked_porkchop : Items.porkchop;
    }

    /**
     * Drop 0-2 items of this living's type. 
     * @param par1 - Whether this entity has recently been hit by a player.
     * @param par2 - Level of Looting used to kill this mob.
     */
    protected void dropFewItems(boolean hitByPlayer, int lootLevel)
    {
    	// drop at least one meat
        int j = this.rand.nextInt(3) + 1 + this.rand.nextInt(1 + lootLevel);
        ItemStack toDrop = this.dropItem.copy();
        toDrop.stackSize = j;
        this.entityDropItem(toDrop, 0.0F);

    }
	
	/**
	 * Misnomer imho, this function should be called "doesNotRequireOxygen"
	 */
	@Override
	public boolean canBreath() {
		return true; 
	}

	@Override
	public EntityAgeable createChild(EntityAgeable p_90011_1_) {
		return new EntityPorcodon(this.worldObj);
	}
	
	/**
     * Checks if the parameter is an item which this animal can be fed to breed it (wheat, carrots or seeds depending on
     * the animal type)
     */
	@Override
    public boolean isBreedingItem(ItemStack p_70877_1_)
    {
        return false;//p_70877_1_ != null && p_70877_1_.getItem() == Items.carrot;
    }


	@Override
	public boolean canBreatheIn(ArrayList<IAtmosphericGas> atmosphere,
			boolean isInSealedArea) {
		boolean hasOxygen = isInSealedArea || atmosphere.contains(IAtmosphericGas.OXYGEN);
		
		// add stuff if oxygen exists
		if(hasOxygen && !isIgnited) {
			ignite();
		}
		if(!hasOxygen && isIgnited) {
			unIgnite();
		}
		
		return atmosphere.contains(IAtmosphericGas.METHANE);
	}
	
	private void ignite() {
		this.isIgnited = true;
		this.timeSinceIgnited = 0;
		this.playSound("creeper.primed", 1.0F, 0.5F);
	}
	
	private void unIgnite() {
		this.isIgnited = false;
		this.timeSinceIgnited = 0;
		this.playSound("random.fizz", 1.0F, 0.5F);
	}
	
	/**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        if (this.isEntityAlive() && isIgnited)
        {
            this.timeSinceIgnited++;

            if (this.timeSinceIgnited >= this.fuseTime)
            {
                this.timeSinceIgnited = this.fuseTime;
                this.explode();
            }
        }

        super.onUpdate();
    }
	
	private void explode()
    {
        if (!this.worldObj.isRemote)
        {
            boolean flag = this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");

            
            this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, (float)this.explosionRadius, flag);
            
            // why is this only in the if here?
            this.setDead();
        }
    }

}
