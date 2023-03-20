module eus.ehu.txipironesmastodonfx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires com.google.gson;
    requires okhttp3;
    requires java.sql.rowset;

    opens eus.ehu.txipironesmastodonfx to javafx.fxml;
    exports eus.ehu.txipironesmastodonfx.controllers;
    exports eus.ehu.txipironesmastodonfx to javafx.graphics;
    opens eus.ehu.txipironesmastodonfx.controllers to javafx.fxml;
    exports eus.ehu.txipironesmastodonfx.controllers.main;
    opens eus.ehu.txipironesmastodonfx.controllers.main to javafx.fxml;
    exports eus.ehu.txipironesmastodonfx.controllers.auth;
    opens eus.ehu.txipironesmastodonfx.controllers.auth to javafx.fxml;
    exports eus.ehu.txipironesmastodonfx.domain;
    opens eus.ehu.txipironesmastodonfx.data_access to com.google.gson;
}