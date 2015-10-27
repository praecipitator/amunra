package de.katzenpapst.amunra.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.astronomy.AngleDistance;
import de.katzenpapst.amunra.astronomy.AstronomyHelper;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.ForgeHooksClient;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.Moon;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.api.world.IAtmosphericGas;
import micdoodle8.mods.galacticraft.api.world.IExitHeight;
import micdoodle8.mods.galacticraft.api.world.ISolarLevel;
import micdoodle8.mods.galacticraft.api.world.ITeleportType;

public abstract class AmunraWorldProvider extends WorldProviderSpace implements
		IExitHeight, ISolarLevel {
	
	/**
	 * Gravity relative to OW. 
	 * 1.35 seems to be the last value where you can jump up blocks. 
	 * walking up stairs seems to work on any gravity
	 * @return
	 */
	protected abstract float getRelativeGravity();
	
	@Override
	public float getGravity() {
		return 0.08F * (1-getRelativeGravity());
	}
	
	@Override
	public double getFuelUsageMultiplier() {
		return getRelativeGravity();
	}
	
	@Override
	public float getFallDamageModifier() {
		return getRelativeGravity();
	}
	
	@Override
    public boolean hasBreathableAtmosphere()
    {
		//return this.isGasPresent(IAtmosphericGas.OXYGEN) && !this.isGasPresent(IAtmosphericGas.CO2); <- WTF
        return this.isGasPresent(IAtmosphericGas.OXYGEN);
		
    }
	
	public boolean hasAtmosphere() {
		return this.getCelestialBody().atmosphere.size() > 0;
	}
	
	/**
     * Return Vec3D with biome specific fog color
     */
    @SideOnly(Side.CLIENT)
    public Vec3 getFogColor(float celestialAngle, float partialTicksIThink)
    {
        float dayFactor = MathHelper.cos(celestialAngle * (float)Math.PI * 2.0F) * 2.0F + 0.5F;

        if (dayFactor < 0.0F)
        {
            dayFactor = 0.0F;
        }

        if (dayFactor > 1.0F)
        {
            dayFactor = 1.0F;
        }
        
        Vector3 baseColor = getFogColor();

        float f3 = baseColor.floatX();
        float f4 = baseColor.floatY();
        float f5 = baseColor.floatZ();
        f3 *= dayFactor * 0.94F + 0.06F;
        f4 *= dayFactor * 0.94F + 0.06F;
        f5 *= dayFactor * 0.91F + 0.09F;
        return Vec3.createVectorHelper((double)f3, (double)f4, (double)f5);
    }
	
	public float getSolarSize()
    {
		// this works only for planets...
		CelestialBody body = this.getCelestialBody();
		
		if(body instanceof Moon) {
			return 1.0F / ((Moon) body).getParentPlanet().getRelativeDistanceFromCenter().unScaledDistance;
		}
		return 1.0F / body.getRelativeDistanceFromCenter().unScaledDistance;
    }
	/*
	@SideOnly(Side.CLIENT)
    @Override
    public Vec3 getFogColor(float var1, float var2)
    {
        Vector3 fogColor = this.getFogColor();
        return Vec3.createVectorHelper(fogColor.floatX(), fogColor.floatY(), fogColor.floatZ());
    }*/
	
	@Override
    public Vec3 getSkyColor(Entity cameraEntity, float partialTicks)
    {
        Vector3 skyColorBase = this.getSkyColor();
        //return Vec3.createVectorHelper(skyColor.floatX(), skyColor.floatY(), skyColor.floatZ());
		//return new Vector3(0.60588, 0.7745, 1);
		float celestialAngle = this.worldObj.getCelestialAngle(partialTicks);
        float dayFactor = MathHelper.cos(celestialAngle * (float)Math.PI * 2.0F) * 2.0F + 0.5F;
        
        

        if (dayFactor < 0.0F)
        {
            dayFactor = 0.0F;
        }

        if (dayFactor > 1.0F)
        {
            dayFactor = 1.0F;
        }
        

        // but why?
        /**int i = MathHelper.floor_double(cameraEntity.posX);
        int j = MathHelper.floor_double(cameraEntity.posY);
        int k = MathHelper.floor_double(cameraEntity.posZ);
        int l = ForgeHooksClient.getSkyBlendColour(this.worldObj, i, j, k);*/
        float red = skyColorBase.floatX();//(float)(l >> 16 & 255) / 255.0F;
        float green = skyColorBase.floatY();//(float)(l >> 8 & 255) / 255.0F;
        float blue = skyColorBase.floatZ();//(float)(l & 255) / 255.0F;
        red *= dayFactor;
        green *= dayFactor;
        blue *= dayFactor;
        /*
        float rainStrength = this.worldObj.getRainStrength(partialTicks);
        float f8;
        float f9;

        if (rainStrength > 0.0F)
        {
            f8 = (red * 0.3F + green * 0.59F + blue * 0.11F) * 0.6F;
            f9 = 1.0F - rainStrength * 0.75F;
            red = red * f9 + f8 * (1.0F - f9);
            green = green * f9 + f8 * (1.0F - f9);
            blue = blue * f9 + f8 * (1.0F - f9);
        }

        f8 = this.worldObj.getWeightedThunderStrength(partialTicks);

        if (f8 > 0.0F)
        {
            f9 = (red * 0.3F + green * 0.59F + blue * 0.11F) * 0.2F;
            float f10 = 1.0F - f8 * 0.75F;
            red = red * f10 + f9 * (1.0F - f10);
            green = green * f10 + f9 * (1.0F - f10);
            blue = blue * f10 + f9 * (1.0F - f10);
        }

        if (this.worldObj.lastLightningBolt > 0)
        {
            f9 = (float)this.worldObj.lastLightningBolt - partialTicks;

            if (f9 > 1.0F)
            {
                f9 = 1.0F;
            }

            f9 *= 0.45F;
            red = red * (1.0F - f9) + 0.8F * f9;
            green = green * (1.0F - f9) + 0.8F * f9;
            blue = blue * (1.0F - f9) + 1.0F * f9;
        }*/

        return Vec3.createVectorHelper((double)red, (double)green, (double)blue);
    }
	
	/**
     * The current sun brightness factor for this dimension.
     * 0.0f means no light at all, and 1.0f means maximum sunlight.
     * This will be used for the "calculateSkylightSubtracted"
     * which is for Sky light value calculation.
     *
     * @return The current brightness factor
     * */
	@Override
    public float getSunBrightnessFactor(float par1)
    {
    	// I *think* that I could use this to make eclipses etc work
        float factor = worldObj.getSunBrightnessFactor(par1) + getAmunBrightnessFactor(par1);
        
        if(factor > 1.0F) {
        	factor = 1.0F;
        }
        /*float f1 = this.getCelestialAngle(p_72967_1_);
        float f2 = 1.0F - (MathHelper.cos(f1 * (float)Math.PI * 2.0F) * 2.0F + 0.5F);*/
        
        return factor;
    }
	
	protected float getAmunBrightnessFactor(float partialTicks) {
		CelestialBody curBody = this.getCelestialBody();
		if(curBody instanceof Moon) {
			curBody = ((Moon) curBody).getParentPlanet();
		}
		AngleDistance ad = AstronomyHelper.projectBodyToSky(curBody, AmunRa.instance.starAmun, partialTicks, this.worldObj.getWorldTime());
		// ad.angle is in pi
		
		// the angle I get is relative to celestialAngle
        float brightnessFactor = 1.0F - (MathHelper.cos((this.worldObj.getCelestialAngle(partialTicks)) * (float)Math.PI * 2.0F  + ad.angle) * 2.0F + 0.5F);
        
        if(brightnessFactor < 0) {
        	brightnessFactor = 0;
        }
        if(brightnessFactor > 1) {
        	brightnessFactor = 1;
        }
        
        brightnessFactor = 1.0F - brightnessFactor;
        
        // let's say brightnessFactor == 1 -> 0.5 of brightness
        return (float) (brightnessFactor * 0.5 / ad.distance);
	}
}
