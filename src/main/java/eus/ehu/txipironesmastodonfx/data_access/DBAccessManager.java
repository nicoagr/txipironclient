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

    public final static String dbName = "TxipironData.db";
    private final static File dbPointer = new File(System.getProperty("user.home"), getConfigDir() + File.separator + dbName);

    /**
     * Returns the configuration directory
     * for the database. Will take into account
     * the three different OSes.
     *
     * @return (String) - The configuration directory
     */
    private static String getConfigDir() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("mac"))
            return "Library" + File.separator + "Application Support" + File.separator + "TxipironClient";
        else if (os.contains("linux") || os.contains("nux")) return ".config" + File.separator + "TxipironClient";
        else if (os.contains("win")) return "AppData" + File.separator + "Roaming" + File.separator + "TxipironClient";
        return null;
    }

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
        File dir = new File(System.getProperty("user.home"), getConfigDir());
        if (!dir.exists()) {
            dir.mkdir();
        }
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
                 mstdtoken VARCHAR(255) NOT NULL,
                 id VARCHAR(255) NOT NULL,
                 acct VARCHAR(255) NOT NULL,
                 avatar VARCHAR(1024) NOT NULL,
                 CONSTRAINT accounts_tk UNIQUE (mstdtoken)
                 );
                """;
        String setsql = """
                CREATE TABLE IF NOT EXISTS clientsettings (
                mainrow INTEGER PRIMARY KEY,
                autoplaymedia BOOLEAN NOT NULL
                );
                """;
        String defaultsql = "INSERT OR IGNORE INTO clientsettings (mainrow, autoplaymedia) VALUES (1, 1);";
        // Execute query
        executeQuery(accsql, null);
        executeQuery(setsql, null);
        executeQuery(defaultsql, null);

    }

    /**
     * Returns all accounts contained in the database.
     *
     * @return (List < Account >) - The list of accounts
     *
     * @throws SQLException - If the query fails to execute
     */
    public static List<Account> getAccounts() throws SQLException {
        List<Account> accounts = new ArrayList<>();
        CachedRowSet rs = executeQuery("SELECT * FROM accounts", null);
        while (rs.next()) {
            Account acc = new Account(rs.getString("id"), rs.getString("acct"), rs.getString("avatar"));
            accounts.add(acc);
        }
        return accounts;
    }

    /**
     * This method will set the given settings
     * in the database.
     *
     * @param autoplaymedia (boolean) - The value of the setting
     * @throws SQLException - If the query fails to execute
     */
    public static void updateSettings(boolean autoplaymedia) throws SQLException {
        executeQuery("UPDATE clientsettings SET autoplaymedia = ?", List.of(autoplaymedia));
    }

    /**
     * Checks if the account is in the db.
     * We can be sure that the parameter (id) is safe
     * because there will not be two mastodon accounts
     * with the same id. (in the same server)
     *
     * @param id (String) - The id of the account
     * @return (boolean) - True if the account is in the db, false otherwise
     *
     * @throws SQLException - If the query fails to execute
     */
    public static boolean isAccountInDb(String id) throws SQLException {
        CachedRowSet rs = executeQuery("SELECT id FROM accounts WHERE id = ?", List.of(id));
        while (rs.next()) {
            if (rs.getString("id").equals(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes an account from the database.
     *
     * @param id (String) - The id of the account to remove
     *
     * @throws SQLException - If the query fails to execute
     */
    public static void removeAccountFromDbId(String id) throws SQLException {
        executeQuery("DELETE FROM accounts WHERE id = ?", List.of(id));
    }

    /**
     * Gets the ref associated with an account.
     *
     * @param AccId (String) - The id of the account
     * @return (String) - The ref associated with the account
     * @throws SQLException - If the query fails to execute
     */
    public static List<Object> getRefTokenFromId(String AccId) throws SQLException {
        ResultSet rs = executeQuery("SELECT ref, mstdtoken FROM accounts WHERE id = ?", List.of(AccId));
        Integer ref = null;
        String token = null;
        while (rs.next()) {
            ref = rs.getInt("ref");
            token = rs.getString("mstdtoken");
        }
        return (token == null || ref == null) ? null : List.of(ref, token, AccId);
    }

    /**
     * This method will add an account to the database.
     * It will first fetch the account data from the api,
     * then it will insert it in the database and finally if
     * the insertion went well it will set the destination sys variable.
     *
     * @param id                (String) - The id of the account
     * @param token             (String) - The mastodon access token of the account
     *
     * @throws SQLException                  - If the query fails to execute
     */
    public static void addAccount(String id, String token) throws SQLException {
        // Get Account Data from api
        Account acc = APIAccessManager.getAccount(id, token);
        // Insert the account in the database
        if (acc != null)
            executeQuery("INSERT INTO accounts (mstdtoken, id, acct, avatar) VALUES (?, ?, ?, ?)", List.of(token, acc.id, acc.acct, acc.avatar)); // set destination sys variable into system
    }

    /**
     * Generic Method to execute an SQL query in the database.
     *
     * @param query  (String) - The query to execute
     * @param params (List<Object>) - The parameters to substitute in the query
     * @return CachedRowSet - The result of the query, null if there is no result
     *
     * @throws SQLException - If the query returns some error
     */
    private static CachedRowSet executeQuery(String query, List<Object> params) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPointer.getPath());
        // Create a PreparedStatement with the query and set the parameters values
        PreparedStatement stmt = conn.prepareStatement(query);
        // if we have parameters to substitute, we do it
        if (params != null) {
            for (Object o : params) {
                // please note smt.set indexes start at 1 and not at 0
                if (o instanceof String) {
                    stmt.setString(params.indexOf(o) + 1, (String) o);
                } else if (o instanceof Integer) {
                    stmt.setInt(params.indexOf(o) + 1, (Integer) o);
                } else if (o instanceof Boolean) {
                    stmt.setBoolean(params.indexOf(o) + 1, (Boolean) o);
                } else if (o instanceof Long) {
                    stmt.setLong(params.indexOf(o) + 1, (Long) o);
                } else if (o instanceof Double) {
                    stmt.setDouble(params.indexOf(o) + 1, (Double) o);
                } else if (o instanceof Float) {
                    stmt.setFloat(params.indexOf(o) + 1, (Float) o);
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

    /**
     * Method to get the list of followers of an account.
     *
     * @param ref (int) - The ref of the account
     * @return List<Follow> - The list of followers of the account
     *
     * @throws SQLException - If the query fails to execute
     */
    public static String getUserAvatar(Integer ref) throws SQLException {

        CachedRowSet rs = executeQuery("SELECT avatar FROM accounts WHERE ref = ?;", List.of(ref));

        if(rs.next()) {
            return rs.getString("avatar");
        }
        return null;

    }

    /**
     * This method will return the config
     * settings of the client for the local user
     *
     * @return (List < Object >) - The settings
     */
    public static List<Object> getSettings() throws SQLException {
        ResultSet rs = executeQuery("SELECT * FROM clientsettings", null);
        List<Object> list = new ArrayList<>();
        // we use if and not while because we'll only have 1 row
        if (rs.next()) {
            list.add(rs.getBoolean("autoplaymedia"));
        }
        return (list.size() != 0) ? list : null;
    }
}