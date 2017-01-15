package de.katzenpapst.amunra.block.machine.mothershipEngine;

import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.GuiIds;
import de.katzenpapst.amunra.tile.TileEntityMothershipEngineBooster;
import de.katzenpapst.amunra.tile.TileEntityMothershipEngineBoosterIon;
import de.katzenpapst.amunra.vec.Vector3int;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class MothershipEngineBoosterIon extends MothershipEngineBoosterBase {

    public MothershipEngineBoosterIon(String name, String texture, String activeTexture) {
        super(name, texture, activeTexture);
        // TODO Auto-generated constructor stub
    }

    public MothershipEngineBoosterIon(
            String name,
            String texture,
            String activeTexture,
            String tool,
            int harvestLevel) {
        super(name, texture, activeTexture, tool, harvestLevel);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        TileEntity leTile = world.getTileEntity(x, y, z);
        if(leTile == null || !(leTile instanceof TileEntityMothershipEngineBooster)) {
            return false;
        }
        TileEntityMothershipEngineBooster tile = (TileEntityMothershipEngineBooster)leTile;

        if(tile.hasMaster()) {
            Vector3int pos = tile.getMasterPosition();

            entityPlayer.openGui(AmunRa.instance, GuiIds.GUI_MS_ION_ENGINE, world, pos.x, pos.y, pos.z);
            return true;
        }
         return false;
    }

    public MothershipEngineBoosterIon(
            String name,
            String texture,
            String activeTexture,
            String tool,
            int harvestLevel,
            float hardness,
            float resistance) {
        super(name, texture, activeTexture, tool, harvestLevel, hardness, resistance);
        // TODO Auto-generated constructor stub
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        return new TileEntityMothershipEngineBoosterIon();
    }

}
