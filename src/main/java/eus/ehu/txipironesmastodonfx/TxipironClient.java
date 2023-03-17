package eus.ehu.txipironesmastodonfx;

import eus.ehu.txipironesmastodonfx.controllers.WindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class TxipironClient extends Application {

    private Window authWindow;
    private Window mainWindow;

    static class Window {
        Parent ui;
        WindowController controller;
    }

    private Window load(String fxmlFile) throws IOException {
        Window window = new Window();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
        window.ui = fxmlLoader.load();
        window.controller = fxmlLoader.getController();
        window.controller.setMain(this);
        return window;
    }

    private Stage stage;
    private Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;

        authWindow = load("auth.fxml");
        mainWindow = load("main.fxml");

        scene = new Scene(authWindow.ui);
        stage.setTitle("Txipiron Client [v1.0] - a Mastodon Client - Account Management");
        stage.getIcons().add(new Image("file:src/main/resources/eus/ehu/txipironesmastodonfx/dark_filled_1000.jpg"));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public void changeScene(String sceneName) {
        switch (sceneName) {
            case "Loading" -> {
                stage.setTitle("Txipiron Client [v1.0] - a Mastodon Client - Account Management");
                scene.setRoot(authWindow.ui);
            }
            case "Main" ->  {
                stage.setTitle("Txipiron Client [v1.0] - a Mastodon Client - Main Window");
                scene.setRoot(mainWindow.ui);
            }
            default -> {
            }
        }
    }
}