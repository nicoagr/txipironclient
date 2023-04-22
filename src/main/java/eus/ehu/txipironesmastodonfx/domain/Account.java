package eus.ehu.txipironesmastodonfx.domain;

/**
 * Represents a Mastodon account.
 * It will contain all attributes needed for our application.
 * Check reference in <a href="https://docs.joinmastodon.org/methods/accounts/#get">here</a>
 *
 * @author Nicolás Aguado
 * @author Haizea Bermejo
 * @author Xiomara Cáceces
 * @author Marcos Chouciño
 */
public class Account {
    public String id;
    public String acct;
    public String avatar;
    public String header;
    public int statuses_count;
    public int followers_count;
    public int following_count;
    public String note;
    public String last_status_at;
    public String display_name;



    /**
     * Big contructor for the Account class.
     * It will set all the attributes.
     *
     * @param id              (String) - The account's id
     * @param acct            (String) - The account's username
     * @param avatar          (String) - The account's avatar url
     * @param header          (String) - The account's header url
     * @param statuses_count  (int) - The account's statuses count
     * @param followers_count (int) - The account's followers count
     * @param following_count (int) - The account's following count
     * @param note            (String) - The account's profile description
     * @param last_status_at  (String) - The account's last status date
     * @param display_name    (String) - The account's display name
     */
    public Account(String id, String acct, String avatar, String header, int statuses_count, int followers_count, int following_count, String note, String last_status_at, String display_name) {
        this(id, acct, avatar);
        this.header = header;
        this.statuses_count = statuses_count;
        this.followers_count = followers_count;
        this.following_count = following_count;
        this.note = note;
        this.last_status_at = last_status_at;
        this.display_name = display_name;
    }

    /**
     * Empty constructor for the Account class.
     * It will set all the attributes to null.
     */
    public Account() {
    }

    public Account(String id, String acct, String avatar) {
        this.id = id;
        this.acct = acct;
        this.avatar = avatar;
    }

    /**
     * For quick testing and debugging,
     * a toString method is provided,
     * containing all the attributes.
     *
     * @return String - A string representation of the object
     */
    public String toString() {
        return "Account{" +
                "id='" + id + '\'' +
                ", acct='" + acct + '\'' +
                ", avatar='" + avatar + '\'' +
                ", header='" + header + '\'' +
                ", statuses_count=" + statuses_count +
                ", followers_count=" + followers_count +
                ", following_count=" + following_count +
                ", note='" + note + '\'' +
                ", last_status_at='" + last_status_at + '\'' +
                ", display_name='" + display_name + '\'' +
                '}';
    }
}
