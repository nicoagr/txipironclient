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

public class APIAccessManagerTest {
    String token, idresult, blockId, tootId, untootId, inexistentId;
    Toot toot;
    Account logAccount;
    Follow follow, haizeaId;

    @BeforeEach
    public void setUp() {
        //@txipirones
        token = "Wn9h3I8mUcEuSwS3LUptIO0aVwK79cdxjrklY0iRxrQ";
        idresult = "110294352117577191";
        logAccount = new Account("110294352117577191");
        blockId = "109897300953116571";
        //@juananpe
        follow = new Follow ("109842111446764244");
        //@Namtium
        haizeaId = new Follow("109897214707125498");
        //@xiiomaraxc's toot which appears in the search
        //searchToot = "110134917103846112";
        //@Namtium's toot which is favourited, bookmarked and boosted
        tootId = "110134917103846112";
        untootId = "110134917103846112";
        inexistentId = "1234";
        toot = new Toot("110134917103846112");


    }

    @Test
    public void testVerifyAndGetId() {
        // Read-only token from @nagr
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
        Assertions.assertTrue(APIAccessManager.getFollow(idresult, token, false).get(0).equals(follow));

        //following
        Assertions.assertNull(APIAccessManager.getFollow(null, null, true));
        Assertions.assertNull(APIAccessManager.getFollow("", null, true));
        Assertions.assertNull(APIAccessManager.getFollow(null, "", true));
        Assertions.assertNull(APIAccessManager.getFollow("", "", true));
        Assertions.assertNull(APIAccessManager.getFollow(null, token, true));
        Assertions.assertNull(APIAccessManager.getFollow("", token, true));
        Assertions.assertNull(APIAccessManager.getFollow(idresult, null, true));
        Assertions.assertNull(APIAccessManager.getFollow(idresult, "", true));
        Assertions.assertTrue(APIAccessManager.getFollow(idresult, token, true).get(0).equals(follow));
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
        //Only appears one account
        SearchResult sr = APIAccessManager.performSearch("Namtium", token, 4);
        Assertions.assertTrue(haizeaId.equals(sr.accounts.get(0)));
        Assertions.assertEquals(sr.statuses,new ArrayList<>());
        //4 accounts and one toot
        SearchResult sr2 = APIAccessManager.performSearch("toot", token, 4);
        Assertions.assertTrue(sr2.statuses.get(0).equals(new Toot("110316393639220016")));
        Assertions.assertTrue(sr2.accounts.get(0).equals(new Follow("109526249052533979")));
        Assertions.assertTrue(sr2.accounts.get(1).equals(new Follow("1061473")));
        Assertions.assertTrue(sr2.accounts.get(2).equals(new Follow("110073888022807394")));
        Assertions.assertTrue(sr2.accounts.get(3).equals(new Follow("420049")));
    }

    @Test
    public void testPostToot(){

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
        //Assertions.assertEquals(APIAccessManager.favouriteToot(untootId, token), 200);
        Assertions.assertEquals(APIAccessManager.favouriteToot(tootId, token),200);
    }

    @Test
    public void testUnfavouriteToot(){
        Assertions.assertEquals(APIAccessManager.unfavouriteToot(null, null), 401);
        Assertions.assertEquals(APIAccessManager.unfavouriteToot(tootId, null), 401);
        Assertions.assertEquals(APIAccessManager.unfavouriteToot(null, token), 404);
        Assertions.assertEquals(APIAccessManager.unfavouriteToot(tootId, ""), 401);
        Assertions.assertEquals(APIAccessManager.unfavouriteToot("", token), 404);
        Assertions.assertEquals(APIAccessManager.unfavouriteToot("", ""), 404);
        Assertions.assertEquals(APIAccessManager.unfavouriteToot(inexistentId, token), 404);
        Assertions.assertEquals(APIAccessManager.unfavouriteToot(untootId, token), 200);
        //Assertions.assertEquals(APIAccessManager.unfavouriteToot(tootId, token),200);
    }

    @Test
    public void testGetHomeTootsId(){

    }

    @Test
    public void testFollow(){
        Assertions.assertNull(APIAccessManager.follow(null, null));
        Assertions.assertNull(APIAccessManager.follow("", null));
        Assertions.assertNull(APIAccessManager.follow(null, ""));
        Assertions.assertNull(APIAccessManager.follow("", ""));
        //Assertions.assertNull(APIAccessManager.follow(null, accountId));
        //Assertions.assertNull(APIAccessManager.follow("", accountId));
        //Assertions.assertNull(APIAccessManager.follow(token, null));
        //Assertions.assertNull(APIAccessManager.follow(token, ""));
        Assertions.assertEquals(APIAccessManager.follow(token, blockId), blockId);
    }

    @Test
    public void testUnfollow(){

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
        //Assertions.assertEquals(APIAccessManager.bookmarkToot(untootId, token), 200);
        Assertions.assertEquals(APIAccessManager.bookmarkToot(tootId, token),200);
    }

    @Test
    public void testUnbookmarkToot(){
        Assertions.assertEquals(APIAccessManager.unbookmarkToot(null, null), 401);
        Assertions.assertEquals(APIAccessManager.unbookmarkToot(tootId, null), 401);
        Assertions.assertEquals(APIAccessManager.unbookmarkToot(null, token), 404);
        Assertions.assertEquals(APIAccessManager.unbookmarkToot(tootId, ""), 401);
        Assertions.assertEquals(APIAccessManager.unbookmarkToot("", token), 404);
        Assertions.assertEquals(APIAccessManager.unbookmarkToot("", ""), 404);
        Assertions.assertEquals(APIAccessManager.unbookmarkToot(inexistentId, token), 404);
        Assertions.assertEquals(APIAccessManager.unbookmarkToot(untootId, token), 200);
        //Assertions.assertEquals(APIAccessManager.unbookmarkToot(tootId, token),200);
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
        //Assertions.assertEquals(APIAccessManager.reblogToot(untootId, token), 200);
        Assertions.assertEquals(APIAccessManager.reblogToot(tootId, token),200);
    }

    @Test
    public void testUnreblogToot(){
        Assertions.assertEquals(APIAccessManager.unreblogToot(null, null), 401);
        Assertions.assertEquals(APIAccessManager.unreblogToot(tootId, null), 401);
        Assertions.assertEquals(APIAccessManager.unreblogToot(null, token), 404);
        Assertions.assertEquals(APIAccessManager.unreblogToot(tootId, ""), 401);
        Assertions.assertEquals(APIAccessManager.unreblogToot("", token), 404);
        Assertions.assertEquals(APIAccessManager.unreblogToot("", ""), 404);
        Assertions.assertEquals(APIAccessManager.unreblogToot(inexistentId, token), 404);
        Assertions.assertEquals(APIAccessManager.unreblogToot(untootId, token), 200);
        //Assertions.assertEquals(APIAccessManager.unreblogToot(tootId, token),200);
    }

    @Test
    public void testUploadMedia(){

    }

    @Test
    public void testChangeProfilePicture(){

    }

    @Test
    public void testIsMediaProcessed(){
        Assertions.assertFalse(APIAccessManager.isMediaProcessed(null, null));
        Assertions.assertFalse(APIAccessManager.isMediaProcessed("", null));
        Assertions.assertFalse(APIAccessManager.isMediaProcessed(null, ""));
        Assertions.assertFalse(APIAccessManager.isMediaProcessed("", ""));
        Assertions.assertFalse(APIAccessManager.isMediaProcessed(token, null));
        Assertions.assertFalse(APIAccessManager.isMediaProcessed(token, ""));
    }
}
