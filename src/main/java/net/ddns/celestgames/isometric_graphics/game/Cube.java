package net.ddns.celestgames.isometric_graphics.game;

public class Cube {
    private int x;
    private int y;

    private int offsetY;
    private int dirY;

    public Cube() {
        this.offsetY = 0;
        this.dirY = 1;
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
