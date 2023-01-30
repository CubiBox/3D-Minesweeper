package fr.celestgames.isoworlds.level;

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

    public Tile[][] getTiles() {
        return tiles;
    }

    public void setTile(int x, int y, Tile tile) {
        tiles[x][y] = tile;
    }

    public Tile getTile(int x, int y) {
        return tiles[x][y];
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
