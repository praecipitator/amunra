package de.katzenpapst.amunra.nei;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import micdoodle8.mods.galacticraft.api.recipe.INasaWorkbenchRecipe;
import micdoodle8.mods.galacticraft.core.items.GCItems;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import codechicken.nei.PositionedStack;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.crafting.CircuitFabricatorRecipe;
import de.katzenpapst.amunra.crafting.RecipeHelper;
import de.katzenpapst.amunra.inventory.schematic.ContainerSchematicShuttle;
import de.katzenpapst.amunra.item.ARItems;
import de.katzenpapst.amunra.nei.recipehandler.ARCircuitFab;
import de.katzenpapst.amunra.nei.recipehandler.ARNasaWorkbenchShuttle;

public class NEIAmunRaConfig implements IConfigureNEI {

    private static HashMap<HashMap<Integer, PositionedStack>, PositionedStack> circuitFabricatorRecipes = new HashMap<HashMap<Integer, PositionedStack>, PositionedStack>();
    private static HashMap<ArrayList<PositionedStack>, PositionedStack> shuttleRecipes = new HashMap<ArrayList<PositionedStack>, PositionedStack>();

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
        // this is just a copy of the recipe. Couldn't I automate it?

//        this.addCircuitFabricatorRecipe(new ItemStack(Items.diamond), new ItemStack(Items.redstone), new ItemStack(Items.ender_pearl), ARItems.waferEnder.getItemStack(1));
//        this.addCircuitFabricatorRecipe(ARItems.lithiumGem.getItemStack(1), new ItemStack(Items.redstone), new ItemStack(Items.paper), ARItems.lithiumMesh.getItemStack(1));

        // now do the circfab
        initCircuitFabricatorRecipes();

        // so at this point I would add the rocket recipe?
        initShuttleRecipes();

        API.registerRecipeHandler(new ARCircuitFab());
        API.registerUsageHandler(new ARCircuitFab());
        API.registerRecipeHandler(new ARNasaWorkbenchShuttle());
        API.registerUsageHandler(new ARNasaWorkbenchShuttle());
    }

    private void initShuttleRecipes() {
        Vector<INasaWorkbenchRecipe> data =
                RecipeHelper.getAllRecipesFor(ARItems.shuttleItem);
        int[][] slotData = ContainerSchematicShuttle.slotCoordinateMapping;
        // let's see if I can convert it
        int offsetX = -4;
        int offsetY = 0;

        for(INasaWorkbenchRecipe recipe: data) {
            ArrayList<PositionedStack> input1 = new ArrayList<PositionedStack>();

            for(int i=0; i<recipe.getRecipeSize();i++) {
                int[] coords = slotData[i];
                ItemStack curStack = recipe.getRecipeInput().get(i+1);
                if(curStack == null) {
                    continue;
                }
                input1.add(new PositionedStack(curStack, coords[0]+offsetX, coords[1]+offsetY));
            }

            shuttleRecipes.put(input1, new PositionedStack(recipe.getRecipeOutput(), 142 + offsetX, 18 + 69 + 9 + offsetY ));
        }
    }

    public static Set<Map.Entry<ArrayList<PositionedStack>, PositionedStack>> getShuttleRecipes() {
        return shuttleRecipes.entrySet();
    }



    private void initCircuitFabricatorRecipes() {
        ArrayList<CircuitFabricatorRecipe> recipes = RecipeHelper.getCircuitFabricatorRecipes();
        for(CircuitFabricatorRecipe recipe: recipes) {
            // add it
            HashMap<Integer, PositionedStack> input1 = new HashMap<Integer, PositionedStack>();
            // slot 0 = gem
            input1.put(0, new PositionedStack(recipe.getCrystal(), 10, 22));

            // silicons
            input1.put(1, new PositionedStack(recipe.getSilicon1(), 69, 51));
            input1.put(2, new PositionedStack(recipe.getSilicon2(), 69, 69));
            // redstone
            input1.put(3, new PositionedStack(recipe.getRedstone(), 117, 51));
            // optional
            Object optional = recipe.getOptional();
            if(optional != null) {
                input1.put(4, new PositionedStack(optional, 140, 25));
            }
            this.registerCircuitFabricatorRecipe(input1, new PositionedStack(recipe.output, 147, 91));
        }
    }

    /**
     *
     * @param slotGem

     * @param redstone
     * @param optional
     * @param output
     * /
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
    }*/

    public void registerCircuitFabricatorRecipe(HashMap<Integer, PositionedStack> input, PositionedStack output)
    {
        circuitFabricatorRecipes.put(input, output);
    }

    public static Set<Entry<HashMap<Integer, PositionedStack>, PositionedStack>> getCircuitFabricatorRecipes() {
        return circuitFabricatorRecipes.entrySet();
    }


}
