package eus.ehu.txipironesmastodonfx.controllers.auth;

import eus.ehu.txipironesmastodonfx.data_access.DBAccessManager;
import eus.ehu.txipironesmastodonfx.domain.Account;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.sql.SQLException;

public class AuthAccoCellController {
    @FXML
    private ImageView avatarImg;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label accIdTxt;

    @FXML
    private Label userNameTxt;
    private AuthWindowController master;

    public AuthAccoCellController(Account account) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/eus/ehu/txipironesmastodonfx/auth/authaccocell.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // set the values for the account cell
        userNameTxt.setText(account.acct);
        accIdTxt.setText(account.id);
        avatarImg.setImage(new Image(account.avatar));
    }

    public AnchorPane getUI() {
        return anchorPane;
    }

    public void setReference(AuthWindowController thisclass) {
        this.master = thisclass;
    }

    @FXML
    void removeAccBtnClick() {
        // Remove account from database
        try {
            DBAccessManager.removeAccountFromDbId(accIdTxt.getText());
            // Refresh listview
            master.updateListView();
        } catch (SQLException e) {
            // if an error occurs, do nothing
        }
    }
}

