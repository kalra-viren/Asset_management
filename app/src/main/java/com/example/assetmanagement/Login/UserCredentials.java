package com.example.assetmanagement.Login;


import android.content.Context;
import android.content.SharedPreferences;

public class UserCredentials {
    private static UserCredentials instance;
    private static final String PREFS_NAME = "UserCredentialsPrefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_PASSWORD = "password";

    private SharedPreferences sharedPreferences;

    // Private constructor
    private UserCredentials(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Singleton instance
    public static synchronized UserCredentials getInstance(Context context) {
        if (instance == null) {
            instance = new UserCredentials(context);
        }
        return instance;
    }

    // Save credentials
    public void saveCredentials(String userId, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }

    // Getters
    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    public String getPassword() {
        return sharedPreferences.getString(KEY_PASSWORD, null);
    }

    // Clear credentials (for logout)
    public void clearCredentials() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}

