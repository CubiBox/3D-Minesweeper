package fr.celestgames.isoworlds.controllers;

import fr.celestgames.isoworlds.MineSweeper;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class Setting implements Initializable {
    public Canvas canvas;
    public Button playButton;
    public Button settingButton;


    public Setting() {

    }

    public String getFileName(String line){
        return line.replaceAll(".png", "");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        canvas.setOnMouseMoved(this::updateCanvas);
        canvas.setOnMouseClicked(this::updateCanvas);
    }

    public void mainMenu(ActionEvent actionEvent) {
        MineSweeper.mainMenu();
    }

    public void play(ActionEvent actionEvent) {
        MineSweeper.play();
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
        //drawMap(gc, mapPosX, mapPosY);
    }

    private void drawMap(GraphicsContext gc, int mapPosX, int mapPosY) {

    }


}