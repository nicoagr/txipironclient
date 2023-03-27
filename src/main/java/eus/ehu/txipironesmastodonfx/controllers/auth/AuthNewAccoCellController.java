package eus.ehu.txipironesmastodonfx.controllers.auth;

import eus.ehu.txipironesmastodonfx.data_access.APIAccessManager;
import eus.ehu.txipironesmastodonfx.data_access.AsyncUtils;
import eus.ehu.txipironesmastodonfx.data_access.DBAccessManager;
import eus.ehu.txipironesmastodonfx.data_access.NetworkUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Controller class for the new account cell in the listview.
 *
 * @author Nicolás Aguado
 * @author Haizea Bermejo
 * @author Marcos Chouciño
 * @author Xiomara Cáceces
 */
public class AuthNewAccoCellController {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TextField mstdTokenTxt;
    @FXML
    private Button addAccBtn;
    @FXML
    private Label errorTxt;
    private AuthWindowController master;

    /**
     * Add account button: It will perform
     * a series of checks. If everything is ok,
     * it will add the account to the database and it
     * will refresh the listview.
     * If something goes wrong, it'll display a message
     * indicating that fact.
     */
    @FXML
    void addAccBtnClick() {
        String token = mstdTokenTxt.getText();
        mstdTokenTxt.setText("Loading...");
        errorTxt.setText("");

        AsyncUtils.asyncTask(() -> {
            // Check for internet connection
            if (!NetworkUtils.hasInternet()) {
                return "Error! No internet connection / Mastodon API Unreachable";
            }
            // verify token present
            if (mstdTokenTxt.getText().isEmpty()) {
                return "Error! Token is empty.";
            }
            // check if token is valid
            String id = APIAccessManager.verifyAndGetId(token);
            if (id == null) {
                return "Error! Token is invalid";
            }
            // check if account is not in database
            try {
                if (DBAccessManager.isAccountInDb(id)) {
                    // If the account is in the database, stop the process
                    return "Error! Account already in database";
                }
            } catch (SQLException e) {
                return "Error when checking if account is in database";
            }
            // add account to database
            try {
                DBAccessManager.addAccount(id, token);
            } catch (SQLException e) {
                return "Error when adding account to database";
            }
            return null;
        }, errorMsg -> {
            mstdTokenTxt.setText("");
            if (errorMsg != null) {
                errorTxt.setText(errorMsg);
            } else {
                // refresh listview
                master.updateListView();
            }
        });
    }


    /**
     * Constructor for the controller.
     * It will load itself at take consciousness (set itself as controller)
     */
    public AuthNewAccoCellController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/eus/ehu/txipironesmastodonfx/auth/authnewcell.fxml"));
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
     * @param authcont (AuthWindowController) - The reference to the auth window controller
     */
    public void setReference(AuthWindowController authcont) {
        this.master = authcont;
    }
}
