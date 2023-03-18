package eus.ehu.txipironesmastodonfx.data_access;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.List;

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
     * @return boolean - True if the file was created, false if it already existed
     * @throws IOException - If the file can't be created
     */
    public static boolean createDbFile() throws IOException {
        return dbPointer.createNewFile();
    }


    public static void checkAndCreateTables() throws SQLException {
        // If we have a INTEGER PRIMARY KEY,
        // in sqllite it will auto increment.
        // see https://stackoverflow.com/a/7906029
        String accsql = """
                CREATE TABLE IF NOT EXISTS accounts (
                ref INT NOT NULL,
                svarname VARCHAR(255) NOT NULL,
                id VARCHAR(255) NOT NULL,
                acct VARCHAR(255) NOT NULL,
                avatar VARCHAR(1024) NOT NULL,
                header VARCHAR(1024),
                display_name VARCHAR(255),
                statuses_count INT,
                followers_count INT,
                following_count INT,
                note VARCHAR(2048),
                last_status_at VARCHAR(255),
                CONSTRAINT acc_pk_userid PRIMARY KEY (ref)
                );""";
        String tootsql = """
                CREATE TABLE IF NOT EXISTS toots (
                  ref INT NOT NULL,
                  id VARCHAR(255) NOT NULL,
                  created_at VARCHAR(255),
                  in_reply_to_id VARCHAR(255),
                  sensitive BOOLEAN,
                  uri VARCHAR(1024),
                  replies_count INT,
                  reblogs_count INT,
                  favourites_count INT,
                  favourited BOOLEAN,
                  reblogged BOOLEAN,
                  content VARCHAR(2048),
                  account_id VARCHAR(255),
                  reblog_id INT,
                  CONSTRAINT toots_pk_id PRIMARY KEY (id),
                  CONSTRAINT toots_fk_ref_id FOREIGN KEY (ref) REFERENCES account(ref) ON DELETE CASCADE ON UPDATE CASCADE,
                  CONSTRAINT toots_fk_reblog_id FOREIGN KEY (reblog_id) REFERENCES toots(id) ON DELETE CASCADE ON UPDATE CASCADE
                );""";
        String followersql = """
                CREATE TABLE IF NOT EXISTS follower (
                  ref INT NOT NULL,
                  id VARCHAR(255) NOT NULL,
                  acct VARCHAR(255) NOT NULL,
                  avatar VARCHAR(1024) NOT NULL,
                  CONSTRAINT toots_fk_ref_id FOREIGN KEY (ref) REFERENCES account(ref) ON DELETE CASCADE ON UPDATE CASCADE
                  );""";
        String followingsql = """
                CREATE TABLE IF NOT EXISTS following (
                  ref INT NOT NULL,
                  id VARCHAR(255) NOT NULL,
                  acct VARCHAR(255) NOT NULL,
                  avatar VARCHAR(1024) NOT NULL,
                  CONSTRAINT toots_fk_ref_id FOREIGN KEY (ref) REFERENCES account(ref) ON DELETE CASCADE ON UPDATE CASCADE
                  );""";
        // Execute querys
        executeQuery(accsql, null);
        executeQuery(tootsql, null);
        executeQuery(followersql, null);
        executeQuery(followingsql, null);
    }

    private static ResultSet executeQuery(String query, List<Object> params) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPointer.getPath());
        // Create a PreparedStatement with the query and set the parameters values
        PreparedStatement stmt = conn.prepareStatement(query);
        // if we have parameters to substitute, we do it
        if (params != null) {
            for (Object o : params) {
                if (o instanceof String) {
                    stmt.setString(params.indexOf(o), (String) o);
                } else if (o instanceof Integer) {
                    stmt.setInt(params.indexOf(o), (Integer) o);
                }
            }
        }
        // Execute the query (and process the results)
        boolean hasResultSet = stmt.execute();
        ResultSet rs = null;
        if (hasResultSet) {
            rs = stmt.getResultSet();
        }
        // Close resources
        stmt.close();
        conn.close();
        return rs;
    }
}
