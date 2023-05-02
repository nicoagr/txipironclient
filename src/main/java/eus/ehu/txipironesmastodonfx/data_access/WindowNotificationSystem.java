package eus.ehu.txipironesmastodonfx.data_access;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.io.IOException;

public class WindowNotificationSystem {



        public static <Notifications> void trowNotificationWindow(String mainText) throws IOException, AWTException {

          //  Image image = ImageIO.read(WindowNotificationSystem.class.getResource("img/logo/light_filled_250.jpg"));
            Image image = Toolkit.getDefaultToolkit().createImage(WindowNotificationSystem.class.getResource("img/logo/light_filled_250.jpg"));

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
                        "display notification \"" + mainText + "\""
                                + " with title \"" + "title" + "\"");
                builder.inheritIO().start();
            }else{
            }
        }
}
