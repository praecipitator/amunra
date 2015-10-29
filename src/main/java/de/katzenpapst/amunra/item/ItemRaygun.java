package de.katzenpapst.amunra.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.entity.EntityBaseLaserArrow;
import de.katzenpapst.amunra.entity.EntityLaserArrow;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase;

public class ItemRaygun extends ItemElectricBase {

	protected IIcon itemEmptyIcon;

	protected float energyPerShot = 500;

	public ItemRaygun(String assetName) {
		this.setUnlocalizedName(assetName);
        this.setTextureName(AmunRa.TEXTUREPREFIX + assetName);
        this.maxStackSize = 1;
	}

	@Override
	public float getMaxElectricityStored(ItemStack theItem) {
		return 15000;
	}

	@Override
    public CreativeTabs getCreativeTab()
    {
    	return AmunRa.instance.arTab;
    }

	/**
     * called when the player releases the use item button. Args: itemstack, world, entityplayer, itemInUseCount
     */
	/*
    @Override
	public void onPlayerStoppedUsing(ItemStack itemStack, World world, EntityPlayer entityPlayer, int itemInUseCount)
    {
        int j = this.getMaxItemUseDuration(itemStack) - itemInUseCount;

        ArrowLooseEvent event = new ArrowLooseEvent(entityPlayer, itemStack, j);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled())
        {
            return;
        }
        j = event.charge;

        boolean flag = entityPlayer.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, itemStack) > 0;

        if (flag || entityPlayer.inventory.hasItem(Items.arrow))
        {
            float f = j / 20.0F;
            f = (f * f + f * 2.0F) / 3.0F;

            if (f < 0.1D)
            {
                return;
            }

            if (f > 1.0F)
            {
                f = 1.0F;
            }

            EntityArrow entityarrow = new EntityArrow(world, entityPlayer, f * 2.0F);

            if (f == 1.0F)
            {
                entityarrow.setIsCritical(true);
            }

            int k = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, itemStack);

            if (k > 0)
            {
                entityarrow.setDamage(entityarrow.getDamage() + k * 0.5D + 0.5D);
            }

            int l = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, itemStack);

            if (l > 0)
            {
                entityarrow.setKnockbackStrength(l);
            }

            if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, itemStack) > 0)
            {
                entityarrow.setFire(100);
            }

            itemStack.damageItem(1, entityPlayer);
            world.playSoundAtEntity(entityPlayer, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

            if (flag)
            {
                entityarrow.canBePickedUp = 2;
            }
            else
            {
                entityPlayer.inventory.consumeInventoryItem(Items.arrow);
            }

            if (!world.isRemote)
            {
                world.spawnEntityInWorld(entityarrow);
            }
        }
    }
    */

    /*
    @Override
	public ItemStack onEaten(ItemStack itemStack, World world, EntityPlayer player)
    {
        return itemStack;
    }*/

    /**
     * How long it takes to use or consume an item
     */
	/*
    @Override
	public int getMaxItemUseDuration(ItemStack itemStack)
    {
        return 72000;
    }*/

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    @Override
	public EnumAction getItemUseAction(ItemStack p_77661_1_)
    {
        return EnumAction.bow;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    @Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer)
    {
    	/*
        ArrowNockEvent event = new ArrowNockEvent(entityPlayer, itemStack);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled())
        {
            return event.result;
        }*/
    	if(entityPlayer.capabilities.isCreativeMode || getElectricityStored(itemStack) >= energyPerShot) {

    		entityPlayer.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
    		fire(itemStack, entityPlayer, world);
    	}

        return itemStack;
    }

    protected boolean fire(ItemStack itemStack, EntityPlayer entityPlayer, World world) {
    	if(!entityPlayer.capabilities.isCreativeMode) {
    		this.setElectricity(itemStack, this.getElectricityStored(itemStack) - this.energyPerShot);
    	}
    	if (!world.isRemote)
        {
    		world.playSoundAtEntity(entityPlayer, AmunRa.TEXTUREPREFIX+"weapon.lasergun.shot", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + 0.5F);
    		//LaserArrow entityarrow = new LaserArrow(world, entityPlayer);
    		EntityBaseLaserArrow ent = new EntityLaserArrow(world, entityPlayer);
    		world.spawnEntityInWorld(ent);
        }
    	return true;
    }

    /**
     * Return the enchantability factor of the item, most of the time is based on material.
     */
    @Override
	public int getItemEnchantability()
    {
        return 0;
    }

    @Override
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
    	this.itemIcon = iconRegister.registerIcon(this.getIconString());
        this.itemEmptyIcon = iconRegister.registerIcon(this.getIconString() + "_empty");
    }

    /**
     * used to cycle through icons based on their used duration, i.e. for the bow
     */
    /*
    @SideOnly(Side.CLIENT)
    public IIcon getItemIconForUseDuration(int duration)
    {
    	if(duration > 0) {
    		return this.itemIcon;
    	}
    	return this.itemEmptyIcon;
        //return this.iconArray[p_94599_1_];
    }
*/


}
