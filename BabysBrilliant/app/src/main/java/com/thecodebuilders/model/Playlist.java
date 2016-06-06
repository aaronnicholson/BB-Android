package com.thecodebuilders.model;

import android.util.Log;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by aaronnicholson on 9/1/15.
 */
public class Playlist implements Serializable{
    String name;
   public  ArrayList<JSONObject> playlistItems;

    public Playlist(String name, ArrayList<JSONObject> playlistItems) {
        this.name = name;
        this.playlistItems = playlistItems;
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setPlaylistItems(ArrayList<JSONObject> playlistItems) {
        this.playlistItems = playlistItems;
    }

    public ArrayList<JSONObject> getPlaylistItems() {
        return playlistItems;
    }

    public ArrayList<JSONObject> addPlaylistItem(JSONObject item) {
        playlistItems.add(item);
        Log.e("PlayList:","Item:"+""+name+"///////"+playlistItems.toString());
        return playlistItems;
    }

    public ArrayList<JSONObject> removePlaylistItemAtIndex(int itemIndex) {
        playlistItems.remove(itemIndex);
        Log.e("PlayList:","Item:Remove"+":"+name+"///////"+playlistItems.toString());
        return playlistItems;
    }

}
