package de.katzenpapst.amunra.item;

import java.util.List;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.block.BlockMachineMeta;
import de.katzenpapst.amunra.block.machine.mothershipEngine.MothershipEngineJetBase;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemJet extends ItemBlockMulti {

    protected IIcon[] icons;


    public ItemJet(BlockMachineMeta blockMothershipEngineRocket, String assetName) {
        super(blockMothershipEngineRocket);
        //blockMeta = blockMothershipEngineRocket.getMetadata();
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setMaxStackSize(1); // why?
        // this.setTextureName(AmunRa.instance.TEXTUREPREFIX + assetName);
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
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        int length = ((BlockMachineMeta)field_150939_a).getNumPossibleSubBlocks();
        icons = new IIcon[length];
        for(int i=0;i<length;i++) {
            MothershipEngineJetBase sb = (MothershipEngineJetBase) ((BlockMachineMeta)field_150939_a).getSubBlock(i);
            if(sb != null) {
                icons[i] = reg.registerIcon(sb.getItemIconName());
            }
        }
        // this.itemIcon = reg.registerIcon(this.getIconString());
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
    public IIcon getIconFromDamage(int dmg)
    {
        return icons[dmg];
        // return ((BlockMachineMeta)field_150939_a).getSubBlock(dmg).getIcon(1, 0);
    }

    @Override
    public int getMetadata(int damage)
    {
        return damage;
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

        metadata = ARBlocks.metaBlockMothershipEngineJet.addRotationMeta(stack.getItemDamage(), blockRotation);

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

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean advanced)
    {
        if (this.field_150939_a instanceof IBlockShiftDesc && ((IBlockShiftDesc) this.field_150939_a).showDescription(stack.getItemDamage()))
        {
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
            {
                info.addAll(FMLClientHandler.instance().getClient().fontRenderer.listFormattedStringToWidth(((IBlockShiftDesc) this.field_150939_a).getShiftDescription(stack.getItemDamage()), 150));
            } else {
                info.add(GCCoreUtil.translateWithFormat("itemDesc.shift.name", GameSettings.getKeyDisplayString(FMLClientHandler.instance().getClient().gameSettings.keyBindSneak.getKeyCode())));
            }
        }
    }

}
