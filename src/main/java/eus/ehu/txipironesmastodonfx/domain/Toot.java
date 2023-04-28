package eus.ehu.txipironesmastodonfx.domain;

import java.util.List;

/**
 * Represents a Mastodon toot/status.
 * It will contain all attributes needed for our application.
 * Check reference in <a href="https://docs.joinmastodon.org/entities/Status/">here</a>
 *
 * @author Nicolás Aguado
 * @author Haizea Bermejo
 * @author Xiomara Cáceces
 * @author Marcos Chouciño
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
    public boolean bookmarked;
    public boolean reblogged;
    public String content;
    public Account account;
    // A reblog simply is an anidated toot
    // Recursion!
    public Toot reblog;

    public List<MediaAttachment> media_attachments;

    public List<Mention> mentions;

    public class Mention {
        public String id;
        public String username;
    }

    /**
     * Constructor for the Toot class.
     * It will be used to create a Toot object from a JSON file.
     *
     * @param id (String) - The ID of the toot
     */
    public Toot (String id){
        this.id = id;
    }

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
                ", media_attachments=" + media_attachments +
                ", mentions=" + mentions +
                '}';
    }

    /**
     * A method to compare two Toot objects.
     * @param toot
     * @return boolean - True if the IDs are the same, false otherwise
     */
    public boolean equals(Toot toot){
        return this.id.equals(toot.id);
    }
}