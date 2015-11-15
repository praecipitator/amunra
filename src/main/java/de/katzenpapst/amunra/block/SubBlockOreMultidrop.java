package de.katzenpapst.amunra.block;

import java.util.ArrayList;

import de.katzenpapst.amunra.item.ItemDamagePair;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class SubBlockOreMultidrop extends SubBlockOre {

	public class DroppedItem {
		public Item item;
		public int metadata;
		public int minDrop;
		public int maxDrop;

		public DroppedItem (Item item, int meta, int minDrop, int maxDrop) {
			this.item = item;
			this.metadata = meta;
			this.minDrop = minDrop;
			this.maxDrop = maxDrop;
		}
	}

	protected ArrayList<DroppedItem> dropList = new ArrayList<DroppedItem>();

	public SubBlockOreMultidrop(String name, String texture) {
		super(name, texture);
	}

	public SubBlockOreMultidrop addDroppedItem(Item item, int metadata, int minDrop, int maxDrop) {
		dropList.add(new DroppedItem(item, metadata, minDrop, maxDrop));
		return this;
	}

	public SubBlockOreMultidrop addDroppedItem(ItemDamagePair idp, int minDrop, int maxDrop) {
		addDroppedItem(idp.getItem(), idp.getDamage(), minDrop, maxDrop);
		return this;
	}

	@Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
    {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		for(DroppedItem di: dropList) {
			float bonusDrop = Math.round(fortune*this.rand.nextInt(di.maxDrop-di.minDrop+1)/3.0F);
			ret.add(new ItemStack(di.item, (int) (di.minDrop+bonusDrop), di.metadata));
		}
        return ret;
    }

	@Override
	public boolean dropsSelf() {
		return dropList.size() == 0;
	}

}
