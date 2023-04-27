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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

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
    private VBox vbox;
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
        loginBtn.setVisible(false);
        accountListView.setVisible(false);
        errorLabel.setText("Loading Data...");

        // execute database and API access tasks asynchronously
        AsyncUtils.asyncTask(() -> DBAccessManager.getRefTokenFromId(selectedAccId), result -> {
            // show login button again
            loginBtn.setVisible(true);
            accountListView.setVisible(true);
            errorLabel.setText("");
            if (result != null) {
                // change scene to main window
                mainApp.changeScene("Main", result);
            } else {
                errorLabel.setText("Error when getting account id & token from database.");
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
        // set items for accountListView
        accountListView.setItems(listViewItems);
        // execute the updating asyncronously
        AsyncUtils.asyncTask(() ->
                {
                    List<Account> accounts;
                    try {
                        accounts = DBAccessManager.getAccounts();
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
                    } else {
                        errStop("Error! Couldn't get accounts from db.");
                    }
                }
        );
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
        errorLabel.setText("Loading...");
        AsyncUtils.asyncTask(() -> {
                    // Check for internet connection
                    if (!NetworkUtils.hasInternet()) {
                        Platform.runLater(() -> errorLabel.setText("Error! No internet connection / Mastodon API Unreachable"));
                    }
                    // Check if db file exists
                    if (!DBAccessManager.isDbReachable()) {
                        try {
                            DBAccessManager.createDbFile();
                        } catch (IOException io) {
                            return "Error! Couldn't create db file. " + io.getMessage();
                        }
                    }
                    // Check if db tables are created
                    try {
                        DBAccessManager.checkAndCreateTables();
                    } catch (SQLException e) {
                        return "Error! Couldn't create db tables. " + e.getMessage();
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
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else if (item instanceof Account) {
                    setText(null);
                    AuthAccoCellController a = new AuthAccoCellController((Account) item);
                    a.setReference(thisclass);
                    a.getUI().setOnMouseClicked(event -> {
                        if (event.getClickCount() == 2) {
                            // perform login action on double-click
                            loginBtnClick();
                        }
                    });
                    setGraphic(a.getUI());
                } else if (item instanceof String && item.equals("Add Account")) {
                    setText(null);
                    AuthNewAccoCellController b = new AuthNewAccoCellController();
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
                selectedAccId = ((Account) listViewItems.get(selectedIndex)).id;
            } else {
                loginBtn.setDisable(true);
            }
        });
    }
}
