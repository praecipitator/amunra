package de.katzenpapst.amunra.block.machine;

import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.GuiIds;
import de.katzenpapst.amunra.block.SubBlockMachine;
import de.katzenpapst.amunra.tile.TileEntityGravitation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockGravitation extends SubBlockMachine {

    public BlockGravitation(String name, String texture) {
        super(name, texture);
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        return new TileEntityGravitation();
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        entityPlayer.openGui(AmunRa.instance, GuiIds.GUI_GRAVITY, world, x, y, z);
        return true;
    }

}
