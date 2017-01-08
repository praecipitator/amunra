package de.katzenpapst.amunra.client.renderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.tile.TileEntityMothershipEngineBooster;
import de.katzenpapst.amunra.tile.TileEntityMothershipEngineJet;
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

    private final ResourceLocation texture = new ResourceLocation(AmunRa.instance.ASSETPREFIX, "textures/blocks/jet-base.png");

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

        ResourceLocation tmp = entity.topFallback;//AmunRa.instance.starAmun.getBodyIcon();

        /*renderFaceYNeg = 0
        renderFaceYPos = 1
        renderFaceZNeg = 2
        renderFaceZPos = 3
        renderFaceXNeg = 4
        renderFaceXPos = 5*/

        // HACK
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);

        //
        TileEntityMothershipEngineJet masterTile = entity.getMasterTile();
        if(masterTile == null) {
            this.renderFaceYNeg(tess, 0, 0.5, 0.25, 0.75, false);
            this.renderFaceYPos(tess, 0, 0.5, 0.25, 0.75, false);


            this.renderFaceZNeg(tess, 0.25, 0.75, 0.5, 1.0);
            this.renderFaceZPos(tess, 0.25, 0.75, 0.5, 1.0);
            this.renderFaceXNeg(tess, 0.25, 0.75, 0.5, 1.0);
            this.renderFaceXPos(tess, 0.25, 0.75, 0.5, 1.0);
        } else {
            int nrInMultiblock = 0;
            boolean isFirst = false;
            boolean isLast = false;
            boolean doRotate = entity.getMasterZ() == entity.zCoord;

            if(entity.getMasterX() == entity.xCoord) {
                // we are on the same x
                nrInMultiblock = entity.getMasterZ()-entity.zCoord;

            } else {
                // same z
                nrInMultiblock = entity.getMasterX()-entity.xCoord;
            }
            if(nrInMultiblock == 1 || -nrInMultiblock == masterTile.getNumBoosters()) {
                isFirst = true;
            }
            if(nrInMultiblock == -1 || nrInMultiblock == masterTile.getNumBoosters()) {
                isLast = true;
            }
            double usageOffset = 0;
            if(masterTile.isInUse()) {
                usageOffset = 0.25;
            }
            if(isFirst && isLast) {

                this.renderFaceYNeg(tess, 0, 0.5, 0.25, 0.75, false);
                this.renderFaceYPos(tess, 0, 0.5, 0.25, 0.75, false);

                if(doRotate) {
                    // side
                    this.renderFaceZNeg(tess, 0, 0+usageOffset, 0.25, 0.25+usageOffset);
                    this.renderFaceZPos(tess, 0, 0+usageOffset, 0.25, 0.25+usageOffset);

                    // front
                    this.renderFaceXNeg(tess, 0, 0.75, 0.25, 1);
                    this.renderFaceXPos(tess, 0, 0.75, 0.25, 1);

                } else {
                    this.renderFaceZNeg(tess, 0, 0.75, 0.25, 1);
                    this.renderFaceZPos(tess, 0, 0.75, 0.25, 1);

                    this.renderFaceXNeg(tess, 0, 0+usageOffset, 0.25, 0.25+usageOffset);
                    this.renderFaceXPos(tess, 0, 0+usageOffset, 0.25, 0.25+usageOffset);
                }



            } else {
                if(isFirst) {
                    this.renderFaceYNeg(tess, 0.25, 0.5, 0.5, 0.75, doRotate);
                    this.renderFaceYPos(tess, 0.25, 0.5, 0.5, 0.75, doRotate);

                    if(doRotate) {
                        this.renderFaceZNeg(tess, 0.25, 0+usageOffset, 0.5, 0.25+usageOffset);
                        this.renderFaceZPos(tess, 0.25, 0+usageOffset, 0.5, 0.25+usageOffset);

                        this.renderFaceXNeg(tess, 0, 0.75, 0.25, 1);
                        this.renderFaceXPos(tess, 0, 0.75, 0.25, 1);
                    } else {
                        this.renderFaceXNeg(tess, 0.25, 0+usageOffset, 0.5, 0.25+usageOffset);
                        this.renderFaceXPos(tess, 0.25, 0+usageOffset, 0.5, 0.25+usageOffset);

                        this.renderFaceZNeg(tess, 0, 0.75, 0.25, 1);
                        this.renderFaceZPos(tess, 0, 0.75, 0.25, 1);
                    }


                } else if (isLast) {
                    this.renderFaceYNeg(tess, 0.75, 0.5, 1.0, 0.75, doRotate);
                    this.renderFaceYPos(tess, 0.75, 0.5, 1.0, 0.75, doRotate);

                    if(doRotate) {
                        this.renderFaceZNeg(tess, 0.75, 0+usageOffset, 1.0, 0.25+usageOffset);
                        this.renderFaceZPos(tess, 0.75, 0+usageOffset, 1.0, 0.25+usageOffset);

                        this.renderFaceXNeg(tess, 0, 0.75, 0.25, 1);
                        this.renderFaceXPos(tess, 0, 0.75, 0.25, 1);
                    } else {
                        this.renderFaceXNeg(tess, 0.75, 0+usageOffset, 1.0, 0.25+usageOffset);
                        this.renderFaceXPos(tess, 0.75, 0+usageOffset, 1.0, 0.25+usageOffset);

                        this.renderFaceZNeg(tess, 0, 0.75, 0.25, 1);
                        this.renderFaceZPos(tess, 0, 0.75, 0.25, 1);
                    }


                } else {
                    this.renderFaceYNeg(tess, 0.5, 0.5, 0.75, 0.75, doRotate);
                    this.renderFaceYPos(tess, 0.5, 0.5, 0.75, 0.75, doRotate);

                    if(doRotate) {
                        this.renderFaceZNeg(tess, 0.5, 0+usageOffset, 0.75, 0.25+usageOffset);
                        this.renderFaceZPos(tess, 0.5, 0+usageOffset, 0.75, 0.25+usageOffset);
                    } else {
                        this.renderFaceXNeg(tess, 0.5, 0+usageOffset, 0.75, 0.25+usageOffset);
                        this.renderFaceXPos(tess, 0.5, 0+usageOffset, 0.75, 0.25+usageOffset);
                    }


                }
            }



        }

        // somehow do the stuff
        // return Minecraft.isAmbientOcclusionEnabled() && p_147784_1_.getLightValue() == 0 ? (this.partialRenderBounds ? this.renderStandardBlockWithAmbientOcclusionPartial(p_147784_1_, p_147784_2_, p_147784_3_, p_147784_4_, f, f1, f2) : this.renderStandardBlockWithAmbientOcclusion(p_147784_1_, p_147784_2_, p_147784_3_, p_147784_4_, f, f1, f2)) : this.renderStandardBlockWithColorMultiplier(p_147784_1_, p_147784_2_, p_147784_3_, p_147784_4_, f, f1, f2);


        GL11.glPopMatrix();
    }

    public void renderFaceZPos(Tessellator tessellator, double uMin, double vMin, double uMax, double vMax)
    {
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.8F, 0.0F, 0.0F);

        tessellator.addVertexWithUV(0, 1, 1, uMax, vMin);
        tessellator.addVertexWithUV(0, 0, 1, uMax, vMax);
        tessellator.addVertexWithUV(1, 0, 1, uMin, vMax);
        tessellator.addVertexWithUV(1, 1, 1, uMin, vMin);

        tessellator.draw();
    }

    public void renderFaceZNeg(Tessellator tessellator, double uMin, double vMin, double uMax, double vMax)
    {
        tessellator.startDrawingQuads();
        tessellator.setNormal(-0.8F, 0.0F, 0.0F);



        tessellator.addVertexWithUV(0, 1, 0, uMax, vMin);
        tessellator.addVertexWithUV(1, 1, 0, uMin, vMin);
        tessellator.addVertexWithUV(1, 0, 0, uMin, vMax);
        tessellator.addVertexWithUV(0, 0, 0, uMax, vMax);

        tessellator.draw();

    }

    public void renderFaceXNeg(Tessellator tessellator, double uMin, double vMin, double uMax, double vMax)
    {
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 0.8F);

        tessellator.addVertexWithUV(0, 1, 1, uMin, vMin);
        tessellator.addVertexWithUV(0, 1, 0, uMax, vMin);
        tessellator.addVertexWithUV(0, 0, 0, uMax, vMax);
        tessellator.addVertexWithUV(0, 0, 1, uMin, vMax);

        tessellator.draw();
    }

    public void renderFaceXPos(Tessellator tessellator, double uMin, double vMin, double uMax, double vMax)
    {
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -0.8F);

        tessellator.addVertexWithUV(1, 0, 1, uMin, vMax);
        tessellator.addVertexWithUV(1, 0, 0, uMax, vMax);
        tessellator.addVertexWithUV(1, 1, 0, uMax, vMin);
        tessellator.addVertexWithUV(1, 1, 1, uMin, vMin);

        tessellator.draw();

    }

    public void renderFaceYNeg(Tessellator tessellator, double uMin, double vMin, double uMax, double vMax, boolean rotate)
    {
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -0.8F, 0.0F);

        double u1 = 1;
        double u2 = 0;
        double v2 = 0;
        double v1 = 1;

        if(rotate) {

            tessellator.addVertexWithUV(0, 0, 1, uMax, vMax);
            tessellator.addVertexWithUV(0, 0, 0, uMax, vMin);
            tessellator.addVertexWithUV(1, 0, 0, uMin, vMin);
            tessellator.addVertexWithUV(1, 0, 1, uMin, vMax);
        } else {
            tessellator.addVertexWithUV(0, 0, 1, uMin, vMax);
            tessellator.addVertexWithUV(0, 0, 0, uMax, vMax);
            tessellator.addVertexWithUV(1, 0, 0, uMax, vMin);
            tessellator.addVertexWithUV(1, 0, 1, uMin, vMin);
        }


        tessellator.draw();

    }

    public void renderFaceYPos(Tessellator tessellator, double uMin, double vMin, double uMax, double vMax, boolean rotate)
    {
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
            tessellator.addVertexWithUV(1, 1, 1, uMin, vMin);
            tessellator.addVertexWithUV(1, 1, 0, uMin, vMax);
            tessellator.addVertexWithUV(0, 1, 0, uMax, vMax);
            tessellator.addVertexWithUV(0, 1, 1, uMax, vMin);
        } else {
            tessellator.addVertexWithUV(1, 1, 1, uMin, vMax);//10
            tessellator.addVertexWithUV(1, 1, 0, uMax, vMax);//00
            tessellator.addVertexWithUV(0, 1, 0, uMax, vMin);//01
            tessellator.addVertexWithUV(0, 1, 1, uMin, vMin);//11
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
