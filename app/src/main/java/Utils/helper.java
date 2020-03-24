package Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class helper {
    private static String profileSetUpPref = "profileSetUpSharedPreferences";
    private static String usernamePref = "usernamePreferences";
    private static String pPicPref = "pPicPreferences";


    public static void setProfileSetUpPref(Context context, Boolean profileSet){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(profileSetUpPref, profileSet);
        editor.apply();
    }

    public static Boolean getProfileSetUpPref(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(profileSetUpPref,false);
    }

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
    public static void removeProfileSetUpPref(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().clear().commit();
    }
}
