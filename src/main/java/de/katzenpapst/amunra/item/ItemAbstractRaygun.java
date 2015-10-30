package de.katzenpapst.amunra.item;

import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.entity.EntityBaseLaserArrow;
import de.katzenpapst.amunra.entity.EntityLaserArrow;

public class ItemAbstractRaygun extends ItemElectricBase {


	// protected IIcon itemEmptyIcon;

	protected float energyPerShot = 500;

	// set to true for chargeMode, instead of single-shot mode, which would fire each time
	// the player rightclicks
	protected boolean chargeMode = false;

	public ItemAbstractRaygun(String assetName) {
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

	@Override
    public void onCreated(ItemStack itemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
		// important NOT to call the parent for this, because there are crafting recipes
		// which create non-empty rayguns
        // this.setElectricity(itemStack, 0);
    }

	/**
     * called when the player releases the use item button. Args: itemstack, world, entityplayer, itemInUseCount
     */

    @Override
	public void onPlayerStoppedUsing(ItemStack itemStack, World world, EntityPlayer entityPlayer, int itemInUseCount)
    {
    	if(!this.chargeMode) {
    		return;
    	}
        int j = this.getMaxItemUseDuration(itemStack) - itemInUseCount;

        this.fire(itemStack, entityPlayer, world);
    }




    /**
     * How long it takes to use or consume an item
     */

    @Override
	public int getMaxItemUseDuration(ItemStack itemStack)
    {
        return 72000;
    }

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
    		if(!this.chargeMode) {
    			fire(itemStack, entityPlayer, world);
    		}
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
        //this.itemEmptyIcon = iconRegister.registerIcon(this.getIconString() + "_empty");
    }

    @Override
    public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining)
    {
    	return super.getIcon(stack, renderPass, player, usingItem, useRemaining);
    	/*
        final int count2 = useRemaining / 2;

        switch (count2 % 5)
        {
        case 0:
            if (useRemaining == 0)
            {
                return this.icons[0];
            }
            return this.icons[4];
        case 1:
            return this.icons[3];
        case 2:
            return this.icons[2];
        case 3:
            return this.icons[1];
        case 4:
            return this.icons[0];
        }

        return this.icons[0];*/
    }

}
