package eus.ehu.txipironesmastodonfx.controllers.auth;

import eus.ehu.txipironesmastodonfx.data_access.DBAccessManager;
import eus.ehu.txipironesmastodonfx.domain.Account;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Controller class for the account cell in the listview.
 *
 * @author Nicolás Aguado
 * @author Haizea Bermejo
 * @author Marcos Chouciño
 * @author Xiomara Cáceces
 */
public class AuthAccoCellController {
    @FXML
    private ImageView avatarImg;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Label accIdTxt;
    @FXML
    private Label userNameTxt;
    private AuthWindowController master;

    /**
     * Constructor for the controller.
     * It will load itself at take consciousness (set itself as controller)
     * Also it will set the corresponding values for the account cell.
     *
     * @param account (Account) - The account to be displayed
     */
    public AuthAccoCellController(Account account) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/eus/ehu/txipironesmastodonfx/auth/authaccocell.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // set the values for the account cell
        userNameTxt.setText(account.acct);
        accIdTxt.setText(account.id);
        avatarImg.setImage(new Image(account.avatar));
    }

    /**
     * Getter for the UI (AnchorPane)
     * This method will be used by the AuthWindowController
     * in order to display custom cells in the listview
     *
     * @return (AnchorPane) - The UI of the controller
     */
    public AnchorPane getUI() {
        return anchorPane;
    }

    /**
     * Setter for the reference to the auth window controller
     *
     * @param thisclass (AuthWindowController) - The reference to the auth window controller
     */
    public void setReference(AuthWindowController thisclass) {
        this.master = thisclass;
    }

    /**
     * Method that will be called when the remove button is clicked.
     * It will remove an account from the database and the system variable
     * associated to it.
     * It will also refresh the listview.
     * If something goes wrong in that process, it'll simply do nothing.
     */
    @FXML
    void removeAccBtnClick() {
        // Remove account from database
        try {
            DBAccessManager.removeAccountFromDbId(accIdTxt.getText());
            // Refresh listview
            master.updateListView();
        } catch (SQLException e) {
            // if an error occurs, do nothing
        }
    }
}

