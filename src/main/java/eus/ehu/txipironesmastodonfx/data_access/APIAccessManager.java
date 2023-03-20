package eus.ehu.txipironesmastodonfx.data_access;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class APIAccessManager {

    public static Gson gson = new Gson();

    private static class idHandler {
        private static String id;
    }

    public static String verifyAndGetId(String token) {
        String response = request("accounts/verify_credentials", token);
        if (response.equals("")) {
            // token is invalid
            return null;
        }
        idHandler result = gson.fromJson(response, idHandler.class);
        return idHandler.id;
    }

    private static String request(String endpoint, String token) {
        String result = "";
        OkHttpClient client = new OkHttpClient();
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
