package de.katzenpapst.amunra.client.renderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.tile.TileEntityMothershipEngineBooster;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import team.chisel.ctmlib.Drawing;

public class RenderMothershipBooster extends TileEntitySpecialRenderer {

    private RenderBlocks blockRenderer = null;

    public RenderMothershipBooster() {
        // TODO Auto-generated constructor stub
    }

    protected void renderMSBooster(TileEntityMothershipEngineBooster entity,
            double x,
            double y,
            double z,
            float partialTickTime) {
        GL11.glPushMatrix();
        //GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glTranslatef((float) x, (float) y, (float) z);
        //GL11.glTranslatef((float) x + 0.5F, (float) y, (float) z + 0.5F);
        //GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        //GL11.glEnable(GL11.GL_ALPHA_TEST);

        // I hope this works
        final Tessellator tess = Tessellator.instance;
        Block block = entity.getBlockType();
        int meta = entity.getBlockMetadata();

        //var3.startDrawingQuads();
        TextureManager renderEngine = Minecraft.getMinecraft().renderEngine;
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        //GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        //renderEngine.bindTexture(AmunRa.instance.starAmun.getBodyIcon());

        ResourceLocation tmp = entity.texLoc;//AmunRa.instance.starAmun.getBodyIcon();

        /*renderFaceYNeg = 0
        renderFaceYPos = 1
        renderFaceZNeg = 2
        renderFaceZPos = 3
        renderFaceXNeg = 4
        renderFaceXPos = 5*/

        // HACK

        this.renderFaceYNeg(tess, entity.getBlockIconFromSide(0), entity.doRotateTopIcon());
        this.renderFaceYPos(tess, entity.getBlockIconFromSide(1), entity.doRotateTopIcon());


        this.renderFaceZNeg(tess, entity.getBlockIconFromSide(2));
        this.renderFaceZPos(tess, entity.getBlockIconFromSide(3));
        this.renderFaceXNeg(tess, entity.getBlockIconFromSide(4));
        this.renderFaceXPos(tess, entity.getBlockIconFromSide(5));
        // somehow do the stuff
        // return Minecraft.isAmbientOcclusionEnabled() && p_147784_1_.getLightValue() == 0 ? (this.partialRenderBounds ? this.renderStandardBlockWithAmbientOcclusionPartial(p_147784_1_, p_147784_2_, p_147784_3_, p_147784_4_, f, f1, f2) : this.renderStandardBlockWithAmbientOcclusion(p_147784_1_, p_147784_2_, p_147784_3_, p_147784_4_, f, f1, f2)) : this.renderStandardBlockWithColorMultiplier(p_147784_1_, p_147784_2_, p_147784_3_, p_147784_4_, f, f1, f2);


        GL11.glPopMatrix();
    }

    public void renderFaceZPos(Tessellator tessellator, ResourceLocation texture)
    {
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.8F, 0.0F, 0.0F);

        double u1 = 1;
        double u2 = 0;
        double v1 = 0;
        double v2 = 1;

        tessellator.addVertexWithUV(0, 1, 1, u1, v1);
        tessellator.addVertexWithUV(0, 0, 1, u1, v2);
        tessellator.addVertexWithUV(1, 0, 1, u2, v2);
        tessellator.addVertexWithUV(1, 1, 1, u2, v1);

        tessellator.draw();
    }

    public void renderFaceZNeg(Tessellator tessellator, ResourceLocation texture)
    {
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
        tessellator.startDrawingQuads();
        tessellator.setNormal(-0.8F, 0.0F, 0.0F);
        double u2 = 0;
        double u1 = 1;
        double v1 = 0;
        double v2 = 1;



        tessellator.addVertexWithUV(0, 1, 0, u1, v1);
        tessellator.addVertexWithUV(1, 1, 0, u2, v1);
        tessellator.addVertexWithUV(1, 0, 0, u2, v2);
        tessellator.addVertexWithUV(0, 0, 0, u1, v2);

        tessellator.draw();

    }

    public void renderFaceXNeg(Tessellator tessellator, ResourceLocation texture)
    {
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 0.8F);

        double u2 = 1;
        double u1 = 0;
        double v1 = 0;
        double v2 = 1;


        tessellator.addVertexWithUV(0, 1, 1, u1, v1);
        tessellator.addVertexWithUV(0, 1, 0, u2, v1);
        tessellator.addVertexWithUV(0, 0, 0, u2, v2);
        tessellator.addVertexWithUV(0, 0, 1, u1, v2);

        tessellator.draw();
    }

    public void renderFaceXPos(Tessellator tessellator, ResourceLocation texture)
    {
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -0.8F);

        double d3 = 0;
        double d4 = 1;
        double d5 = 0;
        double d6 = 1;

        tessellator.addVertexWithUV(1, 0, 1, d3, d6);
        tessellator.addVertexWithUV(1, 0, 0, d4, d6);
        tessellator.addVertexWithUV(1, 1, 0, d4, d5);
        tessellator.addVertexWithUV(1, 1, 1, d3, d5);

        tessellator.draw();

    }

    public void renderFaceYNeg(Tessellator tessellator, ResourceLocation texture, boolean rotate)
    {
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -0.8F, 0.0F);

        double u1 = 1;
        double u2 = 0;
        double v2 = 0;
        double v1 = 1;

        if(rotate) {

            tessellator.addVertexWithUV(0, 0, 1, 1, 1);
            tessellator.addVertexWithUV(0, 0, 0, 1, 0);
            tessellator.addVertexWithUV(1, 0, 0, 0, 0);
            tessellator.addVertexWithUV(1, 0, 1, 0, 1);
        } else {
            tessellator.addVertexWithUV(0, 0, 1, 0, 1);
            tessellator.addVertexWithUV(0, 0, 0, 1, 1);
            tessellator.addVertexWithUV(1, 0, 0, 1, 0);
            tessellator.addVertexWithUV(1, 0, 1, 0, 0);
        }


        tessellator.draw();

    }

    public void renderFaceYPos(Tessellator tessellator, ResourceLocation texture, boolean rotate)
    {
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.8F, 0.8F);
        double d3 = 1;
        double d4 = 0;
        double d5 = 1;
        double d6 = 0;
        if(rotate) {
            d3 = 1;
            d4 = 0;
            d5 = 1;
            d6 = 0;
            tessellator.addVertexWithUV(1, 1, 1, 0, 0);
            tessellator.addVertexWithUV(1, 1, 0, 0, 1);
            tessellator.addVertexWithUV(0, 1, 0, 1, 1);
            tessellator.addVertexWithUV(0, 1, 1, 1, 0);
        } else {
            tessellator.addVertexWithUV(1, 1, 1, 0, 1);//10
            tessellator.addVertexWithUV(1, 1, 0, 1, 1);//00
            tessellator.addVertexWithUV(0, 1, 0, 1, 0);//01
            tessellator.addVertexWithUV(0, 1, 1, 0, 0);//11
        }




        tessellator.draw();
    }



    @Override
    public void renderTileEntityAt(
            TileEntity entity,
            double x,
            double y,
            double z,
            float partialTickTime) {
        renderMSBooster((TileEntityMothershipEngineBooster) entity, x, y, z, partialTickTime);
    }

    @Override
    public void func_147496_a(World world) {
        this.blockRenderer = new RenderBlocks(world);
    }

}
