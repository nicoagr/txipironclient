package eus.ehu.txipironesmastodonfx.controllers.windowControllers;

import eus.ehu.txipironesmastodonfx.controllers.main.MainWindowController;
import eus.ehu.txipironesmastodonfx.data_access.APIAccessManager;
import eus.ehu.txipironesmastodonfx.data_access.AsyncUtils;
import eus.ehu.txipironesmastodonfx.data_access.HTMLParser;
import eus.ehu.txipironesmastodonfx.data_access.NetworkUtils;
import eus.ehu.txipironesmastodonfx.domain.TootToBePosted;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;

public class PostTootController {
    private MainWindowController master;
    @FXML
    private Label charLabel;
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private Label taggedAcctTxt;
    @FXML
    private AnchorPane anchor;
    @FXML
    private CheckBox sensitiveId;
    @FXML
    private TextArea content;

    /**
     * This method controls the actions
     * done after clicking on the "post toot" button.
     * It will check if the toot is empty or has more than 500 characters.
     * If it is not, it will post the toot to the servers.
     * If it is, it will show an error message.
     * It will also check if the user has internet connection.
     * If it does not, it will show an error message.
     */
    @FXML
    void PostAction() {
        master.listViewItems.clear();
        if (content.getText().isEmpty() || content.getText().length() > 500) {
            master.listViewItems.add("Error - Toots must not be empty and contain maximum 500 characters.");
            return;
        }
        master.listViewItems.add("Posting toot...");
        AsyncUtils.asyncTask(() -> {
            if (!NetworkUtils.hasInternet()) {
                return null;
            }
            TootToBePosted toot = new TootToBePosted(content.getText(), sensitiveId.isSelected());
            return APIAccessManager.postToot(master.token, toot);
        }, res -> {
            if (res != null)
                master.homeListView();//despues de postear el toot, se resetea y se muestra home
            else {
                master.listViewItems.clear();
                master.listViewItems.add("Error when posting toot to the servers. Please check connection and try again.");
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
     * Constructor for the controller.
     * It will load itself at take consciousness (set itself as controller)
     * Also it will set the corresponding values for the account cell.
     *
     * @param master (MainWindowController)- The controller of the main class, will be used for internal comunication
     */
    public PostTootController(MainWindowController master) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/eus/ehu/txipironesmastodonfx/maincell/postToot.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setReference(master);
    }

    /**
     * Setter for the reference to the auth window controller
     *
     * @param master (AuthWindowController) - The reference to the auth window controller
     */
    public void setReference(MainWindowController master) {
        this.master = master;
    }

    /**
     * This method will be called by the FXMLLoader when initialization is complete
     * It will set the listeners for the text area and the character counter
     * It will also set the listener for the list view in order to select the cell
     * when the user clicks on it.
     */
    @FXML
    public void initialize() {
        content.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 501) {
                content.setText(oldValue);
                charLabel.setText("500 Character Limit Reached");
                return;
            }
            // Select cell inside list view (in case its not selected)
            master.listView.getSelectionModel().select(anchor);
            master.listView.getFocusModel().focus(master.listView.getSelectionModel().getSelectedIndex());
            content.setText(newValue);
            // set character number
            charLabel.setText(newValue.length() + "/500 Characters");
            // set tagged accounts
            AsyncUtils.asyncTask(() -> {
                // detect for usernames
                HashSet<String> names = new HashSet<>();
                int charcount = 0;
                Matcher matcher = HTMLParser.USERNAME_PATTERN.matcher(newValue);
                while (matcher.find()) {
                        String username = matcher.group().substring(1);
                        charcount += username.length() + 1;
                        names.add("@" + username);
                    }
                    if (charcount > 60) return List.of("Various [...]");
                    return names;
                }, names -> taggedAcctTxt.setText(names.size() == 0 ? "None" : String.join(", ", names)));
            });
            content.setWrapText(true);
        }
    }
