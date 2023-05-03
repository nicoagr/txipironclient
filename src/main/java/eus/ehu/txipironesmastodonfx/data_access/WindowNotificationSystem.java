package eus.ehu.txipironesmastodonfx.data_access;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class WindowNotificationSystem {
    private TrayIcon trayIcon;


    public WindowNotificationSystem() {

    }

    public void initialice() throws IOException, AWTException {
        SystemTray tray = SystemTray.getSystemTray();
        BufferedImage trayIconImage = ImageIO.read(getClass().getResource(("/eus/ehu/txipironesmastodonfx/logos/dark_filled_1000.jpg")));
        int trayIconWidth = new TrayIcon(trayIconImage).getSize().width;
            this.trayIcon = new TrayIcon(trayIconImage.getScaledInstance(trayIconWidth, -1, Image.SCALE_SMOOTH), "TwitterC");
        System.out.println("caca culo");
        // add the tray image
        tray.add(trayIcon);

    }

        public  void trowNotificationWindow(String mainText) throws IOException, AWTException {
            String os = System.getProperty("os.name");

            if (os.contains("Linux")) {
                ProcessBuilder builder = new ProcessBuilder(
                        "zenity",
                        "--notification",
                        "--text=" + mainText);
                builder.inheritIO().start();
            } else if (os.contains("Mac")) {
                ProcessBuilder builder = new ProcessBuilder(
                        "osascript", "-e",
                        "display notification \"" + "REPLAZAMEEEEEEEE" + "\""
                                + " with title \"" + "REPLAZAMEEEEEE" + "\"");
                builder.inheritIO().start();
            } else if (SystemTray.isSupported()) {


                if (trayIcon != null) {
                    trayIcon.displayMessage(mainText, "PlaceHolder", TrayIcon.MessageType.NONE);
                }
            /*
                SystemTray tray = SystemTray.getSystemTray();

                TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
                trayIcon.setImageAutoSize(true);
                tray.add(trayIcon);

                trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
            }
            */

            }

        }
}




