package de.katzenpapst.amunra;

import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.api.recipe.CircuitFabricatorRecipes;
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry;
import micdoodle8.mods.galacticraft.api.recipe.SpaceStationRecipe;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import micdoodle8.mods.galacticraft.core.items.GCItems;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems;
import micdoodle8.mods.galacticraft.planets.asteroids.util.AsteroidsUtil;
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems;
import micdoodle8.mods.galacticraft.planets.mars.schematic.SchematicTier2Rocket;
import micdoodle8.mods.galacticraft.planets.mars.util.MarsUtil;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.HashMap;

import cpw.mods.fml.common.registry.GameRegistry;
import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.block.BlockStairsAR;
import de.katzenpapst.amunra.block.ore.BlockOreMulti;
import de.katzenpapst.amunra.block.ore.SubBlockOre;
import de.katzenpapst.amunra.item.ARItems;
import de.katzenpapst.amunra.schematic.SchematicPageShuttle;

public class RecipeHelper {

    public static SpaceStationRecipe mothershipRecipe;

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
        ItemStack compressedTitaniumStack = new ItemStack(AsteroidsItems.basicItem, 1, 6);
        ItemStack button = new ItemStack(Item.getItemFromBlock(Blocks.stone_button), 1);
        ItemStack laserDiodeStack = ARItems.laserDiode.getItemStack(1);
        ItemStack cryoDiodeStack = ARItems.cryoDiode.getItemStack(1);
        ItemStack beamCore = new ItemStack(AsteroidsItems.basicItem, 1, 8);
        ItemStack waferSolar = new ItemStack(GCItems.basicItem, 1, 12);
        ItemStack waferBasic = new ItemStack(GCItems.basicItem, 1, 13);
        ItemStack waferAdvanced = new ItemStack(GCItems.basicItem, 1, 14);

        // *** mothership ***
        final HashMap<Object, Integer> inputMap = new HashMap<Object, Integer>();
        inputMap.put("ingotTin", 32);
        inputMap.put(compressedAluStack, 16);
        inputMap.put(enderWaferStack, 1);
        inputMap.put(Items.iron_ingot, 24);
        mothershipRecipe = new SpaceStationRecipe(inputMap);

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

        // rebar to steel
        GameRegistry.addSmelting(
                ARItems.ancientRebar.getItemStack(1),
                ARItems.steelIngot.getItemStack(1), 1.5F);





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

        //
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

        // uranium
        GameRegistry.addRecipe(ARBlocks.getItemStack(ARBlocks.blockUraniumBlock, 1), new Object[]{
                "XXX",
                "XXX",
                "XXX",
                'X', ARItems.uraniumIngot.getItemStack(1)
        });

        GameRegistry.addShapelessRecipe(
                ARItems.uraniumIngot.getItemStack(9),
                ARBlocks.getItemStack(ARBlocks.blockUraniumBlock, 1));

        // nuclear generators
        // basic
        GameRegistry.addRecipe(new ShapedOreRecipe(ARBlocks.getItemStack(ARBlocks.blockIsotopeGeneratorBasic, 1),
                "XAX",
                "XBC",
                "XDX",
                'X', compressedSteelStack, // compressed steel
                'A', waferAdvanced,
                'B', "ingotUranium",
                'C', new ItemStack(GCBlocks.aluminumWire, 1, 0), // basic wire
                'D', waferSolar
                ));

        // advanced
        GameRegistry.addRecipe(new ShapedOreRecipe(ARBlocks.getItemStack(ARBlocks.blockIsotopeGeneratorAdvanced, 1),
                "XAX",
                "XBC",
                "XDX",
                'X', compressedTitaniumStack, // compressed titanium
                'A', enderWaferStack,
                'B', "blockUranium",
                'C', new ItemStack(GCBlocks.aluminumWire, 1, 1), // basic wire
                'D', lithiumMeshStack
                ));

        initOreSmelting();

        addSlabAndStairsCrafting(ARBlocks.blockAluCrate, ARBlocks.slabAluCrate, ARBlocks.stairsAluCrate);
        addSlabAndStairsCrafting(ARBlocks.blockBasalt, ARBlocks.slabBasaltBlock, ARBlocks.stairsBasaltBlock);
        addSlabAndStairsCrafting(ARBlocks.blockBasaltBrick, ARBlocks.slabBasaltBrick, ARBlocks.stairsBasaltBrick);
        addSlabAndStairsCrafting(ARBlocks.blockSmoothBasalt, ARBlocks.slabBasaltSmooth, ARBlocks.stairsSmoothBasalt);
        addSlabAndStairsCrafting(ARBlocks.blockMethanePlanks, ARBlocks.slabMethanePlanks, ARBlocks.stairsMethanePlanks);
        addSlabAndStairsCrafting(ARBlocks.blockObsidianBrick, ARBlocks.slabObsidianBrick, ARBlocks.stairsObsidianBrick);
        addSlabAndStairsCrafting(ARBlocks.blockPodPlanks, ARBlocks.slabPodPlanks, ARBlocks.stairsPodPlanks);

        initNasaWorkbenchCrafting();
    }

    private static void  initNasaWorkbenchCrafting() {
        /// TEST TODO TEST TODO
        SchematicRegistry.registerSchematicRecipe(new SchematicPageShuttle());

        // Schematic
        HashMap<Integer, ItemStack> input = new HashMap<Integer, ItemStack>();
        input.put(1, new ItemStack(GCItems.partNoseCone));
        input.put(2, new ItemStack(MarsItems.marsItemBasic, 1, 3));
        input.put(3, new ItemStack(MarsItems.marsItemBasic, 1, 3));
        input.put(4, new ItemStack(MarsItems.marsItemBasic, 1, 3));
        input.put(5, new ItemStack(MarsItems.marsItemBasic, 1, 3));
        input.put(6, new ItemStack(MarsItems.marsItemBasic, 1, 3));
        input.put(7, new ItemStack(MarsItems.marsItemBasic, 1, 3));
        input.put(8, new ItemStack(MarsItems.marsItemBasic, 1, 3));
        input.put(9, new ItemStack(MarsItems.marsItemBasic, 1, 3));
        input.put(10, new ItemStack(MarsItems.marsItemBasic, 1, 3));
        input.put(11, new ItemStack(MarsItems.marsItemBasic, 1, 3));
        input.put(12, new ItemStack(GCItems.rocketEngine, 1, 1));
        input.put(13, new ItemStack(GCItems.partFins));
        input.put(14, new ItemStack(GCItems.partFins));
        input.put(15, new ItemStack(GCItems.rocketEngine));
        input.put(16, new ItemStack(GCItems.rocketEngine, 1, 1));
        input.put(17, new ItemStack(GCItems.partFins));
        input.put(18, new ItemStack(GCItems.partFins));
        input.put(19, null);
        input.put(20, null);
        input.put(21, null);

        MarsUtil.addRocketBenchT2Recipe(new ItemStack(ARItems.shuttleItem, 1, 0), input);

        HashMap<Integer, ItemStack> input2 = new HashMap<Integer, ItemStack>(input);
        input2.put(19, new ItemStack(Blocks.chest));
        input2.put(20, null);
        input2.put(21, null);
        MarsUtil.addRocketBenchT2Recipe(new ItemStack(ARItems.shuttleItem, 1, 1), input2);

        input2 = new HashMap<Integer, ItemStack>(input);
        input2.put(19, null);
        input2.put(20, new ItemStack(Blocks.chest));
        input2.put(21, null);
        MarsUtil.addRocketBenchT2Recipe(new ItemStack(ARItems.shuttleItem, 1, 1), input2);

        input2 = new HashMap<Integer, ItemStack>(input);
        input2.put(19, null);
        input2.put(20, null);
        input2.put(21, new ItemStack(Blocks.chest));
        MarsUtil.addRocketBenchT2Recipe(new ItemStack(ARItems.shuttleItem, 1, 1), input2);

        input2 = new HashMap<Integer, ItemStack>(input);
        input2.put(19, new ItemStack(Blocks.chest));
        input2.put(20, new ItemStack(Blocks.chest));
        input2.put(21, null);
        MarsUtil.addRocketBenchT2Recipe(new ItemStack(ARItems.shuttleItem, 1, 2), input2);

        input2 = new HashMap<Integer, ItemStack>(input);
        input2.put(19, new ItemStack(Blocks.chest));
        input2.put(20, null);
        input2.put(21, new ItemStack(Blocks.chest));
        MarsUtil.addRocketBenchT2Recipe(new ItemStack(ARItems.shuttleItem, 1, 2), input2);

        input2 = new HashMap<Integer, ItemStack>(input);
        input2.put(19, null);
        input2.put(20, new ItemStack(Blocks.chest));
        input2.put(21, new ItemStack(Blocks.chest));
        MarsUtil.addRocketBenchT2Recipe(new ItemStack(ARItems.shuttleItem, 1, 2), input2);

        input2 = new HashMap<Integer, ItemStack>(input);
        input2.put(19, new ItemStack(Blocks.chest));
        input2.put(20, new ItemStack(Blocks.chest));
        input2.put(21, new ItemStack(Blocks.chest));
        MarsUtil.addRocketBenchT2Recipe(new ItemStack(ARItems.shuttleItem, 1, 3), input2);
    }

    private static void initOreSmelting() {
        addSmeltingForMultiOre(ARBlocks.metaBlockBasaltOre);
        addSmeltingForMultiOre(ARBlocks.metaBlockObsidianOre);
        addSmeltingForMultiOre(ARBlocks.metaBlockHardClayOre);
        addSmeltingForMultiOre(ARBlocks.metaBlockConcreteOre);
    }

    private static void addSmeltingForMultiOre(BlockOreMulti block) {
        for(int i=0; i<block.getNumPossibleSubBlocks();i++) {
            SubBlockOre sb = (SubBlockOre)block.getSubBlock(i);
            if(sb != null && sb.getSmeltItem() != null) {

                ItemStack input = new ItemStack(block, 1, i);

                GameRegistry.addSmelting(input, sb.getSmeltItem(), 1.0F);
            }
        }
    }

    private static void addSlabAndStairsCrafting(BlockMetaPair block, BlockMetaPair slab, BlockStairsAR stairsAluCrate) {
        ItemStack blockStack = ARBlocks.getItemStack(block, 1);
        // slab
        GameRegistry.addRecipe(ARBlocks.getItemStack(slab, 6),
                "XXX",
                'X', blockStack
                );
        // slab to block
        GameRegistry.addRecipe(blockStack,
                "X",
                "X",
                'X', ARBlocks.getItemStack(slab, 1)
                );

        ItemStack stairStack = new ItemStack(stairsAluCrate, 4);

        // stairs
        GameRegistry.addRecipe(stairStack,
                "  X",
                " XX",
                "XXX",
                'X', blockStack
                );
        // stairs reverse
        GameRegistry.addRecipe(stairStack,
                "X  ",
                "XX ",
                "XXX",
                'X', blockStack
                );
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
