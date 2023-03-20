package eus.ehu.txipironesmastodonfx.data_access;

import eus.ehu.txipironesmastodonfx.domain.Follow;
import eus.ehu.txipironesmastodonfx.domain.Toot;

import java.util.Vector;

public interface DataInterface {

    /**
     * This method gives the a vector of toots that  should be seen in the frontpage of the user that is passed as a paramenter.
     */

    public Vector<Toot> getUserToots(String id);


    /**
     * This method gives  a vector of followers of the user that is passed as a paramenter.
     */
    public Vector<Follow> getUserFollowers(String id);

    /**
     * This method gives  a vector of the users that which the user that is passed as a parament follows.
     */
    public Vector<Follow> getUserFollows(String id);



    /**
     * This method gives the user id of the ser that is passed as a parament.
     */
    public String getUserId(String username);
}
