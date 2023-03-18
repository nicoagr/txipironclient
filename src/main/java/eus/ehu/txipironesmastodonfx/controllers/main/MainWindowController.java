package eus.ehu.txipironesmastodonfx.controllers.main;

import eus.ehu.txipironesmastodonfx.TxipironClient;
import eus.ehu.txipironesmastodonfx.controllers.WindowController;

public class MainWindowController implements WindowController {
    private TxipironClient mainApp;
    public void setMain(TxipironClient app) {
        mainApp = app;
    }
}
