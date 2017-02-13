package de.katzenpapst.amunra.item;

import java.util.List;

import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase;
import micdoodle8.mods.galacticraft.core.items.GCItems;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.entity.EntityBaseLaserArrow;

public abstract class ItemAbstractRaygun extends ItemElectricBase {



    // set to true for chargeMode, instead of single-shot mode, which would fire each time
    // the player rightclicks
    protected boolean chargeMode = false;

    public ItemAbstractRaygun(String assetName) {
        this.setUnlocalizedName(assetName);
        this.setTextureName(AmunRa.TEXTUREPREFIX + assetName);
        this.maxStackSize = 1;

        //batteryInUse = new ItemStack(GCItems.battery, 1);
        //batteryInUse.getTagCompound()
    }

    @Override
    public float getMaxElectricityStored(ItemStack theItem) {
        if(theItem.getTagCompound() == null) {
            theItem.setTagCompound(new NBTTagCompound());
        }
        if(theItem.getTagCompound().hasKey("maxEnergy")) {
            return theItem.getTagCompound().getFloat("maxEnergy");
        }

        ItemStack bat = getUsedBattery(theItem, false);
        float maxEnergy = ((ItemElectricBase)bat.getItem()).getMaxElectricityStored(bat);
        theItem.getTagCompound().setFloat("maxEnergy", maxEnergy);
        return maxEnergy;
        //return 15000; // fallback
    }

    @Override
    public CreativeTabs getCreativeTab()
    {
        return AmunRa.instance.arTab;
    }

    /**
     * Set the battery to use for this raygun. also sets the gun's energy level to that of the battery
     *
     * @param theItem
     * @param battery
     */
    public void setUsedBattery(ItemStack theItem, ItemStack battery) {
        if (theItem.getTagCompound() == null)
        {
            theItem.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound batteryStackCompound = new NBTTagCompound();
        battery.writeToNBT(batteryStackCompound);

        // NBTTagCompound batteryTag = battery.getTagCompound();
        //if(batteryTag == null) {
        //NBTTagCompound batteryTag = new NBTTagCompound();
        //}
        //batteryTag = battery.writeToNBT(batteryTag);
        // theItem.getTagCompound().setInteger("batteryID", Item.getIdFromItem(battery.getItem()));
        theItem.getTagCompound().setFloat("maxEnergy", ((ItemElectricBase)battery.getItem()).getMaxElectricityStored(battery));
        theItem.getTagCompound().setTag("batteryStack", batteryStackCompound);

        this.setElectricity(theItem, ((ItemElectricBase)battery.getItem()).getElectricityStored(battery));
    }

    /**
     * Returns the battery currently in the raygun as ItemStack. The ItemStack will be newly constructed
     *
     * @param theItem	The raygun ItemStack
     * @param setEnergy	if true, the result itemstack will also have the energy of the current raygun
     * @return
     */
    public ItemStack getUsedBattery(ItemStack theItem, boolean setEnergy) {
        if (theItem.getTagCompound() == null)
        {
            theItem.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound stackNBT = theItem.getTagCompound().getCompoundTag("batteryStack");
        ItemStack batteryStack = null;
        if(stackNBT == null) {
            // default?
            batteryStack = new ItemStack(GCItems.battery, 1, 0);
            //return null;
        } else {
            batteryStack = ItemStack.loadItemStackFromNBT(stackNBT);
            if(batteryStack == null) {
                batteryStack = new ItemStack(GCItems.battery, 1, 0);
            }
        }


        // ItemStack bat = new ItemStack(getUsedBatteryID(theItem), 1, 0);
        if(setEnergy) {
            ((ItemElectricBase)batteryStack.getItem()).setElectricity(batteryStack, this.getElectricityStored(theItem));
        }
        return batteryStack;
    }

    public Item getUsedBatteryID(ItemStack theItem) {
        if(theItem.getTagCompound().hasKey("batteryStack")) {
            NBTTagCompound stackNBT = theItem.getTagCompound().getCompoundTag("batteryStack");
            ItemStack batteryStack = ItemStack.loadItemStackFromNBT(stackNBT);

            return batteryStack.getItem();
        }
        return GCItems.battery;
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
        //int j = this.getMaxItemUseDuration(itemStack) - itemInUseCount;

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
        if(entityPlayer.capabilities.isCreativeMode || getElectricityStored(itemStack) >= getEnergyPerShot()) {

            entityPlayer.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
            if(!this.chargeMode) {
                fire(itemStack, entityPlayer, world);
            }
        } else {
            if (!world.isRemote) {
                world.playSoundAtEntity(entityPlayer, getEmptySound(), 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + 0.5F);

            }
        }

        return itemStack;
    }

    public float getEnergyPerShot() {
        return 300.0F;
    }

    protected String getFiringSound() {
        return AmunRa.TEXTUREPREFIX+"weapon.lasergun.shot";
    }

    protected String getEmptySound() {
        return AmunRa.TEXTUREPREFIX+"weapon.lasergun.empty";
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int stackNumber, boolean isBeingHeld) {
        ItemStack battery = this.getUsedBattery(stack, true);
        battery.getItem().onUpdate(battery, world, entity, stackNumber, isBeingHeld);
        // do I write the battery back in?
        // I'm somewhat afraid regarding the efficiency of this
        // this.setUsedBattery(stack, battery);
        // maybe this is better
        this.setElectricity(stack, ((ItemElectricBase)battery.getItem()).getElectricityStored(battery));
    }

    protected boolean fire(ItemStack itemStack, EntityPlayer entityPlayer, World world) {
        if(!entityPlayer.capabilities.isCreativeMode) {
            this.setElectricity(itemStack, this.getElectricityStored(itemStack) - this.getModifiedEnergyPerShot(itemStack));
        }
        if (!world.isRemote)
        {
            world.playSoundAtEntity(entityPlayer, getFiringSound(), 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + 0.5F);
            //LaserArrow entityarrow = new LaserArrow(world, entityPlayer);
            spawnProjectile(itemStack, entityPlayer, world);
        }
        return true;
    }

    protected float getModifiedEnergyPerShot(ItemStack stack)
    {
        float base = this.getEnergyPerShot();

        int efficiency = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack);
        // max level seems to be 5
        float relativeEff = ((float)efficiency) / 10.0F;

        base = base * (1.0F - relativeEff);


        return base;
    }

    protected void spawnProjectile(ItemStack itemStack, EntityPlayer entityPlayer, World world) {
        EntityBaseLaserArrow ent = createProjectile(itemStack, entityPlayer, world);

        // enchantment stuff


        world.spawnEntityInWorld(ent);
    }

    abstract protected EntityBaseLaserArrow createProjectile(ItemStack itemStack, EntityPlayer entityPlayer, World world);

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

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4)
    {
        super.addInformation(itemStack, entityPlayer, list, par4);

        Item batItem = getUsedBatteryID(itemStack);
        String s = "Power Storage: " + StatCollector.translateToLocal(batItem.getUnlocalizedName()+".name");

        list.add(s);
        //String s = ("" + StatCollector.translateToLocal(this.getUnlocalizedName() + ".name")).trim();


        //list.add(color + EnergyDisplayHelper.getEnergyDisplayS(joules) + "/" + EnergyDisplayHelper.getEnergyDisplayS(this.getMaxElectricityStored(itemStack)));
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
