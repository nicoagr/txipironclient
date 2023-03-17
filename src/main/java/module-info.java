module eus.ehu.txipironesmastodonfx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens eus.ehu.txipironesmastodonfx to javafx.fxml;
    exports eus.ehu.txipironesmastodonfx.controllers;
    exports eus.ehu.txipironesmastodonfx to javafx.graphics;
    opens eus.ehu.txipironesmastodonfx.controllers to javafx.fxml;
}