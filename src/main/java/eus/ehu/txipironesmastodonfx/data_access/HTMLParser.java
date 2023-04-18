package eus.ehu.txipironesmastodonfx.data_access;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Helper class used to parse HTML.
 * It will be used to parse the HTML returned by the mastodon API.
 * It has an ad-hoc implementation for the specific HTML, so don't spect
 * it to work with any HTML.
 *
 * @author Nicolás Aguado
 * @author Haizea Bermejo
 * @author Marcos Chouciño
 * @author Xiomara Cáceces
 */
public class HTMLParser {

    /**
     * Mastodon username matcher pattern.
     * It will match any username in the form of:
     * <p>
     * "@nagr"
     * "@nagr@mastodon.social"
     * "@nagr@sub.do.main"
     */
    public static final Pattern USERNAME_PATTERN = Pattern.compile("@[a-zA-Z0-9][a-zA-Z0-9_-]{0,28}[a-zA-Z0-9](@[a-zA-Z0-9][a-zA-Z0-9-]*[a-zA-Z0-9](\\.[a-zA-Z0-9][a-zA-Z0-9-]*[a-zA-Z0-9])+)?");


    /**
     * Method to parse HTML.
     * It will divide the elements and separate them
     * into text, hashtags, mentions, new lines and links.
     *
     * @param html (String) - HTML to parse
     * @return (List < String >) - list of strings with the parsed HTML
     */
    public static List<String> parseHTML(String html) {
        Document doc = Jsoup.parse(html);
        List<String> output = new ArrayList<>();
        boolean hashtag = false;
        boolean at = false;
        for (Element element : doc.getAllElements()) {
            if (!element.ownText().isEmpty() && !element.tagName().equalsIgnoreCase("span")) {
                if (element.ownText().equals("#")) {
                    hashtag = true;
                    continue;
                }
                if (element.ownText().equals("@")) {
                    at = true;
                    continue;
                }
                output.add(element.ownText());
            } else if (element.tagName().equalsIgnoreCase("a")) {
                output.add(element.attr("href"));
            } else if (element.tagName().equalsIgnoreCase("br")) {
                output.add("\n");
            } else if (element.tagName().equalsIgnoreCase("span")) {
                if (at) {
                    output.add("@" + element.ownText());
                    at = false;
                } else if (hashtag) {
                    output.add("#" + element.ownText());
                    hashtag = false;
                }
            }
        }
        return output;
    }
}