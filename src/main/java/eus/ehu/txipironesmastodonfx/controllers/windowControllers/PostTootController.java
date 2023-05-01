package eus.ehu.txipironesmastodonfx.controllers.windowControllers;

import eus.ehu.txipironesmastodonfx.controllers.main.MainWindowController;
import eus.ehu.txipironesmastodonfx.data_access.APIAccessManager;
import eus.ehu.txipironesmastodonfx.data_access.AsyncUtils;
import eus.ehu.txipironesmastodonfx.data_access.HTMLParser;
import eus.ehu.txipironesmastodonfx.data_access.NetworkUtils;
import eus.ehu.txipironesmastodonfx.domain.MediaAttachment;
import eus.ehu.txipironesmastodonfx.domain.TootToBePosted;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import jfxtras.scene.control.LocalDateTimeTextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;

/**
 * Controller class for posting a toot.
 * It will be used to allow user post a toot the way he/she prefers with a limit amount of characters.
 *
 * @author Nicolás Aguado
 * @author Haizea Bermejo
 * @author Xiomara Cáceces
 * @author Marcos Chouciño
 */
public class PostTootController {
    private MainWindowController master;
    private static final Logger logger = LogManager.getLogger("PostTootController");
    private List<File> paths;
    LocalDateTimeTextField ldtf;
    @FXML
    private Button pickFileBtn;
    @FXML
    private Label charLabel;
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private Label selectTxt;
    @FXML
    private Label taggedAcctTxt;
    @FXML
    private AnchorPane anchor;
    @FXML
    private CheckBox sensitiveId;
    @FXML
    private TextArea content;
    @FXML
    private ComboBox<String> comboBox;


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
        LocalDateTime postDate = ldtf.getLocalDateTime();
        if (postDate != null && postDate.isBefore(LocalDateTime.now().plusMinutes(6))) {
            // One minute for us to post media, and 5 minutes server requirement
            master.listViewItems.add("Post date must be 6 minutes or more in the future!");
            logger.warn("Post date must be 6 minutes or more in the future! - Cancelling posting toot.");
            return;
        }
        master.listViewItems.clear();
        if (content.getText().isEmpty() || content.getText().length() > 500) {
            master.listViewItems.add("Error - Toots must not be empty and contain maximum 500 characters.");
            logger.warn("Toot is empty or has more than 500 characters. - Cancelling posting toot.");
            return;
        }
        master.listViewItems.add("Processing...");
        logger.info("Attempting to post toot...");
        master.showLoading();
        AsyncUtils.asyncTask(() -> {
            if (!NetworkUtils.hasInternet()) {
                logger.error("No internet connection. - Cancelling posting toot.");
                return null;
            }
            List<String> mediaIds = null;
            if (paths != null && paths.size() != 0) {
                // Upload media
                mediaIds = new ArrayList<>();
                MediaAttachment res;
                Platform.runLater(() -> master.listViewItems.add("Uploading media..."));
                for (File path : paths) {
                    logger.debug("Uploading media: " + path.getAbsolutePath());
                    res = APIAccessManager.uploadMedia(master.token, path);
                    mediaIds.add(res.id);
                }
                boolean processed = false;
                int time = 0;
                // Check if media was processed correctly
                Platform.runLater(() -> master.listViewItems.add("Waiting for server response..."));
                logger.debug("Waiting for media processing done by server...");
                while (!processed) {
                    for (String id : mediaIds) {
                        processed = processed || APIAccessManager.isMediaProcessed(master.token, id);
                    }
                    Thread.sleep(1000); // wait for 1 seconds
                    time++;
                    // wait 30 seconds before timeout
                    if (time > 31) {
                        return null;
                    }
                }
            }
            // format datetime (add timezone, convert it to utc and then into an ISO string)
            String isoDate = null;
            if (postDate != null) {
                ZonedDateTime zdt = ZonedDateTime.of(postDate, ZoneId.systemDefault());
                ZonedDateTime utcZdt = zdt.withZoneSameInstant(ZoneOffset.UTC);
                isoDate = utcZdt.format(DateTimeFormatter.ISO_DATE_TIME);
                logger.debug("UTC Scheduled toot post date: " + isoDate);
            }
            // Create toot to be posted object and post it
            TootToBePosted toot = new TootToBePosted(content.getText(), sensitiveId.isSelected(), mediaIds, isoDate);
            Platform.runLater(() -> master.listViewItems.add("Posting toot..."));
            logger.debug("Sending toot to mastodon's servers...");
            return APIAccessManager.postToot(master.token, toot);
        }, res -> {
            if (res != null) {
                logger.info("Toot successfully posted!");
                master.homeListView();//despues de postear el toot, se resetea y se muestra home
            } else {
                master.listViewItems.clear();
                master.listViewItems.add("Error when posting toot to the servers. Please check connection and try again.");
            }
            master.hideLoading();
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
     * @param master (MainWindowController) - The reference to the main window controller
     */
    public void setReference(MainWindowController master) {
        this.master = master;
    }

    /**
     * This method will handle the click action on
     * the "pick file" button.
     */
    @FXML
    void pickFileAction() {
        if (paths != null && paths.size() > 0) {
            paths.clear();
            pickFileBtn.setText("Media Attachment (Optional)");
            selectTxt.setText("");
            logger.info("Cleared file selection.");
            return;
        }
        int MAX_IMAGES = 4;
        // Create a file chooser dialog
        FileChooser fileChooser = new FileChooser();

        // Set the title of the dialog
        fileChooser.setTitle("Txipiron Client [v1.0] - a Mastodon Client - Multiple File Chooser");

        // Set the file extension filters
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images/Videos", "*.png", "*.jpg", "*.jpeg", "*.mp4", "*.mov", "*.webm", "*.m4v")
        );
        logger.info("Opening file chooser...");
        // Allow the user to select multiple files
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(master.TxipironClient().stage);

        if (selectedFiles != null && selectedFiles.size() > 0) {
            if (paths == null) {
                paths = new ArrayList<>();
            }
            paths.clear();
            if (HTMLParser.getFileExtension(selectedFiles.get(0)).matches("mp4|mov|webm|m4v")) {
                File f = selectedFiles.get(0);
                if (f.length() < 41943040) { // 40 MB
                    paths.add(f);
                    selectTxt.setText("Video:" + f.getName());
                } else {
                    logger.warn("Selected Video too large. Max size: 40 MB");
                    selectTxt.setText("Selected Video too large. Max size: 40 MB");
                }
            } else {
                for (File f : selectedFiles) {
                    if (HTMLParser.getFileExtension(f).matches("png|jpg|jpeg") && paths.size() < MAX_IMAGES && f.length() < 8388608) {
                        selectTxt.setText((paths.size() == 0) ? "Image: " + f.getName() : selectTxt.getText() + "\n" + f.getName());
                        logger.info("Selected image: " + f.getAbsolutePath());
                        paths.add(f);
                    } else
                        logger.warn("Selected image too large (>8MB) or image limit reached (4 images)");
                }
                if (paths.size() == 0) {
                    selectTxt.setText("No matching files selected");
                    logger.warn("No matching files selected");
                } else {
                    pickFileBtn.setText("Clear Selection");
                }
            }
        }
    }

    /**
     * This method will be called by the FXMLLoader when initialization is complete
     * It will set the listeners for the text area and the character counter
     * It will also set the listener for the list view in order to select the cell
     * when the user clicks on it.
     */
    @FXML
    public void initialize() {
        logger.debug("Loading post toot cell initial tasks...");
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
        // add toot schedule to anchor-pane
        ldtf = new LocalDateTimeTextField();
        List<LocalDateTime> d = ldtf.disabledLocalDateTimes();
        LocalDateTime now = LocalDateTime.now();
        // Set the past 5 years as disabled dates
        // Done for more user-friendliness. Actual check
        // will be done in next method
        for (int i = 1; i <= 365 * 5; i++) {
            LocalDateTime pastDateTime = now.minusDays(i);
            d.add(pastDateTime);
        }
        // actual check and protection
        ldtf.localDateTimeProperty().addListener((obs, oldVal, newVal) -> {
            // One minute for us to post media, and 5 minutes server requirement
            if (newVal != null && newVal.isBefore(LocalDateTime.now().plusMinutes(6))) {
                ldtf.setLocalDateTime(null);
                return;
            }
        });
        ldtf.setPrefWidth(226);
        ldtf.setPrefHeight(26);
        ldtf.setPromptText("Schedule toot (Optional)");
        anchor.getChildren().add(ldtf);
        anchor.setTopAnchor(ldtf, 271.0);
        anchor.setLeftAnchor(ldtf, 300.0);
        logger.debug("Post toot cell initial tasks loaded");
    }
}