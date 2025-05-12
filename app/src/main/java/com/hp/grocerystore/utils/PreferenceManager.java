package com.hp.grocerystore.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.hp.grocerystore.application.GRCApplication;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreferenceManager {
    private static final String PREF_NAME = "grc_app_pref";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_DEVICE = "device";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public PreferenceManager(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            editor = sharedPreferences.edit();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Lỗi khi khởi tạo EncryptedSharedPreferences", e);
        }
    }

    public void saveTokens(String accessToken, String refreshToken, String device) {
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.putString(KEY_DEVICE, device);
        editor.apply();
    }

    public void saveUserData(String userName, String userEmail) {
        editor.putString(KEY_USER_NAME, userName);
        editor.putString(KEY_USER_EMAIL, userEmail);
        editor.apply();
    }

    public String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }

    public String getRefreshToken() {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null);
    }

    public void setAccessToken(String accessToken) {
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.apply();
    }

    public String getDevice() {
        return sharedPreferences.getString(KEY_DEVICE, null);
    }

    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, null);
    }

    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, null);
    }

    public void clear() {
        editor.clear().apply();
    }

    public boolean isUserLoggedIn() {
        String accessToken = sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
        String userName = getUserName();
        String userEmail = getUserEmail();

        return accessToken != null && !accessToken.isEmpty()
                && userName != null && !userName.isEmpty()
                && userEmail != null && !userEmail.isEmpty();
    }

    @NonNull
    public static PreferenceManager saveTokens(List<String> cookies, String accessToken) {
        Map<String, String> cookieMap = new HashMap<>();
        for (String cookie : cookies) {
            String[] parts = cookie.split(";", 2);
            String[] kv = parts[0].split("=", 2);
            if (kv.length == 2) {
                cookieMap.put(kv[0].trim(), kv[1].trim());
            }
        }

        String refreshToken = cookieMap.get("refresh_token");
        String device = cookieMap.get("device");
        PreferenceManager prefManager = new PreferenceManager(GRCApplication.getAppContext());
        prefManager.saveTokens(accessToken, refreshToken, device);
        return prefManager;
    }
}
