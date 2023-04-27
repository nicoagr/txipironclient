package eus.ehu.txipironesmastodonfx.controllers.windowControllers;

import eus.ehu.txipironesmastodonfx.controllers.main.MainWindowController;
import eus.ehu.txipironesmastodonfx.data_access.AsyncUtils;
import eus.ehu.txipironesmastodonfx.data_access.DBAccessManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;

public class SettingsController {
    private MainWindowController master;
    @FXML
    private VBox anchor;
    @FXML
    private CheckBox autoplaycheck;
    @FXML
    private Label infoLabel;

    @FXML
    private Button applyBtn;

    /**
     * This method will be used to
     * apply the selected user settings.
     */
    @FXML
    private void applyBtnAction() {
        applyBtn.setDisable(true);
        applyBtn.setText("Loading...");
        autoplaycheck.setDisable(true);
        AsyncUtils.asyncTask(() -> {
            try {
                DBAccessManager.updateSettings(autoplaycheck.isSelected());
            } catch (SQLException e) {
                return e.getMessage();
            }
            master.autoplayMedia = autoplaycheck.isSelected();
            return null;
        }, res -> {
            applyBtn.setVisible(false);
            autoplaycheck.setVisible(false);
            if (res != null) {
                infoLabel.setText("Error: " + res + ". Please try again.");
            } else {
                infoLabel.setText("Settings applied successfully.");
            }
        });
    }

    /**
     * This method will be used to
     * load the default settings
     */
    public void loadDefaultSettings() {
        autoplaycheck.setSelected(master.autoplayMedia);
    }

    /**
     * Getter for the UI (AnchorPane)
     * This method will be used by the AuthWindowController
     * in order to display custom cells in the listview
     *
     * @return (VBox) - The UI of the controller
     */
    public VBox getUI() {
        return anchor;
    }

    /**
     * Constructor for the controller.
     * It will load itself at take consciousness (set itself as controller)
     * Also it will set the corresponding values for the account cell.
     *
     * @param master (MainWindowController)- The controller of the main class, will be used for internal comunication
     */
    public SettingsController(MainWindowController master) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/eus/ehu/txipironesmastodonfx/maincell/settingscell.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setReference(master);
    }

    /**
     * Setter for the reference to the auth window controller
     *
     * @param master (AuthWindowController) - The reference to the auth window controller
     */
    public void setReference(MainWindowController master) {
        this.master = master;
    }

}