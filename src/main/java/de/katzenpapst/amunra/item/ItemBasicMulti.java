package de.katzenpapst.amunra.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import micdoodle8.mods.galacticraft.core.items.ItemBlockDesc;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemBasicMulti extends Item implements ItemBlockDesc.IBlockShiftDesc {
    // public static final String[] names = { "solar_module_0", "solar_module_1", "rawSilicon", "ingotCopper", "ingotTin", "ingotAluminum", "compressedCopper", "compressedTin", "compressedAluminum", "compressedSteel", "compressedBronze", "compressedIron", "waferSolar", "waferBasic", "waferAdvanced", "dehydratedApple", "dehydratedCarrot", "dehydratedMelon", "dehydratedPotato", "frequencyModule" };

    // protected IIcon[] icons = new IIcon[ItemBasic.names.length];
    protected ArrayList<SubItem> subItems = null;

    protected HashMap<String, Integer> nameDamageMapping = null;

    public ItemBasicMulti(String name)
    {
        super();
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setUnlocalizedName(name);
        subItems = new ArrayList<SubItem>();
        nameDamageMapping = new HashMap<String, Integer>();
    }

    public ItemStack getItemStack(String name, int count) {

        return getItemStack(getDamageByName(name), count);
    }

    public ItemStack getItemStack(int damage, int count) {
        // ensure it exists
        if(subItems.get(damage) == null) {
            throw new IllegalArgumentException("SubItem with damage "+damage+" does not exist in "+this.getUnlocalizedName());
        }

        return new ItemStack(this, count, damage);
    }



    public ItemDamagePair addSubItem(int damage, SubItem item) {
        if(damage >= subItems.size()) {
            subItems.ensureCapacity(damage);
            while(damage >= subItems.size()) {
                subItems.add(null);
            }
        }
        if(subItems.get(damage) != null) {
            throw new IllegalArgumentException("SubItem with damage "+damage+" already exists in "+this.getUnlocalizedName());
        }
        String itemName = item.getUnlocalizedName();
        if(nameDamageMapping.get(itemName) != null) {
            throw new IllegalArgumentException("SubItem with name "+itemName+" already exists in "+this.getUnlocalizedName());
        }
        nameDamageMapping.put(itemName, damage);
        subItems.add(damage, item);
        return new ItemDamagePair(this, damage);
    }

    public int getDamageByName(String name) {
        if(!nameDamageMapping.containsKey(name)) {
            return -1;
        }
        return nameDamageMapping.get(name).intValue();
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
        for(SubItem item: subItems) {
            if(item == null) continue;

            item.registerIcons(iconRegister);
            //item.icon = iconRegister.registerIcon(item.getIconString());
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack)
    {
        return this.getUnlocalizedName()+"."+getSubItem(itemStack.getItemDamage()).getUnlocalizedName();
    }

    @Override
    public IIcon getIconFromDamage(int damage)
    {
        return subItems.get(damage).getIconFromDamage(0);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int i=0; i<subItems.size(); i++)
        {
            if(subItems.get(i) == null)  continue;
            par3List.add(new ItemStack(par1, 1, i));
        }
    }

    @Override
    public int getMetadata(int par1)
    {
        return par1;
    }

    public SubItem getSubItem(int damage) {
        if(damage >= subItems.size() || subItems.get(damage) == null) {
            throw new IllegalArgumentException("Requested invalid SubItem "+damage+" from "+this.getUnlocalizedName());
        }
        return subItems.get(damage);
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
        String info = getSubItem(par1ItemStack.getItemDamage()).getItemInfo();
        if(info != null) {
            info = GCCoreUtil.translate(info);
            par3List.addAll(FMLClientHandler.instance().getClient().fontRenderer.listFormattedStringToWidth(info, 150));
        }
    }


    @Override
    public ItemStack onEaten(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        return getSubItem(par1ItemStack.getItemDamage()).onEaten(par1ItemStack, par2World, par3EntityPlayer);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack)
    {
        return getSubItem(par1ItemStack.getItemDamage()).getMaxItemUseDuration(par1ItemStack);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack)
    {
        return getSubItem(par1ItemStack.getItemDamage()).getItemUseAction(par1ItemStack);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        return getSubItem(par1ItemStack.getItemDamage()).onItemRightClick(par1ItemStack, par2World, par3EntityPlayer);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack itemStack, EntityPlayer player, Entity entity)
    {
        return getSubItem(itemStack.getItemDamage()).onLeftClickEntity(itemStack, player, entity);
    }

    public int getFuelDuration(int meta) {
        return getSubItem(meta).getFuelDuration();
    }

    @Override
    public String getShiftDescription(int meta) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean showDescription(int meta) {
        // TODO Auto-generated method stub
        return false;
    }
}
