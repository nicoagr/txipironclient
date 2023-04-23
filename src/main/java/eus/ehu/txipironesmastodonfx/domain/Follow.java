package eus.ehu.txipironesmastodonfx.domain;

/**
 * Represents a Mastodon follower/following.
 * It will contain all attributes needed for our application.
 * Its like an Account, but with fewer attributes.
 * It's NOT general, an Account will be the general equivalent.
 * We'll use it for quick and easy access.
 *
 * @author Nicolás Aguado
 * @author Haizea Bermejo
 * @author Xiomara Cáceces
 * @author Marcos Chouciño
 */
public class Follow {
    public String id;
    public String acct;
    public String avatar;
    public Boolean following;

    /**
     * For quick testing and debugging,
     * a toString method is provided,
     * containing all the attributes.
     *
     * @return String - A string representation of the object
     */
    public String toString() {
        return "Follow{" +
                "id='" + id + '\'' +
                ", acct='" + acct + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
