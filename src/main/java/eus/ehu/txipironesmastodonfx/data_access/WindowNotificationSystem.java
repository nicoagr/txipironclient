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
            System.out.println("cualquier dia");

            if (trayIcon != null) {
                System.out.println("sdf");
                trayIcon.displayMessage("title", "message", TrayIcon.MessageType.NONE);
            }

        }
}




