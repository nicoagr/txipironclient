package eus.ehu.txipironesmastodonfx.controllers.windowControllers;

import eus.ehu.txipironesmastodonfx.TxipironClient;
import eus.ehu.txipironesmastodonfx.controllers.main.MainWindowController;
import eus.ehu.txipironesmastodonfx.data_access.APIAccessManager;
import eus.ehu.txipironesmastodonfx.data_access.AsyncUtils;
import eus.ehu.txipironesmastodonfx.data_access.NetworkUtils;
import eus.ehu.txipironesmastodonfx.domain.Follow;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller class for the follow cell.
 * It will be used to display the follow cells in the follow window.
 *
 * @author Nicolás Aguado
 * @author Haizea Bermejo
 * @author Xiomara Cáceces
 * @author Marcos Chouciño
 */
public class FollowCellController {
    private MainWindowController master;
    private static final Logger logger = LogManager.getLogger("FollowCellController");
    @FXML
    private AnchorPane anchor;
    String idauxi;//porsi luego modificamos la otra, necesitamos esto para ir a su perfil
    @FXML
    private ImageView icon;
    @FXML
    private Label displayNameTxt;
    @FXML
    private Button followButton;
    @FXML
    private Label username;

    /**
     * Button to follow or unfollow the user
     */
    @FXML
    void onClickFollowButton() {
        followButton.setDisable(true);
        if (followButton.getText().equals("Follow")) {
            String load = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("Load");
            followButton.setText(load);
            AsyncUtils.asyncTask(() -> APIAccessManager.follow(master.token, idauxi), param -> {
                if (param == null) {
                    followButton.setText("Error!");
                    logger.error("Error when following userid: " + idauxi);
                } else {
                    logger.info("Followed user with id: " + idauxi + " successfully");
                    String unfollow = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("Unfollow");
                    followButton.setText(unfollow);
                    followButton.setDisable(false);
                }
            });
        } else if (followButton.getText().equals("Unfollow")) {
            String load = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("Load");
            followButton.setText(load);
            AsyncUtils.asyncTask(() -> APIAccessManager.unfollow(master.token, idauxi), param -> {
                if (param == null) {
                    followButton.setText("Error!");
                    logger.error("Error when unfollowing userid: " + idauxi);
                } else {
                    logger.info("Unfollowed user with id: " + idauxi + " successfully");
                    String follow = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("Follow");
                    followButton.setText(follow);
                    followButton.setDisable(false);
                }
            });
        }
    }

    /**
     * goes to the profile of the user wich has been clicked
     */
    @FXML
    void pickClick() {
        master.userTootListViewFromId(idauxi);
    }

    /**
     * Constructor for the controller.
     * It will load itself at take consciousness (set itself as controller)
     * Also it will set the corresponding values for the account cell.
     *
     * @param follow (Follow) - The account to be displayed
     * @param master (MainWindowController)- The controller of the main class, will be used for internal comunication
     */
    public FollowCellController(Follow follow, MainWindowController master) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/eus/ehu/txipironesmastodonfx/maincell/followcell.fxml"),
                ResourceBundle.getBundle("strings", TxipironClient.lang));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.setReference(master);
        String load = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("Load");
        followButton.setText(load);
        // set the values for the follow button
        AsyncUtils.asyncTask(() -> {
            if (follow.id.equals(master.authenticatedId)) return "Self";
            List<Follow> following = APIAccessManager.getFollow(master.authenticatedId, master.token, true);
            if (following == null) return "Error!";
            for (Follow f : following) {
                if (f.id.equals(follow.id)) {
                    return ResourceBundle.getBundle("strings", TxipironClient.lang).getString("Unfollow");
                }
            }
            return ResourceBundle.getBundle("strings", TxipironClient.lang).getString("Follow");
        }, param -> {
            if (param.equals("Self")) followButton.setVisible(false);
            if (!param.equals("Error!")) followButton.setDisable(false);
            followButton.setText(param);
            logger.debug("Loaded follow cell for user: " + follow.id);
        });
        username.setText("@" + follow.acct);
        idauxi = follow.id;
        displayNameTxt.setText(follow.display_name);
        icon.setImage(new Image(getClass().getResourceAsStream("/eus/ehu/txipironesmastodonfx/mainassets/dark-accounticon.png")));
        AsyncUtils.asyncTask(() ->
                {
                    Image img = null;
                    if (NetworkUtils.hasInternet())
                        img = new Image(follow.avatar);
                    return img;
                }, param -> {
                    if (param != null) icon.setImage(param);
                    else
                        icon.setImage(new Image(getClass().getResourceAsStream("/eus/ehu/txipironesmastodonfx/mainassets/dark-notfound.jpg")));
                }
        );

    }

    /**
     * Getter for the UI (AnchorPane)
     * This method will be used by the AuthWindowController
     * in order to display custom cells in the listview
     *
     * @return (AnchorPane) - The UI of the controller
     */
    public AnchorPane getUI() {
        return anchor;
    }

    /**
     * Setter for the reference to the auth window controller
     *
     * @param thisclass (AuthWindowController) - The reference to the auth window controller
     */
    public void setReference(MainWindowController thisclass) {
        this.master = thisclass;
    }
}
