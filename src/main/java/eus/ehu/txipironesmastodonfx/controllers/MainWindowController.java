package eus.ehu.txipironesmastodonfx.controllers;

import eus.ehu.txipironesmastodonfx.TxipironClient;

public class MainWindowController implements WindowController {
    private TxipironClient mainApp;
    public void setMain(TxipironClient app) {
        mainApp = app;
    }
}
