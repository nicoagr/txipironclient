package eus.ehu.txipironesmastodonfx.controllers.windowControllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class followCellController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label Id;

    @FXML
    private Button followbutton;

    @FXML
    private ImageView icon;

    @FXML
    private Label info;

    @FXML
    private Label username;

    @FXML
    void followAction(ActionEvent event) {

    }

    @FXML
    void initialize() {
        assert Id != null : "fx:id=\"Id\" was not injected: check your FXML file 'followcell.fxml'.";
        assert followbutton != null : "fx:id=\"followbutton\" was not injected: check your FXML file 'followcell.fxml'.";
        assert icon != null : "fx:id=\"icon\" was not injected: check your FXML file 'followcell.fxml'.";
        assert info != null : "fx:id=\"info\" was not injected: check your FXML file 'followcell.fxml'.";
        assert username != null : "fx:id=\"username\" was not injected: check your FXML file 'followcell.fxml'.";

    }

}
