package net.ddns.celestgames.isometric_graphics;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import net.ddns.celestgames.isometric_graphics.game.Cube;
import net.ddns.celestgames.isometric_graphics.game.Map;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    Pane pane;
    final double scale = 0.5;
    final int maxHeight = (int) (10 * scale);
    final int size = (int) (32 * scale);
    Image image = new Image(String.valueOf(Main.class.getResource("isometric_cube_v2.png")));
    private Map map;

    private int offsetX = 600;
    private int offsetY = 128;
    private double off = 0.25;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.map = new Map(50, 50);

        for (int i = 0; i < this.map.getMap().length; i++) {
            for (int j = 0; j < this.map.getMap()[i].length; j++) {
                Cube cube = this.map.getCube(i, j);
                cube.setOffsetY(f(i + j));
                cube.setDirY(1);
                this.map.setCube(i, j, cube);
            }
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Platform.runLater(() -> {
                        pane.getChildren().clear();
                        Cube[][] cMap = (map.getMap());

                        for (int i = 0; i < cMap.length; i++) {
                            for (int j = 0; j < cMap[i].length; j++) {
                                ImageView imageView = new ImageView(image);
                                Cube cube = cMap[i][j];

                                imageView.setX(j * size - (j * size / 2.0) - (i * size / 2.0) + offsetX);
                                imageView.setY(i * size - (i * size / 4.0 * 3) + (j * size / 4.0) + offsetY + cube.getOffsetY());

                                imageView.setFitWidth(size);
                                imageView.setFitHeight(size);

                                if (cube.getOffsetY() > (maxHeight) || cube.getOffsetY() < -(maxHeight)) {
                                    cube.setDirY(-cube.getDirY());
                                }
                                cube.setOffsetY(cube.getOffsetY() + cube.getDirY());

                                pane.getChildren().add(imageView);
                            }
                        }
                    });
                }
            }
        });

        if (thread.isAlive()) {
            thread.interrupt();
            thread.start();
        } else {
            thread.start();
        }
    }

    public int f(int x) {
        return (int) (maxHeight * Math.sin(x * off));
    }
}