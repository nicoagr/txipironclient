package eus.ehu.txipironesmastodonfx.data_access;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.TrayIcon;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.Image;
import java.awt.AWTException;
import java.io.IOException;
import java.awt.PopupMenu;
import java.awt.MenuItem;

public class WindowNotificationSystem {
    private TrayIcon trayIcon;

    private NotificationSystem noti;
    private static final Logger logger = LogManager.getLogger("WindowNotificationSystem");

    /**
     * This method intiializes the tray sistem for notifications
     */
    public WindowNotificationSystem(NotificationSystem notify) {
        this.noti = notify;
        if (!SystemTray.isSupported() || System.getProperty("os.name").contains("Linux")) {
            return;
        }
        SystemTray tray = SystemTray.getSystemTray();

        Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/eus/ehu/txipironesmastodonfx/logos/dark_filled_1000.jpg"));
        trayIcon = new TrayIcon(image, "Txipiron Client Notification Service");
        //Let the system resize the image if needed
        trayIcon.setImageAutoSize(true);
        //Add the icon to the system tray
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
        // Add a popup exit menu to tray icon
        PopupMenu popupMenu = new PopupMenu();
        MenuItem closeItem = new MenuItem("Close Application");
        closeItem.addActionListener(e -> {
            noti.deactivateNotification();
            System.exit(0);
        });
        popupMenu.add(closeItem);
        trayIcon.setPopupMenu(popupMenu);
    }

    /**
     * This method checks the OS and displays a notification Window with the mainText
     */
    public void trowNotificationWindow(String mainText) throws IOException {
        String os = System.getProperty("os.name");
        logger.info("Throwing notification with text: " + mainText);
        if (os.contains("Linux")) {
            ProcessBuilder builder = new ProcessBuilder(
                    "zenity",
                    "--notification",
                    "--text=" + mainText);
            builder.inheritIO().start();
        } else if (SystemTray.isSupported()) {
            trayIcon.displayMessage("Txipiron Client", mainText, TrayIcon.MessageType.INFO);
        }
    }

    /**
     * This method removes the tray icon from the system tray
     */
    public void removeTrayIcon() {
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            tray.remove(trayIcon);
        }
    }
}




