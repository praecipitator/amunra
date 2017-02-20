package de.katzenpapst.amunra.mob.render;

import org.lwjgl.opengl.GL11;

import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.mob.model.ModelFirstBoss;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.util.ResourceLocation;

public class RenderFirstBoss extends RenderBiped {

    private static final ResourceLocation zombieTextures = new ResourceLocation(AmunRa.ASSETPREFIX, "textures/entity/mummy.png");

    public RenderFirstBoss() {
        super(new ModelFirstBoss(), 1.0F);
    }

    @Override
    protected void preRenderCallback(EntityLivingBase par1EntityLiving, float par2)
    {
        GL11.glScalef(2.5F, 2.5F, 2.5F);
    }

    @Override
    public void doRender(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9)
    {
        BossStatus.setBossStatus((IBossDisplayData) par1EntityLiving, false);

        super.doRender(par1EntityLiving, par2, par4, par6, par8, par9);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityLiving e)
    {
        return zombieTextures;
    }
}
