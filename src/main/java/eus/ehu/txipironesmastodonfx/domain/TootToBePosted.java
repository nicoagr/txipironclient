package eus.ehu.txipironesmastodonfx.domain;

import java.util.ArrayList;
import java.util.List;

public class TootToBePosted {

    public String status;
    public String in_reply_to_id;

    public boolean sensitive;
    public List<String> media_ids;

    /**
     * Constructor for a toot with no media.
     *
     * @param status The text of the toot.
     */
    public TootToBePosted(String status) {
        media_ids = new ArrayList<>();
        this.status = status;
    }

    /**
     * Constructor for a toot with media.
     * @param status The text of the toot.
     * @param sensitive Whether the toot is sensitive or not.
     */
    public TootToBePosted(String status, boolean sensitive) {
        this(status);
        this.sensitive = sensitive;
    }

    /**
     * Constructor for a toot with media and a reply.
     * @param status The text of the toot.
     * @param sensitive Whether the toot is sensitive or not.
     * @param in_reply_to_id The id of the toot to which this toot is a reply.
     */
    public TootToBePosted(String status, boolean sensitive, String in_reply_to_id) {
        this(status, sensitive);
        this.in_reply_to_id = in_reply_to_id;
    }
}
