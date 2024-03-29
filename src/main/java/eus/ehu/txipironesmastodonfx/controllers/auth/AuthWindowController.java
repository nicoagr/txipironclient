package eus.ehu.txipironesmastodonfx.controllers.auth;

import eus.ehu.txipironesmastodonfx.TxipironClient;
import eus.ehu.txipironesmastodonfx.controllers.WindowController;
import eus.ehu.txipironesmastodonfx.data_access.AsyncUtils;
import eus.ehu.txipironesmastodonfx.data_access.DBAccessManager;
import eus.ehu.txipironesmastodonfx.data_access.NetworkUtils;
import eus.ehu.txipironesmastodonfx.domain.Account;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the authentication window.
 * This controller will check basic info (internet, database, etc)
 * and it will show the accounts that are stored in the database.
 * If there are no accounts, it will show a message to the user.
 * It also handles cells (listview).
 * Provides the main entry point to the application through an
 * account selection.
 *
 * @author Nicolás Aguado
 * @author Haizea Bermejo
 * @author Xiomara Cáceces
 * @author Marcos Chouciño
 */
public class AuthWindowController implements WindowController {

    private TxipironClient mainApp;
    private static final Logger logger = LogManager.getLogger("AuthWindowController");

    /**
     * Sets a reference to the main application
     *
     * @param app The main application
     */
    public void setMain(TxipironClient app) {
        mainApp = app;
    }

    private String selectedAccId;
    @FXML
    public VBox vbox;
    @FXML
    private ListView<Object> accountListView;
    private ObservableList<Object> listViewItems = FXCollections.observableArrayList();
    @FXML
    private Label errorLabel;
    @FXML
    private Button loginBtn;
    @FXML
    private Button exitBtn;

    /**
     * Exits the application with status code 0
     */
    @FXML
    void exitBtnClick() {
        // Exit the application without errors
        System.exit(0);
    }

    /**
     * Login button click action.
     * It will check if there is internet connection.
     * It will check if the selected account is in the database.
     * It will get the reference of the account.
     * It will get the sysvar of the account.
     * It will download the toots of the account.
     * It will insert the toots in the database.
     * It will download the follows (followers/followings) of the account.
     * It will insert the follows (followers/followings) in the database.
     * It will change the scene to the main window.
     */
    @FXML
    void loginBtnClick() {
        // hide everything - show loading
        logger.debug("Attempting login...");
        loginBtn.setVisible(false);
        accountListView.setVisible(false);
        String ld  = TxipironClient.s("LoadData");
        errorLabel.setText(ld);

        // execute database and API access tasks asynchronously
        AsyncUtils.asyncTask(() -> DBAccessManager.getRefTokenFromId(selectedAccId), result -> {
            // show login button again
            loginBtn.setVisible(true);
            accountListView.setVisible(true);
            errorLabel.setText("");
            if (result != null) {
                // change scene to main window
                logger.debug("RefToken successfully retrieved from database.");
                mainApp.changeScene("Main", result);
            } else {
                logger.error("Error when getting ref token from database.");
                String error = TxipironClient.s("Error18");
                errorLabel.setText(error);
            }
        });
    }


    /**
     * This method will be called when a fatal error occurs.
     * It will show the error message and the exit button,
     * hiding all normal workflow and disabling the login button.
     *
     * @param error (String) - The error message to be shown
     */
    void errStop(String error) {
        errorLabel.setText(error);
        errorLabel.setVisible(true);
        exitBtn.setVisible(true);
        logger.error("Critical app stop - " + error);
        // Because it's a vbox, everything re-aligns perfectly
        // when we dynamically remove elements
        vbox.getChildren().remove(accountListView);
        vbox.getChildren().remove(loginBtn);
    }

    /**
     * This method will refresh the listview.
     * It will download the accounts from the database,
     * and it will add them to the observable list.
     * It will also add the "Add Account" cell.
     * And finally, it will link the observable list to the listview.
     */
    protected void updateListView() {
        logger.debug("Attempting to update listview...");
        // set items for accountListView
        accountListView.setItems(listViewItems);
        // execute the updating asyncronously
        AsyncUtils.asyncTask(() ->
                {
                    List<Account> accounts;
                    try {
                        accounts = DBAccessManager.getAccounts();
                        logger.debug("Accounts successfully retrieved from database.");
                    } catch (SQLException e) {
                        accounts = null;
                    }
                    return accounts;
                }, accounts -> {
                    if (accounts != null) {
                        // Clear ListView
                        listViewItems.clear();
                        // Add accounts to ListView
                        listViewItems.addAll(accounts);
                        // Add "Add Account" cell to ListView
                        listViewItems.add("Add Account");
                        logger.debug("ListView updated successfully.");
                    } else {
                        errStop("Error! Couldn't get accounts from db.");
                    }
                }
        );
    }

    /**
     * Returns the main scene wrapper. Used by the main app
     * for applying styles
     * @return (Parent) - The main scene wrapper
     */
    @Override
    public Parent getSceneWrapper() {
        return vbox;
    }

    /**
     * Starting point of this window.
     * Will check for internet connection, database
     * existance and tables inside the database.
     * If everything is ok, it will update the listview so
     * it can handle our custom cells. (cell factory)
     * Finally, it will refresh the listview.
     */
    @FXML
    void initialize() {
        logger.debug("Starting auth initial tasks...");
        String load = TxipironClient.s("Load");
        errorLabel.setText(load);
        AsyncUtils.asyncTask(() -> {
                    // Check for internet connection
                    if (!NetworkUtils.hasInternet()) {
                        logger.warn("No internet connection / Mastodon API Unreachable");
                        String noCon = TxipironClient.s("Error10");
                        Platform.runLater(() -> errorLabel.setText(noCon));
                    }
                    // Check if db file exists
                    if (!DBAccessManager.isDbReachable()) {
                        try {
                            logger.info("Database file not found - Creating new one...");
                            DBAccessManager.createDbFile();
                        } catch (IOException io) {
                            String error = TxipironClient.s("Error20");
                            return error + io.getMessage();
                        }
                    }
                    // Check if db tables are created
                    try {
                        logger.debug("Checking if db tables exist...");
                        DBAccessManager.checkAndCreateTables();
                    } catch (SQLException e) {
                        String error = TxipironClient.s("Error21");
                        return error + e.getMessage();
                    }
                    return null;
                }, param -> {
                    if (param != null) {
                        errStop(param);
                    } else {
                        errorLabel.setText("");
                        // Update ListView
                        updateListView();
                    }
                }
        );

        // modify the ListView's cell factory to use our custom cells and styles
        AuthWindowController thisclass = this;
        accountListView.setCellFactory(param -> new ListCell<>() {
            AuthAccoCellController a = new AuthAccoCellController();
            AuthNewAccoCellController b = new AuthNewAccoCellController();

            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);
                if (empty || item == null) {
                    setGraphic(null);
                } else if (item instanceof Account) {
                    a.setReference(thisclass);
                    a.getUI().setOnMouseClicked(event -> {
                        if (event.getClickCount() == 2) {
                            // perform login action on double-click
                            logger.debug("Double click on account object detected");
                            loginBtnClick();
                        }
                    });
                    setGraphic(a.getUI());
                    a.loadAccount((Account) item);
                } else if (item instanceof String && item.equals("Add Account")) {
                    b.setReference(thisclass);
                    setGraphic(b.getUI());
                }
            }
        });
        // add a listener to the ListView's selection model
        accountListView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            // get the selected index
            int selectedIndex = newValue.intValue();
            // if the selected cell is an account, enable the login button
            // if the selected cell is not an account, disable the login button
            if (selectedIndex >= 0 && selectedIndex < listViewItems.size() && listViewItems.get(selectedIndex) instanceof Account) {
                loginBtn.setDisable(false);
                logger.debug("New selected account detected! - id:" + ((Account) listViewItems.get(selectedIndex)).id);
                selectedAccId = ((Account) listViewItems.get(selectedIndex)).id;
            } else {
                loginBtn.setDisable(true);
                logger.debug("A non-account cell was selected! - Disabling login button");
            }
        });
        logger.info("Authentication initial tasks finished - Client operative");
    }

    // Tasks to perform after the window is shown
    public void initialTask() {
        // Reset selected account
        selectedAccId = null;
        loginBtn.setDisable(true);
        // Refresh ListView
        updateListView();
    }
}
