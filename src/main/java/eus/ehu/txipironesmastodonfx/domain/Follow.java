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
    public String display_name;
    public Boolean following;

    public Follow(String id) {
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
        return "Follow{" +
                "id='" + id + '\'' +
                ", acct='" + acct + '\'' +
                ", avatar='" + avatar + '\'' +
                ", display_name='" + display_name + '\'' +
                ", following=" + following +
                '}';
    }

    public boolean equals(Follow f) {
    	return this.id.equals(f.id);
    }
}
