package de.katzenpapst.amunra.block;

import net.minecraft.world.World;

public class SubBlockMassive extends SubBlock implements IMassiveBlock {

    protected float mass = 1.0F;

    public SubBlockMassive(String name, String texture) {
        super(name, texture);
    }

    public SubBlockMassive(String name, String texture, String tool, int harvestLevel) {
        super(name, texture, tool, harvestLevel);
    }

    public SubBlockMassive(
            String name,
            String texture,
            String tool,
            int harvestLevel,
            float hardness,
            float resistance) {
        super(name, texture, tool, harvestLevel, hardness, resistance);
    }

    @Override
    public float getMass(World w, int x, int y, int z, int meta) {
        return mass;
    }

    public SubBlockMassive setMass(float mass) {
        this.mass = mass;
        return this;
    }

}
