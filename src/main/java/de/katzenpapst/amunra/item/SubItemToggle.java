package de.katzenpapst.amunra.item;

import java.util.List;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class SubItemToggle extends SubItem {

    protected String inactiveAssetName;

    protected boolean defaultState = false;

    @SideOnly(Side.CLIENT)
    protected IIcon inactItemIcon;

    public SubItemToggle(String name, String assetName, String inactiveAssetName) {
        super(name, assetName);
        this.inactiveAssetName = inactiveAssetName;
    }

    public SubItemToggle(String name, String assetName, String inactiveAssetName, String info) {
        super(name, assetName, info);
        this.inactiveAssetName = inactiveAssetName;
    }

    public SubItemToggle(String name, String assetName, String inactiveAssetName, String info, boolean defaultState) {
        super(name, assetName, info);
        this.inactiveAssetName = inactiveAssetName;
        this.defaultState = defaultState;
    }

    public boolean getState(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if(nbt == null || !nbt.hasKey("toggleState")) {
            return defaultState;
        }

        return nbt.getBoolean("toggleState");
    }

    public void setState(ItemStack stack, boolean state) {
        NBTTagCompound nbt = stack.getTagCompound();
        if(nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }

        nbt.setBoolean("toggleState", state);
    }

    public void toggleState(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if(nbt == null || !nbt.hasKey("toggleState")) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
            nbt.setBoolean("toggleState", !defaultState);
        } else {
            nbt.setBoolean("toggleState", !nbt.getBoolean("toggleState"));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister registerer)
    {
        super.registerIcons(registerer);
        inactItemIcon = registerer.registerIcon(AmunRa.TEXTUREPREFIX + inactiveAssetName);
    }

    /**
     * Returns the icon index of the stack given as argument.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconIndex(ItemStack stack)
    {
        if(this.getState(stack)) {
            return this.itemIcon;
        }
        return this.inactItemIcon;
    }

    /**
     * Return the correct icon for rendering based on the supplied ItemStack and render pass.
     *
     * Defers to {@link #getIconFromDamageForRenderPass(int, int)}
     * @param stack to render for
     * @param pass the multi-render pass
     * @return the icon
     */
    @Override
    public IIcon getIcon(ItemStack stack, int pass)
    {
        if(this.getState(stack)) {
            return this.itemIcon;
        }
        return this.inactItemIcon;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer)
    {
        this.toggleState(itemStack);
        return itemStack;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4)
    {
        super.addInformation(itemStack, entityPlayer, list, par4);

        if(this.getState(itemStack)) {
            //EnumChatFormatting.GREEN
            list.add(EnumChatFormatting.GREEN + StatCollector.translateToLocal("gui.status.active.name"));
        } else {
            list.add(EnumChatFormatting.RED + StatCollector.translateToLocal("gui.status.disabled.name"));
        }
    }
}
