package eus.ehu.txipironesmastodonfx.data_access;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Test class for the HTMLParser class.
 *
 * @author Nicolás Aguado
 * @author Haizea Bermejo
 * @author Xiomara Cáceces
 * @author Marcos Chouciño
 */
public class HTMLParserTest {

    @Test
    public void validUserNameCheck() {
        String v1 = "@nagr";
        String v2 = "@nagr@mastodon.social";
        String v3 = "@nagr@sub.do.main";
        String v4 = "@h0La_que-tal";
        Assertions.assertTrue(HTMLParser.USERNAME_PATTERN.matcher(v1).matches());
        Assertions.assertTrue(HTMLParser.USERNAME_PATTERN.matcher(v2).matches());
        Assertions.assertTrue(HTMLParser.USERNAME_PATTERN.matcher(v3).matches());
        Assertions.assertTrue(HTMLParser.USERNAME_PATTERN.matcher(v4).matches());
        String i1 = "@nagr@";
        String i2 = "@";
        String i3 = "@#";
        String i4 = "nagr@mastodon.social";
        String i5 = "nagr";
        String i6 = "#nagr";
        Assertions.assertFalse(HTMLParser.USERNAME_PATTERN.matcher(i1).matches());
        Assertions.assertFalse(HTMLParser.USERNAME_PATTERN.matcher(i2).matches());
        Assertions.assertFalse(HTMLParser.USERNAME_PATTERN.matcher(i3).matches());
        Assertions.assertFalse(HTMLParser.USERNAME_PATTERN.matcher(i4).matches());
        Assertions.assertFalse(HTMLParser.USERNAME_PATTERN.matcher(i5).matches());
        Assertions.assertFalse(HTMLParser.USERNAME_PATTERN.matcher(i6).matches());
    }

    @Test
    public void correctHtmlParseCheck() {
        String html1 = "<p>hola, esto es una prueba</p>";
        List<String> ex1 = List.of("hola, esto es una prueba");
        Assertions.assertEquals(ex1, HTMLParser.parseHTML(html1));
        String html2 = "<p>hola <a href=\"https://mastodon.social/@nagr\">@nagr</a> que tal</p>";
        List<String> ex2 = List.of("hola ", "@nagr", " que tal");
        Assertions.assertEquals(ex2, HTMLParser.parseHTML(html2));
        String html3 = "<p><span class=\"h-card\"><a href=\"https://mastodon.social/@Namtium\" class=\"u-url mention\">@<span>Namtium</span></a></span> What a nice day we have here, don&#39;t you agree? <span class=\"h-card\"><a href=\"https://mastodon.social/@xiiomaraxc\" class=\"u-url mention\">@<span>xiiomaraxc</span></a></span> <a href=\"https://mastodon.social/tags/softwaredevelopment\" class=\"mention hashtag\" rel=\"tag\">#<span>softwaredevelopment</span></a> <a href=\"https://www.github.com\" target=\"_blank\" rel=\"nofollow noopener noreferrer\"><span class=\"invisible\">https://www.</span><span class=\"\">github.com</span><span class=\"invisible\"></span></a></p>";
        List<String> ex3 = List.of("@Namtium", " What a nice day we have here, don't you agree? ", "@xiiomaraxc", "#softwaredevelopment", "https://www.github.com");
        Assertions.assertEquals(ex3, HTMLParser.parseHTML(html3));
    }
}
