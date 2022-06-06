package net.ddns.celestgames.isometric_graphics.game;

import java.util.ArrayList;

public class Map {
    private Cube map[][];

    public Map(int width, int height) {
        map = new Cube[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                map[i][j] = new Cube();
            }
        }
    }

    public Cube[][] getMap() {
        return map;
    }

    public void setCube(int x, int y, Cube cube) {
        map[x][y] = cube;
    }

    public Cube getCube(int x, int y) {
        return map[x][y];
    }

    public int getWidth() {
        return map.length;
    }

    public int getHeight() {
        return map[0].length;
    }


}
