package de.katzenpapst.amunra.item;

import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.block.BlockBasicMulti;
import de.katzenpapst.amunra.block.SubBlock;
import micdoodle8.mods.galacticraft.core.items.ItemBlockDesc;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;

/**
 * Item for multiblocks
 * @author Alex
 *
 */
public class ItemBlockMulti extends ItemBlockDesc {

	public ItemBlockMulti(Block block) {
		super(block); // it ends up in field_150939_a
        this.setMaxDamage(0);	// ?
        this.setHasSubtypes(true);
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
		SubBlock sb = ((BlockBasicMulti) field_150939_a).getSubBlock(itemstack.getItemDamage());
        return this.field_150939_a.getUnlocalizedName() + "." + sb.getUnlocalizedName();
    }
	
	@Override
    public String getUnlocalizedName()
    {
        return this.field_150939_a.getUnlocalizedName() + ".0";
    }
	
	@Override
    public int getMetadata(int meta)
    {
        return meta;
    }
}
