package de.katzenpapst.amunra.client.renderer;

import org.lwjgl.opengl.GL11;

import de.katzenpapst.amunra.client.renderer.model.ModelHydroponics;
import de.katzenpapst.amunra.tile.TileEntityHydroponics;
import micdoodle8.mods.galacticraft.api.transmission.tile.IOxygenReceiver;
import micdoodle8.mods.galacticraft.core.util.OxygenUtil;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class RenderHydroponics extends TileEntitySpecialRenderer {

    private ModelHydroponics model;

    public RenderHydroponics() {
        model = new ModelHydroponics();
    }

    @Override
    public void renderTileEntityAt(
            TileEntity te,
            double x,
            double y,
            double z,
            float partialTicks) {
        if(te instanceof TileEntityHydroponics) {
            TileEntityHydroponics tile = (TileEntityHydroponics)te;

            GL11.glPushMatrix();
            GL11.glTranslatef((float) x, (float) y, (float) z);

            final TileEntity[] connections = OxygenUtil.getAdjacentOxygenConnections(tile);

            // meh
            for(int i = 0; i<connections.length;i++) {
                TileEntity cur = connections[i];
                ForgeDirection direction = ForgeDirection.values()[i];

                if(cur instanceof IOxygenReceiver) {
                    if(((IOxygenReceiver)cur).getOxygenRequest(direction.getOpposite()) <= 0) {
                        connections[i] = null;
                    }
                }
            }


            boolean hasNorth = connections[ForgeDirection.NORTH.ordinal()] != null;
            boolean hasSouth = connections[ForgeDirection.SOUTH.ordinal()] != null;
            boolean hasWest  = connections[ForgeDirection.WEST.ordinal()] != null;
            boolean hasEast  = connections[ForgeDirection.EAST.ordinal()] != null;



            final Tessellator tess = Tessellator.instance;
            model.render(tess, tile.getPlantGrowthStatus(), hasNorth, hasSouth, hasWest, hasEast);

            GL11.glPopMatrix();
        }

    }


}
