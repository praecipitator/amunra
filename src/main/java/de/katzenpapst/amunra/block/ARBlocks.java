package de.katzenpapst.amunra.block;

import cpw.mods.fml.common.registry.GameRegistry;
import de.katzenpapst.amunra.AmunRa;

import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import micdoodle8.mods.galacticraft.core.items.ItemBlockLandingPad;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class ARBlocks {
	public static BlockBasicMulti multiBlockRock;
	public static BlockBasicMulti multiBlockDirt;

	public static void initBlocks()
    {
		multiBlockRock = new BlockBasicMulti("baseBlockRock",Material.rock, 5);
    	// blockRegistry.addObject(7, "bedrock", (new Block(Material.rock)).setBlockUnbreakable().setResistance(6000000.0F).setStepSound(soundTypePiston).setBlockName("bedrock").disableStats().setCreativeTab(CreativeTabs.tabBlock).setBlockTextureName("bedrock"));
		multiBlockRock.addSubBlock(0, new SubBlock("darkMatter", "amunra:darkmatter", "pickaxe", 9000, 9000, 9000));
		multiBlockRock.addSubBlock(1, new SubBlock("basalt", "amunra:basalt", "pickaxe", 1));
		multiBlockRock.addSubBlock(2, new SubBlock("bluerock", "amunra:bluerock", "pickaxe", 1));
		multiBlockRock.addSubBlock(3, new SubBlock("redrock", "amunra:redrock", "pickaxe", 1));
		multiBlockRock.addSubBlock(4, new SubBlock("yellowrock", "amunra:olivinebasalt", "pickaxe", 1));
		multiBlockRock.addSubBlock(5, new SubBlock("alucrate", "amunra:alucrate", "pickaxe", 1, 1, 1));
		multiBlockRock.addSubBlock(6, new SubBlock("basaltbrick", "amunra:basaltbrick", "pickaxe", 1));
		multiBlockRock.addSubBlock(7, new SubBlock("smoothbasalt", "amunra:smoothbasalt", "pickaxe", 1));

		multiBlockRock.register();
		
		
		multiBlockDirt = new BlockBasicMulti("baseBlockGround", Material.ground, 4);
		multiBlockDirt.setStepSound(Block.soundTypeGravel);
		multiBlockDirt.addSubBlock(0, new SubBlock("basaltregolith", "amunra:black_stone", "shovel", 1));
		multiBlockDirt.addSubBlock(1, new SubBlock("methanedirt", "amunra:methanedirt", "shovel", 1));
		multiBlockDirt.addSubBlock(2, (SubBlock) new MethaneGrass("methanegrass"));
		multiBlockDirt.addSubBlock(3, new SubBlock("dustblock", "amunra:dust", "shovel", 0, 0, 0));
		multiBlockDirt.register();
		
    }
}
