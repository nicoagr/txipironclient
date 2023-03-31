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
                mstdtoken VARCHAR(255) NOT NULL,
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
                CONSTRAINT accounts_tk UNIQUE (mstdtoken)
                );""";
        // Execute query
        executeQuery(accsql, null);
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
            Account acc = new Account(rs.getString("id"), rs.getString("acct"), rs.getString("avatar"), rs.getString("header"), rs.getInt("statuses_count"), rs.getInt("followers_count"), rs.getInt("following_count"), rs.getString("note"), rs.getString("last_status_at"), rs.getString("display_name"));
            accounts.add(acc);
        }
        return accounts;
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
            executeQuery("INSERT INTO accounts (mstdtoken, id, acct, avatar, header, display_name, statuses_count, followers_count, following_count, note, last_status_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", List.of(token, acc.id, acc.acct, acc.avatar, acc.header, acc.display_name, acc.statuses_count, acc.followers_count, acc.following_count, acc.note, acc.last_status_at));        // set destination sys variable into system
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
     * Method to get user account from a reference
     *
     * @param ref (Integer) - The reference of the account
     * @return (Account) - The account
     * @throws SQLException - If the query fails to execute
     */
    public Account getUserAccount(Integer ref) throws SQLException {
        CachedRowSet rs = executeQuery("SELECT * FROM accounts WHERE ref = ?;", List.of(ref));
        if(rs.next()) {
            Account a = new Account();
            a.acct = rs.getString("acct");
            a.avatar = rs.getString("avatar");
            a.id = rs.getString("id");
            a.header= rs.getString("header");
            a.display_name = rs.getString("display_name");
            a.statuses_count = rs.getInt("statuses_count");
            a.followers_count = rs.getInt("followers_count");
            a.following_count = rs.getInt("following_count");
            a.note = rs.getString("note");
            a.last_status_at = rs.getString("last_status_at");
            return a;
        }
        return null;
    }
}