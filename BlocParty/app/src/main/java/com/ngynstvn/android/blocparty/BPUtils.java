package com.ngynstvn.android.blocparty;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

/**
 * Created by Ngynstvn on 9/23/15.
 */

public class BPUtils {

    // Universal method to show toast messages

    public static void toast(String message) {
        Toast.makeText(BlocpartyApplication.getSharedInstance(), message, Toast.LENGTH_SHORT).show();
    }

    public static SharedPreferences.Editor sharePrefEditor(String name) {
        SharedPreferences sharedPreferences = BlocpartyApplication.getSharedInstance()
                .getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        return editor;
    }

    public static SharedPreferences newSPrefInstance(String name) {
        return BlocpartyApplication.getSharedInstance().getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public static void putSharedPrefValues(SharedPreferences sharedPreferences, String fileName, String posKeyName,
                                           int adapterPosition, String stateKeyName, boolean state) {
        sharedPreferences = BlocpartyApplication.getSharedInstance().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(posKeyName, adapterPosition);
        editor.putBoolean(stateKeyName, state);
        editor.commit();
    }

    public static void putSharedPrefCounter(SharedPreferences sharedPreferences, String fileName, String key, int value) {
        sharedPreferences = BlocpartyApplication.getSharedInstance().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

}
