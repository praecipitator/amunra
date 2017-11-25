package de.katzenpapst.amunra.client.renderer;
/*
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import de.katzenpapst.amunra.tile.TileEntityMothershipEngineAbstract;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelCustom;

public class RenderMothershipJet extends TileEntitySpecialRenderer {

    protected ResourceLocation texture;
    // protected static final ResourceLocation textureActive = new ResourceLocation(AmunRa.ASSETPREFIX, "textures/model/jet-burning.png");

    protected final IModelCustom model;

    public RenderMothershipJet(IModelCustom leModel, ResourceLocation texture) {
        this.model = leModel;
        this.texture = texture;
    }


    public void renderMothershipEngine(TileEntityMothershipEngineAbstract chamber, double par2, double par4, double par6, float par8)
    {
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPushMatrix();
        //GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glTranslatef((float) par2 + 0.5F, (float) par4, (float) par6 + 0.5F);

        float rotation = 0.0F;

        /*
         * 2 -> -Z
         * 1 -> -X
         * 3 -> +X
         * 0 -> +Z
         * * /
        switch (chamber.getRotationMeta())
        {
        case 0:
            rotation = 0;//180.0F;// -> Z
            break;
        case 1:
            rotation = 270.0F;//90.0F;// -> -X
            break;
        case 2:
            rotation = 180.0F;//0;// -> -Z
            break;
        case 3:
            rotation = 90.0F;//270.0F;// -> X
            break;
        }

        GL11.glScalef(0.5F, 0.5F, 0.5F);
        GL11.glRotatef(rotation, 0, 1, 0);
        GL11.glTranslatef(0.0F, 1.0F, 1.0F);

        //if(chamber.isInUse()) {
//            this.bindTexture(textureActive);
            this.bindTexture(texture);
        /*} else {
        }* /
        this.model.renderAll();
/*
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(0.1F, 0.6F, 0.5F, 0.4F);

        this.model.renderPart("Shield_Torus");

        GL11.glEnable(GL11.GL_TEXTURE_2D);
* /
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
    @Override
    public void renderTileEntityAt(TileEntity entity, double x, double y, double z,
            float partialTickTime) {
        this.renderMothershipEngine((TileEntityMothershipEngineAbstract) entity, x, y, z, partialTickTime);
        // micdoodle8.mods.galacticraft.planets.mars.client.render.tile.TileEntityCryogenicChamberRenderer
    }

}
*/