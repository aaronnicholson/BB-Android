package com.thecodebuilders.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.thecodebuilders.classes.ObjectSerializer;
import com.thecodebuilders.model.Playlist;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Yuvaraj on 04-May-16.
 */
public class PreferenceStorage {

    public static final String FAVOURITE_SAVE = "favourite_save";

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


    public static void saveFavouriteObject (Context context, ArrayList<Playlist> favoriteItems){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        try {
            editor.putString(FAVOURITE_SAVE, ObjectSerializer.serialize(favoriteItems));
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.commit();
    }
    public static ArrayList<JSONObject> getFavouriteObject (Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        ArrayList<JSONObject> favouriteItems = null;
        try {
            favouriteItems = (ArrayList<JSONObject>) ObjectSerializer.deserialize(prefs.getString(FAVOURITE_SAVE, ObjectSerializer.serialize(new ArrayList<JSONObject>())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return favouriteItems;
    }
}
