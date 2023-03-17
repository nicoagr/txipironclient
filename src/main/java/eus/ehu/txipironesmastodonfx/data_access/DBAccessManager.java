package eus.ehu.txipironesmastodonfx.data_access;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * This class is responsible for managing access to the database.
 * Everything from creating, inserting and extracting the data will be
 * controlled by this class.
 *
 * @author Nicolás Aguado
 * @author Haizea Bermejo
 * @author Marcos Chouciño
 * @author Xiomara Cáceces
 */
public class DBAccessManager {

    // TODO- Code smell: Hard-Coded value
    public final static String dbName = "TxipironData.db";
    private final static File dbPointer = new File(dbName);

    /**
     * This method will check if the database is reachable.
     * It will check if the database file exists and if it can be opened.
     *
     * @return boolean - True if the database is reachable, false otherwise
     */
    public static boolean isDbReachable() {
        return dbPointer.exists();
    }

    /**
     * This method will create the database file.
     * If it exists, it will overwrite it.
     * SO BE CAREFUL WHEN CALLING!
     *
     * @throws IOException - If the file can't be created
     */
    public static void createDbFile() throws IOException {
        dbPointer.createNewFile();
    }

    public static void createTables() throws SQLException {

    }

    public static boolean checkTables() {
        return true;
    }
}
