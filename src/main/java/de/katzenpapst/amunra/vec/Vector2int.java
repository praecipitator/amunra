package de.katzenpapst.amunra.vec;

public class Vector2int {

    public int x;
    public int y;

    public Vector2int(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int hashCode() {
        // Should work somewhat good if I don't go too far, I guess
        return (x << 16) ^ y;
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Vector2int)) {
            return false;
        }
        return x == ((Vector2int)other).x && y == ((Vector2int)other).y;
    }

}
