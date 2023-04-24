package eus.ehu.txipironesmastodonfx.data_access;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class APIAccessManagerTest {

    @Test
    public void checkIfCorrectTkn() {
        Assertions.assertNull(APIAccessManager.verifyAndGetId(null));
        Assertions.assertNull(APIAccessManager.verifyAndGetId(""));
        // Read-only token from @nagr
        String tkn = "QjkejVq_kjyeqY3D3ZyIUFuTtL8Dj1vPRcTA012KBRI";
        String idresult = "109897228835942792";
        Assertions.assertEquals(idresult, APIAccessManager.verifyAndGetId(tkn));
    }

}
