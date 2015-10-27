package de.katzenpapst.amunra.world.mapgen.village.populator;

import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.core.tile.TileEntitySolar;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TouchSolarPanel extends AbstractPopulator {
	
	/**
	 * This is just here to make the solar panels generate their fakeblocks
	 * @param x
	 * @param y
	 * @param z
	 */

	public TouchSolarPanel(int x, int y, int z) {
		super(x, y, z);// this doesn't need any further stuff
	}

	@Override
	public boolean populate(World world) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile != null && tile instanceof TileEntitySolar)
        {
            ((TileEntitySolar) tile).onCreate(new BlockVec3(x, y, z));
            return true;
        }
		return false;
	}

}
