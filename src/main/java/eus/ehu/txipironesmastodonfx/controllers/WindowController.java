package eus.ehu.txipironesmastodonfx.controllers;

import eus.ehu.txipironesmastodonfx.TxipironClient;

import java.sql.SQLException;

public interface WindowController {
    void setMain(TxipironClient app);

    default void setRef(Integer ref) throws SQLException {
        // Do nothing
    }

    default public void start(){

    }
}
