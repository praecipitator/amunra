package de.katzenpapst.amunra.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class VacuumGrass extends SubBlockGrass {

    //blockIcon = 0 = top
    @SideOnly(Side.CLIENT)
    protected IIcon blockIconSide;
    @SideOnly(Side.CLIENT)
    protected IIcon blockIconBottom;

    public VacuumGrass(String name, String textureTop, String textureSide, String textureBottom) {
        super(name, textureTop, textureSide, textureBottom);
    }

    /**
     * Return the block what this should revert to if the conditions are bad
     * @return
     */
    @Override
    public BlockMetaPair getDirtBlock() {
        return ARBlocks.blockMethaneDirt;
    }

    /**
     * Return true if the current conditions are good for this grasses survival, usually light stuff
     * The Multiblock will replace it with this.getDirtBlock()
     * Will also be called for dirt neighbors of this in order to check if this *could* live there
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @return
     */
    @Override
    public boolean canLiveHere(World world, int x, int y, int z) {

        return (
                (world.provider instanceof WorldProviderSpace)
                &&
                (((WorldProviderSpace)world.provider).getCelestialBody() != null)
                &&
                (((WorldProviderSpace)world.provider).getCelestialBody().atmosphere.isEmpty())
                &&
                super.canLiveHere(world, x, y, z)
                );

    }
}
