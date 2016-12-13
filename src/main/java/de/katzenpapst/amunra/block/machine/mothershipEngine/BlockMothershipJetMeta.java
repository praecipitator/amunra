package de.katzenpapst.amunra.block.machine.mothershipEngine;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.block.BlockMachineMeta;
import de.katzenpapst.amunra.block.SubBlock;
import de.katzenpapst.amunra.block.SubBlockMachine;
import de.katzenpapst.amunra.item.ItemBlockMulti;
import de.katzenpapst.amunra.item.ItemDamagePair;
import de.katzenpapst.amunra.item.ItemJet;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseUniversalElectrical;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class BlockMothershipJetMeta extends BlockMachineMeta implements IMothershipEngine {

    public BlockMothershipJetMeta(String name, Material material) {
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
    public BlockMetaPair addSubBlock(int meta, SubBlock sb) {
        if(!(sb instanceof MothershipEngineJetBase)) {
            throw new IllegalArgumentException("BlockMothershipJetMeta can only accept MothershipEngineJetBase");
        }
        return super.addSubBlock(meta, sb);
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
        if (sb != null && sb instanceof MothershipEngineJetBase)
        {
            return ((MothershipEngineJetBase)sb).getItem().getItemStack(1);
        }

        return super.getPickBlock(target, world, x, y, z);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack itemStack)
    {
        int metadata = world.getBlockMetadata(x, y, z);

        SubBlock sb = this.getSubBlock(metadata);
        if(sb != null) {
            sb.onBlockPlacedBy(world, x, y, z, entityLiving, itemStack);
        }
    }

    @Override
    public double getActualThrust(World w, int x, int y, int z, int meta) {
        return ((MothershipEngineJetBase)this.getSubBlock(meta)).getActualThrust(w, x, y, z, meta);
    }

    @Override
    public double getSpeed(World w, int x, int y, int z, int meta) {
        return ((MothershipEngineJetBase)this.getSubBlock(meta)).getSpeed(w, x, y, z, meta);
    }

    @Override
    public boolean canTravelDistance(World w, int x, int y, int z, int meta, double distance) {
        return ((MothershipEngineJetBase)this.getSubBlock(meta)).canTravelDistance(w, x, y, z, meta, distance);
    }

    @Override
    public int getDirection(World world, int x, int y, int z, int meta) {
        return this.getRotationMeta(meta);
    }

    @Override
    public double getPotentialThrust(World w, int x, int y, int z, int meta) {
        return ((MothershipEngineJetBase)this.getSubBlock(meta)).getPotentialThrust(w, x, y, z, meta);
    }


}
