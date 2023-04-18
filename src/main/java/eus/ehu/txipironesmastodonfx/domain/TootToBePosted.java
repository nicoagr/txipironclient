package eus.ehu.txipironesmastodonfx.domain;

import java.util.ArrayList;
import java.util.List;

public class TootToBePosted {

    public String status;
    public String in_reply_to_id;

    public boolean sensitive;
    public List<String> media_ids;

    public TootToBePosted(String status) {
        media_ids = new ArrayList<>();
        this.status = status;
    }

    public TootToBePosted(String status, boolean sensitive) {
        this(status);
        this.sensitive = sensitive;
    }

    public TootToBePosted(String status, boolean sensitive, String in_reply_to_id) {
        this(status, sensitive);
        this.in_reply_to_id = in_reply_to_id;
    }
}
