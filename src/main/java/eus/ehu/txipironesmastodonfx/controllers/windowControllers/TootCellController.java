package eus.ehu.txipironesmastodonfx.controllers.windowControllers;



import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import eus.ehu.txipironesmastodonfx.controllers.main.MainWindowController;
import eus.ehu.txipironesmastodonfx.domain.Account;
import eus.ehu.txipironesmastodonfx.domain.Toot;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

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
    private Label tootinfo;

    @FXML
    private Label username;


    @FXML
    private ImageView imagen;
    private MainWindowController master;


    public TootCellController(Toot toot) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/eus/ehu/txipironesmastodonfx/tootcell.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // set the values for the account cell
            this.Id.setText(toot.account.id);
            this.username.setText(toot.account.acct);
            this.imagen.setImage(new Image(toot.account.avatar));
            this.date.setText((toot.created_at));
            this.tootinfo.setText(toot.content);
            this.numLikes.setText(Integer.toString(toot.favourites_count));
            this.numReboots.setText(Integer.toString(toot.reblogs_count));
            this.numComments.setText(Integer.toString(toot.replies_count));




    }

    public AnchorPane getUI() {
        return anchor;
    }
    public void setReference(MainWindowController thisclass) {
        this.master = thisclass;
    }
    
    
}
