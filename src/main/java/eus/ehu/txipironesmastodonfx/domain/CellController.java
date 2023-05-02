package eus.ehu.txipironesmastodonfx.domain;

import javafx.scene.Node;
import eus.ehu.txipironesmastodonfx.controllers.main.MainWindowController;

/**
 * Interface that will represent
 * a domain item that can be shown
 * via a custom cell
 */
public interface CellController {

    /**
     * This method will initialize the
     * custom cell controller, and return
     * the node that will be shown in the
     * VBox
     *
     * @param m (MainWindowController) The main window controller
     * @return (Node) The node that will be shown in the VBox
     */
    Node display(MainWindowController m);
}
