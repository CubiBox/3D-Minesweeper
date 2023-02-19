package fr.cubibox.minesweeper.controllers;

import fr.cubibox.minesweeper.MineSweeper;
import fr.cubibox.minesweeper.level.Map;
import fr.cubibox.minesweeper.level.Tile;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class Menu implements Initializable {
    public Button settingButton;
    public Button customButton;
    public Canvas background;
    public ScrollPane base;

    public static Map menuMap;
    public Button playEasyButton;
    public Button playMediumButton;
    public Button playHardButton;
    public Button aboutButton;
    public Button recordButton;
    public Button loadButton;
    public HBox loadPanel;
    public VBox struct;
    public Button openLoadPanel;
    public Button closeLoadPanel;

    private double TILE_SIZE = 64;
    private double A, B, C, D;

    private double xOffset = 0;
    private double yOffset = 0;

    private double stageWidth;
    private double stageHeight;

    public static byte menuState;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Map File");
        fileChooser.setInitialDirectory(new File(Objects.requireNonNull(MineSweeper.class.getResource("saves")).getFile()));

        menuState = 0;
        playEasyButton.setOnMouseClicked(e -> {play(1);});
        playMediumButton.setOnMouseClicked(e -> {play(2);});
        playHardButton.setOnMouseClicked(e -> {play(3);});
        settingButton.setOnMouseClicked(e-> {setting();});
        customButton.setOnMouseClicked(e-> {custom();});

        loadPanel.setVisible(!loadPanel.isDisabled());
        loadButton.setOnMouseClicked(e -> {
            loadPanel.setDisable(!loadPanel.isDisabled());
            loadPanel.setVisible(!loadPanel.isDisabled());
        });
        closeLoadPanel.setOnMouseClicked(e -> {
            loadPanel.setDisable(!loadPanel.isDisabled());
            loadPanel.setVisible(!loadPanel.isDisabled());
        });

        openLoadPanel.setOnMouseClicked(e -> {
            fileChooser.showOpenDialog(MineSweeper.stage);
        });

        openLoadPanel.setOnAction(e -> {
                    File file = fileChooser.showOpenDialog(MineSweeper.stage);
                    if (file != null && file.isFile()) {
                        Custom.previewMap = Map.recover(file);
                        play(4);
                    }
                });

        MineSweeper.stage.widthProperty().addListener((obs, oldVal, newVal) -> updatePreview());
        MineSweeper.stage.heightProperty().addListener((obs, oldVal, newVal) -> updatePreview());

        initializePreview();
        animationThread();

        menuState = 1;
    }

    public void animationThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!(menuState == 2)) {
                    try { Thread.sleep(1000);} catch (InterruptedException ignore) { }

                    if (menuState == 1) {
                        updateMap();
                        Platform.runLater(() -> updatePreview());
                    }
                }
            }
        }).start();
    }

    private void play(int diff){
        MineSweeper.selectDifficulty = diff;
        menuState = 2;
        MineSweeper.play();
    }

    public void setting() {
        menuState = 2;
        MineSweeper.setting();
    }
    public void custom() {
        menuState = 2;
        MineSweeper.custom();
    }

    private void drawMap(GraphicsContext gc) {
        int countSprite = 0;

        double TILE_SIZE_HALF = TILE_SIZE/2;
        for (int layer = menuMap.getLayersCount()-1; layer >= 0; layer--) {
            for (int y = 0; y < menuMap.getWidthY(); y++) {
                for (int x = 0; x < menuMap.getWidthX(); x++) {
                    int posX = (int) (x * A + y * B - TILE_SIZE_HALF + xOffset);
                    int posY = (int) (x * C + y * D + TILE_SIZE_HALF * layer + yOffset);

                    if ((y == menuMap.getWidthY()-1 || x == menuMap.getWidthX()-1)   //except last layer
                        ||
                        (   //don't show hidden sprites
                            layer - 1 >= 0
                            && y + 1 < menuMap.getWidthY()
                            && x + 1 < menuMap.getWidthX()
                            && menuMap.getLayer(layer - 1).getTile(x + 1, y + 1).isRevealed()
                        )
                    ){
                        Tile tile = menuMap.getLayer(layer).getTile(x, y);

                        //draw cube
                        ImageView tileSprite = MineSweeper.tileSprites.get(tile.getTexture())[tile.getGraphic().getVariation()];
                        if (tileSprite != null)
                            gc.drawImage(tileSprite.getImage(), posX, posY);
                         countSprite ++;

                        //draw decoration
                        ImageView decoSprite;
                        if (!tile.isRevealed() && tile.hasDecoration()) {
                            countSprite++;
                            decoSprite = MineSweeper.tileSprites.get(tile.getGraphic().getDecoration().getFolder())[tile.getGraphic().getDecoration().getVariation()];
                            if (decoSprite != null)
                                gc.drawImage(decoSprite.getImage(), posX + tile.getGraphic().getDecoration().getVx(), posY - TILE_SIZE_HALF + tile.getGraphic().getDecoration().getVy());
                        }

                        //draw decoration of the layer under the current one (to fix some perspectives issues)
                        if (tile.isRevealed() && layer + 1 < menuMap.getLayersCount() && !menuMap.getLayer(layer + 1).getTile(x, y).isRevealed()) {
                            tile = menuMap.getLayer(layer + 1).getTile(x, y);
                            double posY_deco = x * C + y * D + TILE_SIZE_HALF * (layer + 1) - TILE_SIZE_HALF + yOffset;
                            if (tile.hasDecoration()) {
                                countSprite ++;
                                decoSprite = MineSweeper.tileSprites.get(tile.getGraphic().getDecoration().getFolder())[tile.getGraphic().getDecoration().getVariation()];
                                if (decoSprite != null)
                                    gc.drawImage(decoSprite.getImage(), posX + tile.getGraphic().getDecoration().getVx(), posY_deco + tile.getGraphic().getDecoration().getVy());

                            }
                        }
                    }
                }
            }
        }
        System.out.println(countSprite + " -> Menu");
    }

    public void updatePreview(){
        if (menuState == 1) {
            updateScreen();

            GraphicsContext gc = background.getGraphicsContext2D();
            gc.clearRect(0, 0, background.getWidth(), background.getHeight());
            gc.setFill(Color.rgb(10, 165, 200));
            gc.fillRect(0, 0, background.getWidth(), background.getHeight());

            drawMap(gc);
        }
    }

    public void updateMap(){
        menuMap = Map.voidMap(20, 12);
        menuMap.decorateMap(menuMap.getLayers());
        menuMap.applyHeightMap(4);
    }

    public void updateScreen(){
        stageWidth = MineSweeper.stage.getWidth();
        stageHeight = MineSweeper.stage.getHeight();

        struct.setPrefWidth(stageWidth/2);
        struct.setPrefHeight(stageHeight/2);
        struct.setMaxHeight(stageHeight/2);

        background.setWidth(stageWidth);
        background.setHeight(stageHeight);
        base.setMinHeight(stageWidth);
        base.setMinWidth(stageHeight);
        TILE_SIZE = (int)(stageWidth/30);

        xOffset = stageWidth/2;
        yOffset = -TILE_SIZE/2;

        MineSweeper.getAllSprite((int) TILE_SIZE);
        A = 0.5 * TILE_SIZE;
        B = -0.5 * TILE_SIZE;
        C = 0.25 * TILE_SIZE;
        D = 0.25 * TILE_SIZE;
    }

    public void initializePreview(){
        updateScreen();
        updateMap();

        GraphicsContext gc = background.getGraphicsContext2D();
        gc.clearRect(0, 0, background.getWidth(), background.getHeight());
        gc.setFill(Color.rgb(10,165,200));
        gc.fillRect(0, 0, background.getWidth(), background.getHeight());
        drawMap(gc);
    }
}