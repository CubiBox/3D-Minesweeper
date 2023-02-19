package fr.cubibox.minesweeper.controllers;

import fr.cubibox.minesweeper.MineSweeper;
import fr.cubibox.minesweeper.level.Map;
import fr.cubibox.minesweeper.level.Tile;
import fr.cubibox.minesweeper.level.TileType;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Game implements Initializable {
    //FXML
    public Canvas canvas;
    public Button turnLeftButton;
    public Button turnRightButton;
    public ScrollPane scrollPane;
    public Button save;
    public Button menuButton;
    public Label counterMine;
    public CheckBox showBombs;
    public CheckBox switchOpti;

    //global variables
    public static Map map;
    private static int spriteCount;
    private static int fpsCount;
    private static int fpsCountMem;
    private static long timer;
    private static long startTime;
    public Label timerLabel;
    public VBox debugMenu;
    public Label debugContent;
    public AnchorPane base;
    private int currentLayer;

    /**  gameState
     *      0 : Initialize
     *      1 : inGame
     *      2 : inLose
     *      3 : end
     */
    private static byte gameSate;
    private int bombsLeft;

    //animation lists
    public ArrayList<int[]> bombs = new ArrayList<>();
    public static byte[][][] particleSprite;

    //util variables
    private double TILE_SIZE = 0;
    private double A, B, C, D, A1, B1, C1, D1 = 0;
    private double xOffset = 0;
    private double yOffset = 0;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //recover map data
        map = MineSweeper.map;
        int width = map.getWidthX();
        int height = map.getLayersCount();

        //initialize game variables
        particleSprite = new byte[height][width][width];
        currentLayer = 0;
        bombsLeft = map.getNbBombs();
        TILE_SIZE = (int)(MineSweeper.stage.getWidth()/(width + 8));
        xOffset = MineSweeper.stage.getWidth() / 2 ;
        yOffset = TILE_SIZE ;

        MineSweeper.getAllSprite((int) TILE_SIZE);
        initScreenValue();

        if (map.getGenerationSeed() == 0) setInitialize();
        else setInGame();

        if (map.getGraphicSeed() == 0)
            map.decorateMap(map.getLayers());

        canvas.setWidth(MineSweeper.stage.getWidth());
        canvas.setHeight(map.getLayersCount() * (TILE_SIZE/2) + map.getWidthX() * (TILE_SIZE/2) + 2 * yOffset);

        scrollPane.setMinHeight(map.getLayersCount() * (TILE_SIZE/2) + map.getWidthX() * (TILE_SIZE/2) + 2 * yOffset);
        scrollPane.setMinWidth(MineSweeper.stage.getWidth());

        canvas.setOnMouseClicked(this::updateMap);
        canvas.setOnScroll(this::updateLayer);
        base.setOnKeyPressed(e->{
            if(e.getCode()== KeyCode.F3) {
                debugMenu.setVisible(!debugMenu.isVisible());
            }
        });
        turnLeftButton.setOnMouseClicked(this::rotateMinesweeperLeft);
        turnRightButton.setOnMouseClicked(this::rotateMinesweeperRight);
        menuButton.setOnMouseClicked(this::mainMenu);
        showBombs.setOnMouseClicked(this::updateMap);
        save.setOnMouseClicked(this::save);
        switchOpti.setSelected(true);
        switchOpti.setOnMouseClicked(this::updateMap);

        MineSweeper.stage.widthProperty().addListener((obs, oldVal, newVal) -> updateScreen());
        MineSweeper.stage.heightProperty().addListener((obs, oldVal, newVal) -> updateScreen());

        updateScreen();

        animationThread();
    }

    public void animationThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isEnd()) {
                    try { Thread.sleep(50);} catch (InterruptedException ignore) { }
                    timer += 50;

                    if (isInGame()){
                        Platform.runLater(() -> updateCanvas());
                    }
                    else if (isInLose()) {
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
                }
            }
        }).start();
    }

    public static String formatTime(){
        long time = (timer/1000);
        long seconds = time%60;
        long minutes = (time/60)%60;
        long hours = (time/60)/60;
        return (hours>=1?(hours<10?"0"+hours:hours)+":":"") + (minutes<10?"0"+minutes:minutes) + ":" + (seconds<10?"0"+seconds:seconds);
    }

    private void rotateMinesweeperLeft(MouseEvent mouseEvent) {
        map.getDemineur().rotateMinesweeperLeft();
        updateCanvas();
    }

    private void mainMenu(MouseEvent mouseEvent) {
        setEnd();
        MineSweeper.mainMenu();
    }

    void save(MouseEvent mouseEvent) {
        map.save("map_real");
    }
    private void rotateMinesweeperRight(MouseEvent mouseEvent) {
        map.getDemineur().rotateMinesweeperRight();
        updateCanvas();
    }

    private void updateLayer(ScrollEvent e) {
        if (isInGame() || isInitialize()) {
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

    public void updateScreen(){
        canvas.setWidth(MineSweeper.stage.getWidth());
        scrollPane.setMinWidth(MineSweeper.stage.getWidth());
        TILE_SIZE = (int)(canvas.getWidth()/(map.getWidthX() + 8));

        xOffset = canvas.getWidth() / 2 ;
        yOffset = TILE_SIZE ;

        MineSweeper.getAllSprite((int) TILE_SIZE);
        initScreenValue();

        scrollPane.setMinHeight(MineSweeper.stage.getHeight());

        canvas.setHeight(Math.max(map.getLayersCount() * (TILE_SIZE / 2) + map.getWidthX() * (TILE_SIZE / 2) + 2 * yOffset, MineSweeper.stage.getHeight()));

        updateCanvas();
    }

    public void updateCanvas() {
        bombsLeft = countBombLeft();
        counterMine.setText(String.valueOf(bombsLeft));
        timerLabel.setText(formatTime());
        if (debugMenu.isVisible())
            debugContent.setText(actuDebug());

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.rgb(10,165,200));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        drawMap(gc);
    }

    public void updateMap(MouseEvent event) {
        double x = event.getX() - xOffset;
        double y = event.getY() - yOffset;

        int posX = (int) (x * A1 + y * B1 - currentLayer);
        int posY = (int) (x * C1 + y * D1 - currentLayer);

        if (posX >= 0 && posY >= 0 && posY < map.getWidthY() && posX < map.getWidthX() && (isInGame() || isInitialize())) {
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
        updateCanvas();
    }

    public boolean optimisation(int x, int y, int z){
        if (switchOpti.isSelected())
            return (
                    (y == map.getWidthY()-1 || x == map.getWidthX()-1 || z == currentLayer) ||  //except last layer
                    (   //don't show hidden sprites
                        z - 1 >= currentLayer
                        && y + 1 < map.getWidthY()
                        && x + 1 < map.getWidthX()
                        && map.getLayer(z - 1).getTile(x + 1, y + 1).isRevealed()
                    ));
        else return true;
    }

    private void drawMap(GraphicsContext gc) {
        spriteCount = 0;
        double TILE_SIZE_HALF = TILE_SIZE/2;
        for (int layer = map.getLayersCount()-1; layer >= currentLayer; layer--) {
            for (int y = 0; y < map.getWidthY(); y++) {
                for (int x = 0; x < map.getWidthX(); x++) {
                    int posX = (int) (x * A + y * B - TILE_SIZE_HALF + xOffset);
                    int posY = (int) (x * C + y * D + TILE_SIZE_HALF * layer + yOffset);

                    if (showBombs.isSelected()){
                        Tile tile = map.getLayer(layer).getTile(x, y);
                        if (tile.isBomb())
                            drawSprite(gc,"bomb",2,posX, posY);
                        else {
                            if (layer == map.getLayersCount()-1)
                                drawSprite(gc,"bases",0, posX, posY);
                            if (x == 0)
                                drawSprite(gc,"bases",1, posX, posY);
                            if (y == 0)
                                drawSprite(gc,"bases",2, posX, posY);
                        }
                    }

                    else if (!isEnd() && optimisation(x,y,layer)){
                        Tile tile = map.getLayer(layer).getTile(x, y);

                        //draw cube
                        drawSprite(gc,tile.getTexture(),tile.getGraphic().getVariation(), posX, posY);

                        //draw numbers
                        if (tile.getNbMine() > 0 && layer == currentLayer && (isInGame() || isInitialize()))
                            drawSprite(gc,"numbers",tile.getNbMine(), posX, posY);

                        //draw decoration
                        ImageView decoSprite;
                        if (!tile.isRevealed()) {
                            if (tile.isMarked())
                                drawSprite(gc,"flag",0, posX, posY - TILE_SIZE_HALF);

                            else if (tile.hasDecoration())
                                drawSprite(gc,
                                        tile.getGraphic().getDecoration().getFolder(),
                                        tile.getGraphic().getDecoration().getVariation(),
                                        posX + tile.getGraphic().getDecoration().getVx(),
                                        posY - TILE_SIZE_HALF + tile.getGraphic().getDecoration().getVy());
                        }

                        //draw decoration of the layer under the current one (to fix some perspectives issues)
                        else if (layer + 1 <= map.getLayersCount() - 1 && !map.getLayer(layer + 1).getTile(x, y).isRevealed()) {
                            tile = map.getLayer(layer + 1).getTile(x, y);
                            double posY_deco = x * C + y * D + TILE_SIZE_HALF * (layer + 1) - TILE_SIZE_HALF + yOffset;

                            if (tile.isMarked())
                                drawSprite(gc,"flag",0, posX, posY_deco);

                            else if (tile.hasDecoration())
                                drawSprite(gc,
                                        tile.getGraphic().getDecoration().getFolder(),
                                        tile.getGraphic().getDecoration().getVariation(),
                                        posX + tile.getGraphic().getDecoration().getVx(),
                                        posY_deco + tile.getGraphic().getDecoration().getVy());
                        }

                        if (particleSprite[layer][y][x] != 0)
                            drawSprite(gc,"explode",particleSprite[layer][y][x] - 1, posX, posY - TILE_SIZE_HALF);
                    }
                }
            }
        }
        fpsCount ++;
    }

    public String actuDebug(){
        if (timer%1000==0) {
            fpsCountMem = fpsCount;
            fpsCount=0;
        }
        return
            "FPS : " + fpsCountMem + "\n" +
            "sprites : " + spriteCount + "\n" +
            "TPS : " + "" + "\n" +
            "screen size : " + MineSweeper.stage.getWidth() + " * " + MineSweeper.stage.getHeight() + "px\n" +
            "GameState : " + gameSate + "\n";
    }

    public static void drawSprite(GraphicsContext gc, String str, int var, double posX, double posY){
        try {
            Image img = MineSweeper.tileSprites.get(str)[var].getImage();
            gc.drawImage(img, posX, posY);
            spriteCount++;
        }catch(Exception e){
            System.out.println(e.getMessage());
            System.out.println(str + " " + var + "; " + posX + ", " + posY);
        };
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

    public int countBombLeft(){
        if (isInitialize())
            return map.getNbBombs();
        if (isInLose() || isEnd())
            return 0;

        int tpm = 0;
        for (int z = 0; z < map.getLayersCount(); z ++)
            for (int y = 0; y < map.getWidthY(); y ++)
                for (int x = 0; x < map.getWidthX(); x++) {
                    if (map.getDemineur().getTile(x, y, z).isBomb())
                        tpm++;
                    if (map.getDemineur().getTile(x, y, z).isMarked() && tpm > 0){
                        tpm --;
                    }
                }
        return tpm;
    }

    public void explode(int posZ, int posY, int posX){
        //if posZ, posY and posX isn't out of bound
        if (posZ > 0 && posY > 0 && posX > 0) {
            if (map.getDemineur().getTile(posX, posY, posZ).isRevealed() && !bombs.isEmpty())
                bombs.remove(0);

            map.getDemineur().getTile(posX, posY, posZ).setRevealed(true);
            map.getDemineur().getTile(posX, posY, posZ).getGraphic().setVariation(8);
        }

        //set explode area (pos of bomb with rayon of 6)
        int[][] explode_area = {{posX,posY,posZ,6}};

        //parkour map to find explode area
        for (int z = 0; z < map.getLayersCount(); z ++)
            for (int y = 0; y < map.getWidthY(); y ++)
                for (int x = 0; x < map.getWidthX(); x ++) {
                    Tile t = map.getDemineur().getTile(x,y,z);

                    //animation
                    if (t.getGraphic().getVariation() > 0)
                        t.getGraphic().setVariation(t.getGraphic().getVariation() - 1);

                    //explosion
                    if (map.isInLode(explode_area, x,y,z) && !bombs.isEmpty()) {

                        //if is bomb, add him to the pile
                        if (t.isBomb() && !t.isRevealed()) {
                            t.setValue(TileType.BOMB);
                            int[] tmp = {z, y, x};
                            if (!contains(bombs,tmp))
                                bombs.add(tmp);
                        }

                        //clear usual cube
                        else if (!t.isBomb())
                            t.setValue(TileType.VOID);

                        //set particle of explosion
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
        return gameSate == 1;
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