package fr.celestgames.isoworlds.controllers;

import fr.celestgames.isoworlds.MineSweeper;
import fr.celestgames.isoworlds.level.Map;
import fr.celestgames.isoworlds.level.Tile;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class Menu implements Initializable {
    public Button playButton;
    public Button settingButton;
    public Button customButton;
    public Button actu;
    public Canvas background;
    public ScrollPane base;

    public static Map menuMap;
    public Button playEasyButton;
    public Button playMediumButton;
    public Button playHardButton;
    public Button aboutButton;
    public Button recordButton;
    private double TILE_SIZE = 64;
    private double A, B, C, D;

    private double xOffset = 0;
    private double yOffset = 0;

    public boolean isOnGame;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        isOnGame = false;
        playEasyButton.setOnMouseClicked(this::easyPlay);
        playMediumButton.setOnMouseClicked(this::mediumPlay);
        playHardButton.setOnMouseClicked(this::hardPlay);
        settingButton.setOnMouseClicked(this::setting);
        customButton.setOnMouseClicked(this::custom);

        background.setWidth(MineSweeper.stage.getWidth());
        background.setHeight(MineSweeper.stage.getHeight());
        base.setMinHeight(MineSweeper.stage.getWidth());
        base.setMinWidth(MineSweeper.stage.getWidth());

        GraphicsContext gc = background.getGraphicsContext2D();
        gc.clearRect(0, 0, background.getWidth(), background.getHeight());
        gc.setFill(Color.rgb(10,165,200));
        gc.fillRect(0, 0, background.getWidth(), background.getHeight());

        updatePreview();
        animationThread();
    }

    public void animationThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (;;) {
                    try { Thread.sleep(1000);} catch (InterruptedException ignore) { }

                    if (!isOnGame)
                        Platform.runLater(() -> updatePreview());
                }
            }
        }).start();
    }

    @FXML
    private void play() {
        isOnGame = true;
        MineSweeper.play();
    }

    private void easyPlay(MouseEvent e){
        MineSweeper.selectDifficulty = 0;
        play();
    }
    private void mediumPlay(MouseEvent e){
        MineSweeper.selectDifficulty = 1;
        play();
    }
    private void hardPlay(MouseEvent e){
        MineSweeper.selectDifficulty = 2;
        play();
    }

    public void setting(MouseEvent mouseEvent) {
        MineSweeper.setting();
    }
    public void custom(MouseEvent mouseEvent) {
        isOnGame = true;
        MineSweeper.custom();
    }


    private void drawMap(GraphicsContext gc) {
        double TILE_SIZE_HALF = TILE_SIZE/2;
        for (int layer = menuMap.getLayersCount()-1; layer >= 0; layer--) {
            for (int y = 0; y < menuMap.getWidthY(); y++) {
                for (int x = 0; x < menuMap.getWidthX(); x++) {
                    int posX = (int) (x * A + y * B - TILE_SIZE_HALF + xOffset);
                    int posY = (int) (x * C + y * D + TILE_SIZE_HALF * layer + yOffset);
                    Tile tile = menuMap.getLayer(layer).getTile(x, y);

                    //draw cube
                    ImageView tileSprite = MineSweeper.tileSprites.get(tile.getTexture())[tile.getGraphic().getVariation()];
                    if (tileSprite != null)
                        gc.drawImage(tileSprite.getImage(), posX, posY);

                    //draw decoration
                    ImageView decoSprite;
                    if (!tile.isRevealed() && tile.hasDecoration()) {
                        decoSprite = MineSweeper.tileSprites.get(tile.getGraphic().getDecoration().getFolder())[tile.getGraphic().getDecoration().getVariation()];
                        if (decoSprite != null)
                            gc.drawImage(decoSprite.getImage(),posX + tile.getGraphic().getDecoration().getVx(),posY - TILE_SIZE_HALF + tile.getGraphic().getDecoration().getVy());
                    }

                    //draw decoration of the layer under the current one (to fix some perspectives issues)
                    if (tile.isRevealed() && layer + 1 <= menuMap.getLayersCount()-1 && !menuMap.getLayer(layer + 1).getTile(x, y).isRevealed()) {
                        tile = menuMap.getLayer(layer + 1).getTile(x, y);
                        double posY_deco = x * C + y * D + TILE_SIZE_HALF * (layer + 1) - TILE_SIZE_HALF + yOffset;
                        if (tile.hasDecoration()) {
                            decoSprite = MineSweeper.tileSprites.get(tile.getGraphic().getDecoration().getFolder())[tile.getGraphic().getDecoration().getVariation()];
                            if (decoSprite != null)
                                gc.drawImage(decoSprite.getImage(), posX + tile.getGraphic().getDecoration().getVx(), posY_deco + tile.getGraphic().getDecoration().getVy());
                        }
                    }
                }
            }
        }
    }

    public void updatePreview(){
        background.setWidth(MineSweeper.stage.getWidth());
        background.setHeight(MineSweeper.stage.getHeight());
        base.setMinHeight(MineSweeper.stage.getWidth());
        base.setMinWidth(MineSweeper.stage.getWidth());

        GraphicsContext gc = background.getGraphicsContext2D();
        gc.clearRect(0, 0, background.getWidth(), background.getHeight());
        gc.setFill(Color.rgb(10,165,200));
        gc.fillRect(0, 0, background.getWidth(), background.getHeight());

        TILE_SIZE = (int)(background.getWidth()/30);
        MineSweeper.getAllSprite((int) TILE_SIZE);
        A = 0.5 * TILE_SIZE;
        B = -0.5 * TILE_SIZE;
        C = 0.25 * TILE_SIZE;
        D = 0.25 * TILE_SIZE;

        menuMap = Map.voidMap(20, 12);
        menuMap.decorateMap(menuMap.getLayers());
        menuMap.applyHeightMap(4);

        xOffset = background.getWidth()/2;
        yOffset = -TILE_SIZE/2;

        drawMap(gc);
    }
}