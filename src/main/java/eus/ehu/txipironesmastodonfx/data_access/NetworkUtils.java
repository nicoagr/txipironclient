package eus.ehu.txipironesmastodonfx.data_access;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

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
            conn.setConnectTimeout(3000);
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
}
