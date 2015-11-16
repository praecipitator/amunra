package de.katzenpapst.amunra.block;

import de.katzenpapst.amunra.item.ItemDamagePair;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ARBlocks {
	public static BlockBasicMulti multiBlockRock;
	// public static BlockBasicMulti multiBlockOre1;
	public static BlockBasicMulti multiBlockDirt;
	public static BlockBasicMulti multiBlockGrass;
	public static BlockBasicMulti multiBlockFalling;
	public static BlockBasicMulti multiBlockPlant;
	public static BlockBasicMulti multiBlockLog;
	public static BlockBasicMulti multiBlockNonRotational;
	public static BlockOreMulti multiBlockBasaltOre;
	public static BlockOreMulti multiBlockObsidianOre;

	public static IMultiBlock multiBlockLeaf;
	public static BlockBasicMulti multiBlockSapling;

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


	public static BlockMetaPair BlockMethaneTGrass;
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

		multiBlockBasaltOre = new BlockOreMulti("basaltMultiOre", "amunra:basalt", Material.rock);
		multiBlockBasaltOre.setMultiblockHarvestLevel(1).setHardness(2.0F).setResistance(10.0F);
		oreGoldBasalt 		= multiBlockBasaltOre.addSubBlock(0, subGold);
		oreAluBasalt 		= multiBlockBasaltOre.addSubBlock(1, subAlu);
		oreCopperBasalt 	= multiBlockBasaltOre.addSubBlock(2, subCopper);
		oreTinBasalt		= multiBlockBasaltOre.addSubBlock(3, subTin);
		oreCryoBasalt 		= multiBlockBasaltOre.addSubBlock(4, subCryo);
		oreLithiumBasalt 	= multiBlockBasaltOre.addSubBlock(5, subLithium);
		oreIronBasalt	 	= multiBlockBasaltOre.addSubBlock(6, subIron);
		oreLapisBasalt	 	= multiBlockBasaltOre.addSubBlock(7, subLapis);
		oreSiliconBasalt	= multiBlockBasaltOre.addSubBlock(8, subSilicon);
		//oreEmeraldObsid		= multiBlockBasaltOre.addSubBlock(9, subEmerald);
		oreTitaniumBasalt	= multiBlockBasaltOre.addSubBlock(9, subTitanium);
		multiBlockBasaltOre.register();

		// OBSIDIAN ORE
		multiBlockObsidianOre = new BlockOreMulti("obsidianMultiOre", "obsidian", Material.rock);
		multiBlockObsidianOre.setMultiblockHarvestLevel(3).setHardness(50.0F).setResistance(6000.0F);
		oreDiamondObsid 	= multiBlockObsidianOre.addSubBlock(0, subDiamond);
		oreRubyObsid 		= multiBlockObsidianOre.addSubBlock(1, subRuby);
		oreEmeraldObsid 	= multiBlockObsidianOre.addSubBlock(9, subEmerald);
		multiBlockObsidianOre.register();


		//Blocks
		//        Block block = (new Block(Material.rock)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundTypePiston).setBlockName("stonebrick").setCreativeTab(CreativeTabs.tabBlock).setBlockTextureName("cobblestone");
		multiBlockRock = new BlockBasicMulti("baseBlockRock",Material.rock);
    	// blockRegistry.addObject(7, "bedrock", (new Block(Material.rock)).setBlockUnbreakable().setResistance(6000000.0F).setStepSound(soundTypePiston).setBlockName("bedrock").disableStats().setCreativeTab(CreativeTabs.tabBlock).setBlockTextureName("bedrock"));
		//blockDarkMatter = multiBlockRock.addSubBlock(0, new SubBlock("darkMatter", "amunra:darkmatter", "pickaxe", 9000, 9000, 9000));
		blockBasaltCobble	= multiBlockRock.addSubBlock(0, new SubBlock("basaltcobble", "amunra:basaltcobble", "pickaxe", 1, 2.0F, 10.0F));
		blockBasalt 		= multiBlockRock.addSubBlock(1, new SubBlockRock("basalt", "amunra:basalt", "pickaxe", 1, 2.0F, 10.0F).setBlockToDrop(blockBasaltCobble));
		blockRedCobble 		= multiBlockRock.addSubBlock(2, new SubBlock("redrockcobble", "amunra:redrockcobble", "pickaxe", 1, 2.0F, 10.0F));
		blockRedRock 		= multiBlockRock.addSubBlock(3, new SubBlockRock("redrock", "amunra:redrock", "pickaxe", 1, 2.0F, 10.0F).setBlockToDrop(blockRedCobble));
		blockYellowCobble 	= multiBlockRock.addSubBlock(4, new SubBlock("yellowcobble", "amunra:olivinebasaltcobble", "pickaxe", 1, 2.0F, 10.0F));
		blockYellowRock 	= multiBlockRock.addSubBlock(5, new SubBlockRock("yellowrock", "amunra:olivinebasalt", "pickaxe", 1, 2.0F, 10.0F).setBlockToDrop(blockYellowCobble));
		blockAluCrate 		= multiBlockRock.addSubBlock(6, new SubBlock("alucrate", "amunra:alucrate", "pickaxe", 0, 1, 1));

		blockBasaltBrick 	= multiBlockRock.addSubBlock(7, new SubBlock("basaltbrick", "amunra:basaltbrick", "pickaxe", 1, 2.0F, 10.0F));
		blockSmoothBasalt	= multiBlockRock.addSubBlock(8, new SubBlock("smoothbasalt", "amunra:smoothbasalt", "pickaxe", 1, 2.0F, 10.0F));
		blockObsidianBrick	= multiBlockRock.addSubBlock(9, new SubBlock("obsidianbrick", "amunra:obsidianbrick", "pickaxe", 3, 50.0F, 6000.0F));

		multiBlockRock.register();



		// multiBlockRock.addSubBlock(8, new CraftingBlock("crafter")); // TODO figure out later how this works





		multiBlockDirt = new BlockBasicMulti("baseBlockGround", Material.ground);
		multiBlockDirt.setStepSound(Block.soundTypeGravel);


		blockMethaneDirt 	= multiBlockDirt.addSubBlock(0, new SubBlock("methanedirt", "amunra:methanedirt", "shovel", 0, 0.5F, 2.5F));
		blockDust 			= multiBlockDirt.addSubBlock(1, new DustBlock("dustblock", "amunra:dust", "shovel", 0, 0, 0));

		multiBlockDirt.register();


		multiBlockGrass = new BlockGrassMulti("baseGrass", Material.grass);
		multiBlockGrass.setStepSound(Block.soundTypeGrass);
		blockMethaneGrass = multiBlockGrass.addSubBlock(0, new MethaneGrass("methanegrass"));
		multiBlockGrass.register();

		multiBlockFalling = new BlockFallingMulti("baseFalling", Material.sand);
		multiBlockFalling.setStepSound(Block.soundTypeGravel);

		blockObsidiSand 	= multiBlockFalling.addSubBlock(0, new SubBlock("obsidianSand", "amunra:obsidiansand", "shovel", 2, 20.0F, 100.0F));
		blockObsidiGravel 	= multiBlockFalling.addSubBlock(1, new SubBlock("obsidianGravel", "amunra:obsidiangravel", "shovel", 2, 30.0F, 300.0F));
		blockBasaltRegolith = multiBlockFalling.addSubBlock(2, new SubBlock("basaltregolith", "amunra:black_stone", "shovel", 1, 1.0F, 3.0F));

		multiBlockFalling.register();

		//Blocks.diamond_ore

		multiBlockPlant = new BlockBushMulti("basePlant", Material.plants);
		multiBlockPlant.setStepSound(Block.soundTypeGrass);
		BlockMethaneTGrass = multiBlockPlant.addSubBlock(0, (SubBlock) new MethaneTallGrass("methaneTallGrass", "amunra:methanetallgrass")
			.setHarvestInfo(null, 0).setHardness(0.0F));
		multiBlockPlant.register();

		// LOGS
		multiBlockLog = new BlockLogMulti("wood1", Material.wood);
		multiBlockLog.setStepSound(Block.soundTypeWood);

		blockMethaneLog = multiBlockLog.addSubBlock(0, new SubBlockWood("methanewood", "amunra:log_methane", "amunra:log_methane_top", "axe", 1));

		multiBlockLog.register();

		// NON-ROTATIONAL LOGS, other wood, etc
		multiBlockNonRotational = new BlockBasicMulti("nonRotationLog", Material.wood);
		blockPodBark = multiBlockNonRotational.addSubBlock(0, new SubBlock("podBark", "amunra:pod_bark", "axe", 0));
		blockPodLeaf = multiBlockNonRotational.addSubBlock(1, (SubBlock) new PodMeatBlock("podleaf", "amunra:podleaves").setLightLevel(0.8F));
		multiBlockNonRotational.register();

		// LEAVES
		multiBlockLeaf = new BlockLeafMulti(Material.leaves, true);
		blockMethaneLeaf = multiBlockLeaf.addSubBlock(0, new SubBlockLeaf("methaneleaf", "amunra:leaves_methane"));

		multiBlockLeaf.register();

		// SAPLINGS
		multiBlockSapling = new BlockBushMulti("saplings", Material.grass, 7);
		multiBlockSapling.setTickRandomly(true);

		blockMethaneSapling = multiBlockSapling.addSubBlock(0, new ARTreeSapling("mTreeSapling", "amunra:methane_tree_sapling").setWood(blockMethaneLog).setLeaves(blockMethaneLeaf));
		blockPodSapling = multiBlockSapling.addSubBlock(1, new PodSapling("podSapling", "amunra:lumipod_sapling").setWood(blockPodBark).setLeaves(blockPodLeaf));


		multiBlockSapling.register();


		setLeafDroppingSapling(blockMethaneLeaf, blockMethaneSapling);







		//((AbstractSapling)blockMethaneSapling.getBlock()).setWood(blockMethaneLog).setLeaves(blockMethaneLeaf);
		//((AbstractSapling)blockPodSapling.getBlock()).setWood(blockMethaneLog).setLeaves(blockMethaneLeaf);



		registerOreDict();
    }

	private static void setLeafDroppingSapling(BlockMetaPair leaf, BlockMetaPair sapling) {
		((SubBlockLeaf)((BlockLeafMulti)leaf.getBlock()).getSubBlock(leaf.getMetadata())).setSaplingDropped(sapling);
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
		return (SubBlockOre) ((BlockBasicMulti)bmp.getBlock()).getSubBlock(bmp.getMetadata());
	}

}
