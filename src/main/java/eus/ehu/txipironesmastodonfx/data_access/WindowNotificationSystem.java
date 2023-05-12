package eus.ehu.txipironesmastodonfx.data_access;

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

    /**
     * This method intiializes the tray sistem for notifications
     */
    public WindowNotificationSystem(NotificationSystem notify) {
        this.noti = notify;
        if (!SystemTray.isSupported()) {
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




