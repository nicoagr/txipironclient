package eus.ehu.txipironesmastodonfx;

import eus.ehu.txipironesmastodonfx.controllers.WindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * Main class of the application. This will be the starting point.
 * It will call the FXML files and load the controllers.
 * Also, starts by default with the "Account Manager" scene.
 * It will have the "Window" class to contain all JavaFX windows we create.
 *
 * @author Nicolás Aguado
 * @author Haizea Bermejo
 * @author Xiomara Cáceces
 * @author Marcos Chouciño
 * @version 1.0
 */
public class TxipironClient extends Application {

    private Window authWindow;
    private Window mainWindow;

    /**
     * An abstracted class that will contain the UI and the controller of a window
     */
    static class Window {
        Parent ui;
        WindowController controller;
    }

    /**
     * Generic method. Will be used to start and create all windows
     *
     * @param fxmlFile (String) - Name of the FXML file to load
     * @return Window - The window class created
     *
     * @throws IOException - When the FXML file is not found
     */
    private Window load(String fxmlFile) throws IOException {
        Window window = new Window();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
        window.ui = fxmlLoader.load();
        window.controller = fxmlLoader.getController();
        window.controller.setMain(this);
        return window;
    }

    public Stage stage;
    private Scene scene;

    /**
     * Loading point of the application.
     * Starts by loading all windows and setting the "acccount manager" to be shown.
     * Will set the title for the application and also the icon.
     *
     * @param stage (Stage) - Automatically Passes it
     *
     * @throws IOException - When the FXML/Image file is not found
     */
    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;

        authWindow = load("auth/auth.fxml");
        mainWindow = load("main.fxml");

        scene = new Scene(authWindow.ui);
        scene.getStylesheets().add(getClass().getResource("styles/listView.css").toExternalForm());
        setStageTitle("Txipiron Client [v1.0] - a Mastodon Client - Account Management");
        stage.getIcons().add(new Image("file:src/main/resources/eus/ehu/txipironesmastodonfx/logos/dark_filled_1000.jpg"));
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Starting point of the java application.
     * Will call the javafx launch() method.
     * We'll ignore all arguments passed on to the application
     *
     * @param args (String[]) - Arguments passed to the application
     */
    public static void main(String[] args) {
        launch();
    }

    public void setStageTitle(String title) {
        stage.setTitle(title);
    }

    /**
     * Dinamically Change Scenes on-the-fly.
     * Will change the scene to the one passed as parameter.
     *
     * @param sceneName (String) - Name of the scene to change to
     * @param result    (List<Object>) - List of reference, token and id to pass to the controller
     */
    public void changeScene(String sceneName, List<Object> result) {
        switch (sceneName) {
            case "Auth" -> {
                setStageTitle("Txipiron Client [v1.0] - a Mastodon Client - Account Management");
                scene.setRoot(authWindow.ui);
            }
            case "Main" -> {
                setStageTitle("Txipiron Client [v1.0] - a Mastodon Client - Main Window");
                scene.setRoot(mainWindow.ui);
                mainWindow.controller.setRefTokenId(result);
                mainWindow.controller.homeListView();
            }
            default -> {
            }
        }
    }
}