module net.ddns.celestgames.isometric_graphics {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;
    requires java.desktop;

    exports fr.cubibox.minesweeper.level;
    opens fr.cubibox.minesweeper.level to javafx.fxml;
    exports fr.cubibox.minesweeper.controllers;
    opens fr.cubibox.minesweeper.controllers to javafx.fxml;
    exports fr.cubibox.minesweeper;
    opens fr.cubibox.minesweeper to javafx.fxml;
}