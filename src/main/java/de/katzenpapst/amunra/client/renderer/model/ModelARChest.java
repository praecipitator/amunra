package de.katzenpapst.amunra.client.renderer.model;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import de.katzenpapst.amunra.block.BlockARChest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelLargeChest;

public class ModelARChest {

    private final ModelChest chestModel = new ModelChest();
    private final ModelLargeChest largeChestModel = new ModelLargeChest();

    public ModelARChest() {
    }

    public void render(BlockARChest chestBlock, boolean isDouble, double x, double y, double z)
    {
        ModelChest chestModel = null;
        //ModelLargeChest largeModel = null;

        if (!isDouble)
        {
            chestModel = this.chestModel;
            Minecraft.getMinecraft().renderEngine.bindTexture(((BlockARChest)chestBlock).getSmallTexture());
        }
        else
        {
            chestModel = this.largeChestModel;
            Minecraft.getMinecraft().renderEngine.bindTexture(((BlockARChest)chestBlock).getLargeTexture());
        }

        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glTranslatef((float) x, (float) y + 1.0F, (float) z + 1.0F);
        GL11.glScalef(1.0F, -1.0F, -1.0F);
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        GL11.glRotated(-90, 0, 1, 0);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);


        chestModel.renderAll();


        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

}
