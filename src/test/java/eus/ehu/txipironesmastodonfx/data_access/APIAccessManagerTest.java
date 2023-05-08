package eus.ehu.txipironesmastodonfx.data_access;

import eus.ehu.txipironesmastodonfx.domain.Account;
import eus.ehu.txipironesmastodonfx.domain.Follow;
import eus.ehu.txipironesmastodonfx.domain.SearchResult;
import eus.ehu.txipironesmastodonfx.domain.Toot;
import org.junit.jupiter.api.AfterEach;
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
    public void testGetTootId(){
        Assertions.assertNull(APIAccessManager.getTootId(null, null));
        Assertions.assertNull(APIAccessManager.getTootId("", null));
        Assertions.assertNull(APIAccessManager.getTootId(null, ""));
        Assertions.assertNull(APIAccessManager.getTootId("", ""));
        Assertions.assertNull(APIAccessManager.getTootId(null, token));
        Assertions.assertNull(APIAccessManager.getTootId("", token));
        Assertions.assertNull(APIAccessManager.getTootId(idresult, null));
        Assertions.assertNull(APIAccessManager.getTootId(idresult, ""));
        Assertions.assertTrue(APIAccessManager.getTootId(idresult, token).get(0).equals(new Toot("110312493653364514")));
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
    public void testGetAccount(){
        Assertions.assertNull(APIAccessManager.getAccount(null, null));
        Assertions.assertNull(APIAccessManager.getAccount("", null));
        Assertions.assertNull(APIAccessManager.getAccount(null, ""));
        Assertions.assertNull(APIAccessManager.getAccount("", ""));
        Assertions.assertNull(APIAccessManager.getAccount(null, token));
        Assertions.assertNull(APIAccessManager.getAccount("", token));
        Assertions.assertNull(APIAccessManager.getAccount(idresult, null));
        Assertions.assertNull(APIAccessManager.getAccount(idresult, ""));
        Assertions.assertTrue(APIAccessManager.getAccount(idresult, token).equals(logAccount));
    }

    @Test
    public void testPerformSearch(){
        Assertions.assertNull(APIAccessManager.performSearch(null, null, 4));
        Assertions.assertNull(APIAccessManager.performSearch("", null, 4));
        Assertions.assertNull(APIAccessManager.performSearch(null, "", 4));
        Assertions.assertNull(APIAccessManager.performSearch("", "", 4));
        Assertions.assertNull(APIAccessManager.performSearch(null, token, 4));

        //No results
        SearchResult sr1 = APIAccessManager.performSearch("upvehu", token, 4);
        Assertions.assertEquals(sr1.accounts, new ArrayList<>());
        Assertions.assertEquals(sr1.statuses, new ArrayList<>());

        //Only appears one account
        SearchResult sr2 = APIAccessManager.performSearch("Namtium", token, 4);
        //@Namtium
        Assertions.assertTrue(sr2.accounts.get(0).equals(new Follow("109897214707125498")));
        //No toots found
        Assertions.assertEquals(sr2.statuses,new ArrayList<>());

        //4 accounts and one toot
        SearchResult sr3 = APIAccessManager.performSearch("toot", token, 4);
        //@xiiomaraxc's toot
        Assertions.assertTrue(sr3.statuses.get(0).equals(new Toot("110316393639220016")));
        //@tootsdk@iosdev.space
        Assertions.assertTrue(sr3.accounts.get(0).equals(new Follow("109526249052533979")));
        //@twit_terrorist@mastodon.cat
        Assertions.assertTrue(sr3.accounts.get(1).equals(new Follow("1061473")));
        //@toot_your_own_adventure@clar.ke
        Assertions.assertTrue(sr3.accounts.get(2).equals(new Follow("110073888022807394")));
        //@tootapp
        Assertions.assertTrue(sr3.accounts.get(3).equals(new Follow("420049")));
    }

    @Test
    public void testGetIdFromUsername(){
        Assertions.assertNull(APIAccessManager.getIdFromUsername(null));
        Assertions.assertNull(APIAccessManager.getIdFromUsername(""));
        Assertions.assertEquals(idresult, APIAccessManager.getIdFromUsername("txipirones"));
    }

    @Test
    public void testGetLikedToots(){
        Assertions.assertNull(APIAccessManager.getLikedToots(null));
        Assertions.assertNull(APIAccessManager.getLikedToots(""));
        Assertions.assertTrue(toot.equals(APIAccessManager.getLikedToots(token).get(0)));
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
    public void testGetBookmarkedToots(){
        Assertions.assertNull(APIAccessManager.getBookmarkedToots(null));
        Assertions.assertNull(APIAccessManager.getBookmarkedToots(""));
        Assertions.assertTrue(toot.equals(APIAccessManager.getBookmarkedToots(token).get(0)));
    }

    @Test
    public void testBookmarkToot(){
        Assertions.assertEquals(APIAccessManager.bookmarkToot(null, null), 401);
        Assertions.assertEquals(APIAccessManager.bookmarkToot(tootId, null), 401);
        Assertions.assertEquals(APIAccessManager.bookmarkToot(null, token), 404);
        Assertions.assertEquals(APIAccessManager.bookmarkToot(tootId, ""), 401);
        Assertions.assertEquals(APIAccessManager.bookmarkToot("", token), 404);
        Assertions.assertEquals(APIAccessManager.bookmarkToot("", ""), 404);
        Assertions.assertEquals(APIAccessManager.bookmarkToot(inexistentId, token), 404);
        Assertions.assertEquals(APIAccessManager.bookmarkToot(tootId, token),200);
    }

    @Test
    public void testUnbookmarkToot(){
        Assertions.assertEquals(APIAccessManager.unbookmarkToot(null, null), 401);
        Assertions.assertEquals(APIAccessManager.unbookmarkToot(untootId, null), 401);
        Assertions.assertEquals(APIAccessManager.unbookmarkToot(null, token), 404);
        Assertions.assertEquals(APIAccessManager.unbookmarkToot(untootId, ""), 401);
        Assertions.assertEquals(APIAccessManager.unbookmarkToot("", token), 404);
        Assertions.assertEquals(APIAccessManager.unbookmarkToot("", ""), 404);
        Assertions.assertEquals(APIAccessManager.unbookmarkToot(inexistentId, token), 404);
        Assertions.assertEquals(APIAccessManager.unbookmarkToot(untootId, token), 200);
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
