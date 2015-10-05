package de.katzenpapst.amunra.block;

import cpw.mods.fml.common.registry.GameRegistry;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.item.ItemBasicRock;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import micdoodle8.mods.galacticraft.core.items.ItemBlockLandingPad;
import net.minecraft.block.Block;

public class ARBlocks {
	public static Block rock;

	public static void initBlocks()
    {
		ARBlocks.rock = new BlockBasicRock();
		
		// ARBlocks.rock.setCreativeTab(AmunRa.arTab);
		
		GameRegistry.registerBlock(ARBlocks.rock, ItemBasicRock.class, ARBlocks.rock.getUnlocalizedName());
		
		ARBlocks.rock.setHarvestLevel("pickaxe", 1, 0);
		ARBlocks.rock.setHarvestLevel("pickaxe", 1, 1);
		ARBlocks.rock.setHarvestLevel("pickaxe", 1, 2);
    }
}
