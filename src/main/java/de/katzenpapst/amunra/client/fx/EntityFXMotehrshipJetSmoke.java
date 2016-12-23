package de.katzenpapst.amunra.client.fx;

import org.lwjgl.opengl.GL11;

import micdoodle8.mods.galacticraft.api.vector.Vector3;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFXMotehrshipJetSmoke extends EntityFX {

    protected float smokeParticleScale;

    public EntityFXMotehrshipJetSmoke(
            World world,
            Vector3 pos,
            Vector3 motion,
            float scale) {
        super(world, pos.x, pos.y, pos.z, motion.x, motion.y, motion.z);

        smokeParticleScale = scale;

        this.motionX = motion.x + (double)((float)(Math.random() * 2.0D - 1.0D) * 0.4F);
        this.motionY = motion.y + (double)((float)(Math.random() * 2.0D - 1.0D) * 0.4F);
        this.motionZ = motion.z + (double)((float)(Math.random() * 2.0D - 1.0D) * 0.4F);
        float f = (float)(Math.random() + Math.random() + 1.0D) * 0.15F;
        float f1 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
        this.motionX = this.motionX / (double)f1 * (double)f * 0.4000000059604645D;
        this.motionY = this.motionY / (double)f1 * (double)f * 0.4000000059604645D;
        this.motionZ = this.motionZ / (double)f1 * (double)f * 0.4000000059604645D;

        // stealing from gc
        this.setSize(0.2F, 0.2F);
        /*this.motionX += motion.x;
        this.motionY += motion.y;
        this.motionZ += motion.z;*/
        this.particleAlpha = 1.0F;
        this.particleRed = this.particleGreen = this.particleBlue = (float) (Math.random() * 0.30000001192092896D) + 0.6F;
        this.particleScale *= 0.75F;
        this.particleScale *= scale * 3;
        this.smokeParticleScale = this.particleScale;


        this.particleMaxAge = (int) (this.particleMaxAge * scale) + 10;


        this.noClip = false;
    }


    @Override
    public void renderParticle(Tessellator par1Tessellator, float ticksPassedMaybe, float xMaybe, float yMaybe, float zMaybe, float uMaybe, float vMaybe)
    {
        GL11.glPushMatrix();
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        float ageDependentSize = (this.particleAge + ticksPassedMaybe) / this.particleMaxAge * 32.0F;

        if (ageDependentSize < 0.0F)
        {
            ageDependentSize = 0.0F;
        }

        if (ageDependentSize > 1.0F)
        {
            ageDependentSize = 1.0F;
        }

        this.particleScale = this.smokeParticleScale * ageDependentSize;
        float minU = this.particleTextureIndexX / 16.0F;
        float maxU = minU + 0.0624375F;
        float minV = this.particleTextureIndexY / 16.0F;
        float maxV = minV + 0.0624375F;
        final float scaleAgain = 0.1F * this.particleScale;

        if (this.particleIcon != null)
        {
            minU = this.particleIcon.getMinU();
            maxU = this.particleIcon.getMaxU();
            minV = this.particleIcon.getMinV();
            maxV = this.particleIcon.getMaxV();
        }

        final float xVertex = (float) (this.prevPosX + (this.posX - this.prevPosX) * ticksPassedMaybe - EntityFX.interpPosX);
        final float yVertex = (float) (this.prevPosY + (this.posY - this.prevPosY) * ticksPassedMaybe - EntityFX.interpPosY);
        final float zVertex = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * ticksPassedMaybe - EntityFX.interpPosZ);
        final float uselessFactor = 1.0F;
        par1Tessellator.setColorRGBA_F(this.particleRed * uselessFactor, this.particleGreen * uselessFactor, this.particleBlue * uselessFactor, this.particleAlpha);
        par1Tessellator.addVertexWithUV(xVertex - xMaybe * scaleAgain - uMaybe * scaleAgain, yVertex - yMaybe * scaleAgain, zVertex - zMaybe * scaleAgain - vMaybe * scaleAgain, maxU, maxV);
        par1Tessellator.addVertexWithUV(xVertex - xMaybe * scaleAgain + uMaybe * scaleAgain, yVertex + yMaybe * scaleAgain, zVertex - zMaybe * scaleAgain + vMaybe * scaleAgain, maxU, minV);
        par1Tessellator.addVertexWithUV(xVertex + xMaybe * scaleAgain + uMaybe * scaleAgain, yVertex + yMaybe * scaleAgain, zVertex + zMaybe * scaleAgain + vMaybe * scaleAgain, minU, minV);
        par1Tessellator.addVertexWithUV(xVertex + xMaybe * scaleAgain - uMaybe * scaleAgain, yVertex - yMaybe * scaleAgain, zVertex + zMaybe * scaleAgain - vMaybe * scaleAgain, minU, maxV);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
    }

    @Override
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setDead();
        }

        this.setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge);
        // was this acceleration? or decelleration?
        /*this.motionY -= 0.002D;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);

        if (this.posY == this.prevPosY)
        {
            this.motionX *= 1.1D;
            this.motionZ *= 1.1D;
        }

        this.motionX *= 0.99D;
        this.motionZ *= 0.99D;
        */
    }
}
