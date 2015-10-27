package de.katzenpapst.amunra.world.mapgen.village.populator;

import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;

public class FillChest extends AbstractPopulator {
	
	protected BlockMetaPair chestBlock;
	protected String chestGenName;

	public FillChest(int x, int y, int z, BlockMetaPair chestBlock, String chestGenName) {
		super(x, y, z);
		this.chestBlock = chestBlock;
		this.chestGenName = chestGenName;
	}

	@Override
	public boolean populate(World world) {
		world.setBlock(x, y, z, chestBlock.getBlock(), chestBlock.getMetadata(), 2);
        TileEntityChest chest = (TileEntityChest) world.getTileEntity(x, y, z);

        if (chest != null)
        {
        	// this clears the chest
            for (int i = 0; i < chest.getSizeInventory(); i++)
            {
                chest.setInventorySlotContents(i, null);
            }

            // hmm that is an interesting concept
            ChestGenHooks info = ChestGenHooks.getInfo(chestGenName);

            WeightedRandomChestContent.generateChestContents(world.rand, info.getItems(world.rand), chest, info.getCount(world.rand));
        }
		return false;
	}

}
