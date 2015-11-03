package de.katzenpapst.amunra.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class SubItemFood extends SubItem {

	/** Number of ticks to run while 'EnumAction'ing until result. */
    public final int itemUseDuration = 32; // wat?!
    /** The amount this food item heals the player. */
    private final int healAmount;
    private final float saturationModifier;
    /** If this field is true, the food can be consumed even if the player don't need to eat. */
    protected boolean alwaysEdible = false;
    /** represents the potion effect that will occurr upon eating this food. Set by setPotionEffect */
    private int potionId;
    /** set by setPotionEffect */
    private int potionDuration;
    /** set by setPotionEffect */
    private int potionAmplifier;
    /** probably of the set potion effect occurring */
    private float potionEffectProbability;

    // itemRegistry.addObject(367, "rotten_flesh", (new ItemFood(4, 0.1F, true)).setPotionEffect(Potion.hunger.id, 30, 0, 0.8F).setUnlocalizedName("rottenFlesh").setTextureName("rotten_flesh"));

	public SubItemFood(String name, String assetName, int healAmount, float saturationModifier) {
		super(name, assetName);
		this.healAmount = healAmount;
		this.saturationModifier = saturationModifier;
	}

	public SubItemFood(String name, String assetName, String info, int healAmount, float saturationModifier) {
		super(name, assetName, info);
		this.healAmount = healAmount;
		this.saturationModifier = saturationModifier;
	}


	@Override
	public ItemStack onEaten(ItemStack itemStack, World world, EntityPlayer player)
    {
        --itemStack.stackSize;

        player.getFoodStats().addStats(this.getHealAmount(itemStack), this.getSaturationModifier(itemStack));
        world.playSoundAtEntity(player, "random.burp", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
        this.onFoodEaten(itemStack, world, player);
        return itemStack;
    }

	protected void onFoodEaten(ItemStack itemStack, World world, EntityPlayer player)
    {
        if (!world.isRemote && this.potionId > 0 && world.rand.nextFloat() < this.potionEffectProbability)
        {
            player.addPotionEffect(new PotionEffect(this.potionId, this.potionDuration * 20, this.potionAmplifier));
        }
    }

	/**
     * How long it takes to use or consume an item
     */
    @Override
	public int getMaxItemUseDuration(ItemStack itemStack)
    {
        return 32;
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    @Override
	public EnumAction getItemUseAction(ItemStack itemStack)
    {
        return EnumAction.eat;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    @Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer)
    {
        if (entityPlayer.canEat(this.alwaysEdible))
        {
            entityPlayer.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
        }

        return itemStack;
    }

    public int getHealAmount(ItemStack itemStack)
    {
        return this.healAmount;
    }

    public float getSaturationModifier(ItemStack itemStack)
    {
        return this.saturationModifier;
    }

    /**
     * sets a potion effect on the item. Args: int potionId, int duration (will be multiplied by 20), int amplifier,
     * float probability of effect happening
     */
    public SubItemFood setPotionEffect(int potionId, int duration, int amplifier, float probability)
    {
        this.potionId = potionId;
        this.potionDuration = duration;
        this.potionAmplifier = amplifier;
        this.potionEffectProbability = probability;
        return this;
    }

    /**
     * Set the field 'alwaysEdible' to true, and make the food edible even if the player don't need to eat.
     */
    public SubItemFood setAlwaysEdible()
    {
        this.alwaysEdible = true;
        return this;
    }
}
