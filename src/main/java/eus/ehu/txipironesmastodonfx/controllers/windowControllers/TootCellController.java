package eus.ehu.txipironesmastodonfx.controllers.windowControllers;


import eus.ehu.txipironesmastodonfx.controllers.main.MainWindowController;
import eus.ehu.txipironesmastodonfx.data_access.AsyncUtils;
import eus.ehu.txipironesmastodonfx.data_access.NetworkUtils;
import eus.ehu.txipironesmastodonfx.data_access.Regex;
import eus.ehu.txipironesmastodonfx.domain.Toot;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

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
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * Controller for the TootCell
 * This class will be used to display the toots in the main window
 * It will be used by the MainWindowController
 *
 * @author Nicolás Aguado
 * @author Haizea Bermejo
 * @author Marcos Chouciño
 * @author Xiomara Cáceces
 */
public class TootCellController   {
    @FXML
    private ResourceBundle resources;
    private String uri;
    @FXML
    private ImageView shareImg;
    @FXML
    private Label Id;
    @FXML
    private Label date;
    @FXML
    private AnchorPane anchor;
    @FXML
    private Label numComments;
    @FXML
    private Label numLikes;
    @FXML
    private Label numReboots;
    private static final Pattern POST_LINE_BREAKS = Pattern.compile("\n+");
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
     * Method to set the values for the toot cell
     *
     * @param toot (Toot) - The toot to be displayed
     */
    public void loadToot(Toot toot) {
        // set the values for the toot cell
        Id.setText(toot.account.id);
        username.setText("@" + toot.account.acct);
        imagen.setImage(new Image(getClass().getResourceAsStream("/eus/ehu/txipironesmastodonfx/mainassets/dark-accounticon.png")));
        AsyncUtils.asyncTask(() ->
                {
                    Image img = null;
                    if (NetworkUtils.hasInternet())
                        img = new Image(toot.account.avatar);
                    return img;
                }, param -> {
                    if (param != null) imagen.setImage(param);
                    else
                        imagen.setImage(new Image(getClass().getResourceAsStream("/eus/ehu/txipironesmastodonfx/mainassets/dark-notfound.jpg")));

                }
        );
        AsyncUtils.asyncTask(() -> formatDate(toot.created_at), param -> date.setText(param));
        date.setText(formatDate(toot.created_at));
        numLikes.setText(Integer.toString(toot.favourites_count));
        numReboots.setText(Integer.toString(toot.reblogs_count));
        numComments.setText(Integer.toString(toot.replies_count));
        uri = toot.uri;
        AsyncUtils.asyncTask(() -> updateContent(toot.content), param -> {
            for (Text t : param)
                textFlow.getChildren().add(t);
        });
    }

    private List<Text> updateContent(String text) {
        List<Text> lista = new ArrayList<>();
        List<String> parsedTweet = Regex.parseTweet(text);
        for (String element : parsedTweet) {
            if (element == null || element.equals("") || element.isEmpty())
                continue;

            Text textElement = new Text();

            // User tag
            if (element.startsWith("@")) {
                textElement.setText(element);
                textElement.setFill(Color.LIGHTBLUE);
                textElement.setOnMouseEntered(event -> textElement.setStyle("-fx-text-fill: darkblue; -fx-cursor: hand"));
                textElement.setOnMouseExited(event -> textElement.setStyle("-fx-text-fill: lightblue; -fx-cursor: inherit"));
                textElement.setOnMouseClicked(event -> AsyncUtils.asyncTask(() -> {
                    // TODO HANDLE CLICK ON USERNAME
                    return null;
                }, param -> {

                }));
            }
            // HashTags
            else if (element.startsWith("#")) {
                textElement.setText(element);
                textElement.setFill(Color.GRAY);
                textElement.setOnMouseEntered(event -> textElement.setStyle("-fx-text-fill: lightgray; -fx-cursor: hand"));
                textElement.setOnMouseExited(event -> textElement.setStyle("-fx-text-fill: gray; -fx-cursor: inherit"));
            }
            // URLs
            else {
                if (element.startsWith("http://") || element.startsWith("https://")) {
                    textElement.setText(element);
                    textElement.setFill(Color.LIGHTBLUE);
                    textElement.setOnMouseEntered(event -> textElement.setStyle("-fx-text-fill: darkgreen; -fx-cursor: hand"));
                    textElement.setOnMouseExited(event -> textElement.setStyle("-fx-text-fill: lightgreen; -fx-cursor: inherit"));
                    textElement.setOnMouseClicked(event -> NetworkUtils.openWebPage(element));
                }
                // Regular text
                else if (element.startsWith("<p>")) {
                    String finaltxt = element.replace("<p>", "");
                    finaltxt = finaltxt.replace("</p>", "");
                    textElement.setText(finaltxt);
                }
            }

            lista.add(textElement);
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
