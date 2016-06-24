package de.katzenpapst.amunra.mothership;

import micdoodle8.mods.galacticraft.core.world.gen.ChunkProviderOrbit;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

public class MothershipChunkProvider extends ChunkProviderOrbit { // for now, just like this

    public MothershipChunkProvider(World par1World, long par2, boolean par4) {
        super(par1World, par2, par4);
    }

    @Override
    public void populate(IChunkProvider par1IChunkProvider, int par2, int par3) {
        // this is where the worldgen happens
        super.populate(par1IChunkProvider, par2, par3);
    }
}
