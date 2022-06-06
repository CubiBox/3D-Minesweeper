module net.ddns.celestgames.isometric_graphics {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;

    opens net.ddns.celestgames.isometric_graphics to javafx.fxml;
    exports net.ddns.celestgames.isometric_graphics;
    exports net.ddns.celestgames.isometric_graphics.game;
    opens net.ddns.celestgames.isometric_graphics.game to javafx.fxml;
}