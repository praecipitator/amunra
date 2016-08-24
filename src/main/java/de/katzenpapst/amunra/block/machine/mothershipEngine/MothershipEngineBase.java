package de.katzenpapst.amunra.block.machine.mothershipEngine;

import de.katzenpapst.amunra.block.SubBlockMachine;

public abstract class MothershipEngineBase extends SubBlockMachine {

    public MothershipEngineBase(String name, String texture) {
        super(name, texture);
        // TODO Auto-generated constructor stub
    }

    /**
     * This should return the amount of blocks this engine can move
     * @return
     */
    abstract public int getStrength();

    /**
     * This should return this engine's speed in AU/t
     * @return
     */
    abstract public double getSpeed();

    /**
     * Should figure out whenever it has enough fuel or energy or whatever for that given distance
     * @param distance
     * @return
     */
    abstract public boolean canTravelDistance(double distance);

}
