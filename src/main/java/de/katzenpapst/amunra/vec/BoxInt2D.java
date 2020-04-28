package de.katzenpapst.amunra.vec;

import net.minecraft.nbt.NBTTagCompound;

public class BoxInt2D {

    public int minX;
    public int minY;
    public int maxX;
    public int maxY;

    public BoxInt2D() {
        this(0,0,0,0);
    }

    public BoxInt2D(int minX, int minY, int maxX, int maxY) {
        setValues(minX, minY, maxX, maxY);
    }

    public BoxInt2D(NBTTagCompound nbt) {
        minX = nbt.getInteger("minX");
        minY = nbt.getInteger("minY");
        maxX = nbt.getInteger("maxX");
        maxY = nbt.getInteger("maxY");
    }

    public void setPositionSize(int x, int y, int width, int height) {
        this.minX = x;
        this.minY = y;

        this.maxX = x + width;
        this.maxY = y + height;
    }

    public void setValues(int minX, int minY, int maxX, int maxY) {
        if(minX <= maxX) {
            this.minX = minX;
            this.maxX = maxX;
        } else {
            this.minX = minX;
            this.maxX = minX;
        }

        if(minY <= maxY) {
            this.minY = minY;
            this.maxY = maxY;
        } else {
            this.minY = minY;
            this.maxY = minX;
        }
    }

    public NBTTagCompound toNBT() {
        NBTTagCompound nbt = new NBTTagCompound();

        nbt.setInteger("minX", minX);
        nbt.setInteger("minY", minY);
        nbt.setInteger("maxX", maxX);
        nbt.setInteger("maxY", maxY);

        return nbt;
    }

    public int getWidth() {
        return this.maxX-this.minX;
    }

    public int getHeight() {
        return this.maxY-this.minY;
    }

    public void expand(int x, int y) {
        if(x < minX) {
            minX = x;
        }
        if(x > maxX) {
            maxX = x;
        }
        if(y < minY) {
            minY = y;
        }
        if(y > maxY) {
            maxY = y;
        }
    }

    public boolean isWithin(int x, int y) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof BoxInt2D)) {
            return false;
        }

        BoxInt2D realOther = (BoxInt2D)other;

        return this.minX == realOther.minX && this.minY == realOther.minY && this.maxX == realOther.maxX && this.maxY == realOther.maxY;
    }

    @Override
    public int hashCode() {
        return minX << 24 ^ minY << 16 ^ maxX << 8 ^ maxY;
    }

}
