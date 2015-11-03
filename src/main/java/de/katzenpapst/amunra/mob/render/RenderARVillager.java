package de.katzenpapst.amunra.mob.render;

import micdoodle8.mods.galacticraft.core.GalacticraftCore;


import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.mob.entity.EntityARVillager;
import de.katzenpapst.amunra.mob.model.ModelARVillager;

public class RenderARVillager extends RenderLiving {
	private static final ResourceLocation villagerTexture = new ResourceLocation(AmunRa.instance.ASSETPREFIX, "textures/entity/villager.png");

    protected ModelARVillager villagerModel;

    public RenderARVillager()
    {
        super(new ModelARVillager(0.0F), 0.5F);
        this.villagerModel = (ModelARVillager) this.mainModel;
    }

    protected int shouldVillagerRenderPass(EntityARVillager par1EntityVillager, int par2, float par3)
    {
        return -1;
    }

    public void renderVillager(EntityARVillager par1EntityVillager, double par2, double par4, double par6, float par8, float par9)
    {
        super.doRender(par1EntityVillager, par2, par4, par6, par8, par9);
    }

    protected void renderVillagerEquipedItems(EntityARVillager par1EntityVillager, float par2)
    {
        super.renderEquippedItems(par1EntityVillager, par2);
    }

    protected void preRenderVillager(EntityARVillager par1EntityVillager, float par2)
    {
        float f1 = 0.9375F;

        if (par1EntityVillager.getGrowingAge() < 0)
        {
            f1 = (float) (f1 * 0.5D);
            this.shadowSize = 0.25F;
        }
        else
        {
            this.shadowSize = 0.5F;
        }

        GL11.glScalef(f1, f1, f1);
    }

    @Override
    public void doRender(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9)
    {
        this.renderVillager((EntityARVillager) par1EntityLiving, par2, par4, par6, par8, par9);
    }

    @Override
    protected void preRenderCallback(EntityLivingBase par1EntityLivingBase, float par2)
    {
        this.preRenderVillager((EntityARVillager) par1EntityLivingBase, par2);
    }

    @Override
    protected int shouldRenderPass(EntityLivingBase par1EntityLivingBase, int par2, float par3)
    {
        return this.shouldVillagerRenderPass((EntityARVillager) par1EntityLivingBase, par2, par3);
    }

    @Override
    protected void renderEquippedItems(EntityLivingBase par1EntityLivingBase, float par2)
    {
        this.renderVillagerEquipedItems((EntityARVillager) par1EntityLivingBase, par2);
    }

    @Override
    public void doRender(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6, float par8, float par9)
    {
        this.renderVillager((EntityARVillager) par1EntityLivingBase, par2, par4, par6, par8, par9);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
        return RenderARVillager.villagerTexture;
    }

    @Override
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.renderVillager((EntityARVillager) par1Entity, par2, par4, par6, par8, par9);
    }
}
