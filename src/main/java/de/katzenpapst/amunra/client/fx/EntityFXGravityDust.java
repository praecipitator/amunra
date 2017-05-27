package de.katzenpapst.amunra.client.fx;

import micdoodle8.mods.galacticraft.api.vector.Vector3;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;

public class EntityFXGravityDust extends EntityFX {

    private final float portalParticleScale;
    private final double portalPosX;
    private final double portalPosY;
    private final double portalPosZ;

    public EntityFXGravityDust(World par1World, Vector3 position, Vector3 motion) {
        super(par1World, position.x, position.y, position.z, motion.x, motion.y, motion.z);

        // better position
        double xDev = par1World.rand.nextGaussian() * 0.75;
        double zDev = par1World.rand.nextGaussian() * 0.75;

        position.x += xDev;
        position.z += zDev;

        if(motion.y < 0) {
            // < 0 means downwards
            position.y += 2;
        }

        double sqDist = xDev*xDev + zDev*zDev + 0.01;

        double maxLength = 1.2;

        this.setPosition(position.x, position.y, position.z);

        this.motionX = 0;//motion.x;
        this.motionY = motion.y;
        this.motionZ = 0;// motion.z;
        this.portalPosX = this.posX = position.x;
        this.portalPosY = this.posY = position.y;
        this.portalPosZ = this.posZ = position.z;
        this.portalParticleScale = this.particleScale = (float) Math.min(0.06D/(sqDist), 0.1);
        Vector3 color = new Vector3(0.4, 0.4, 0.4);
        //Vector3 color = new Vector3(1.0, 0.4, 0.4);
        this.particleRed = color.floatX();
        this.particleGreen = color.floatY();
        this.particleBlue = color.floatZ();
        double g = Math.abs(motion.y);
        double timeNeeded = maxLength/g;
        this.particleMaxAge = (int) (Math.random() * 10.0D + timeNeeded);
        this.noClip = true;
        this.setParticleTextureIndex((int) (Math.random() * 8.0D));
    }

    @Override
    public int getBrightnessForRender(float par1)
    {
        final int var2 = super.getBrightnessForRender(par1);
        float var3 = (float) this.particleAge / (float) this.particleMaxAge;
        var3 *= var3;
        var3 *= var3;
        final int var4 = var2 & 255;
        int var5 = var2 >> 16 & 255;
        var5 += (int) (var3 * 15.0F * 16.0F);

        if (var5 > 240)
        {
            var5 = 240;
        }

        return var4 | var5 << 16;
    }

    /**
     * Gets how bright this entity is.
     */
    @Override
    public float getBrightness(float par1)
    {
        final float var2 = super.getBrightness(par1);
        float var3 = (float) this.particleAge / (float) this.particleMaxAge;
        var3 = var3 * var3 * var3 * var3;
        return var2 * (1.0F - var3) + var3;
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;


        this.moveEntity(this.motionX, this.motionY, this.motionZ);


        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setDead();
        }
    }
}
