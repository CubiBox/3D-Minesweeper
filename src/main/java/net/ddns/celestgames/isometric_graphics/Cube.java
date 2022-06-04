package net.ddns.celestgames.isometric_graphics;

public class Cube {
    private int x;
    private int y;

    private int offsetY;
    private int dirY;

    public Cube() {
        this.x = 0;
        this.y = 0;

        this.offsetY = 0;
        this.dirY = 1;
    }

    public Cube(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public int getDirY() {
        return dirY;
    }

    public void setDirY(int dirY) {
        this.dirY = dirY;
    }
}
