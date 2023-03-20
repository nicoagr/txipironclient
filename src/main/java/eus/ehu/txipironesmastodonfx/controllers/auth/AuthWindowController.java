package eus.ehu.txipironesmastodonfx.controllers.auth;

import eus.ehu.txipironesmastodonfx.TxipironClient;
import eus.ehu.txipironesmastodonfx.controllers.WindowController;
import eus.ehu.txipironesmastodonfx.data_access.DBAccessManager;
import eus.ehu.txipironesmastodonfx.data_access.NetworkUtils;
import eus.ehu.txipironesmastodonfx.domain.Account;
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
 * @author Marcos Chouciño
 * @author Xiomara Cáceces
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

    @FXML
    void loginBtnClick() {
        mainApp.changeScene("Main");
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
    private void updateListView() {
        // set items for accountListView
        accountListView.setItems(listViewItems);
        // Download accounts from db
        List<Account> accounts;
        try {
            accounts = DBAccessManager.getAccounts();
        } catch (SQLException e) {
            errStop("Error! Couldn't get accounts from db. " + e.getMessage());
            return;
        }
        // Clear ListView
        listViewItems.clear();
        // Add accounts to ListView
        listViewItems.addAll(accounts);
        // Add "Add Account" cell to ListView
        listViewItems.add("Add Account");
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
        // Check for internet connection
        if (!NetworkUtils.hasInternet()) {
            errStop("Error! No internet connection / Mastodon API Unreachable");
            return;
        }
        // Check if db file exists
        if (!DBAccessManager.isDbReachable()) {
            try {
                DBAccessManager.createDbFile();
            } catch (IOException io) {
                errStop("Error! Couldn't create db file. " + io.getMessage());
                return;
            }
        }
        // Check if db tables are created
        try {
            DBAccessManager.checkAndCreateTables();
        } catch (SQLException e) {
            errStop("Error! Couldn't create db tables. " + e.getMessage());
            return;
        }
        // modify the ListView's cell factory to use our custom cells and styles
        accountListView.getStyleClass().add("list-cell");
        accountListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else if (item instanceof Account) {
                    setText(null);
                    setGraphic(new AuthAccoCellController((Account) item).getUI());
                } else if (item instanceof String && item.equals("Add Account")) {
                    setText(null);
                    setGraphic(new AuthNewAccoCellController().getUI());
                }
            }
        });
        // add a listener to the ListView's selection model
        accountListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // get the index of the selected cell
            int selectedIndex = accountListView.getSelectionModel().getSelectedIndex();
            // if the selected cell is an account, enable the login button
            // if the selected cell is not an account, disable the login button
            loginBtn.setDisable(!(listViewItems.get(selectedIndex) instanceof Account));
        });
        // Update ListView
        updateListView();

    }

}
