package fr.celestgames.isoworlds.controllers;

import fr.celestgames.isoworlds.MineSweeper;
import fr.celestgames.isoworlds.level.Map;
import fr.celestgames.isoworlds.level.Tile;
import fr.celestgames.isoworlds.minesweeper.Demineur;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class Custom implements Initializable {
    public Button playButton;
    public Button backButton;
    public TextField bombsInput;
    public MenuButton biomeInput;
    public Canvas preview;
    public Slider widthSlider;
    public Slider heightSlider;

    private Map previewMap;
    private double TILE_SIZE = 0;

    private double A, B, C, D, A1, B1, C1, D1 = 0;

    private double xOffset = 0;
    private double yOffset = 0;



    public Custom() {
    }

    public Map voidMap(){
        Map map = new Map((int) widthSlider.getValue(), (int) heightSlider.getValue());
        map.setDemineur(new Demineur(map.getLayers()));
        return map;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        xOffset = preview.getLayoutX();
        yOffset = preview.getLayoutY();
        widthSlider.setOnMouseDragged(this::updatePreview);
        heightSlider.setOnMouseDragged(this::updatePreview);
        biomeInput.setOnKeyReleased(this::updatePreview);
        bombsInput.setOnKeyReleased(this::updatePreview);
        backButton.setOnMouseClicked(this::mainMenu);
        playButton.setOnMouseClicked(this::play);

        updatePreview();
    }

    private void drawMap(GraphicsContext gc) {
        double TILE_SIZE_HALF = TILE_SIZE/2;
        for (int layer = previewMap.getLayersCount()-1; layer >= 0; layer--) {
            for (int y = 0; y < previewMap.getHeight(); y++) {
                for (int x = 0; x < previewMap.getWidth(); x++) {
                    int posX = (int) (x * A + y * B - TILE_SIZE_HALF + xOffset);
                    int posY = (int) (x * C + y * D + TILE_SIZE_HALF * layer + yOffset);

                    Tile tile = previewMap.getLayer(layer).getTile(x, y);

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


    public void updatePreview(){
        GraphicsContext gc = preview.getGraphicsContext2D();
        gc.clearRect(0, 0, preview.getWidth(), preview.getHeight());
        gc.setFill(Color.rgb(10,165,200));
        gc.fillRect(0, 0, preview.getWidth(), preview.getHeight());

        TILE_SIZE = (int)(preview.getWidth()/(widthSlider.getValue() + 8));
        MineSweeper.getAllSprite((int) TILE_SIZE);

        previewMap = voidMap();

        initScreenvalue(TILE_SIZE);
        xOffset = preview.getLayoutX() + preview.getWidth() / 2 ;
        yOffset = preview.getLayoutY() + TILE_SIZE ;

        drawMap(gc);
    }

    public void updatePreview(MouseEvent event) {
        updatePreview();
    }

    private void updatePreview(KeyEvent keyEvent) {
        updatePreview();
    }

    private void mainMenu(MouseEvent mouseEvent) {
        MineSweeper.mainMenu();
    }

    public void play(MouseEvent mouseEvent) {
        MineSweeper.play();
    }

}