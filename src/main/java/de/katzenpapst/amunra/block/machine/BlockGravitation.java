package de.katzenpapst.amunra.block.machine;

import de.katzenpapst.amunra.block.SubBlockMachine;
import de.katzenpapst.amunra.tile.TileEntityGravitation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockGravitation extends SubBlockMachine {

    public BlockGravitation(String name, String texture) {
        super(name, texture);
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        return new TileEntityGravitation();
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

}
