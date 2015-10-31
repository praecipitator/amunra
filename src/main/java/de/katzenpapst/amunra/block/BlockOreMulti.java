package de.katzenpapst.amunra.block;

import java.util.Random;

import de.katzenpapst.amunra.item.ItemDamagePair;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

public class BlockOreMulti extends BlockBasicMulti {

	/**
	 * The IDP containing what to drop
	 */
	protected ItemDamagePair droppedItems = null;

	/**
	 * Minimum amount to drop. Probably shouldn't be != 1...
	 */
	protected int baseDropRateMin = 1;
	/**
	 * Usually fortune 3 can give up to 4 items. This will be multiplied on that value
	 */
	protected float bonusDropMultiplier = 1;

	public BlockOreMulti(String name, Material mat) {
		super(name, mat);
		// TODO Auto-generated constructor stub
	}

	public boolean dropsSelf() {
		if(droppedItems == null)
			return true;
		return false;
	}

	@Override
	public int quantityDropped(int meta, int fortune, Random random)
	{
		if(dropsSelf()) {
			return 1;
		}
		int j = random.nextInt(fortune + 2) - 1;

        if (j < 0) {
            j = 0;
        }

        return (int) (this.quantityDropped(random) * (j + 1) * bonusDropMultiplier);

		//return Math.min(random.nextInt(3)+random.nextInt(10)*fortune, 9);
	}
	/**
     * Returns the quantity of items to drop on block destruction.
     * There is no metadata here, so if this stuff is called from the outside, I can't do shit
     */
    @Override
	public int quantityDropped(Random rand)
    {
    	if(dropsSelf()) {
			return 1;
		}
        return baseDropRateMin;
    }

    @Override
    public int damageDropped(int meta)
    {
    	if(dropsSelf()) {
			return meta;
		}
		return droppedItems.getDamage();
    }

    @Override
	public Item getItemDropped(int meta, Random random, int fortune)
    {
    	if(dropsSelf()) {
			return Item.getItemFromBlock(this);
		}
		return droppedItems.getItem();
    }

    public BlockOreMulti setDroppedItem(ItemDamagePair item) {
    	droppedItems = item;
    	return this;
    }

    public BlockOreMulti setMinDropRate(int val) {
    	baseDropRateMin = val;
    	return this;
    }

    public BlockOreMulti setBonusMultiplier(float val) {
    	bonusDropMultiplier = val;
    	return this;
    }

}
