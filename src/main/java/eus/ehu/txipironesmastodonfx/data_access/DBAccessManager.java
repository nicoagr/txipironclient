package eus.ehu.txipironesmastodonfx.data_access;

import eus.ehu.txipironesmastodonfx.domain.Account;
import eus.ehu.txipironesmastodonfx.domain.Follow;
import eus.ehu.txipironesmastodonfx.domain.Toot;
import javafx.scene.image.Image;

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
                  avatar VARCHAR(1024),
                  acct VARCHAR(255),
                  CONSTRAINT toots_fk_ref_id FOREIGN KEY (ref) REFERENCES account(ref) ON DELETE CASCADE ON UPDATE CASCADE
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
     */
    public static void removeAccountFromDbId(String id) throws SQLException {
        executeQuery("DELETE FROM accounts WHERE id = ?", List.of(id));
    }

    /**
     * Gets the system variable associated with an account.
     *
     * @param id (String) - The id of the account
     * @return (String) - The system variable associated with the account
     * @throws SQLException - If the query fails to execute
     */
    public static String getSysVarFromDbId(String id) throws SQLException {
        ResultSet rs = executeQuery("SELECT svarname FROM accounts WHERE id = ?", List.of(id));
        String svarname = null;
        while (rs.next()) {
            svarname = rs.getString("svarname");
        }
        return svarname;
    }

    /**
     * Gets the ref associated with an account.
     *
     * @param AccId (String) - The id of the account
     * @return (String) - The ref associated with the account
     * @throws SQLException - If the query fails to execute
     */
    public static Integer getRefFromId(String AccId) throws SQLException {
        ResultSet rs = executeQuery("SELECT ref FROM accounts WHERE id = ?", List.of(AccId));
        Integer ref = null;
        while (rs.next()) {
            ref = rs.getInt("ref");
        }
        return ref;
    }

    /**
     * Gets the system variable associated with an account.
     *
     * @param ref (String) - The ref of the account
     * @return (String) - The system variable associated with the account
     */
    public static String getSysVarFromRef(Integer ref) throws SQLException {
        ResultSet rs = executeQuery("SELECT svarname FROM accounts WHERE ref = ?", List.of(ref));
        String sysvar = null;
        while (rs.next()) {
            sysvar = rs.getString("svarname");
        }
        return sysvar;
    }

    /**
     * This generic method will be used to delete
     * entries with an specific ref in a specific table.
     *
     * @param ref       (String) - The ref of the account
     * @param tablename (String) - The name of the table
     */
    public static void deleteRefFromDb(Integer ref, String tablename) throws SQLException {
        executeQuery("DELETE FROM " + tablename + " WHERE ref = ?", List.of(ref));
    }

    /**
     * Method to insert a list of toots in the database.
     * It will have a ref parameter to know which account
     * the toots belong to.
     *
     * @param toots (List < Toot >) - The list of toots to insert
     * @param ref   (String) - The ref of the account
     */
    public static void insertTootsInDb(List<Toot> toots, Integer ref) throws SQLException {
        List<Object> params;
        for (Toot t : toots) {
            params = new ArrayList<>();
            // unflip the recursion stack
            while (t.reblog != null) {
                t = t.reblog;
                t.reblogged = true;
            }
            params.add(ref);
            params.add(t.id);
            params.add(t.created_at);
            params.add(t.in_reply_to_id);
            params.add(t.sensitive);
            params.add(t.uri);
            params.add(t.replies_count);
            params.add(t.reblogs_count);
            params.add(t.favourites_count);
            params.add(t.favourited);
            params.add(t.reblogged);
            params.add(t.content);
            params.add(t.account != null ? t.account.id : null);
            params.add(t.account.acct);
            params.add(t.account.avatar);
            executeQuery("INSERT INTO toots (ref, id, created_at, in_reply_to_id, sensitive, uri, replies_count, reblogs_count, favourites_count, favourited, reblogged, content, account_id, acct, avatar) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ? , ?, ?, ?, ?, ?, ?);", params);
        }
    }

    /**
     * Method to insert a list of follow in the database.
     * If the following parameter is true, it will insert the accounts
     * into the following table. If not, it will insert the accounts
     * into the follower table.
     *
     * @param followList (String) - The list of accounts to insert
     * @param ref        (String) - The ref of the account
     * @param following  (boolean) - True if the accounts are following the account, false otherwise
     */
    public static void insertFollowInDb(List<Follow> followList, Integer ref, boolean following) throws SQLException {
        String tablename = following ? "following" : "follower";
        for (Follow f : followList) {
            executeQuery("INSERT INTO " + tablename + " (ref, id, acct, avatar) VALUES (?, ?, ?, ?);", List.of(ref, f.id, f.acct, f.avatar));
        }
    }

    /**
     * This method will add an account to the database.
     * It will first fetch the account data from the api,
     * then it will insert it in the database and finally if
     * the insertion went well it will set the destination sys variable.
     *
     * @param destinationSysVar (String) - The destination sys variable
     * @param id                (String) - The id of the account
     * @param token             (String) - The mastodon access token of the account
     * @throws SQLException                  - If the query fails to execute
     * @throws IOException                   - If the setting of the system variable fails
     * @throws UnsupportedOperationException - If the operating system is unsupported (cannot set sysenv)
     */
    public static void addAccount(String destinationSysVar, String id, String token) throws SQLException, IOException, UnsupportedOperationException {
        // Get Account Data from api
        Account acc = APIAccessManager.getAccount(id, token);
        // Insert the account in the database
        executeQuery("INSERT INTO accounts (svarname, id, acct, avatar, header, display_name, statuses_count, followers_count, following_count, note, last_status_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", List.of(destinationSysVar, acc.id, acc.acct, acc.avatar, acc.header, acc.display_name, acc.statuses_count, acc.followers_count, acc.following_count, acc.note, acc.last_status_at));        // set destination sys variable into system
        SysUtils.setSysVariable(destinationSysVar, token);
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
     * Method to get the list of toots of an account.
     * @param ref (int) - The ref of the account
     * @return List<Toot> - The list of toots of the account
     */
    public List<Toot> getUserToots(int ref) throws SQLException {

        List<Toot> toots = new ArrayList<>();
        CachedRowSet rs = executeQuery("SELECT * FROM toots WHERE ref = ?;", List.of(ref));

        while(rs.next()){
            Toot t = new Toot();
            t.id = rs.getString("id");
            t.created_at = rs.getString("created_at");
            t.in_reply_to_id = rs.getString("in_reply_to_id");
            t.sensitive = rs.getBoolean("sensitive");
            t.uri = rs.getString("uri");
            t.replies_count = rs.getInt("replies_count");
            t.reblogs_count = rs.getInt("reblogs_count");
            t.favourites_count = rs.getInt("favourites_count");
            t.favourited = rs.getBoolean("favourited");
            t.reblogged = rs.getBoolean("reblogged");
            t.content = rs.getString("content");
            t.account.acct = rs.getString("acct");
            t.account.avatar = rs.getString("avatar");
            t.account.id = rs.getString("account_id");
            toots.add(t);
        }




        return toots;
    }

    /**
     * Method to get the list of followers of an account.
     * @param ref (int) - The ref of the account
     * @return List<Follow> - The list of followers of the account
     */
    public String getUserAvatar(Integer ref) throws SQLException {

        CachedRowSet rs = executeQuery("SELECT avatar FROM accounts WHERE ref = ?;", List.of(ref));

        if(rs.next()) {
            return rs.getString("avatar");
        }
        return null;

    }



}
