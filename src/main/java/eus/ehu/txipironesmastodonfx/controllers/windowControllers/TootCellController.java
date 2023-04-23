package eus.ehu.txipironesmastodonfx.controllers.windowControllers;

import eus.ehu.txipironesmastodonfx.controllers.main.MainWindowController;
import eus.ehu.txipironesmastodonfx.data_access.*;
import eus.ehu.txipironesmastodonfx.data_access.AsyncUtils;
import eus.ehu.txipironesmastodonfx.data_access.HTMLParser;
import eus.ehu.txipironesmastodonfx.data_access.NetworkUtils;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
    private Label numReboots;
    @FXML
    private TextFlow textFlow;
    @FXML
    private Label username;
    @FXML
    private ImageView imagen;
    private MainWindowController master;

    /**
     * Constructor for the controller.
     * It will load itself at take consciousness (set itself as controller)
     * Also it will set the corresponding values for the account cell.
     *
     * @param master (MainWindowController)- The controller of the main class, will be used for internal comunication
     */
    public TootCellController(MainWindowController master) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/eus/ehu/txipironesmastodonfx/maincell/tootcell.fxml"));
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
     *
     *
     */
    @FXML
    void usernameClicked() {
    master.userTootListView(username.getText());
    }
    @FXML
    void profilePIctureClicked() {
        System.out.println("the username is clicked");
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
            rebootedText.setText("Rebooted by @" + toot.account.acct);
        }
        while (toot.reblog != null) toot = toot.reblog;
        // set the values for the toot cell
        Id = toot.id;
        username.setText("@" + toot.account.acct);
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
        AsyncUtils.asyncTask(() -> formatDate(finalToot.created_at), param -> date.setText(param));
        if (toot.favourited){
            likes.setImage(new Image(getClass().getResourceAsStream("/eus/ehu/txipironesmastodonfx/mainassets/black-heart_160.png")));
            likes.setDisable(true);
        }
        else
            likes.setImage(new Image(getClass().getResourceAsStream("/eus/ehu/txipironesmastodonfx/mainassets/grey-heart.png")));
        numLikes.setText(Integer.toString(finalToot.favourites_count));
        if (finalToot.sensitive)
            sensitiveImg.setVisible(true);
        numReboots.setText(Integer.toString(finalToot.reblogs_count));
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

            // User tag
            if (element.startsWith("@")) {
                textElement.setText(element);
                textElement.setFill(Color.BLUE);
                textElement.setOnMouseEntered(event -> textElement.setStyle("-fx-text-fill: white; -fx-cursor: hand"));
                textElement.setOnMouseExited(event -> textElement.setStyle("-fx-text-fill: blue; -fx-cursor: inherit"));
                textElement.setOnMouseClicked(event -> AsyncUtils.asyncTask(() -> {


                    return null;
                }, param -> {
                    System.out.println(element);
                    master.userTootListView(element);
                }));
            }
            // HashTags
//            else if (element.startsWith("#")) {
//                textElement.setText(element);
//                textElement.setFill(Color.BLUE);
//                textElement.setOnMouseEntered(event -> textElement.setStyle("-fx-text-fill: white; -fx-cursor: hand"));
//                textElement.setOnMouseExited(event -> textElement.setStyle("-fx-text-fill: blue; -fx-cursor: inherit"));
//            }
            // URLs
            else if (element.startsWith("http://") || element.startsWith("https://")) {
                textElement.setText(element);
                textElement.setFill(Color.BLUE);
                textElement.setOnMouseEntered(event -> textElement.setStyle("-fx-text-fill: white; -fx-cursor: hand"));
                textElement.setOnMouseExited(event -> textElement.setStyle("-fx-text-fill: blue; -fx-cursor: inherit"));
                textElement.setOnMouseClicked(event -> NetworkUtils.openWebPage(element));
            }
            // Regular text
            else {
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
     * Method to format a given date into our preferred format
     * We will transform yyyy-MM-dd'T'HH:mm:ss.SSS'Z' into dd/MM/yyyy HH:mm
     *
     * @param createdAt (String) - The date to be formatted
     * @return (String) - The formatted date
     */
    private String formatDate(String createdAt) {
        // Set input formatter
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .withZone(ZoneId.of("UTC"));
        // Get the current date in UTC+2 (Spain)
        ZoneId spainZone = ZoneId.of("UTC+2");
        LocalDate currentDate = LocalDate.now(spainZone);
        // Set output formatter
        DateTimeFormatter hourOutputFormatter = DateTimeFormatter.ofPattern("HH:mm")
                .withZone(spainZone);
        DateTimeFormatter dateOutputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                .withZone(spainZone);
        // Parse the input date with the input formatter
        ZonedDateTime zdt = ZonedDateTime.parse(createdAt, inputFormatter);
        // Convert the input date to UTC+2
        ZonedDateTime zdtSpain = zdt.withZoneSameInstant(spainZone);
        // Check if the date is today
        LocalDate date = zdtSpain.toLocalDate();
        String outputDate;
        // if date is today, set today instead of dd/MM/yyyy
        if (date.equals(currentDate)) {
            outputDate = "Today";
        } else {
            outputDate = date.format(dateOutputFormatter);
        }
        // return the formatted the output date
        return outputDate + " " + zdtSpain.format(hourOutputFormatter);
    }

    /**
     * This method will handle
     * click event on "view media"
     * text. It will open a popup
     * with the corresponding video/image
     */
    @FXML
    void viewMedia() {
        viewMediaTxt.setText("Loading...");
        AsyncUtils.asyncTask(() -> {
            // create the popup
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/eus/ehu/txipironesmastodonfx/maincell/mediaViewer.fxml"));
            Parent root = fxmlLoader.load();
            MediaViewController contr = fxmlLoader.getController();
            contr.setMedia(media);
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
            viewMediaTxt.setText("View attached media");
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
        // Change image to a checkbox
        shareImg.setImage(new Image(getClass().getResourceAsStream("/eus/ehu/txipironesmastodonfx/mainassets/dark-ok.png")));
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
     * @param thisclass (AuthWindowController) - The reference to the auth window controller
     */
    public void setReference(MainWindowController thisclass) {
        this.master = thisclass;
    }

    /**
     * This method controls the actions done after the favourite image is clicked
     * If the toot is not favourited, it will add it to the favourites
     * If the toot is already favourited, it will not do anything
     */
    @FXML
    void likedModified() {
        if(APIAccessManager.addFavouriteToot(Id, master.token)==200){
            int j=-1;
            for (int i=0; i<master.listViewItems.size(); i++){
                if(master.listViewItems.get(i) instanceof Toot && ((Toot) master.listViewItems.get(i)).id.equals(Id)){
                    j=i;
                    break;
                }
            }
            ((Toot) master.listViewItems.get(j)).favourited=true;
            ((Toot) master.listViewItems.get(j)).favourites_count++;
            numLikes.setText(String.valueOf(Integer.parseInt(numLikes.getText()) + 1));
            likes.setImage(new Image(getClass().getResourceAsStream("/eus/ehu/txipironesmastodonfx/mainassets/black-heart_160.png")));
            likes.setDisable(true);
        }
    }
}
