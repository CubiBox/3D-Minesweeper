package fr.celestgames.isoworlds.minesweeper;

import fr.celestgames.isoworlds.level.MapLayer;
import fr.celestgames.isoworlds.level.Tile;
import fr.celestgames.isoworlds.level.TileType;

import java.awt.*;
import java.util.Random;

public class Demineur {
    private int size;
    private int height;
    private MapLayer[] layers;

    public Demineur(MapLayer[] layers) {
        this.layers = layers;
        this.height = layers.length;
        this.size = layers[0].getWidth();

        Random rand = new Random();
        for (int k = 0; k < height; k ++) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    //tabCase[i][j].setBomb((rand.nextInt(0, 20) == 1));
                    layers[k].getTiles()[i][j].setValue(TileType.CUBE);

                    if (rand.nextInt(0, 20) == 1) {
                        layers[k].getTiles()[i][j].setValue(TileType.CUBE);
                        layers[k].getTiles()[i][j].setBomb(true);
                    }
                }
            }
        }
    }

    public void rotateMinesweeperLeft(){
        for (MapLayer layer : layers){
            int width = layer.getWidth();
            int height = layer.getHeight();

            Tile[][] newTileTab = new Tile[width][height];
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    newTileTab[i][j] = layer.getTiles()[width-i][j];
                }
            }
            layer.setTiles(newTileTab);
        }
    }


    public int nbMineNeigh(int x, int y, int z){
        int bombs = 0;
        for (int k = -1; k <= 1; k ++) {
            for (int i = -1; i <= 1; i ++){
                for (int j = -1; j <= 1; j++) {
                    if (x + j >= 0 && x + j < size
                            && y + i >= 0 && y + i < size
                            && z + k >= 0 && z + k < height
                            && isBomb(x + j, y + i, z + k))
                        bombs++;
                }
            }
        }
        return bombs;
    }

    public void revele(int x, int y, int z){
        if (nbMineNeigh(x,y,z) == 0 && !estRevele(x,y,z) && !isBomb(x,y,z)){
            layers[z].getTiles()[y][x].setValue(TileType.VOID);
            layers[z].getTiles()[y][x].setReveled(true);
            layers[z].getTiles()[y][x].setNbMine(0);
            for (int k = -1; k <= 1; k ++) {
                for (int i = -1; i <= 1; i ++){
                    for (int j = -1; j <= 1; j++) {
                        if (x + j >= 0 && x + j < size
                                && y + i >= 0 && y + i < size
                                && z + k >= 0 && z + k < height)
                            revele(x + j, y + i, z + k);
                    }
                }
            }
        }
        else if (!estRevele(x,y,z) && !isBomb(x,y,z)){
            layers[z].getTiles()[y][x].setNbMine(nbMineNeigh(x,y,z));
            layers[z].getTiles()[y][x].setValue(TileType.VOID);
            layers[z].getTiles()[y][x].setReveled(true);
        }
        else if (isBomb(x,y,z)) {
            layers[z].getTiles()[y][x].setValue(TileType.BOMB);
            System.out.println("tes nul");
            System.out.println(System.currentTimeMillis());
        }
    }

    public boolean isBomb(int x, int y, int z){
        return (layers[z].getTiles()[y][x].isBomb());
    }
    public boolean estRevele(int x, int y, int z){
        return (layers[z].getTiles()[y][x].isReveled());
    }


    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public MapLayer[] getLayers() {
        return layers;
    }

    public void setLayers(MapLayer[] layers) {
        this.layers = layers;
    }
}
