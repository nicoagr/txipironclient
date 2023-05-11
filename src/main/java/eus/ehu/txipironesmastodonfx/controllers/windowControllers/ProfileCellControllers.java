package eus.ehu.txipironesmastodonfx.controllers.windowControllers;

import eus.ehu.txipironesmastodonfx.TxipironClient;
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
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
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
    boolean self = false;
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
    private Button omniButton;

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
    private String load = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("Load");;

    /**
     * Constructor for the profile cell controller
     *
     * @param master (MainWindowController) - The reference to the main window controller
     */
    public ProfileCellControllers(MainWindowController master) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/eus/ehu/txipironesmastodonfx/maincell/profileCell.fxml"),
                ResourceBundle.getBundle("strings", TxipironClient.lang));
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
            if (account.id.equals(master.authenticatedId)) return "Change Picture";
            List<Follow> following = APIAccessManager.getFollow(master.authenticatedId, master.token, true);
            if (following == null) return "Error!";
            for (Follow f : following) {
                if (f.id.equals(account.id)) {
                    return "Unfollow";
                }
            }
            return "Follow";
        }, param -> {
            if (param.equals("Change Picture")) self = true;
            if (!param.equals("Error!")) omniButton.setDisable(false);
            omniButton.setText(param);
        });
    }

    /**
     * This method will be called
     * when the user clicks in the omnibutton.
     * It will decide which action to perform
     */
    @FXML
    void onClickOmniButton() {
        if (self) {
            onClickChangeProfilePicButton();
        } else {
            onClickFollowButton();
        }
    }

    /**
     * This method will be executed
     * when the user clicks on the
     * omnibutton, and it is in mode
     * "Change Account"
     */
    private void onClickChangeProfilePicButton() {
        omniButton.setText(load);
        omniButton.setDisable(true);
        // Create a file chooser dialog
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Txipiron Client [v1.0] - a Mastodon Client - File Chooser");
        // Set the file extension filters
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        // Allow the user to select a file
        File selectedFile = fileChooser.showOpenDialog(master.TxipironClient().stage);
        if (selectedFile == null) {
            omniButton.setDisable(false);
            omniButton.setText("Change Picture");
            return;
        }
        if (HTMLParser.getFileExtension(selectedFile).matches("png|jpg|jpeg")) {
            AsyncUtils.asyncTask(() -> {
                if (selectedFile.length() > 2097151) {
                    return "File too big";
                }
                if (NetworkUtils.hasInternet()) {
                    boolean result = APIAccessManager.changeProfilePicture(master.token, selectedFile);
                    return (result) ? null : "Server error";
                }
                return null;
            }, res -> {
                if (res == null) {
                    master.refreshAvatar();
                    master.loggedUserListView();
                } else {
                    omniButton.setDisable(false);
                    omniButton.setText(res);
                }
            });
        }
    }

    /**
     * This method will be executed
     * when the user clicks on the
     * omnibutton, and it is in mode
     * "Follow/Unfollow
     */
    private void onClickFollowButton() {
        omniButton.setDisable(true);
        if (omniButton.getText().equals("Follow")) {
            omniButton.setText(load);
            AsyncUtils.asyncTask(() -> APIAccessManager.follow(master.token, this.id), param -> {
                if (param == null) {
                    omniButton.setText("Error!");
                } else {
                    omniButton.setText("Unfollow");
                    omniButton.setDisable(false);
                }
            });
        } else if (omniButton.getText().equals("Unfollow")) {
            omniButton.setText(load);
            AsyncUtils.asyncTask(() -> APIAccessManager.unfollow(master.token, this.id), param -> {
                if (param == null) {
                    omniButton.setText("Error!");
                } else {
                    omniButton.setText("Follow");
                    omniButton.setDisable(false);
                }
            });
        }
    }

    /**
     * This method will open the
     * current profiles picture
     * in a media viewer popup
     */
    @FXML
    void openProfilePic() {
        if (imageurl == null) return;
        AsyncUtils.asyncTask(() -> {
            // create the popup
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/eus/ehu/txipironesmastodonfx/maincell/mediaViewer.fxml"),
                    ResourceBundle.getBundle("strings", TxipironClient.lang));
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
            String popup = ResourceBundle.getBundle("strings", TxipironClient.lang).getString("MediaViewer");
            popupStage.setTitle(popup);
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
