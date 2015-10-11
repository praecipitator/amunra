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
		multiBlockRock = new BlockBasicMulti("baseBlockRock",Material.rock, 2);
    	// blockRegistry.addObject(7, "bedrock", (new Block(Material.rock)).setBlockUnbreakable().setResistance(6000000.0F).setStepSound(soundTypePiston).setBlockName("bedrock").disableStats().setCreativeTab(CreativeTabs.tabBlock).setBlockTextureName("bedrock"));
		multiBlockRock.addSubBlock(0, new SubBlock(Material.rock, "darkMatter", "amunra:darkmatter", "pickaxe", 9000, 9000, 9000));
		multiBlockRock.addSubBlock(1, new SubBlock(Material.rock, "basalt", "amunra:basalt", "pickaxe", 1));

		multiBlockRock.register();
		
		
		multiBlockDirt = new BlockBasicMulti("baseBlockGround", Material.ground, 3);
		multiBlockDirt.setStepSound(Block.soundTypeGravel);
		multiBlockDirt.addSubBlock(0, new SubBlock(Material.ground, "basaltregolith", "amunra:black_stone", "shovel", 1));
		multiBlockDirt.addSubBlock(1, new SubBlock(Material.ground, "methanedirt", "amunra:methanedirt", "shovel", 1));
		multiBlockDirt.addSubBlock(2, (SubBlock) new MethaneGrass("methanegrass"));
		multiBlockDirt.addSubBlock(3, new SubBlock(Material.ground, "dustblock", "amunra:dust", "shovel", 0, 0, 0));
		multiBlockDirt.register();
		
    }
}
