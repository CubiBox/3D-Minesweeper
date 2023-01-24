package fr.celestgames.isoworlds.level;

import fr.celestgames.isoworlds.minesweeper.Demineur;

public class Map {

    private Demineur demineur;
    private MapLayer[] layers;
    private final int width;
    private final int height;

    public Map(int width, int height) {
        this.width = width;
        this.height = height;
        this.layers = new MapLayer[1];
        this.layers[0] = new MapLayer(width, height);
    }

    public MapLayer[] getLayers() {
        return layers;
    }

    public MapLayer getLayer(int layerNb) {
        if (layerNb >= layers.length) {
            throw new IllegalArgumentException("Layer number is too high");
        } else if (layerNb < 0) {
            throw new IllegalArgumentException("Layer number is negative");
        }

        return layers[layerNb];
    }

    public void addNewLayer() {
        MapLayer[] temp = new MapLayer[layers.length + 1];

        for (int i = 0; i < layers.length; i++) {
            temp[i] = layers[i];
        }

        temp[layers.length] = new MapLayer(this.width, this.height);

        layers = temp;
    }

    public Demineur getDemineur() {
        return demineur;
    }

    public void setDemineur(Demineur demineur) {
        this.demineur = demineur;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getLayersCount() {
        return layers.length;
    }
}
