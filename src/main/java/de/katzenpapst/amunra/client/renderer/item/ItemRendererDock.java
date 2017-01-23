package de.katzenpapst.amunra.client.renderer.item;

import org.lwjgl.opengl.GL11;

import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.client.renderer.model.ModelShuttleDock;
import micdoodle8.mods.galacticraft.planets.mars.blocks.BlockMachineMars;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;

public class ItemRendererDock implements IItemRenderer {

    private ModelShuttleDock model;

    public ItemRendererDock() {
        model =  new ModelShuttleDock();
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {

        // I think this prevents this thing from doing other items
        if (item.getItemDamage() == ARBlocks.blockShuttleDock.getMetadata())
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

    protected void renderDock(ItemRenderType type, RenderBlocks render, ItemStack item, float translateX, float translateY, float translateZ)
    {
        switch(type) {
        case ENTITY:
            break;
        case EQUIPPED:
            GL11.glTranslated(0.5, -0.0, 0.5);
            //GL11.glScaled(0.65, 0.65, 0.65);
            GL11.glRotated(90, 0, 1, 0);
            break;
        case EQUIPPED_FIRST_PERSON:
            GL11.glTranslated(0.0, -0.7, 0.0);
            //GL11.glScaled(0.65, 0.65, 0.65);
            GL11.glRotated(90, 0, 1, 0);
            break;
        case INVENTORY:
            GL11.glTranslated(0.0, -0.7, 0.0);
            GL11.glScaled(0.65, 0.65, 0.65);
            GL11.glRotated(180, 0, 1, 0);
            break;
        default:
            break;
        }
        GL11.glPushMatrix();
        model.render(Tessellator.instance, false);
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
                this.renderDock(type, (RenderBlocks) data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            default:
                break;
            }
        }

    }

}
