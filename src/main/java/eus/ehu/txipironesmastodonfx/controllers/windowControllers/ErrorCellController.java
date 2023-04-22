package eus.ehu.txipironesmastodonfx.controllers.windowControllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Controller class for the error cell.
 * It will be used to display errors.
 *
 * @author Nicolás Aguado
 * @author Haizea Bermejo
 * @author Xiomara Cáceces
 * @author Marcos Chouciño
 */
public class ErrorCellController {
    @FXML
    private Label errorTxt;

    @FXML
    private VBox vbox;

    /**
     * Constructor for the controller.
     * It will load itself at take consciousness (set itself as controller)
     * Also it will set the corresponding values for the error cell.
     *
     * @param error (String) - The error message to be displayed
     */

    public ErrorCellController(String error) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/eus/ehu/txipironesmastodonfx/maincell/errorcell.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        errorTxt.setText(error);
    }


    /**
     * Getter for the UI (VBox)
     * This method will be used by the MainWindowController
     * in order to display custom cells in the listview
     *
     * @return (VBox) - The UI of the controller
     */
    public VBox getUI() {
        return vbox;
    }

}
