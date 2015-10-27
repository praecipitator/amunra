package de.katzenpapst.amunra.block;

import cpw.mods.fml.common.registry.GameRegistry;
import de.katzenpapst.amunra.AmunRa;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import micdoodle8.mods.galacticraft.core.items.ItemBlockLandingPad;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;

public class ARBlocks {
	public static BlockBasicMulti multiBlockRock;
	public static BlockBasicMulti multiBlockDirt;
	public static BlockBasicMulti multiBlockGrass;
	public static BlockBasicMulti multiBlockFalling;
	
	public static BlockMetaPair blockDarkMatter;
	public static BlockMetaPair blockBasalt;
	public static BlockMetaPair blockRedRock;
	public static BlockMetaPair blockYellowRock;
	public static BlockMetaPair blockBlueRock;
	public static BlockMetaPair blockAluCrate;
	public static BlockMetaPair blockBasaltBrick;
	public static BlockMetaPair blockSmoothBasalt;
	
	
	public static BlockMetaPair blockBasaltRegolith;
	public static BlockMetaPair blockMethaneDirt;
	public static BlockMetaPair blockDust;
	
	
	public static BlockMetaPair blockMethaneGrass;
	public static BlockMetaPair blockObsidiSand;
	public static BlockMetaPair blockObsidiGravel;
	
	
	

	public static void initBlocks()
    {
		multiBlockRock = new BlockBasicMulti("baseBlockRock",Material.rock, 5);
    	// blockRegistry.addObject(7, "bedrock", (new Block(Material.rock)).setBlockUnbreakable().setResistance(6000000.0F).setStepSound(soundTypePiston).setBlockName("bedrock").disableStats().setCreativeTab(CreativeTabs.tabBlock).setBlockTextureName("bedrock"));
		blockDarkMatter = multiBlockRock.addSubBlock(0, new SubBlock("darkMatter", "amunra:darkmatter", "pickaxe", 9000, 9000, 9000));
		blockBasalt 	= multiBlockRock.addSubBlock(1, new SubBlock("basalt", "amunra:basalt", "pickaxe", 1));
		blockBlueRock 	= multiBlockRock.addSubBlock(2, new SubBlock("bluerock", "amunra:bluerock", "pickaxe", 1));
		blockRedRock 	= multiBlockRock.addSubBlock(3, new SubBlock("redrock", "amunra:redrock", "pickaxe", 1));
		blockYellowRock = multiBlockRock.addSubBlock(4, new SubBlock("yellowrock", "amunra:olivinebasalt", "pickaxe", 1));
		blockAluCrate 	= multiBlockRock.addSubBlock(5, new SubBlock("alucrate", "amunra:alucrate", "pickaxe", 1, 1, 1));
		
		blockBasaltBrick = multiBlockRock.addSubBlock(6, new SubBlock("basaltbrick", "amunra:basaltbrick", "pickaxe", 1));
		blockSmoothBasalt= multiBlockRock.addSubBlock(7, new SubBlock("smoothbasalt", "amunra:smoothbasalt", "pickaxe", 1));
		// multiBlockRock.addSubBlock(8, new CraftingBlock("crafter")); // TODO figure out later how this works
		

		multiBlockRock.register();
		
		
		multiBlockDirt = new BlockBasicMulti("baseBlockGround", Material.ground, 4);
		multiBlockDirt.setStepSound(Block.soundTypeGravel);
		
		blockBasaltRegolith = multiBlockDirt.addSubBlock(0, new SubBlock("basaltregolith", "amunra:black_stone", "shovel", 1));
		blockMethaneDirt 	= multiBlockDirt.addSubBlock(1, new SubBlock("methanedirt", "amunra:methanedirt", "shovel", 1));
		// multiBlockDirt.addSubBlock(2, (SubBlock) new MethaneGrass("methanegrass"));
		blockDust 			= multiBlockDirt.addSubBlock(3, new SubBlock("dustblock", "amunra:dust", "shovel", 0, 0, 0));
		
		multiBlockDirt.register();
		
		
		multiBlockGrass = new BlockGrassMulti("baseGrass", Material.grass, 5);
		multiBlockGrass.setStepSound(Block.soundTypeGrass);
		blockMethaneGrass = multiBlockGrass.addSubBlock(0, (SubBlock) new MethaneGrass("methanegrass"));
		multiBlockGrass.register();
		
		multiBlockFalling = new BlockFallingMulti("baseFalling", Material.sand, 5);
		multiBlockFalling.setStepSound(Block.soundTypeGravel);
		
		blockObsidiSand 	= multiBlockFalling.addSubBlock(0, new SubBlock("obsidianSand", "amunra:obsidiansand", "shovel", 2));
		blockObsidiGravel 	= multiBlockFalling.addSubBlock(1, new SubBlock("obsidianGravel", "amunra:obsidiangravel", "shovel", 2));
		
		multiBlockFalling.register();
		
		
		// Blocks.gravel
		
    }
}
