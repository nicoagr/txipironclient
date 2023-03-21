package eus.ehu.txipironesmastodonfx.controllers;

import eus.ehu.txipironesmastodonfx.TxipironClient;

public interface WindowController {
    void setMain(TxipironClient app);

    default void setRef(Integer ref) {
        // Do nothing
    }
}
