package de.katzenpapst.amunra.client.renderer;

import org.lwjgl.opengl.GL11;

import de.katzenpapst.amunra.client.gui.GuiHelper;
import de.katzenpapst.amunra.tile.TileEntityBlockScale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public class RenderBlockScale extends TileEntitySpecialRenderer {

    public RenderBlockScale() {}

    @Override
    public void renderTileEntityAt(
            TileEntity entity,
            double x,
            double y,
            double z,
            float partialTickTime) {
        if(!(entity instanceof TileEntityBlockScale)) {
            return;
        }
        TileEntityBlockScale scaleEntity = (TileEntityBlockScale)entity;
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);
        int rotation = scaleEntity.getRotationMeta();
        switch(rotation) {
        case 0:
            GL11.glRotatef(180, 0, 1, 0);
            GL11.glTranslatef((float) 0.0F, (float) 1.0F, (float) -1.01F);
            GL11.glRotatef(180, 0, 0, 1);
            break;
        case 1:
            GL11.glTranslatef((float) 1.0F, (float) 1.0F, (float) -0.01F);
            GL11.glRotatef(180, 0, 0, 1);
            break;
        case 2:
            GL11.glRotatef(-90, 0, 1, 0);
            GL11.glTranslatef((float) 1.0F, (float) 1.0F, (float) -1.01F);
            GL11.glRotatef(180, 0, 0, 1);
            break;
        case 3:
            GL11.glRotatef(90, 0, 1, 0);
            GL11.glTranslatef((float) -0.0F, (float) 1.0F, (float) -0.01F);
            GL11.glRotatef(180, 0, 0, 1);
            break;
        }

        // now try to draw some text onto the block
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

        float yOffset = (50-fr.FONT_HEIGHT)/2.0F + 7.0F;


        float mass = scaleEntity.getCurrentMass();
        String toDisplay = GuiHelper.formatKilogram(mass);
        int width = fr.getStringWidth(toDisplay); //29 pixels
        GL11.glScalef(0.020F, 0.020F, 0.020F);
        // I think now, translating by 1 means translating by 1 pixel
        // I also think the total width is 1/0,02 = 50
        GL11.glTranslatef((float)(50-width)/2.0F, yOffset, 0);
        fr.drawString(toDisplay, 0, 0, 0);

        GL11.glPopMatrix();
    }

}
