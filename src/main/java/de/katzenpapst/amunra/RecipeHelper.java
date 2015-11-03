package de.katzenpapst.amunra;

import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.api.recipe.CircuitFabricatorRecipes;
import micdoodle8.mods.galacticraft.core.items.GCItems;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.registry.GameRegistry;
import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.item.ARItems;
import de.katzenpapst.amunra.item.ItemDamagePair;

public class RecipeHelper {

	public RecipeHelper() {
		// TODO Auto-generated constructor stub
	}

	public static void initRecipes() {

    	//ItemStack enderWaferStack = ARItems.baseItem.getItemStack("waferEnder", 1);
    	ItemStack enderWaferStack = ARItems.waferEnder.getItemStack(1);
    	ItemStack lithiumMeshStack = ARItems.lithiumMesh.getItemStack(1);
    	ItemStack lithiumGemStack = ARItems.lithiumGem.getItemStack(1);
    	ItemStack compressedAluStack = new ItemStack(GCItems.basicItem, 1, 8);
    	ItemStack compressedSteelStack = new ItemStack(GCItems.basicItem, 1, 9);
    	ItemStack button = new ItemStack(Item.getItemFromBlock(Blocks.stone_button), 1);
    	ItemStack laserDiodeStack = ARItems.laserDiode.getItemStack(1);
        ItemStack cryoDiodeStack = ARItems.cryoDiode.getItemStack(1);
        ItemStack beamCore = new ItemStack(AsteroidsItems.basicItem, 1, 8);

    	// *** circuit fabricator recipes ***
    	int siliconCount = OreDictionary.getOres(ConfigManagerCore.otherModsSilicon).size();
    	// for NEI, see:
    	// micdoodle8.mods.galacticraft.core.nei.NEIGalacticraftConfig.addCircuitFabricatorRecipes()
        for (int j = 0; j <= siliconCount; j++)
        {
        	ItemStack silicon;
        	if (j == 0) {
        		silicon = new ItemStack(GCItems.basicItem, 1, 2);
    		} else {
    			silicon = OreDictionary.getOres("itemSilicon").get(j - 1);
			}

        	CircuitFabricatorRecipes.addRecipe(enderWaferStack,
        			new ItemStack[] {
	        			new ItemStack(Items.diamond),
	        			silicon, silicon,
	        			new ItemStack(Items.redstone),
	        			new ItemStack(Items.ender_pearl)
			});

        	CircuitFabricatorRecipes.addRecipe(lithiumMeshStack,
        			new ItemStack[] {
        				lithiumGemStack,
	        			silicon, silicon,
	        			new ItemStack(Items.redstone),
	        			new ItemStack(Items.paper)
			});


        }

        // *** smelting ***
        // cobble to smooth
        GameRegistry.addSmelting(
        		ARBlocks.getItemStack(ARBlocks.blockBasaltCobble, 1),
        		ARBlocks.getItemStack(ARBlocks.blockBasalt, 1), 1.0F);

        GameRegistry.addSmelting(
        		ARBlocks.getItemStack(ARBlocks.blockRedCobble, 1),
        		ARBlocks.getItemStack(ARBlocks.blockRedRock, 1), 1.0F);

        GameRegistry.addSmelting(
        		ARBlocks.getItemStack(ARBlocks.blockYellowCobble, 1),
        		ARBlocks.getItemStack(ARBlocks.blockYellowRock, 1), 1.0F);






        // *** raygun reload ***
        ItemStack battery = new ItemStack(GCItems.battery, 1, OreDictionary.WILDCARD_VALUE);
        ItemStack liBattery = new ItemStack(ARItems.batteryLithium, 1, OreDictionary.WILDCARD_VALUE);
        ItemStack quBattery = new ItemStack(ARItems.batteryQuantum, 1, OreDictionary.WILDCARD_VALUE);
        ItemStack enBattery = new ItemStack(ARItems.batteryEnder,   1, OreDictionary.WILDCARD_VALUE);

        ItemStack raygun = new ItemStack(ARItems.raygun, 1, OreDictionary.WILDCARD_VALUE);
        ItemStack cryogun = new ItemStack(ARItems.cryogun, 1, OreDictionary.WILDCARD_VALUE);
        initRaygunReloadingRecipes(new ItemStack[]{
        		raygun,
        		cryogun
        }, new ItemStack[]{
        		battery,
        		liBattery,
        		quBattery,
        		enBattery
        });

        // *** regular crafting ***

        // batteries
        GameRegistry.addRecipe(liBattery, new Object[]{
            	" X ",
            	"XBX",
            	"XAX",
            	'X', compressedAluStack, // 8 = metadata for compressed alu
            	'A', enderWaferStack,
            	'B', lithiumMeshStack
            });

        // TODO find a better recipe, it's too cheap otherwise
        GameRegistry.addRecipe(enBattery, new Object[]{
            	" X ",
            	"XBX",
            	"XAX",
            	'X', compressedAluStack, // 8 = metadata for compressed alu
            	'A', enderWaferStack,
            	'B', Blocks.redstone_block
            });

        // laser diode
        GameRegistry.addRecipe(laserDiodeStack, new Object[]{
        	"XXX",
        	"ABC",
        	"XXX",
        	'X', compressedAluStack, // 8 = metadata for compressed alu
        	'A', Blocks.glass_pane,
        	'B', ARItems.rubyGem.getItemStack(1),
        	'C', beamCore
        });

        // cryo diode
        GameRegistry.addRecipe(cryoDiodeStack, new Object[]{
        	"XXX",
        	"ABC",
        	"XXX",
        	'X', compressedAluStack, // 8 = metadata for compressed alu
        	'A', Blocks.glass_pane,
        	'B', ARItems.coldCrystal.getItemStack(1),
        	'C', beamCore
        });

        // laser gun
        GameRegistry.addRecipe(raygun, new Object[]{
        	"XYZ",
        	" AZ",
        	"  B",
        	'X', laserDiodeStack,
        	'Y', enderWaferStack,
        	'Z', compressedSteelStack,
        	'A', button,
        	'B', battery
        });

        // cryo gun
        GameRegistry.addRecipe(cryogun, new Object[]{
            	"XYZ",
            	" AZ",
            	"  B",
            	'X', cryoDiodeStack,
            	'Y', enderWaferStack,
            	'Z', compressedSteelStack,
            	'A', button,
            	'B', battery
            });



        // block crafting
        GameRegistry.addShapelessRecipe(
        		ARBlocks.getItemStack(ARBlocks.blockSmoothBasalt, 1),
        		ARBlocks.getItemStack(ARBlocks.blockBasalt, 1));

        GameRegistry.addRecipe(ARBlocks.getItemStack(ARBlocks.blockBasaltBrick, 4), new Object[]{
        	"XX",
        	"XX",
        	'X', ARBlocks.getItemStack(ARBlocks.blockBasalt, 1)
        });

        GameRegistry.addRecipe(ARBlocks.getItemStack(ARBlocks.blockObsidianBrick, 4), new Object[]{
        	"XX",
        	"XX",
        	'X', Blocks.obsidian
        });

        GameRegistry.addRecipe(ARBlocks.getItemStack(ARBlocks.blockAluCrate, 32), new Object[]{
        	" X ",
        	"X X",
        	" X ",
        	'X', new ItemStack(GCItems.basicItem, 1, 8) // 8 = metadata for compressed alu
        });


        initOreSmelting();
    }

	private static void initOreSmelting() {

		// alu
		addSmelting(new BlockMetaPair[]{
				ARBlocks.oreAluBasalt
		}, new ItemDamagePair(GCItems.basicItem, 5));

		// copper
		addSmelting(new BlockMetaPair[]{
				ARBlocks.oreCopperBasalt
		}, new ItemDamagePair(GCItems.basicItem, 3));

		// gold
		addSmelting(new BlockMetaPair[]{
				ARBlocks.oreGoldBasalt
		}, new ItemDamagePair(Items.gold_ingot, 0));

		// iron
		addSmelting(new BlockMetaPair[]{
				ARBlocks.oreIronBasalt
		}, new ItemDamagePair(Items.iron_ingot, 0));

		// tin
		addSmelting(new BlockMetaPair[]{
				ARBlocks.oreTinBasalt
		}, new ItemDamagePair(GCItems.basicItem, 4));
	}

	/**
	 * Helper to add smelting for multiple variatons of the same ore
	 *
	 * @param blocks
	 * @param ingot
	 */
	private static void addSmelting(BlockMetaPair[] blocks, ItemDamagePair ingot) {
		ItemStack output = ingot.getItemStack(1);
		for(BlockMetaPair block: blocks) {
			GameRegistry.addSmelting(ARBlocks.getItemStack(block, 1), output, 1.0F);
		}
	}

	/**
     * Helper function to add all reloading recipes for all rayguns and batteries...
     *
     * @param guns
     * @param batteries
     */
	private static void initRaygunReloadingRecipes(ItemStack[] guns, ItemStack[] batteries) {
    	for(ItemStack gun: guns) {
    		for(ItemStack battery: batteries) {
    			GameRegistry.addShapelessRecipe(gun, new Object[]{gun, battery});
    		}
    	}
    }
/*
    protected static void tryStuff() {
    	// new ShapedOreRecipe(result, recipe)
    }
*/
    /**/

}
