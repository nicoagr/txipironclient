package eus.ehu.txipironesmastodonfx.data_access;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class APIAccessManagerTest {
    String token, idresult;

    @BeforeEach
    public void setUp() {
        token = "QjkejVq_kjyeqY3D3ZyIUFuTtL8Dj1vPRcTA012KBRI";
        idresult = "109897228835942792";
    }

    @Test
    public void testVerifyAndGerId() {
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

    }

    @Test
    public void testGetLikedToots(){

    }

    @Test
    public void testFavouriteToot(){

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
