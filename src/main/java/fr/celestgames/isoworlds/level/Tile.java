package fr.celestgames.isoworlds.level;

public class Tile {
    private int x;
    private int y;
    private final String id;

    private final TileType type;

    public Tile(String id, TileType type) {
        this.id = id;
        this.type = type;
    }

    public Tile() {
        this.id = "void";
        this.type = TileType.VOID;
    }

    public String getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}