package eus.ehu.txipironesmastodonfx.controllers;

import eus.ehu.txipironesmastodonfx.TxipironClient;

import java.util.List;

/**
 * Interface for the controllers of the windows.
 *
 * @author Nicolás Aguado
 * @author Haizea Bermejo
 * @author Marcos Chouciño
 * @author Xiomara Cáceces
 */
public interface WindowController {

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
    default void homeListView() {
        // Do nothing
    }
}
