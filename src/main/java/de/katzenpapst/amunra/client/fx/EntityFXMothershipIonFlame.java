package de.katzenpapst.amunra.client.fx;

import java.util.List;

import org.lwjgl.opengl.GL11;

import micdoodle8.mods.galacticraft.api.vector.Vector3;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFXMothershipIonFlame extends EntityFX {

    protected float smokeParticleScale;

    public EntityFXMothershipIonFlame(
            World world,
            Vector3 pos,
            Vector3 motion,
            float scale) {
        super(world, pos.x, pos.y, pos.z, motion.x, motion.y, motion.z);

        //setParticleTextureIndex(82); // same as happy villager
        particleScale = 2.0F;
        //setRBGColorF(0x88, 0x00, 0x88);

        // this is needed because the vanilla code adds a y component
        this.motionX = motion.x + (double)((float)(Math.random() * 2.0D - 1.0D) * 0.4F);
        this.motionY = motion.y + (double)((float)(Math.random() * 2.0D - 1.0D) * 0.4F);
        this.motionZ = motion.z + (double)((float)(Math.random() * 2.0D - 1.0D) * 0.4F);
        float f = (float)(Math.random() + Math.random() + 1.0D) * 0.15F;
        float f1 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ) / 9.0F;
        this.motionX = this.motionX / (double)f1 * (double)f * 0.4000000059604645D;
        this.motionY = this.motionY / (double)f1 * (double)f * 0.4000000059604645D;
        this.motionZ = this.motionZ / (double)f1 * (double)f * 0.4000000059604645D;

        // stealing stuff from GC
        this.particleRed = 50F / 255F + this.rand.nextFloat() / 3;
        this.particleGreen = 255F / 255F;// + ;
        this.particleBlue = 255F / 255F;

        this.particleMaxAge = (int) (Math.ceil(particleMaxAge)*2.0F);

        this.noClip = true; // for now
    }

    @Override
    public void renderParticle(WorldRenderer worldRenderer, Entity entity, float f0, float f1, float f2, float f3, float f4, float f5)
    {
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        float var8 = (this.particleAge + f0) / this.particleMaxAge * 32.0F;

        if (var8 < 0.0F)
        {
            var8 = 0.0F;
        }

        if (var8 > 1.0F)
        {
            var8 = 1.0F;
        }

        this.particleScale = this.smokeParticleScale * var8;
        super.renderParticle(worldRenderer, entity, f0, f1, f2, f3, f4, f5);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
    }
/*

    @Override
    public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        float var8 = (this.particleAge + par2) / this.particleMaxAge * 32.0F;

        if (var8 < 0.0F)
        {
            var8 = 0.0F;
        }

        if (var8 > 1.0F)
        {
            var8 = 1.0F;
        }

        //this.particleScale = this.smokeParticleScale * var8;
        super.renderParticle(par1Tessellator, par2, par3, par4, par5, par6, par7);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
    }*/

    @Override
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge)
        {
            //AmunRa.proxy.spawnParticles(ParticleType.PT_MOTHERSHIP_JET_SMOKE, this.worldObj, new Vector3(posX, posY, posZ), new Vector3(this.motionX, this.motionY, this.motionZ).scale(5.0D));
            /*
            GalacticraftCore.proxy.spawnParticle(this.spawnSmokeShort ? "whiteSmokeLaunched" : "whiteSmokeIdle", new Vector3(this.posX, this.posY + this.rand.nextDouble() * 2, this.posZ), new Vector3(this.motionX, this.motionY, this.motionZ), new Object[] {});
            GalacticraftCore.proxy.spawnParticle(this.spawnSmokeShort ? "whiteSmokeLargeLaunched" : "whiteSmokeLargeIdle", new Vector3(this.posX, this.posY + this.rand.nextDouble() * 2, this.posZ), new Vector3(this.motionX, this.motionY, this.motionZ), new Object[] {});
            if (!this.spawnSmokeShort)
            {
                GalacticraftCore.proxy.spawnParticle("whiteSmokeIdle", new Vector3(this.posX, this.posY + this.rand.nextDouble() * 2, this.posZ), new Vector3(this.motionX, this.motionY, this.motionZ), new Object[] {});
                GalacticraftCore.proxy.spawnParticle("whiteSmokeLargeIdle", new Vector3(this.posX, this.posY + this.rand.nextDouble() * 2, this.posZ), new Vector3(this.motionX, this.motionY, this.motionZ), new Object[] {});
            }*/
            this.setDead();
            return;
        }

        // after a while, vary my other coordinates slightly



        this.setParticleTextureIndex(/*128 + 7 - this.particleAge * 8 / this.particleMaxAge*/167);
  //      this.motionY += 0.001D;


        this.moveEntity(this.motionX, this.motionY, this.motionZ);

        //this.particleGreen += 0.01F;
/*
        if (this.posY == this.prevPosY)
        {
            this.motionX *= 1.1D;
            this.motionZ *= 1.1D;
        }*/
/*
        this.motionX *= 0.9599999785423279D;
        this.motionY *= 0.9599999785423279D;
        this.motionZ *= 0.9599999785423279D;*/

        final List<?> var3 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(1.0D, 0.5D, 1.0D));

        if (var3 != null)
        {
            for (int var4 = 0; var4 < var3.size(); ++var4)
            {
                final Entity var5 = (Entity) var3.get(var4);

                if (var5 instanceof EntityLivingBase)
                {
                    if (!var5.isDead && !var5.isBurning() && !var5.equals(this.ridingEntity))
                    {
                        // not just fire, do some more
                        // or maybe do this in the tile entity instead
                        /*
                        var5.setFire(3);
                        GalacticraftCore.packetPipeline.sendToServer(new PacketSimple(EnumSimplePacket.S_SET_ENTITY_FIRE, new Object[] { var5.getEntityId() }));
                        */
                    }
                }
            }
        }
    }

    @Override
    public int getBrightnessForRender(float par1)
    {
        return 15728880;
    }

    @Override
    public float getBrightness(float par1)
    {
        return 1.0F;
    }
}
