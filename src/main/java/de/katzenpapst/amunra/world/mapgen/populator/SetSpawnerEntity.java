package de.katzenpapst.amunra.world.mapgen.populator;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class SetSpawnerEntity extends AbstractPopulator {

    String entityName;

    public SetSpawnerEntity(BlockPos pos, String entityName) {
        super(pos);
        this.entityName = entityName;
    }

    @Override
    public boolean populate(World world) {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == Blocks.mob_spawner)
        {
            final TileEntityMobSpawner spawner = (TileEntityMobSpawner) world.getTileEntity(pos);
            if (spawner != null)
            {
                spawner.getSpawnerBaseLogic().setEntityName(entityName);
                return true;
            }
        }
        return false;
    }

}
