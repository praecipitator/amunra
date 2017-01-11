package de.katzenpapst.amunra.block.machine.mothershipEngine;

import de.katzenpapst.amunra.tile.TileEntityMothershipEngineBoosterIon;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class MothershipEngineBoosterIon extends MothershipEngineBoosterBase {

    public MothershipEngineBoosterIon(String name, String texture, String activeTexture) {
        super(name, texture, activeTexture);
        // TODO Auto-generated constructor stub
    }

    public MothershipEngineBoosterIon(
            String name,
            String texture,
            String activeTexture,
            String tool,
            int harvestLevel) {
        super(name, texture, activeTexture, tool, harvestLevel);
        // TODO Auto-generated constructor stub
    }

    public MothershipEngineBoosterIon(
            String name,
            String texture,
            String activeTexture,
            String tool,
            int harvestLevel,
            float hardness,
            float resistance) {
        super(name, texture, activeTexture, tool, harvestLevel, hardness, resistance);
        // TODO Auto-generated constructor stub
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        return new TileEntityMothershipEngineBoosterIon();
    }

}
