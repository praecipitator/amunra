package de.katzenpapst.amunra.item;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import micdoodle8.mods.galacticraft.api.item.IItemThermal;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemThermalSuit extends Item implements IItemThermal {

    protected int thermalStrength;

    protected String[] iconStrings  = new String[4];
    protected IIcon[]  icons        = new IIcon[4];
    protected final String[] names  = {"helmet", "chest", "legs", "boots"};

    public ItemThermalSuit(String name, int thermalStrength, String helmetIcon, String chestIcon, String legsIcon, String bootsIcon) {
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setUnlocalizedName(name);

        this.thermalStrength = thermalStrength;
        iconStrings[0] = AmunRa.TEXTUREPREFIX + helmetIcon;
        iconStrings[1] = AmunRa.TEXTUREPREFIX + chestIcon;
        iconStrings[2] = AmunRa.TEXTUREPREFIX + legsIcon;
        iconStrings[3] = AmunRa.TEXTUREPREFIX + bootsIcon;
    }

    public ItemDamagePair getHelmet() {
        return new ItemDamagePair(this, 0);
    }

    public ItemDamagePair getChest() {
        return new ItemDamagePair(this, 1);
    }

    public ItemDamagePair getLegts() {
        return new ItemDamagePair(this, 2);
    }

    public ItemDamagePair getBoots() {
        return new ItemDamagePair(this, 3);
    }

    @Override
    public int getThermalStrength() {
        return thermalStrength;
    }

    @Override
    public boolean isValidForSlot(ItemStack stack, int armorSlot) {
        return armorSlot == stack.getItemDamage();
    }

    public void register() {
        GameRegistry.registerItem(this, this.getUnlocalizedName(), AmunRa.MODID);
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

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        for(int i=0;i<4;i++) {
            icons[i] = iconRegister.registerIcon(iconStrings[i]);
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack)
    {
        return this.getUnlocalizedName()+"."+names[itemStack.getItemDamage()];
    }

    @Override
    public IIcon getIconFromDamage(int damage)
    {
        return icons[damage];
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int i=0; i<4; i++)
        {
            par3List.add(new ItemStack(par1, 1, i));
        }
    }

    @Override
    public int getMetadata(int par1)
    {
        return par1;
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
        par3List.add(GCCoreUtil.translateWithFormat("item.thermalSuit.thermalLevel.name", thermalStrength));
        /*String info = getSubItem(par1ItemStack.getItemDamage()).getItemInfo();
        if(info != null) {
            par3List.add(GCCoreUtil.translate(info));
        }*/
    }
}
