package de.katzenpapst.amunra.client.renderer.model;

import org.lwjgl.opengl.GL11;

import de.katzenpapst.amunra.AmunRa;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

public class ModelShuttleDock {

    private final ResourceLocation texture = new ResourceLocation(AmunRa.instance.ASSETPREFIX, "textures/model/dock.png");

    // not really a model, but meh
    public ModelShuttleDock() {
        // this.texture = texture;
    }

    public void render(Tessellator tessellator, boolean renderConnector) {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
        // front
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -0.8F);
        // 0,0 -> 0,25/0,5
        tessellator.addVertexWithUV(+0.5, 0, -0.5, 0,    0.5);
        tessellator.addVertexWithUV(-0.5, 0, -0.5, 0.25, 0.5);
        tessellator.addVertexWithUV(-0.5, 2, -0.5, 0.25, 0);
        tessellator.addVertexWithUV(+0.5, 2, -0.5, 0,    0);
        tessellator.draw();

        //back 0,5/0 -> 0,75/0,5
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 0.8F);
        tessellator.addVertexWithUV(-0.5, 0, 0.5, 0.5,  0);
        tessellator.addVertexWithUV(+0.5, 0, 0.5, 0.75, 0);
        tessellator.addVertexWithUV(+0.5, 2, 0.5, 0.75, 0.5);
        tessellator.addVertexWithUV(-0.5, 2, 0.5, 0.5,  0.5);
        tessellator.draw();

        // right side // 0,25/0 -> 0,5/0,5
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.8F, 0.0F, 0.0F);
        tessellator.addVertexWithUV(-0.5, 0, -0.5, 0.25, 0);
        tessellator.addVertexWithUV(-0.5, 0, +0.5, 0.5,  0);
        tessellator.addVertexWithUV(-0.5, 2, +0.5, 0.5,  0.5);
        tessellator.addVertexWithUV(-0.5, 2, -0.5, 0.25, 0.5);
        tessellator.draw();

        // left side
        tessellator.startDrawingQuads();
        tessellator.setNormal(-0.8F, 0.0F, 0.0F);
        tessellator.addVertexWithUV(0.5, 0, +0.5, 0.25, 0);
        tessellator.addVertexWithUV(0.5, 0, -0.5, 0.5,  0);
        tessellator.addVertexWithUV(0.5, 2, -0.5, 0.5,  0.5);
        tessellator.addVertexWithUV(0.5, 2, +0.5, 0.25, 0.5);
        tessellator.draw();

        // top 0/0,5 -> 0,25/0,75
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.8F, 0.0F);
        tessellator.addVertexWithUV(-0.5, 2, +0.5, 0,    0.75);
        tessellator.addVertexWithUV(+0.5, 2, +0.5, 0.25, 0.75);
        tessellator.addVertexWithUV(+0.5, 2, -0.5, 0.25, 0.5);
        tessellator.addVertexWithUV(-0.5, 2, -0.5, 0,    0.5);
        tessellator.draw();

        // bottom
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -0.8F, 0.0F);
        tessellator.addVertexWithUV(-0.5, 0, -0.5, 0,    0.5);
        tessellator.addVertexWithUV(+0.5, 0, -0.5, 0.25, 0.5);
        tessellator.addVertexWithUV(+0.5, 0, +0.5, 0.25, 0.75);
        tessellator.addVertexWithUV(-0.5, 0, +0.5, 0,    0.75);
        tessellator.draw();

        if(renderConnector) {
            renderConnector(tessellator);
        }
    }

    protected void renderConnector(Tessellator tessellator) {
        // now try to draw the thing
           float scale = 0.9F;
           GL11.glTranslatef(0.0F, (1.0F-scale), 1.0F);
           GL11.glScalef(0.8F, scale, 1.0F);

           // right side // 0,75/0 -> 1/0.5
           tessellator.startDrawingQuads();
           tessellator.setNormal(0.8F, 0.0F, 0.0F);
           tessellator.addVertexWithUV(-0.5, 0, -0.5, 1,    0);
           tessellator.addVertexWithUV(-0.5, 0, +0.5, 0.75, 0);
           tessellator.addVertexWithUV(-0.5, 2, +0.5, 0.75, 0.5);
           tessellator.addVertexWithUV(-0.5, 2, -0.5, 1, 0.5);
           tessellator.draw();

           // left side
           tessellator.startDrawingQuads();
           tessellator.setNormal(-0.8F, 0.0F, 0.0F);
           tessellator.addVertexWithUV(0.5, 0, +0.5, 0.75, 0);
           tessellator.addVertexWithUV(0.5, 0, -0.5, 1,    0);
           tessellator.addVertexWithUV(0.5, 2, -0.5, 1,    0.5);
           tessellator.addVertexWithUV(0.5, 2, +0.5, 0.75, 0.5);
           tessellator.draw();

           // top 0,25/0,5 -> 0,5/0,75
           tessellator.startDrawingQuads();
           tessellator.setNormal(0.0F, 0.8F, 0.0F);
           tessellator.addVertexWithUV(-0.5, 2, -0.5, 0.5,  0.5);
           tessellator.addVertexWithUV(-0.5, 2, +0.5, 0.25, 0.5);
           tessellator.addVertexWithUV(+0.5, 2, +0.5, 0.25, 0.75);
           tessellator.addVertexWithUV(+0.5, 2, -0.5, 0.5,  0.75);
           tessellator.draw();

           // bottom
           tessellator.startDrawingQuads();
           tessellator.setNormal(0.0F, -0.8F, 0.0F);
           tessellator.addVertexWithUV(-0.5, 0, -0.5, 0.5, 0.75);
           tessellator.addVertexWithUV(+0.5, 0, -0.5, 0.5, 0.5);
           tessellator.addVertexWithUV(+0.5, 0, +0.5, 0.25,  0.5);
           tessellator.addVertexWithUV(-0.5, 0, +0.5, 0.25,  0.75);
           tessellator.draw();

           // back 0,75/0,5 -> 1/1
           tessellator.startDrawingQuads();
           tessellator.setNormal(0.0F, 0.0F, 0.8F);
           tessellator.addVertexWithUV(-0.5, 0, 0.5, 0.75, 0.5);
           tessellator.addVertexWithUV(+0.5, 0, 0.5, 1,    0.5);
           tessellator.addVertexWithUV(+0.5, 2, 0.5, 1,    1);
           tessellator.addVertexWithUV(-0.5, 2, 0.5, 0.75, 1);
           tessellator.draw();
       }

}
