package eus.ehu.txipironesmastodonfx.data_access;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class APIAccessManagerTest {

    @Test
    public void checkIfCorrectTkn() {
        // Read-only token from @nagr
        String token = "QjkejVq_kjyeqY3D3ZyIUFuTtL8Dj1vPRcTA012KBRI";
        Assertions.assertNull(APIAccessManager.verifyAndGetId(null));
        Assertions.assertNull(APIAccessManager.verifyAndGetId(""));
        String idresult = "109897228835942792";
        Assertions.assertEquals(idresult, APIAccessManager.verifyAndGetId(token));
    }

    @Test
    public void getTootIdCheck() {
        // Read-only token from @nagr
        String token = "QjkejVq_kjyeqY3D3ZyIUFuTtL8Dj1vPRcTA012KBRI";
        // Movidote, método mal hecho, hay que tratar las excepciones (Cuando param son null)
    }


}
