package eus.ehu.txipironesmastodonfx.controllers.auth;

import eus.ehu.txipironesmastodonfx.TxipironClient;
import eus.ehu.txipironesmastodonfx.data_access.*;
import eus.ehu.txipironesmastodonfx.data_access.AsyncUtils;
import eus.ehu.txipironesmastodonfx.data_access.DBAccessManager;
import eus.ehu.txipironesmastodonfx.data_access.NetworkUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
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
 * @author Xiomara Cáceces
 * @author Marcos Chouciño
 */
public class AuthNewAccoCellController {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Hyperlink accessTokenAuthLink;
    @FXML
    private Button oauthBtn;
    @FXML
    private TextField mstdTokenTxt;
    @FXML
    private Button addAccBtn;
    @FXML
    private Label errorTxt;
    private AuthWindowController master;
    private Boolean oauth = null;

    /**
     * This method will call the specific actions
     * for each authentication form when the button is clicked.
     */
    @FXML
    void addAccBtnClick() {
        if (oauth == null) {
            return;
        }
        errorTxt.setText("");
        if (oauth) {
            oauthAuthentication(mstdTokenTxt.getText());
        } else {
            accessTokenAuthentication(mstdTokenTxt.getText());
        }
    }

    /**
     * OAUTH AUTHENTICATION:
     * Will check if the authCode is correct,
     * and will get the access token from the API.
     * Then it will call the access token authentication process.
     *
     * @param authCode (String) - The auth code to be checked.
     */
    private void oauthAuthentication(String authCode) {
        mstdTokenTxt.setText("Loading oauth authentication...");
        addAccBtn.setDisable(true);
        AsyncUtils.asyncTask(() -> {
            String token = null;
            if (!NetworkUtils.hasInternet()) {
                return "Error! No internet connection / Mastodon API Unreachable";
            }
            if (authCode.isEmpty()) {
                return "Error! Auth code is empty.";
            }
            try {
                token = APIAccessManager.getTokenFromAuthCode(authCode);
            } catch (IOException e) {
                return "Error! AuthCode invalid";
            }
            return token;
        }, param -> {
            mstdTokenTxt.setText("");
            if (param == null) {
                errorTxt.setText("Error - AuthCode invalid");
                addAccBtn.setDisable(false);
            } else if (param.toLowerCase().contains("error")) {
                errorTxt.setText(param);
                addAccBtn.setDisable(false);
            } else {
                accessTokenAuthentication(param);
            }
        });
    }

    /**
     * Access token authentication process.
     */
    private void accessTokenAuthentication(String token) {
        mstdTokenTxt.setText("Validating token...");
        errorTxt.setText("");
        addAccBtn.setDisable(true);
        AsyncUtils.asyncTask(() -> {
            // Check for internet connection
            if (!NetworkUtils.hasInternet()) {
                return "Error! No internet connection / Mastodon API Unreachable";
            }
            // verify token present
            if (token.isEmpty()) {
                return "Error! Token is empty.";
            }
            // check if token is valid
            String id = APIAccessManager.verifyAndGetId(token);
            if (id == null) {
                return "Error! Provided code is invalid";
            }
            // check if account is not in database
            try {
                if (DBAccessManager.isAccountInDb(id)) {
                    // If the account is in the database, stop the process
                    return "Error! Account already added";
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
                addAccBtn.setDisable(false);
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
     * Not a very descriptive name,
     * but the method will handle going back
     * and switching between auth methods.
     */
    @FXML
    void accessTokenAuthClick() {
        if (oauth == null) {
            oauth = false;
            oauthBtn.setVisible(false);
            mstdTokenTxt.setPromptText("Mastodon API Access Token - 44 characters long");
            accessTokenAuthLink.setText("Go back");
            addAccBtn.setVisible(true);
            mstdTokenTxt.setVisible(true);
        } else {
            oauth = null;
            oauthBtn.setVisible(true);
            errorTxt.setText("");
            accessTokenAuthLink.setText("Paste access token instead");
            addAccBtn.setVisible(false);
            mstdTokenTxt.setVisible(false);
        }
    }

    /**
     * This method will select
     * the oauth authentication method.
     * It will hide and show the appropriate
     * elements in the UI.
     */
    @FXML
    void oauthBtnClick() {
        oauth = true;
        NetworkUtils.openWebPage("https://mastodon.social/oauth/authorize?response_type=code&client_id=" + TxipironClient.MASTODON_APP_ID + "&scope=read+write+follow+push&redirect_uri=urn:ietf:wg:oauth:2.0:oob&force_login=true");
        oauthBtn.setVisible(false);
        accessTokenAuthLink.setText("Go back");
        addAccBtn.setVisible(true);
        mstdTokenTxt.setPromptText("Paste authorization code here");
        mstdTokenTxt.setVisible(true);
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
