package eus.ehu.txipironesmastodonfx.controllers.main;

import eus.ehu.txipironesmastodonfx.TxipironClient;
import eus.ehu.txipironesmastodonfx.controllers.WindowController;
import eus.ehu.txipironesmastodonfx.controllers.windowControllers.*;
import eus.ehu.txipironesmastodonfx.data_access.APIAccessManager;
import eus.ehu.txipironesmastodonfx.data_access.AsyncUtils;
import eus.ehu.txipironesmastodonfx.data_access.DBAccessManager;
import eus.ehu.txipironesmastodonfx.data_access.NetworkUtils;
import eus.ehu.txipironesmastodonfx.domain.Account;
import eus.ehu.txipironesmastodonfx.domain.Follow;
import eus.ehu.txipironesmastodonfx.domain.SearchResult;
import eus.ehu.txipironesmastodonfx.domain.Toot;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.AccessibleAttribute;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.ScrollEvent;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Controller class for the main window.
 *
 * @author Nicolás Aguado
 * @author Haizea Bermejo
 * @author Xiomara Cáceces
 * @author Marcos Chouciño
 */
public class MainWindowController implements WindowController {

    private TxipironClient mainApp;
    private static final Logger logger = LogManager.getLogger("MainWindowController");
    public String id;

    private Integer ref;
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
        logger.info("Loaded post toot screen");
    }

    /**
     * Gets the reference to the main application
     *
     * @return (Application) - The main application
     */
    public TxipironClient TxipironClient() {
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
        logger.debug("Established token and authenticated user id:" + authenticatedId);
        refreshAvatar();
        // Download defaults asynchronously
        AsyncUtils.asyncTask(() -> DBAccessManager.getSetting("autoplaymedia", true), res -> {
            if (res != null) {
                autoplayMedia = (Boolean) res;
                logger.debug("Established autoplay media setting: " + autoplayMedia);
            }
        });
    }

    /**
     * This method will refresh
     * the avatar of the current user
     * in the top left corner icon
     */
    public void refreshAvatar() {
        logger.debug("Attempting to refresh avatar...");
        icon.setImage(new Image(getClass().getResourceAsStream("/eus/ehu/txipironesmastodonfx/loading-gif.gif")));
        AsyncUtils.asyncTask(() -> {
            String avatarUrl;
            avatarUrl = APIAccessManager.getAccount(authenticatedId, token).avatar;
            logger.debug("Avatar url fetched from servers: " + avatarUrl);
            return (avatarUrl != null) ? new Image(avatarUrl) : new Image(getClass().getResourceAsStream("/eus/ehu/txipironesmastodonfx/mainassets/dark-notfound.jpg"));
        }, image -> {
            icon.setImage(image);
            logger.debug("Avatar refreshed");
        });
    }

    /**
     * Adds the settings to the list view
     */
    @FXML
    void settings() {
        listViewItems.clear();
        listViewItems.add("Settings");
        logger.info("Loaded settings screen");
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
            logger.error("User tried to type a null search query");
            return;
        }
        listViewItems.add("Loading...");
        logger.debug("Attempting search with query: " + searchQuery.getText());
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
                logger.error("Error downloading search results. Please check your connection and try again.");
                return;
            }
            logger.info("Performed search with query: " + searchQuery.getText() + " and got " + res.accounts.size() + " accounts and " + res.statuses.size() + " statuses");
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
     * Sets the list view to show the bookmarked
     * toots of the current logged in user
     */
    @FXML
    void bookmarkedTootsListView() {
        listViewItems.clear();
        listViewItems.add("Loading...");
        logger.debug("Attempting to download bookmarked toots");
        showLoading();
        AsyncUtils.asyncTask(() -> {
            if (!NetworkUtils.hasInternet()) return null;
            List<Toot> toot;
            toot = APIAccessManager.getBookmarkedToots(token);
            return toot;
        }, toot -> {
            listViewItems.clear();
            if (toot == null) {
                listViewItems.add("Error downloading bookmarked toots. Please check your connection and try again.");
                logger.error("Error downloading bookmarked toots. Please check your connection and try again.");
                return;
            }
            hideLoading();
            logger.info("Downloaded " + toot.size() + " bookmarked toots");
            if (toot.size() == 0) {
                listViewItems.add("No bookmarked toots found");
                return;
            }
            listViewItems.add("Bookmarks");
            listViewItems.addAll(toot);
        });
    }

    /**
     * Sets the list view to show the timeline of home
     * toots of the current logged in user
     */
    @FXML
    public void homeListView() {
        listViewItems.clear();
        listViewItems.add("Loading...");
        logger.debug("Attempting to download home toots");
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
                listViewItems.add("Error downloading home toots. Please check your connection and try again.");
                logger.error("Error downloading home toots from user id: " + authenticatedId);
                return;
            }
            hideLoading();
            logger.info("Downloaded " + toots.size() + " home toots from user id: " + authenticatedId);
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
        logger.debug("Attempting to download liked toots");
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
                listViewItems.add("Error downloading liked toots. Please check your connection and try again.");
                logger.error("Error downloading liked toots from user id: " + authenticatedId);
                return;
            }
            hideLoading();
            logger.info("Downloaded " + toots.size() + " liked toots from user id" + authenticatedId);
            if (toots.size() == 0) {
                listViewItems.add("No liked toots found");
                return;
            }
            listViewItems.add("Liked toots");
            listViewItems.addAll(toots);
        });
    }

    /**
     * Sets the list view to show the toots of
     * the user with the id passed as a parameter
     *
     * @param id (String) The id of the user whose toots are going to be shown
     */
    @FXML
    public void userTootListViewFromId(String id) {
        listViewItems.clear();
        listViewItems.add("Loading...");
        logger.debug("Attempting to download profile and toots from id: " + id);
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
                logger.error("Error downloading profile from user id: " + id);
                return;
            }
            listViewItems.add(account);
            logger.info("Downloaded profile from id: " + id);
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
                logger.error("Error downloading profile toots from user id" + id);
                return;
            }
            listViewItems.remove("Loading...");
            listViewItems.add("Toots and replies");
            listViewItems.addAll(toots);
            logger.info("Downloaded " + toots.size() + " toots from user id: " + id);
            hideLoading();
        });
    }

    public void loggedUserListView(){
        userTootListViewFromId(authenticatedId);
    }

    /**
     * Sets the list view to show the toots of the username
     * passed as a parameter
     *
     * @param username (String) The username of the account whose toots are going to be shown
     */
    @FXML
    public void userTootListView(String username) {
        listViewItems.clear();
        listViewItems.add("Loading...");
        logger.debug("Attempting to mastodon id from username: " + username);
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
                logger.error("Error getting id from account: " + username);
                return;
            }
            logger.debug("Downloaded id from username: " + username);
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
                } else if (item instanceof Follow) {
                    setText(null);
                    FollowCellController f = new FollowCellController((Follow) item, thisclass);
                    setGraphic(f.getUI());
                } else if (item instanceof Account) {
                    setText(null);
                    ProfileCellControllers p = new ProfileCellControllers(thisclass);
                    setGraphic(p.getUI());
                    p.loadAccount((Account) item);
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
        // force remove horizontal scrolling
        listView.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.getDeltaX() != 0) {
                event.consume();
            }
        });
    }
}
