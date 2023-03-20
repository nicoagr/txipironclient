package eus.ehu.txipironesmastodonfx.data_access;

import com.google.gson.Gson;
import eus.ehu.txipironesmastodonfx.domain.Account;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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

    public static Account getAccount(String id, String token) {
        String response = request("accounts/" + id, token, true);
        if (response.equals("")) {
            // token is invalid
            return null;
        }
        return gson.fromJson(response, Account.class);
    }

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
