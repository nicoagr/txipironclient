package eus.ehu.txipironesmastodonfx.controllers.windowControllers;

import eus.ehu.txipironesmastodonfx.controllers.main.MainWindowController;
import eus.ehu.txipironesmastodonfx.data_access.APIAccessManager;
import eus.ehu.txipironesmastodonfx.data_access.AsyncUtils;
import eus.ehu.txipironesmastodonfx.data_access.HTMLParser;
import eus.ehu.txipironesmastodonfx.data_access.NetworkUtils;
import eus.ehu.txipironesmastodonfx.domain.Account;
import eus.ehu.txipironesmastodonfx.domain.Follow;
import eus.ehu.txipironesmastodonfx.domain.MediaAttachment;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the profile cell
 *
 * @author Nicolás Aguado
 * @author Haizea Bermejo
 * @author Xiomara Cáceces
 * @author Marcos Chouciño
 */
public class ProfileCellControllers {

    private MainWindowController master;

    private String id;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ImageView bannerPic;

    @FXML
    private AnchorPane anchor;

    @FXML
    private Button followButton;

    @FXML
    private Label name;

    @FXML
    private Label numFollowers;

    @FXML
    private Label numFollowing;

    @FXML
    private Label numPost;

    @FXML
    private ImageView profilePic;

    @FXML
    private Label username;

    @FXML
    private TextFlow description;
    private String imageurl;

    /**
     * Constructor for the profile cell controller
     *
     * @param master (MainWindowController) - The reference to the main window controller
     */
    public ProfileCellControllers(MainWindowController master) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/eus/ehu/txipironesmastodonfx/maincell/profileCell.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setReference(master);
    }

    /**
     * Method to set the reference to the main window controller
     *
     * @param account (Account) - The account to load
     */
    public void loadAccount(Account account) {

        bannerPic.setPreserveRatio(false);
        bannerPic.setSmooth(true);
        bannerPic.setCache(true);

        this.username.setText("@" + account.acct);
        this.name.setText(account.display_name);
        this.numPost.setText(String.valueOf(account.statuses_count));
        this.numFollowers.setText(String.valueOf(account.followers_count));
        this.numFollowing.setText(String.valueOf(account.following_count));
        this.id = account.id;
        List<Text> texts = new ArrayList<>();
        for (String string : HTMLParser.parseHTML(account.note)) {
            Text text = new Text(string);
            texts.add(text);
            texts.add(new Text(" "));

        }
        this.description.getChildren().addAll(texts);
        profilePic.setImage(new Image(getClass().getResourceAsStream("/eus/ehu/txipironesmastodonfx/mainassets/dark-accounticon.png")));
        AsyncUtils.asyncTask(() ->
                {
                    Image img = null;
                    if (NetworkUtils.hasInternet()) {
                        img = new Image(account.avatar);
                        imageurl = account.avatar;
                    }
                    return img;
                }, param -> {
                    if (param != null) profilePic.setImage(param);
                    else
                        profilePic.setImage(new Image(getClass().getResourceAsStream("/eus/ehu/txipironesmastodonfx/mainassets/dark-notfound.jpg")));
                }
        );
        AsyncUtils.asyncTask(() ->
                {
                    Image imgg = null;
                    if (NetworkUtils.hasInternet())
                        imgg = new Image(account.header);
                    return imgg;
                }, param -> {
                    if (param != null) bannerPic.setImage(param);
                }
        );
        AsyncUtils.asyncTask(() -> {
            if (account.id.equals(master.authenticatedId)) return "Self";
            List<Follow> following = APIAccessManager.getFollow(master.authenticatedId, master.token, true);
            if (following == null) return "Error!";
            for (Follow f : following) {
                if (f.id.equals(account.id)) {
                    return "Unfollow";
                }
            }
            return "Follow";
        }, param -> {
            if (param.equals("Self")) followButton.setVisible(false);
            else if (!param.equals("Error!")) followButton.setDisable(false);
            followButton.setText(param);
        });
    }

    @FXML
    void onClickFollowButton() {
        followButton.setDisable(true);
        followButton.setText("Loading...");
        if (followButton.getText().equals("Follow")) {
            AsyncUtils.asyncTask(() -> APIAccessManager.follow(master.token, this.id), param -> {
                if (param == null) {
                    followButton.setText("Error!");
                } else {
                    followButton.setText("Unfollow");
                    followButton.setDisable(false);
                }
            });
        } else if (followButton.getText().equals("Unfollow")) {
            AsyncUtils.asyncTask(() -> APIAccessManager.unfollow(master.token, this.id), param -> {
                if (param == null) {
                    followButton.setText("Error!");
                } else {
                    followButton.setText("Follow");
                    followButton.setDisable(false);
                }
            });
        }
    }

    @FXML
    void openProfilePic() {
        if (imageurl == null) return;
        AsyncUtils.asyncTask(() -> {
            // create the popup
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/eus/ehu/txipironesmastodonfx/maincell/mediaViewer.fxml"));
            Parent root = fxmlLoader.load();
            MediaViewController contr = fxmlLoader.getController();
            contr.setMedia(List.of(new MediaAttachment("image", imageurl)));
            contr.setReference(master);
            Scene scene = new Scene(root);
            return List.of(scene, contr);
        }, list -> {
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setScene((Scene) list.get(0));
            popupStage.setResizable(false);
            popupStage.setTitle("Txipiron Client [v1.0] - a Mastodon Client - Media Viewer");
            popupStage.getIcons().add(new Image("file:src/main/resources/eus/ehu/txipironesmastodonfx/mainassets/dark-media-512.png"));
            ((MediaViewController) list.get(1)).setPopupStage(popupStage);
            popupStage.showAndWait();
        });
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
