package de.katzenpapst.amunra.entity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

public class RenderLaserArrow  extends Render {

    private final IModelCustom meteorChunkModel = AdvancedModelLoader.loadModel(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "models/meteorChunk.obj"));

    public RenderLaserArrow()
    {
        this.shadowSize = 0.1F;
    }

    protected ResourceLocation func_110779_a(EntityBaseLaserArrow par1EntityArrow)
    {
        return par1EntityArrow.getTexture();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
        return this.func_110779_a((EntityBaseLaserArrow) par1Entity);
    }



    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void func_76986_a(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    @Override
	public void doRender(Entity entity, double x, double y, double z, float p_76986_8_, float p_76986_9_)
    {
        this.bindEntityTexture(entity);

        /*final float var24 = entity.rotationPitch;
        final float var24b = entity.rotationYaw;
        GL11.glTranslatef((float) x, (float) y, (float) z);
        GL11.glScalef(0.3F, 0.3F, 0.3F);
        GL11.glRotatef(var24b, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(var24, 0.0F, 0.0F, 1.0F);*/



        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glRotatef(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * p_76986_9_ - 90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * p_76986_9_, 0.0F, 0.0F, 1.0F);

        //GL11.glRotatef((entity.rotationYaw) - 90.0F, 0.0F, 1.0F, 0.0F);
        //GL11.glRotatef((entity.rotationPitch) , 0.0F, 0.0F, 1.0F);
        Tessellator tessellator = Tessellator.instance;

        byte b0 = 0;
        float f2 = 0.0F;
        float f3 = 0.5F;
        float f4 = (0 + b0 * 10) / 32.0F;
        float f5 = (5 + b0 * 10) / 32.0F;
        float f6 = 0.0F;
        float f7 = 0.15625F;
        float f8 = (5 + b0 * 10) / 32.0F;
        float f9 = (10 + b0 * 10) / 32.0F;
        float f10 = 0.05625F;
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        //RenderHelper.disableStandardItemLighting();

        // now try that light stuff
        GL11.glDisable(GL11.GL_LIGHTING);
        /*

        float f11 = 0;//p_76986_1_.arrowShake - p_76986_9_;

        if (f11 > 0.0F)
        {
            float f12 = -MathHelper.sin(f11 * 3.0F) * f11;
            GL11.glRotatef(f12, 0.0F, 0.0F, 1.0F);
        }
        */

        char c0 = 61680;
        int j = c0 % 65536;
        int k = c0 / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0F, k / 1.0F);
        //Tessellator tessellator = Tessellator.instance;
        //GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(f10, f10, f10);
        GL11.glTranslatef(-4.0F, 0.0F, 0.0F);
        GL11.glNormal3f(f10, 0.0F, 0.0F);
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(-7.0D, -2.0D, -2.0D, f6, f8);
        tessellator.addVertexWithUV(-7.0D, -2.0D, 2.0D, f7, f8);
        tessellator.addVertexWithUV(-7.0D, 2.0D, 2.0D, f7, f9);
        tessellator.addVertexWithUV(-7.0D, 2.0D, -2.0D, f6, f9);
        tessellator.draw();
        GL11.glNormal3f(-f10, 0.0F, 0.0F);
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(-7.0D, 2.0D, -2.0D, f6, f8);
        tessellator.addVertexWithUV(-7.0D, 2.0D, 2.0D, f7, f8);
        tessellator.addVertexWithUV(-7.0D, -2.0D, 2.0D, f7, f9);
        tessellator.addVertexWithUV(-7.0D, -2.0D, -2.0D, f6, f9);
        tessellator.draw();

        for (int i = 0; i < 4; ++i)
        {
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glNormal3f(0.0F, 0.0F, f10);
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(-8.0D, -2.0D, 0.0D, f2, f4);
            tessellator.addVertexWithUV(8.0D, -2.0D, 0.0D, f3, f4);
            tessellator.addVertexWithUV(8.0D, 2.0D, 0.0D, f3, f5);
            tessellator.addVertexWithUV(-8.0D, 2.0D, 0.0D, f2, f5);
            tessellator.draw();
        }
        //?

        GL11.glEnable(GL11.GL_LIGHTING);
        //RenderHelper.enableStandardItemLighting();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }


}
