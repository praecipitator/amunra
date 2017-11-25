package de.katzenpapst.amunra.client;
/*
import net.minecraft.client.renderer.Tessellator;

public class BlockRenderHelper {

    public static void renderFaceZPos(Tessellator tessellator, double uMin, double vMin, double uMax, double vMax)
    {
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.8F, 0.0F, 0.0F);

        tessellator.addVertexWithUV(0, 1, 1, uMax, vMin);
        tessellator.addVertexWithUV(0, 0, 1, uMax, vMax);
        tessellator.addVertexWithUV(1, 0, 1, uMin, vMax);
        tessellator.addVertexWithUV(1, 1, 1, uMin, vMin);

        tessellator.draw();
    }

    public static void renderFaceZNeg(Tessellator tessellator, double uMin, double vMin, double uMax, double vMax)
    {
        tessellator.startDrawingQuads();
        tessellator.setNormal(-0.8F, 0.0F, 0.0F);



        tessellator.addVertexWithUV(0, 1, 0, uMax, vMin);
        tessellator.addVertexWithUV(1, 1, 0, uMin, vMin);
        tessellator.addVertexWithUV(1, 0, 0, uMin, vMax);
        tessellator.addVertexWithUV(0, 0, 0, uMax, vMax);

        tessellator.draw();

    }

    public static void renderFaceXNeg(Tessellator tessellator, double uMin, double vMin, double uMax, double vMax)
    {
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 0.8F);

        tessellator.addVertexWithUV(0, 1, 1, uMin, vMin);
        tessellator.addVertexWithUV(0, 1, 0, uMax, vMin);
        tessellator.addVertexWithUV(0, 0, 0, uMax, vMax);
        tessellator.addVertexWithUV(0, 0, 1, uMin, vMax);

        tessellator.draw();
    }

    public static void renderFaceXPos(Tessellator tessellator, double uMin, double vMin, double uMax, double vMax)
    {
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -0.8F);

        tessellator.addVertexWithUV(1, 0, 1, uMin, vMax);
        tessellator.addVertexWithUV(1, 0, 0, uMax, vMax);
        tessellator.addVertexWithUV(1, 1, 0, uMax, vMin);
        tessellator.addVertexWithUV(1, 1, 1, uMin, vMin);

        tessellator.draw();

    }

    public static void renderFaceYNeg(Tessellator tessellator, double uMin, double vMin, double uMax, double vMax, boolean rotate)
    {
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -0.8F, 0.0F);

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

    public static void renderFaceYPos(Tessellator tessellator, double uMin, double vMin, double uMax, double vMax, boolean rotate)
    {
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.8F, 0.0F);

        if(rotate) {
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

}
*/
