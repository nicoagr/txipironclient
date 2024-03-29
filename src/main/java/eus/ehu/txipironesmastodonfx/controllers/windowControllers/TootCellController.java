package eus.ehu.txipironesmastodonfx.controllers.windowControllers;

import eus.ehu.txipironesmastodonfx.TxipironClient;
import eus.ehu.txipironesmastodonfx.controllers.main.MainWindowController;
import eus.ehu.txipironesmastodonfx.data_access.*;
import eus.ehu.txipironesmastodonfx.domain.MediaAttachment;
import eus.ehu.txipironesmastodonfx.domain.Toot;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the TootCell
 * This class will be used to display the toots in the main window
 * It will be used by the MainWindowController
 *
 * @author Nicolás Aguado
 * @author Haizea Bermejo
 * @author Xiomara Cáceces
 * @author Marcos Chouciño
 */
public class TootCellController {
    private String uri;
    List<MediaAttachment> media;
    List<Toot.Mention> mentions;
    @FXML
    private ImageView sensitiveImg;
    @FXML
    private ImageView shareImg;
    private String Id;
    @FXML
    private ImageView rebootedBy;
    @FXML
    private Label rebootedText;
    @FXML
    private Label date;
    @FXML
    private AnchorPane anchor;
    @FXML
    private Label numComments;
    @FXML
    private ImageView mediaImg;
    @FXML
    private Hyperlink viewMediaTxt;
    @FXML
    private ImageView likes;
    @FXML
    private Label numLikes;
    @FXML
    private ImageView reboot;
    @FXML
    private Label numReboots;
    @FXML
    private ImageView bookmarks;
    @FXML
    private TextFlow textFlow;
    @FXML
    private Label username;
    @FXML
    private ImageView imagen;
    private MainWindowController master;
    private boolean fav;
    private boolean reblog;
    private boolean bm;
    private String id;

    public TxipironClient txipi;

    /**
     * Constructor for the controller.
     * It will load itself at take consciousness (set itself as controller)
     * Also it will set the corresponding values for the account cell.
     *
     * @param master (MainWindowController)- The controller of the main class, will be used for internal comunication
     */
    public TootCellController(MainWindowController master) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/eus/ehu/txipironesmastodonfx/maincell/tootcell.fxml"),
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
     * Method to change to the toots of the user who posted that toot
     */
    @FXML
    void usernameClicked() {
        master.userTootListView(username.getText());
    }

    /**
     * Method to change to the toots of the user who posted that toot
     */
    @FXML
    void profilePictureClicked() {
        master.userTootListView(username.getText());
    }

    /**
     * Method to set the values for the toot cell
     *
     * @param toot (Toot) - The toot to be displayed
     */
    public void loadToot(Toot toot) {
        // Unflip the retoot recursion stack
        if (toot.reblog != null) {
            rebootedBy.setVisible(true);
            rebootedText.setVisible(true);
            String rebooted = TxipironClient.s("RebootBy");
            rebootedText.setText(rebooted + toot.account.acct);
        }
        while (toot.reblog != null) toot = toot.reblog;
        // set the values for the toot cell
        Id = toot.id;
        username.setText("@" + toot.account.acct);
        bm = toot.bookmarked;
        fav = toot.favourited;
        reblog = toot.reblogged;
        id = toot.id;
        imagen.setImage(new Image(getClass().getResourceAsStream("/eus/ehu/txipironesmastodonfx/mainassets/dark-accounticon.png")));
        Toot finalToot = toot;
        AsyncUtils.asyncTask(() ->
                {
                    Image img = null;
                    if (NetworkUtils.hasInternet())
                        img = new Image(finalToot.account.avatar);
                    return img;
                }, param -> {
                    if (param != null) imagen.setImage(param);
                    else
                        imagen.setImage(new Image(getClass().getResourceAsStream("/eus/ehu/txipironesmastodonfx/mainassets/dark-notfound.jpg")));

                }
        );
       AsyncUtils.asyncTask(() -> DisplayUtils.formatDate(finalToot.created_at), param -> date.setText(param));
        if (toot.favourited)
            likes.setId("liked");
        else
            likes.setId("unliked");
        numLikes.setText(Integer.toString(finalToot.favourites_count));
        if (finalToot.sensitive)
            sensitiveImg.setVisible(true);
        if(toot.reblogged)
            reboot.setId("reboot");
        else
            reboot.setId("unReboot");

        numReboots.setText(Integer.toString(finalToot.reblogs_count));
        if(toot.bookmarked)
            bookmarks.setId("bookmarked");
        else
            bookmarks.setId("unBookmarked");
        mentions = toot.mentions;
        numComments.setText(Integer.toString(finalToot.replies_count));
        uri = toot.uri;
        if (toot.media_attachments != null && !toot.media_attachments.isEmpty()) {
            media = toot.media_attachments;
            mediaImg.setVisible(true);
            viewMediaTxt.setVisible(true);
        }
        AsyncUtils.asyncTask(() -> updateContent(finalToot.content), param -> {
            for (Text t : param)
                textFlow.getChildren().add(t);
                textFlow.getStyleClass().add("tootText");
        });
    }

    /**
     * Method to update and get the (Text type) contents of the toot
     *
     * @param text (String) - The content of the toot as HTML
     * @return (List < Text >) - The contents of the toot as Text
     */
    private List<Text> updateContent(String text) {
        List<Text> lista = new ArrayList<>();
        List<String> parsedTweet = HTMLParser.parseHTML(text);
        for (int i = 0; i < parsedTweet.size(); i++) {
            String element = parsedTweet.get(i);
            if (element == null || element.isEmpty())
                continue;

            Text textElement = new Text();
            textElement.getStyleClass().add("tootText");

            // User tag
            if (element.startsWith("@")) {
                textElement.setText(element);
                textElement.setFill(Color.BLUE);
                textElement.setOnMouseEntered(event -> textElement.setStyle("-fx-text-fill: white; -fx-cursor: hand"));
                textElement.setOnMouseExited(event -> textElement.setStyle("-fx-text-fill: blue; -fx-cursor: inherit"));
                textElement.setOnMouseClicked(event -> AsyncUtils.asyncTask(() -> {
                    for (Toot.Mention aux : mentions) {
                        if (("@" + aux.username).equals(element)) {
                            return aux.id;
                        }
                    }
                    return null;
                }, param -> {
                    if (param != null) master.firstUserTootListViewFromId(param);
                }));
            }
            // HashTags
            else if (element.startsWith("#")) {
                textElement.setText(element);
                textElement.setFill(Color.CADETBLUE);
                textElement.setOnMouseEntered(event -> textElement.setStyle("-fx-text-fill: white;"));
                textElement.setOnMouseExited(event -> textElement.setStyle("-fx-text-fill: CADETBLUE"));
            }
            // URLs
            else if (element.startsWith("http://") || element.startsWith("https://")) {
                textElement.setText(element);
                textElement.setFill(Color.CORNFLOWERBLUE);
                textElement.setOnMouseEntered(event -> textElement.setStyle("-fx-text-fill: white; -fx-cursor: hand"));
                textElement.setOnMouseExited(event -> textElement.setStyle("-fx-text-fill: CORNFLOWERBLUE; -fx-cursor: inherit"));
                textElement.setOnMouseClicked(event -> NetworkUtils.openWebPage(element));
            }
            // Regular text
            else {
                textElement.setId("tootText");
                textElement.setText(element);
            }
            lista.add(textElement);
            // Add spacing between elements
            if (i != parsedTweet.size() - 1)
                lista.add(new Text(" "));
        }
        return lista;
    }

    /**
     * This method will handle
     * click event on "view media"
     * text. It will open a popup
     * with the corresponding video/image
     */
    @FXML
    void viewMedia() {
        String load = TxipironClient.s("Load");
        viewMediaTxt.setText(load);
        AsyncUtils.asyncTask(() -> {
            // create the popup
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/eus/ehu/txipironesmastodonfx/maincell/mediaViewer.fxml"),
                    ResourceBundle.getBundle("strings", TxipironClient.lang));
            Parent root = fxmlLoader.load();
            MediaViewController contr = fxmlLoader.getController();
            contr.setMedia(media);
            contr.setReference(master);
            Scene scene = new Scene(root);
            switch (txipi.currentstyle) {
                case "Dark" -> scene.getStylesheets().add(txipi.darkstyle);
                case "Light" -> scene.getStylesheets().add(txipi.lightstyle);
                case "Halloween" -> scene.getStylesheets().add(txipi.halloweenstyle);
                case "Summer" -> scene.getStylesheets().add(txipi.summerstyle);
            }
            return List.of(scene, contr);
        }, list -> {
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setScene((Scene) list.get(0));
            popupStage.setResizable(false);
            String popup = TxipironClient.s("MediaViewer");
            popupStage.setTitle(popup);
            popupStage.getIcons().add(new Image("file:src/main/resources/eus/ehu/txipironesmastodonfx/mainassets/dark-media-512.png"));
            ((MediaViewController) list.get(1)).setPopupStage(popupStage);
            String media = TxipironClient.s("View");
            viewMediaTxt.setText(media);
            popupStage.showAndWait();
        });
    }


    /**
     * This method will handle
     * the click event of the share button.
     * It will paste the uri of the toot
     * into the clipboard
     */
    @FXML
    void shareBtnClick() {
        // Get the system clipboard
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        // Create a string selection
        StringSelection selection = new StringSelection(uri);
        // Set the clipboard content with the string selection
        clipboard.setContents(selection, null);
        // Open the default browser with the toot's uri
        NetworkUtils.openWebPage(uri);
    }

    /**
     * On sensitive content toots,
     * when clicking on the image it will hide it
     * so the toot's contents can be seen
     */
    @FXML
    void sensitiveClose() {
        sensitiveImg.setVisible(false);
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
     * @param thisclass (AuthWindowController) - The reference to the main window controller
     */
    public void setReference(MainWindowController thisclass) {
        this.master = thisclass;
        this.txipi = thisclass.mainApp;
    }

    /**
     * This method controls the actions done after the favourite image is clicked
     * If the toot is not favourited, it will add it to the favourites
     * If the toot is already favourited, it will not do anything
     */
    @FXML
    void likedModified() {
        likes.setVisible(false);
        if(!fav){
            if(APIAccessManager.favouriteToot(Id, master.token)==200){
                AsyncUtils.asyncTask(() -> {
                    int j=-1;
                    for (int i=0; i<master.listViewItems.size(); i++){
                        if(master.listViewItems.get(i) instanceof Toot && ((Toot) master.listViewItems.get(i)).id.equals(Id)){
                            j=i;
                            break;
                        }
                    }
                    return j;
                }, pos -> {
                    if(pos!=-1){
                        ((Toot) master.listViewItems.get(pos)).favourited=true;
                        ((Toot) master.listViewItems.get(pos)).favourites_count++;
                    }
                    fav = true;
                    numLikes.setText(String.valueOf(Integer.parseInt(numLikes.getText()) + 1));
                    likes.setId("liked");
                    likes.setVisible(true);
                });
            }
        }
        else{
            if(APIAccessManager.unfavouriteToot(Id, master.token)==200){
                AsyncUtils.asyncTask(() -> {
                    int j=-1;
                    for (int i=0; i<master.listViewItems.size(); i++){
                        if(master.listViewItems.get(i) instanceof Toot && ((Toot) master.listViewItems.get(i)).id.equals(Id)){
                            j=i;
                            break;
                        }
                    }
                    return j;
                }, pos -> {
                    if(pos!=-1){
                        ((Toot) master.listViewItems.get(pos)).favourited=false;
                        ((Toot) master.listViewItems.get(pos)).favourites_count--;
                    }
                    fav = false;
                    numLikes.setText(String.valueOf(Integer.parseInt(numLikes.getText()) - 1));
                    likes.setId("unliked");
                    likes.setVisible(true);
                });
            }
        }
    }

    /**
     * This method controls the actions done after the reboot image is clicked
     * If the toot is not rebooted, it will add it to the reboots
     * If the toot is already rebooted, it will not do anything
     */
    @FXML
    void rebootModified(){
        reboot.setVisible(false);
        if(!reblog){
            if(APIAccessManager.reblogToot(Id, master.token)==200){
                AsyncUtils.asyncTask(() -> {
                    int j=-1;
                    for (int i=0; i<master.listViewItems.size(); i++){
                        if(master.listViewItems.get(i) instanceof Toot && ((Toot) master.listViewItems.get(i)).id.equals(Id)){
                            j=i;
                            break;
                        }
                    }
                    return j;
                }, pos -> {
                    if(pos!=-1){
                        ((Toot) master.listViewItems.get(pos)).reblogged=true;
                        ((Toot) master.listViewItems.get(pos)).reblogs_count++;
                    }
                    reblog = true;
                    numReboots.setText(String.valueOf(Integer.parseInt(numReboots.getText()) + 1));
                    reboot.setId("reboot");
                    reboot.setVisible(true);
                });
            }
        }
        else{
            if(APIAccessManager.unreblogToot(Id, master.token)==200){
                AsyncUtils.asyncTask(() -> {
                    int j=-1;
                    for (int i=0; i<master.listViewItems.size(); i++){
                        if(master.listViewItems.get(i) instanceof Toot && ((Toot) master.listViewItems.get(i)).id.equals(Id)){
                            j=i;
                            break;
                        }
                    }
                    return j;
                }, pos -> {
                    if(pos!=-1) {
                        ((Toot) master.listViewItems.get(pos)).reblogged=false;
                        ((Toot) master.listViewItems.get(pos)).reblogs_count--;
                    }
                    reblog = false;
                    numReboots.setText(String.valueOf(Integer.parseInt(numReboots.getText()) - 1));
                    reboot.setId("unReboot");
                    reboot.setVisible(true);
                });
            }
        }
    }

    /**
     * This method controls the actions done after the bookmark image is clicked
     * If the toot is not bookmarked, it will add it to the bookmarks
     * If the toot is already bookmarked, it will not do anything
     */
    @FXML
    void bookmarkModified(){
        bookmarks.setVisible(false);
        if(!bm){
            if(APIAccessManager.bookmarkToot(Id, master.token)==200){
                AsyncUtils.asyncTask(() -> {
                    int j=-1;
                    for (int i=0; i<master.listViewItems.size(); i++){
                        if(master.listViewItems.get(i) instanceof Toot && ((Toot) master.listViewItems.get(i)).id.equals(Id)){
                            j=i;
                            break;
                        }
                    }
                    return j;
                }, pos -> {
                    if(pos != -1) ((Toot) master.listViewItems.get(pos)).bookmarked=true;
                    bm = true;
                    bookmarks.setId("bookmarked");
                    bookmarks.setVisible(true);
                });
            }
        }
        else{
            if(APIAccessManager.unbookmarkToot(Id, master.token)==200){
                AsyncUtils.asyncTask(() -> {
                    int j=-1;
                    for (int i=0; i<master.listViewItems.size(); i++){
                        if(master.listViewItems.get(i) instanceof Toot && ((Toot) master.listViewItems.get(i)).id.equals(Id)){
                            j=i;
                            break;
                        }
                    }
                    return j;
                }, pos -> {
                    if(pos!=-1) ((Toot) master.listViewItems.get(pos)).bookmarked=false;
                    bm = false;
                    bookmarks.setId("unBookmarked");
                    bookmarks.setVisible(true);
                });
            }
        }
    }
}