package net.ddns.celestgames.isometric_graphics;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private Pane pane;

    private final int scale = 2;
    private final int size = 32 * scale;

    private final ArrayList<Cube> cubes = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Cube cube = new Cube();

                if (i % 2 == 0) {
                    cube.setX(j * size);
                } else {
                    cube.setX((int) (j * size - (size / 2.0)));
                }

                cube.setY((int) (i * (size / 4.0)));
                cube.setOffsetY(f(j));

                cubes.add(cube);
            }
        }
    }

    public int f(int x) {
        return (int) (10 * Math.sin(x));
    }

    public void run() {
        Image image = new Image(String.valueOf(Main.class.getResource("isometric_cube_v2.png")));
        while (true) {
            pane.getChildren().clear();
            for (Cube cube : cubes) {
                ImageView imageView = new ImageView(image);

                imageView.setX(cube.getX());
                imageView.setY(cube.getY() + cube.getOffsetY());

                imageView.setFitWidth(size);
                imageView.setFitHeight(size);

                pane.getChildren().add(imageView);

                if (cube.getOffsetY() + cube.getDirY() > 10 || cube.getOffsetY() + cube.getDirY() < -10) {
                    cube.setDirY(cube.getDirY() * -1);
                }

                cube.setOffsetY(cube.getOffsetY() + cube.getDirY());
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}