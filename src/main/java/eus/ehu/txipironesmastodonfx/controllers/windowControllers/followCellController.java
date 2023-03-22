package eus.ehu.txipironesmastodonfx.controllers.windowControllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import eus.ehu.txipironesmastodonfx.controllers.auth.AuthWindowController;
import eus.ehu.txipironesmastodonfx.controllers.main.MainWindowController;
import eus.ehu.txipironesmastodonfx.domain.Account;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class followCellController {
    private MainWindowController master;
    @FXML
    private Button followbutton;
    @FXML
    private AnchorPane anchor;
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label Id;



    @FXML
    private ImageView icon;

    @FXML
    private Label info;

    @FXML
    private Label username;



    /**
     * Constructor for the controller.
     * It will load itself at take consciousness (set itself as controller)
     * Also it will set the corresponding values for the account cell.
     *
     * @param account (Account) - The account to be displayed
     */
    public followCellController(Account account) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/eus/ehu/txipironesmastodonfx/followcell.fxml"));//ejemplo: /eus/ehu/txipironesmastodonfx/auth/authaccocell.fxml
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // set the values for the account cell
        Id.setText(account.id);
        username.setText(account.acct);
        info.setText(account.note);
        icon.setImage(new Image(account.avatar));
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

    /**
     * Setter for the reference to the auth window controller
     *
     * @param thisclass (AuthWindowController) - The reference to the auth window controller
     */
    public void setReference(MainWindowController thisclass) {
        this.master = thisclass;
    }





}
