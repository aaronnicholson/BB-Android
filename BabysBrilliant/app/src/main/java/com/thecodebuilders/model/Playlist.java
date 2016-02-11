package com.thecodebuilders.model;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by aaronnicholson on 9/1/15.
 */
public class Playlist {
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
        return playlistItems;
    }

    public ArrayList<JSONObject> removePlaylistItemAtIndex(int itemIndex) {
        playlistItems.remove(itemIndex);
        return playlistItems;
    }

}
