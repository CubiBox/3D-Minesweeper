package fr.cubibox.minesweeper.controllers;

import fr.cubibox.minesweeper.MineSweeper;
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
    public Button backButton;


    public Setting() {

    }

    public String getFileName(String line){
        return line.replaceAll(".png", "");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        canvas.setOnMouseMoved(this::updateCanvas);
        canvas.setOnMouseClicked(this::updateCanvas);
        backButton.setOnMouseClicked(this::mainMenu);
    }

    private void mainMenu(MouseEvent mouseEvent) {
        MineSweeper.mainMenu();
    }


    public void lowUpdateCanvas() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.rgb(10,165,200));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        drawMap(gc);
    }

    public void updateCanvas(MouseEvent event) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.rgb(10,165,200));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        //drawMap(gc, mapPosX, mapPosY);
    }

    private void drawMap(GraphicsContext gc) {

    }


}