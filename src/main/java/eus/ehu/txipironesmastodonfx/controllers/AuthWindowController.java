package eus.ehu.txipironesmastodonfx.controllers;

import eus.ehu.txipironesmastodonfx.TxipironClient;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class AuthWindowController implements WindowController {

    private TxipironClient mainApp;
    public void setMain(TxipironClient app) {
        mainApp = app;
    }

    @FXML
    private ListView<AuthCell> accountListView;

    @FXML
    void loginBtnClick() {

    }


}
