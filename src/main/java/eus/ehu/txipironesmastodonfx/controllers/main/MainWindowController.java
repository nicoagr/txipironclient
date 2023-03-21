package eus.ehu.txipironesmastodonfx.controllers.main;

import eus.ehu.txipironesmastodonfx.TxipironClient;
import eus.ehu.txipironesmastodonfx.controllers.WindowController;
import javafx.fxml.FXML;

/**
 * Controller class for the main window.
 *
 * @author Nicolás Aguado
 * @author Haizea Bermejo
 * @author Marcos Chouciño
 * @author Xiomara Cáceces
 */
public class MainWindowController implements WindowController {
    private TxipironClient mainApp;
    private Integer ref;

    /**
     * Sets a reference to the main application
     *
     * @param app (TxipironClient) - The main application
     */
    public void setMain(TxipironClient app) {
        mainApp = app;
    }

    /**
     * Changes the scene to the account selection scene
     * when the change account button is clicked
     */
    @FXML
    void changeAcctBtnClick() {
        mainApp.changeScene("Auth", null);
    }

    /**
     * Sets the reference of the current logged in user
     *
     * @param ref (Integer) - The reference of the current logged in user
     */
    @Override
    public void setRef(Integer ref) {
        this.ref = ref;
        System.out.println(ref);
    }
}
