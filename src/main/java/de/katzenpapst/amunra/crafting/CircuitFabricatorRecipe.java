package de.katzenpapst.amunra.crafting;

import net.minecraft.item.ItemStack;

/**
 * Helper for NEI and stuff
 * @author katzenpapst
 *
 */
public class CircuitFabricatorRecipe {

    public ItemStack output;
    public ItemStack[] crystal;
    public ItemStack[] silicon1;
    public ItemStack[] silicon2;
    public ItemStack[] redstone;
    public ItemStack[] optional;

    public CircuitFabricatorRecipe(ItemStack output, ItemStack[] crystal, ItemStack[] silicon1, ItemStack[] silicon2, ItemStack[] redstone) {
        this(output, crystal, silicon1, silicon2, redstone, null);
    }

    public CircuitFabricatorRecipe(ItemStack output, ItemStack[] crystal, ItemStack[] silicon1, ItemStack[] silicon2, ItemStack[] redstone, ItemStack[] optional) {
        this.crystal = crystal;
        this.silicon1 = silicon1;
        this.silicon2 = silicon2;
        this.redstone = redstone;
        this.optional = optional;
        this.output = output;
    }

    public Object getCrystal() {
        if(crystal.length == 1) {
            return crystal[0];
        }
        return crystal;
    }

    public Object getSilicon1() {
        if(silicon1.length == 1) {
            return silicon1[0];
        }
        return silicon1;
    }

    public Object getSilicon2() {
        if(silicon2.length == 1) {
            return silicon2[0];
        }
        return silicon2;
    }

    public Object getRedstone() {
        if(redstone.length == 1) {
            return redstone[0];
        }
        return redstone;
    }

    public Object getOptional() {
        if(optional == null || optional.length == 0) {
            return null;
        }
        if(optional.length == 1) {
            return optional[0];
        }
        return optional;
    }
}
