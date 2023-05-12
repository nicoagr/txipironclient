package eus.ehu.txipironesmastodonfx.controllers.main;

import eus.ehu.txipironesmastodonfx.TxipironClient;
import eus.ehu.txipironesmastodonfx.controllers.WindowController;
import eus.ehu.txipironesmastodonfx.data_access.*;
import eus.ehu.txipironesmastodonfx.domain.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.event.WindowAdapter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;

/**
 * Controller class for the main window.
 *
 * @author Nicolás Aguado
 * @author Haizea Bermejo
 * @author Xiomara Cáceces
 * @author Marcos Chouciño
 */
public class MainWindowController implements WindowController {

    @FXML
    public BorderPane mainBorderpane;
    public NotificationSystem notificationSystem = new NotificationSystem();
    private TxipironClient mainApp;
    private static final Logger logger = LogManager.getLogger("MainWindowController");
    public String id;
    private HashMap<view, String> status = new HashMap<>();
    private boolean infinityBlock = false;
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
    public VBox vbox;
    @FXML
    public ScrollPane scrollpane;
    public ObservableList<CellController> listViewItems = FXCollections.observableArrayList();
    public boolean autoplayMedia = false;
    public String load = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("Load");

    private enum view {
        HOME, PROFILETOOT, PROFILEFOLLOWING, PROFILEFOLLOWERS, SEARCH, POST_TOOT, NOTIFICATION, FAVOURITES, BOOKMARKS, SETTINGS, LOADING
    }

    /**
     * Changes title and Shows loading gif
     */
    public void showLoading() {
        String mainTitle1 = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("LoadTitle");
        mainApp.setStageTitle(mainTitle1);
        loading.setVisible(true);
        status.clear();
        status.put(view.LOADING, null);
    }

    /**
     * Changes the title
     * and hides the loading gif
     */
    public void hideLoading() {
        String mainTitle2 = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("MainTitle");
        mainApp.setStageTitle(mainTitle2);
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
        String post = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("Post");
        listViewItems.add(new Generic(Generic.of.POST_TOOT, post));
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
        notificationSystem.deactivateNotification();
        mainApp.changeScene("Auth", null);
    }

    /**
     * Sets the reference of the current logged in user and sets the avatar
     * Also initializes notifications
     *
     * @param result (List<Object>) - The list of reference and token to be set
     */
    @Override
    public void setRefTokenId(List<Object> result) {
        // Add listener for correctly closing notifications
        mainApp.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEvent);

        this.ref = (Integer) result.get(0);
        this.token = (String) result.get(1);
        this.notificationSystem = new NotificationSystem();
        notificationSystem.activateNotifications(this);

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
            String error = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("Error1");
            listViewItems.add(new Generic(Generic.of.ERROR, error));
            logger.error("User tried to type a null search query");
            return;
        }
        listViewItems.add(new Generic(Generic.of.MESSAGE, load));
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
                String error = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("Error2");
                listViewItems.add(new Generic(Generic.of.ERROR, error));
                logger.error("Error downloading search results. Please check your connection and try again.");
                return;
            }
            logger.info("Performed search with query: " + searchQuery.getText() + " and got " + res.accounts.size() + " accounts and " + res.statuses.size() + " statuses");
            status.clear();
            status.put(view.SEARCH, null);
            listViewItems.remove(0);
            if (res.accounts.size() == 0) {
                String noAcc = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("NoAcc");
                listViewItems.add(new Generic(Generic.of.MESSAGE, noAcc));
            } else {
                String acc = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("Acc");
                listViewItems.add(new Generic(Generic.of.MESSAGE, acc));
                listViewItems.addAll(res.accounts);
            }
            if (res.statuses.size() == 0) {
                String noStat = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("NoStat");
                listViewItems.add(new Generic(Generic.of.MESSAGE, noStat));
            } else {
                String stat = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("Stat");
                listViewItems.add(new Generic(Generic.of.MESSAGE, stat));
                listViewItems.addAll(res.statuses);
            }
        });
    }

    /**
     * Loads the notification view
     */
    @FXML
    void NotificationListView() {
        listViewItems.clear();
        listViewItems.add(new Generic(Generic.of.MESSAGE, load));
        showLoading();
        AsyncUtils.asyncTask(() -> {
            if (!NetworkUtils.hasInternet()) return null;
            List<Notification> notifications;
            logger.debug("Attempting to download notifications and clear them");
            notifications = APIAccessManager.getNewNotification(token);
            APIAccessManager.clearNotification(token);
            return notifications;
        }, notifications -> {
            listViewItems.clear();
            if (notifications == null) {
                String error = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("Error19");
                listViewItems.add(new Generic(Generic.of.ERROR, error));
                return;
            }
            hideLoading();
            status.clear();
            status.put(view.NOTIFICATION, null);
            String notif = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("Notification");
            listViewItems.add(new Generic(Generic.of.MESSAGE, notif));
            lastNotification = notifications.get(0).id;
            logger.info("Downloaded " + notifications.size() + " notifications");
            for (Notification element : notifications) {
                if (element.type.equals("mention")) {
                    String mention = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("Mention");
                    listViewItems.add(new Generic(Generic.of.MESSAGE, element.account.acct + mention));
                    listViewItems.add(element.status);
                } else if (element.type.equals("status")) {
                    String status = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("Status");
                    listViewItems.add(new Generic(Generic.of.MESSAGE, element.account.acct + status));
                    listViewItems.add(element.status);
                } else if (element.type.equals("follow")) {
                    String follow = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("FollowNotif");
                    listViewItems.add(new Generic(Generic.of.MESSAGE, element.account.acct + follow));
                    listViewItems.add(element.account);
                } else if (element.type.equals("favorite")) {
                    String fav = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("FavNotif");
                    listViewItems.add(new Generic(Generic.of.MESSAGE, element.account.acct + fav));
                    listViewItems.add(element.status);
                }
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
        listViewItems.add(new Generic(Generic.of.MESSAGE, load));
        logger.debug("Attempting to download bookmarked toots");
        showLoading();
        AsyncUtils.asyncTask(() -> {
            if (!NetworkUtils.hasInternet()) return null;
            List<Toot> toot;
            toot = APIAccessManager.getBookmarkedToots(token);
            return toot;
        }, toot -> {
            if (toot == null) {
                String error = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("Error3");
                listViewItems.add(new Generic(Generic.of.ERROR, error));
                logger.error("Error downloading bookmarked toots. Please check your connection and try again.");
                return;
            }
            hideLoading();
            logger.info("Downloaded " + toot.size() + " bookmarked toots");
            if (toot.size() == 0) {
                String noBook = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("NoBook");
                listViewItems.add(new Generic(Generic.of.MESSAGE, noBook));
                return;
            }
            listViewItems.remove(0);
            String book = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("Bookmark");
            listViewItems.add(new Generic(Generic.of.MESSAGE, book));
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
            listViewItems.add(new Generic(Generic.of.MESSAGE, load));
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
                String error = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("Error4");
                listViewItems.add(new Generic(Generic.of.ERROR, error));
                logger.error("Error downloading home toots from user id: " + authenticatedId);
                return;
            }
            hideLoading();
            logger.info("Downloaded " + toots.size() + " home toots from user id: " + authenticatedId);
            if (max_id == null) {
                listViewItems.remove(0);
                String home = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("Home");
                listViewItems.add(new Generic(Generic.of.MESSAGE, home));
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
                String error = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("Error5");
                listViewItems.add(new Generic(Generic.of.ERROR, error));
                logger.error("Error downloading liked toots from user id: " + authenticatedId);
                return;
            }
            hideLoading();
            logger.info("Downloaded " + toots.size() + " liked toots from user id" + authenticatedId);
            if (toots.size() == 0) {
                String noLike = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("NoLike");
                listViewItems.add(new Generic(Generic.of.MESSAGE, noLike));
                return;
            }
            listViewItems.remove(0);
            String like = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("Like");
            listViewItems.add(new Generic(Generic.of.MESSAGE, like));            listViewItems.addAll(toots);
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
    @FXML
    public void firstUserTootListViewFromId(String id) {
        listViewItems.clear();
        listViewItems.add(new Generic(Generic.of.MESSAGE, "Loading..."));
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
                listViewItems.add(new Generic(Generic.of.ERROR, "Error downloading profile . Please check your connection and try again."));
                logger.error("Error downloading profile from user id: " + id);
                return;
            }
            listViewItems.add(account);
            logger.info("Downloaded profile from id: " + id);
            listViewItems.add(new Generic(Generic.of.MESSAGE, "Loading..."));
        });
        AsyncUtils.asyncTask(() -> {
            if (!NetworkUtils.hasInternet()) return null;
            List<Toot> toots;
            // Here, the id parameter is going to control which toots
            // from which are going to be downloaded
            toots = APIAccessManager.getTootId(id, token, null);
            return toots;
        }, toots -> {
            if (toots == null) {
                String error = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("Error7");
                listViewItems.add(new Generic(Generic.of.ERROR, error));
                logger.error("Error downloading profile toots from user id" + id);
                return;
            }
            listViewItems.remove(listViewItems.size() - 1);
            String tr = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("TootReply");
            listViewItems.add(new Generic(Generic.of.MESSAGE, tr));
            listViewItems.addAll(toots);
            logger.info("Downloaded " + toots.size() + " toots from user id: " + id);
            hideLoading();
            status.clear();
            status.put(view.PROFILETOOT, id);
        });
    }

    /**
     * Sets the list view to show the toots of
     * the user with the id passed as a parameter
     *
     * @param id     (String) The id of the user whose toots are going to be shown
     * @param max_id (String) The id of the minimum toot to show
     */
    @FXML
    public void userTootListViewFromId(String id, String max_id) {
        if (max_id == null) {
            Iterator<CellController> it = listViewItems.iterator();
            it.next();
            while (it.hasNext()) {
                it.next();
                it.remove();
            }
        }
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
                listViewItems.add(new Generic(Generic.of.ERROR, "Error downloading profile . Please check your connection and try again."));
                logger.error("Error downloading profile from user id: " + id);
                return;
            }
            listViewItems.add(account);
            logger.info("Downloaded profile from id: " + id);
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
            String tootreply = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("TootReply");
            listViewItems.add(new Generic(Generic.of.MESSAGE, tootreply));
            listViewItems.addAll(toots);
            logger.info("Downloaded " + toots.size() + " toots from user id: " + id);
            hideLoading();
        });
    }
    /**
     * click on the profile pic to see my profile
     */
    public void loggedUserListView(){
        firstUserTootListViewFromId(authenticatedId);
    }

    /**
     * Sets the list view to show the followers of
     * the user with the id passed as a parameter
     *
     * @param id (String) The id of the user whose toots are going to be shown
     */
    @FXML
    public void userTootListViewFromIdFollowers(String id) {
        Iterator<CellController> it = listViewItems.iterator();
        it.next();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        logger.debug("Attempting to download profile and followers from id: " + id);
        showLoading();
        AsyncUtils.asyncTask(() -> {
            if (!NetworkUtils.hasInternet()) return null;
            Account account;
            account = APIAccessManager.getAccount(id, token);
            return account;
        }, account -> {
            listViewItems.clear();
            if (account == null) {
                String profileerr = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("Error6");
                listViewItems.add(new Generic(Generic.of.ERROR, profileerr));
                logger.error("Error downloading profile from user id: " + id);
                return;
            }
            listViewItems.add(account);
            logger.info("Downloaded profile from id: " + id);
        });
        AsyncUtils.asyncTask(() -> {
            if (!NetworkUtils.hasInternet()) return null;
            List<Follow> follower;
            follower = APIAccessManager.getFollow(id, token, false);
            return follower;
        }, follower -> {
            if (follower == null) {
                listViewItems.add(new Generic(Generic.of.MESSAGE, "Error downloading followers. Please check your connection and try again."));
                return;
            }
            String followtxt = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("Followers");
            listViewItems.add(new Generic(Generic.of.MESSAGE, followtxt));
            listViewItems.addAll(follower);
            logger.info("Downloaded " + follower.size() + " followers from user id: " + id);
            hideLoading();
        });
    }
    /**
     * Sets the list view to show the post of
     * the user with the id passed as a parameter
     *
     * @param id (String) The id of the user whose toots are going to be shown
     */
    @FXML
    public void userTootListViewFromIdFollowings(String id) {
       Iterator<CellController> it = listViewItems.iterator();
        it.next();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        logger.debug("Attempting to download profile and followings from id: " + id);
        showLoading();
        AsyncUtils.asyncTask(() -> {
            if (!NetworkUtils.hasInternet()) return null;
            Account account;
            account = APIAccessManager.getAccount(id, token);
            return account;
        }, account -> {
            listViewItems.clear();
            if (account == null) {
                String profileerr = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("Error6");
                listViewItems.add(new Generic(Generic.of.ERROR, profileerr));
                logger.error("Error downloading profile from user id: " + id);
                return;
            }
            listViewItems.add(account);
            logger.info("Downloaded profile from id: " + id);
        });
        AsyncUtils.asyncTask(() -> {
            if (!NetworkUtils.hasInternet()) return null;
            List<Follow> follower;
            follower = APIAccessManager.getFollow(id, token, true);
            return follower;
        }, follower -> {
            if (follower == null) {
                listViewItems.add(new Generic(Generic.of.MESSAGE, "Error downloading following. Please check your connection and try again."));
                return;
            }
            String following = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("Following");
            listViewItems.add(new Generic(Generic.of.MESSAGE, following));
            listViewItems.addAll(follower);
            logger.info("Downloaded " + follower.size() + " followings from user id: " + id);
            hideLoading();
        });
    }

    /**
     * Sets the list view to show the followers of the current logged in user
     */
    public void followerListView(String id) {
        Iterator<CellController> it = listViewItems.iterator();
        it.next();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        showLoading();
        AsyncUtils.asyncTask(() -> {
            if (!NetworkUtils.hasInternet()) return null;
            List<Follow> follower;
            follower = APIAccessManager.getFollow(id, token, false);
            return follower;
        }, follower -> {
            listViewItems.clear();
            if (follower == null) {
                listViewItems.add(new Generic(Generic.of.MESSAGE, "Error downloading followers. Please check your connection and try again."));
                return;
            }
            hideLoading();
            listViewItems.add(new Generic(Generic.of.MESSAGE, "Followers"));
            listViewItems.addAll(follower);
        });
    }
    /**
     * Sets the list view to show the users that the current logged in user is following
     */
    public void followingListView(String id) {
        Iterator<CellController> it = listViewItems.iterator();
        it.next();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        listViewItems.add(new Generic(Generic.of.MESSAGE, load));
        showLoading();
        AsyncUtils.asyncTask(() -> {
            if (!NetworkUtils.hasInternet()) return null;
            List<Follow> follower;
            follower = APIAccessManager.getFollow(id, token, true);
            return follower;
        }, following -> {
            listViewItems.clear();
            if (following == null) {
                listViewItems.add(new Generic(Generic.of.MESSAGE, "Error downloading following. Please check your connection and try again."));
                return;
            }
            hideLoading();
            listViewItems.add(new Generic(Generic.of.MESSAGE, "Following"));
            listViewItems.addAll(following);
        });
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
        listViewItems.add(new Generic(Generic.of.MESSAGE, load));
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
                String error = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("Error8");
                listViewItems.add(new Generic(Generic.of.ERROR, error));
                logger.error("Error getting id from account: " + username);
                return;
            }
            logger.debug("Downloaded id from username: " + username);
            userTootListViewFromId(id, null);
        });
    }

    /**
     * Event triggered when the window is closing
     * It will turn off the notification system
     *
     * @param windowEvent (WindowEvent) The event triggered
     */
    void closeWindowEvent(WindowEvent windowEvent) {
        logger.info("Detected window closing. Shutting down...");
        notificationSystem.deactivateNotification();
    }

    /**
     * Initializes the list view
     */
    @FXML
    void initialize() {
        MainWindowController thisclass = this;
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                notificationSystem.deactivateNotification();
            }
        });
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
