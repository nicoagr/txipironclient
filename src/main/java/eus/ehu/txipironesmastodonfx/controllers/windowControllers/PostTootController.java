package eus.ehu.txipironesmastodonfx.controllers.windowControllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import eus.ehu.txipironesmastodonfx.controllers.main.MainWindowController;
import eus.ehu.txipironesmastodonfx.data_access.APIAccessManager;
import eus.ehu.txipironesmastodonfx.data_access.AsyncUtils;
import eus.ehu.txipironesmastodonfx.domain.TootToBePosted;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

    public class PostTootController {
        private MainWindowController master;

        @FXML
        private ResourceBundle resources;

        @FXML
        private URL location;

        @FXML
        private AnchorPane anchor;

        @FXML
        private TextField content;

        @FXML
        void PostAction() {
            master.listViewItems.clear();
            master.listViewItems.add("Posting toot...");
            AsyncUtils.asyncTask(() -> {
                TootToBePosted toot = new TootToBePosted(content.getText());
                APIAccessManager.postToot(master.token,toot);
                return null;
            }, param -> {
                master.homeListView();//despues de postear el toot, se resetea y se muestra home
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
        void initialize() {
            assert anchor != null : "fx:id=\"anchor\" was not injected: check your FXML file 'postToot.fxml'.";
            assert content != null : "fx:id=\"content\" was not injected: check your FXML file 'postToot.fxml'.";

        }

    }
