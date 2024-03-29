package eus.ehu.txipironesmastodonfx.data_access;

import eus.ehu.txipironesmastodonfx.domain.Account;
import eus.ehu.txipironesmastodonfx.domain.Follow;
import eus.ehu.txipironesmastodonfx.domain.SearchResult;
import eus.ehu.txipironesmastodonfx.domain.Toot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

/**
 * Test class for the APIAccessManager class.
 * Some methods are not tested because they are ambiguous
 * or too generic
 *
 * @author Nicolás Aguado
 * @author Haizea Bermejo
 * @author Xiomara Cáceces
 * @author Marcos Chouciño
 */
public class APIAccessManagerTest {
    String token, idresult, tootId, untootId, inexistentId;
    Toot toot;
    Account logAccount;

    @BeforeEach
    public void setUp() {
        //this id is not a valid id for a toot
        inexistentId = "1234";
        //@txipirones
        token = "Wn9h3I8mUcEuSwS3LUptIO0aVwK79cdxjrklY0iRxrQ";
        idresult = "110294352117577191";
        logAccount = new Account("110294352117577191");
        //@Namtium's toot which is favourited, bookmarked and boosted
        tootId = "110134917103846112";
        untootId = "110134917103846112";
        toot = new Toot("110134917103846112");
    }

    @Test
    public void testVerifyAndGetId() {
        Assertions.assertNull(APIAccessManager.verifyAndGetId(null));
        Assertions.assertNull(APIAccessManager.verifyAndGetId(""));
        Assertions.assertEquals(idresult, APIAccessManager.verifyAndGetId(token));
    }

    @Test
    public void testGetFollow(){
        //followers
        Assertions.assertNull(APIAccessManager.getFollow(null, null, false));
        Assertions.assertNull(APIAccessManager.getFollow("", null, false));
        Assertions.assertNull(APIAccessManager.getFollow(null, "", false));
        Assertions.assertNull(APIAccessManager.getFollow("", "", false));
        Assertions.assertNull(APIAccessManager.getFollow(null, token, false));
        Assertions.assertNull(APIAccessManager.getFollow("", token, false));
        Assertions.assertNull(APIAccessManager.getFollow(idresult, null, false));
        Assertions.assertNull(APIAccessManager.getFollow(idresult, "", false));
        //@juananpe
        Assertions.assertTrue(APIAccessManager.getFollow(idresult, token, false).get(0).equals(new Follow("109842111446764244")));

        //following
        Assertions.assertNull(APIAccessManager.getFollow(null, null, true));
        Assertions.assertNull(APIAccessManager.getFollow("", null, true));
        Assertions.assertNull(APIAccessManager.getFollow(null, "", true));
        Assertions.assertNull(APIAccessManager.getFollow("", "", true));
        Assertions.assertNull(APIAccessManager.getFollow(null, token, true));
        Assertions.assertNull(APIAccessManager.getFollow("", token, true));
        Assertions.assertNull(APIAccessManager.getFollow(idresult, null, true));
        Assertions.assertNull(APIAccessManager.getFollow(idresult, "", true));
        //@juananpe
        Assertions.assertTrue(APIAccessManager.getFollow(idresult, token, true).get(0).equals(new Follow("109842111446764244")));
    }

    @Test
    public void testGetIdFromUsername(){
        Assertions.assertNull(APIAccessManager.getIdFromUsername(null));
        Assertions.assertNull(APIAccessManager.getIdFromUsername(""));
        Assertions.assertEquals(idresult, APIAccessManager.getIdFromUsername("txipirones"));
    }

    @Test
    public void testFavouriteToot(){
        Assertions.assertEquals(APIAccessManager.favouriteToot(null, null), 401);
        Assertions.assertEquals(APIAccessManager.favouriteToot(tootId, null), 401);
        Assertions.assertEquals(APIAccessManager.favouriteToot(null, token), 404);
        Assertions.assertEquals(APIAccessManager.favouriteToot(tootId, ""), 401);
        Assertions.assertEquals(APIAccessManager.favouriteToot("", token), 404);
        Assertions.assertEquals(APIAccessManager.favouriteToot("", ""), 404);
        Assertions.assertEquals(APIAccessManager.favouriteToot(inexistentId, token), 404);
        Assertions.assertEquals(APIAccessManager.favouriteToot(tootId, token),200);
    }

    @Test
    public void testUnfavouriteToot(){
        Assertions.assertEquals(APIAccessManager.unfavouriteToot(null, null), 401);
        Assertions.assertEquals(APIAccessManager.unfavouriteToot(untootId, null), 401);
        Assertions.assertEquals(APIAccessManager.unfavouriteToot(null, token), 404);
        Assertions.assertEquals(APIAccessManager.unfavouriteToot(untootId, ""), 401);
        Assertions.assertEquals(APIAccessManager.unfavouriteToot("", token), 404);
        Assertions.assertEquals(APIAccessManager.unfavouriteToot("", ""), 404);
        Assertions.assertEquals(APIAccessManager.unfavouriteToot(inexistentId, token), 404);
        Assertions.assertEquals(APIAccessManager.unfavouriteToot(untootId, token), 200);
    }

    @Test
    public void testReblogToot(){
        Assertions.assertEquals(APIAccessManager.reblogToot(null, null), 401);
        Assertions.assertEquals(APIAccessManager.reblogToot(tootId, null), 401);
        Assertions.assertEquals(APIAccessManager.reblogToot(null, token), 404);
        Assertions.assertEquals(APIAccessManager.reblogToot(tootId, ""), 401);
        Assertions.assertEquals(APIAccessManager.reblogToot("", token), 404);
        Assertions.assertEquals(APIAccessManager.reblogToot("", ""), 404);
        Assertions.assertEquals(APIAccessManager.reblogToot(inexistentId, token), 404);
        Assertions.assertEquals(APIAccessManager.reblogToot(tootId, token),200);
    }

    @Test
    public void testUnreblogToot(){
        Assertions.assertEquals(APIAccessManager.unreblogToot(null, null), 401);
        Assertions.assertEquals(APIAccessManager.unreblogToot(untootId, null), 401);
        Assertions.assertEquals(APIAccessManager.unreblogToot(null, token), 404);
        Assertions.assertEquals(APIAccessManager.unreblogToot(untootId, ""), 401);
        Assertions.assertEquals(APIAccessManager.unreblogToot("", token), 404);
        Assertions.assertEquals(APIAccessManager.unreblogToot("", ""), 404);
        Assertions.assertEquals(APIAccessManager.unreblogToot(inexistentId, token), 404);
        Assertions.assertEquals(APIAccessManager.unreblogToot(untootId, token), 200);
    }
}
