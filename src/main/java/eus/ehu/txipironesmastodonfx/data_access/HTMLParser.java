package eus.ehu.txipironesmastodonfx.data_access;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.util.ArrayList;
import java.util.Iterator;
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
        List<String> temp = new ArrayList<>();
        List<Element> allElements = doc.getAllElements();
        if (allElements.size() > 3) {
            // why 3 you may ask? Well, we have to get trough
            // html -> head -> body -> content. Inspecting it, we get
            // index 3 as the first element of what we need.
            temp.addAll(traverseNode(allElements.get(3)));
        }
        // join hashtags and mentions
        Iterator<String> it = temp.iterator();
        String s, t;
        while (it.hasNext()) {
            s = it.next();
            if (s.equals("#") || s.equals("@")) {
                output.add(s + it.next());
            } else if (s.startsWith("http://") || s.startsWith("https://")) {
                t = s + it.next();
                if (it.hasNext())
                    t = t + it.next();
                output.add(t);
            } else if (!s.isEmpty() && !s.equals(" ")) {
                output.add(s);
            }
        }
        return output;
    }

    public static List<String> traverseNode(Node e) {
        if (e instanceof TextNode) {
            return List.of(((TextNode) e).text());
        } else {
            List<String> output = new ArrayList<>();
            for (Node child : e.childNodes()) {
                output.addAll(traverseNode(child));
            }
            return output;
        }
    }

}