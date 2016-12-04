package de.katzenpapst.amunra.block.machine.mothershipEngine;

import de.katzenpapst.amunra.block.SubBlockMachine;
import de.katzenpapst.amunra.item.ItemDamagePair;
import net.minecraft.world.World;

public abstract class MothershipEngineJetBase extends SubBlockMachine implements IMothershipEngine {

    public MothershipEngineJetBase(String name, String texture) {
        super(name, texture);
        // TODO Auto-generated constructor stub
    }

    /**
     * Not sure why I have to do this here, but...
     */
    abstract protected ItemDamagePair getItem();
}
