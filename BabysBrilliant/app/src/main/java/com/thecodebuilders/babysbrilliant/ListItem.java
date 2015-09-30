package com.thecodebuilders.babysbrilliant;

import android.content.Context;
import android.view.View;

import org.json.JSONObject;

/**
 * Created by aaronnicholson on 8/17/15.
 */
public class ListItem extends View {
    private String title;
    private String imageResource;
    private String mediaFile;
    private String category;
    private String sectionTitle;
    private String price;
    private Boolean showBackground = true;
    private Boolean showText = true;
    private Boolean isSection;
    private Boolean isPurchased;
    private Boolean isFavorite;
    private Boolean isPlaylistItem;
    private Boolean isPlaylist;

    private Boolean playInline;
    private Boolean isPurchasable = false;
    private JSONObject rawJSON;

    public ListItem(JSONObject rawJSON, String title, Boolean playInline, String imageResource, String mediaFile, String price, String category, Boolean isSection, Boolean isPurchased, Boolean isPlaylistItem, Boolean isPlaylist, Boolean isFavorite, Context context) {
        super(context);
        this.rawJSON = rawJSON;
        this.title = title;
        this.playInline = playInline;
        this.imageResource = imageResource;
        this.mediaFile = mediaFile;
        this.price = price;
        this.category = category;
        this.isSection = isSection;
        this.isPurchased = isPurchased;
        this.isFavorite = isFavorite;
        this.isPlaylistItem = isPlaylistItem;
        this.isPlaylist = isPlaylist;

        setIsPurchasable();

    }

    public JSONObject getRawJSON() {
        return rawJSON;
    }

    public String getTitle() {
        return title;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public String getImageResource() {
        return imageResource;
    }

    public String getMediaFile() {
        return mediaFile;
    }

    public String getCategory() {
        return category;
    }

    public String getPrice() {
        return price;
    }

    public Boolean playInline() {
        return playInline;
    }

    public Boolean isPurchased() {
        return isPurchased;
    }

    public Boolean isFavorite() {
        return isFavorite;
    }

    public Boolean isPlaylistItem() {
        return isPlaylistItem;
    }

    public Boolean isPlaylist() {
        return isPlaylist;
    }

    public void setIsPurchased(Boolean isPurchased) {
        this.isPurchased = isPurchased;
    }

    public Boolean isSection() {
        return isSection;
    }

    public Boolean isPurchasable() {
        return isPurchasable;
    }

    private void setIsPurchasable() {
        isPurchasable = false;

        //sound board groups are purchasable
        if (category.equals("5") && isSection) {
            isPurchasable = true;
        }

        //products that are not sound board items are purchasable
        if (!category.equals("5") && !isSection) {
            isPurchasable = true;
        }

    }

    public Boolean doShowBackground() {
        if (category.equals("1")) {
            showBackground = false;
        }
        if (category.equals("5") && !isSection) {
            showBackground = false;
        }
        return showBackground;
    }

    public Boolean doShowText() {
        if (category.equals("5") && !isSection) {
            showText = false;
        }
        return showText;
    }

    @Override
    public String toString() {
        return title;
    }
}
