package de.katzenpapst.amunra.world.mapgen.pyramid;

import java.util.Random;

import de.katzenpapst.amunra.world.mapgen.BaseStructureStart;
import de.katzenpapst.amunra.world.mapgen.StructureGenerator;



public class PyramidGenerator extends StructureGenerator {

	@Override
	protected boolean canGenerateHere(int chunkX, int chunkZ, Random rand) {
		this.rand.setSeed(this.worldObj.getSeed());
		//final long r0 = this.rand.nextLong();
        //final long r1 = this.rand.nextLong();
		final long randX = chunkX * getSalt();
        final long randZ = chunkZ * getSalt();
        this.rand.setSeed(randX ^ randZ ^ this.worldObj.getSeed());
		return this.rand.nextInt(700) == 0;
	}

	@Override
	protected BaseStructureStart createNewStructure(int xChunkCoord,
			int zChunkCoord) {
		return new Pyramid(this.worldObj, xChunkCoord, zChunkCoord, this.rand);
	}

	@Override
	public String getName() {
		return "Pyramid";
	}

	@Override
	protected long getSalt() {
		return 549865610521L;
	}

}
