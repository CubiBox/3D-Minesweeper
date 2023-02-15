package fr.cubibox.minesweeper.level;


import fr.cubibox.minesweeper.minesweeper.Demineur;
import fr.cubibox.minesweeper.minesweeper.PerlinNoise;

import java.io.*;
import java.util.Random;

public class Map implements Serializable {
    @Serial
    private static final long serialVersionUID = 1350092881346723535L;
    private Demineur demineur;
    private final MapLayer[] layers;
    private final int widthX;
    private final int height;
    private final int widthY;
    private int nbBombs;
    public boolean isDecorate;

    public Map(int width, int height) {
        this.widthX = width;
        this.widthY = width;
        this.height= height;

        this.layers = new MapLayer[height];
        for (int i = 0; i < height; i ++)
            layers[i] = new MapLayer(widthX,widthY);
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

    public static Map voidMap(int width, int height){
        Map map = new Map(width, height);
        map.setDemineur(new Demineur(map.getLayers()));
        return map;
    }

    public static void save(Map map) throws IOException {
        FileOutputStream file = new FileOutputStream("src\\main\\resources\\fr\\celestgames\\isoworlds\\saves\\map.ser"); // MineSweeper.class.getResource("scenes/"+str+".fxml")

        ObjectOutputStream oos = new ObjectOutputStream(file);
        oos.writeObject(map);
        oos.writeUTF("here");
        oos.flush();
        oos.close();

    }

    public void decorateMap(MapLayer[] layers) {
        this.isDecorate = true;
        Random rand = new Random();
        int deepStoneLayer = height - height/3;

        int r = rand.nextInt(2,((height*widthX)/16 > 2 ? (height*widthX)/16 : 3));
        int[][] lodes = new int[r*2][4];

        for (int i = 0; i < r; i ++) {
            int x = rand.nextInt(0, widthX);
            int y = rand.nextInt(0, widthY);
            int z = rand.nextInt(0, height);
            int size =  rand.nextInt(1,4);

            lodes[i*2] = new int[]{x,y,z,size};
            lodes[i*2+1] = new int[]{x + rand.nextInt(3),y + rand.nextInt(3),z + rand.nextInt(3),size};
        }

        for (int z = 0; z < height; z ++)
            for (int y = 0; y < widthY; y++)
                for (int x = 0; x < widthX; x++) {
                    Graphic graphic = new Graphic();
                    if (z==0 || layers[z-1].getTile(x,y).getValue() == TileType.VOID){
                        graphic.setTexture("grass_block");
                        if (rand.nextInt(2)==1) {
                            if (rand.nextInt(10) < 8)
                                graphic.setDecoration(new Decoration("grass", "grass"));
                            else if (rand.nextInt(10) < 8)
                                graphic.setDecoration(new Decoration("flower", "flowers"));
                            else
                                graphic.setDecoration(new Decoration("rock", "rocks"));
                        }
                    }
                    else if (layers[z-1].getTile(x,y).getValue() == TileType.CUBE) {
                        if (z <= 3) {
                            graphic.setTexture(
                                    (z == 3) ? ((rand.nextInt(10) < 8 ) ? "dirt" : "stone") : "dirt"
                            );
                            if (rand.nextInt(20) == 1)
                                graphic.setDecoration(new Decoration("rock","rocks"));
                        }
                        else if (z < deepStoneLayer) {
                            graphic.setTexture("stone");
                            if (rand.nextInt(10) == 1)
                                graphic.setDecoration(new Decoration("rock","rocks"));
                        }
                        else {
                            graphic.setTexture(
                                    (z == deepStoneLayer) ? ((rand.nextInt(10) < 8) ? "deepstone" : "stone") : "deepstone"
                            );
                            if (rand.nextInt(10) == 1)
                                graphic.setDecoration(new Decoration("deeprock","deeprocks"));
                        }

                        //ores
                        if (isInLode(lodes, x, y, z))
                            graphic.setTexture((z < deepStoneLayer) ? "coal_stone" : "coal_deep");

                    }
                    layers[z].getTile(x,y).setGraphic(graphic);
                }
    }

    /**
     * @param lodes list of spheres with 4 var {x,y,z,rayon}
     * @param x  coordinate x of the tile
     * @param y  coordinate y of the tile
     * @param z  coordinate z of the tile
     * @return   if the tile is in the sphere list
     */
    public boolean isInLode(int[][] lodes, int x, int y, int z){
        for (int[] lode : lodes) {
            int size = lode[3]; //as rayon

            //check if x,y and z is on the possibles limits of the lode
            if (
                    x >= 0 && x <= widthX &&
                    y >= 0 && y <= widthY &&
                    z >= 0 && z <= height -1 &&

                    x >= lode[0] - size && x <= lode[0] + size &&
                    y >= lode[1] - size && y <= lode[1] + size &&
                    z >= lode[2] - size && z <= lode[2] + size
            ) {
                int dx = lode[0] - x;
                int dy = lode[1] - y;
                int dz = lode[2] - z;

                double Lsqrt = Math.sqrt(dx * dx + dy * dy);

                if (Lsqrt * Lsqrt + dz * dz <= size * size)
                    return true;
            }
        }
        return false;
    }

    public void applyHeightMap(int seed, int height){
        byte[][] heightMap = PerlinNoise.getHeightMap(widthX,seed,height);
        Random rand = new Random();
        for (int y = 0; y < widthY; y++)
            for (int x = 0; x < widthX; x++) {
                Graphic graphic = new Graphic();
                int i = 0;
                while(i <= heightMap[y][x]) {
                    this.getLayer(i).getTile(x, y).setValue(TileType.VOID);
                    this.getLayer(i).getTile(x, y).setRevealed(true);
                    i ++;
                }
                graphic.setTexture("grass_block");
                if (rand.nextInt(2)==1) {
                    if (rand.nextInt(10) < 8)
                        graphic.setDecoration(new Decoration("grass","grass"));
                    else if (rand.nextInt(10) < 8)
                        graphic.setDecoration(new Decoration("flower","flowers"));
                    else
                        graphic.setDecoration(new Decoration("rock","rocks"));
                }
                this.getLayer(i).getTile(x, y).setGraphic(graphic);

                graphic = new Graphic();
                graphic.setTexture("dirt");
                this.getLayer(i+1).getTile(x, y).setGraphic(graphic);

                graphic = new Graphic();
                graphic.setTexture((rand.nextInt(10) < 8 ) ? "dirt" : "stone");
                this.getLayer(i+2).getTile(x, y).setGraphic(graphic);
            }
    }

    public void applyHeightMap(int height){
        applyHeightMap(new Random().nextInt(Integer.MAX_VALUE),height);
    }


    public Demineur getDemineur() {
        return demineur;
    }

    public void setDemineur(Demineur demineur) {
        this.demineur = demineur;
    }

    public int getWidthY() {
        return widthY;
    }
    public int getWidthX() {
        return widthX;
    }

    public int getLayersCount() {
        return layers.length;
    }

    public int getNbBombs() {
        return nbBombs;
    }

    public void setNbBombs(int nbBombs) {
        this.nbBombs = nbBombs;
    }

    public int getHeight() {
        return height;
    }
}