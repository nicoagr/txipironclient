package eus.ehu.txipironesmastodonfx.data_access;

import java.io.IOException;

/**
 * This class contains some useful methods for system variables
 * It is used to store the tokens in the system variables
 *
 * @author Nicolás Aguado
 * @author Haizea Bermejo
 * @author Marcos Chouciño
 * @author Xiomara Cáceces
 */
public class SysUtils {

    /**
     * Checks if a system variable is used
     * @param var The system variable to check
     * @return boolean - True if the system variable is used, false otherwise
     */
    public static boolean isSysVariableUsed(String var) {
        return System.getenv(var) != null;
    }

    /**
     * Gets the next free system variable
     * @param prefix The prefix of the system variable
     * @return String - The next free system variable
     */
    public static String getNextFreeSysVar(String prefix) {
        int i = 0;
        while (isSysVariableUsed(prefix + i)) {
            i++;
        }
        return prefix + i;
    }

    /**
     * Sets a system variable
     * @param destinationSysVar The system variable to set
     * @param token The token to set
     * @throws IOException If the system variable cannot be set
     * @throws UnsupportedOperationException If the operating system is not supported
     */
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

    /**
     * Removes a system variable
     * @param sysVarFromDbId The system variable to remove
     */
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
