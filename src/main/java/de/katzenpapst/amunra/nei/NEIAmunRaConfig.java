package de.katzenpapst.amunra.nei;

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import micdoodle8.mods.galacticraft.core.items.GCItems;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import codechicken.nei.PositionedStack;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.item.ARItems;
import de.katzenpapst.amunra.nei.recipehandler.ARCircuitFab;

public class NEIAmunRaConfig implements IConfigureNEI {

	private static HashMap<HashMap<Integer, PositionedStack>, PositionedStack> circuitFabricatorRecipes = new HashMap<HashMap<Integer, PositionedStack>, PositionedStack>();

	public NEIAmunRaConfig() {

	}

	@Override
	public String getName() {
		return "AmunRa NEI Plugin";
	}

	@Override
	public String getVersion() {
		return AmunRa.VERSION;
	}

	@Override
	public void loadConfig() {


    	this.addCircuitFabricatorRecipe(new ItemStack(Items.diamond), new ItemStack(Items.redstone), new ItemStack(Items.ender_pearl), ARItems.waferEnder.getItemStack(1));
    	this.addCircuitFabricatorRecipe(ARItems.lithiumGem.getItemStack(1), new ItemStack(Items.redstone), new ItemStack(Items.paper), ARItems.lithiumMesh.getItemStack(1));

		API.registerRecipeHandler(new ARCircuitFab());
        API.registerUsageHandler(new ARCircuitFab());
	}


	/**
	 *
	 * @param slotGem

	 * @param redstone
	 * @param optional
	 * @param output
	 */
	private void addCircuitFabricatorRecipe(ItemStack slotGem, ItemStack redstone, ItemStack optional, ItemStack output) {
		HashMap<Integer, PositionedStack> input1 = new HashMap<Integer, PositionedStack>();
		// slot 0 = gem
        input1.put(0, new PositionedStack(slotGem, 10, 22));
        int siliconCount = OreDictionary.getOres(ConfigManagerCore.otherModsSilicon).size();
        ItemStack[] silicons = new ItemStack[siliconCount + 1];
        silicons[0] = new ItemStack(GCItems.basicItem, 1, 2);
        for (int j = 0; j < siliconCount; j++)
        {
        	silicons[j + 1] = OreDictionary.getOres("itemSilicon").get(j);
        }
        input1.put(1, new PositionedStack(silicons, 69, 51));
        input1.put(2, new PositionedStack(silicons, 69, 69));
        // redstone
        input1.put(3, new PositionedStack(redstone, 117, 51));
        // optional
        if(optional != null) {
        	input1.put(4, new PositionedStack(optional, 140, 25));
        }
        this.registerCircuitFabricatorRecipe(input1, new PositionedStack(output, 147, 91));
	}

	public void registerCircuitFabricatorRecipe(HashMap<Integer, PositionedStack> input, PositionedStack output)
    {
		circuitFabricatorRecipes.put(input, output);
    }

	public static Set<Entry<HashMap<Integer, PositionedStack>, PositionedStack>> getCircuitFabricatorRecipes() {
		return circuitFabricatorRecipes.entrySet();
	}


}
