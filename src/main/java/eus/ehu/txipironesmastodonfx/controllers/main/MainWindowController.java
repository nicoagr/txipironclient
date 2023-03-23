package eus.ehu.txipironesmastodonfx.controllers.main;

import eus.ehu.txipironesmastodonfx.TxipironClient;
import eus.ehu.txipironesmastodonfx.controllers.*;
import eus.ehu.txipironesmastodonfx.data_access.DBAccessManager;
import eus.ehu.txipironesmastodonfx.domain.Account;
import eus.ehu.txipironesmastodonfx.domain.Follow;
import eus.ehu.txipironesmastodonfx.controllers.windowControllers.*;
import eus.ehu.txipironesmastodonfx.domain.Toot;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;


import java.sql.SQLException;
import java.util.List;

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
    private BorderPane mainWraper;
    @FXML
    private ListView<Object> listView;
    private ObservableList<Object> listViewItems = FXCollections.observableArrayList();
    private MainWindowController mainWindowController;

    public Application getTxipironClient(){
        return mainApp;
    }
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
     *
     * @throws SQLException
     */
    @FXML
    void changeAcctBtnClick() throws SQLException {
        mainApp.changeScene("Auth", null);
    }

    /**
     * Sets the reference of the current logged in user and sets the avatar
     *
     * @param ref (Integer) - The reference of the current logged in user
     */
    @Override
    public void setRef(Integer ref) throws SQLException {
        this.ref = ref;
        Image avatar = new Image(DBAccessManager.getUserAvatar(ref));
        icon.setImage(avatar);
    }

    /**
     * Sets the list view to show the followers of the current logged in user
     *
     * @throws SQLException
     */
    @FXML
    void followerListView() throws SQLException {
        listViewItems.clear();
        listViewItems.add("Followers");
        List<Follow> followers = DBAccessManager.getUserFollowers(ref);
        //Account account = new Account();
        for (Follow f: followers) {
            //FollowCellController followCellController = new FollowCellController(f);
            listViewItems.add(f);
        }
        listView.setItems(listViewItems);
    }

    /**
     * Sets the list view to show the users that the current logged in user is following
     *
     * @throws SQLException
     */
    @FXML
    void followingListView() throws SQLException {
        listViewItems.clear();
        listViewItems.add("Following");
        List<Follow> following = DBAccessManager.getUserFollowings(ref);
        for (Follow f: following) {
            //followCellController followCellController = new followCellController(f, mainWindowController);
            //listViewItems.add(followCellController);
            listViewItems.add(f);
        }
        listView.setItems(listViewItems);
    }

    /**
     * Sets the list view to show the toots of the current logged in user and the users that the current logged in
     * user is following starting from the most recent
     */
    @FXML
    void homeListView() throws SQLException {
        listViewItems.clear();
        listViewItems.add("Home");
        List< Toot> toots = DBAccessManager.getUserToots(ref);
        for (Toot t: toots) {
            //TootCellController tootCellController = new TootCellController(t);
            listViewItems.add(t);
        }
        listView.setItems(listViewItems);

    }

    @FXML
    void initialize() {
        listViewItems.clear();
        listViewItems.add("Home");
        listView.setItems(listViewItems);


    }


}
