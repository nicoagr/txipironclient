package eus.ehu.txipironesmastodonfx.domain;

public class Profile {

    public String username;
    public String name;
    public String avatar;
    public String header;
    public String followers_count;
    public String following_count;
    public String posts_count;




    /**
     * For quick testing and debugging,
     * a toString method is provided,
     * containing all the attributes.
     *
     * @return String - A string representation of the object
     */
    @Override
    public String toString() {
        return "Profile{" +
                "username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", header='" + header + '\'' +
                ", followers_count='" + followers_count + '\'' +
                ", following_count='" + following_count + '\'' +
                ", posts_count='" + posts_count + '\'' +
                '}';
    }
}
