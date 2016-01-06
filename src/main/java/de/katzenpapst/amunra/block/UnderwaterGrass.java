package de.katzenpapst.amunra.block;

import java.util.Random;

import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class UnderwaterGrass extends SubBlockGrass {

	private BlockMetaPair dirtVersion;

	public UnderwaterGrass(String name, String textureTop, String textureSide, String textureBottom) {
		super(name, textureTop, textureSide, textureBottom);

		dirtVersion = new BlockMetaPair(Blocks.clay, (byte) 0);
	}

	@Override
	public int quantityDropped(int meta, int fortune, Random random)
	{
		return this.getDirtBlock().getBlock().quantityDropped(this.getDirtBlock().getMetadata(), fortune, random);
	}

	/**
	 * Return the block what this should revert to if the conditions are bad
	 * @return
	 */
	@Override
	public BlockMetaPair getDirtBlock() {
		return dirtVersion;
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
		// this can only live underwater
		// TODO add special check for fences, grasses etc
		// Blocks.
		Block blockAbove = world.getBlock(x, y+1, z);
		return blockAbove == Blocks.water || blockAbove == Blocks.flowing_water;
	}

	/**
	 *
	 */
	@Override
	public boolean canSpread(World world, int x, int y, int z) {
		// if it can live, then it can spread, no extra checks
		return true;
	}

}
