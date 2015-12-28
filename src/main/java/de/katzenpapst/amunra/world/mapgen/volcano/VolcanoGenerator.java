package de.katzenpapst.amunra.world.mapgen.volcano;

import java.util.Random;

import de.katzenpapst.amunra.world.mapgen.BaseStructureStart;
import de.katzenpapst.amunra.world.mapgen.StructureGenerator;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.util.MathHelper;

public class VolcanoGenerator extends StructureGenerator  {

	protected final BlockMetaPair fluid;
	protected final BlockMetaPair mountainMaterial;
	protected final BlockMetaPair shaftMaterial;
	protected final int maxDepth;
	protected final boolean createMagmaChamber;

	public VolcanoGenerator(BlockMetaPair fluid, BlockMetaPair mountainMaterial, BlockMetaPair shaftMaterial, int maxDepth, boolean magmaChamber) {
		this.fluid = fluid;
		this.mountainMaterial = mountainMaterial;
		this.shaftMaterial = shaftMaterial;
		this.maxDepth = maxDepth;
		this.createMagmaChamber = magmaChamber;
	}

	@Override
	protected long getSalt() {
		return 84375932847598L;
	}

	@Override
	protected boolean canGenerateHere(int chunkX, int chunkZ, Random rand) {
		int rangeShift = 4;
		int range = 1 << rangeShift;
		int superchunkX = chunkX >> rangeShift;
		int superchunkZ = chunkZ >> rangeShift;

		int chunkStartX = superchunkX << rangeShift;
		int chunkStartZ = superchunkZ << rangeShift;
		int chunkEndX = chunkStartX+range-1;
		int chunkEndZ = chunkStartZ+range-1;
		// this square of chunk coords superchunkX,superchunkX+range-1 and superchunkZ,superchunkZ+range-1
		// now could contain a village
		this.rand.setSeed(this.worldObj.getSeed() ^ this.getSalt() ^ superchunkX ^ superchunkZ);

		int actualVillageX = MathHelper.getRandomIntegerInRange(this.rand, chunkStartX, chunkEndX);
		int actualVillageZ = MathHelper.getRandomIntegerInRange(this.rand, chunkStartZ, chunkEndZ);

		return (chunkX == actualVillageX && chunkZ == actualVillageZ);
	}

	@Override
	protected BaseStructureStart createNewStructure(int xChunkCoord, int zChunkCoord) {
		Random rand = new Random(this.worldObj.getSeed() ^ xChunkCoord ^ zChunkCoord ^ this.getSalt());
		Volcano v =  new Volcano(worldObj, xChunkCoord, zChunkCoord, rand);
		v.setFluid(fluid);
		v.setMaxDepth(maxDepth);
		v.setMountainMaterial(mountainMaterial);
		v.setShaftMaterial(shaftMaterial);
		v.setHasMagmaChamber(createMagmaChamber);
		return v;
	}

	@Override
	public String getName() {
		return "Volcano";
	}

}
