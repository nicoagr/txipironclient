package eus.ehu.txipironesmastodonfx.data_access;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import eus.ehu.txipironesmastodonfx.TxipironClient;
import eus.ehu.txipironesmastodonfx.domain.*;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
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
 * @author Xiomara Cáceces
 * @author Marcos Chouciño
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
        if (response == null || response.equals("")) {
            // token is invalid
            return null;
        }
        idHandler result = gson.fromJson(response, idHandler.class);
        return result.id;
    }

    private class authCodeResponse {
        String access_token;
    }

    /**
     * Method that will get a mastodon token from an auth code.
     *
     * @param authCode (String) - auth code to get the token
     * @return (String) - token of the account
     */
    public static String getTokenFromAuthCode(String authCode) {
        String result = null;
        OkHttpClient client = new OkHttpClient();
        String requestBodyString = "grant_type=authorization_code&code=" + authCode +
                "&client_id=" + TxipironClient.MASTODON_APP_ID + "&client_secret=" + TxipironClient.MASTODON_APP_SECRET +
                "&redirect_uri=urn:ietf:wg:oauth:2.0:oob";
        RequestBody req = RequestBody.create(requestBodyString, MediaType.parse("application/x-www-form-urlencoded"));
        Request request = new Request.Builder()
                .url("https://mastodon.social/oauth/token")
                .post(req)
                .build();
        try{
            Response response = client.newCall(request).execute();
            if (response.code() == 200 && response.body() != null) {
                result = response.body().string();
                result = gson.fromJson(result, authCodeResponse.class).access_token;
            }
        }
        catch (IOException e){
            return null;
        }
        return result;
    }

    /**
     * Method to get the statuses (activity toots) of an account.
     * It will return a list of toots. If the request can't be made,
     * it will return null.
     *
     * @param selectedAccId (String) - id of the account
     * @param token         (String) - system variable of the account
     * @param max_id        (String) - max_id of the toot
     * @return (List < Toot >) - list of toots
     */
    public static List<Toot> getTootId(String selectedAccId, String token, String max_id) {
        if (token == null || token.equals("")) return null;
        String endpoint = "accounts/" + selectedAccId + "/statuses";
        if (max_id != null && !max_id.equals("")) {
            endpoint += "?max_id=" + max_id;
        }
        String response = request(endpoint, token);
        if (response == null || response.equals("")) {
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
        if(token == null || token == "") return null;

        String endtarget = following ? "following" : "followers";
        String response = request("accounts/" + selectedAccId + "/" + endtarget, token);
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
        if (token == null || token == "") return null;

        String response = request("accounts/" + id, token);
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
        if (token == null || token.equals("") || query == null) return null;
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
    private static String request(String endpoint, String token){
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
            return null;
        }
        return result;
    }

    /**
     * Generic method to perform a request to the
     * mastodon API without token. The endpoint must be formated by the part
     * that comes after "https://mastodon.social/api/v1/"
     *
     * @param endpoint (String) - The endpoint to request
     * @return (String) - The response of the request - Usually formatted as json
     */
    private static String requestNoToken(String endpoint) {
        String result = null;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://mastodon.social/api/v1/" + endpoint)
                .get()
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
     * @param toot (TootToBePosted) - The toot already instanced that will
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

    /**
     * Obtains the list of views of  a user wich is introduced as parameter
     *
     * @param username (String)  - Mastodon account token
     * @return (String) - The response of the request - Usually formatted as json
     */
    public static String getIdFromUsername(String username) {
        if(username == null || username.equals("")) return null;

        String response = requestNoToken("accounts/lookup?acct=" + username);
        if (response == null || response.equals("")) {
            // token is invalid
            return null;
        }
        return gson.fromJson(response, Account.class).id;
    }

    /**
     * Method to get the toots liked by a user
     * It will return a list of toots. If the request can't be made,
     * it will return null.
     *
     * @param token         (String) - token of the account
     * @return (List<Toot>) - the list of liked toots
     */
    public static List<Toot> getLikedToots(String token){
        String response = request("favourites", token);
        if (response == null || response.equals("")) {
            // token is invalid
            return null;
        }
        Type fTootListType = new TypeToken<ArrayList<Toot>>() {
        }.getType();
        return gson.fromJson(gson.fromJson(response, JsonArray.class).getAsJsonArray(), fTootListType);
    }

    /**
     * Method to add a favourite toot to the list of favourites
     *
     * @param tootId (String) - The id of the toot
     * @param token (String) - Mastodon account token
     * @return (String) - The response of the request - Usually formatted as json
     */
    public static int favouriteToot(String tootId, String token) {
        URL url = null;
        int responseCode = 0;
        try{
            url = new URL("https://mastodon.social/api/v1/statuses/"+tootId+"/favourite");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            responseCode = conn.getResponseCode();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return responseCode;
    }

    /**
     * Method to delete a favourite toot from the list of favourites
     *
     * @param tootId (String) - The id of the toot
     * @param token (String) - Mastodon account token
     * @return (String) - The response of the request - Usually formatted as json
     */
    public static int unfavouriteToot(String tootId, String token) {
        URL url = null;
        int responseCode = 0;
        try{
            url = new URL("https://mastodon.social/api/v1/statuses/"+tootId+"/unfavourite");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            responseCode = conn.getResponseCode();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return responseCode;
    }

    /**
     * Obtains the list of views of  a user wich is introduced as parameter
     *
     * @param token  (String) - token of the account
     * @param max_id
     * @return (List < Toot >) - the list of views of a user
     */
    public static List<Toot> getHomeTootsId(String token, String max_id) {
        String endpoint = "timelines/home";
        if (max_id != null && !max_id.equals("")) {
            endpoint += "?max_id=" + max_id;
        }
        String response = request(endpoint, token);
        if (response == null || response.equals("")) {
            // token is invalid
            return null;
        }
        Type statusListType = new TypeToken<ArrayList<Toot>>() {
        }.getType();
        // get json array and then convert it to a list of Toots
        return gson.fromJson(gson.fromJson(response, JsonArray.class).getAsJsonArray(), statusListType);
    }

    /**
     * Makes a request to the mastodon API to follow an account
     * @param token (String) - token of the account
     * @param id (String) - id of the account
     * @return (String) - The response of the request - Usually formatted as json
     */
    public static String follow(String token,String id) {
        String result = null;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://mastodon.social/api/v1/accounts/" + id + "/follow")
                .addHeader("Authorization", "Bearer " + token)
                .post(RequestBody.create(null, new byte[0]))
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
     * Makes a request to the mastodon API to unfollow an account
     * @param token (String) - token of the account
     * @param id (String) - id of the account
     * @return (String) - The response of the request - Usually formatted as json
     */
    public static String unfollow(String token,String id) {
        String result = null;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://mastodon.social/api/v1/accounts/" + id + "/unfollow ")
                .addHeader("Authorization", "Bearer " + token)
                .post(RequestBody.create(null, new byte[0]))
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
     * Method to get the toots bookmarked by a user
     * It will return a list of toots. If the request can't be made,
     * it will return null.
     *
     * @param token         (String) - token of the account
     * @return (List<Toot>) - the list of bookmarked toots
     */
    public static List<Toot> getBookmarkedToots(String token){
        String response = request("bookmarks", token);
        if (response == null || response.equals("")) {
            // token is invalid
            return null;
        }
        Type fTootListType = new TypeToken<ArrayList<Toot>>() {
        }.getType();
        return gson.fromJson(gson.fromJson(response, JsonArray.class).getAsJsonArray(), fTootListType);
    }

    /**
     * Method to add a bookmarked toot to the list of bookmarks
     *
     * @param tootId (String) - The id of the toot
     * @param token (String) - Mastodon account token
     * @return (String) - The response of the request - Usually formatted as json
     */
    public static int bookmarkToot(String tootId, String token) {
        URL url = null;
        int responseCode = 0;
        try{
            url = new URL("https://mastodon.social/api/v1/statuses/"+tootId+"/bookmark");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            responseCode = conn.getResponseCode();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return responseCode;
    }

    /**
     * Method to delete a bookamrked toot from the list of bookmarks
     *
     * @param tootId (String) - The id of the toot
     * @param token (String) - Mastodon account token
     * @return (String) - The response of the request - Usually formatted as json
     */
    public static int unbookmarkToot(String tootId, String token) {
        URL url = null;
        int responseCode = 0;
        try{
            url = new URL("https://mastodon.social/api/v1/statuses/"+tootId+"/unbookmark");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            responseCode = conn.getResponseCode();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return responseCode;
    }

    /**
     * Method to add a rebooted toot to the list of reboots
     *
     * @param tootId (String) - The id of the toot
     * @param token (String) - Mastodon account token
     * @return (String) - The response of the request - Usually formatted as json
     */
    public static int reblogToot(String tootId, String token) {
        URL url = null;
        int responseCode = 0;
        try{
            url = new URL("https://mastodon.social/api/v1/statuses/"+tootId+"/reblog");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            responseCode = conn.getResponseCode();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return responseCode;
    }

    /**
     * Method to delete a rebooted toot from the list of reboots
     *
     * @param tootId (String) - The id of the toot
     * @param token (String) - Mastodon account token
     * @return (String) - The response of the request - Usually formatted as json
     */
    public static int unreblogToot(String tootId, String token) {
        URL url = null;
        int responseCode = 0;
        try{
            url = new URL("https://mastodon.social/api/v1/statuses/"+tootId+"/unreblog");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            responseCode = conn.getResponseCode();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return responseCode;
    }
    /**
     * Method to upload a media to the mastodon servers
     *
     * @param token (String) - token of the account
     * @param file  (File) -  file to upload
     * @return (String) - id of the uploaded media
     */
    public static MediaAttachment uploadMedia(String token, File file) {
        String result = null;
        OkHttpClient client = new OkHttpClient();
        // Determine the MIME type based on the file extension
        String mediaType = "image/jpg";
        String extension = HTMLParser.getFileExtension(file);
        switch (extension.toLowerCase()) {
            case "jpg":
                mediaType = "image/jpg";
                break;
            case "jpeg":
                mediaType = "image/jpeg";
                break;
            case "png":
                mediaType = "image/png";
                break;
            case "mov":
                mediaType = "video/quicktime";
                break;
            case "mp4":
            case "m4v":
                mediaType = "video/mp4";
                break;
            case "webm":
                mediaType = "video/webm";
                break;
        }
        // Build the multipart form request body
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse(mediaType), file))
                .build();
        Request request = new Request.Builder()
                .url("https://mastodon.social/api/v2/media")
                .addHeader("Authorization", "Bearer " + token)
                .post(requestBody)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if ((response.code() == 200 || response.code() == 202) && response.body() != null) {
                result = response.body().string();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gson.fromJson(result, MediaAttachment.class);
    }

    /**
     * Method to change the users profile picture
     * in the mastodon's servers
     *
     * @param token (String) - token of the account
     * @param pic   (File) - file to upload
     * @return (boolean) - true if the profile picture was changed successfully
     */
    public static boolean changeProfilePicture(String token, File pic) {
        if (token == null || pic == null) return false;
        String mediaType = null, result = null;
        OkHttpClient client = new OkHttpClient();
        String extension = HTMLParser.getFileExtension(pic);
        switch (extension.toLowerCase()) {
            case "jpg":
                mediaType = "image/jpg";
                break;
            case "jpeg":
                mediaType = "image/jpeg";
                break;
            case "png":
                mediaType = "image/png";
                break;
            case "gif":
                mediaType = "image/gif";
                break;
            default:
                return false;
        }
        // Build the multipart form request body
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("avatar", pic.getName(), RequestBody.create(MediaType.parse(mediaType), pic))
                .build();
        Request request = new Request.Builder()
                .url("https://mastodon.social/api/v1/accounts/update_credentials")
                .addHeader("Authorization", "Bearer " + token)
                .patch(requestBody)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if ((response.code() == 200 || response.code() == 202) && response.body() != null) {
                result = response.body().string();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (result != null);
    }

    /**
     * Method to check if a media was processed
     * in the mastodons' servers
     *
     * @param token   (String) - token of the account
     * @param mediaId (String) - id of the media
     * @return (boolean) - true if the media was processed
     */
    public static boolean isMediaProcessed(String token, String mediaId) {
        if (token == null || mediaId == null) return false;
        return (request("media/" + mediaId, token) != null);
    }

    /**
     * Send a signal to the server to clear the notifications
     * @param token (String) - token of the account

     */
    public static void clearNotification(String token) throws IOException{
        String response = request("notifications/clear", token);
    }

    /**
     * Get an array of the type Notifications from the server
     * @param token (String) - token of the account
     * @return (List < Notification >) - list of notification
     */
    public static List<Notification> getNewNotification(String token) throws IOException {
        String response = request("notifications", token);
        if (response == null) {
            // token is invalid
            return null;
        }
        Type NotificationListType = new TypeToken<ArrayList<Notification>>() {
        }.getType();
        // get json array and then convert it to a list of Notifications
        return gson.fromJson(gson.fromJson(response, JsonArray.class).getAsJsonArray(), NotificationListType);
    }
    /**
     * Get an array of the type Notifications from the server since the Notification ip that is passed as a parameter
     * @param token (String) - token of the account
     * @param id (String) - id of the Notification
     * @return (List < Notification >) - list of notification
     */
    public static List<Notification> getNotificationSinceip(String token,String id) throws IOException {
        String result = null;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://mastodon.social/api/v1/" + "notifications/?since_id=" + id)
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
        String response = result;
        Type NotificationListType = new TypeToken<ArrayList<Notification>>() {
        }.getType();
        // get json array and then convert it to a list of Notifications
        if (result != null) {
            return gson.fromJson(gson.fromJson(response, JsonArray.class).getAsJsonArray(), NotificationListType);
        }
        else {
            List<Notification> a = new ArrayList<>();
            return a;
        }
    }

}
