package eus.ehu.txipironesmastodonfx.domain;

/**
 * Represents a Mastodon account.
 * It will contain all attributes needed for our application.
 * Check reference in <a href="https://docs.joinmastodon.org/methods/accounts/#get">here</a>
 *
 * @author Nicolás Aguado
 * @author Haizea Bermejo
 * @author Marcos Chouciño
 * @author Xiomara Cáceces
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
