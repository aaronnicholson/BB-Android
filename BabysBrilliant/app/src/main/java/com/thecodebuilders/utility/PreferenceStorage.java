package com.thecodebuilders.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Yuvaraj on 04-May-16.
 */
public class PreferenceStorage {

    public static final String FAVOURITE_SAVE = "favourite_save";
    public static final String PLAYLIST_SAVE = "playlist_save";

    public static void  saveFileLength (Context context, String key, int value){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }


    public static int returnFileLength (Context context, String key){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return  preferences.getInt(key, 0);
    }

    public static void  saveFavourites (Context context, String key, String value){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getFavourites (Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(FAVOURITE_SAVE, "");
    }

    public static void  clearDefaultFavouritePref (Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(FAVOURITE_SAVE);
        editor.commit();
    }

    public static void  savePlaylist (Context context, String value){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PLAYLIST_SAVE, value);
        editor.commit();
    }

    public static String getPlaylist (Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PLAYLIST_SAVE, "");
    }

    public static void  clearDefaultPlaylistPref (Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(PLAYLIST_SAVE);
        editor.commit();
    }

    public static String returnFavourites(Context context, String key){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return  preferences.getString(key, "");
    }
    public static void  removeFavourites (Context context, String key){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.commit();
    }

}
