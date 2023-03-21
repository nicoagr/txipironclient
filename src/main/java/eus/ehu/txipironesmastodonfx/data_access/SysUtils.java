package eus.ehu.txipironesmastodonfx.data_access;

import java.io.IOException;

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

    public static void setSysVariable(String destinationSysVar, String token) throws IOException, UnsupportedOperationException {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            // Set environment variable on Windows
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "setx " + destinationSysVar + " " + token);
            pb.start();
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            // Set environment variable on Linux or macOS
            ProcessBuilder pb = new ProcessBuilder("sh", "-c", "export " + destinationSysVar + "=" + token);
            pb.start();
        } else {
            // Unsupported operating system
            throw new UnsupportedOperationException("Unsupported operating system: " + os);
        }
    }

    public static void removeSysVariable(String sysVarFromDbId) {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            // Set environment variable on Windows
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "REG delete HKCU\\Environment /F /V " + sysVarFromDbId);
            try {
                pb.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            // Set environment variable on Linux or macOS
            ProcessBuilder pb = new ProcessBuilder("sh", "-c", "unset " + sysVarFromDbId);
            try {
                pb.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Unsupported operating system
            throw new UnsupportedOperationException("Unsupported operating system: " + os);
        }
    }
}
