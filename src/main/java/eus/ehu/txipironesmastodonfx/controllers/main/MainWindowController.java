package eus.ehu.txipironesmastodonfx.controllers.main;

import eus.ehu.txipironesmastodonfx.TxipironClient;
import eus.ehu.txipironesmastodonfx.controllers.WindowController;
import eus.ehu.txipironesmastodonfx.controllers.windowControllers.*;
import eus.ehu.txipironesmastodonfx.data_access.APIAccessManager;
import eus.ehu.txipironesmastodonfx.data_access.AsyncUtils;
import eus.ehu.txipironesmastodonfx.data_access.DBAccessManager;
import eus.ehu.txipironesmastodonfx.data_access.NetworkUtils;
import eus.ehu.txipironesmastodonfx.domain.Follow;
import eus.ehu.txipironesmastodonfx.domain.SearchResult;
import eus.ehu.txipironesmastodonfx.domain.Toot;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.AccessibleAttribute;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
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
    protected String authenticatedId;
    public String token;
    @FXML
    private ImageView icon;
    @FXML
    private TextField searchQuery;
    @FXML
    private Button searchBtn;
    @FXML
    private BorderPane mainWraper;
    @FXML
    private ListView<Object> listView;
    public ObservableList<Object> listViewItems = FXCollections.observableArrayList();

    @FXML
    void postTootListview() {
        listViewItems.clear();
        listViewItems.add("Post Toot");
    }

    public Application TxipironClient() {
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
     */
    @FXML
    void changeAcctBtnClick() {
        listViewItems.clear();
        mainApp.changeScene("Auth", null);
    }

    /**
     * Sets the reference of the current logged in user and sets the avatar
     *
     * @param result (List<Object>) - The list of reference and token to be set
     */
    @Override
    public void setRefTokenId(List<Object> result) {
        listView.setItems(listViewItems);
        this.ref = (Integer) result.get(0);
        this.token = (String) result.get(1);
        this.authenticatedId = (String) result.get(2);
        icon.setImage(new Image(getClass().getResourceAsStream("/eus/ehu/txipironesmastodonfx/mainassets/dark-accounticon.png")));
        AsyncUtils.asyncTask(() -> {
            String avatarUrl;
            try {
                avatarUrl = DBAccessManager.getUserAvatar(ref);
            } catch (SQLException e) {
                avatarUrl = null;
            }
            return (avatarUrl != null) ? new Image(avatarUrl) : new Image(getClass().getResourceAsStream("/eus/ehu/txipironesmastodonfx/mainassets/dark-notfound.jpg"));
        }, image -> icon.setImage(image));
    }

    /**
     * Performs a search on the mastodon servers
     * with the specified query. It will search
     * both toots and users
     */
    @FXML
    void performSearch() {
        listViewItems.clear();
        if (searchQuery.getText().isEmpty()) {
            listViewItems.add("Error - Please enter a search query");
            return;
        }
        listViewItems.add("Loading...");
        AsyncUtils.asyncTask(() -> {
            if (!NetworkUtils.hasInternet()) return null;
            SearchResult srslt;
            srslt = APIAccessManager.performSearch(searchQuery.getText(), token, 5);
            return srslt;
        }, res -> {
            listViewItems.clear();
            if (res == null) {
                listViewItems.add("Error downloading search results. Please check your connection and try again.");
                return;
            }
            if (res.accounts.size() == 0) {
                listViewItems.add("No users found with that query");
            } else {
                listViewItems.add("Result accounts");
                listViewItems.addAll(res.accounts);
            }
            if (res.statuses.size() == 0) {
                listViewItems.add("No statuses found with that query");
            } else {
                listViewItems.add("Result statuses");
                listViewItems.addAll(res.statuses);
            }
        });
    }

    /**
     * Sets the list view to show the followers of the current logged in user
     */
    @FXML
    void followerListView() {
        listViewItems.clear();
        listViewItems.add("Loading...");
        AsyncUtils.asyncTask(() -> {
            if (!NetworkUtils.hasInternet()) return null;
            List<Follow> follower;
            follower = APIAccessManager.getFollow(authenticatedId, token, false);
            return follower;
        }, follower -> {
            listViewItems.clear();
            if (follower == null) {
                listViewItems.add("Error downloading followers. Please check your connection and try again.");
                return;
            }
            listViewItems.add("Followers");
            listViewItems.addAll(follower);
        });
    }

    /**
     * Sets the list view to show the users that the current logged in user is following
     */
    @FXML
    void followingListView() {
        listViewItems.clear();
        listViewItems.add("Loading...");
        AsyncUtils.asyncTask(() -> {
            if (!NetworkUtils.hasInternet()) return null;
            List<Follow> follower;
            follower = APIAccessManager.getFollow(authenticatedId, token, true);
            return follower;
        }, follower -> {
            listViewItems.clear();
            if (follower == null) {
                listViewItems.add("Error downloading following. Please check your connection and try again.");
                return;
            }
            listViewItems.add("Followers");
            listViewItems.addAll(follower);
        });
    }

    /**
     * Sets the list view to show the toots of the current logged in user
     */
    @FXML
    public void homeListView() {
        listViewItems.clear();
        listViewItems.add("Loading...");
        AsyncUtils.asyncTask(() -> {
            if (!NetworkUtils.hasInternet()) return null;
            List<Toot> toots;
            // Here, the id parameter is going to control which toots
            // from which are going to be downloaded
            toots = APIAccessManager.getProfileToots(authenticatedId, token);
            return toots;
        }, toots -> {
            listViewItems.clear();
            if (toots == null) {
                listViewItems.add("Error downloading profile toots. Please check your connection and try again.");
                return;
            }
            listViewItems.add("Profile toots");
            listViewItems.addAll(toots);
        });
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
                    TootCellController b = new TootCellController(thisclass);
                    setGraphic(b.getUI());
                    b.loadToot((Toot) item);
                } else if (item instanceof String && ((String) item).toLowerCase().contains("error")) {
                    setText(null);
                    ErrorCellController d = new ErrorCellController((String) item);
                    setGraphic(d.getUI());
                } else if (item instanceof String && item.equals("Post Toot")) {
                    setText(null);
                    PostTootController c = new PostTootController(thisclass);
                    setGraphic(c.getUI());
                } else if (item instanceof String) {
                    setText(null);
                    HeaderCellController c = new HeaderCellController((String) item, thisclass);
                    setGraphic(c.getUI());
                }
                // Remove horizontal scrollbar for each item that we load
                // (Yes! necessary!) for each item, because the list view
                // will be re-dimensioned and the horizontal scrollbar will
                // appear again
                ScrollBar scrollBar = (ScrollBar) listView.queryAccessibleAttribute(AccessibleAttribute.HORIZONTAL_SCROLLBAR);
                if (scrollBar != null) {
                    scrollBar.setPrefHeight(0);
                    scrollBar.setMaxHeight(0);
                    scrollBar.setOpacity(0);
                    scrollBar.setDisable(true);
                    scrollBar.setVisible(false);
                }
            }
        });
        searchQuery.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                searchBtn.fire();
            }
        });
    }
}
