package de.katzenpapst.amunra.client.sound;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

public class PositionedLoopedSound extends PositionedSoundRecord {

    public PositionedLoopedSound(
            ResourceLocation res,
            float volume,
            float dafuq, // pitch, apparently
            float x,
            float y,
            float z) {
        super(res, volume, dafuq, x, y, z);
        // TODO Auto-generated constructor stub
        this.repeat = true;
    }


}
