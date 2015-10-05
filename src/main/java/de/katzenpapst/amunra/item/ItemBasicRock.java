package de.katzenpapst.amunra.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import micdoodle8.mods.galacticraft.core.items.ItemBlockDesc;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;

public class ItemBasicRock extends ItemBlockDesc {

	public ItemBasicRock(Block block) {
		super(block);
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
        String name = "";

        switch (itemstack.getItemDamage())
        {
        case 0:
            name = "basalt";
            break;
        case 1:
            name = "olivine";
            break;
        case 2:
            name = "quarzSandstone";
            break;
        default:
            name = "null";
        }

        return this.field_150939_a.getUnlocalizedName() + "." + name;
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
