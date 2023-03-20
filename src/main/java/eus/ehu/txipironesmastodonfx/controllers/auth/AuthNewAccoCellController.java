package eus.ehu.txipironesmastodonfx.controllers.auth;

import eus.ehu.txipironesmastodonfx.data_access.APIAccessManager;
import eus.ehu.txipironesmastodonfx.data_access.NetworkUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class AuthNewAccoCellController {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TextField mstdTokenTxt;

    @FXML
    private Label errorTxt;

    @FXML
    void addAccBtnClick() {
        // Check for internet connection
        if (!NetworkUtils.hasInternet()) {
            errorTxt.setText("Error! No internet connection / Mastodon API Unreachable");
            return;
        }
        // verify token present
        if (mstdTokenTxt.getText().isEmpty()) {
            errorTxt.setText("Error! Token is empty");
            return;
        }
        // check if token is valid
        String username = APIAccessManager.verifyAndGetId(mstdTokenTxt.getText());
        // check if account is not in database
        // add account to database
        // refresh listview
    }

    public AuthNewAccoCellController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/eus/ehu/txipironesmastodonfx/auth/authnewcell.fxml"));
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
