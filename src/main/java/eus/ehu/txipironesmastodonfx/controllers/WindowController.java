package eus.ehu.txipironesmastodonfx.controllers;

import eus.ehu.txipironesmastodonfx.TxipironClient;

import java.sql.SQLException;

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
     * @param ref (Integer) - The reference to the main application
     * @throws SQLException - If there is a problem with the database
     */
    default void setRef(Integer ref) throws SQLException {
        // Do nothing
    }
}
