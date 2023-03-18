package eus.ehu.txipironesmastodonfx.controllers.auth;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class AuthNewAccoCellController {

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TextField mstdTokenTxt;

    @FXML
    void addAccBtnClick() {

    }

    @FXML
    void initialize() {

    }

    public AuthNewAccoCellController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("authnewcell.fxml"));
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public AnchorPane getUI() {
        return anchorPane;
    }

}
