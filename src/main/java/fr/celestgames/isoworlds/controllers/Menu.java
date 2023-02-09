package fr.celestgames.isoworlds.controllers;

import fr.celestgames.isoworlds.MineSweeper;
import fr.celestgames.isoworlds.level.Map;
import fr.celestgames.isoworlds.level.Tile;
import fr.celestgames.isoworlds.level.TileType;
import fr.celestgames.isoworlds.minesweeper.Demineur;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class Menu implements Initializable {
    public Canvas canvas;
    public Button playButton;
    public Button settingButton;
    public Button customButton;


    public Menu() {

    }

    public String getFileName(String line){
        return line.replaceAll(".png", "");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        canvas.setOnMouseMoved(this::updateCanvas);
        canvas.setOnMouseClicked(this::updateCanvas);
        playButton.setOnMouseClicked(this::play);
        settingButton.setOnMouseClicked(this::setting);
        customButton.setOnMouseClicked(this::custom);
    }

    @FXML
    private void play(MouseEvent mouseEvent) {
        MineSweeper.play();
    }

    public void mainMenu(MouseEvent mouseEvent) {
        MineSweeper.mainMenu();
    }
    public void setting(MouseEvent mouseEvent) {
        MineSweeper.setting();
    }
    public void custom(MouseEvent mouseEvent) {
        MineSweeper.custom();
    }

    public void updateCanvas(MouseEvent event) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.rgb(10,165,200));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        //drawMap(gc, mapPosX, mapPosY);
    }

    private void drawMap(GraphicsContext gc, int mapPosX, int mapPosY) {

    }


}