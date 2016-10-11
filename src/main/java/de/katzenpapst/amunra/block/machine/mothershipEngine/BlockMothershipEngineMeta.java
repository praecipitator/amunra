package de.katzenpapst.amunra.block.machine.mothershipEngine;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.block.BlockMachineMeta;
import de.katzenpapst.amunra.block.SubBlock;
import de.katzenpapst.amunra.item.ItemBlockMulti;
import de.katzenpapst.amunra.item.ItemDamagePair;
import de.katzenpapst.amunra.item.ItemJet;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

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

    @Override
    public void register() {
        GameRegistry.registerBlock(this, null, this.getUnlocalizedName());

        for(int i=0;i<subBlocksArray.length;i++) {
            SubBlock sb = subBlocksArray[i];
            if(sb != null) {
                this.setHarvestLevel(sb.getHarvestTool(0), sb.getHarvestLevel(0), i);
            }
        }
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
    {
        int meta = world.getBlockMetadata(x, y, z);
        SubBlock sb = getSubBlock(meta);
        if (sb != null && sb instanceof MothershipEngineBase)
        {
            return ((MothershipEngineBase)sb).getItem().getItemStack(1);
        }

        return super.getPickBlock(target, world, x, y, z);
    }
}
