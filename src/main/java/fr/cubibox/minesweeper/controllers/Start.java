package fr.cubibox.minesweeper.controllers;

import fr.cubibox.minesweeper.MineSweeper;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class Start implements Initializable {
    public AnchorPane base;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        base.setOnMouseMoved(this::loadMenu);
    }
    private void loadMenu(MouseEvent mouseEvent) {
        MineSweeper.goToPage("menu");
    }
}
