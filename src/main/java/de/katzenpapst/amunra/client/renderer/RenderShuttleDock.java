package de.katzenpapst.amunra.client.renderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.client.BlockRenderHelper;
import de.katzenpapst.amunra.client.renderer.model.ModelShuttleDock;
import de.katzenpapst.amunra.tile.TileEntityShuttleDock;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class RenderShuttleDock extends TileEntitySpecialRenderer {

    //private ResourceLocation texture = new ResourceLocation(AmunRa.instance.ASSETPREFIX, "textures/model/dock.png");
    private ModelShuttleDock model;

    public RenderShuttleDock() {
        model =  new ModelShuttleDock();
    }

    @Override
    public void renderTileEntityAt(
            TileEntity entity,
            double x,
            double y,
            double z,
            float partialTicks) {

        if(!(entity instanceof TileEntityShuttleDock)) {
            return;
        }
        TileEntityShuttleDock dock = (TileEntityShuttleDock)entity;





        // render the stuff
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);

        TextureManager renderEngine = Minecraft.getMinecraft().renderEngine;
        /*GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);*/

        Tessellator tessellator = Tessellator.instance;

        // Minecraft.getMinecraft().renderEngine.bindTexture(texture);

        float rotation = 0.0F;

        /*
         * 2 -> -Z
         * 1 -> -X
         * 3 -> +X
         * 0 -> +Z
         * */

        switch (dock.getRotationMeta())
        {
        case 0:
            rotation = 90.0F;//180.0F;// -> Z
            break;
        case 1:
            rotation = 270.0F;//90.0F;// -> -X
            break;
        case 2:
            rotation = 180.0F;//0;// -> -Z
            break;
        case 3:
            rotation = 0.0F;//270.0F;// -> X
            break;
        }
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        //rotation = 0;
        GL11.glTranslatef(0.5F, 0.0F, 0.5F);
        GL11.glRotatef(rotation, 0, 1, 0);

        GL11.glRotatef(90.0F, 0, 1, 0);
        GL11.glEnable(GL11.GL_LIGHTING);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        model.render(tessellator, dock.hasShuttle());

/*
        Block block = dock.getWorldObj().getBlock(dock.xCoord, dock.yCoord, dock.zCoord);
        tessellator.setBrightness(block.getMixedBrightnessForBlock(dock.getWorldObj(), dock.xCoord, dock.yCoord, dock.zCoord));

        */


        GL11.glPopMatrix();
    }



}
