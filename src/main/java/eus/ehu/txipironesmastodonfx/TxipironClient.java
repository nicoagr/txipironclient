package eus.ehu.txipironesmastodonfx;

import eus.ehu.txipironesmastodonfx.controllers.WindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private static final Logger logger = LogManager.getLogger("TxipironClient");

    /**
     * Registered Mastodon App Id. Will be used for oauth
     */
    public static final String MASTODON_APP_ID = "dYnZgMCCGEg4DpYOgQX8LzR2J8GPo_drmuVqaYAVtok";
    /**
     * Registered Mastodon App Secret. Will be used for oauth
     */
    public static final String MASTODON_APP_SECRET = "9FxLwJO760hJZsL1blHQ1KrBJP3Ey5ShMgwl7dVnH7M";
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
        logger.info("--------------------------------------------");
        logger.info("Txipiron Client [v1.0] - a Mastodon Client");
        logger.info("--------------------------------------------");
        logger.info("Starting Client");
        authWindow = load("auth/auth.fxml");
        mainWindow = load("main.fxml");
        logger.debug("Windows (UIs) loaded");
        scene = new Scene(authWindow.ui);

        logger.debug("Stylesheet loaded");
        setStageTitle("Txipiron Client [v1.0] - a Mastodon Client - Account Management");
        stage.getIcons().add(new Image("file:src/main/resources/eus/ehu/txipironesmastodonfx/logos/dark_filled_1000.jpg"));
        logger.debug("Icon loaded");
        stage.setScene(scene);
        stage.show();
        logger.info("Scene shown - Client Started");
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

    /**
     * Sets the title of the stage
     *
     * @param title (String) - Title to set
     */
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
                logger.info("Changed scene to Auth");
                scene.setRoot(authWindow.ui);
            }
            case "Main" -> {
                setStageTitle("Txipiron Client [v1.0] - a Mastodon Client - Main Window");
                scene.setRoot(mainWindow.ui);
                logger.info("Changed scene to Main");
                mainWindow.controller.setRefTokenId(result);
                mainWindow.controller.homeListView();
            }
            default -> {
            }
        }
    }
}