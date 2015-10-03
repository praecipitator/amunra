package de.katzenpapst.amunra.world;

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
		return super.getSolarSize() * 3.0F;
    }
}
