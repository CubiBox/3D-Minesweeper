package fr.celestgames.isoworlds;

import fr.celestgames.isoworlds.controllers.Game;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class MineSweeper extends Application {
    public static Stage stage;
    public static Scene mainMenu;
    public static Scene game;
    public static Scene settings;
    public static Scene custom;
    public static Scene scene;

    @Override
    public void start(Stage stage) {
        MineSweeper.stage = stage;
        goToPage("menu");
    }

    public static void mainMenu(){
        goToPage("menu");
    }

    public static void play(){
        goToPage("game");
    }

    public static void goToPage(String str){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("scenes/"+str+".fxml"));
            MineSweeper.scene = new Scene(fxmlLoader.load());
        }
        catch(Exception ignored) {}
        showStage();
    }

    public static void switchScene(Scene scene){
        MineSweeper.scene = scene;
        showStage();
    }

    public static void showStage(){
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(event -> System.exit(0));
        if (!stage.isMaximized()) stage.setMaximized(true);
    }

    public static void launch(String[] args) {
        launch();
    }
}
