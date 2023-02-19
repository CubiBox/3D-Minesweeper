package fr.cubibox.minesweeper.level;

import java.io.Serializable;

public class MapLayer {
    private final int width;
    private final int height;
    private Tile[][] tiles;

    public MapLayer(int width, int height) {
        this.width = width;
        this.height = height;
        tiles = new Tile[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                tiles[i][j] = new Tile();
            }
        }
    }

    public MapLayer(Tile[][] tiles) {
        this.width = tiles.length;
        this.height = tiles.length;
        this.tiles = tiles;
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public void setTile(int x, int y, Tile tile) {
        tiles[y][x] = tile;
    }

    public Tile getTile(int x, int y) {
        return tiles[y][x];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setTiles(Tile[][] tiles) {
        this.tiles = tiles;
    }
}
