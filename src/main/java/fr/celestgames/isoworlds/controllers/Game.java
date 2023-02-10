package fr.celestgames.isoworlds.controllers;

import fr.celestgames.isoworlds.MineSweeper;
import fr.celestgames.isoworlds.level.Map;
import fr.celestgames.isoworlds.level.Tile;
import fr.celestgames.isoworlds.level.TileType;
import fr.celestgames.isoworlds.minesweeper.Demineur;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.*;

public class Game implements Initializable {
    //FXML
    public Canvas canvas;
    public Button turnLeftButton;
    public Button turnRightButton;

    //global variables
    public static Map currentMap;
    private int currentLayer;
    private boolean isLose;

    //animation lists
    public ArrayList<int[]> bombs = new ArrayList<>();
    public byte[][][] particleSprite;

    //util variables
    private double TILE_SIZE = 0;

    private double A, B, C, D, A1, B1, C1, D1 = 0;

    private double xOffset = 0;
    private double yOffset = 0;


    public static Map createMinedMap(int width, int height, int nbBombs) {
        Map map = new Map(width, height);
        map.setDemineur(new Demineur(map.getLayers(), nbBombs));

        return map;
    }


    public void initScreenvalue(){
        A = 0.5 * TILE_SIZE;
        B = -0.5 * TILE_SIZE;
        C = 0.25 * TILE_SIZE;
        D = 0.25 * TILE_SIZE;

        double det = 1 / (A * D - B * C);

        A1 = D * det;
        B1 = -B * det;
        C1 = -C * det;
        D1 = A * det;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        int width = 10;   //5  8  22
        int height = 10;  //5  10  15
        int nbBombs = 60;

        /*
        if ((width*width*height)/128 <= 10)
            nbBombs = (width*width*height)/10;
        */

        particleSprite = new byte[height][width][width];
        currentLayer = 0;
        isLose = false;
        canvas.setWidth(MineSweeper.stage.getWidth());

        TILE_SIZE = (int)(canvas.getWidth()/(width + 8));
        xOffset = canvas.getWidth() / 2 ;
        yOffset = TILE_SIZE ;

        System.out.println(TILE_SIZE);

        MineSweeper.getAllSprite((int) TILE_SIZE);
        initScreenvalue();
        currentMap = createMinedMap(width, height, nbBombs);
        canvas.setHeight(currentMap.getLayersCount() * (TILE_SIZE/2) + currentMap.getWidth() * (TILE_SIZE/2) + 2 * yOffset);

        canvas.setOnMouseMoved(this::updateCanvas);
        canvas.setOnMouseClicked(this::updateMap);
        canvas.setOnScroll(this::updateLayer);
        turnLeftButton.setOnMouseClicked(this::rotateMinesweeperLeft);
        turnRightButton.setOnMouseClicked(this::rotateMinesweeperRight);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                for (;;) {
                    try {
                        Thread.sleep((isLose ? 40 : 10));
                    } catch (InterruptedException e) { throw new RuntimeException(e); }

                    if (isLose) {
                        if (!bombs.isEmpty()) {
                            particleSprite = actualizeParticle();
                            endGame();
                        }
                        else
                            System.out.println("fini"); //TODO verif si il reste des bombs

                        Platform.runLater(() -> lowUpdateCanvas());
                    }
                    else
                        Platform.runLater(() -> lowUpdateCanvas());
                }
            }
        }).start();
    }

    private void rotateMinesweeperLeft(MouseEvent mouseEvent) {
        currentMap.getDemineur().rotateMinesweeperLeft();
        lowUpdateCanvas();
    }
    private void rotateMinesweeperRight(MouseEvent mouseEvent) {
        currentMap.getDemineur().rotateMinesweeperRight();
        lowUpdateCanvas();
    }

    private void updateLayer(ScrollEvent e) {
        if (!isLose) {
            if (e.getDeltaY() > 0 && currentLayer - 1 >= 0)
                currentLayer--;
            else if (e.getDeltaY() < 0 && currentLayer + 1 < currentMap.getLayers().length)
                currentLayer++;
            lowUpdateCanvas();
        }
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
        gc.setFill(Color.rgb(10,165,200));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        drawMap(gc, -1, -1);
    }

    public void updateCanvas(MouseEvent event) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.rgb(10,165,200));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double mx = event.getX() - xOffset;
        double my = event.getY() - yOffset;

        int mapPosX = (int) (mx * A1 + my * B1);
        int mapPosY = (int) (mx * C1 + my * D1);

        drawMap(gc, mapPosX, mapPosY);
    }

    private void drawMap(GraphicsContext gc, int mapPosX, int mapPosY) {
        double TILE_SIZE_HALF = TILE_SIZE/2;
        for (int layer = currentMap.getLayersCount()-1; layer >= currentLayer; layer--) {
            for (int y = 0; y < currentMap.getHeight(); y++) {
                for (int x = 0; x < currentMap.getWidth(); x++) {
                    int posX = (int) (x * A + y * B - TILE_SIZE_HALF + xOffset);
                    int posY = (int) (x * C + y * D + TILE_SIZE_HALF * layer + yOffset);

                    Tile tile = currentMap.getLayer(layer).getTile(x, y);

                    //draw cube
                    ImageView tileSprite = MineSweeper.tileSprites.get(tile.getTexture())[tile.getGraphic().getVariation()];
                    if (tileSprite != null) {
                        gc.drawImage(tileSprite.getImage(), posX, posY);
                        if (tile.getNbMine() > 0 && layer == currentLayer && !isLose) {
                            ImageView numberSprite = MineSweeper.tileSprites.get("numbers")[tile.getNbMine()];
                            gc.drawImage(numberSprite.getImage(), posX, posY);
                        }
                    }

                    //draw decoration
                    ImageView decoSprite = null;
                    if (!tile.isRevealed()) {
                        if (tile.isMarked())
                            gc.drawImage(MineSweeper.tileSprites.get("flag")[0].getImage(), posX, posY - TILE_SIZE_HALF);
                        else if (tile.hasDecoration()) {
                            decoSprite = MineSweeper.tileSprites.get(tile.getGraphic().getDecoration().getFolder())[tile.getGraphic().getDecoration().getVariation()];
                            if (decoSprite != null)
                                gc.drawImage(decoSprite.getImage(), posX + tile.getGraphic().getDecoration().getVx(), posY - TILE_SIZE_HALF + tile.getGraphic().getDecoration().getVy());
                        }
                    }

                    //draw decoration of the layer under the current one (to fix some perspectives issues)
                    else if (layer + 1 <= currentMap.getLayersCount() - 1) {
                        tile = currentMap.getLayer(layer + 1).getTile(x, y);
                        double posY_deco = x * C + y * D + TILE_SIZE_HALF * (layer + 1) - TILE_SIZE_HALF + yOffset;

                        if (tile.isMarked()) {
                            decoSprite = MineSweeper.tileSprites.get("flag")[0];
                            gc.drawImage(decoSprite.getImage(), posX, posY_deco);
                        } else if (tile.hasDecoration()) {
                            decoSprite = MineSweeper.tileSprites.get(tile.getGraphic().getDecoration().getFolder())[tile.getGraphic().getDecoration().getVariation()];
                            if (decoSprite != null)
                                gc.drawImage(decoSprite.getImage(), posX + tile.getGraphic().getDecoration().getVx(), posY_deco + tile.getGraphic().getDecoration().getVy());
                        }
                    }

                    if (particleSprite[layer][y][x] != 0){
                        gc.drawImage(MineSweeper.tileSprites.get("explode")[particleSprite[layer][y][x] - 1].getImage(), posX, posY - TILE_SIZE_HALF);
                    }
                }
            }
        }
    }

    public void updateMap(MouseEvent event) {
        double x = event.getX() - xOffset;
        double y = event.getY() - yOffset;

        int posX = (int) (x * A1 + y * B1 - currentLayer);
        int posY = (int) (x * C1 + y * D1 - currentLayer);

        if (posX >= 0 && posY >= 0 && posY < currentMap.getHeight() && posX < currentMap.getWidth()) {
            if (event.getButton() == MouseButton.SECONDARY) {
                currentMap.getLayer(currentLayer).getTile(posX, posY).setMarked(
                        !currentMap.getLayer(currentLayer).getTile(posX, posY).isMarked()
                );
            }
            else if (event.getButton() == MouseButton.PRIMARY)
                if (!currentMap.getDemineur().reveal(posX, posY, currentLayer)) {
                    bombs.add(new int[]{currentLayer, posY, posX});
                    currentLayer = 0;
                    endGame();
                }
        }
        updateCanvas(event);
    }

    public boolean contains(ArrayList<int[]> bombs, int[] bomb){
        for (int[] i : bombs){
            if (
                    i[0] == bomb[0] &&
                    i[1] == bomb[1] &&
                    i[2] == bomb[2]
            ) return true;
        }
        return false;
    }

    public byte[][][] actualizeParticle() {
        for (int z = 0; z < currentMap.getLayersCount(); z ++) {
            for (int y = 0; y < currentMap.getHeight(); y ++) {
                for (int x = 0; x < currentMap.getWidth(); x++) {
                    if (particleSprite[z][y][x] != 0)
                        particleSprite[z][y][x] -= 1;
                }
            }
        }
        return particleSprite;
    }

    public void explode(int z, int y, int x){
        if (this.currentMap.getDemineur().getTile(x,y,z).isRevealed()){
            bombs.remove(0);
        }
        this.currentMap.getDemineur().getTile(x,y,z).setRevealed(true);
        this.currentMap.getDemineur().getTile(x,y,z).getGraphic().setVariation(8);
        int[][] lodes = {{x,y,z,x,y,z,6}};

        for (int cur_z = 0; cur_z < this.currentMap.getLayersCount(); cur_z ++)
            for (int cur_y = 0; cur_y < this.currentMap.getHeight(); cur_y ++)
                for (int cur_x = 0; cur_x < this.currentMap.getWidth(); cur_x ++) {
                    Tile t = this.currentMap.getDemineur().getTile(cur_x,cur_y,cur_z);
                    if (t.getGraphic().getVariation() > 0)
                        t.getGraphic().setVariation(t.getGraphic().getVariation() - 1);

                    if (this.currentMap.getDemineur().isInLode(lodes, cur_x,cur_y,cur_z)) {
                        if (t.isBomb() && !t.isRevealed()) {
                            t.setValue(TileType.BOMB);
                            int[] tmp = {cur_z, cur_y, cur_x};
                            if (!contains(bombs,tmp))
                                bombs.add(new int[]{cur_z, cur_y, cur_x});
                        }
                        else if (!t.isBomb())
                            t.setValue(TileType.VOID);

                        Random rand = new Random();
                        if (rand.nextInt(0,20) == 1 && particleSprite[cur_z][cur_y][cur_x]==0) particleSprite[cur_z][cur_y][cur_x] = (byte) rand.nextInt(4,8);
                        t.setRevealed(true);
                    }
                }
        if (!bombs.isEmpty())
            bombs.remove(0);
    }

    private void endGame() {
        isLose = true;
        if (!bombs.isEmpty()) {
            int[] i = bombs.get(0);
            System.out.println("perdu");

            this.currentMap.getDemineur().getTile(i[2], i[1], i[0]).setValue(TileType.BOMB);
            explode(i[0], i[1], i[2]);
        }
    }
}