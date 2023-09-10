package eus.ehu.txipironesmastodonfx.controllers;

import eus.ehu.txipironesmastodonfx.TxipironClient;
import javafx.scene.Parent;

import java.util.List;

/**
 * Interface for the controllers of the windows.
 *
 * @author Nicolás Aguado
 * @author Haizea Bermejo
 * @author Xiomara Cáceces
 * @author Marcos Chouciño
 */
public interface WindowController {

    /**
     * Gets the scene wrapper. This can be an
     * AnchorPane, a BorderPane, a VBox, etc.
     * Used for applying styles
     * @return (Parent) - The scene wrapper
     */
     Parent getSceneWrapper();

    /**
     * Sets a reference to the main application
     *
     * @param app (TxipironClient) - The main application
     */
    void setMain(TxipironClient app);

    /**
     * Sets a reference to the main application
     *
     * @param result (List<Object>) - The list of reference, token and id to be set
     */
    default void setRefTokenId(List<Object> result) {
        // Do nothing
    }

    /**
     * homeListView method
     */
    default void initialTask() {
        // Do nothing
    }
}
