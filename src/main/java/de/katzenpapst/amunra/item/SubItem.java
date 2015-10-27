package de.katzenpapst.amunra.item;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.items.GCItems;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

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
	
	public String getItemInfo() {
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
    

    /*
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
        if (par1ItemStack != null && this == GCItems.heavyPlatingTier1)
        {
            par3List.add(GCCoreUtil.translate("item.tier1.desc"));
        }
    }
    */
}
