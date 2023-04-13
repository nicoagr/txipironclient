package eus.ehu.txipironesmastodonfx.controllers.windowControllers;

import eus.ehu.txipironesmastodonfx.controllers.main.MainWindowController;
import eus.ehu.txipironesmastodonfx.data_access.APIAccessManager;
import eus.ehu.txipironesmastodonfx.data_access.AsyncUtils;
import eus.ehu.txipironesmastodonfx.data_access.NetworkUtils;
import eus.ehu.txipironesmastodonfx.domain.TootToBePosted;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

    public class PostTootController {
        private MainWindowController master;

        @FXML
        private Label charLabel;
        @FXML
        private ResourceBundle resources;

        @FXML
        private URL location;

        @FXML
        private AnchorPane anchor;

        @FXML
        private TextArea content;

        @FXML
        void PostAction() {
            master.listViewItems.clear();
            if (content.getText().length() > 500) {
                master.listViewItems.add("Error - Toots must contain maximum 500 characters.");
                master.listViewItems.add("Post Toot");
                return;
            }
            master.listViewItems.add("Posting toot...");
            AsyncUtils.asyncTask(() -> {
                if (!NetworkUtils.hasInternet()) {
                    return null;
                }
                TootToBePosted toot = new TootToBePosted(content.getText());
                String res = APIAccessManager.postToot(master.token, toot);
                return res;
            }, res -> {
                if (res != null)
                    master.homeListView();//despues de postear el toot, se resetea y se muestra home
                else {
                    master.listViewItems.add("Error when posting toot to the servers. Please check connection and try again.");
                    master.listViewItems.add("Post Toot");
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

        @FXML
        public void initialize() {
            content.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.length() > 501) {
                    content.setText(oldValue);
                    charLabel.setText("500 Character Limit Reached");
                }
                // set character number
                else charLabel.setText(newValue.length() + "/500 Characters");
            });
            content.setWrapText(true);
        }
    }
