package eus.ehu.txipironesmastodonfx.controllers;

import eus.ehu.txipironesmastodonfx.TxipironClient;
import eus.ehu.txipironesmastodonfx.data_access.DBAccessManager;
import eus.ehu.txipironesmastodonfx.data_access.NetworkUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;

public class AuthWindowController implements WindowController {

    private TxipironClient mainApp;

    public void setMain(TxipironClient app) {
        mainApp = app;
    }

    @FXML
    private VBox vbox;
    @FXML
    private ListView<AuthCell> accountListView;
    @FXML
    private Label errorLabel;
    @FXML
    private Button loginBtn;
    @FXML
    private Button exitBtn;

    @FXML
    void exitBtnClick() {
        // Exit the application without errors
        System.exit(0);
    }

    @FXML
    void loginBtnClick() {
        mainApp.changeScene("Main");
    }

    void errStop(String error) {
        errorLabel.setText(error);
        errorLabel.setVisible(true);
        exitBtn.setVisible(true);
        // Because it's a vbox, everything re-aligns perfectly
        // when we dynamically remove elements
        vbox.getChildren().remove(accountListView);
        vbox.getChildren().remove(loginBtn);
    }

    @FXML
    void initialize() {
        if (!NetworkUtils.hasInternet()) {
            errStop("Error! No internet connection / Mastodon API Unreachable");
            return;
        }
        if (!DBAccessManager.isDbReachable()) {
            try {
                DBAccessManager.createDbFile();
            } catch (IOException io) {
                errStop("Error! Couldn't create db file. " + io.getMessage());
                return;
            }
        }
        try {
            DBAccessManager.checkAndCreateTables();
        } catch (SQLException e) {
            errStop("Error! Couldn't create db tables. " + e.getMessage());
            return;
        }

    }


}
