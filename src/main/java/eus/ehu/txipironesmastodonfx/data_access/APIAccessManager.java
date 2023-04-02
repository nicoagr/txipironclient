package eus.ehu.txipironesmastodonfx.data_access;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import eus.ehu.txipironesmastodonfx.domain.Account;
import eus.ehu.txipironesmastodonfx.domain.Follow;
import eus.ehu.txipironesmastodonfx.domain.Toot;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for managing access to the API.
 * It will be used to get the data from the API and convert it.
 * It will also be used to verify tokens.
 * This is the only class that needs access to the internet.
 *
 * @author Nicolás Aguado
 * @author Haizea Bermejo
 * @author Marcos Chouciño
 * @author Xiomara Cáceces
 */
public class APIAccessManager {

    public static Gson gson = new Gson();

    /**
     * Simple class to do a quick json parsing.
     * Used in the verifyAndGetId method.
     */
    private class idHandler {
        String id;
    }

    /**
     * Method to verify if a token is valid. It will return the id of the account
     * if the account is valid and null if it is not.
     *
     * @param token (String) - token to verify
     * @return (String) - id of the account if the token is valid, null if it is not
     */
    public static String verifyAndGetId(String token) {
        String response = request("accounts/verify_credentials", token);
        if (response.equals("")) {
            // token is invalid
            return null;
        }
        idHandler result = gson.fromJson(response, idHandler.class);
        return result.id;

    }

    /**
     * Method to get the statuses (activity toots) of an account.
     * It will return a list of toots. If the request can't be made,
     * it will return null.
     *
     * @param selectedAccId (String) - id of the account
     * @param token         (String) - system variable of the account
     * @return (List < Toot >) - list of toots
     */
    public static List<Toot> getProfileToots(String selectedAccId, String token) {
        String response = request("accounts/" + selectedAccId + "/statuses", token);
        if (response.equals("")) {
            // token is invalid
            return null;
        }
        Type statusListType = new TypeToken<ArrayList<Toot>>() {
        }.getType();
        // get json array and then convert it to a list of Toots
        List<Toot> toots = gson.fromJson(gson.fromJson(response, JsonArray.class).getAsJsonArray(), statusListType);
        // Unflip the reblog recursion stack
        for (int i = 0; i < toots.size(); i++) {
            Toot t = toots.get(i);
            while (t.reblog != null) t = t.reblog;
            toots.set(i, t);
        }
        return toots;
    }

    /**
     * Generic method to get the follow list of an account
     * The parameter following indicates if we want to get the
     * following list or the followers list.
     *
     * @param selectedAccId (String) - id of the account
     * @param token         (String) - token of the account
     * @param following     (boolean) - true if we want the following list, false if we want the followers list
     * @return (List < Follow >) - list of follows
     */
    public static List<Follow> getFollow(String selectedAccId, String token, boolean following) {
        String endtarget = following ? "following" : "followers";
        String response = request("accounts/" + selectedAccId + "/" + endtarget, token);
        if (response.equals("")) {
            // token is invalid
            return null;
        }
        Type followListType = new TypeToken<ArrayList<Follow>>() {
        }.getType();
        // get json array and then convert it to a list of Accounts
        return gson.fromJson(gson.fromJson(response, JsonArray.class).getAsJsonArray(), followListType);
    }


    /**
     * Generic method to get an account from its id
     *
     * @param id    (String) - id of the account
     * @param token (String) - token of the account
     * @return (Account) - the account
     */
    public static Account getAccount(String id, String token) {
        String response = request("accounts/" + id, token);
        if (response.equals("")) {
            // token is invalid
            return null;
        }
        return gson.fromJson(response, Account.class);
    }

    /**
     * Generic method to perform a request to the
     * mastodon API. The endpoint must be formated by the part
     * that comes after "https://mastodon.social/api/v1/"
     *
     * @param endpoint (String) - The endpoint to request
     * @param token    - Mastodon account token
     * @return (String) - The response of the request - Usually formatted as json
     */
    private static String request(String endpoint, String token) {
        String result = null;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://mastodon.social/api/v1/" + endpoint)
                .get()
                .addHeader("Authorization", "Bearer " + token)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.code() == 200 && response.body() != null) {
                result = response.body().string();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }




    /**
     * Method to post a toot, NOT FINISHED
     *
     * @param content (String) - The content of the status
     * @param token    - Mastodon account token
     * @return (String) - The response of the request - Usually formatted as json
     */
    public static String postToot(String token, String content, String idReply) {
        String result = null;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://mastodon.social/api/v1/statuses")
                .get()
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("status", content)
                .addHeader("in_reply_to_id", idReply)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.code() == 200 && response.body() != null) {
                result = response.body().string();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


}
