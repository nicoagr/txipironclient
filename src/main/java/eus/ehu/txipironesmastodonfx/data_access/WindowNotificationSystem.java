package eus.ehu.txipironesmastodonfx.data_access;

import eus.ehu.txipironesmastodonfx.controllers.main.MainWindowController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class WindowNotificationSystem {
    private TrayIcon trayIcon;

private MainWindowController master;
    public WindowNotificationSystem(MainWindowController master) {
        this.master = master;

    }
    /**
     * This method intiializes the tray sistem for notifications to thw winodows and macOS
     */

    /**
     * This method cheks the OS and display a notification Window with the mainText
     */
        public  void trowNotificationWindow(String mainText) throws IOException, AWTException {
            String os = System.getProperty("os.name");
            if (os.contains("Linux")) {
                ProcessBuilder builder = new ProcessBuilder(
                        "zenity",
                        "--notification",
                        "--text=" + mainText);
                builder.inheritIO().start();
            }else if(SystemTray.isSupported()) {


                SystemTray tray = SystemTray.getSystemTray();

                //If the icon is a file
                //Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
                //Alternative (if the icon is on the classpath):
                Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/eus/ehu/txipironesmastodonfx/logos/dark_filled_1000.jpg"));

                TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
                //Let the system resize the image if needed
                trayIcon.setImageAutoSize(true);
                //Set tooltip text for the tray icon
                trayIcon.setToolTip("System tray icon demo");
                tray.add(trayIcon);

                trayIcon.displayMessage("Txipiron Client", mainText, MessageType.INFO);


            }

        }
}




