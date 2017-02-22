package de.katzenpapst.amunra.block;

import de.katzenpapst.amunra.tile.TileEntityBossDungeonSpawner;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class SubBlockBossSpawner extends SubBlock {

    public SubBlockBossSpawner(String name, String texture) {
        super(name, texture);
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        return new TileEntityBossDungeonSpawner();
    }
}
