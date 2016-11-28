package de.katzenpapst.amunra.vec;

public class Vector3int {

    public int x;
    public int y;
    public int z;

    public Vector3int(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public int hashCode() {
        // now, what do I do for 3 values?
        return (x << 20) ^ (y << 10) ^ z;
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Vector3int)) {
            return false;
        }
        return x == ((Vector3int)other).x && y == ((Vector3int)other).y && z == ((Vector3int)other).z;
    }

}
