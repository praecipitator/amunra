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
	public static BlockBasicMulti multiBlockOre1;
	public static BlockBasicMulti multiBlockDirt;
	public static BlockBasicMulti multiBlockGrass;
	public static BlockBasicMulti multiBlockFalling;
	public static BlockBasicMulti multiBlockPlant;
	public static BlockBasicMulti multiBlockLog;
	public static BlockBasicMulti multiBlockNonRotational;
	public static BlockOreVariable multiBlockBasaltOre;

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


	public static BlockMetaPair BlockMethaneTGrass;
	public static BlockMetaPair blockMethaneLog;
	public static BlockMetaPair blockPodBark;

	public static BlockMetaPair blockPodLeaf;
	public static BlockMetaPair blockMethaneLeaf;
	public static BlockMetaPair blockMethaneSapling;
	public static BlockMetaPair blockPodSapling;



	public static ItemStack getItemStack(BlockMetaPair input, int amount) {
		return new ItemStack(input.getBlock(), amount, input.getMetadata());
	}

	public static ItemDamagePair getBlockIDP(BlockMetaPair input) {
		return new ItemDamagePair(Item.getItemFromBlock(input.getBlock()), input.getMetadata());
	}

	public static void initBlocks()
    {
		//Blocks
		//        Block block = (new Block(Material.rock)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundTypePiston).setBlockName("stonebrick").setCreativeTab(CreativeTabs.tabBlock).setBlockTextureName("cobblestone");
		multiBlockRock = new BlockBasicMulti("baseBlockRock",Material.rock);
    	// blockRegistry.addObject(7, "bedrock", (new Block(Material.rock)).setBlockUnbreakable().setResistance(6000000.0F).setStepSound(soundTypePiston).setBlockName("bedrock").disableStats().setCreativeTab(CreativeTabs.tabBlock).setBlockTextureName("bedrock"));
		//blockDarkMatter = multiBlockRock.addSubBlock(0, new SubBlock("darkMatter", "amunra:darkmatter", "pickaxe", 9000, 9000, 9000));
		blockBasaltCobble	= multiBlockRock.addSubBlock(0, new SubBlock("basaltcobble", "amunra:basaltcobble", "pickaxe", 2, 2.0F, 10.0F));
		blockBasalt 		= multiBlockRock.addSubBlock(1, new SubBlockRock("basalt", "amunra:basalt", "pickaxe", 2, 2.0F, 10.0F).setBlockToDrop(blockBasaltCobble));
		blockRedCobble 		= multiBlockRock.addSubBlock(2, new SubBlock("redrockcobble", "amunra:redrockcobble", "pickaxe", 2, 2.0F, 10.0F));
		blockRedRock 		= multiBlockRock.addSubBlock(3, new SubBlockRock("redrock", "amunra:redrock", "pickaxe", 2, 2.0F, 10.0F).setBlockToDrop(blockRedCobble));
		blockYellowCobble 	= multiBlockRock.addSubBlock(4, new SubBlock("yellowcobble", "amunra:olivinebasaltcobble", "pickaxe", 2, 2.0F, 10.0F));
		blockYellowRock 	= multiBlockRock.addSubBlock(5, new SubBlockRock("yellowrock", "amunra:olivinebasalt", "pickaxe", 2, 2.0F, 10.0F).setBlockToDrop(blockYellowCobble));
		blockAluCrate 		= multiBlockRock.addSubBlock(6, new SubBlock("alucrate", "amunra:alucrate", "pickaxe", 0, 1, 1));

		blockBasaltBrick 	= multiBlockRock.addSubBlock(7, new SubBlock("basaltbrick", "amunra:basaltbrick", "pickaxe", 2, 2.0F, 10.0F));
		blockSmoothBasalt	= multiBlockRock.addSubBlock(8, new SubBlock("smoothbasalt", "amunra:smoothbasalt", "pickaxe", 2, 2.0F, 10.0F));
		blockObsidianBrick	= multiBlockRock.addSubBlock(9, new SubBlock("obsidianbrick", "amunra:obsidianbrick", "pickaxe", 3, 50.0F, 6000.0F));

		// these need to be inited after the items are done
		// TODO maybe make multiblocks for ores, and their subitems are their variations?
		// see: http://www.minecraftforge.net/wiki/Multiple_Pass_Render_Blocks
		// there might be a way to actually use dynamic textures


		multiBlockRock.register();

		multiBlockOre1 = new BlockBasicMulti("ore1", Material.rock);

		oreCryoBasalt	= multiBlockOre1.addSubBlock(0, new SubBlockOre("cryoBasaltOre", "amunra:ore-cryo-basalt", "pickaxe", 2, 2.0F, 10.0F));
		oreDiamondObsid	= multiBlockOre1.addSubBlock(1, new SubBlockOre("diamondObsidiOre", "amunra:ore-diamond-obsidian", "pickaxe", 3, 50.0F, 6000.0F));
		oreRubyObsid	= multiBlockOre1.addSubBlock(2, new SubBlockOre("rubyObsidiOre", "amunra:ore-ruby-obsidian", "pickaxe", 3, 50.0F, 6000.0F));
		oreLithiumBasalt= multiBlockOre1.addSubBlock(3, new SubBlockOre("oreLithiumBasalt", "amunra:ore-lithium-basalt", "pickaxe", 2, 2.0F, 10.0F));

		oreAluBasalt	= multiBlockOre1.addSubBlock(4,  new SubBlockOre("oreAluBasalt", "amunra:ore-alu-basalt", "pickaxe", 2, 2.0F, 10.0F));
		oreCopperBasalt	= multiBlockOre1.addSubBlock(5,  new SubBlockOre("oreCopperBasalt", "amunra:ore-copper-basalt", "pickaxe", 2, 2.0F, 10.0F));
		oreGoldBasalt	= multiBlockOre1.addSubBlock(6,  new SubBlockOre("oreGoldBasalt", "amunra:ore-gold-basalt", "pickaxe", 2, 2.0F, 10.0F));
		oreIronBasalt	= multiBlockOre1.addSubBlock(7,  new SubBlockOre("oreIronBasalt", "amunra:ore-iron-basalt", "pickaxe", 2, 2.0F, 10.0F));
		oreLapisBasalt	= multiBlockOre1.addSubBlock(8,  new SubBlockOre("oreLapisBasalt", "amunra:ore-lapis-basalt", "pickaxe", 2, 2.0F, 10.0F));
		oreSiliconBasalt= multiBlockOre1.addSubBlock(9,  new SubBlockOre("oreSiliconBasalt", "amunra:ore-silicon-basalt", "pickaxe", 2, 2.0F, 10.0F));
		oreTinBasalt	= multiBlockOre1.addSubBlock(10, new SubBlockOre("oreTinBasalt", "amunra:ore-tin-basalt", "pickaxe", 2, 2.0F, 10.0F));

		oreEmeraldObsid	= multiBlockOre1.addSubBlock(11, new SubBlockOre("emeraldObsidiOre", "amunra:ore-emerald-obsidian", "pickaxe", 3, 50.0F, 6000.0F));

		multiBlockOre1.register();


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
		BlockMethaneTGrass = multiBlockPlant.addSubBlock(0, new MethaneTallGrass("methaneTallGrass", "amunra:methanetallgrass"));
		multiBlockPlant.register();

		// LOGS
		multiBlockLog = new BlockLogMulti("wood1", Material.wood);
		multiBlockLog.setStepSound(Block.soundTypeWood);

		blockMethaneLog = multiBlockLog.addSubBlock(0, new SubBlockWood("methanewood", "amunra:log_methane", "amunra:log_methane_top", "axe", 1));

		multiBlockLog.register();

		// NON-ROTATIONAL LOGS, other wood, etc
		multiBlockNonRotational = new BlockBasicMulti("nonRotationLog", Material.wood);
		blockPodBark = multiBlockNonRotational.addSubBlock(0, new SubBlock("podBark", "amunra:pod_bark"));
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

		multiBlockBasaltOre = new BlockOreVariable("test", "amunra:basalt", Material.rock);
		multiBlockBasaltOre.addSubBlock(0, new SubBlockOre("leGold", "amunra:ore-gold"));
		multiBlockBasaltOre.register();



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
