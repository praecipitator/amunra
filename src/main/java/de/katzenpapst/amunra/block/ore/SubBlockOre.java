package de.katzenpapst.amunra.block.ore;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import de.katzenpapst.amunra.block.SubBlockDropItem;

public class SubBlockOre extends SubBlockDropItem {

    protected String[] oredictNames = {};

    protected ItemStack smeltItem = null;

    public SubBlockOre setOredictNames(String... newNames) {
        this.oredictNames = newNames;
        return this;
    }

    public String[] getOredictNames() {
        return this.oredictNames;
    }

    public ItemStack getSmeltItem() {
        return smeltItem;
    }

    public SubBlockOre setSmeltItem(Item item, int num, int metadata) {
        smeltItem = new ItemStack(item, num, metadata);
        return this;
    }

    public SubBlockOre setSmeltItem(Item item, int num) {
        smeltItem = new ItemStack(item, num, 0);
        return this;
    }

    public SubBlockOre setSmeltItem(ItemStack stack) {
        smeltItem = stack;
        return this;
    }


    public SubBlockOre(String name, String texture) {
        super(name, texture);
        this.isValuable = true;
    }

    public SubBlockOre(String name, String texture, String tool,
            int harvestLevel) {
        super(name, texture, tool, harvestLevel);
        this.isValuable = true;
    }

    public SubBlockOre(String name, String texture, String tool,
            int harvestLevel, float hardness, float resistance) {
        super(name, texture, tool, harvestLevel, hardness, resistance);
        this.isValuable = true;
    }

}
