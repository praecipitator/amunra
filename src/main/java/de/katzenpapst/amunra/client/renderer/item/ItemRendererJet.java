package de.katzenpapst.amunra.client.renderer.item;

import org.lwjgl.opengl.GL11;

import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.item.ARItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.model.IModelCustom;

public class ItemRendererJet implements IItemRenderer {

    protected IModelCustom[]      models;
    protected ResourceLocation[]  textures;

    public ItemRendererJet(IModelCustom[] models, ResourceLocation[] textures) {
        // TODO find a better idea
        this.models = models;
        this.textures = textures;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {

        if(item.getItem() == ARItems.jetItemMeta && item.getItemDamage() < models.length && item.getItemDamage() < textures.length)
        {
            switch (type)
            {
            case ENTITY:
            case EQUIPPED:
            case EQUIPPED_FIRST_PERSON:
            case INVENTORY:
                return true;
            default:
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    protected void renderJet(ItemRenderType type, RenderBlocks render, ItemStack item, float translateX, float translateY, float translateZ)
    {
        // TODO get a better idea how to do this
        ResourceLocation texture = textures[item.getItemDamage()];
        IModelCustom model = models[item.getItemDamage()];

        switch(type) {
        case ENTITY:
            GL11.glScaled(0.53, 0.53, 0.53);
            GL11.glTranslated(0.0, 1.0, 1.0);
            break;
        case EQUIPPED:
            GL11.glScaled(0.53, 0.53, 0.53);
            GL11.glTranslated(2.0, 0.8, 1.5);
            GL11.glRotated(90, 0, 1, 0);
            break;
        case EQUIPPED_FIRST_PERSON:
            GL11.glScaled(0.53, 0.53, 0.53);
            GL11.glTranslated(1.9, 1.0, 1.0);
            GL11.glRotated(90, 0, 1, 0);
            break;
        case INVENTORY:
            GL11.glTranslated(0.5, 0.35, 0.0);
            GL11.glScaled(0.53, 0.53, 0.53);
            GL11.glRotated(180, 0, 1, 0);
            break;
        default:
            break;
        }
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
        model.renderAll();
        GL11.glPopMatrix();
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        if(this.handleRenderType(item, type)) {
            switch (type)
            {
            case EQUIPPED:
            case EQUIPPED_FIRST_PERSON:
            case INVENTORY:
            case ENTITY:
                this.renderJet(type, (RenderBlocks) data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            default:
                break;
            }
        }
    }

}
