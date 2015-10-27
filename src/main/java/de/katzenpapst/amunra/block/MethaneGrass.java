package de.katzenpapst.amunra.block;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace;
import micdoodle8.mods.galacticraft.api.world.IAtmosphericGas;
import micdoodle8.mods.galacticraft.core.util.OxygenUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class MethaneGrass extends SubBlockGrass {
	
	//blockIcon = 0 = top
	@SideOnly(Side.CLIENT)
	protected IIcon blockIconSide;
	@SideOnly(Side.CLIENT)
	protected IIcon blockIconBottom;

	
	
	public MethaneGrass(String name) {
		// blockRegistry.addObject(3, "dirt", (new BlockDirt()).setHardness(0.5F).setStepSound(soundTypeGravel).setBlockName("dirt").setBlockTextureName("dirt"));
		super(name, "amunra:methanegrass", "amunra:methanegrassside", "amunra:methanedirt");
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
	public boolean canLiveHere(World world, int x, int y, int z) {
		// now this grass can only live in a methane atmosphere
		return 
				(world.provider instanceof WorldProviderSpace) &&
				super.canLiveHere(world, x, y, z) &&
				((WorldProviderSpace)world.provider).isGasPresent(IAtmosphericGas.METHANE);
				//!OxygenUtil.testContactWithBreathableAir(world, world.getBlock(x, y+1, z), x, y, z, 0);
	}
	
	/**
	 * Return true if the conditions are right in order to spread to blocks returned by this.getDirtBlock() 
	 * no call of canLiveHere is needed
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public boolean canSpread(World world, int x, int y, int z) {
		return world.getBlockLightValue(x, y + 1, z) >= 9;
	}

}
