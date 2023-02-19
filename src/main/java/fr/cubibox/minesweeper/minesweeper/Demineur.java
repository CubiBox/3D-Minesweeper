package fr.cubibox.minesweeper.minesweeper;

import fr.cubibox.minesweeper.controllers.Game;
import fr.cubibox.minesweeper.level.*;

import java.io.Serializable;
import java.util.Random;

public class Demineur implements Serializable {
    private int size;
    private int height;
    private long seed;

    public Demineur(MapLayer[] layers) {
        this.height = layers.length;
        this.size = layers[0].getWidth();

        for (int z = height-1; z >= 0; z --)
            for (int y = 0; y < size; y++)
                for (int x = 0; x < size; x++)
                    layers[z].getTiles()[y][x].setValue(TileType.CUBE);
    }

    public Demineur(MapLayer[] layers, long seed, int nbBombs) {
        this.height = layers.length;
        this.size = layers[0].getWidth();

        for (int z = height-1; z >= 0; z --)
            for (int y = 0; y < size; y++)
                for (int x = 0; x < size; x++)
                    layers[z].getTiles()[y][x].setValue(TileType.CUBE);

        setMines(layers, nbBombs, seed);
    }

    public void setMines(MapLayer[] layers, int nbBombs, int posX, int posY){
        long seed = new Random().nextLong(Long.MAX_VALUE)/10000*10000 + posX * 100L + posY;
        setMines(layers, nbBombs, seed);
    }

    public void setMines(MapLayer[] layers, int nbBombs, long seed){
        this.seed = seed;
        Random rand = new Random(seed);
        int posY = (int) Math.abs(seed%100);
        int posX = (int) Math.abs(seed%10000-posY)/100;

        System.out.println(seed + "; " + posX + "; " + posY);

        int x,y,z;
        while (nbBombs != 0) {
            x = rand.nextInt(0 ,size);
            y = rand.nextInt(0 ,size);
            z = rand.nextInt(0 ,height);
            if (!layers[z].getTiles()[y][x].isBomb() && x != posX && y != posY) {
                layers[z].getTiles()[y][x].setBomb(true);
                nbBombs --;
            }
        }
    }

    public void clearMines(MapLayer[] layers){
        for (int z = height-1; z >= 0; z --)
            for (int y = 0; y < size; y++)
                for (int x = 0; x < size; x++)
                    layers[z].getTiles()[y][x].setBomb(false);
    }

    public void rotateMinesweeperLeft(){
        for (MapLayer layer : Game.map.getLayers()) {
            int width = layer.getWidth();
            int height = layer.getHeight();

            Tile[][] newTileTab = layer.getTiles();
            for (int i = 0; i < height; i++){
                for (int j = i; j < width; j++){
                    Tile temp = newTileTab[i][j];
                    newTileTab[i][j] = newTileTab[j][i];
                    newTileTab[j][i] = temp;
                }
            }
            for (int i = 0; i< height; i++){
                for(int j = 0; j< width/2; j++){
                    Tile temp = newTileTab[i][j];
                    newTileTab[i][j] = newTileTab[i][width - 1 - j];
                    newTileTab[i][width - 1 - j] = temp;
                }
            }
            layer.setTiles(newTileTab);
        }
    }

    public void rotateMinesweeperRight(){
        rotateMinesweeperLeft();
        rotateMinesweeperLeft();
        rotateMinesweeperLeft();
    }


    public byte nbMineNeigh(int x, int y, int z){
        byte bombs = 0;
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

    public boolean reveal(int x, int y, int z){
        if (nbMineNeigh(x,y,z) == 0 && !getLayers()[z].getTiles()[y][x].isRevealed() && !isBomb(x,y,z)){
            getLayers()[z].getTiles()[y][x].setValue(TileType.VOID);
            getLayers()[z].getTiles()[y][x].setRevealed(true);
            getLayers()[z].getTiles()[y][x].setNbMine(0);
            for (int k = -1; k <= 1; k ++)
                for (int i = -1; i <= 1; i ++)
                    for (int j = -1; j <= 1; j++)
                        if(     x + j >= 0 && x + j < size
                                && y + i >= 0 && y + i < size
                                && z + k >= 0 && z + k < height)
                            reveal(x + j, y + i, z + k);
        }
        else if (!getLayers()[z].getTiles()[y][x].isRevealed() && !isBomb(x,y,z)){
            getLayers()[z].getTiles()[y][x].setNbMine(nbMineNeigh(x,y,z));
            getLayers()[z].getTiles()[y][x].setValue(TileType.VOID);
            getLayers()[z].getTiles()[y][x].setRevealed(true);
        }
        else if (isBomb(x,y,z)) {
            getLayers()[z].getTiles()[y][x].setValue(TileType.BOMB);
            return false;
        }
        return true;
    }

    public boolean isBomb(int x, int y, int z){
        return (getLayers()[z].getTiles()[y][x].isBomb());
    }

    public Tile getTile(int x, int y, int z){
        return getLayers()[z].getTile(x,y);
    }

    public void setTile(int x, int y, int z, Tile t){
        getLayers()[z].setTile(x,y,t);
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
        return Game.map.getLayers();
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }
}
