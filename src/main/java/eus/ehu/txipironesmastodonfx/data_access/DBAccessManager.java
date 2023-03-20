package eus.ehu.txipironesmastodonfx.data_access;

import eus.ehu.txipironesmastodonfx.domain.Account;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
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
     * @throws IOException - If the file can't be created
     */
    public static void createDbFile() throws IOException {
        dbPointer.createNewFile();
    }


    /**
     * This method will create the tables needed for the application.
     * If the tables already exist, it will send the queries,
     * but because its CREATE TABLE IF NOT EXIST practically
     * it will do nothing.
     *
     * @throws SQLException - If the queries can't be executed
     */
    public static void checkAndCreateTables() throws SQLException {
        // If we have a INTEGER PRIMARY KEY,
        // in sqllite it will auto increment.
        // see https://stackoverflow.com/a/7906029
        String accsql = """
                CREATE TABLE IF NOT EXISTS accounts (
                ref INTEGER PRIMARY KEY,
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
                last_status_at VARCHAR(255)
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

    /**
     * Returns all accounts contained in the database.
     *
     * @return (List < Account >) - The list of accounts
     * @throws SQLException - If the query fails to execute
     */
    public static List<Account> getAccounts() throws SQLException {
        List<Account> accounts = new ArrayList<>();
        CachedRowSet rs = executeQuery("SELECT * FROM accounts", null);
        while (rs.next()) {
            Account acc = new Account(rs.getString("id"), rs.getString("acct"), rs.getString("avatar"), rs.getString("header"), rs.getInt("statuses_count"), rs.getInt("followers_count"), rs.getInt("following_count"), rs.getString("note"), rs.getString("last_status_at"), rs.getString("display_name"));
            if (SysUtils.isSysVariableUsed(rs.getString("svarname"))) {
                accounts.add(acc);
            } else {
                // if we don't have the variable, we remove the account
                // from the database
                removeAccountFromDb(rs.getString("ref"));
            }
        }
        return accounts;
    }

    /**
     * Removes an account from the database.
     *
     * @param ref (String) - The reference of the account to remove
     */
    private static void removeAccountFromDb(String ref) throws SQLException {
        executeQuery("DELETE FROM accounts WHERE ref = ?", List.of(ref));
    }

    /**
     * Generic Method to execute an SQL query in the database.
     *
     * @param query  (String) - The query to execute
     * @param params (List<Object>) - The parameters to substitute in the query
     * @return CachedRowSet - The result of the query, null if there is no result
     * @throws SQLException - If the query returns some error
     */
    private static CachedRowSet executeQuery(String query, List<Object> params) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPointer.getPath());
        // Create a PreparedStatement with the query and set the parameters values
        PreparedStatement stmt = conn.prepareStatement(query);
        // if we have parameters to substitute, we do it
        if (params != null) {
            for (Object o : params) {
                if (o instanceof String) {
                    stmt.setString(params.indexOf(o) + 1, (String) o);
                } else if (o instanceof Integer) {
                    stmt.setInt(params.indexOf(o) + 1, (Integer) o);
                }
            }
        }
        // Execute the query (and process the results)
        boolean hasResultSet = stmt.execute();
        ResultSet rs;
        CachedRowSet crs = null;
        if (hasResultSet) {
            rs = stmt.getResultSet();
            crs = RowSetProvider.newFactory().createCachedRowSet();
            crs.populate(rs);
            rs.close();
        }
        // Close Resources
        conn.close();
        stmt.close();
        return crs;
    }
}
