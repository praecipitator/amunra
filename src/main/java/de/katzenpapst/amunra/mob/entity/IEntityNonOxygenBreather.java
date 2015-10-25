package de.katzenpapst.amunra.mob.entity;

import java.util.ArrayList;

import micdoodle8.mods.galacticraft.api.world.IAtmosphericGas;

public interface IEntityNonOxygenBreather {
	public boolean canBreatheIn(ArrayList<IAtmosphericGas> atmosphere, boolean isInSealedArea);
}
