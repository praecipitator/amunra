package de.katzenpapst.amunra.block;

import java.util.Random;

import de.katzenpapst.amunra.item.ItemDamagePair;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

public class SubBlockDropItem extends SubBlock {

    protected Random rand = new Random();

    protected ItemDamagePair droppedItems = null;
    /**
     * Minimum amount to drop. Probably shouldn't be != 1...
     */
    protected int baseDropRateMin = 1;
    /**
     * Usually fortune 3 can give up to 4 items. This will be multiplied on that value
     */
    protected float bonusDropMultiplier = 1;

    protected int xpDropMin = 0;
    protected int xpDropMax = 0;

    protected boolean isValuable = false;

    public SubBlockDropItem(String name, String texture) {
        super(name, texture);
    }

    public SubBlockDropItem(String name, String texture, String tool, int harvestLevel) {
        super(name, texture, tool, harvestLevel);
    }

    public SubBlockDropItem(
            String name,
            String texture,
            String tool,
            int harvestLevel,
            float hardness,
            float resistance) {
        super(name, texture, tool, harvestLevel, hardness, resistance);
    }

    @Override
    public boolean dropsSelf() {
        return droppedItems == null;
    }

    public SubBlockDropItem setDroppedItem(ItemDamagePair item) {
        droppedItems = item;
        return this;
    }

    @Override
    public int quantityDropped(int meta, int fortune, Random random)
    {
        int j = random.nextInt(fortune + 2) - 1;

        if (j < 0) {
            j = 0;
        }

        int result = (int) (this.quantityDropped(random) * (j + 1) * bonusDropMultiplier);
        if(result < baseDropRateMin) {
            result = baseDropRateMin;
        }
        return result;

        //return Math.min(random.nextInt(3)+random.nextInt(10)*fortune, 9);
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     * There is no metadata here, so if this stuff is called from the outside, I can't do shit
     */
    @Override
    public int quantityDropped(Random rand)
    {
        return baseDropRateMin;
    }

    @Override
    public int damageDropped(int meta)
    {
        return droppedItems.getDamage();
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune)
    {
        return droppedItems.getItem();
    }

    public SubBlockDropItem setDroppedItem(Item item) {
        droppedItems = new ItemDamagePair(item, 0);
        return this;
    }

    public SubBlockDropItem setMinDropRate(int val) {
        baseDropRateMin = val;
        return this;
    }

    public SubBlockDropItem setBonusMultiplier(float val) {
        bonusDropMultiplier = val;
        return this;
    }

    public SubBlockDropItem setXpDrop(int dropMin, int dropMax) {
        xpDropMin = dropMin;
        xpDropMax = dropMax;
        return this;
    }

    @Override
    public int getExpDrop(IBlockAccess world, int metadata, int fortune) {
        if(!dropsSelf()) {
            if(xpDropMin <= xpDropMax) {
                return xpDropMin;
            }
            MathHelper.getRandomIntegerInRange(rand, xpDropMin, xpDropMax);
        }
        return 0;
    }

    @Override
    public boolean isValueable(int metadata) {
        return isValuable;
    }

    public SubBlockDropItem setIsValueable(boolean set) {
        isValuable = set;
        return this;
    }
}
