package de.katzenpapst.amunra.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.block.BlockMachineMeta;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemJet extends ItemBlock {

    protected int blockMeta;



    public ItemJet(BlockMetaPair blockMothershipEngineRocket, String assetName) {
        super(blockMothershipEngineRocket.getBlock());
        blockMeta = blockMothershipEngineRocket.getMetadata();
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setMaxStackSize(1);
        this.setTextureName(AmunRa.instance.TEXTUREPREFIX + assetName);
        this.setUnlocalizedName(assetName);

    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.itemIcon = reg.registerIcon(this.getIconString());
    }

    @Override
    public CreativeTabs getCreativeTab()
    {
        return AmunRa.instance.arTab;
    }

    /**
     * Returns 0 for /terrain.png, 1 for /gui/items.png
     */
    @Override
    @SideOnly(Side.CLIENT)
    public int getSpriteNumber()
    {
        return 1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int p_77617_1_)
    {
        return this.itemIcon;
    }

    @Override
    public int getMetadata(int damage)
    {
        return this.blockMeta;
    }


    /**
     * itemstack, player, world, x, y, z, side, hitX, hitY, hitZ
     * /
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        ++y;
        BlockMetaPair blockJet = ARBlocks.blockMothershipEngineRocket;
        int blockRotation = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;


        if (player.canPlayerEdit(x, y, z, side, itemStack))
        {
            if (world.isAirBlock(x, y, z))
            {
                int meta = BlockMachineMeta.addRotationMeta(blockJet.getMetadata(), blockRotation);
                if(world.setBlock(x, y, z, blockJet.getBlock(), meta, 3)) {
                    blockJet.getBlock().onBlockPlacedBy(world, x, y, z, player, itemStack);
                    blockJet.getBlock().onPostBlockPlaced(world, x, y, z, meta);

                    --itemStack.stackSize;
                    return true;
                } else {
                    return false;
                }
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
*/
    /**
     * Called to actually place the block, after the location is determined
     * and all permission checks have been made.
     *
     * @param stack The item stack that was used to place the block. This can be changed inside the method.
     * @param player The player who is placing the block. Can be null if the block is not being placed by a player.
     * @param side The side the player (or machine) right-clicked on.
     */
    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
    {
       int blockRotation = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
       metadata = BlockMachineMeta.addRotationMeta(blockMeta, blockRotation);

       if (!world.setBlock(x, y, z, field_150939_a, metadata, 3))
       {
           return false;
       }

       if (world.getBlock(x, y, z) == field_150939_a)
       {
           field_150939_a.onBlockPlacedBy(world, x, y, z, player, stack);
           field_150939_a.onPostBlockPlaced(world, x, y, z, metadata);
       }

       return true;
    }

}
