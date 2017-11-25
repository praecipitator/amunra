package de.katzenpapst.amunra.client.renderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.util.MathHelper;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.entity.spaceship.EntityShuttle;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderShuttle extends Render<EntityShuttle> {

    private ResourceLocation rocketTexture;

    protected ModelBase rocketModelObj;

    protected ResourceLocation texture = new ResourceLocation(AmunRa.ASSETPREFIX, "textures/model/shuttle.png");

    public RenderShuttle(RenderManager manager, ModelBase spaceshipModel, String textureDomain, String texture)
    {
        super(manager);
        this.rocketModelObj = spaceshipModel;
        this.rocketTexture = new ResourceLocation(textureDomain, "textures/model/" + texture + ".png");
        this.shadowSize = 2F;
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityShuttle par1Entity)
    {
        return this.rocketTexture;
    }

    public void renderSpaceship(EntitySpaceshipBase entity, double par2, double par4, double par6, float par8, float par9)
    {
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPushMatrix();
        final float var24 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * par9 + 180;
        // final float var25 = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * par9 + 45;

        GL11.glTranslatef((float) par2, (float) par4 - 0.4F, (float) par6);
        GL11.glRotatef(180.0F - par8, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-var24, 0.0F, 0.0F, 1.0F);
        final float var28 = entity.rollAmplitude / 3 - par9;
        float var30 = entity.shipDamage - par9;

        if (var30 < 0.0F)
        {
            var30 = 0.0F;
        }



        if (var28 > 0.0F)
        {
            final float i = entity.getLaunched() ? (5 - MathHelper.floor_double(entity.timeUntilLaunch / 85)) / 10F : 0.3F;
            GL11.glRotatef((float) (MathHelper.sin(var28) * var28 * i * par9), 1.0F, 0.0F, 0.0F);
            GL11.glRotatef((float) (MathHelper.sin(var28) * var28 * i * par9), 1.0F, 0.0F, 1.0F);
        }

        this.bindTexture(texture);
        //this.bindEntityTexture(entity);
        GL11.glScalef(-1.0F, -1.0F, 1.0F);
        GL11.glScalef(0.9F, 0.9F, 0.9F);
        //this.rocketModelObj.renderAll();
        this.rocketModelObj.render(entity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        /*
        this.rocketModelObj.renderOnly("Boosters", "Rocket");
        Vector3 teamColor = ClientUtil.updateTeamColor(FMLClientHandler.instance().getClient().thePlayer.getCommandSenderName(), true);
        if (teamColor != null)
        {
            GL11.glColor3f(teamColor.floatX(), teamColor.floatY(), teamColor.floatZ());
        }
        this.rocketModelObj.renderPart("NoseCone");

        if (FMLClientHandler.instance().getClient().thePlayer.ticksExisted / 10 % 2 < 1)
        {
            GL11.glColor3f(1, 0, 0);
        }
        else
        {
            GL11.glColor3f(0, 1, 0);
        }

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        this.rocketModelObj.renderPart("Cube");
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING);
         */
        GL11.glColor3f(1, 1, 1);

        GL11.glPopMatrix();
    }

}
