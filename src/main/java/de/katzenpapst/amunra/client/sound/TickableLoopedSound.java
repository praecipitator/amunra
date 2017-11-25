package de.katzenpapst.amunra.client.sound;

import net.minecraft.client.audio.MovingSound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TickableLoopedSound extends MovingSound {

    protected TileEntity tile;

    public TickableLoopedSound(TileEntity tile, ResourceLocation res) {
        super(res);
        this.tile = tile;
        this.volume = 10.0F; // volume
        this.pitch = 1.0F; // WTF
        this.xPosF = tile.getPos().getX()+0.5F;
        this.yPosF = tile.getPos().getY()+0.5F;
        this.zPosF = tile.getPos().getZ()+0.5F;
        this.repeat = true;
    }

    @Override
    public void update() {
        if(tile == null || tile.isInvalid() || (tile instanceof ISoundableTile && ((ISoundableTile)tile).isDonePlaying())) {
            this.donePlaying = true;
        }
    }

}
