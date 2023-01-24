package fr.celestgames.isoworlds.controllers;

import fr.celestgames.isoworlds.IWApplication;
import fr.celestgames.isoworlds.level.Map;
import fr.celestgames.isoworlds.level.Tile;
import fr.celestgames.isoworlds.level.TileType;
import fr.celestgames.isoworlds.minesweeper.Demineur;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Scanner;

public class MapCreator implements Initializable {
    public Canvas canvas;
    public GridPane tileList;
    public CheckBox gridShown;

    public CheckBox showLights;
    public Slider lightForce;

    private ImageView gridTile = new ImageView();
    private final HashMap<String, ImageView> tileSprites = new HashMap<>();

    private final HashMap<String, ImageView> normalSprites = new HashMap<>();
    private String selectedTile = null;


    private Map currentMap;
    private int currentLayer = 0;


    private final double TILE_SIZE = 32;
    private final double TILE_SIZE_HALF = TILE_SIZE / 2;

    private final double A = 0.5 * TILE_SIZE;
    private final double B = -0.5 * TILE_SIZE;
    private final double C = 0.25 * TILE_SIZE;
    private final double D = 0.25 * TILE_SIZE;

    private final double det = 1 / (A * D - B * C);
    private final double A1 = D * det;
    private final double B1 = -B * det;
    private final double C1 = -C * det;
    private final double D1 = A * det;

    private double xOffset = 0;

    public static Map createFilledMap(int width, int height, TileType type) {
        Map map = new Map(width, height);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                map.getLayer(0).setTile(i, j, new Tile(type));
            }
        }
        return map;
    }

    public static Map createMinedMap(int width, int height) {
        Map map = new Map(width, height);
        map.setDemineur(new Demineur(map.getLayer(0).getTiles()));

        return map;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        InputStream gridTileIS = IWApplication.class.getResourceAsStream("textures/grid.png");
        if (gridTileIS != null) {
            gridTile = new ImageView(new javafx.scene.image.Image(gridTileIS));
        }
        currentMap = createMinedMap(20, 20);

        canvas.setWidth(currentMap.getWidth() * TILE_SIZE);
        canvas.setHeight(currentMap.getHeight() * TILE_SIZE);

        xOffset = canvas.getWidth() / 2;

        try {
            InputStream tileListFileIS = IWApplication.class.getResourceAsStream("tiles.txt");

            if (tileListFileIS == null) {
                System.out.println("Error: tiles.txt not found");
                return;
            }

            Scanner scan = new Scanner(tileListFileIS);

            while (scan.hasNextLine()) {
                String line = scan.nextLine();

                InputStream tileIS = IWApplication.class.getResourceAsStream("textures/" + line + ".png");
                if (tileIS == null) break;

                ImageView tile = new ImageView(new javafx.scene.image.Image(tileIS));
                tile.setFitWidth(32);
                tile.setFitHeight(32);
                tile.setOnMouseClicked(event -> {
                    selectedTile = line;
                    System.out.println("Tile selected: " + selectedTile);
                });

                tileSprites.put(line, tile);
                tileList.add(tile, tileSprites.size(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        canvas.setOnMouseMoved(this::updateCanvas);
        canvas.setOnMouseClicked(this::updateMap);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void updateCanvas(MouseEvent event) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double mx = event.getX() - xOffset;
        double my = event.getY();

        int mapPosX = (int) (mx * A1 + my * B1);
        int mapPosY = (int) (mx * C1 + my * D1);

        drawMap(gc, mapPosX, mapPosY);
    }

    private void drawMap(GraphicsContext gc, int mapPosX, int mapPosY) {
        for (int layer = 0; layer < currentMap.getLayersCount(); layer++) {
            for (int y = 0; y < currentMap.getHeight(); y++) {
                for (int x = 0; x < currentMap.getWidth(); x++) {
                    int posX = (int) (x * A + y * B - TILE_SIZE_HALF + xOffset);
                    int posY = (int) (x * C + y * D);

                    if (selectedTile != null && mapPosX == x && mapPosY == y) {
                        gc.drawImage(tileSprites.get(selectedTile).getImage(), posX, posY);
                    } else {
                        Tile tile = currentMap.getLayer(layer).getTile(x, y);
                        ImageView tileSprite = tileSprites.get(tile.getStrValue());
                        if (tileSprite != null) {
                            gc.drawImage(tileSprite.getImage(), posX, posY);
                            if (tile.getNbMine() > 0){
                                ImageView numberSprite = tileSprites.get(String.valueOf(tile.getNbMine()));
                                gc.drawImage(numberSprite.getImage(), posX, posY);
                            }
                        }
                        else if (layer == 0 && gridShown.isSelected()) {
                            gc.drawImage(gridTile.getImage(), posX, posY);
                        }
                    }
                }
            }
        }
    }

    public void updateMap(MouseEvent event) {
        double x = event.getX() - xOffset;
        double y = event.getY();

        int posX = (int) (x * A1 + y * B1);
        int posY = (int) (x * C1 + y * D1);

        if (posX >= 0 && posY >= 0 && posX < currentMap.getWidth() && posY < currentMap.getHeight()) {
            if (selectedTile == null){
                currentMap.getDemineur().revele(posY, posX);
            }
            else
                currentMap.getLayer(currentLayer).setTile(posX, posY, new Tile(TileType.VOID));
        }
    }
}