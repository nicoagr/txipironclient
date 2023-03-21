package eus.ehu.txipironesmastodonfx.data_access;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import eus.ehu.txipironesmastodonfx.domain.Account;
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

    private class idHandler {
        String id;
    }

    public static String verifyAndGetId(String token) {
        String response = request("accounts/verify_credentials", token, true);
        if (response.equals("")) {
            // token is invalid
            return null;
        }
        idHandler result = gson.fromJson(response, idHandler.class);
        return result.id;
    }

    public static List<Toot> getActivityToots(String selectedAccId, String sysvar) {
        String response = request("accounts/" + selectedAccId + "/statuses", sysvar, false);
        if (response.equals("")) {
            // token is invalid
            return null;
        }
        Type statusListType = new TypeToken<ArrayList<Toot>>() {
        }.getType();
        // get json array and then convert it to a list of Toots
        return gson.fromJson(gson.fromJson(response, JsonArray.class).getAsJsonArray(), statusListType);
    }

    public static Account getAccount(String id, String token) {
        String response = request("accounts/" + id, token, true);
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
     * @param endpoint   (String) - The endpoint to request
     * @param sysvarname - The name of the system variable that contains the token
     * @param override   - If true, the sysvar will be interpreted as a token
     * @return (String) - The response of the request - Usually formatted as json
     */
    private static String request(String endpoint, String sysvarname, boolean override) {
        String result = "";
        OkHttpClient client = new OkHttpClient();
        String token = System.getenv(sysvarname);
        if (override) {
            token = sysvarname;
        }
        Request request = new Request.Builder()
                .url("https://mastodon.social/api/v1/" + endpoint)
                .get()
                .addHeader("Authorization", "Bearer " + token)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                result = response.body().string();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
