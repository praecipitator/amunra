package de.katzenpapst.amunra.crafting;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;

public class RocketRecipeHelper {

    ArrayList<ItemStack>[] stacks = new ArrayList[3];
    /*ArrayList<ItemStack> stack2 = new ArrayList<ItemStack>();
    ArrayList<ItemStack> stack3 = new ArrayList<ItemStack>();*/

    public RocketRecipeHelper() {
        for(int i=0;i<stacks.length;i++) {
            stacks[i] = new ArrayList<ItemStack>();
        }
    }

    public RocketRecipeHelper(ItemStack one) {
        this();
        addSame(one);
    }

    public RocketRecipeHelper(ItemStack one, ItemStack otherTwo) {
        this();
        addPermutation1And2(one, otherTwo);
    }

    public RocketRecipeHelper(ItemStack stack1, ItemStack stack2, ItemStack stack3) {
        this();
        addPermutation3different(stack1, stack2, stack3);
    }

    public ItemStack[] getStackArray(int i) {
        ItemStack[] result = new ItemStack[i];
        stacks[i].toArray(result);
        return result;
    }

    public ArrayList<ItemStack> getStacks(int i) {
        return stacks[i];
    }

    /**
     * Adds permutations of 1 and 2 items, either can be null
     * @param one
     * @param otherTwo
     */
    public void addPermutation1And2(ItemStack one, ItemStack otherTwo) {
        stacks[0].add(one);
        stacks[1].add(otherTwo);
        stacks[2].add(otherTwo);

        stacks[0].add(otherTwo);
        stacks[1].add(one);
        stacks[2].add(otherTwo);

        stacks[0].add(otherTwo);
        stacks[1].add(otherTwo);
        stacks[2].add(one);
    }

    /**
     * Adds permutations for 3 different items
     * @param stack1
     * @param stack2
     * @param stack3
     */
    public void addPermutation3different(ItemStack stack1, ItemStack stack2, ItemStack stack3) {
        stacks[0].add(stack1);
        stacks[1].add(stack2);
        stacks[2].add(stack3);

        stacks[0].add(stack1);
        stacks[1].add(stack3);
        stacks[2].add(stack2);

        stacks[0].add(stack2);
        stacks[1].add(stack1);
        stacks[2].add(stack3);

        stacks[0].add(stack2);
        stacks[1].add(stack3);
        stacks[2].add(stack1);

        stacks[0].add(stack3);
        stacks[1].add(stack1);
        stacks[2].add(stack2);

        stacks[0].add(stack3);
        stacks[1].add(stack2);
        stacks[2].add(stack1);
    }

    /**
     * Just adds the stack to everything
     * @param stack
     */
    public void addSame(ItemStack stack) {
        stacks[0].add(stack);
        stacks[1].add(stack);
        stacks[2].add(stack);
    }

}
