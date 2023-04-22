package eus.ehu.txipironesmastodonfx.data_access;

import java.awt.*;
import java.io.IOException;
import java.net.*;

/**
 * This class contains some useful methods for system variables
 * It is used to store the tokens in the system variables
 *
 * @author Nicolás Aguado
 * @author Haizea Bermejo
 * @author Xiomara Cáceces
 * @author Marcos Chouciño
 */
public class NetworkUtils {

    /**
     * This method checks if the device has an internet connection
     * To do so, it tries to connect to mastodon.social API and fetch
     * a single (special) toot. If it can't, it returns false.
     *
     * @return boolean - True if the device has an internet connection, false otherwise
     */
    public static boolean hasInternet() {
        try {
            final URL url = new URL("https://mastodon.social/api/v1/statuses/109897715842769702");
            final URLConnection conn = url.openConnection();
            conn.setConnectTimeout(2000);
            conn.connect();
            conn.getInputStream().close();
            return true;
        } catch (MalformedURLException e) {
            // Url is not malformed, but just in case
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Open the provided URL in the web browser.
     *
     * @param url The URL (string) to open.
     */
    public static void openWebPage(String url) {
        try {
            openWebPage(new URL(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Open the provided URL in the web browser.
     *
     * @param url The URL to open.
     */
    public static void openWebPage(URL url) {
        try {
            openWebPage(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * Open the provided URI in the web browser.
     *
     * @param uri The URI to open.
     */
    public static void openWebPage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
