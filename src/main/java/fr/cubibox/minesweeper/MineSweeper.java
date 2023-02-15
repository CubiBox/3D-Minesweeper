package fr.cubibox.minesweeper;

import fr.cubibox.minesweeper.controllers.Custom;
import fr.cubibox.minesweeper.level.Map;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

public class MineSweeper extends Application {
    public static int selectDifficulty = 0;

    public static Stage stage;

    public static Scene scene;

    public static HashMap<String, ImageView[]> tileSprites = new HashMap<>();
    public static Map map;
    public static String globalStyle;


    @Override
    public void start(Stage stage) {
        globalStyle = getClass().getResource("style.css").toExternalForm();
        MineSweeper.stage = stage;
        goToPage("start");
    }

    public static void mainMenu(){
        goToPage("menu");
    }

    public static void play(){
        map = switch (selectDifficulty) {
            case 0 :
                Map easyMap = Map.voidMap(5,5);
                easyMap.setNbBombs(10);
                yield easyMap;
            case 1 :
                Map mediumMap = Map.voidMap(8,10);
                mediumMap.setNbBombs(50);
                yield mediumMap;
            case 2 :
                Map hardMap = Map.voidMap(15,15);
                hardMap.setNbBombs(180);
                yield hardMap;
            default : yield Custom.previewMap;
        };
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
            Parent root = FXMLLoader.load(MineSweeper.class.getResource("scenes/"+str+".fxml"));
            MineSweeper.scene = new Scene(root);
        }
        catch(Exception ignored) {}

        showStage();
    }

    public static void showStage(){
        //scene.getStylesheets().add(globalStyle);
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(event -> System.exit(0));
    }

    public static void launch(String[] args) {
        launch();
    }


    public static void getAllSprite(int TILE_SIZE){
        File folder = new File(MineSweeper.class.getResource("textures").getFile());
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
