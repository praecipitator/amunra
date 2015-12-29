package de.katzenpapst.amunra.world.mapgen.volcano;

import java.util.Random;

import cpw.mods.fml.common.FMLLog;
import de.katzenpapst.amunra.world.CoordHelper;
import de.katzenpapst.amunra.world.mapgen.BaseStructureStart;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.core.perlin.generator.Gradient;
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
	protected int radius = 50;
	protected int shaftRadius = 2;
	protected int calderaRadius = 6;
	protected int falloffWidth = 9;

	protected int magmaChamberWidth;
	protected int magmaChamberHeight;
	protected Gradient testGrad;


	protected boolean hasMagmaChamber = false;


	public boolean hasMagmaChamber() {
		return hasMagmaChamber;
	}

	public void setHasMagmaChamber(boolean hasMagmaChamber) {
		this.hasMagmaChamber = hasMagmaChamber;
	}

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

		testGrad = new Gradient(this.rand.nextLong(), 4, 0.25F);
		testGrad.setFrequency(0.05F);

		calderaRadius = MathHelper.getRandomIntegerInRange(rand, 5, 7);
		shaftRadius = MathHelper.getRandomIntegerInRange(rand, 1, 3);

		radius = MathHelper.getRandomIntegerInRange(rand, 46, 56);

		magmaChamberWidth = MathHelper.getRandomIntegerInRange(rand, radius-10, radius);
		magmaChamberHeight = MathHelper.getRandomIntegerInRange(rand, radius/2, radius);

	}

	protected double getHeightFromDistance(double distance)
	{
		return maxHeight*(
				(this.radius-distance)/((double)this.radius)
			) ;
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

		double sqRadius = Math.pow(this.radius, 2);


		int maxVolcanoHeight = (maxHeight+groundLevel);

		// after this radius, falloff will be used
		int faloffRadius = radius-falloffWidth;

		// TODO: make all height variables absolute, then try to figure out
		// why the fuck it explodes with noise
		for(int x = myBB.minX; x <= myBB.maxX; x++) {
			for(int z = myBB.minZ; z <= myBB.maxZ; z++) {

				if(!chunkBB.isVecInside(x, 64, z)) {
					continue;
				}

				int lowestBlock = this.getHighestSpecificBlock(
						blocks,
						metas,
						CoordHelper.abs2rel(x, chunkX),
						CoordHelper.abs2rel(z, chunkZ),
						this.mountainMaterial.getBlock(),
						this.mountainMaterial.getMetadata()
					);
				if(lowestBlock == -1) {
					lowestBlock = maxDepth;
				}

				int xRel = x-xCenter;
				int zRel = z-zCenter;

				int sqDistance = xRel*xRel + zRel*zRel;

				double heightAtCalderaBorder = getHeightFromDistance(calderaRadius)+groundLevel;
				double fluidHeight 			 = getHeightFromDistance(shaftRadius)+groundLevel;


				if(sqDistance <= sqRadius) {
					double distance = Math.sqrt(sqDistance);

					int height;
					if(distance <= this.shaftRadius) {
						height = (int) fluidHeight;
						height = (int) (heightAtCalderaBorder-(height-heightAtCalderaBorder));
					} else {

						height = (int) getHeightFromDistance(distance)+groundLevel;

						if(distance > faloffRadius && lowestBlock < height && groundLevel > lowestBlock) {
							// somewhat of a falloff at the edges
							double faloffFactor = (distance-faloffRadius)/((double)this.falloffWidth);
							height = (int) (this.lerp(height, lowestBlock, faloffFactor));

						}

						// if we are past the caldera radius, go lower again
						if(distance <= calderaRadius) {
							height = (int) (heightAtCalderaBorder-(height-heightAtCalderaBorder));
						}


						double noise = testGrad.getNoise(x, z);




						// noise has less effect the closer to the shaft we come
						//noise *= (distance*distance)/this.radius*4;
						//noise *= (distance/radius)*18;
						noise *= 8;
						height += Math.round(noise);
					}
					// height += MathHelper.getRandomIntegerInRange(rand, -1, 1);

					if(height > 255) {
						height = 255;
					}
					if(height < lowestBlock) {
						height = lowestBlock;
					}

					//int height = (int)((1-sqDistance/sqRadius)*maxVolcanoHeight);

					if(distance < this.shaftRadius+2) {
						for(int y = maxDepth+1; y < height; y++) {

							if(distance <= this.shaftRadius) {
								this.placeBlockAbs(blocks, metas, x, y, z, chunkX, chunkZ, fluid);
							} else {
								//if(y == groundLevel+height-1) {
								//	this.placeBlockAbs(blocks, metas, x, y, z, chunkX, chunkZ, fluid);
								//} else {
									this.placeBlockAbs(blocks, metas, x, y, z, chunkX, chunkZ, this.shaftMaterial);
								//}
							}
						}

					} else {
						for(int y = lowestBlock; y < height; y++) {

							this.placeBlockAbs(blocks, metas, x, y, z, chunkX, chunkZ, mountainMaterial);

						}
					}
				}


				if(hasMagmaChamber) {
					// ellipsoid: x²/a² + y²/b² + z²/c² = 1
					for(int y = 0;y<this.magmaChamberHeight;y++) {
						if (
								(xRel*xRel/magmaChamberWidth*magmaChamberWidth +
								y*y/magmaChamberHeight*magmaChamberHeight +
								zRel*zRel/magmaChamberWidth*magmaChamberWidth) <= 1

						) {
							this.placeBlockAbs(blocks, metas, x, y+maxDepth, z, chunkX, chunkZ, fluid);
						}
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
