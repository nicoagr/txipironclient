package eus.ehu.txipironesmastodonfx.data_access;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import eus.ehu.txipironesmastodonfx.domain.*;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
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
        String response;
        try {
            response = request("accounts/verify_credentials", token);
        } catch (IOException e) {
            return null;
        }
        if (response == null || response.equals("")) {
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
    public static List<Toot> getProfileToots(String selectedAccId, String token) throws IOException {
        String response = request("accounts/" + selectedAccId + "/statuses", token);
        if (response.equals("")) {
            // token is invalid
            return null;
        }
        Type statusListType = new TypeToken<ArrayList<Toot>>() {
        }.getType();
        // get json array and then convert it to a list of Toots
        return gson.fromJson(gson.fromJson(response, JsonArray.class).getAsJsonArray(), statusListType);
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
        String response;
        try {
            response = request("accounts/" + selectedAccId + "/" + endtarget, token);
        } catch (IOException e) {
            return null;
        }
        if (response == null || response.equals("")) {
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
        String response = null;
        try {
            response = request("accounts/" + id, token);
        } catch (IOException e) {
            return null;
        }
        if (response == null || response.equals("")) {
            // token is invalid
            return null;
        }
        return gson.fromJson(response, Account.class);
    }


    /**
     * Method to perform a search on the mastodon servers.
     * It will return a SearchResult object with the results.
     *
     * @param query (String) - query to search
     * @param token (String) - token of the account
     * @param limit (int) - limit of results for each category in searchresult
     * @return (SearchResult) - object with the results
     */
    public static SearchResult performSearch(String query, String token, int limit) {
        HashMap<Object, Object> params = new HashMap<>();
        params.put("q", query);
        params.put("limit", limit);
        String response = requestWithParams(2, "search", token, params);
        if (response == null || response.equals("")) {
            // token is invalid
            return null;
        }
        return gson.fromJson(response, SearchResult.class);
    }

    /**
     * Generic method to perform a GET to the
     * mastodon API. The endpoint must be formated by the part
     * that comes after "https://mastodon.social/api/vX/".
     * This method will accept some parameters to add to the request.
     *
     * @param apiVersion (int) - The version of the API to use
     * @param endpoint   (String) - The endpoint to request
     * @param token      (String) - Mastodon account token
     * @param params     (HashMap<Object,Object>) - Parameters to add to the request
     * @return (String) - The response of the request - Usually formatted as json
     */
    private static String requestWithParams(int apiVersion, String endpoint, String token, HashMap<Object, Object> params) {
        String result = null;
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://mastodon.social/api/v" + apiVersion + "/" + endpoint).newBuilder();
        for (Object key : params.keySet()) {
            urlBuilder.addQueryParameter(key.toString(), params.get(key).toString());
        }
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)
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
     * Generic method to perform a request to the
     * mastodon API. The endpoint must be formated by the part
     * that comes after "https://mastodon.social/api/v1/"
     *
     * @param endpoint (String) - The endpoint to request
     * @param token    - Mastodon account token
     * @return (String) - The response of the request - Usually formatted as json
     */
    private static String request(String endpoint, String token) throws IOException {
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
     * @param aut (String) - Mastodon account token
     * @param toot (TootToBePosted) - The toot already insanced that will
     * @return (String) - The response of the request - Usually formatted as json
     */
    public static String postToot(String aut, TootToBePosted toot) {
        String result = null;
        OkHttpClient client = new OkHttpClient();
        RequestBody req = RequestBody.create(MediaType.parse("application/json"), gson.toJson(toot, TootToBePosted.class));
        Request request = new Request.Builder()
                .url("https://mastodon.social/api/v1/statuses")
                .post(req)
                .addHeader("Authorization", "Bearer " + aut)
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
