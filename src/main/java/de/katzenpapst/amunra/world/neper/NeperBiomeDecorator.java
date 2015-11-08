package de.katzenpapst.amunra.world.neper;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenerator;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace;

public class NeperBiomeDecorator extends BiomeDecoratorSpace {
	protected World mWorld = null;
	protected WorldGenerator grassGen = new WorldGenTallGrass(Blocks.tallgrass, 1);
	private int grassPerChunk = 10;
	@Override
	protected void setCurrentWorld(World world) {
		mWorld = world;

	}

	@Override
	protected World getCurrentWorld() {
		return mWorld;
	}

	@Override
	protected void decorate() {
		for (int j = 0; j < this.grassPerChunk ; ++j)
        {
            int k = this.chunkX + this.mWorld.rand.nextInt(16) + 8;
            int l = this.chunkZ + this.mWorld.rand.nextInt(16) + 8;
            int i1 = mWorld.rand.nextInt(this.mWorld.getHeightValue(k, l) * 2);

            grassGen.generate(this.mWorld, this.mWorld.rand, k, i1, l);
        }
	}

}
