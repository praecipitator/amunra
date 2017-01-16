package de.katzenpapst.amunra.client.renderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.client.BlockRenderHelper;
import de.katzenpapst.amunra.tile.TileEntityMothershipEngineAbstract;
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

    private ResourceLocation texture = new ResourceLocation(AmunRa.instance.ASSETPREFIX, "textures/blocks/jet-base.png");

    public RenderMothershipBooster(ResourceLocation texture) {
        this.texture = texture;
    }

    protected void renderMSBooster(TileEntityMothershipEngineBooster entity,
            double x,
            double y,
            double z,
            float partialTickTime) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);


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
        TileEntityMothershipEngineAbstract masterTile = entity.getMasterTile();
        if(masterTile == null) {
            BlockRenderHelper.renderFaceYNeg(tess, 0, 0.5, 0.25, 0.75, false);
            BlockRenderHelper.renderFaceYPos(tess, 0, 0.5, 0.25, 0.75, false);


            BlockRenderHelper.renderFaceZNeg(tess, 0, 0, 0.25, 0.25);
            BlockRenderHelper.renderFaceZPos(tess, 0, 0, 0.25, 0.25);
            BlockRenderHelper.renderFaceXNeg(tess, 0, 0, 0.25, 0.25);
            BlockRenderHelper.renderFaceXPos(tess, 0, 0, 0.25, 0.25);
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

                BlockRenderHelper.renderFaceYNeg(tess, 0, 0.5, 0.25, 0.75, false);
                BlockRenderHelper.renderFaceYPos(tess, 0, 0.5, 0.25, 0.75, false);

                if(doRotate) {
                    // side
                    BlockRenderHelper.renderFaceZNeg(tess, 0, 0+usageOffset, 0.25, 0.25+usageOffset);
                    BlockRenderHelper.renderFaceZPos(tess, 0, 0+usageOffset, 0.25, 0.25+usageOffset);

                    // front
                    BlockRenderHelper.renderFaceXNeg(tess, 0, 0.75, 0.25, 1);
                    BlockRenderHelper.renderFaceXPos(tess, 0, 0.75, 0.25, 1);

                } else {
                    BlockRenderHelper.renderFaceZNeg(tess, 0, 0.75, 0.25, 1);
                    BlockRenderHelper.renderFaceZPos(tess, 0, 0.75, 0.25, 1);

                    BlockRenderHelper.renderFaceXNeg(tess, 0, 0+usageOffset, 0.25, 0.25+usageOffset);
                    BlockRenderHelper.renderFaceXPos(tess, 0, 0+usageOffset, 0.25, 0.25+usageOffset);
                }



            } else {
                if(isFirst) {
                    BlockRenderHelper.renderFaceYNeg(tess, 0.25, 0.5, 0.5, 0.75, doRotate);
                    BlockRenderHelper.renderFaceYPos(tess, 0.25, 0.5, 0.5, 0.75, doRotate);

                    if(doRotate) {
                        BlockRenderHelper.renderFaceZNeg(tess, 0.25, 0+usageOffset, 0.5, 0.25+usageOffset);
                        BlockRenderHelper.renderFaceZPos(tess, 0.25, 0+usageOffset, 0.5, 0.25+usageOffset);

                        BlockRenderHelper.renderFaceXNeg(tess, 0, 0.75, 0.25, 1);
                        BlockRenderHelper.renderFaceXPos(tess, 0, 0.75, 0.25, 1);
                    } else {
                        BlockRenderHelper.renderFaceXNeg(tess, 0.25, 0+usageOffset, 0.5, 0.25+usageOffset);
                        BlockRenderHelper.renderFaceXPos(tess, 0.25, 0+usageOffset, 0.5, 0.25+usageOffset);

                        BlockRenderHelper.renderFaceZNeg(tess, 0, 0.75, 0.25, 1);
                        BlockRenderHelper.renderFaceZPos(tess, 0, 0.75, 0.25, 1);
                    }


                } else if (isLast) {
                    BlockRenderHelper.renderFaceYNeg(tess, 0.75, 0.5, 1.0, 0.75, doRotate);
                    BlockRenderHelper.renderFaceYPos(tess, 0.75, 0.5, 1.0, 0.75, doRotate);

                    if(doRotate) {
                        BlockRenderHelper.renderFaceZNeg(tess, 0.75, 0+usageOffset, 1.0, 0.25+usageOffset);
                        BlockRenderHelper.renderFaceZPos(tess, 0.75, 0+usageOffset, 1.0, 0.25+usageOffset);

                        BlockRenderHelper.renderFaceXNeg(tess, 0, 0.75, 0.25, 1);
                        BlockRenderHelper.renderFaceXPos(tess, 0, 0.75, 0.25, 1);
                    } else {
                        BlockRenderHelper.renderFaceXNeg(tess, 0.75, 0+usageOffset, 1.0, 0.25+usageOffset);
                        BlockRenderHelper.renderFaceXPos(tess, 0.75, 0+usageOffset, 1.0, 0.25+usageOffset);

                        BlockRenderHelper.renderFaceZNeg(tess, 0, 0.75, 0.25, 1);
                        BlockRenderHelper.renderFaceZPos(tess, 0, 0.75, 0.25, 1);
                    }


                } else {
                    BlockRenderHelper.renderFaceYNeg(tess, 0.5, 0.5, 0.75, 0.75, doRotate);
                    BlockRenderHelper.renderFaceYPos(tess, 0.5, 0.5, 0.75, 0.75, doRotate);

                    if(doRotate) {
                        BlockRenderHelper.renderFaceZNeg(tess, 0.5, 0+usageOffset, 0.75, 0.25+usageOffset);
                        BlockRenderHelper.renderFaceZPos(tess, 0.5, 0+usageOffset, 0.75, 0.25+usageOffset);
                    } else {
                        BlockRenderHelper.renderFaceXNeg(tess, 0.5, 0+usageOffset, 0.75, 0.25+usageOffset);
                        BlockRenderHelper.renderFaceXPos(tess, 0.5, 0+usageOffset, 0.75, 0.25+usageOffset);
                    }


                }
            }
        }

        GL11.glPopMatrix();
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
