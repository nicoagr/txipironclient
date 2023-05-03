package eus.ehu.txipironesmastodonfx.data_access;

import eus.ehu.txipironesmastodonfx.domain.Toot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class APIAccessManagerTest {
    String token, idresult, tootId, untootId, inexistentId;
    Toot toot;

    @BeforeEach
    public void setUp() {
        token = "KlLyZVeg68XFYfH9Yp2WkFwr88dsp8dj3FopzRC2fNE";
        idresult = "109897228835942792";
        tootId = "110250228437175158";
        untootId = "110134917103846112";
        inexistentId = "1234";
        //toot = new Toot();
    }

    @Test
    public void testVerifyAndGetId() {
        // Read-only token from @nagr
        Assertions.assertNull(APIAccessManager.verifyAndGetId(null));
        Assertions.assertNull(APIAccessManager.verifyAndGetId(""));
        Assertions.assertEquals(idresult, APIAccessManager.verifyAndGetId(token));
    }

    @Test
    public void testGetTokenFromAuthCode() {
        // Read-only token from @nagr
        // Movidote, metodo mal hecho, hay que tratar las excepciones (Cuando param son null)
    }

    @Test
    public void testGetTootId(){
        Assertions.assertNull(APIAccessManager.getTootId(null, null));
        Assertions.assertNull(APIAccessManager.getTootId("", null));
        Assertions.assertNull(APIAccessManager.getTootId(null, ""));
        Assertions.assertNull(APIAccessManager.getTootId("", ""));
        Assertions.assertNull(APIAccessManager.getTootId(null, token));
        Assertions.assertNull(APIAccessManager.getTootId("", token));
        //Assertions.assertNull(APIAccessManager.getTootId(idresult, null));
        //Assertions.assertNull(APIAccessManager.getTootId(idresult, ""));
        //Assertions.assertNull(APIAccessManager.getTootId(idresult, token));
    }

    @Test
    public void testGetFollow(){

    }

    @Test
    public void testGetAccount(){

    }

    @Test
    public void testPerformSearch(){

    }

    @Test
    public void testRequestWithParams(){

    }

    @Test
    public void testRequest(){

    }

    @Test
    public void testRequestNoToken(){

    }

    @Test
    public void testPostToot(){

    }

    @Test
    public void testGetIdFromUsername(){
        //Assertions.assertNull(APIAccessManager.getIdFromUsername(null));
        Assertions.assertNull(APIAccessManager.getIdFromUsername(""));
        Assertions.assertEquals(idresult, APIAccessManager.getIdFromUsername("nagr"));
    }

    @Test
    public void testGetLikedToots(){

    }

    @Test
    public void testFavouriteToot(){
        Assertions.assertEquals(APIAccessManager.favouriteToot(null, null), 401);
        Assertions.assertEquals(APIAccessManager.favouriteToot(tootId, null), 401);
        Assertions.assertEquals(APIAccessManager.favouriteToot(null, token), 403);
        Assertions.assertEquals(APIAccessManager.favouriteToot(tootId, ""), 401);
        Assertions.assertEquals(APIAccessManager.favouriteToot("", token), 404);
        Assertions.assertEquals(APIAccessManager.favouriteToot("", ""), 404);
        Assertions.assertEquals(APIAccessManager.favouriteToot(inexistentId, token), 403);
        Assertions.assertEquals(APIAccessManager.favouriteToot(untootId, token), 403);
        Assertions.assertEquals(APIAccessManager.favouriteToot(tootId, token),200);
    }

    @Test
    public void testUnfavouriteToot(){

    }

    @Test
    public void testGetHomeTootsId(){

    }

    @Test
    public void testFollow(){

    }

    @Test
    public void testUnfollow(){

    }

    @Test
    public void testGetBookmarkedToots(){

    }

    @Test
    public void testBookmarkToot(){

    }

    @Test
    public void testUnbookmarkToot(){

    }

    @Test
    public void testReblogToot(){

    }

    @Test
    public void testUnreblogToot(){

    }

    @Test
    public void testUploadMedia(){

    }

    @Test
    public void testChangeProfilePicture(){

    }

    @Test
    public void testIsMediaProcessed(){

    }
}
