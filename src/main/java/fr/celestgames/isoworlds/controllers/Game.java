package fr.celestgames.isoworlds.controllers;

import fr.celestgames.isoworlds.MineSweeper;
import fr.celestgames.isoworlds.level.Map;
import fr.celestgames.isoworlds.level.Tile;
import fr.celestgames.isoworlds.level.TileType;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Game implements Initializable {
    //FXML
    public Canvas canvas;
    public Button turnLeftButton;
    public Button turnRightButton;

    //global variables
    public static Map map;
    public CheckBox showBombs;
    public ScrollPane scrollPane;
    public Button save;
    public Button menuButton;
    private int currentLayer;

    /**  gameState
     *      0 : Initialize
     *      1 : inGame
     *      2 : inLose
     *      3 : end
     */
    private byte gameSate;

    //animation lists
    public ArrayList<int[]> bombs = new ArrayList<>();
    public byte[][][] particleSprite;

    //util variables
    private double TILE_SIZE = 0;

    private double A, B, C, D, A1, B1, C1, D1 = 0;

    private double xOffset = 0;
    private double yOffset = 0;

    public void initScreenValue(){
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
        map = MineSweeper.map;

        int width = map.getWidthX();
        int height = map.getLayersCount();

        //initialize game variables
        particleSprite = new byte[height][width][width];
        currentLayer = 0;
        setInitialize();
        canvas.setWidth(MineSweeper.stage.getWidth());

        TILE_SIZE = (int)(canvas.getWidth()/(width + 8));
        xOffset = canvas.getWidth() / 2 ;
        yOffset = TILE_SIZE ;

        MineSweeper.getAllSprite((int) TILE_SIZE);
        initScreenValue();

        if (!map.isDecorate)
            map.decorateMap(map.getLayers());

        canvas.setHeight(map.getLayersCount() * (TILE_SIZE/2) + map.getWidthX() * (TILE_SIZE/2) + 2 * yOffset);

        scrollPane.setMinHeight(map.getLayersCount() * (TILE_SIZE/2) + map.getWidthX() * (TILE_SIZE/2) + 2 * yOffset);
        scrollPane.setMinWidth(MineSweeper.stage.getWidth());

        canvas.setOnMouseMoved(this::updateCanvas);
        canvas.setOnMouseClicked(this::updateMap);
        canvas.setOnScroll(this::updateLayer);
        turnLeftButton.setOnMouseClicked(this::rotateMinesweeperLeft);
        turnRightButton.setOnMouseClicked(this::rotateMinesweeperRight);
        menuButton.setOnMouseClicked(this::mainMenu);
        showBombs.setOnMouseClicked(this::updateMap);
        save.setOnMouseClicked(this::save);

        animationThread();
    }

    public void animationThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (;;) {
                    try { Thread.sleep((isInLose() ? 50 : 10));} catch (InterruptedException ignore) { }

                    if (isInLose()) {
                        if (!bombs.isEmpty() || animationsLeft() || bombLeft()) {
                            actualizeParticle();
                            endGame();
                        }
                        else {
                            System.out.println("fini");
                            setEnd();
                        }
                        Platform.runLater(() -> updateCanvas());
                    }
                    else Platform.runLater(() -> updateCanvas());
                }
            }
        }).start();
    }

    private void rotateMinesweeperLeft(MouseEvent mouseEvent) {
        map.getDemineur().rotateMinesweeperLeft();
        updateCanvas();
    }

    private void mainMenu(MouseEvent mouseEvent) {
        MineSweeper.mainMenu();
    }

    void save(MouseEvent mouseEvent) {
        try {
            Map.save(map);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void rotateMinesweeperRight(MouseEvent mouseEvent) {
        map.getDemineur().rotateMinesweeperRight();
        updateCanvas();
    }

    private void updateLayer(ScrollEvent e) {
        if (isInGame()) {
            if (e.getDeltaY() > 0 && currentLayer - 1 >= 0)
                currentLayer--;
            else if (e.getDeltaY() < 0 && currentLayer + 1 < map.getLayers().length)
                currentLayer++;
            updateCanvas();
        }
    }

    public void upLayer(MouseEvent mouseEvent) {
        if (currentLayer - 1 >= 0)
            currentLayer --;
        updateCanvas();
    }
    public void downLayer(MouseEvent mouseEvent) {
        if (currentLayer + 1 < map.getLayers().length)
            currentLayer ++;
        updateCanvas();
    }

    public void updateCanvas() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.rgb(10,165,200));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        drawMap(gc);
    }

    public void updateCanvas(MouseEvent event) {
        updateCanvas();
    }

    private void drawMap(GraphicsContext gc) {
        double TILE_SIZE_HALF = TILE_SIZE/2;
        for (int layer = map.getLayersCount()-1; layer >= currentLayer; layer--) {
            for (int y = 0; y < map.getWidthY(); y++) {
                for (int x = 0; x < map.getWidthX(); x++) {
                    int posX = (int) (x * A + y * B - TILE_SIZE_HALF + xOffset);
                    int posY = (int) (x * C + y * D + TILE_SIZE_HALF * layer + yOffset);

                    if (showBombs.isSelected()){
                        Tile tile = map.getLayer(layer).getTile(x, y);
                        if (tile.isBomb()) {
                            gc.drawImage(MineSweeper.tileSprites.get("bomb")[2].getImage(),posX, posY);
                        }
                        else {
                            if (layer == map.getLayersCount()-1)
                                gc.drawImage(MineSweeper.tileSprites.get("bases")[0].getImage(), posX, posY);
                            if (x == 0)
                                gc.drawImage(MineSweeper.tileSprites.get("bases")[1].getImage(), posX, posY);
                            if (y == 0)
                                gc.drawImage(MineSweeper.tileSprites.get("bases")[2].getImage(), posX, posY);
                        }
                    }


                    else if (!isEnd()) {
                        Tile tile = map.getLayer(layer).getTile(x, y);

                        //draw cube
                        ImageView tileSprite = MineSweeper.tileSprites.get(tile.getTexture())[tile.getGraphic().getVariation()];
                        if (tileSprite != null) {
                            gc.drawImage(tileSprite.getImage(), posX, posY);
                            if (tile.getNbMine() > 0 && layer == currentLayer && isInGame()) {
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
                        else if (layer + 1 <= map.getLayersCount() - 1 && !map.getLayer(layer + 1).getTile(x, y).isRevealed()) {
                            tile = map.getLayer(layer + 1).getTile(x, y);
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


                        if (particleSprite[layer][y][x] != 0) {
                            gc.drawImage(MineSweeper.tileSprites.get("explode")[particleSprite[layer][y][x] - 1].getImage(), posX, posY - TILE_SIZE_HALF);
                        }
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

        if (posX >= 0 && posY >= 0 && posY < map.getWidthY() && posX < map.getWidthX()) {
            if (event.getButton() == MouseButton.SECONDARY) {
                map.getLayer(currentLayer).getTile(posX, posY).setMarked(
                        !map.getLayer(currentLayer).getTile(posX, posY).isMarked()
                );
            }
            else if (event.getButton() == MouseButton.PRIMARY) {
                if (isInitialize()) {
                    map.getDemineur().setMines(map.getLayers(), map.getNbBombs(), posX, posY);
                    setInGame();
                }
                if (!map.getDemineur().reveal(posX, posY, currentLayer)) {
                    bombs.add(new int[]{currentLayer, posY, posX});
                    currentLayer = 0;
                    endGame();
                }
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

    public void actualizeParticle() {
        for (int z = 0; z < map.getLayersCount(); z ++)
            for (int y = 0; y < map.getWidthY(); y ++)
                for (int x = 0; x < map.getWidthX(); x++)
                    if (particleSprite[z][y][x] != 0)
                        particleSprite[z][y][x] -= 1;
    }

    public boolean animationsLeft(){
        for (int z = 0; z < map.getLayersCount(); z ++)
            for (int y = 0; y < map.getWidthY(); y ++)
                for (int x = 0; x < map.getWidthX(); x++)
                    if (particleSprite[z][y][x] != 0 || map.getDemineur().getTile(x,y,z).getGraphic().getVariation() > 0)
                        return true;
        return false;
    }

    public boolean bombLeft(){
        for (int z = 0; z < map.getLayersCount(); z ++)
            for (int y = 0; y < map.getWidthY(); y ++)
                for (int x = 0; x < map.getWidthX(); x++) {
                    Tile t = map.getDemineur().getTile(x, y, z);
                    if (t.isBomb() && !t.isRevealed()){
                        t.setValue(TileType.BOMB);
                        int[] tmp = {z, y, x};
                        if (!contains(bombs, tmp))
                            bombs.add(tmp);
                        t.setRevealed(true);
                        return true;
                    }
                }
        return false;
    }

    public void explode(int posZ, int posY, int posX){
        if (posZ > 0 && posY > 0 && posX > 0) {
            if (map.getDemineur().getTile(posX, posY, posZ).isRevealed() && !bombs.isEmpty())
                bombs.remove(0);

            map.getDemineur().getTile(posX, posY, posZ).setRevealed(true);
            map.getDemineur().getTile(posX, posY, posZ).getGraphic().setVariation(8);
        }
        int[][] explode_area = {{posX,posY,posZ,6}};

        for (int z = 0; z < map.getLayersCount(); z ++)
            for (int y = 0; y < map.getWidthY(); y ++)
                for (int x = 0; x < map.getWidthX(); x ++) {
                    Tile t = map.getDemineur().getTile(x,y,z);

                    //animation
                    if (t.getGraphic().getVariation() > 0)
                        t.getGraphic().setVariation(t.getGraphic().getVariation() - 1);

                    //explosion
                    if (map.isInLode(explode_area, x,y,z) && !bombs.isEmpty()) {
                        if (t.isBomb() && !t.isRevealed()) {
                            t.setValue(TileType.BOMB);
                            int[] tmp = {z, y, x};
                            if (!contains(bombs,tmp))
                                bombs.add(tmp);
                        }
                        else if (!t.isBomb())
                            t.setValue(TileType.VOID);

                        if (particleSprite[z][y][x] == 0 && new Random().nextInt(0,40) == 1)
                            particleSprite[z][y][x] = (byte) new Random().nextInt(3,8);

                        t.setRevealed(true);
                    }
                }
        if (!bombs.isEmpty())
            bombs.remove(0);
    }

    private void endGame() {
        setInLose();
        if (!bombs.isEmpty()) {
            int[] i = bombs.get(0);

            map.getDemineur().getTile(i[2], i[1], i[0]).setValue(TileType.BOMB);
            explode(i[0], i[1], i[2]);
        }
        if (animationsLeft())
            explode(-1,-1,-1);
    }


    /**  gameState
     *      0 : Initialize
     *      1 : inGame
     *      2 : inLose
     *      3 : end
     */

    public boolean isInitialize(){
        return gameSate == 0;
    }
    public boolean isInGame(){
        return gameSate == 1 || isInitialize();
    }
    public boolean isInLose(){
        return gameSate == 2;
    }
    public boolean isEnd(){
        return gameSate == 3;
    }

    public void setInitialize(){
        gameSate = 0;
    }
    public void setInGame(){
        gameSate = 1;
    }
    public void setInLose(){
        gameSate = 2;
    }
    public void setEnd(){
        gameSate = 3;
    }
}