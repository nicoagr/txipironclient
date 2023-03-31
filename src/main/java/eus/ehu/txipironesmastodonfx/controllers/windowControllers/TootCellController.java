package eus.ehu.txipironesmastodonfx.controllers.windowControllers;


import eus.ehu.txipironesmastodonfx.controllers.main.MainWindowController;
import eus.ehu.txipironesmastodonfx.data_access.AsyncUtils;
import eus.ehu.txipironesmastodonfx.data_access.NetworkUtils;
import eus.ehu.txipironesmastodonfx.domain.Toot;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLAnchorElement;

import java.io.IOException;
import java.net.URL;
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
    @FXML
    private URL location;
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
    private WebView tootWebView;
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
        username.setText(toot.account.acct);
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
        date.setText((toot.created_at));
        numLikes.setText(Integer.toString(toot.favourites_count));
        numReboots.setText(Integer.toString(toot.reblogs_count));
        numComments.setText(Integer.toString(toot.replies_count));

        tootWebView.getEngine().loadContent(toot.content);
    }

    /**
     * Initializes the controller class.
     *
     */
    @FXML
    void initialize() {
        // Adds a click event listener to all <a> elements in the WebView.
        // When an <a> element is clicked, the listener gets the URL from the element's href attribute.
        // The URL is then opened in the default system browser using the HostServices class.
        WebEngine webEngine = tootWebView.getEngine();
        webEngine.getLoadWorker().stateProperty().addListener((ObservableValue<? extends Worker.State> ov, Worker.State oldState, Worker.State newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                Document doc = webEngine.getDocument();
                NodeList nodeList = doc.getElementsByTagName("a");
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    EventTarget eventTarget = (EventTarget) node;
                    eventTarget.addEventListener("click", evt -> {
                        evt.preventDefault();
                        Node targetNode = (Node) evt.getTarget();
                        while (targetNode != null && !(targetNode instanceof HTMLAnchorElement)) {
                            targetNode = targetNode.getParentNode();
                        }
                        if (targetNode != null) {
                            String url = ((HTMLAnchorElement) targetNode).getHref();
                            master.TxipironClient().getHostServices().showDocument(url);
                        }
                    }, true);
                }
            }
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
