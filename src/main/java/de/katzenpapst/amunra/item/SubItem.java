package de.katzenpapst.amunra.item;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SubItem  extends Item {

    protected String itemInfo = null;
    protected String fuckYouName = null;// fuck you, private

    public SubItem(String name, String assetName)
    {
        super();
        fuckYouName = name;
        this.setUnlocalizedName(name);
        this.setTextureName(AmunRa.TEXTUREPREFIX + assetName);
    }

    public SubItem(String name, String assetName, String info)
    {
        this(name, assetName);
        itemInfo = info;
    }

    @Override
    public String getUnlocalizedName()
    {
        return fuckYouName;
    }

    public String getItemInfo()
    {
        return itemInfo;
    }


    @Override
    public CreativeTabs getCreativeTab()
    {
        return AmunRa.instance.arTab;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack par1ItemStack)
    {
        return ClientProxyCore.galacticraftItem;
    }

    public int getFuelDuration() {
        return 0;
    }
}
