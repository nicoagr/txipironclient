package eus.ehu.txipironesmastodonfx.controllers.windowControllers;



import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

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
    void initialize() {
        assert Id != null : "fx:id=\"Id\" was not injected: check your FXML file 'tootcell.fxml'.";
        assert date != null : "fx:id=\"date\" was not injected: check your FXML file 'tootcell.fxml'.";
        assert numComments != null : "fx:id=\"numComments\" was not injected: check your FXML file 'tootcell.fxml'.";
        assert numLikes != null : "fx:id=\"numLikes\" was not injected: check your FXML file 'tootcell.fxml'.";
        assert numReboots != null : "fx:id=\"numReboots\" was not injected: check your FXML file 'tootcell.fxml'.";
        assert tootinfo != null : "fx:id=\"tootinfo\" was not injected: check your FXML file 'tootcell.fxml'.";
        assert username != null : "fx:id=\"username\" was not injected: check your FXML file 'tootcell.fxml'.";

    }

}
