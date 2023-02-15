package fr.cubibox.minesweeper.controllers;

import fr.cubibox.minesweeper.MineSweeper;
import fr.cubibox.minesweeper.level.Map;
import fr.cubibox.minesweeper.level.Tile;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class Custom implements Initializable {
    public Button playButton;
    public Button backButton;
    public Spinner<Integer> bombsInput;
    public MenuButton biomeInput;
    public Canvas preview;
    public Slider widthSlider;
    public Slider heightSlider;
    public CheckBox showBombs;

    public static Map previewMap;
    public static int nbBombs;
    private double TILE_SIZE = 0;

    private double A, B, C, D;
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        xOffset = preview.getLayoutX();
        yOffset = preview.getLayoutY();

        //listener
        widthSlider.setOnMouseDragged(e->{updatePreview();});
        heightSlider.setOnMouseDragged(e->{updatePreview();});
        biomeInput.setOnKeyReleased(e->{updatePreview();});
        showBombs.setOnMouseClicked(e->{updatePreview();});

        backButton.setOnMouseClicked(e->{MineSweeper.mainMenu();});
        bombsInput.setOnKeyReleased(e->{checkBombs();});
        playButton.setOnMouseClicked(e->{play();});

        //nbBombs = Integer.parseInt(bombsInput.getText());

        updatePreview();
    }

    private void checkBombs() {
        if (String.valueOf(bombsInput.getValue()).matches("[0-9]*")){

        }
        else {

        }
        updatePreview();
    }

    private void drawMap(GraphicsContext gc) {
        if (showBombs.isSelected())
            previewMap.getDemineur().setMines(previewMap.getLayers(), 64, -1, -1);

        double TILE_SIZE_HALF = TILE_SIZE/2;
        for (int layer = previewMap.getLayersCount()-1; layer >= 0; layer--) {
            for (int y = 0; y < previewMap.getWidthY(); y++) {
                for (int x = 0; x < previewMap.getWidthX(); x++) {
                    int posX = (int) (x * A + y * B - TILE_SIZE_HALF + xOffset);
                    int posY = (int) (x * C + y * D + TILE_SIZE_HALF * layer + yOffset);

                    Tile tile = previewMap.getLayer(layer).getTile(x, y);

                    if (showBombs.isSelected()) {
                        if (tile.isBomb()) {
                            //gc.drawImage(tileSprite.getImage(), posX, posY);
                            gc.drawImage(MineSweeper.tileSprites.get("bomb")[2].getImage(),posX, posY);
                            //gc.drawImage(MineSweeper.tileSprites.get("showTnt")[0].getImage(), posX, posY);
                        }
                        else {
                            if (layer == previewMap.getLayersCount()-1)
                                gc.drawImage(MineSweeper.tileSprites.get("bases")[0].getImage(), posX, posY);
                            if (x == 0)
                                gc.drawImage(MineSweeper.tileSprites.get("bases")[1].getImage(), posX, posY);
                            if (y == 0)
                                gc.drawImage(MineSweeper.tileSprites.get("bases")[2].getImage(), posX, posY);
                        }
                    }
                    else {

                        //draw cube
                        ImageView tileSprite = MineSweeper.tileSprites.get(tile.getTexture())[tile.getGraphic().getVariation()];
                        if (tileSprite != null) {
                            gc.drawImage(tileSprite.getImage(), posX, posY);
                        }

                        //draw decoration
                        ImageView decoSprite = null;
                        if (!tile.isRevealed()) {
                            if (tile.hasDecoration()) {
                                decoSprite = MineSweeper.tileSprites.get(tile.getGraphic().getDecoration().getFolder())[tile.getGraphic().getDecoration().getVariation()];
                                if (decoSprite != null)
                                    gc.drawImage(decoSprite.getImage(), posX + tile.getGraphic().getDecoration().getVx(), posY - TILE_SIZE_HALF + tile.getGraphic().getDecoration().getVy());
                            }
                        }
                    }
                }
            }
        }
    }

    public void updatePreview(){
        preview.setWidth((MineSweeper.stage.getWidth()/4)*3);
        preview.setHeight(MineSweeper.stage.getHeight());

        GraphicsContext gc = preview.getGraphicsContext2D();
        gc.clearRect(0, 0, preview.getWidth(), preview.getHeight());
        gc.setFill(Color.rgb(10,165,200));
        gc.fillRect(0, 0, preview.getWidth(), preview.getHeight());

        TILE_SIZE = (int)(preview.getWidth()/(widthSlider.getValue() + 8));
        MineSweeper.getAllSprite((int) TILE_SIZE);
        A = 0.5 * TILE_SIZE;
        B = -0.5 * TILE_SIZE;
        C = 0.25 * TILE_SIZE;
        D = 0.25 * TILE_SIZE;

        Custom.previewMap = Map.voidMap((int) widthSlider.getValue(), (int) heightSlider.getValue());
        previewMap.decorateMap(previewMap.getLayers());

        xOffset =  preview.getWidth()/2 ;
        yOffset = TILE_SIZE ;

        drawMap(gc);
    }

    public void play() {
        previewMap.getDemineur().clearMines(previewMap.getLayers());
        previewMap.setNbBombs(nbBombs);
        MineSweeper.selectDifficulty = 3;
        MineSweeper.play();
    }
}