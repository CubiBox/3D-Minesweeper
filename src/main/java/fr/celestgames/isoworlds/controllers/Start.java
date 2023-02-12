package fr.celestgames.isoworlds.controllers;

import fr.celestgames.isoworlds.MineSweeper;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
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
