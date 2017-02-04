package de.katzenpapst.amunra.mob.render;

import org.lwjgl.opengl.GL11;

import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.mob.model.ModelBug;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class RenderBug extends RenderLiving {

    protected static final ResourceLocation tex = new ResourceLocation(AmunRa.ASSETPREFIX, "textures/entity/bug.png");

    public RenderBug() {
        super(new ModelBug(), 1.0F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity ent) {
        return tex;
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entity, float partialTickTime)
    {
        GL11.glTranslated(0.0, 1.1, 0.0);
    }

}
