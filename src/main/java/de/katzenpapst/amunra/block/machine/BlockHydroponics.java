package de.katzenpapst.amunra.block.machine;

import java.util.Random;

import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.GuiIds;
import de.katzenpapst.amunra.block.SubBlockMachine;
import de.katzenpapst.amunra.tile.TileEntityHydroponics;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockHydroponics extends SubBlockMachine {


    public BlockHydroponics(String name, String sideTexture) {
        super(name, sideTexture);
    }

    @Override
    public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        entityPlayer.openGui(AmunRa.instance, GuiIds.GUI_HYDROPONICS, world, x, y, z);
        return true;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile != null && tile instanceof TileEntityHydroponics) {
            TileEntityHydroponics multiBlock = (TileEntityHydroponics) tile;
            if (multiBlock.hasMaster()) {

                multiBlock.updateMultiblock();

                world.markBlockForUpdate(x, y, z);
            }
        }
        super.onNeighborBlockChange(world, x, y, z, block);
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        return new TileEntityHydroponics();
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }


    @Override
    public String getShiftDescription(int meta)
    {
        return GCCoreUtil.translate("tile.hydroponics.description");
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block var5, int var6)
    {
        // drop harvest items
        TileEntity te = world.getTileEntity(x, y, z);
        if(te == null || !(te instanceof TileEntityHydroponics)) {
            return;
        }
        ItemStack[] harvest = ((TileEntityHydroponics)te).getHarvest();
        for(ItemStack stack: harvest) {
            if (stack != null)
            {
                Random random = new Random();
                float randX = random.nextFloat() * 0.8F + 0.1F;
                float randY = random.nextFloat() * 0.8F + 0.1F;
                float randZ = random.nextFloat() * 0.8F + 0.1F;

                while (stack.stackSize > 0)
                {
                    int randStackSize = random.nextInt(21) + 10;

                    if (randStackSize > stack.stackSize)
                    {
                        randStackSize = stack.stackSize;
                    }

                    stack.stackSize -= randStackSize;
                    EntityItem itemEntity = new EntityItem(world, x + randX, y + randY, z + randZ, new ItemStack(stack.getItem(), randStackSize, stack.getItemDamage()));

                    if (stack.hasTagCompound())
                    {
                        itemEntity.getEntityItem().setTagCompound((NBTTagCompound) stack.getTagCompound().copy());
                    }

                    float someFactor = 0.05F;
                    itemEntity.motionX = (float) random.nextGaussian() * someFactor;
                    itemEntity.motionY = (float) random.nextGaussian() * someFactor + 0.2F;
                    itemEntity.motionZ = (float) random.nextGaussian() * someFactor;
                    world.spawnEntityInWorld(itemEntity);
                }
            }
        }
    }


}
