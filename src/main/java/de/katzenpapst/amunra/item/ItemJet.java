package de.katzenpapst.amunra.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.block.BlockMachineMeta;
import de.katzenpapst.amunra.block.IMetaBlock;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
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

    /**
     * Returns the unlocalized name of this item.
     */
    @Override
    public String getUnlocalizedName()
    {
        return this.field_150939_a.getUnlocalizedName();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack par1ItemStack)
    {
        // colors the name
        return ClientProxyCore.galacticraftItem;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack)
    {
        String subBlockName = ((IMetaBlock) field_150939_a).getUnlocalizedSubBlockName(itemstack.getItemDamage());
        return "tile." + subBlockName;
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

        /**
         * 0 -> +Y
         * 1 -> -Y
         * 2 -> -Z
         * 3 -> +Z
         * 4 -> -X
         * 5 -> +X
         *
         *
         ** value | motion direction |
     * ------+----------------- +
     *   0   |        +Z        |
     *   1   |        -X        |
     *   2   |        -Z        |
     *   3   |        +X        |
     *
         */

        int blockRotation = 0;

        switch(side) {
        case 0:
        case 1:
            blockRotation = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
            break;
        case 2:
            blockRotation = 0;
            break;
        case 3:
            blockRotation = 2;
            break;
        case 4:
            blockRotation = 3;
            break;
        case 5:
            blockRotation = 1;
            break;
        }

        metadata = ARBlocks.metaBlockMothershipEngineJet.addRotationMeta(blockMeta, blockRotation);

        // metadata = BlockMachineMeta.addRotationMeta(blockMeta, blockRotation);

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
