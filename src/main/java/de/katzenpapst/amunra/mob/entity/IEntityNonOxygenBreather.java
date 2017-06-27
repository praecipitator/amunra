package de.katzenpapst.amunra.mob.entity;

import micdoodle8.mods.galacticraft.api.world.AtmosphereInfo;

public interface IEntityNonOxygenBreather {
	/**
	 * Return true if the creature can breathe in the given atmosphere
	 *
	 * @param atmosphere		ArrayList of IAtmosphericGas
	 * @param isInSealedArea	if true, behave as if oxygen is present in the atmosphere
	 * @return
	 */
	public boolean canBreatheIn(AtmosphereInfo atmosphere, boolean isInSealedArea);
}
