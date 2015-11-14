package de.katzenpapst.amunra.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.item.ItemDamagePair;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class BlockOreVariable extends BlockBasicMulti {

	// the subblocks will be the different stones, the main block will have the overlay
	// otherwise, the subblocks will have priority in as much as possible as usual
	// kinda sucks that I will still have to use subblocks for vanilla blocks, but meh
/*
	/**
	 * The IDP containing what to drop
	 */
	protected ItemDamagePair droppedItems = null;

	/**
	 * Minimum amount to drop. Probably shouldn't be != 1...
	 * /
	protected int baseDropRateMin = 1;
	/**
	 * Usually fortune 3 can give up to 4 items. This will be multiplied on that value
	 * /
	protected float bonusDropMultiplier = 1;

	protected int xpDropMin = 0;
	protected int xpDropMax = 0;

	//for xp drop
	private Random rand = new Random();

	protected String oreDictName = null;
*/
	public BlockOreVariable(String name, String texture, Material mat) {
		super(name, mat);
		this.textureName = texture;
	}

	public IIcon getActualBlockIcon() {
		return this.blockIcon;
	}

	@Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
		super.registerBlockIcons(par1IconRegister);
		this.blockIcon = par1IconRegister.registerIcon(this.textureName);
    }

	/**
	 * Registers the block with the GameRegistry and sets the harvestlevels for all subblocks
	 */
	/*@Override
	public void register() {
		super.register();
		if(this.oreDictName != null) {
			OreDictionary.registerOre(this.oreDictName, new ItemStack(this, 1, OreDictionary.WILDCARD_VALUE));
		}
	}*/

	/*
	@Override
	public int quantityDropped(int meta, int fortune, Random random)
	{
		int j = random.nextInt(fortune + 2) - 1;

        if (j < 0) {
            j = 0;
        }

        return (int) (this.quantityDropped(random) * (j + 1) * bonusDropMultiplier);

		//return Math.min(random.nextInt(3)+random.nextInt(10)*fortune, 9);
	}

	@Override
	public int quantityDropped(Random rand)
    {
        return baseDropRateMin;
    }

	@Override
    public int damageDropped(int meta)
    {
		if(droppedItems == null) {
			return meta;
		}
		return droppedItems.getDamage();
    }

    @Override
	public Item getItemDropped(int meta, Random random, int fortune)
    {
    	if(droppedItems == null) {
    		return Item.getItemFromBlock(this);
    	}
		return droppedItems.getItem();
    }

    public BlockOreVariable setDroppedItem(ItemDamagePair item) {
    	droppedItems = item;
    	return this;
    }

    public BlockOreVariable setDroppedItem(Item item) {
    	droppedItems = new ItemDamagePair(item, 0);
    	return this;
    }

    public BlockOreVariable setMinDropRate(int val) {
    	baseDropRateMin = val;
    	return this;
    }

    public BlockOreVariable setBonusMultiplier(float val) {
    	bonusDropMultiplier = val;
    	return this;
    }

    public BlockOreVariable setXpDrop(int dropMin, int dropMax) {
    	xpDropMin = dropMin;
    	xpDropMax = dropMax;
    	return this;
    }

    public BlockOreVariable setOredictName(String name) {
    	this.oreDictName = name;
    	return this;
    }
    */

	/**
     * The type of render function that is called for this block
     */
    @Override
    @SideOnly(Side.CLIENT)
	public int getRenderType()
    {
        return AmunRa.multiOreRendererId;
    }

    /*
    @Override
	public int getExpDrop(IBlockAccess world, int metadata, int fortune) {
    	if(droppedItems != null) {
    		if(xpDropMin <= xpDropMax) {
    			return xpDropMin;
    		}
    		MathHelper.getRandomIntegerInRange(rand, xpDropMin, xpDropMax);
    	}
    	return 0;
    }
    */

    @Override
	public boolean isValueable(int metadata) {
		return true;
	}


}
