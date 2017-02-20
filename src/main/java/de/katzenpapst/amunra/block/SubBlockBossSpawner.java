package de.katzenpapst.amunra.block;

import de.katzenpapst.amunra.tile.TileEntityDungeonSpawnerOsiris;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class SubBlockBossSpawner extends SubBlock {

  //  protected Class<? extends IBoss> bossType;

    public SubBlockBossSpawner(String name, String texture/*, Class<? extends IBoss> bossType*/) {
        super(name, texture);

//        this.bossType = bossType;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata)
    {

        return new TileEntityDungeonSpawnerOsiris();
    }


}
