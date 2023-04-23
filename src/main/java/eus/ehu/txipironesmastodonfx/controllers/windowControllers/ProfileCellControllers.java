package eus.ehu.txipironesmastodonfx.controllers.windowControllers;
import eus.ehu.txipironesmastodonfx.controllers.main.MainWindowController;
import eus.ehu.txipironesmastodonfx.data_access.AsyncUtils;
import eus.ehu.txipironesmastodonfx.data_access.HTMLParser;
import eus.ehu.txipironesmastodonfx.data_access.NetworkUtils;
import eus.ehu.txipironesmastodonfx.domain.Account;

import eus.ehu.txipironesmastodonfx.domain.Toot;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ProfileCellControllers {


        private MainWindowController master;

        @FXML
        private ResourceBundle resources;

        @FXML
        private URL location;

        @FXML
        private ImageView bannerPic;

        @FXML
        private AnchorPane anchor;

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




        /**
         * Constructor for the profile cell controller
         *
         * @param master (MainWindowController) - The reference to the main window controller
         */
        public ProfileCellControllers( MainWindowController master) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/eus/ehu/txipironesmastodonfx/maincell/profileCell.fxml"));
            fxmlLoader.setController(this);
            try {
                fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            setReference(master);
        }


        public void loadAccount(Account account){

            bannerPic.setPreserveRatio(false);
            bannerPic.setSmooth(true);
            bannerPic.setCache(true);

// Bind the fitWidth and fitHeight properties to the width and height of the bannerPic



            this.username.setText(account.id);
            this.name.setText(account.acct);
            this.numPost.setText(String.valueOf(account.statuses_count));
            this.numFollowers.setText(String.valueOf(account.followers_count));
            this.numFollowing.setText(String.valueOf(account.following_count));
            List<Text> texts = new ArrayList<>();
            for(String string: HTMLParser.parseHTML(account.note)){
                Text text = new Text(string);
                texts.add(text);
                texts.add(new Text(" "));

            }
            this.description.getChildren().addAll(texts);

            profilePic.setImage(new Image(getClass().getResourceAsStream("/eus/ehu/txipironesmastodonfx/mainassets/dark-accounticon.png")));

            AsyncUtils.asyncTask(() ->
                    {
                        Image img = null;
                        if (NetworkUtils.hasInternet())
                            img = new Image(account.avatar);
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
        }

        @FXML
        void initialize() {


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
