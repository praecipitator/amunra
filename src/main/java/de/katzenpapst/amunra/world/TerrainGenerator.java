package de.katzenpapst.amunra.world;

import java.util.Random;

import de.katzenpapst.amunra.block.BlockMetaContainer;
import de.katzenpapst.amunra.helper.CoordHelper;
import net.minecraft.world.chunk.ChunkPrimer;
import micdoodle8.mods.galacticraft.core.perlin.generator.Gradient;

/**
 * Basically this thing is just one wrapper for a copy of ChunkProviderSpace::generateTerrain,
 * just that it isn't inside a ChunkProviderSpace.
 * I made this for Seth, which has basically two surfaces.
 *
 */
public class TerrainGenerator {

	protected final Gradient noiseGenBase;
	protected final Gradient noiseGenSmallHill;
	protected final Gradient noiseGenMountain;
	protected final Gradient noiseGenFeature;
	protected final Gradient noiseGenLargeFilter;
	protected final Gradient noiseGenValley;
	protected final Gradient noiseGenSmallFilter;

	protected final float  heightModifier;				// getHeightModifier
	protected final float  smallFeatureHeightModifier;	// getSmallFeatureHeightModifier
	protected final double mountainHeightMod;			// getMountainHeightModifier
	protected final double valleyHeightMod;				// getValleyHeightModifier
	protected final double seaLevel;					// getSeaLevel

	// these are constants in the original
	protected final float  mainFeatureFilterMod;
	protected final float  largeFeatureFilterMod;
	protected final float  smallFeatureFilterMod;

	protected final BlockMetaContainer stoneBlock;
	protected final BlockMetaContainer airBlock;

	protected final int maxHeight;

	protected Random rand;

	/**
	 * Full constructor
	 *
	 * @param rand						The Random object
	 * @param stoneBlock				The Block(MetaPair) to use for stone
	 * @param airBlock					The Block(MetaPair) to fill up the space above the stones
	 * @param heightMod					General terrain height, usually from getHeightModifier
	 * @param smallFeatureMod			Small hill height, usually from getSmallFeatureHeightModifier
	 * @param mountainHeightMod			Mountain height, usually from getMountainHeightModifier
	 * @param valleyHeightMod			Valley height (depth?), usually from getValleyHeightModifier
	 * @param seaLevel					Medium height(?), usually from getSeaLevel
	 * @param maxHeight					The space above the stones up to maxHeight gets filled up with airBlock.
	 * 									Also, no terrain will be generated above this
	 * @param mainFeatureFilterMod		Not sure, default = 4
	 * @param largeFeatureFilterMod		Not sure, default = 8
	 * @param smallFeatureFilterMod		Not sure, default = 8
	 */
	public TerrainGenerator(
			Random rand,
			BlockMetaContainer stoneBlock,
			BlockMetaContainer airBlock,
			float heightMod,
			float smallFeatureMod,
			double mountainHeightMod,
			double valleyHeightMod,
			double seaLevel,
			int maxHeight,
			float mainFeatureFilterMod,
			float largeFeatureFilterMod,
			float smallFeatureFilterMod
	) {
		this.rand = rand;

		this.stoneBlock = stoneBlock;
		this.airBlock = airBlock;

		this.maxHeight = maxHeight;

		this.noiseGenBase 		 = new Gradient(this.rand.nextLong(), 4, 0.25F);
        this.noiseGenSmallHill 	 = new Gradient(this.rand.nextLong(), 4, 0.25F);
        this.noiseGenMountain 	 = new Gradient(this.rand.nextLong(), 4, 0.25F);
        this.noiseGenValley 	 = new Gradient(this.rand.nextLong(), 2, 0.25F);
        this.noiseGenFeature 	 = new Gradient(this.rand.nextLong(), 1, 0.25F);
        this.noiseGenLargeFilter = new Gradient(this.rand.nextLong(), 1, 0.25F);
        this.noiseGenSmallFilter = new Gradient(this.rand.nextLong(), 1, 0.25F);

        this.heightModifier = heightMod;
        this.smallFeatureHeightModifier = smallFeatureMod;
        this.valleyHeightMod = valleyHeightMod;
        this.seaLevel = seaLevel;
        this.mountainHeightMod = mountainHeightMod;

        this.mainFeatureFilterMod = mainFeatureFilterMod;
        this.largeFeatureFilterMod = largeFeatureFilterMod;
        this.smallFeatureFilterMod = smallFeatureFilterMod;
	}

	/**
	 * "Light" constructor.
	 * The other 3 values are set to 4, 8, 8, since that's their values in ChunkProviderSpace
	 *
	 * @param rand
	 * @param stoneBlock
	 * @param airBlock
	 * @param heightMod
	 * @param smallFeatureMod
	 * @param mountainHeightMod
	 * @param valleyHeightMod
	 * @param seaLevel
	 * @param maxHeight
	 */
	public TerrainGenerator(
			Random rand,
			BlockMetaContainer stoneBlock,
			BlockMetaContainer airBlock,
			float heightMod,
			float smallFeatureMod,
			double mountainHeightMod,
			double valleyHeightMod,
			double seaLevel,
			int maxHeight
	) {
		this(rand, stoneBlock, airBlock, heightMod, smallFeatureMod, mountainHeightMod, valleyHeightMod, seaLevel, maxHeight, 4, 8, 8);
	}

	/**
	 * Even "lighter" constructor.
	 * The other 4 values are set to 255, 4, 8, 8, since that's their values in ChunkProviderSpace
	 *
	 * @param rand
	 * @param stoneBlock
	 * @param airBlock
	 * @param heightMod
	 * @param smallFeatureMod
	 * @param mountainHeightMod
	 * @param valleyHeightMod
	 * @param seaLevel
	 * @param maxHeight
	 */
	public TerrainGenerator(
			Random rand,
			BlockMetaContainer stoneBlock,
			BlockMetaContainer airBlock,
			float heightMod,
			float smallFeatureMod,
			double mountainHeightMod,
			double valleyHeightMod,
			double seaLevel
	) {
		this(rand, stoneBlock, airBlock, heightMod, smallFeatureMod, mountainHeightMod, valleyHeightMod, seaLevel, 255, 4, 8, 8);
	}


	/**
	 * Basically a clone of micdoodle8.mods.galacticraft.api.prefab.world.gen.ChunkProviderSpace.generateTerrain
	 * I just need it in a more configurable form
	 *
	 * @param chunkX
	 * @param chunkZ
	 * @param idArray
	 * @param metaArray
	 */
	public void generateTerrain(int chunkX, int chunkZ, ChunkPrimer primer)
    {
        noiseGenBase.setFrequency(0.015F);
        noiseGenSmallHill.setFrequency(0.01F);
        noiseGenMountain.setFrequency(0.01F);
        noiseGenValley.setFrequency(0.01F);
        noiseGenFeature.setFrequency(0.01F);
        noiseGenLargeFilter.setFrequency(0.001F);
        noiseGenSmallFilter.setFrequency(0.005F);

        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
            	// magic
                final double baseHeight = noiseGenBase.getNoise(chunkX * 16 + x, chunkZ * 16 + z) * heightModifier;
                final double smallHillHeight = noiseGenSmallHill.getNoise(chunkX * 16 + x, chunkZ * 16 + z) * smallFeatureHeightModifier;
                double mountainHeight = Math.abs(noiseGenMountain.getNoise(chunkX * 16 + x, chunkZ * 16 + z));
                double valleyHeight = Math.abs(noiseGenValley.getNoise(chunkX * 16 + x, chunkZ * 16 + z));
                final double featureFilter = noiseGenFeature.getNoise(chunkX * 16 + x, chunkZ * 16 + z) * mainFeatureFilterMod;
                final double largeFilter = noiseGenLargeFilter.getNoise(chunkX * 16 + x, chunkZ * 16 + z) * largeFeatureFilterMod;
                final double smallFilter = noiseGenSmallFilter.getNoise(chunkX * 16 + x, chunkZ * 16 + z) * smallFeatureFilterMod - 0.5;
                mountainHeight = this.lerp(smallHillHeight, mountainHeight * mountainHeightMod, this.fade(this.clamp(mountainHeight * 2, 0, 1)));
                valleyHeight = this.lerp(smallHillHeight, valleyHeight * valleyHeightMod - valleyHeightMod + 9, this.fade(this.clamp((valleyHeight + 2) * 4, 0, 1)));

                double yDev = this.lerp(valleyHeight, mountainHeight, this.fade(largeFilter));
                yDev = this.lerp(smallHillHeight, yDev, smallFilter);
                yDev = this.lerp(baseHeight, yDev, featureFilter);

                for (int y = 0; y <= this.maxHeight; y++)
                {
                	int index = CoordHelper.getIndex(x, y, z);
                    if (y < seaLevel + yDev)
                    {
                        primer.setBlockState(index, stoneBlock.getBlockState());
                    } else {
                        primer.setBlockState(index, airBlock.getBlockState());
                    }
                }
            }
        }
    }

	protected double lerp(double d1, double d2, double t)
    {
        if (t < 0.0)
        {
            return d1;
        }
        else if (t > 1.0)
        {
            return d2;
        }
        else
        {
            return d1 + (d2 - d1) * t;
        }
    }

	protected double clamp(double x, double min, double max)
    {
        if (x < min)
        {
            return min;
        }
        if (x > max)
        {
            return max;
        }
        return x;
    }

	protected double fade(double n)
    {
        return n * n * n * (n * (n * 6 - 15) + 10);
    }
}
