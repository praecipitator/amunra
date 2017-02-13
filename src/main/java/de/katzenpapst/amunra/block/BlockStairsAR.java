package de.katzenpapst.amunra.block;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.helper.BlockMassHelper;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.block.BlockStairs;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.World;

public class BlockStairsAR extends BlockStairs implements IMassiveBlock {

    BlockMetaPair sourceBlock;

    public BlockStairsAR(BlockMetaPair sourceBlock) {
        // protected constructor? WTF IS THIS SHIT?!!?!
        super(sourceBlock.getBlock(), sourceBlock.getMetadata());
        this.sourceBlock = sourceBlock;
    }


    @Override
    public String getUnlocalizedName()
    {
        IMetaBlock mBlock = (IMetaBlock)this.sourceBlock.getBlock();
        if(mBlock != null) {
            return "tile."+mBlock.getSubBlock(sourceBlock.getMetadata()).getUnlocalizedName()+".stairs";
        }
        return "tile."+this.sourceBlock.getBlock().getUnlocalizedName()+".stairs";
    }

    public void register() {
        GameRegistry.registerBlock(this, ItemBlock.class, this.getUnlocalizedName());


        this.setHarvestLevel(
                sourceBlock.getBlock().getHarvestTool(sourceBlock.getMetadata()),
                sourceBlock.getBlock().getHarvestLevel(sourceBlock.getMetadata())
                );

    }

    @SideOnly(Side.CLIENT)
    @Override
    public CreativeTabs getCreativeTabToDisplayOn()
    {
        return AmunRa.arTab;
    }

    @Override
    public float getMass(World w, int x, int y, int z, int meta) {
        float parentMass = BlockMassHelper.getBlockMass(w, sourceBlock.getBlock(), sourceBlock.getMetadata(), x, y, z);
        // 4/6 = 2/3, because stairs
        return parentMass*2.0F/3.0F;
    }


}
