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
 * It has an ad-hoc implementation for the specific HTML.
 * From the regex to the recursive algorithm, it's the best tool
 * for THIS SPECIFIC job.
 *
 * @author Nicolás Aguado
 */
public class HTMLParser {

    /**
     * Mastodon username matcher pattern.
     * It will match any username in the form of:
     * "@n.agr"
     * "@n-a.gr@mastodon.social"
     * "@ngr@sub.do.main"
     * Taken from the official mastodon code
     * https://github.com/mastodon/mastodon/blob/88ce59505e701763468c83b3ac352bcc4be553d9/app/models/account.rb#L65
     */
    public static final Pattern USERNAME_PATTERN = Pattern.compile("(?<=^|[^\\/\\w])@(([a-zA-Z0-9_]+([a-zA-Z0-9_\\.-]+[a-zA-Z0-9_]+)?)(?:@[\\w\\.\\-]+[\\w]+)?)");


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
        // by design, when getting the output from the HTML
        // and after recursively traversing the tree, we get
        // that hashtags and mentions are always in the form of:
        // # - text // @ - text
        // And links are always in the form of:
        // http(s):// - text - (optional text depending on if last elem or not)
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

    /**
     * Recursive method to traverse the Node tree.
     * It will return a list of strings with the text of the node
     * and its children.
     * <p>
     * In the end, this resulted to be the most effective parser,
     * as it just goes element by element getting its root text.
     * By the design of the HTML, this is a textbook example of
     * a recursive tree algorithm.
     *
     * @param e (Node) - Node to traverse
     * @return (List < String >) - list of strings with the text of the node
     */
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