package de.katzenpapst.amunra.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import micdoodle8.mods.galacticraft.api.block.IPartialSealableBlock;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockMachineMetaDummyRender extends BlockMachineMeta implements IPartialSealableBlock {

    public BlockMachineMetaDummyRender(String name, Material material) {
        super(name, material);
        // TODO Auto-generated constructor stub
    }

    public BlockMachineMetaDummyRender(String name, Material material, int numSubBlocks) {
        super(name, material, numSubBlocks);
        // TODO Auto-generated constructor stub
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

    @Override
    public boolean isSealed(World world, int x, int y, int z, ForgeDirection direction) {
        return true;
    }

}
