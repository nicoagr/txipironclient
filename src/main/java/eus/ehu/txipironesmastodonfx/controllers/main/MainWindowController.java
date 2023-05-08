package eus.ehu.txipironesmastodonfx.controllers.main;

import eus.ehu.txipironesmastodonfx.TxipironClient;
import eus.ehu.txipironesmastodonfx.controllers.WindowController;
import eus.ehu.txipironesmastodonfx.data_access.*;
import eus.ehu.txipironesmastodonfx.domain.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import java.util.HashMap;
import java.util.List;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
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
    private HashMap<view, String> status = new HashMap<>();
    private boolean infinityBlock = false;
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
    public VBox vbox;
    @FXML
    public ScrollPane scrollpane;
    public ObservableList<CellController> listViewItems = FXCollections.observableArrayList();
    public boolean autoplayMedia = false;

    private enum view {
        HOME, PROFILETOOT, PROFILEFOLLOWING, PROFILEFOLLOWERS, SEARCH, POST_TOOT, NOTIFICATION, FAVOURITES, BOOKMARKS, SETTINGS, LOADING
    }

    /**
     * Changes title and Shows loading gif
     */
    public void showLoading() {
        mainApp.setStageTitle("Txipiron Client [v1.0] - a Mastodon Client - Loading...");
        loading.setVisible(true);
        status.clear();
        status.put(view.LOADING, null);
    }

    /**
     * Changes the title
     * and hides the loading gif
     */
    public void hideLoading() {
        mainApp.setStageTitle("Txipiron Client [v1.0] - a Mastodon Client - Main Window");
        loading.setVisible(false);
        status.remove(view.LOADING);
    }

    /**
     * Loads the post Toot screen
     */
    @FXML
    public void postTootListview() {
        showLoading();
        listViewItems.clear();
        listViewItems.add(new Generic(Generic.of.POST_TOOT, "Post Toot"));
        logger.info("Loaded post toot screen");
        hideLoading();
        status.clear();
        status.put(view.POST_TOOT, null);
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
        status.clear();
        mainApp.changeScene("Auth", null);
    }

    /**
     * Sets the reference of the current logged in user and sets the avatar
     *
     * @param result (List<Object>) - The list of reference and token to be set
     */
    @Override
    public void setRefTokenId(List<Object> result) {
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
        listViewItems.add(new Generic(Generic.of.SETTINGS, "Settings"));
        logger.info("Loaded settings screen");
        status.clear();
        status.put(view.SETTINGS, null);
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
            listViewItems.add(new Generic(Generic.of.ERROR, "Error - Please enter a search query"));
            logger.error("User tried to type a null search query");
            return;
        }
        listViewItems.add(new Generic(Generic.of.MESSAGE, "Loading..."));
        logger.debug("Attempting search with query: " + searchQuery.getText());
        showLoading();
        AsyncUtils.asyncTask(() -> {
            if (!NetworkUtils.hasInternet()) return null;
            SearchResult srslt;
            srslt = APIAccessManager.performSearch(searchQuery.getText(), token, 5);
            return srslt;
        }, res -> {
            hideLoading();
            if (res == null) {
                listViewItems.add(new Generic(Generic.of.ERROR, "Error downloading search results. Please check your connection and try again."));
                logger.error("Error downloading search results. Please check your connection and try again.");
                return;
            }
            logger.info("Performed search with query: " + searchQuery.getText() + " and got " + res.accounts.size() + " accounts and " + res.statuses.size() + " statuses");
            status.clear();
            status.put(view.SEARCH, null);
            listViewItems.remove(0);
            if (res.accounts.size() == 0) {
                listViewItems.add(new Generic(Generic.of.MESSAGE, "No users found with that query"));
            } else {
                listViewItems.add(new Generic(Generic.of.MESSAGE, "Result accounts"));
                listViewItems.addAll(res.accounts);
            }
            if (res.statuses.size() == 0) {
                listViewItems.add(new Generic(Generic.of.MESSAGE, "No statuses found with that query"));
            } else {
                listViewItems.add(new Generic(Generic.of.MESSAGE, "Result statuses"));
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
        listViewItems.add(new Generic(Generic.of.MESSAGE, "Loading..."));
        logger.debug("Attempting to download bookmarked toots");
        showLoading();
        AsyncUtils.asyncTask(() -> {
            if (!NetworkUtils.hasInternet()) return null;
            List<Toot> toot;
            toot = APIAccessManager.getBookmarkedToots(token);
            return toot;
        }, toot -> {
            if (toot == null) {
                listViewItems.add(new Generic(Generic.of.ERROR, "Error downloading bookmarked toots. Please check your connection and try again."));
                logger.error("Error downloading bookmarked toots. Please check your connection and try again.");
                return;
            }
            hideLoading();
            logger.info("Downloaded " + toot.size() + " bookmarked toots");
            if (toot.size() == 0) {
                listViewItems.add(new Generic(Generic.of.MESSAGE, "No bookmarked toots found"));
                return;
            }
            listViewItems.remove(0);
            listViewItems.add(new Generic(Generic.of.MESSAGE, "Bookmarks"));
            listViewItems.addAll(toot);
            status.clear();
            status.put(view.BOOKMARKS, authenticatedId);
        });
    }

    /**
     * Action when clicking on the home button
     */
    @FXML
    public void homeListView() {
        homeListView(null);
    }

    /**
     * Sets the list view to show the timeline of home
     * toots of the current logged in user
     *
     * @param max_id The id of the toot from which to start downloading
     */
    public void homeListView(String max_id) {
        if (max_id == null) {
            listViewItems.clear();
            listViewItems.add(new Generic(Generic.of.MESSAGE, "Loading..."));
        }
        logger.debug("Attempting to download home toots");
        showLoading();
        AsyncUtils.asyncTask(() -> {
            if (!NetworkUtils.hasInternet()) return null;
            List<Toot> toots;
            // Here, the id parameter is going to control which toots
            // from which are going to be downloaded
            toots = APIAccessManager.getHomeTootsId(token, max_id);
            return toots;
        }, toots -> {
            if (toots == null) {
                listViewItems.add(new Generic(Generic.of.ERROR, "Error downloading home toots. Please check your connection and try again."));
                logger.error("Error downloading home toots from user id: " + authenticatedId);
                return;
            }
            hideLoading();
            logger.info("Downloaded " + toots.size() + " home toots from user id: " + authenticatedId);
            if (max_id == null) {
                listViewItems.remove(0);
                listViewItems.add(new Generic(Generic.of.MESSAGE, "Home"));
            }
            listViewItems.addAll(toots);
            status.clear();
            status.put(view.HOME, null);
        });
    }

    /**
     * Sets the list view to show the toots liked by the current logged in user
     */
    @FXML
    void likedTootsListView() {
        listViewItems.clear();
        listViewItems.add(new Generic(Generic.of.MESSAGE, "Loading..."));
        logger.debug("Attempting to download liked toots");
        showLoading();
        AsyncUtils.asyncTask(() -> {
            if (!NetworkUtils.hasInternet()) return null;
            List<Toot> toots;
            // Here, the id parameter is going to control which toots
            // from which are going to be downloaded
            toots = APIAccessManager.getLikedToots(token);
            return toots;
        }, toots -> {
            if (toots == null) {
                listViewItems.add(new Generic(Generic.of.ERROR, "Error downloading liked toots. Please check your connection and try again."));
                logger.error("Error downloading liked toots from user id: " + authenticatedId);
                return;
            }
            hideLoading();
            logger.info("Downloaded " + toots.size() + " liked toots from user id" + authenticatedId);
            if (toots.size() == 0) {
                listViewItems.add(new Generic(Generic.of.MESSAGE, "No liked toots found"));
                return;
            }
            listViewItems.remove(0);
            listViewItems.add(new Generic(Generic.of.MESSAGE, "Liked toots"));
            listViewItems.addAll(toots);
            status.clear();
            status.put(view.FAVOURITES, authenticatedId);
        });
    }

    /**
     * Sets the list view to show the toots of
     * the user with the id passed as a parameter
     *
     * @param id (String) The id of the user whose toots are going to be shown
     */
    public void userTootListViewFromId(String id) {
        userTootListViewFromId(id, null);
    }

    /**
     * Sets the list view to show the toots of
     * the user with the id passed as a parameter
     *
     * @param id     (String) The id of the user whose toots are going to be shown
     * @param max_id (String) The id of the toot from which to start downloading
     */
    @FXML
    public void userTootListViewFromId(String id, String max_id) {
        if (max_id == null) {
            listViewItems.clear();
            listViewItems.add(new Generic(Generic.of.MESSAGE, "Loading..."));
        }
        logger.debug("Attempting to download profile and toots from id: " + id);
        showLoading();
        if (max_id == null)
            AsyncUtils.asyncTask(() -> {
                if (!NetworkUtils.hasInternet()) return null;
                Account account;
                account = APIAccessManager.getAccount(id, token);
                return account;
            }, account -> {
                if (account == null) {
                    listViewItems.add(new Generic(Generic.of.ERROR, "Error downloading profile . Please check your connection and try again."));
                    logger.error("Error downloading profile from user id: " + id);
                    return;
                }
                if (max_id == null)
                    listViewItems.remove(0);
                listViewItems.add(account);
                logger.info("Downloaded profile from id: " + id);
                listViewItems.add(new Generic(Generic.of.MESSAGE, "Loading..."));
            });
        AsyncUtils.asyncTask(() -> {
            if (!NetworkUtils.hasInternet()) return null;
            List<Toot> toots;
            // Here, the id parameter is going to control which toots
            // from which are going to be downloaded
            toots = APIAccessManager.getTootId(id, token, max_id);
            return toots;
        }, toots -> {
            if (toots == null) {
                listViewItems.add(new Generic(Generic.of.ERROR, "Error downloading profile toots. Please check your connection and try again."));
                logger.error("Error downloading profile toots from user id" + id);
                return;
            }
            if (max_id == null) {
                listViewItems.remove(listViewItems.size() - 1);
                listViewItems.add(new Generic(Generic.of.MESSAGE, "Toots and replies"));
            }
            listViewItems.addAll(toots);
            logger.info("Downloaded " + toots.size() + " toots from user id: " + id);
            hideLoading();
            status.clear();
            status.put(view.PROFILETOOT, id);
        });
    }

    public void loggedUserListView() {
        userTootListViewFromId(authenticatedId, null);
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
        listViewItems.add(new Generic(Generic.of.MESSAGE, "Loading..."));
        showLoading();
        logger.debug("Attempting to mastodon id from username: " + username);
        AsyncUtils.asyncTask(() -> {
            if (!NetworkUtils.hasInternet()) return null;
            String id;
            // Here, the id parameter is going to control which toots
            // from which are going to be downloaded
            id = APIAccessManager.getIdFromUsername(username);
            return id;
        }, id -> {
            if (id == null) {
                listViewItems.add(new Generic(Generic.of.ERROR, "Error getting id from account. Please check your connection and try again."));
                logger.error("Error getting id from account: " + username);
                return;
            }
            logger.debug("Downloaded id from username: " + username);
            userTootListViewFromId(id, null);
        });
    }

    /**
     * Initializes the list view
     */
    @FXML
    void initialize() {
        MainWindowController thisclass = this;
        if (vbox != null) {
            // Map the list view items to the vbox.
            // Each domain entity (a) will contain the logic to display the item
            // like invoking each appropriate controller and returning its view
            DisplayUtils.mapByValue(listViewItems, vbox.getChildren(), a -> a.display(thisclass));
        }
        // Disable Horizontal Scrolling
        scrollpane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollpane.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.getDeltaX() != 0) {
                event.consume();
            }
        });
        // Enter button in search query
        searchQuery.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                searchBtn.fire();
            }
        });
        // Infinite scroll
        scrollpane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
            // check if the new value is close to 1.0 (the bottom of the VBox)
            if (newValue.doubleValue() > oldValue.doubleValue() && newValue.doubleValue() >= 0.95) {
                // check if the VBox has overflowed and created a scrollbar
                if (!status.containsKey(view.LOADING) && listViewItems.size() > 5 && scrollpane.getViewportBounds().getHeight() < vbox.getBoundsInParent().getHeight() && !infinityBlock) {
                    // user has scrolled to the bottom of the VBox
                    infinityBlock = true;
                    for (view v : status.keySet()) {
                        if (v == view.LOADING) continue;
                        switch (v) {
                            case HOME -> homeListView(((Toot) listViewItems.get(listViewItems.size() - 1)).id);
                            case PROFILETOOT ->
                                    userTootListViewFromId(status.get(view.PROFILETOOT), ((Toot) listViewItems.get(listViewItems.size() - 1)).id);
                        }
                    }
                    infinityBlock = false;
                }
            }
        });

    }
}
