package de.katzenpapst.amunra.world.mapgen.populator;

import de.katzenpapst.amunra.helper.CoordHelper;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

abstract public class AbstractPopulator {

    protected BlockPos pos;

    public abstract boolean populate(World world);

    public AbstractPopulator(BlockPos pos) {
        this.pos = pos;
    }

    public boolean isInChunk(int chunkX, int chunkZ) {

        StructureBoundingBox box = CoordHelper.getChunkBB(chunkX, chunkZ);

        return box.isVecInside(pos);

    }

    @Deprecated
    public BlockVec3 getBlockVec3() {
        return new BlockVec3(pos);
    }

    public BlockPos getPos() {
        return pos;
    }
}
