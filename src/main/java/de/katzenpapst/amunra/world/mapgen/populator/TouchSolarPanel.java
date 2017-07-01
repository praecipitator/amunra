package de.katzenpapst.amunra.world.mapgen.populator;

import micdoodle8.mods.galacticraft.core.tile.TileEntitySolar;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class TouchSolarPanel extends AbstractPopulator {

    /**
     * This is just here to make the solar panels generate their fakeblocks
     *
     * @param x
     * @param y
     * @param z
     */

    public TouchSolarPanel(BlockPos pos) {
        super(pos);// this doesn't need any further stuff
    }

    @Override
    public boolean populate(World world) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileEntitySolar) {
            ((TileEntitySolar) tile).onCreate(world, pos);
            return true;
        }
        return false;
    }

}
