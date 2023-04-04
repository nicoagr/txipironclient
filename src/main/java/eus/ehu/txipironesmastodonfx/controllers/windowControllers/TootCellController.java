package eus.ehu.txipironesmastodonfx.controllers.windowControllers;


import eus.ehu.txipironesmastodonfx.controllers.main.MainWindowController;
import eus.ehu.txipironesmastodonfx.data_access.AsyncUtils;
import eus.ehu.txipironesmastodonfx.data_access.NetworkUtils;
import eus.ehu.txipironesmastodonfx.domain.Toot;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeVisitor;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

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

        String html = toot.content;
        Document doc = Jsoup.parse(html);
        Element body = doc.body();

        body.traverse(new NodeVisitor() {
            @Override
            public void head(Node node, int depth) {
                if (node instanceof TextNode) {
                    String text = ((TextNode) node).text();
                    Text textNode = new Text(text);
                    textFlow.getChildren().add(textNode);
                } else if (node instanceof Element) {
                    Element element = (Element) node;
                    if (element.tagName().equals("br")) {
                        textFlow.getChildren().add(new Text("\n"));
                    } else if (element.tagName().equals("a")) {
                        // Create a Hyperlink object for the link and add it to the TextFlow
                        String link = element.attr("href");
                        String text = element.text();
                        Hyperlink hyperlink = new Hyperlink(text);
                        hyperlink.setOnAction(e -> {
                            try {
                                Desktop.getDesktop().browse(new URI(link));
                            } catch (IOException | URISyntaxException ex) {
                                ex.printStackTrace();
                            }
                        });
                        textFlow.getChildren().add(hyperlink);
                    }
                }
            }

            @Override
            public void tail(Node node, int depth) {
                // Do nothing
            }
        });
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
