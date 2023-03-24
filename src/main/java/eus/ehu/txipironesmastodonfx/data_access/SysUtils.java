package eus.ehu.txipironesmastodonfx.data_access;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
     * Checks if a system variable is used.
     * This method will call a terminal on Windows or a shell on Linux/Mac
     * Then it will check if the variable is used executing the appropiate commands
     * on both operating systems. This is done this way because System.getEnv()
     * doesn't update dynamically and relies on restarting the JVM.
     *
     * @param var The system variable to check
     * @return boolean - True if the system variable is used, false otherwise
     */
    public static boolean isSysVariableUsed(String var) {
        String os = System.getProperty("os.name").toLowerCase();
        String cmd;
        if (os.contains("win")) {
            cmd = "cmd.exe /c set " + var;
        } else if (os.contains("mac") || os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            cmd = "echo $" + var;
        } else {
            throw new UnsupportedOperationException("Unsupported operating system: " + os);
        }

        try {
            ProcessBuilder builder = new ProcessBuilder(cmd.split("\\s"));
            Process process = builder.start();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (os.contains("win")) {
                    if (line.contains("=")) {
                        String[] parts = line.split("=", 2);
                        String name = parts[0].trim();
                        if (name.equalsIgnoreCase(var)) {
                            return true;
                        }
                    }
                } else {
                    if (line.equals(var)) {
                        return true;
                    }
                }
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Gets the next free system variable.
     *
     * @param prefix The prefix of the system variable
     * @return String - The next free system variable
     */
    public static String getNextFreeSysVar(String prefix) {
        // I think 200 is a big enough value.
        // (I hope no one will have 200 accounts in the same computer)
        for (int i = 0; i < 200; i++) {
            if (!isSysVariableUsed(prefix + i)) {
                return prefix + i;
            }
        }
        return null;
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
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "set " + destinationSysVar + "=" + token);
            pb.start();
        } else if (os.contains("mac") || os.contains("nix") || os.contains("nux") || os.contains("aix")) {
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
            // Delete environment variable on Windows
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "set " + sysVarFromDbId + "=");
            try {
                pb.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (os.contains("mac") || os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            // Delete environment variable on Linux or macOS
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
