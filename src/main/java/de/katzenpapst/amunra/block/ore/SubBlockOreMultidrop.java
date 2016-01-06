package de.katzenpapst.amunra.block.ore;

import java.util.ArrayList;

import de.katzenpapst.amunra.item.ItemDamagePair;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class SubBlockOreMultidrop extends SubBlockOre {

	public class DroppedItem {
		// the item
		public Item item;
		// the item's metadata
		public int metadata;
		// min amount to drop
		public int minDrop;
		// max amount to drop. relevant for fortune
		public int maxDrop;
		// the probability to be evaluated at all. fortune will be multiplied onto this
		public float probability = 1;

		public DroppedItem (Item item, int meta, int minDrop, int maxDrop, float probability) {
			this.item = item;
			this.metadata = meta;
			this.minDrop = minDrop;
			this.maxDrop = maxDrop;
			this.probability = probability;
		}
	}

	protected ArrayList<DroppedItem> dropList = new ArrayList<DroppedItem>();

	public SubBlockOreMultidrop(String name, String texture) {
		super(name, texture);
	}

	public SubBlockOreMultidrop addDroppedItem(Item item, int metadata, int minDrop, int maxDrop, float probability) {
		dropList.add(new DroppedItem(item, metadata, minDrop, maxDrop, probability));
		return this;
	}

	public SubBlockOreMultidrop addDroppedItem(Item item, int metadata, int minDrop, int maxDrop) {
		dropList.add(new DroppedItem(item, metadata, minDrop, maxDrop, 1));
		return this;
	}

	public SubBlockOreMultidrop addDroppedItem(ItemDamagePair idp, int minDrop, int maxDrop) {
		addDroppedItem(idp.getItem(), idp.getDamage(), minDrop, maxDrop);
		return this;
	}

	public SubBlockOreMultidrop addDroppedItem(ItemDamagePair idp, int minDrop, int maxDrop, float probability) {
		addDroppedItem(idp.getItem(), idp.getDamage(), minDrop, maxDrop, probability);
		return this;
	}

	@Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
    {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		for(DroppedItem di: dropList) {
			if(di.probability < 1) {
				float effectiveProb = di.probability*fortune;
				if(effectiveProb < 1) {
					if(this.rand.nextFloat() >= effectiveProb) {
						continue; // skip this
					}
				}
			}
			float bonusDrop = Math.round(fortune*this.rand.nextInt(di.maxDrop-di.minDrop+1)/3.0F);
			int numDrops = (int) (bonusDrop+di.minDrop);
			if(numDrops > 0) {
				ret.add(new ItemStack(di.item, numDrops, di.metadata));
			}
		}
        return ret;
    }

	@Override
	public boolean dropsSelf() {
		return dropList.size() == 0;
	}

}
