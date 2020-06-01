package com.bondex.library.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.bondex.library.base.Constant;

/**
 * date: 2020/5/21
 *
 * @Author: ysl
 * description:
 */
public class SharedPreferenceUtils {


    private SharedPreferences preferences;
    private Context context;

    public SharedPreferenceUtils(Context context, String dbName) {
        this.context = context;

        if (context == null) {
            return;
        }

        preferences = context.getSharedPreferences(dbName, Context.MODE_PRIVATE);
    }


    public void saveStr(String key, String value) {

        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(key, value);
        editor.apply();
    }


    public String getStr(String key) {


        String value = preferences.getString(key, "");

        return value;
    }

    public void saveBoolean(String key, boolean value) {

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key) {


        return preferences.getBoolean(key, true);
    }


    public void saveLong(String key, long value) {

        SharedPreferences.Editor editor = preferences.edit();

        editor.putLong(key, value);
        editor.apply();
    }

    public void saveInt(String key, int value) {

        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key) {

        int value = preferences.getInt(key, -1);
        return value;
    }

    public long getLong(String key) {

        long value = preferences.getLong(key, 0);

        return value;
    }

}
