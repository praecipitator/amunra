package de.katzenpapst.amunra.mob;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

public class RobotVillagerProfession {
	protected ResourceLocation icon;
	protected String name;
	protected MerchantRecipeList merchantList;
	
	protected static ArrayList<RobotVillagerProfession> professionRegistry = new ArrayList<RobotVillagerProfession>();
	
	public static int addProfession(RobotVillagerProfession prof) {
		professionRegistry.add(prof);
		return professionRegistry.size()-1;
	}
	
	public static RobotVillagerProfession getProfession(int profession) {
		return professionRegistry.get(profession);
	}
	
	public static int getRandomProfession(Random rand) {
		return rand.nextInt(professionRegistry.size());
	}
	
	public RobotVillagerProfession(ResourceLocation icon, String name, MerchantRecipeList list ){
		this.icon = icon;
		this.name = name;
		merchantList = list;
	}
	
	public RobotVillagerProfession(ResourceLocation icon, String name){
		this.icon = icon;
		this.name = name;
		merchantList = new MerchantRecipeList();
	}
	
	public ResourceLocation getIcon() {
		return this.icon;
	}
	
	public String getName() {
		return this.name;
	}
	
	public MerchantRecipeList getRecipeList() {
		return merchantList;
	}
	
	public RobotVillagerProfession addRecipe(MerchantRecipe recipe) {
		merchantList.add(recipe);
		return this;
	}
	
	public RobotVillagerProfession addRecipe(ItemStack input1, ItemStack input2, ItemStack output) {
		merchantList.add(new MerchantRecipe(input1, input2, output));
		return this;
	}
	
	public RobotVillagerProfession addRecipe(ItemStack input, ItemStack output) {
		merchantList.add(new MerchantRecipe(input, output));
		return this;
	}
	
	public RobotVillagerProfession addRecipe(Item singleInputItem, int numEmeralds, Item singleOutputItem) {
		merchantList.add(new MerchantRecipe(
				new ItemStack(singleInputItem, 1), 
				new ItemStack(Items.emerald, numEmeralds), 
				new ItemStack(singleOutputItem, 1)));
		return this;
	}
}
