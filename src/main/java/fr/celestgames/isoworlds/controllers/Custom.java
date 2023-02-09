package fr.celestgames.isoworlds.controllers;

import fr.celestgames.isoworlds.MineSweeper;
import fr.celestgames.isoworlds.level.Map;
import fr.celestgames.isoworlds.level.Tile;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class Custom implements Initializable {
    public Button playButton;
    public Button backButton;
    public TextField widthInput;
    public TextField heightInput;
    public TextField BombsInput;
    public MenuButton biomeInput;
    public Canvas preview;

    public static HashMap<String, ImageView[]> tileSprites = new HashMap<>();

    private Map previewMap;

    private double TILE_SIZE = 0;

    private double A, B, C, D, A1, B1, C1, D1 = 0;

    private double xOffset = 0;
    private double yOffset = 0;

    public Custom() {
        //initScreenvalue()
    }

    public String getFileName(String line){
        return line.replaceAll(".png", "");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        xOffset = preview.getLayoutX();
        yOffset = preview.getLayoutY();
        preview.setOnMouseMoved(this::updateCanvas);
        preview.setOnMouseClicked(this::updateCanvas);
    }

    private void drawMap(GraphicsContext gc, int mapPosX, int mapPosY) {
        double TILE_SIZE_HALF = TILE_SIZE/2;
        for (int layer = previewMap.getLayersCount()-1; layer >= 0; layer--) {
            for (int y = 0; y < previewMap.getHeight(); y++) {
                for (int x = 0; x < previewMap.getWidth(); x++) {
                    int posX = (int) (x * A + y * B - TILE_SIZE_HALF + xOffset);
                    int posY = (int) (x * C + y * D + TILE_SIZE_HALF * layer + yOffset);

                    Tile tile = previewMap.getLayer(layer).getTile(x, y);

                    //draw cube
                    ImageView tileSprite = tileSprites.get(tile.getTexture())[tile.getGraphic().getVariation()];
                    if (tileSprite != null) {
                        gc.drawImage(tileSprite.getImage(), posX, posY);
                    }

                    //draw decoration
                    ImageView decoSprite = null;
                    if (!tile.isReveled()) {
                        if (tile.hasDecoration()) {
                            decoSprite = tileSprites.get(tile.getGraphic().getDecoration().getFolder())[tile.getGraphic().getDecoration().getVariation()];
                            if (decoSprite != null)
                                gc.drawImage(decoSprite.getImage(), posX + tile.getGraphic().getDecoration().getVx(), posY - TILE_SIZE_HALF + tile.getGraphic().getDecoration().getVy());
                        }
                    }
                }
            }
        }
    }

    public void updateCanvas(MouseEvent event) {
        GraphicsContext gc = preview.getGraphicsContext2D();
        gc.clearRect(0, 0, preview.getWidth(), preview.getHeight());
        gc.setFill(Color.rgb(10,165,200));
        gc.fillRect(0, 0, preview.getWidth(), preview.getHeight());

        drawMap(gc, -1, -1);
    }

    public void initScreenvalue(double tileSize){
        A = 0.5 * tileSize;
        B = -0.5 * tileSize;
        C = 0.25 * tileSize;
        D = 0.25 * tileSize;

        double det = 1 / (A * D - B * C);

        A1 = D * det;
        B1 = -B * det;
        C1 = -C * det;
        D1 = A * det;
    }

    public void mainMenu(ActionEvent actionEvent) {
        MineSweeper.mainMenu();
    }

    public void play(ActionEvent actionEvent) {
        MineSweeper.play();
    }

}