package com.PollBuzz.pollbuzz.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class helper {
    private static String usernamePref = "usernamePreferences";
    private static String pPicPref = "pPicPreferences";


    public static void setpPicPref(Context context, String pPic) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(pPicPref, pPic);
        editor.apply();
    }

    public static String getpPicPref(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(pPicPref, null);
    }

    public static void setusernamePref(Context context, String username) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(usernamePref, username);
        editor.apply();
    }

    public static String getusernamePref(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(usernamePref, null);
    }

    public static void removeProfileSetUpPref(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().clear().commit();
    }
}
