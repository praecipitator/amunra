package de.katzenpapst.amunra.world.mapgen.populator;

import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.inventory.IInventory;
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
        // world.setBlock(x, y, z, chestBlock.getBlock(), chestBlock.getMetadata(), 2);
        IInventory chest = (IInventory) world.getTileEntity(x, y, z);

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
            return true;
        }
        return false;
    }

}
