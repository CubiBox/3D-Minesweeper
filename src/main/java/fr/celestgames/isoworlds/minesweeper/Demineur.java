package fr.celestgames.isoworlds.minesweeper;

import fr.celestgames.isoworlds.level.Tile;
import fr.celestgames.isoworlds.level.TileType;

import java.awt.*;
import java.util.Random;

public class Demineur {
    private int size;
    private Tile[][] tabCase;

    public Demineur(Tile[][] base) {
        this.tabCase = base;
        this.size = base.length;
        Random rand = new Random();
        for (int i = 0; i < size; i ++) {
            for (int j = 0; j < size; j++) {
                //tabCase[i][j].setBomb((rand.nextInt(0, 20) == 1));
                tabCase[i][j].setValue(TileType.CUBE);

                if (rand.nextInt(0, 10) == 1){
                    tabCase[i][j].setValue(TileType.BOMB);
                    tabCase[i][j].setBomb(true);
                }
            }
        }
    }


    public int nbMineNeigh(int x, int y){
        int bombs = 0;
        for (int i = -1; i <= 1; i ++){
            for (int j = -1; j <= 1; j++) {
                if (x+j>=0 && x+j<size && y+i>=0 && y+i<size && isBomb(x+j,y+i))
                    bombs ++;
            }
        }
        return bombs;
    }

    public void revele(int x, int y){
        if (nbMineNeigh(x,y) == 0 && !estRevele(x,y) && !isBomb(x,y)){
            tabCase[y][x].setValue(TileType.VOID);
            tabCase[y][x].setReveled(true);
            tabCase[y][x].setNbMine(0);
            for (int i = -1; i <= 1; i ++){
                for (int j = -1; j <= 1; j++) {
                    if (x+j>=0 && x+j<size && y+i>=0 && y+i<size)
                        revele(x+j,y+i);
                }
            }
        }
        else if (!estRevele(x,y) && !isBomb(x,y)){
            tabCase[y][x].setNbMine(nbMineNeigh(x,y));
            tabCase[y][x].setValue(TileType.CUBE);
            tabCase[y][x].setReveled(true);
        }
        else if (isBomb(x,y)) {
            tabCase[y][x].setValue(TileType.BOMB);
        }
    }

    public boolean isBomb(int x, int y){
        return (tabCase[y][x].isBomb());
    }
    public boolean estRevele(int x, int y){
        return (tabCase[y][x].isReveled());
    }

    public Tile[][] getTabCase() {
        return tabCase;
    }

    public void setTabCase(Tile[][] tabCase) {
        this.tabCase = tabCase;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
