package fr.celestgames.isoworlds;

import fr.celestgames.isoworlds.controllers.Game;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

public class MineSweeper extends Application {
    public static int selectDifficulty;

    public static Stage stage;
    public static Scene mainMenu;
    public static Scene game;
    public static Scene settings;
    public static Scene custom;
    public static Scene scene;

    public static HashMap<String, ImageView[]> tileSprites = new HashMap<>();



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

    public static void custom() {
        goToPage("custom");
    }

    public static void setting() {
        goToPage("setting");
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


    public static void getAllSprite(int TILE_SIZE){
        File folder = new File(Main.class.getResource("textures").getFile());
        File[] listOfFiles = folder.listFiles();

        assert listOfFiles != null;
        for (File file : listOfFiles) {
            if (file.isDirectory()) {
                File[] files_intern = file.listFiles();

                assert files_intern != null;
                ImageView[] images = new ImageView[files_intern.length];

                int count = 0;
                for (File file_intern : files_intern) {
                    ImageView img = getImageView(file.getName() + "/" + file_intern.getName(),TILE_SIZE);
                    if (img != null) {
                        if (file.getName().equals("numbers")) {
                            try {
                                images[Integer.parseInt(getFileName(file_intern.getName()))] = img;
                            } catch (Exception e) {
                                images[count] = img;
                                count++;
                            }
                        }
                        else {
                            images[count] = img;
                            count++;
                        }
                    }
                }
                tileSprites.put(file.getName(), images);
            }
            else if (file.isFile()) {
                ImageView img = getImageView(file.getName(),TILE_SIZE);
                if (img != null) {
                    tileSprites.put(getFileName(file.getName()), new ImageView[]{img});
                }
            }
        }
    }

    public static String getFileName(String line){
        return line.replaceAll(".png", "");
    }

    public static ImageView getImageView(String line, int TILE_SIZE){
        line = getFileName(line);
        InputStream tileIS = MineSweeper.class.getResourceAsStream("textures/" + line + ".png");
        return (tileIS == null) ?
                null :
                new ImageView(new javafx.scene.image.Image(tileIS,TILE_SIZE,TILE_SIZE,false,false));
    }
}
