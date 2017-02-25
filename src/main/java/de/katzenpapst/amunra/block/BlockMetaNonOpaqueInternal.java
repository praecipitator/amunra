package de.katzenpapst.amunra.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BlockMetaNonOpaqueInternal extends BlockBasicMeta {

    public BlockMetaNonOpaqueInternal(String name, Material mat, int numSubBlocks) {
        super(name, mat, numSubBlocks);
    }

    public BlockMetaNonOpaqueInternal(String name, Material mat) {
        super(name, mat);
    }


    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public boolean isBlockNormalCube()
    {
        return false;
    }

    @Override
    public boolean isNormalCube()
    {
        return false;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTabToDisplayOn()
    {
        return null;
    }
}
