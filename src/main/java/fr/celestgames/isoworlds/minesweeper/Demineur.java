package fr.celestgames.isoworlds.minesweeper;

import fr.celestgames.isoworlds.level.*;

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
        for (int z = 0; z < height; z ++) {
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    layers[z].getTiles()[y][x].setBomb((rand.nextInt(0, 20) == 1));
                    layers[z].getTiles()[y][x].setValue(TileType.CUBE);
                }
            }
        }
        decorateMap();
    }

    private void decorateMap() {
        Random rand = new Random();

        int deepstoneLayer = this.getHeight()-this.getHeight()/3;

        int r = rand.nextInt(2,this.height/4);
        int[][] lodes = new int[r][7];

        for (int i = 0; i < r; i ++) {
            int tmp_x = rand.nextInt(0, this.size);
            int tmp_y = rand.nextInt(0, this.size);
            int tmp_z = rand.nextInt(0, this.height);

            int[] lode = {
                    tmp_x,
                    tmp_y,
                    tmp_z,
                    tmp_x + rand.nextInt(2),
                    tmp_y + rand.nextInt(2),
                    tmp_z + rand.nextInt(2),
                    rand.nextInt(1,4)
            };
            lodes[i] = lode;
        }

        for (int z = 0; z < height; z ++) {
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    Graphic graphic = new Graphic();
                    if (z==0 || this.getTile(x,y,z-1).getValue() == TileType.VOID){
                        graphic.setTexture("grass_block");
                        if (rand.nextInt(2)==1)
                            graphic.setDecoration(new Decoration(
                                    (rand.nextInt(10) < 8) ? "grass" : "flower"
                            ));
                    }
                    else if (this.getTile(x,y,z-1).getValue() == TileType.CUBE) {
                        if (z <= 3) {
                            graphic.setTexture(
                                    (z == 3) ? (
                                            (rand.nextInt(10) < 8 ) ? "dirt" : "stone"
                                    ) : "dirt"
                            );
                            if (rand.nextInt(20) == 1)
                                graphic.setDecoration(new Decoration("rock"));
                        }
                        else if (z < deepstoneLayer) {
                            graphic.setTexture("stone");
                            if (rand.nextInt(10) == 1)
                                graphic.setDecoration(new Decoration("rock"));
                        }
                        else
                            graphic.setTexture(
                                (z == deepstoneLayer) ? (
                                        (rand.nextInt(10) < 8 ) ? "deepstone" : "stone"
                                ) : "deepstone"
                        );


                        //ores
                        if (isInLode(lodes, x, y, z))
                            graphic.setTexture((z < deepstoneLayer) ? "coal_stone" : "coal_deep");

                    }
                    getTile(x,y,z).setGraphic(graphic);
                }
            }
        }
    }

    public boolean isInLode(int[][] lodes, int x, int y, int z){
        for (int[] lode : lodes) {
            int fs = lode[6];
            if (
                    x >= 0 && x <= this.size &&
                    y >= 0 && y <= this.size &&
                    z >= 0 && z <= this.height &&

                    ((x >= lode[0] - fs && x <= lode[0] + fs) || (x >= lode[3] - fs && x <= lode[4] + fs)) &&
                    ((y >= lode[1] - fs && y <= lode[1] + fs) || (y >= lode[3] - fs && y <= lode[4] + fs)) &&
                    ((z >= lode[2] - fs && z <= lode[2] + fs) || (z >= lode[3] - fs && z <= lode[4] + fs))
            ) {
                int dx = lode[0] - x;
                int dy = lode[1] - y;
                int dz = lode[2] - z;

                int dx2 = lode[3] - x;
                int dy2 = lode[4] - y;
                int dz2 = lode[5] - z;

                double Lsqrt = Math.sqrt(dx * dx + dy * dy);
                double Lsqrt2 = Math.sqrt(dx2 * dx2 + dy2 * dy2);
                if (Lsqrt * Lsqrt + dz * dz <= fs * fs)
                    return true;
                else if (Lsqrt2 * Lsqrt2 + dz2 * dz2 <= fs * fs){
                    return true;
                }
            }
        }
        return false;
    }

    public void rotateMinesweeperLeft(){
        for (MapLayer layer : layers) {
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

    public boolean revele(int x, int y, int z){
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
            return false;
        }
        return true;
    }

    public boolean isBomb(int x, int y, int z){
        return (layers[z].getTiles()[y][x].isBomb());
    }
    public boolean estRevele(int x, int y, int z){
        return (layers[z].getTiles()[y][x].isReveled());
    }

    public Tile getTile(int x, int y, int z){
        return layers[z].getTile(x,y);
    }

    public void setTile(int x, int y, int z, Tile t){
        layers[z].setTile(x,y,t);
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
