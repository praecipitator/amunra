package de.katzenpapst.amunra.block;

import java.util.Random;

import de.katzenpapst.amunra.item.ARItems;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class DustBlock extends SubBlock implements IMassiveBlock {

    public DustBlock(String name, String texture) {
        super(name, texture);
    }

    public DustBlock(String name, String texture, String tool, int harvestLevel) {
        super(name, texture, tool, harvestLevel);
    }

    public DustBlock(String name, String texture, String tool,
            int harvestLevel, float hardness, float resistance) {
        super(name, texture, tool, harvestLevel, hardness, resistance);
    }

    @Override
    public boolean dropsSelf() {
        return false;
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune)
    {
        return ARItems.dustMote.getItem();
    }

    @Override
    public int damageDropped(int meta)
    {
        return ARItems.dustMote.getDamage();
    }

    @Override
    public int quantityDropped(int meta, int fortune, Random random)
    {
        return Math.min(random.nextInt(3)+random.nextInt(10)*fortune, 9);
    }

    @Override
    public float getMass(World w, int x, int y, int z, int meta) {
        return 0.01F;
    }
}
