package eus.ehu.txipironesmastodonfx.controllers.main;

import eus.ehu.txipironesmastodonfx.TxipironClient;
import eus.ehu.txipironesmastodonfx.controllers.WindowController;
import eus.ehu.txipironesmastodonfx.controllers.windowControllers.*;
import eus.ehu.txipironesmastodonfx.data_access.*;
import eus.ehu.txipironesmastodonfx.data_access.AsyncUtils;
import eus.ehu.txipironesmastodonfx.data_access.DBAccessManager;
import eus.ehu.txipironesmastodonfx.data_access.NetworkUtils;
import eus.ehu.txipironesmastodonfx.domain.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.AccessibleAttribute;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller class for the main window.
 *
 * @author Nicolás Aguado
 * @author Haizea Bermejo
 * @author Xiomara Cáceces
 * @author Marcos Chouciño
 */
public class MainWindowController implements WindowController {


    public WindowNotificationSystem windowNotificationSystem = new WindowNotificationSystem();
    public NotificationSystem notificationSystem = new NotificationSystem();

    private TxipironClient mainApp;


    public String id;

    private Integer ref;
    public String lastNotification;
    public String authenticatedId;

    public String token;
    @FXML
    private ImageView icon;
    @FXML
    private TextField searchQuery;
    @FXML
    private Button searchBtn;
    @FXML
    private ImageView loading;
    @FXML
    public ListView<Object> listView;
    public ObservableList<Object> listViewItems = FXCollections.observableArrayList();
    public boolean autoplayMedia = false;



    /**
     * Changes title and Shows loading gif
     */
    public void showLoading() {
        mainApp.setStageTitle("Txipiron Client [v1.0] - a Mastodon Client - Loading...");
        loading.setVisible(true);
    }

    /**
     * Changes the title
     * and hides the loading gif
     */
    public void hideLoading() {
        mainApp.setStageTitle("Txipiron Client [v1.0] - a Mastodon Client - Main Window");
        loading.setVisible(false);
    }

    /**
     * Loads the post Toot screen
     */
    @FXML
    public void postTootListview() {
        listViewItems.clear();
        listViewItems.add("Post Toot");
        listView.getFocusModel().focus(0);
        listView.getSelectionModel().select(0);
    }



    /**
     * Loads the notification
     */
    @FXML
    void NotificationListView() throws IOException, AWTException {
        windowNotificationSystem.trowNotificationWindow("A");
        listViewItems.clear();
        listViewItems.add("Loading...");
        showLoading();
        AsyncUtils.asyncTask(() -> {
            if (!NetworkUtils.hasInternet()) return null;
            List<Notification> notifications;
            notifications = APIAccessManager.getNewNotification(token);
            return notifications;
        }, notifications -> {
            listViewItems.clear();
            if (notifications == null) {
                listViewItems.add("Error downloading following. Please check your connection and try again.");
                return;
            }
            hideLoading();
            listViewItems.add("Notifications");
            lastNotification = notifications.get(0).id;
            for (Notification element : notifications) {
                if (element.type.equals("mention")) {
                    listViewItems.add(element.account.acct + " has mention you :D");
                    listViewItems.add(element.status);
                } else if (element.type.equals("status")) {
                    listViewItems.add(element.account.acct + ", has posted a toot    :P");
                    listViewItems.add(element.status);
                } else if (element.type.equals("follow")) {
                    listViewItems.add(element.account.acct + ", has followed you");
                    listViewItems.add(element.account);
                } else if (element.type.equals("favorite")) {
                    listViewItems.add(element.account.acct + ", has liked your toot :v");
                    listViewItems.add(element.status);
                }
            }
        });
        APIAccessManager.clearNotification(token);
    }
    /**
     * Gets the reference to the main application
     * @return
     */
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
    void changeAcctBtnClick() throws IOException, AWTException {
        listViewItems.clear();
        mainApp.changeScene("Auth", null);
    }

    /**
     * Sets the reference of the current logged in user ,sets the avatar and initialices the notification system
     *
     * @param result (List<Object>) - The list of reference and token to be set
     */
    @Override
    public void setRefTokenId(List<Object> result) throws IOException, AWTException {
        listView.setItems(listViewItems);
        this.ref = (Integer) result.get(0);
        this.token = (String) result.get(1);
        NotificationSystem NotificationSystem = new NotificationSystem();
        NotificationSystem.activateNotifications(this);
        windowNotificationSystem.initialice();
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
        // Download defaults asynchronously
        AsyncUtils.asyncTask(() -> DBAccessManager.getSetting("autoplaymedia", true), res -> {
            if (res != null) {
                autoplayMedia = (Boolean) res;
            }
        });
    }

    /**
     * Adds the settings to the list view
     */
    @FXML
    void settings() {
        listViewItems.clear();
        listViewItems.add("Settings");
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
        showLoading();
        AsyncUtils.asyncTask(() -> {
            if (!NetworkUtils.hasInternet()) return null;
            SearchResult srslt;
            srslt = APIAccessManager.performSearch(searchQuery.getText(), token, 5);
            return srslt;
        }, res -> {
            listViewItems.clear();
            hideLoading();
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
        showLoading();
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
            hideLoading();
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
        showLoading();
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
            hideLoading();
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
        showLoading();
        AsyncUtils.asyncTask(() -> {
            if (!NetworkUtils.hasInternet()) return null;
            List<Toot> toots;
            // Here, the id parameter is going to control which toots
            // from which are going to be downloaded
            try {
                toots = APIAccessManager.getHomeTootsId(authenticatedId, token);
            } catch (IOException e) {
                toots = null;
            }
            return toots;
        }, toots -> {
            listViewItems.clear();
            if (toots == null) {
                listViewItems.add("Error downloading profile toots. Please check your connection and try again.");
                return;
            }
            hideLoading();
            listViewItems.add("Home");
            listViewItems.addAll(toots);
        });
    }

    /**
     * Sets the list view to show the toots liked by the current logged in user
     */
    @FXML
    void likedTootsListView() {
        listViewItems.clear();
        listViewItems.add("Loading...");
        showLoading();
        AsyncUtils.asyncTask(() -> {
            if (!NetworkUtils.hasInternet()) return null;
            List<Toot> toots;
            // Here, the id parameter is going to control which toots
            // from which are going to be downloaded
            try {
                toots = APIAccessManager.getLikedToots(token);
            } catch (IOException e) {
                toots = null;
            }
            return toots;
        }, toots -> {
            listViewItems.clear();
            if (toots == null) {
                listViewItems.add("Error downloading profile toots. Please check your connection and try again.");
                return;
            }
            hideLoading();
            listViewItems.add("Liked toots");
            listViewItems.addAll(toots);
        });
    }

    /**
     * Sets the list view to show the toots of the current logged in user from an id
     */
    @FXML
    public void userTootListViewFromId(String id) {
        listViewItems.clear();
        listViewItems.add("Loading...");
        showLoading();
        AsyncUtils.asyncTask(() -> {
            if (!NetworkUtils.hasInternet()) return null;
            Account account;
            account = APIAccessManager.getAccount(id, token);
            return account;
        }, account -> {
            listViewItems.clear();
            if (account == null) {
                listViewItems.add("Error downloading profile . Please check your connection and try again.");
                return;
            }
            listViewItems.add(account);
            listViewItems.add("Loading...");
        });
        AsyncUtils.asyncTask(() -> {
            if (!NetworkUtils.hasInternet()) return null;
            List<Toot> toots;
            // Here, the id parameter is going to control which toots
            // from which are going to be downloaded
            try {
                toots = APIAccessManager.getTootId(id, token);
            } catch (IOException e) {
                toots = null;
            }
            return toots;
        }, toots -> {
            if (toots == null) {
                listViewItems.add("Error downloading profile toots. Please check your connection and try again.");
                return;
            }
            listViewItems.remove("Loading...");
            listViewItems.add("Toots and replies");
            listViewItems.addAll(toots);
            hideLoading();
        });
    }

    /**
     * Sets the list view to show the profile of the current logged in user
     */
    @FXML
    void profileListView() {
        userTootListViewFromId(authenticatedId);
    }


    /**
     * Sets the list view to show the toots of the current logged in user
     */
    @FXML
    public void userTootListView(String username) {
        listViewItems.clear();
        listViewItems.add("Loading...");
        AsyncUtils.asyncTask(() -> {
            if (!NetworkUtils.hasInternet()) return null;
            String id;
            // Here, the id parameter is going to control which toots
            // from which are going to be downloaded
            try {
                id = APIAccessManager.getIdFromUsername(username, token);
            } catch (IOException e) {
                id = null;
            }
            return id;
        }, id -> {
            if (id == null) {
                listViewItems.add("Error getting id from account. Please check your connection and try again.");
                return;
            }
            userTootListViewFromId(id);
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
                } else if (item instanceof String && item.equals("Settings")) {
                    setText(null);
                    SettingsController s = new SettingsController(thisclass);
                    s.loadDefaultSettings();
                    setGraphic(s.getUI());
                } else if (item instanceof String) {
                    setText(null);
                    HeaderCellController c = new HeaderCellController((String) item, thisclass);
                    setGraphic(c.getUI());
                }else if (item instanceof Account) {
                    setText(null);
                    AsyncUtils.asyncTask(() -> new ProfileCellControllers(thisclass), param -> {
                                setGraphic(param.getUI());
                                param.loadAccount((Account) item);
                            }
                    );
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
