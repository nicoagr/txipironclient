package eus.ehu.txipironesmastodonfx.domain;

/**
 * Represents a Mastodon toot/status.
 * It will contain all attributes needed for our application.
 * Check reference in <a href="https://docs.joinmastodon.org/entities/Status/">here</a>
 *
 * @author Nicolás Aguado
 * @author Haizea Bermejo
 * @author Marcos Chouciño
 * @author Xiomara Cáceces
 */
public class Toot {

    public String id;
    public String created_at;
    public String in_reply_to_id;
    public boolean sensitive;
    public String uri;
    public int replies_count;
    public int reblogs_count;
    public int favourites_count;
    public boolean favourited;
    public boolean reblogged;
    public String content;
    public Account account;
    // A reblog simply is an anidated toot
    // Recursion!
    public Toot reblog;

    // TODO: Sprint 2: Media attachments
    // TODO: Sprint 2: Mentions
    // TODO: Sprint 2: Card

    /**
     * For quick testing and debugging,
     * a toString method is provided,
     * containing all the attributes.
     *
     * @return String - A string representation of the object
     */
    @Override
    public String toString() {
        return "Toot{" +
                "id='" + id + '\'' +
                ", created_at='" + created_at + '\'' +
                ", in_reply_to_id='" + in_reply_to_id + '\'' +
                ", sensitive=" + sensitive +
                ", uri='" + uri + '\'' +
                ", replies_count=" + replies_count +
                ", reblogs_count=" + reblogs_count +
                ", favourites_count=" + favourites_count +
                ", favourited=" + favourited +
                ", reblogged=" + reblogged +
                ", content='" + content + '\'' +
                ", account=" + account +
                ", reblog=" + reblog +
                '}';
    }
}