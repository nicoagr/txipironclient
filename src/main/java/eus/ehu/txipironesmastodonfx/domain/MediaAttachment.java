package eus.ehu.txipironesmastodonfx.domain;

/**
 * This class represents a MediaAttachment
 * entity in the mastodon API.
 *
 * @author Nicolás Aguado
 * @author Haizea Bermejo
 * @author Xiomara Cáceces
 * @author Marcos Chouciño
 */
public class MediaAttachment {
    public String id;
    public String type;
    public String url;
    public String description;

    public MediaAttachment(String type, String url) {
        this.type = type;
        this.url = url;
    }
}
