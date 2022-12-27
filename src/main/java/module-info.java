module net.ddns.celestgames.isometric_graphics {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;
    requires java.desktop;

    opens fr.celestgames.isoworlds to javafx.fxml;
    exports fr.celestgames.isoworlds;
    exports fr.celestgames.isoworlds.level;
    opens fr.celestgames.isoworlds.level to javafx.fxml;
    exports fr.celestgames.isoworlds.controllers;
    opens fr.celestgames.isoworlds.controllers to javafx.fxml;
}