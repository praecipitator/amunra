package de.katzenpapst.amunra.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.block.material.Material;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLogMeta extends BlockBasicMeta {

	public BlockLogMeta(String name, Material mat) {
		super(name, mat, 4); // only 4 subblocks of wood are possible
	}

	@Override
	public BlockMetaPair addSubBlock(int meta, SubBlock sb) {
		if(!(sb instanceof SubBlockWood)) {
			throw new IllegalArgumentException("BlockWoodMulti can only accept SubBlockWood");
		}
		return super.addSubBlock(meta, sb);
	}

	@Override
	public SubBlock getSubBlock(int meta) {
		return subBlocksArray[(meta & 3)]; // use only the first 2 bits, the rest is rotation
	}

	@SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta)
    {
		// /*Face 0 (Bottom Face) 	Face 1 (Top Face) 	Face 2 (Northern Face) 	Face 3 (Southern Face) 	Face 4 (Western Face) 	Face 5 (Eastern Face)*/
		int rotationMeta = (meta & 12) >> 2;

		return getSubBlock(meta).getIcon(side, rotationMeta);
    }

	@Override
	public int damageDropped(int meta)
    {
		return super.damageDropped(meta & 3);
    }

	@Override
	public boolean isWood(IBlockAccess world, int x, int y, int z)
    {
         return true;
    }

	/**
     * The type of render function that is called for this block
     */
    @Override
	public int getRenderType()
    {
        return 31; // ..?
    }

	/**
     * Called when a block is placed using its ItemBlock. Args: World, X, Y, Z, side, hitX, hitY, hitZ, block metadata
     */
    @Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
    {
        int actualMeta = metadata & 3;
        byte rotationalMeta = 0;

        switch (side)
        {
            case 0:
            case 1:
                rotationalMeta = 0;
                break;
            case 2:
            case 3:
                rotationalMeta = 8;
                break;
            case 4:
            case 5:
                rotationalMeta = 4;
        }

        return actualMeta | rotationalMeta;
    }

    @Override
	public boolean canSustainLeaves(IBlockAccess world, int x, int y, int z)
    {
        return true;
    }

}
