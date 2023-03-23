package eus.ehu.txipironesmastodonfx.controllers.windowControllers;

import eus.ehu.txipironesmastodonfx.controllers.main.MainWindowController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class HeaderCellController {
    private MainWindowController master;
    @FXML
    private Label info= new Label();
    @FXML
    private AnchorPane anchor;


    /**
     * Constructor for the controller.
     * It will load itself at take consciousness (set itself as controller)
     * Also it will set the corresponding values for the account cell.
     *
     * @param cuerpo (Account) - The account to be displayed
     * @param master (MainWindowController)- The controller of the main class, will be used for internal comunication
     */

    public HeaderCellController(String cuerpo, MainWindowController master) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/eus/ehu/txipironesmastodonfx/maincell/headercell.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setReference(master);
        info.setText(cuerpo);
    }


    /**
     * Setter for the reference to the auth window controller
     *
     * @param thisclass (AuthWindowController) - The reference to the auth window controller
     */
    public void setReference(MainWindowController thisclass) {
        this.master = thisclass;
    }


    /**
     * Getter for the UI (AnchorPane)
     * This method will be used by the AuthWindowController
     * in order to display custom cells in the listview
     *
     * @return (AnchorPane) - The UI of the controller
     */
    public AnchorPane getUI() {
        return anchor;
    }

}
