module eus.ehu.txipironesmastodonfx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens eus.ehu.txipironesmastodonfx to javafx.fxml;
    exports eus.ehu.txipironesmastodonfx.fx;
    opens eus.ehu.txipironesmastodonfx.fx to javafx.fxml;
}