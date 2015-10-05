package de.katzenpapst.amunra.world;

import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.Moon;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace;
import micdoodle8.mods.galacticraft.api.world.IAtmosphericGas;
import micdoodle8.mods.galacticraft.api.world.IExitHeight;
import micdoodle8.mods.galacticraft.api.world.ISolarLevel;
import micdoodle8.mods.galacticraft.api.world.ITeleportType;

public abstract class AmunraWorldProvider extends WorldProviderSpace implements
		IExitHeight, ISolarLevel, ITeleportType {
	
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
	
	public float getSolarSize()
    {
		// this works only for planets...
		CelestialBody body = this.getCelestialBody();
		
		if(body instanceof Moon) {
			return 1.0F / ((Moon) body).getParentPlanet().getRelativeDistanceFromCenter().unScaledDistance;
		}
		return 1.0F / body.getRelativeDistanceFromCenter().unScaledDistance;
    }
	
	/**
     * The current sun brightness factor for this dimension.
     * 0.0f means no light at all, and 1.0f means maximum sunlight.
     * This will be used for the "calculateSkylightSubtracted"
     * which is for Sky light value calculation.
     *
     * @return The current brightness factor
     * */
	/*
	@Override
    public float getSunBrightnessFactor(float par1)
    {
    	// I *think* that I could use this to make eclipses etc work
        return worldObj.getSunBrightnessFactor(par1);
    }
	*/
}
