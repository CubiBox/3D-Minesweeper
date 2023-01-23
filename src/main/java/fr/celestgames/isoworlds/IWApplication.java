package fr.celestgames.isoworlds;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class IWApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("scenes/mapcreator.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1080, 720);
        stage.setTitle("IsoWorlds map creator");
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(event -> System.exit(0));
    }

    public static void launch(String[] args) {
        launch();
    }
}
