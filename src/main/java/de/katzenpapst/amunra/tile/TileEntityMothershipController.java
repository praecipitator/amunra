package de.katzenpapst.amunra.tile;

import de.katzenpapst.amunra.block.BlockMachineMeta;
import de.katzenpapst.amunra.block.SubBlockMachine;
import de.katzenpapst.amunra.mothership.Mothership;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.core.tile.TileEntityAdvanced;

public class TileEntityMothershipController extends TileEntityAdvanced {

    protected CelestialBody selectedTarget;
    protected Mothership parentMothership;

    private SubBlockMachine subBlock = null;

    public TileEntityMothershipController() { }


    public SubBlockMachine getSubBlock()
    {
        if(subBlock == null) {
            subBlock = (SubBlockMachine) ((BlockMachineMeta)this.getBlockType()).getSubBlock(this.getBlockMetadata());
        }
        return subBlock;
    }


    @Override
    public double getPacketRange()
    {
        return 12.0D;
    }

    @Override
    public int getPacketCooldown()
    {
        return 3;
    }

    @Override
    public boolean isNetworkedTile()
    {
        return true;
    }

}
