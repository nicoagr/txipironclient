package eus.ehu.txipironesmastodonfx.data_access;

public class SysUtils {

    public static boolean isSysVariableUsed(String var) {
        return System.getenv(var) != null;
    }

    public static String getNextFreeSysVar(String prefix) {
        int i = 0;
        while (isSysVariableUsed(prefix + i)) {
            i++;
        }
        return prefix + i;
    }

}
