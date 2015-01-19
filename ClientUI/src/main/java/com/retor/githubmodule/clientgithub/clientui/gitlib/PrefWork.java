package com.retor.githubmodule.clientgithub.clientui.gitlib;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by retor on 17.01.2015.
 */
public class PrefWork {
    public static String PREF_NAME = "client";
    public static String PREF_KEY = "token";

    public static void savePref(Context context, String toSave){
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_MULTI_PROCESS);
        preferences.edit().putString(PREF_KEY, toSave).commit();
    }

    public static String loadPref(Context context){
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_MULTI_PROCESS);
        return preferences.getString(PREF_KEY, null);
    }

    public static void delPref(Context context){
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_MULTI_PROCESS);
        preferences.edit().clear().commit();
    }

}
