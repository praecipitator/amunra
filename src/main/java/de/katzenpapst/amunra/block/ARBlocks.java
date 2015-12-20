package de.katzenpapst.amunra.block;

import de.katzenpapst.amunra.block.bush.ARTreeSapling;
import de.katzenpapst.amunra.block.bush.BlockBushMulti;
import de.katzenpapst.amunra.block.bush.MethaneTallGrass;
import de.katzenpapst.amunra.block.bush.PodSapling;
import de.katzenpapst.amunra.block.ore.BlockOreMulti;
import de.katzenpapst.amunra.block.ore.SubBlockOre;
import de.katzenpapst.amunra.block.ore.SubBlockOreMultidrop;
import de.katzenpapst.amunra.item.ItemDamagePair;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ARBlocks {
	public static BlockBasicMeta metaBlockRock;
	// public static BlockBasicMulti multiBlockOre1;
	public static BlockBasicMeta metaBlockDirt;
	public static BlockBasicMeta metaBlockGrass;
	public static BlockBasicMeta metaBlockFalling;
	public static BlockBasicMeta metaBlockPlant;
	public static BlockBasicMeta metaBlockLog;
	public static BlockBasicMeta metaBlockNonRotational;
	public static BlockMetaPair blockMethanePlanks;
	public static BlockMetaPair blockPodPlanks;

	public static BlockOreMulti metaBlockBasaltOre;
	public static BlockOreMulti metaBlockObsidianOre;

	public static IMetaBlock metaBlockLeaf;
	public static BlockBasicMeta metaBlockSapling;

	public static BlockMetaPair blockDarkMatter;
	public static BlockMetaPair blockBasalt;
	public static BlockMetaPair blockRedRock;
	public static BlockMetaPair blockYellowCobble;
	public static BlockMetaPair blockYellowRock;
	public static BlockMetaPair blockRedCobble;
	public static BlockMetaPair blockAluCrate;
	public static BlockMetaPair blockBasaltBrick;
	public static BlockMetaPair blockSmoothBasalt;
	public static BlockMetaPair blockObsidianBrick;


	public static BlockMetaPair blockBasaltRegolith;
	public static BlockMetaPair blockMethaneDirt;
	public static BlockMetaPair blockDust;


	public static BlockMetaPair blockMethaneGrass;
	public static BlockMetaPair blockObsidiSand;
	public static BlockMetaPair blockObsidiGravel;
	public static BlockMetaPair blockBasaltCobble;

	public static BlockMetaPair oreDiamondObsid;
	public static BlockMetaPair oreRubyObsid;
	public static BlockMetaPair oreEmeraldObsid;

	public static BlockMetaPair oreCryoBasalt;
	public static BlockMetaPair oreLithiumBasalt;
	public static BlockMetaPair oreAluBasalt;

	public static BlockMetaPair oreCopperBasalt;
	public static BlockMetaPair oreGoldBasalt;
	public static BlockMetaPair oreIronBasalt;
	public static BlockMetaPair oreLapisBasalt;
	public static BlockMetaPair oreSiliconBasalt;
	public static BlockMetaPair oreTinBasalt;
	public static BlockMetaPair oreTitaniumBasalt;


	public static BlockMetaPair blockMethaneTGrass;
	public static BlockMetaPair blockMethaneLog;
	public static BlockMetaPair blockPodBark;

	public static BlockMetaPair blockPodLeaf;
	public static BlockMetaPair blockMethaneLeaf;
	public static BlockMetaPair blockMethaneSapling;
	public static BlockMetaPair blockPodSapling;


	public static SubBlockOre subGold;
	public static SubBlockOre subAlu;
	public static SubBlockOre subCopper;
	public static SubBlockOre subCryo;
	public static SubBlockOre subDesh;
	public static SubBlockOre subDiamond;
	public static SubBlockOre subEmerald;
	public static SubBlockOre subIron;
	public static SubBlockOre subLapis;
	public static SubBlockOre subLithium;
	public static SubBlockOre subRuby;
	public static SubBlockOre subSilicon;
	public static SubBlockOre subTin;
	public static SubBlockOreMultidrop subTitanium;

	// STAIRS
	public static BlockStairsAR stairsObsidianBrick;
	public static BlockStairsAR stairsSmoothBasalt;
	public static BlockStairsAR stairsBasaltBrick;
	public static BlockStairsAR stairsBasaltBlock;
	public static BlockStairsAR stairsAluCrate;
	public static BlockStairsAR stairsMethanePlanks;
	public static BlockStairsAR stairsPodPlanks;

	// SLABS
	public static BlockSlabMeta metaSlabRock;
	public static BlockSlabMeta metaSlabWood;

	public static BlockMetaPair slabBasaltBlock;
	public static BlockMetaPair slabBasaltBrick;
	public static BlockMetaPair slabBasaltSmooth;
	public static BlockMetaPair slabObsidianBrick;
	public static BlockMetaPair slabAluCrate;
	public static BlockMetaPair slabPodPlanks;
	public static BlockMetaPair slabMethanePlanks;




	public static ItemStack getItemStack(BlockMetaPair input, int amount) {
		return new ItemStack(input.getBlock(), amount, input.getMetadata());
	}

	public static ItemDamagePair getBlockIDP(BlockMetaPair input) {
		return new ItemDamagePair(Item.getItemFromBlock(input.getBlock()), input.getMetadata());
	}

	public static void initBlocks()
    {
		// blast resistance: http://minecraft.gamepedia.com/Explosion#Blast_Resistance
		// hardness: http://minecraft.gamepedia.com/Breaking#Blocks_by_hardness
		// net.minecraft.init.Blocks
		// micdoodle8.mods.galacticraft.core.blocks.GCBlocks
		// micdoodle8.mods.galacticraft.planets.asteroids.blocks.AsteroidBlocks
		// micdoodle8.mods.galacticraft.planets.mars.blocks.MarsBlocks

		/*GCBlocks.basicBlock.setHarvestLevel("pickaxe", 2, 5); //Copper ore
        GCBlocks.basicBlock.setHarvestLevel("pickaxe", 2, 6); //Tin ore
        GCBlocks.basicBlock.setHarvestLevel("pickaxe", 2, 7); //Aluminium ore
        GCBlocks.basicBlock.setHarvestLevel("pickaxe", 1, 8); //Silicon ore
        GCBlocks.fallenMeteor.setHarvestLevel("pickaxe", 3);
        GCBlocks.blockMoon.setHarvestLevel("pickaxe", 2, 0); //Copper ore
        GCBlocks.blockMoon.setHarvestLevel("pickaxe", 2, 1); //Tin ore
        GCBlocks.blockMoon.setHarvestLevel("pickaxe", 1, 2); //Cheese ore
        GCBlocks.blockMoon.setHarvestLevel("shovel", 0, 3); //Moon dirt
        GCBlocks.blockMoon.setHarvestLevel("pickaxe", 1, 4); //Moon rock*/

		// Blocks.brewing_stand


		// MULTIORES
		// sub-ores first
		subGold 	= (SubBlockOre) new SubBlockOre("oreGold", "amunra:ore-gold").setHarvestInfo("pickaxe", 1).setHardness(3).setResistance(5);
		subAlu 		= (SubBlockOre) new SubBlockOre("oreAluminium", "amunra:ore-alu").setHarvestInfo("pickaxe", 2).setHardness(3).setResistance(5);
		subCopper 	= (SubBlockOre) new SubBlockOre("oreCopper", "amunra:ore-copper").setHarvestInfo("pickaxe", 2).setHardness(3).setResistance(5);
		subCryo	 	= (SubBlockOre) new SubBlockOre("oreCryo", "amunra:ore-cryo").setHarvestInfo("pickaxe", 3).setHardness(3).setResistance(5);;
		subDesh 	= (SubBlockOre) new SubBlockOre("oreDesh", "amunra:ore-desh").setHarvestInfo("pickaxe", 3).setHardness(3).setResistance(5);
		subDiamond 	= (SubBlockOre) new SubBlockOre("oreDiamond", "amunra:ore-diamond").setHarvestInfo("pickaxe", 3).setHardness(3).setResistance(5);
		subEmerald 	= (SubBlockOre) new SubBlockOre("oreEmerald", "amunra:ore-emerald").setHarvestInfo("pickaxe", 3).setHardness(3).setResistance(5);
		subIron 	= (SubBlockOre) new SubBlockOre("oreIron", "amunra:ore-iron").setHarvestInfo("pickaxe", 2).setHardness(3).setResistance(5);
		subLapis 	= (SubBlockOre) new SubBlockOre("oreLapis", "amunra:ore-lapis").setHarvestInfo("pickaxe", 1).setHardness(3).setResistance(5);
		subLithium 	= (SubBlockOre) new SubBlockOre("oreLithium", "amunra:ore-lithium").setHarvestInfo("pickaxe", 3).setHardness(3).setResistance(5);
		subRuby 	= (SubBlockOre) new SubBlockOre("oreRuby", "amunra:ore-ruby").setHarvestInfo("pickaxe", 3).setHardness(3).setResistance(5);
		subSilicon 	= (SubBlockOre) new SubBlockOre("oreSilicon", "amunra:ore-silicon").setHarvestInfo("pickaxe", 1).setHardness(3).setResistance(5);
		subTin 		= (SubBlockOre) new SubBlockOre("oreTin", "amunra:ore-tin").setHarvestInfo("pickaxe", 2).setHardness(3).setResistance(5);
		subTitanium = (SubBlockOreMultidrop) new SubBlockOreMultidrop("oreTitanium", "amunra:ore-titanium").setHarvestInfo("pickaxe", 3).setHardness(3).setResistance(5);


		// BASALT ORE

		metaBlockBasaltOre = new BlockOreMulti("basaltMultiOre", "amunra:basalt", Material.rock);
		metaBlockBasaltOre.setMultiblockHarvestLevel(1).setHardness(2.0F).setResistance(10.0F);
		oreGoldBasalt 		= metaBlockBasaltOre.addSubBlock(0, subGold);
		oreAluBasalt 		= metaBlockBasaltOre.addSubBlock(1, subAlu);
		oreCopperBasalt 	= metaBlockBasaltOre.addSubBlock(2, subCopper);
		oreTinBasalt		= metaBlockBasaltOre.addSubBlock(3, subTin);
		oreCryoBasalt 		= metaBlockBasaltOre.addSubBlock(4, subCryo);
		oreLithiumBasalt 	= metaBlockBasaltOre.addSubBlock(5, subLithium);
		oreIronBasalt	 	= metaBlockBasaltOre.addSubBlock(6, subIron);
		oreLapisBasalt	 	= metaBlockBasaltOre.addSubBlock(7, subLapis);
		oreSiliconBasalt	= metaBlockBasaltOre.addSubBlock(8, subSilicon);
		//oreEmeraldObsid		= multiBlockBasaltOre.addSubBlock(9, subEmerald);
		oreTitaniumBasalt	= metaBlockBasaltOre.addSubBlock(9, subTitanium);
		metaBlockBasaltOre.register();

		// OBSIDIAN ORE
		metaBlockObsidianOre = new BlockOreMulti("obsidianMultiOre", "obsidian", Material.rock);
		metaBlockObsidianOre.setMultiblockHarvestLevel(3).setHardness(50.0F).setResistance(6000.0F);
		oreDiamondObsid 	= metaBlockObsidianOre.addSubBlock(0, subDiamond);
		oreRubyObsid 		= metaBlockObsidianOre.addSubBlock(1, subRuby);
		oreEmeraldObsid 	= metaBlockObsidianOre.addSubBlock(9, subEmerald);
		metaBlockObsidianOre.register();


		//Blocks
		//        Block block = (new Block(Material.rock)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundTypePiston).setBlockName("stonebrick").setCreativeTab(CreativeTabs.tabBlock).setBlockTextureName("cobblestone");
		metaBlockRock = new BlockBasicMeta("baseBlockRock",Material.rock);
    	// blockRegistry.addObject(7, "bedrock", (new Block(Material.rock)).setBlockUnbreakable().setResistance(6000000.0F).setStepSound(soundTypePiston).setBlockName("bedrock").disableStats().setCreativeTab(CreativeTabs.tabBlock).setBlockTextureName("bedrock"));
		//blockDarkMatter = multiBlockRock.addSubBlock(0, new SubBlock("darkMatter", "amunra:darkmatter", "pickaxe", 9000, 9000, 9000));
		blockBasaltCobble	= metaBlockRock.addSubBlock(0, new SubBlock("basaltcobble", "amunra:basaltcobble", "pickaxe", 1, 2.0F, 10.0F));
		blockBasalt 		= metaBlockRock.addSubBlock(1, new SubBlockRock("basalt", "amunra:basalt", "pickaxe", 1, 2.0F, 10.0F).setBlockToDrop(blockBasaltCobble));
		blockRedCobble 		= metaBlockRock.addSubBlock(2, new SubBlock("redrockcobble", "amunra:redrockcobble", "pickaxe", 1, 2.0F, 10.0F));
		blockRedRock 		= metaBlockRock.addSubBlock(3, new SubBlockRock("redrock", "amunra:redrock", "pickaxe", 1, 2.0F, 10.0F).setBlockToDrop(blockRedCobble));
		blockYellowCobble 	= metaBlockRock.addSubBlock(4, new SubBlock("yellowcobble", "amunra:olivinebasaltcobble", "pickaxe", 1, 2.0F, 10.0F));
		blockYellowRock 	= metaBlockRock.addSubBlock(5, new SubBlockRock("yellowrock", "amunra:olivinebasalt", "pickaxe", 1, 2.0F, 10.0F).setBlockToDrop(blockYellowCobble));
		blockAluCrate 		= metaBlockRock.addSubBlock(6, new SubBlock("alucrate", "amunra:alucrate", "pickaxe", 0, 1, 1));

		blockBasaltBrick 	= metaBlockRock.addSubBlock(7, new SubBlock("basaltbrick", "amunra:basaltbrick", "pickaxe", 1, 2.0F, 10.0F));
		blockSmoothBasalt	= metaBlockRock.addSubBlock(8, new SubBlock("smoothbasalt", "amunra:smoothbasalt", "pickaxe", 1, 2.0F, 10.0F));
		blockObsidianBrick	= metaBlockRock.addSubBlock(9, new SubBlock("obsidianbrick", "amunra:obsidianbrick", "pickaxe", 3, 50.0F, 6000.0F));

		metaBlockRock.register();



		// multiBlockRock.addSubBlock(8, new CraftingBlock("crafter")); // TODO figure out later how this works





		metaBlockDirt = new BlockBasicMeta("baseBlockGround", Material.ground);
		metaBlockDirt.setStepSound(Block.soundTypeGravel);


		blockMethaneDirt 	= metaBlockDirt.addSubBlock(0, new SubBlock("methanedirt", "amunra:methanedirt", "shovel", 0, 0.5F, 2.5F));
		blockDust 			= metaBlockDirt.addSubBlock(1, new DustBlock("dustblock", "amunra:dust", "shovel", 0, 0, 0));

		metaBlockDirt.register();


		metaBlockGrass = new BlockGrassMeta("baseGrass", Material.grass);
		metaBlockGrass.setStepSound(Block.soundTypeGrass);
		blockMethaneGrass = metaBlockGrass.addSubBlock(0, new MethaneGrass("methanegrass"));
		metaBlockGrass.register();

		metaBlockFalling = new BlockFallingMeta("baseFalling", Material.sand);
		metaBlockFalling.setStepSound(Block.soundTypeGravel);

		blockObsidiSand 	= metaBlockFalling.addSubBlock(0, new SubBlock("obsidianSand", "amunra:obsidiansand", "shovel", 2, 20.0F, 100.0F));
		blockObsidiGravel 	= metaBlockFalling.addSubBlock(1, new SubBlock("obsidianGravel", "amunra:obsidiangravel", "shovel", 2, 30.0F, 300.0F));
		blockBasaltRegolith = metaBlockFalling.addSubBlock(2, new SubBlock("basaltregolith", "amunra:black_stone", "shovel", 1, 1.0F, 3.0F));

		metaBlockFalling.register();

		//Blocks.diamond_ore

		metaBlockPlant = new BlockBushMulti("basePlant", Material.plants);
		metaBlockPlant.setStepSound(Block.soundTypeGrass);
		blockMethaneTGrass = metaBlockPlant.addSubBlock(0, (SubBlock) new MethaneTallGrass("methaneTallGrass", "amunra:methanetallgrass")
			.setHarvestInfo(null, 0).setHardness(0.0F));
		metaBlockPlant.register();

		// LOGS
		metaBlockLog = new BlockLogMeta("log1", Material.wood);
		metaBlockLog.setStepSound(Block.soundTypeWood);

		blockMethaneLog = metaBlockLog.addSubBlock(0, new SubBlockWood("methanewood", "amunra:log_methane", "amunra:log_methane_top", "axe", 1));

		metaBlockLog.register();

		// WOOD, other wood, etc
		metaBlockNonRotational 	= new BlockBasicMeta("wood1", Material.wood);
		blockPodBark 			= metaBlockNonRotational.addSubBlock(0, new SubBlock("podBark", "amunra:pod_bark", "axe", 0));
		blockPodLeaf 			= metaBlockNonRotational.addSubBlock(1, (SubBlock) new PodMeatBlock("podleaf", "amunra:podleaves").setLightLevel(0.8F));
		blockMethanePlanks 		= metaBlockNonRotational.addSubBlock(2, new SubBlock("methanePlanks", "amunra:planks_methane", "axe", 0));
		blockPodPlanks 			= metaBlockNonRotational.addSubBlock(3, new SubBlock("podPlanks", "amunra:planks_pod", "axe", 0));
		metaBlockNonRotational.register();

		// LEAVES
		metaBlockLeaf = new BlockLeafMeta(Material.leaves, true);
		blockMethaneLeaf = metaBlockLeaf.addSubBlock(0, new SubBlockLeaf("methaneleaf", "amunra:leaves_methane"));

		metaBlockLeaf.register();

		// SAPLINGS
		metaBlockSapling = new BlockBushMulti("saplings", Material.grass, 7);
		metaBlockSapling.setTickRandomly(true);

		blockMethaneSapling = metaBlockSapling.addSubBlock(0, new ARTreeSapling("mTreeSapling", "amunra:methane_tree_sapling").setWood(blockMethaneLog).setLeaves(blockMethaneLeaf));
		blockPodSapling = metaBlockSapling.addSubBlock(1, new PodSapling("podSapling", "amunra:lumipod_sapling").setWood(blockPodBark).setLeaves(blockPodLeaf));


		metaBlockSapling.register();


		setLeafDroppingSapling(blockMethaneLeaf, blockMethaneSapling);




		// STAIRS
		stairsObsidianBrick = new BlockStairsAR(blockObsidianBrick);
		stairsObsidianBrick.register();

		stairsSmoothBasalt = new BlockStairsAR(blockSmoothBasalt);
		stairsSmoothBasalt.register();

		stairsBasaltBrick = new BlockStairsAR(blockBasaltBrick);
		stairsBasaltBrick.register();

		stairsBasaltBlock = new BlockStairsAR(blockBasalt);
		stairsBasaltBlock.register();

		stairsAluCrate = new BlockStairsAR(blockAluCrate);
		stairsAluCrate.register();

		stairsMethanePlanks = new BlockStairsAR(blockMethanePlanks);
		stairsMethanePlanks.register();

		stairsPodPlanks = new BlockStairsAR(blockPodPlanks);
		stairsPodPlanks.register();

		// SLABS
		metaSlabRock = new BlockSlabMeta("rockSlab", Material.rock);
		slabBasaltBlock 	= metaSlabRock.addSubBlock(0, blockBasalt);
		slabBasaltBrick 	= metaSlabRock.addSubBlock(1, blockBasaltBrick);
		slabBasaltSmooth  	= metaSlabRock.addSubBlock(2, blockSmoothBasalt);
		slabObsidianBrick 	= metaSlabRock.addSubBlock(3, blockObsidianBrick);
		slabAluCrate		= metaSlabRock.addSubBlock(4, blockAluCrate);
		metaSlabRock.register();

		metaSlabWood = new BlockSlabMeta("woodSlab", Material.wood);
		//metaSlabWood.addSubBlock(0, blockPodBark);
		slabPodPlanks 		= metaSlabWood.addSubBlock(0, blockPodPlanks);
		slabMethanePlanks 	= metaSlabWood.addSubBlock(1, blockMethanePlanks);
		metaSlabWood.register();

		registerOreDict();
    }

	private static void setLeafDroppingSapling(BlockMetaPair leaf, BlockMetaPair sapling) {
		((SubBlockLeaf)((BlockLeafMeta)leaf.getBlock()).getSubBlock(leaf.getMetadata())).setSaplingDropped(sapling);
	}

	protected static void registerOreDict() {


		OreDictionary.registerOre("oreCopper", getItemStack(oreCopperBasalt, 1));
		OreDictionary.registerOre("oreAluminum", getItemStack(oreAluBasalt, 1));
		OreDictionary.registerOre("oreAluminium", getItemStack(oreAluBasalt, 1));
		OreDictionary.registerOre("oreNaturalAluminum", getItemStack(oreAluBasalt, 1));
		OreDictionary.registerOre("oreTin", getItemStack(oreTinBasalt, 1));
		OreDictionary.registerOre("oreSilicon", getItemStack(oreSiliconBasalt, 1));
		OreDictionary.registerOre("oreGold", getItemStack(oreGoldBasalt, 1));
		OreDictionary.registerOre("oreIron", getItemStack(oreIronBasalt, 1));
		OreDictionary.registerOre("oreLapis", getItemStack(oreLapisBasalt, 1));
		OreDictionary.registerOre("oreDiamond", getItemStack(oreDiamondObsid, 1));
		OreDictionary.registerOre("oreRuby", getItemStack(oreRubyObsid, 1));
		OreDictionary.registerOre("oreLithium", getItemStack(oreLithiumBasalt, 1));

	}


	public static SubBlockOre getSubBlockOre(BlockMetaPair bmp) {
		return (SubBlockOre) ((BlockBasicMeta)bmp.getBlock()).getSubBlock(bmp.getMetadata());
	}

}
