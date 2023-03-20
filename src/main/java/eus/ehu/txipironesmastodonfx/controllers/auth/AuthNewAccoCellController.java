package eus.ehu.txipironesmastodonfx.controllers.auth;

import eus.ehu.txipironesmastodonfx.data_access.APIAccessManager;
import eus.ehu.txipironesmastodonfx.data_access.DBAccessManager;
import eus.ehu.txipironesmastodonfx.data_access.NetworkUtils;
import eus.ehu.txipironesmastodonfx.data_access.SysUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.sql.SQLException;

public class AuthNewAccoCellController {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TextField mstdTokenTxt;
    @FXML
    private Button addAccBtn;
    @FXML
    private Label errorTxt;
    private AuthWindowController master;

    @FXML
    void addAccBtnClick() {
        addAccBtn.setDisable(true);
        mstdTokenTxt.setDisable(true);
        String token = mstdTokenTxt.getText();
        mstdTokenTxt.setText("Loading");
        // Check for internet connection
        if (!NetworkUtils.hasInternet()) {
            errorTxt.setText("Error! No internet connection / Mastodon API Unreachable");
            addAccBtn.setDisable(false);
            return;
        }
        // verify token present
        if (mstdTokenTxt.getText().isEmpty()) {
            errorTxt.setText("Error! Token is empty");
            addAccBtn.setDisable(false);
            return;
        }
        // check if token is valid
        String id = APIAccessManager.verifyAndGetId(token);
        if (id == null) {
            errorTxt.setText("Error! Token is invalid");
            addAccBtn.setDisable(false);
            return;
        }
        // check if account is not in database
        try {
            if (DBAccessManager.isAccountInDb(id)) {
                // If the account is in the database, stop the process
                errorTxt.setText("Error! Account already in database");
                addAccBtn.setDisable(false);
                return;
            }
        } catch (SQLException e) {
            errorTxt.setText("Error when checking if account is in database");
            addAccBtn.setDisable(false);
            return;
        }
        // add account to database
        try {
            String destinationsysvar = SysUtils.getNextFreeSysVar("TXIPIRON_MSTD_TK_");
            DBAccessManager.addAccount(destinationsysvar, id, token);
        } catch (UnsupportedOperationException e) {
            errorTxt.setText("Error! Unsupported operating system (System Variables)");
            addAccBtn.setDisable(false);
            return;
        } catch (SQLException e) {
            errorTxt.setText("Error when adding account to database");
            addAccBtn.setDisable(false);
            return;
        } catch (IOException e) {
            errorTxt.setText("Error when setting system variable");
            addAccBtn.setDisable(false);
            return;
        }
        // refresh listview
        master.updateListView();
        addAccBtn.setDisable(false);
    }

    public AuthNewAccoCellController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/eus/ehu/txipironesmastodonfx/auth/authnewcell.fxml"));
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public AnchorPane getUI() {
        return anchorPane;
    }

    public void setReference(AuthWindowController thisclass) {
        this.master = thisclass;
    }
}
