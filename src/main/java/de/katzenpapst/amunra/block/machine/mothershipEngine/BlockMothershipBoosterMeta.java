package de.katzenpapst.amunra.block.machine.mothershipEngine;

import de.katzenpapst.amunra.block.BlockMachineMeta;
import de.katzenpapst.amunra.block.SubBlock;
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseUniversalElectrical;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockMothershipBoosterMeta extends BlockMachineMeta {

    public BlockMothershipBoosterMeta(String name, Material material) {
        super(name, material);
        // TODO Auto-generated constructor stub
    }
/*
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack itemStack)
    {
        int metadata = world.getBlockMetadata(x, y, z);
        SubBlock sb = this.getSubBlock(metadata);
        if(sb != null) {
            sb.onBlockPlacedBy(world, x, y, z, entityLiving, itemStack);
        }
    }*/

    @Override
    public boolean onUseWrench(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        return false;
    }

}
