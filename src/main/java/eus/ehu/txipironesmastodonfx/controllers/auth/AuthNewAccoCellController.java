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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ResourceBundle;

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
    private static final Logger logger = LogManager.getLogger("AuthNewAccoCellController");

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
        String load = TxipironClient.s("Load");
        mstdTokenTxt.setText(load);
        logger.info("Starting oauth authentication...");
        addAccBtn.setDisable(true);
        AsyncUtils.asyncTask(() -> {
            String token = null;
            if (!NetworkUtils.hasInternet()) {
                logger.error("No internet connection / Mastodon API Unreachable");
                return TxipironClient.s("Error10");
            }
            if (authCode.isEmpty()) {
                logger.warn("Auth code is empty");
                return TxipironClient.s("Error11");
            }

            token = APIAccessManager.getTokenFromAuthCode(authCode);
            if (token == null) {
                logger.error("Error when getting token from auth code (AuthCode invalid)");
                return "Error! AuthCode invalid";
            }
            return token;
        }, param -> {
            mstdTokenTxt.setText("");
            if (param == null) {
                String error = TxipironClient.s("Error12");
                errorTxt.setText(error);
                addAccBtn.setDisable(false);
            } else if (param.toLowerCase().contains("error")) {
                errorTxt.setText(param);
                addAccBtn.setDisable(false);
            } else {
                logger.info("OAuth authentication successful.");
                accessTokenAuthentication(param);
            }
        });
    }

    /**
     * Access token authentication process.
     */
    private void accessTokenAuthentication(String token) {
        String validTk = TxipironClient.s("Token");
        mstdTokenTxt.setText(validTk);
        logger.info("Starting access token authentication...");
        errorTxt.setText("");
        addAccBtn.setDisable(true);
        AsyncUtils.asyncTask(() -> {
            // Check for internet connection
            if (!NetworkUtils.hasInternet()) {
                 return TxipironClient.s("Error10");
            }
            // verify token present
            if (token.isEmpty()) {
                return TxipironClient.s("Error13");
            }
            // check if token is valid
            String id = APIAccessManager.verifyAndGetId(token);
            if (id == null) {
                return TxipironClient.s("Error14");
            }
            // check if account is not in database
            try {
                if (DBAccessManager.isAccountInDb(id)) {
                    // If the account is in the database, stop the process
                    return TxipironClient.s("Error15");
                }
            } catch (SQLException e) {
                return TxipironClient.s("Error16");
            }
            // add account to database
            try {
                DBAccessManager.addAccount(id, token);
            } catch (SQLException e) {
                return TxipironClient.s("Error17");
            }
            return null;
        }, errorMsg -> {
            mstdTokenTxt.setText("");
            if (errorMsg != null) {
                errorTxt.setText(errorMsg);
                logger.error(errorMsg);
                addAccBtn.setDisable(false);
            } else {
                // refresh listview
                logger.info("Access token authentication successful. Adding account");
                master.updateListView();
            }
        });
    }


    /**
     * Constructor for the controller.
     * It will load itself at take consciousness (set itself as controller)
     */
    public AuthNewAccoCellController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/eus/ehu/txipironesmastodonfx/auth/authnewcell.fxml"),
                ResourceBundle.getBundle("strings", TxipironClient.lang));
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
            logger.info("Switching to access token authentication");
            oauth = false;
            oauthBtn.setVisible(false);
            String mToken = TxipironClient.s("MastodonToken");
            mstdTokenTxt.setPromptText(mToken);
            accessTokenAuthLink.setText(TxipironClient.s("Back"));
            addAccBtn.setVisible(true);
            mstdTokenTxt.setVisible(true);
        } else {
            oauth = null;
            logger.info("Cancelling authentication");
            oauthBtn.setVisible(true);
            errorTxt.setText("");
            String pToken = TxipironClient.s("PasteTk");
            accessTokenAuthLink.setText(pToken);
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
        logger.info("Opening oauth link...");
        oauth = true;
        NetworkUtils.openWebPage("https://mastodon.social/oauth/authorize?response_type=code&client_id=" + TxipironClient.MASTODON_APP_ID + "&scope=read+write+follow+push&redirect_uri=urn:ietf:wg:oauth:2.0:oob&force_login=true");
        oauthBtn.setVisible(false);
        String back = TxipironClient.s("Back");
        accessTokenAuthLink.setText(back);
        addAccBtn.setVisible(true);
        String pAuth = TxipironClient.s("PasteAuth");
        mstdTokenTxt.setPromptText(pAuth);
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
