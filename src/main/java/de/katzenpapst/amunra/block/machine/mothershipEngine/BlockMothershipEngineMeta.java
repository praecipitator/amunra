package de.katzenpapst.amunra.block.machine.mothershipEngine;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.block.BlockMachineMeta;
import net.minecraft.block.material.Material;

public class BlockMothershipEngineMeta extends BlockMachineMeta {

    public BlockMothershipEngineMeta(String name, Material material) {
        super(name, material);
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
    public int getRenderType()
    {
        return AmunRa.dummyRendererId;
    }

}
