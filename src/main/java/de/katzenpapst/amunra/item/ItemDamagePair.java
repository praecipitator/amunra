package de.katzenpapst.amunra.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemDamagePair {

	protected Item item;
	protected int damage;

	public ItemDamagePair(Item item, int damage) {
		this.item = item;
		this.damage = damage;
	}

	public Item getItem() {
		return item;
	}

	public int getDamage() {
		return damage;
	}

	public ItemStack getItemStack(int numItems) {
		return new ItemStack(item, numItems, damage);
	}

}
