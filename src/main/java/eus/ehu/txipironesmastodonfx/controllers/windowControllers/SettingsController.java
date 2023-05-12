package eus.ehu.txipironesmastodonfx.controllers.windowControllers;

import eus.ehu.txipironesmastodonfx.TxipironClient;
import eus.ehu.txipironesmastodonfx.controllers.auth.AuthWindowController;
import eus.ehu.txipironesmastodonfx.controllers.main.MainWindowController;
import eus.ehu.txipironesmastodonfx.data_access.AsyncUtils;
import eus.ehu.txipironesmastodonfx.data_access.DBAccessManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SettingsController {
    private MainWindowController master;

    private AuthWindowController authMaster;

    private MediaViewController mediaMaster;
    @FXML
    private VBox anchor;
    @FXML
    private CheckBox autoplaycheck;
    @FXML
    private Label infoLabel;
    private static final Logger logger = LogManager.getLogger("SettingsController");
    @FXML
    private Button applyBtn;

    @FXML
    private ComboBox<?> comboLanguages;

    @FXML
    private ComboBox<String> comboStyles;

    ObservableList<String> Styles = FXCollections.observableArrayList();

    public String dark= getClass().getResource("/eus/ehu/txipironesmastodonfx/styles/DarkTheme.css").toExternalForm();

    public String light= getClass().getResource("/eus/ehu/txipironesmastodonfx/styles/LightTheme.css").toExternalForm();


    /**
     * This method will be used to
     * apply the selected user settings.
     */
    @FXML
    private void applyBtnAction() {
        applyBtn.setDisable(true);
        String load = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("Load");
        applyBtn.setText(load);
        autoplaycheck.setDisable(true);
        logger.info("Applying selected settings...");
        AsyncUtils.asyncTask(() -> {
            try {
                DBAccessManager.updateSettings(autoplaycheck.isSelected());
            } catch (SQLException e) {
                return e.getMessage();
            }
            logger.debug("autoplayMedia: " + autoplaycheck.isSelected() + " updated");
            master.autoplayMedia = autoplaycheck.isSelected();
            return null;
        }, res -> {
            applyBtn.setVisible(false);
            autoplaycheck.setVisible(false);
            comboStyles.setVisible(false);
            comboLanguages.setVisible(false);
            if (res != null) {
                logger.error("Error applying settings: " + res);
                String again = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("Again");
                infoLabel.setText("Error: " + res + again);
            } else {
                logger.info("Settings applied successfully.");
                String success = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("Success");
                infoLabel.setText(success);
            }
        });
        switch (comboStyles.getValue()) {
            case "Dark" -> {
                master.mainBorderpane.getStylesheets().remove(light);
                master.mainBorderpane.getStylesheets().add(dark);
            }
            case "Light" -> {
                master.mainBorderpane.getStylesheets().remove(dark);
                master.mainBorderpane.getStylesheets().add(light);
            }
        }
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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/eus/ehu/txipironesmastodonfx/maincell/settingscell.fxml"),
                ResourceBundle.getBundle("strings", TxipironClient.lang));
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

    @FXML
    void initialize() {
        comboStyles.getItems().addAll("Dark", "Light");
    }


}
