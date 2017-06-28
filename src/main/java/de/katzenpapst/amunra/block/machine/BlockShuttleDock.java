package de.katzenpapst.amunra.block.machine;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.block.SubBlockMachine;
import de.katzenpapst.amunra.tile.TileEntityShuttleDock;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockShuttleDock extends SubBlockMachine {

    public BlockShuttleDock(String name, String texture) {
        super(name, texture);
        // TODO Auto-generated constructor stub
    }

    public BlockShuttleDock(String name, String texture, String tool, int harvestLevel) {
        super(name, texture, tool, harvestLevel);
        // TODO Auto-generated constructor stub
    }

    public BlockShuttleDock(
            String name,
            String texture,
            String tool,
            int harvestLevel,
            float hardness,
            float resistance) {
        super(name, texture, tool, harvestLevel, hardness, resistance);
        // TODO Auto-generated constructor stub
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        return new TileEntityShuttleDock();
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public void breakBlock(World world, int x0, int y0, int z0, Block var5, int var6)
    {
        TileEntity te = world.getTileEntity(x0, y0, z0);
        if(te instanceof TileEntityShuttleDock) {
            ((TileEntityShuttleDock)te).onDestroy(te);
        }
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack itemStack)
    {
        TileEntity te = world.getTileEntity(x, y, z);
        if(te instanceof TileEntityShuttleDock) {
            ((TileEntityShuttleDock)te).onCreate(new BlockVec3(x, y, z));
        }
    }
    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderType()
    {
        return AmunRa.dummyRendererId;
    }
/*
    @Override
    public boolean canReplace(World world, int x, int y, int z, int probablySide, ItemStack stack)
    {
        return this.getSubBlock(stack.getItemDamage()).canReplace(world, x, y, z, probablySide, stack);
    }*/
    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z)
    {
        return world.getBlock(x, y, z).isReplaceable(world, x, y, z) && world.getBlock(x, y+1, z).isReplaceable(world, x, y+1, z);
    }

    @Override
    public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        TileEntity te = world.getTileEntity(x, y, z);
        if(te instanceof TileEntityShuttleDock) {
            ((TileEntityShuttleDock)te).onActivated(entityPlayer);
        }
        return true;
    }
}
