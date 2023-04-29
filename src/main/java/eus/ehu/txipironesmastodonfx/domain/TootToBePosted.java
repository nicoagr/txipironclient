package eus.ehu.txipironesmastodonfx.domain;

import java.util.List;

public class TootToBePosted {

    public String status;
    public String in_reply_to_id;

    public boolean sensitive;
    public List<String> media_ids;
    public String scheduled_at;

    /**
     * Constructor for a toot with no media.
     *
     * @param status The text of the toot.
     */
    public TootToBePosted(String status) {
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
     *
     * @param status    The text of the toot.
     * @param sensitive Whether the toot is sensitive or not.
     * @param media_ids The media ids to attach to the toot.
     */
    public TootToBePosted(String status, boolean sensitive, List<String> media_ids) {
        this(status, sensitive);
        this.media_ids = media_ids;
    }

    /**
     * Constructor for a toot with media, a reply and a post date.
     *
     * @param text      (String) - The text of the toot.
     * @param sensitive (boolean) - Whether the toot is sensitive or not.
     * @param mediaIds  (List<String>) - The media ids to attach to the toot.
     * @param postDate  (String) - The date to post the toot.
     */
    public TootToBePosted(String text, boolean sensitive, List<String> mediaIds, String postDate) {
        this(text, sensitive, mediaIds);
        this.scheduled_at = postDate;
    }
}
