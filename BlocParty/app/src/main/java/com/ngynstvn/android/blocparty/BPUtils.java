package com.ngynstvn.android.blocparty;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

/**
 * Created by Ngynstvn on 9/23/15.
 */

public class BPUtils {

    // ----- Static Variables ----- //

    public static final String FILE_NAME = "log_states";

    // Facebook Variables

    public static final String FB_LOGIN = "isFBLoggedIn";
    public static final String FB_POSITION = "fbAdapterPosition";

    // Twitter Variables

    public static final String TW_LOGIN = "isTWLoggedIn";
    public static final String TW_POSITION = "twAdapterPosition";
    public static final String TW_ACCESS_TOKEN = "twAccessToken";
    public static final String TW_ACCESS_TOKEN_SECRET = "twAccessTokenSecret";
    public static final String TW_CONSUMER_KEY = "twConsumerKey";
    public static final String TW_CONSUMER_SECRET = "twConsumerSecret";

    // Instagram Variables

    public static final String IG_LOGIN = "isIGLoggedIn";
    public static final String IG_POSITION ="igAdapterPosition";
    public static final String IG_TOKEN = "ig_token";
    public static final String IG_AUTH_CODE = "ig_auth_code";

    // ----- Static Methods ----- //

    public static String classTag(Class className) {
        return "(" + className.getSimpleName() + "): ";
    }

    // Universal method to show toast messages

    public static void toast(String message) {
        Toast.makeText(BlocpartyApplication.getSharedInstance(), message, Toast.LENGTH_SHORT).show();
    }

        // SharedPreferences

    public static SharedPreferences newSPrefInstance(String name) {
        return BlocpartyApplication.getSharedInstance().getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public static void putSPrefLoginValue(SharedPreferences sharedPreferences, String fileName, String posKey,
                                          int adapterPosition, String stateKeyName, boolean state) {
        sharedPreferences = BlocpartyApplication.getSharedInstance().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(posKey, adapterPosition);
        editor.putBoolean(stateKeyName, state);
        editor.apply();
    }

    public static void putSPrefIntValue(SharedPreferences sharedPreferences, String fileName, String key, int value) {
        sharedPreferences = BlocpartyApplication.getSharedInstance().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void putSPrefStrValue(SharedPreferences sharedPreferences, String fileName, String key, String value) {
        sharedPreferences = BlocpartyApplication.getSharedInstance().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void putSPrefBooleanValue(SharedPreferences sharedPreferences, String fileName, String key, boolean value) {
        sharedPreferences = BlocpartyApplication.getSharedInstance().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void putSPrefFloatValue(SharedPreferences sharedPreferences, String fileName, String key, float value) {
        sharedPreferences = BlocpartyApplication.getSharedInstance().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public static void putSPrefLongValue(SharedPreferences sharedPreferences, String fileName, String key, long value) {
        sharedPreferences = BlocpartyApplication.getSharedInstance().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static void delSPrefStrValue(SharedPreferences sharedPreferences, String fileName, String key) {
        sharedPreferences = BlocpartyApplication.getSharedInstance().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }
}
