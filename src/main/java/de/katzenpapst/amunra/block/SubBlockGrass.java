package de.katzenpapst.amunra.block;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class SubBlockGrass extends SubBlock {

	@SideOnly(Side.CLIENT)
	protected IIcon blockIconSide;
	@SideOnly(Side.CLIENT)
	protected IIcon blockIconBottom;

	protected String textureSide;
	protected String textureBottom;


	public SubBlockGrass(String name, String textureTop, String textureSide, String textureBottom) {
		//super(name, textureTop);
		super(name, textureTop, "shovel", 1, 0.5F, 2.5F);
		this.textureSide = textureSide;
		this.textureBottom = textureBottom;
		this.setStepSound(Block.soundTypeGrass);
	}

	@Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
		blockIcon = par1IconRegister.registerIcon(getTextureName());
		blockIconSide = par1IconRegister.registerIcon(textureSide);
		blockIconBottom = par1IconRegister.registerIcon(textureBottom);

    }

	@SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta)
    {
		switch(side) {
		case 0:
			return blockIconBottom;
		case 1:
			return blockIcon;
		default:
			return blockIconSide;
		}
    }

	@Override
    public Item getItemDropped(int meta, Random random, int fortune)
    {
		return this.getDirtBlock().getBlock().getItemDropped(this.getDirtBlock().getMetadata(), random, fortune);
    }

	@Override
    public int damageDropped(int meta)
    {
		return this.getDirtBlock().getBlock().damageDropped(this.getDirtBlock().getMetadata());
    }

	@Override
	public int quantityDropped(Random rand)
	{
		return this.getDirtBlock().getBlock().quantityDropped(rand);
	}

	/**
	 * Return the block what this should revert to if the conditions are bad
	 * @return
	 */
	public BlockMetaPair getDirtBlock() {
		return new BlockMetaPair(Blocks.dirt, (byte) 0);
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
		// this is the vanilla check
		return !(world.getBlockLightValue(x, y + 1, z) < 4 && world.getBlockLightOpacity(x, y + 1, z) > 2);
	}

	/**
	 * Return true if the conditions are right in order for this grass block to spread.
	 * This can be considered an extension of canLiveHere; if that returned true for a block,
	 * then canSpread is called for it, and only then the neighbors are compared to this.getDirtBlock()
	 * and this.canLiveHere() is called on them.
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

	/**
	 * Called when something could grow on top of this block
	 * The coordinates are of the block ABOVE this one, they can be used right away
	 *
	 * @param world
	 * @param rand
	 * @param x
	 * @param y
	 * @param z
	 */
	public void growPlantsOnTop(World world, Random rand, int x, int y, int z) {

	}

	@Override
	public boolean dropsSelf() {
		return false;
	}

}
