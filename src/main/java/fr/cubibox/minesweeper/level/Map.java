package fr.cubibox.minesweeper.level;


import fr.cubibox.minesweeper.MineSweeper;
import fr.cubibox.minesweeper.minesweeper.Demineur;
import fr.cubibox.minesweeper.minesweeper.PerlinNoise;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Map {
    private Demineur demineur;
    private final MapLayer[] layers;
    private final int widthX;
    private final int height;
    private final int widthY;
    private int nbBombs;
    private long graphicSeed;

    public Map(int width, int height) {
        this.widthX = width;
        this.widthY = width;
        this.height= height;

        this.layers = new MapLayer[height];
        for (int i = 0; i < height; i ++)
            layers[i] = new MapLayer(widthX,widthY);
    }

    public Map(long graphicSeed, long generationSeed, int width, int height, int nbBombs, MapLayer[] layers) {
        this.widthX = width;
        this.widthY = width;
        this.height= height;
        this.nbBombs = nbBombs;
        this.layers = layers;
        this.graphicSeed = graphicSeed;

        this.demineur = new Demineur(this.layers, generationSeed, nbBombs);
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

    public void decorateMap(MapLayer[] layers) {
        decorateMap(layers,new Random().nextLong(Long.MAX_VALUE));
    }

    public void decorateMap(MapLayer[] layers, long seed) {
        Random rand = new Random(seed);
        this.graphicSeed = seed;
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
                    if (z==0){
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
                            graphic.setTexture((z == 3) ? ((rand.nextInt(10) < 8 ) ? "dirt" : "stone") : "dirt");
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
                    z >= 0 && z <= height-1 &&

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

    public void applyHeightMap(int height){
        applyHeightMap(new Random().nextInt(Integer.MAX_VALUE),height);
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

    public void save(String name) {
        StringBuilder output = new StringBuilder();
        StringBuilder head = new StringBuilder();
        StringBuilder mapLayer = new StringBuilder();
        StringBuilder tilesY;   //Tile[][]
        StringBuilder tilesX;   //Tile[]
        StringBuilder tileStr;  //Tile
        int indent = 0;

        head.append("\"width\":").append(widthX).append(",\n")
            .append("\"height\":").append(height).append(",\n")
            .append("\"nbBombs\":").append(nbBombs).append(",\n")
            .append("\"generationSeed\":").append(demineur.getSeed()).append(",\n")
            .append("\"graphicSeed\":").append(graphicSeed).append(",\n");

        mapLayer.append("\"MapLayer\":[\n");
        for (int z = 0; z < height; z ++){
            tilesY = new StringBuilder("\t".repeat(Math.max(0, z==0 ?++indent:indent)) + "[\n");
            for (int y = 0; y < widthY; y ++){
                tilesX = new StringBuilder("\t".repeat(Math.max(0, y==0 ?++indent:indent)) + "[");
                for (int x = 0; x < widthX; x ++){
                    Tile tile = layers[z].getTile(x,y);
                    tileStr = new StringBuilder("{");
                    tileStr.append(tile.isRevealed()?"1":"0").append(",").append(tile.isMarked()?"1":"0");
                    tilesX.append(tileStr).append(x+1 >= widthX ? "}" : "},");
                }
                tilesY.append(tilesX).append(y+1>=widthY ? "]" : "],").append('\n');
            }
            mapLayer.append(tilesY).append("\t".repeat(Math.max(0, --indent))).append(z+1>=height ? "]" : "],").append('\n');
        }
        mapLayer.append(']');

        try {
            FileWriter file = new FileWriter("saves/"+name+".ser");
            file.write(output.append(head).append(mapLayer).toString());
            file.close();
        }
        catch (IOException ignored) { }
    }

    public static Map recover(String name) {
        return recover(new File("saves/"+name+".ser"));
    }

    public static Map recover(File map) {
        StringBuilder input = new StringBuilder();
        try{
            Scanner reader = new Scanner(map);
            while(reader.hasNextLine())
                input.append(reader.nextLine()).append("\n");

            String str = input.toString();

            int width = 0;
            int height = 0;
            int nbBombs = 0;
            long graphicSeed = 0;
            long generationSeed = 0;

            //header
            Matcher head = Pattern.compile("\"(\\w+)\":(\\d+),").matcher(str);

            while (head.find()) {
                switch (head.group(1)){
                    case "width" -> width = Integer.parseInt(head.group(2));
                    case "height" -> height = Integer.parseInt(head.group(2));
                    case "nbBombs" -> nbBombs = Integer.parseInt(head.group(2));
                    case "generationSeed" -> generationSeed = Long.parseLong(head.group(2));
                    case "graphicSeed" -> graphicSeed = Long.parseLong(head.group(2));
                    default -> {}
                }
            }

            MapLayer[] layers = new MapLayer[height];
            Matcher layersRegex = Pattern.compile("\\[(\\s*\\[(\\s*\\{\\d,\\d},?\\s*)*],?\\s*)*],?").matcher(str);

            for (int z = 0; layersRegex.find(); z++) {
                Matcher tilesRegex = Pattern.compile("\\[(\\s*\\{\\d,\\d},?\\s*)*],?").matcher(layersRegex.group());

                Tile[][] tiles = new Tile[width][width];
                for (int y = 0; tilesRegex.find() && y<width; y++) {
                    Matcher tile = Pattern.compile("\\{(\\d),(\\d)},?").matcher(tilesRegex.group());

                    for (int x = 0; tile.find(); x++)
                        tiles[y][x] = new Tile(tile.group(1).equals("1"), tile.group(2).equals("1"));
                }
                layers[z] = new MapLayer(tiles);
            }

            Map finalMap = new Map(graphicSeed, generationSeed, width, height, nbBombs, layers);
            finalMap.decorateMap(finalMap.getLayers(),graphicSeed);

            for (int z = 0; z < height; z++) {
                for (int y = 0; y < width; y++)
                    for (int x = 0; x < width; x++)
                        if (layers[z].getTile(x, y).isRevealed()) {
                            layers[z].getTile(x, y).setValue(TileType.VOID);
                            byte bombs = 0;
                            for (int k = -1; k <= 1; k++)
                                for (int i = -1; i <= 1; i++)
                                    for (int j = -1; j <= 1; j++)
                                        if (x + j >= 0 && x + j < width
                                                && y + i >= 0 && y + i < width
                                                && z + k >= 0 && z + k < height
                                                && finalMap.getLayers()[z + k].getTiles()[y + i][x + j].isBomb())
                                            bombs++;
                            layers[z].getTile(x, y).setNbMine(bombs);
                        }
            }
            return finalMap;
        }
        catch (Exception e){
            System.out.println("wrong format");
            System.out.println(e.getMessage());
            System.out.println(e.getLocalizedMessage());
        }
        return null;
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
    public int getHeight() {
        return height;
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

    public static void main(String[] args) {
        Map easyMap = Map.voidMap(4,4);
        easyMap.setNbBombs(5);

        MineSweeper.getAllSprite(64);
        easyMap.decorateMap(easyMap.getLayers());
        easyMap.setDemineur(new Demineur(easyMap.getLayers()));
        easyMap.getDemineur().setMines(easyMap.getLayers(), easyMap.nbBombs, 84,36);

        easyMap.save("test");

        Map map = recover("test");

        assert map != null;
        map.save("test");
    }

    public long getGraphicSeed() {
        return graphicSeed;
    }
    public void setGraphicSeed(long graphicSeed) {
        this.graphicSeed = graphicSeed;
    }

    public long getGenerationSeed() {
        return this.demineur.getSeed();
    }
    public void setGenerationSeed(long generationSeed) {
        this.demineur.setSeed(generationSeed);
    }

}
