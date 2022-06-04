package net.ddns.celestgames.isometric_graphics;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main.fxml"));
        MainController controller = new MainController();
        fxmlLoader.setController(controller);
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Welcome to the Isometric World !");
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(event -> {
            System.exit(0);
        });

        controller.run();
    }

    public static void main(String[] args) {
        launch();
    }
}