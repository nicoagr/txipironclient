package eus.ehu.txipironesmastodonfx.controllers.main;

import eus.ehu.txipironesmastodonfx.TxipironClient;
import eus.ehu.txipironesmastodonfx.controllers.*;
import eus.ehu.txipironesmastodonfx.data_access.DBAccessManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Controller class for the main window.
 *
 * @author Nicolás Aguado
 * @author Haizea Bermejo
 * @author Marcos Chouciño
 * @author Xiomara Cáceces
 */
public class MainWindowController implements WindowController {
    private TxipironClient mainApp;
    private Integer ref;
    @FXML
    private Button changeAcctBtn;
    @FXML
    private Button followers;
    @FXML
    private Button following;
    @FXML
    private Button home;
    @FXML
    private ImageView icon;
    @FXML
    private ListView<Object> listView;

    private ObservableList<Object> listViewItems = FXCollections.observableArrayList();


    /**
     * Sets a reference to the main application
     *
     * @param app (TxipironClient) - The main application
     */
    public void setMain(TxipironClient app) {
        mainApp = app;
    }

    /**
     * Changes the scene to the account selection scene
     * when the change account button is clicked
     */
    @FXML
    void changeAcctBtnClick() {
        mainApp.changeScene("Auth", null);
    }

    /**
     * Sets the reference of the current logged in user
     *
     * @param ref (Integer) - The reference of the current logged in user
     */
    @Override
    public void setRef(Integer ref) {
        this.ref = ref;
      //  Image avatar = DBAccessManager.getUserAvatar(ref);
       // icon.setImage(avatar);
    }

    @FXML
    void followerListView() {

    }

    @FXML
    void followingListView() {

    }

    @FXML
    void homeListView() {

    }

    @FXML
    void initialize() {
    }
}
