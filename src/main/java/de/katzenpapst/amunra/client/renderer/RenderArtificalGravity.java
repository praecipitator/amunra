package de.katzenpapst.amunra.client.renderer;
/*
import org.lwjgl.opengl.GL11;

import de.katzenpapst.amunra.tile.TileEntityGravitation;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class RenderArtificalGravity extends TileEntitySpecialRenderer {

    private enum Side {
        RIGHT,
        LEFT,
        FRONT,
        BACK
    }

    public RenderArtificalGravity() {
    }

    @Override
    public void renderTileEntityAt(
            TileEntity te,
            double x,
            double y,
            double z,
            float ticks) {

        if(!(te instanceof TileEntityGravitation)) {
            return;
        }


        TileEntityGravitation entity = (TileEntityGravitation)te;

        if(!entity.isBoxShown) {
            return;
        }
        int rotationMeta = entity.getRotationMeta();
        GL11.glPushMatrix();
        //GL11.glTranslated(-0.5, -0.5, -0.5);

        AxisAlignedBB box = entity.getRotatedAABB();
        box = AxisAlignedBB.getBoundingBox(
                box.minX,     box.minY,     box.minZ,
                box.maxX + 1, box.maxY + 1, box.maxZ + 1
                );



        GL11.glTranslatef((float) x, (float) y, (float) z);

        char c0 = 61680;
        int j = c0 % 65536;
        int k = c0 / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j / 1.0F, (float)k / 1.0F);


        GL11.glLineWidth(6.0F);
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ZERO);
        RenderHelper.disableStandardItemLighting();
        GL11.glColor4d(1.0, 0.0, 0.0, 1.0);
        GL11.glBegin(GL11.GL_LINES);

        double offset = 0.001;

        // start
        // lower rectangle
        GL11.glVertex3d(box.minX+offset, box.minY+offset, box.minZ+offset);
        GL11.glVertex3d(box.maxX-offset, box.minY+offset, box.minZ+offset);

        GL11.glVertex3d(box.minX+offset, box.maxY-offset, box.minZ+offset);
        GL11.glVertex3d(box.maxX-offset, box.maxY-offset, box.minZ+offset);

        GL11.glVertex3d(box.minX+offset, box.minY+offset, box.minZ+offset);
        GL11.glVertex3d(box.minX+offset, box.maxY-offset, box.minZ+offset);

        GL11.glVertex3d(box.maxX-offset, box.minY+offset, box.minZ+offset);
        GL11.glVertex3d(box.maxX-offset, box.maxY-offset, box.minZ+offset);

        // upper rectangle
        GL11.glVertex3d(box.minX+offset, box.minY+offset, box.maxZ-offset);
        GL11.glVertex3d(box.maxX-offset, box.minY+offset, box.maxZ-offset);

        GL11.glVertex3d(box.minX+offset, box.maxY-offset, box.maxZ-offset);
        GL11.glVertex3d(box.maxX-offset, box.maxY-offset, box.maxZ-offset);

        GL11.glVertex3d(box.minX+offset, box.minY+offset, box.maxZ-offset);
        GL11.glVertex3d(box.minX+offset, box.maxY-offset, box.maxZ-offset);

        GL11.glVertex3d(box.maxX-offset, box.minY+offset, box.maxZ-offset);
        GL11.glVertex3d(box.maxX-offset, box.maxY-offset, box.maxZ-offset);

        // connectors
        GL11.glVertex3d(box.minX+offset, box.minY+offset, box.minZ+offset);
        GL11.glVertex3d(box.minX+offset, box.minY+offset, box.maxZ-offset);

        GL11.glVertex3d(box.maxX-offset, box.minY+offset, box.minZ+offset);
        GL11.glVertex3d(box.maxX-offset, box.minY+offset, box.maxZ-offset);

        GL11.glVertex3d(box.minX+offset, box.maxY-offset, box.minZ+offset);
        GL11.glVertex3d(box.minX+offset, box.maxY-offset, box.maxZ-offset);

        GL11.glVertex3d(box.maxX-offset, box.maxY-offset, box.minZ+offset);
        GL11.glVertex3d(box.maxX-offset, box.maxY-offset, box.maxZ-offset);

        GL11.glEnd();

        // draw the colored lines
        // red, upwards
        GL11.glColor4d(0.87, 0.0, 0.0, 1.0);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(0.5, 0.5, 0.5);
        GL11.glVertex3d(0.5, box.maxY, 0.5);
        GL11.glEnd();

        // downwards
        GL11.glColor4d(0.0, 0.87, 0.87, 1.0);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(0.5, 0.5, 0.5);
        GL11.glVertex3d(0.5, box.minY, 0.5);
        GL11.glEnd();


        // right
        setColorForRotation(Side.RIGHT, rotationMeta);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(0.5, 0.5, 0.5);
        GL11.glVertex3d(box.maxX, 0.5, 0.5);
        GL11.glEnd();


        // left
        setColorForRotation(Side.LEFT, rotationMeta);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(0.5, 0.5, 0.5);
        GL11.glVertex3d(box.minX, 0.5, 0.5);
        GL11.glEnd();

        // front
        setColorForRotation(Side.FRONT, rotationMeta);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(0.5, 0.5, 0.5);
        GL11.glVertex3d(0.5, 0.5, box.minZ);
        GL11.glEnd();

        // back
        setColorForRotation(Side.BACK, rotationMeta);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(0.5, 0.5, 0.5);
        GL11.glVertex3d(0.5, 0.5, box.maxZ);
        GL11.glEnd();


        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_TEXTURE_2D);

    }

    private void setColorForRotation(Side side, int rotationMeta) {
        switch(side) {
        case BACK:
            switch(rotationMeta) {
            case 3:
                GL11.glColor4d(0.87, 0.87, 0.0, 1.0);
                //setColorForRotation(Side.LEFT, 0);
                break;
            case 2:
                GL11.glColor4d(0.0, 0.0, 0.87, 1.0);
                //setColorForRotation(Side.RIGHT, 0);
                break;
            case 0:
                GL11.glColor4d(0.0, 0.87, 0.0, 1.0);
                //setColorForRotation(Side.FRONT, 0);
                break;
            case 1:
            default:
                GL11.glColor4d(0.87, 0.0, 0.87, 1.0);
            }
            break;
        case FRONT:
            switch(rotationMeta) {
            case 0:
                GL11.glColor4d(0.87, 0.0, 0.87, 1.0);
                break;
            case 1:
                GL11.glColor4d(0.0, 0.87, 0.0, 1.0);
                break;
            case 2:
                GL11.glColor4d(0.87, 0.87, 0.0, 1.0);
                break;
            case 3:
                GL11.glColor4d(0.0, 0.0, 0.87, 1.0);
                break;
            }

            break;
        case LEFT:
            switch(rotationMeta) {
            case 0:
                GL11.glColor4d(0.0, 0.0, 0.87, 1.0);
                break;
            case 1:
                GL11.glColor4d(0.87, 0.87, 0.0, 1.0);
                break;
            case 2:
                GL11.glColor4d(0.87, 0.0, 0.87, 1.0);
                break;
            case 3:
                GL11.glColor4d(0.0, 0.87, 0.0, 1.0);
                break;
            }
            break;
        case RIGHT:
            switch(rotationMeta) {
            case 0:
                GL11.glColor4d(0.87, 0.87, 0.0, 1.0);
                break;
            case 1:
                GL11.glColor4d(0.0, 0.0, 0.87, 1.0);
                break;
            case 2:
                GL11.glColor4d(0.0, 0.87, 0.0, 1.0);
                break;
            case 3:
                GL11.glColor4d(0.87, 0.0, 0.87, 1.0);
                break;
            }
            //GL11.glColor4d(0.0, 0.0, 0.87, 1.0);
            break;
        default:
            break;

        }
    }

}
*/