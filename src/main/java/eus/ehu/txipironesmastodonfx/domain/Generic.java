package eus.ehu.txipironesmastodonfx.domain;

import eus.ehu.txipironesmastodonfx.controllers.main.MainWindowController;
import eus.ehu.txipironesmastodonfx.controllers.windowControllers.ErrorCellController;
import eus.ehu.txipironesmastodonfx.controllers.windowControllers.HeaderCellController;
import eus.ehu.txipironesmastodonfx.controllers.windowControllers.PostTootController;
import eus.ehu.txipironesmastodonfx.controllers.windowControllers.SettingsController;
import javafx.scene.Node;

/**
 * This class will represent
 * a generic object that can
 * be shown in the VBox
 * It will have 4 types of possible
 * objects:
 * - Error
 * - Post toot
 * - Settings
 * - Message
 *
 * @author Nicolás Aguado
 * @author Haizea Bermejo
 * @author Xiomara Cáceces
 * @author Marcos Chouciño
 */
public class Generic implements CellController {

    /**
     * This enum represents all the types
     * that a Generic object can have
     */
    public enum of {
        ERROR,
        POST_TOOT,
        SETTINGS,
        MESSAGE
    }

    of type;
    String text;

    /**
     * This method will initialize the
     * custom cell controller, and return
     * the node that will be shown in the
     * VBox
     *
     * @param m (MainWindowController) The main window controller
     * @return (Node) The node that will be shown in the VBox
     */
    @Override
    public Node display(MainWindowController m) {
        switch (type) {
            case ERROR -> {
                ErrorCellController e = new ErrorCellController(text);
                return e.getUI();
            }
            case MESSAGE -> {
                HeaderCellController h = new HeaderCellController(text, m);
                return h.getUI();
            }
            case SETTINGS -> {
                SettingsController s = new SettingsController(m);
                s.loadDefaultSettings();
                return s.getUI();
            }
            case POST_TOOT -> {
                PostTootController p = new PostTootController(m);
                return p.getUI();
            }
        }
        return null;
    }

    public Generic(of type, String text) {
        this.type = type;
        this.text = text;
    }
}
