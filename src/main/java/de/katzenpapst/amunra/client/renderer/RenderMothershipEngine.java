package de.katzenpapst.amunra.client.renderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.tile.TileEntityMothershipEngine;
import micdoodle8.mods.galacticraft.planets.mars.MarsModule;
import micdoodle8.mods.galacticraft.planets.mars.blocks.BlockMachineMars;
import micdoodle8.mods.galacticraft.planets.mars.client.render.tile.TileEntityCryogenicChamberRenderer;
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityCryogenicChamber;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelCustom;

public class RenderMothershipEngine extends TileEntitySpecialRenderer {

    protected static final ResourceLocation texture = new ResourceLocation(AmunRa.ASSETPREFIX, "textures/model/engine.png");

    protected final IModelCustom model;

    public RenderMothershipEngine(IModelCustom leModel) {
        this.model = leModel;
    }


    public void renderMothershipEngine(TileEntityMothershipEngine chamber, double par2, double par4, double par6, float par8)
    {
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPushMatrix();
        //GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glTranslatef((float) par2 + 0.5F, (float) par4, (float) par6 + 0.5F);

        float rotation = 0.0F;

        switch (chamber.getRotationMeta())
        {
        case 0:
            rotation = 0;// -> -Z
            break;
        case 1:
            rotation = 180.0F;// -> Z
            break;
        case 2:
            rotation = 90.0F;// -> -X
            break;
        case 3:
            rotation = 270.0F;// -> X
            break;
        }

        GL11.glScalef(0.5F, 0.5F, 0.5F);
        GL11.glRotatef(rotation, 0, 1, 0);
        GL11.glTranslatef(0.0F, 0.0F, -3.0F);

        this.bindTexture(texture);
        this.model.renderPart("jet_Cylinder");
/*
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(0.1F, 0.6F, 0.5F, 0.4F);

        this.model.renderPart("Shield_Torus");

        GL11.glEnable(GL11.GL_TEXTURE_2D);
*/
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
    @Override
    public void renderTileEntityAt(TileEntity entity, double x, double y, double z,
            float partialTickTime) {
        this.renderMothershipEngine((TileEntityMothershipEngine) entity, x, y, z, partialTickTime);
        // micdoodle8.mods.galacticraft.planets.mars.client.render.tile.TileEntityCryogenicChamberRenderer
    }

}
