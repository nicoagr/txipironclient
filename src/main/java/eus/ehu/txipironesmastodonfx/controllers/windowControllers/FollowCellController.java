package eus.ehu.txipironesmastodonfx.controllers.windowControllers;

import eus.ehu.txipironesmastodonfx.controllers.main.MainWindowController;
import eus.ehu.txipironesmastodonfx.data_access.APIAccessManager;
import eus.ehu.txipironesmastodonfx.data_access.AsyncUtils;
import eus.ehu.txipironesmastodonfx.data_access.NetworkUtils;
import eus.ehu.txipironesmastodonfx.domain.Follow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    @FXML
    private AnchorPane anchor;
    String idauxi;//porsi luego modificamos la otra, necesitamos esto para ir a su perfil
    @FXML
    private ImageView icon;

    @FXML
    private Label id;

    @FXML
    private Button followButton;

    @FXML
    private Label username;

    /**
     * Button to follow or unfollow the user
     * @param event (ActionEvent) - The event that triggers the method
     */
    @FXML
    void onClickFollowButton(ActionEvent event) {
        if (followButton.getText().equals("Follow")) {

            followButton.setText("Unfollow");
        } else {

            followButton.setText("Follow");
        }
    }


    /**
     * goes to the profile of the user wich has been clicked
     */
    @FXML
    void clickedThings() {
        master.userTootListViewFromId(idauxi);
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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/eus/ehu/txipironesmastodonfx/maincell/followcell.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.setReference(master);
        List<Follow> following= new ArrayList<>();
        followButton.setText("Loading...");
        // set the values for the account cell
        AsyncUtils.asyncTask(()->

                APIAccessManager.getFollow(master.authenticatedId, master.token, true)

        , param -> {

                    if (param != null) {
                        for (Follow f : param) {
                            if (f.id.equals(follow.id)) {
                                followButton.setText("Unfollow");
                            }
                        }
                    }
        }
                );


        username.setText(follow.acct);
        id.setText(follow.id);
        idauxi = follow.id;
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
