package de.katzenpapst.amunra.item;

import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.block.BlockMachineMeta;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemJet extends Item {

    public ItemJet(String assetName) {
        super();
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setMaxStackSize(1);
        this.setTextureName(AmunRa.instance.TEXTUREPREFIX + assetName);
        this.setUnlocalizedName(assetName);

        //Items.bed
    }

    @Override
    public CreativeTabs getCreativeTab()
    {
        return AmunRa.instance.arTab;
    }

    /**
     * itemstack, player, world, x, y, z, side, hitX, hitY, hitZ
     */
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        if (world.isRemote)
        {
            return true;
        }
        /*else if (side != 1)
        {
            return false;
        }*/
        else
        {
            ++y;
            //BlockBed blockbed = (BlockBed)Blocks.bed;
            BlockMetaPair blockJet = ARBlocks.blockMothershipEngineRocket;
            int blockRotation = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;


            if (player.canPlayerEdit(x, y, z, side, itemStack))
            {
                if (world.isAirBlock(x, y, z))
                {

                    world.setBlock(x, y, z, blockJet.getBlock(), BlockMachineMeta.addRotationMeta(blockJet.getMetadata(), blockRotation), 3);

                    /*if (world.getBlock(x, y, z) == blockbed)
                    {
                        world.setBlock(x + otherBlockX, y, z + otherBlockZ, blockbed, blockRotation + 8, 3);
                    }*/

                    --itemStack.stackSize;
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        }
    }

}
