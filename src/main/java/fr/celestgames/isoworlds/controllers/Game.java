package fr.celestgames.isoworlds.controllers;

import fr.celestgames.isoworlds.IWApplication;
import fr.celestgames.isoworlds.level.Map;
import fr.celestgames.isoworlds.level.MapLayer;
import fr.celestgames.isoworlds.level.Tile;
import fr.celestgames.isoworlds.level.TileType;
import fr.celestgames.isoworlds.minesweeper.Demineur;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Game implements Initializable {
    public Canvas canvas;

    public Button upButton;
    public Button downButton;

    public Button turnLeftButton;
    public Button turnRightButton;

    private ImageView gridTile = new ImageView();
    private final HashMap<String, ImageView> tileSprites = new HashMap<>();
    private String selectedTile = null;


    private Map currentMap;
    private int currentLayer;
    private boolean isLose;


    private final double TILE_SIZE = 64;
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

    public static Map createMinedMap(int width, int height) {
        Map map = new Map(width, height);
        map.setDemineur(new Demineur(map.getLayers()));

        return map;
    }

    public void addToFileList(String line){
        line = line.replaceAll(".png", "");
        System.out.println(line);
        InputStream tileIS = IWApplication.class.getResourceAsStream("textures/" + line + ".png");
        if (tileIS == null) return;

        ImageView tile = new ImageView(new javafx.scene.image.Image(tileIS));
        tile.setFitWidth(32);
        tile.setFitHeight(32);
        tileSprites.put(line, tile);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentLayer = 0;
        currentMap = createMinedMap(10, 15);
        isLose = false;

        canvas.setWidth(currentMap.getWidth() * TILE_SIZE);
        canvas.setHeight(currentMap.getLayersCount() * TILE_SIZE * 2);

        xOffset = canvas.getWidth() / 2;

        File folder = new File("D:/Divers/Eclipse/3D-D-mineur/src/main/resources/fr/celestgames/isoworlds/textures");
        File[] listOfFiles = folder.listFiles();

        String line = null;
        
        assert listOfFiles != null;
        for (File file : listOfFiles) {
            if (file.isFile()) {
                addToFileList(file.getName());
            }
            else if (file.isDirectory()) {
                File[] files_intern = file.listFiles();
                int count = 0;
                assert files_intern != null;
                for (File file_intern : files_intern) {
                    if (file_intern.isFile()) {
                        addToFileList(file.getName() + "/" + file_intern.getName());
                        count ++;
                    }
                }
                //TODO make a count of textures to variation
            }
        }

        canvas.setOnMouseMoved(this::updateCanvas);
        canvas.setOnMouseClicked(this::updateMap);
        canvas.setOnScroll(this::updateLayer);

        upButton.setOnMouseClicked(this::upLayer);
        turnLeftButton.setOnMouseClicked(this::rotateMinesweeperLeft);
        turnRightButton.setOnMouseClicked(this::rotateMinesweeperRight);
        downButton.setOnMouseClicked(this::downLayer);

        lowUpdateCanvas();
    }

    private void rotateMinesweeperLeft(MouseEvent mouseEvent) {
        this.currentMap.getDemineur().rotateMinesweeperLeft();
        lowUpdateCanvas();
    }
    private void rotateMinesweeperRight(MouseEvent mouseEvent) {
        this.currentMap.getDemineur().rotateMinesweeperRight();
        lowUpdateCanvas();
    }

    private void updateLayer(ScrollEvent e) {
        if (e.getDeltaY() > 0 && currentLayer - 1 >= 0)
            currentLayer --;
       else if (e.getDeltaY() < 0 && currentLayer + 1 < currentMap.getLayers().length)
            currentLayer ++;
        lowUpdateCanvas();
    }

    public void upLayer(MouseEvent mouseEvent) {
        if (currentLayer - 1 >= 0)
            currentLayer --;
        lowUpdateCanvas();
    }
    public void downLayer(MouseEvent mouseEvent) {
        if (currentLayer + 1 < currentMap.getLayers().length)
            currentLayer ++;
        lowUpdateCanvas();
    }

    public void lowUpdateCanvas() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        drawMap(gc, -1, -1);
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
        for (int layer = currentMap.getLayersCount()-1; layer >= currentLayer; layer--) {
            for (int y = 0; y < currentMap.getHeight(); y++) {
                for (int x = 0; x < currentMap.getWidth(); x++) {
                    int posX = (int) (x * A + y * B - TILE_SIZE_HALF + xOffset);
                    int posY = (int) (x * C + y * D + TILE_SIZE_HALF * layer);

                    Tile tile = currentMap.getLayer(layer).getTile(x, y);

                    // get sprite of current tile + short if tile is top texture or not

                    ImageView tileSprite = tileSprites.get(tile.getTexture());

                    if (tileSprite != null) {
                        gc.drawImage(tileSprite.getImage(), posX, posY);
                        if (tile.getNbMine() > 0 && layer == currentLayer && !isLose) {
                            ImageView numberSprite = tileSprites.get(String.valueOf(tile.getNbMineModel()));
                            gc.drawImage(numberSprite.getImage(), posX, posY);
                        }
                    }

                    //decoration
                    ImageView decoSprite = null;
                    if (!tile.isReveled()) {
                        if (tile.isMarked())
                            gc.drawImage(tileSprites.get("flag").getImage(), posX, posY - TILE_SIZE_HALF);
                        else if (tile.hasDecoration()) {
                            decoSprite = tileSprites.get(tile.getGraphic().getDecoration().getDecorationModel());
                            if (decoSprite != null)
                                gc.drawImage(decoSprite.getImage(), posX+tile.getGraphic().getDecoration().getVx(), posY - TILE_SIZE_HALF+tile.getGraphic().getDecoration().getVy());
                        }
                    }
                    else if (layer + 1 <= currentMap.getHeight() - 1) {
                        tile = currentMap.getLayer(layer + 1).getTile(x, y);
                        double posY_deco = x * C + y * D + TILE_SIZE_HALF * (layer + 1) - TILE_SIZE_HALF;

                        if (tile.isMarked()) {
                            decoSprite = tileSprites.get("flag");
                            gc.drawImage(decoSprite.getImage(), posX+tile.getGraphic().getDecoration().getVx(), posY_deco+tile.getGraphic().getDecoration().getVy());
                        }
                        else if (tile.hasDecoration())
                            decoSprite = tileSprites.get(tile.getGraphic().getDecoration().getDecorationModel());
                            if (decoSprite != null)
                                gc.drawImage(decoSprite.getImage(), posX+tile.getGraphic().getDecoration().getVx(), posY_deco+tile.getGraphic().getDecoration().getVy());

                    }
                }
            }
        }
    }

    public void updateMap(MouseEvent event) {
        double x = event.getX() - xOffset;
        double y = event.getY();

        int posX = (int) (x * A1 + y * B1 - currentLayer);
        int posY = (int) (x * C1 + y * D1 - currentLayer);

        if (posX >= 0 && posY >= 0 && posX < currentMap.getWidth() && posY < currentMap.getHeight()) {
            if (event.getButton() == MouseButton.SECONDARY) {
                currentMap.getLayer(currentLayer).getTile(posX, posY).setMarked(
                        !currentMap.getLayer(currentLayer).getTile(posX, posY).isMarked()
                );
            }
            else if (event.getButton() == MouseButton.PRIMARY)
                if (!currentMap.getDemineur().revele(posY, posX, currentLayer))
                    endGame();
        }
        updateCanvas(event);
    }

    private void endGame() {
        for (MapLayer layer : currentMap.getLayers()){
            for (Tile[] t1 : layer.getTiles()){
                for (Tile t : t1){
                    TileType tt = t.isBomb() ? TileType.BOMB : TileType.VOID;
                    t.setValue(tt);
                    t.setReveled(true);
                }
            }
        }
        System.out.println("perdu");
        isLose = true;
    }
}