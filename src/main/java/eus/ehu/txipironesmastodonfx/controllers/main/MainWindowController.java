package eus.ehu.txipironesmastodonfx.controllers.main;

import eus.ehu.txipironesmastodonfx.TxipironClient;
import eus.ehu.txipironesmastodonfx.controllers.WindowController;
import eus.ehu.txipironesmastodonfx.controllers.windowControllers.FollowCellController;
import eus.ehu.txipironesmastodonfx.controllers.windowControllers.HeaderCellController;
import eus.ehu.txipironesmastodonfx.controllers.windowControllers.TootCellController;
import eus.ehu.txipironesmastodonfx.data_access.DBAccessManager;
import eus.ehu.txipironesmastodonfx.domain.Account;
import eus.ehu.txipironesmastodonfx.domain.Follow;
import eus.ehu.txipironesmastodonfx.domain.Toot;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

import java.sql.SQLException;
import java.util.ArrayList;
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

    public Application TxipironClient(){
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
    public void setRef(Integer ref) {
        this.ref = ref;
        //change this!! avatar=null?????????? STRANGE
        Image avatar= null;
        try{
            avatar = new Image(DBAccessManager.getUserAvatar(ref));
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        icon.setImage(avatar);
    }

    /**
     * Sets the list view to show the followers of the current logged in user
     */
    @FXML
    void followerListView() {
        listViewItems.clear();
        listView.setItems(listViewItems);
        listViewItems.add("Followers");
        List<Follow> follower = new ArrayList<Follow>();
        try{
            follower = DBAccessManager.getUserFollowers(ref);
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        listViewItems.addAll(follower);
        //listView.setItems(listViewItems);
    }

    /**
     * Sets the list view to show the users that the current logged in user is following
     */
    @FXML
    void followingListView()  {
        listViewItems.clear();
        listView.setItems(listViewItems);
        listViewItems.add("Following");
        List<Follow> following = new ArrayList<Follow>();
        try{
            following = DBAccessManager.getUserFollowings(ref);
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        listViewItems.addAll(following);
    }

    /**
     * Sets the list view to show the toots of the current logged in user and the users that the current logged in
     * user is following starting from the most recent
     */
    @FXML
    void homeListView() {
        listViewItems.clear();
        listView.setItems(listViewItems);
        listViewItems.add("Home");
        List<Toot> toots = new ArrayList<Toot>();
        try{
            toots = DBAccessManager.getUserToots(ref);
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        listViewItems.addAll(toots);
    }

    public void start()  {
        listView.setItems(listViewItems);
        listViewItems.add("Home");
        List<Toot> toots = new ArrayList<Toot>();
        try{
            toots = DBAccessManager.getUserToots(ref);
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        listViewItems.addAll(toots);
    }

    /**
     * Initializes the list view
     */
    @FXML
    void initialize() {
        MainWindowController thisclass = this;
        listView.setCellFactory(param -> new ListCell<>(){
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else if (item instanceof Follow) {
                    setText(null);
                    FollowCellController a = new FollowCellController((Follow) item, thisclass);
                    setGraphic(a.getUI());
                } else if (item instanceof Toot) {
                    setText(null);
                    TootCellController b = new TootCellController((Toot) item, thisclass);
                    setGraphic(b.getUI());
                } else if (item instanceof String && item.equals("Home")) {
                    setText(null);
                    HeaderCellController c = new HeaderCellController((String) item, thisclass);
                    setGraphic(c.getUI());
                } else if (item instanceof String && item.equals("Followers")) {
                    setText(null);
                    HeaderCellController d = new HeaderCellController((String) item, thisclass);
                    setGraphic(d.getUI());
                } else if (item instanceof String && item.equals("Following")) {
                    setText(null);
                    HeaderCellController e = new HeaderCellController((String) item, thisclass);
                    setGraphic(e.getUI());
                }
            }
        });
    }
}
