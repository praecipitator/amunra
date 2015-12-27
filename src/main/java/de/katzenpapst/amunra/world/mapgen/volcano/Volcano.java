package de.katzenpapst.amunra.world.mapgen.volcano;

import java.util.Random;

import cpw.mods.fml.common.FMLLog;
import de.katzenpapst.amunra.world.CoordHelper;
import de.katzenpapst.amunra.world.mapgen.BaseStructureStart;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class Volcano extends BaseStructureStart {



	protected BlockMetaPair fluid;
	protected BlockMetaPair mountainMaterial;
	protected BlockMetaPair shaftMaterial;
	protected int maxDepth = 2;
	protected int maxHeight = 50; // over ground

	// radius*2+1 will be the circumference
	protected int radius = 10;

	public Volcano(World world, int chunkX, int chunkZ, Random rand) {
		super(world, chunkX, chunkZ, rand);
		int startX = CoordHelper.chunkToMinBlock(chunkX)+MathHelper.getRandomIntegerInRange(rand, 0, 15);
		int startZ = CoordHelper.chunkToMinBlock(chunkZ)+MathHelper.getRandomIntegerInRange(rand, 0, 15);
		StructureBoundingBox bb = new StructureBoundingBox(
				startX-radius,
				startZ-radius,
				startX+radius,
				startZ+radius
			);
		this.setStructureBoundingBox(bb);
		FMLLog.info("Generating Volcano at "+startX+"/"+startZ);


	}


	@Override
	public boolean generateChunk(int chunkX, int chunkZ, Block[] blocks, byte[] metas) {
		super.generateChunk(chunkX, chunkZ, blocks, metas);

		// test first
		StructureBoundingBox chunkBB = CoordHelper.getChunkBB(chunkX, chunkZ);
		StructureBoundingBox myBB = this.getStructureBoundingBox();

		if(!chunkBB.intersectsWith(myBB)) {
			return false;
		}

		int fallbackGround = this.getWorldGroundLevel();
		if(groundLevel == -1) {
			groundLevel = getAverageGroundLevel(blocks, metas, getStructureBoundingBox(), chunkBB, fallbackGround);
			if(groundLevel == -1) {
				groundLevel = fallbackGround; // but this shouldn't even happen...
			}
		}

		int xCenter = myBB.getCenterX();
		int zCenter = myBB.getCenterZ();

		double radiusSquared = Math.pow(this.radius, 2);

		for(int x = myBB.minX; x <= myBB.maxX; x++) {
			for(int z = myBB.minZ; z <= myBB.maxZ; z++) {

				if(!chunkBB.isVecInside(x, 64, z)) {
					continue;
				}

				int xRel = x-xCenter;
				int zRel = z-zCenter;

				int sqDistance = xRel*xRel + zRel*zRel;

				if(sqDistance <= radiusSquared) {
					for(int y = this.maxDepth; y < maxHeight+groundLevel; y++) {
						this.placeBlockAbs(blocks, metas, x, y, z, chunkX, chunkZ, mountainMaterial);
					}
				}
			}

		}

		return true;
	}

	public BlockMetaPair getFluid() {
		return fluid;
	}


	public void setFluid(BlockMetaPair fluid) {
		this.fluid = fluid;
	}


	public BlockMetaPair getMountainMaterial() {
		return mountainMaterial;
	}


	public void setMountainMaterial(BlockMetaPair mountainMaterial) {
		this.mountainMaterial = mountainMaterial;
	}


	public BlockMetaPair getShaftMaterial() {
		return shaftMaterial;
	}


	public void setShaftMaterial(BlockMetaPair shaftMaterial) {
		this.shaftMaterial = shaftMaterial;
	}


	public int getMaxDepth() {
		return maxDepth;
	}


	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}


	public int getRadius() {
		return radius;
	}


	public void setRadius(int radius) {
		this.radius = radius;
	}


}
