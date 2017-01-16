package de.katzenpapst.amunra.client;

import de.katzenpapst.amunra.vec.Vector2int;
import net.minecraft.util.ResourceLocation;

public class RingsRenderInfo {

    public ResourceLocation textureLocation;

    public int gapStart;
    public int gapEnd;

    public Vector2int textureSize = null;

    public RingsRenderInfo(ResourceLocation textureLocation, int gapStart, int gapEnd) {
        this.textureLocation = textureLocation;
        this.gapStart = gapStart;
        this.gapEnd = gapEnd;
    }

    public void setTextureSize(int x, int y) {
        textureSize = new Vector2int(x, y);
    }

}
