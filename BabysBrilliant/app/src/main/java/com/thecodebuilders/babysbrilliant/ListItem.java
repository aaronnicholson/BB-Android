package com.thecodebuilders.babysbrilliant;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import org.json.JSONObject;

import java.io.InputStream;

/**
 * Created by aaronnicholson on 8/17/15.
 */
public class ListItem extends View {

    private String title;
    private String imageResource;
    private String mediaFile;
    private String category;
    private Boolean showBackground = true;
    private Boolean showText = true;

    private Boolean isSubcategory;
    private Boolean isPurchased;
    private Boolean isFavorite;
    private Boolean isPurchasable = false;
    private String price;
    private JSONObject rawJSON;



    public ListItem(JSONObject rawJSON, String title, String imageResource, String mediaFile, String price, String category, Boolean isSubcategory, Boolean isPurchased, Boolean isFavorite, Context context) {
        super(context);
        this.rawJSON = rawJSON;
        this.title = title;
        this.imageResource = imageResource;
        this.mediaFile = mediaFile;
        this.price = price;
        this.category = category;
        this.isSubcategory = isSubcategory;
        this.isPurchased = isPurchased;
        this.isFavorite = isFavorite;

        setIsPurchasable();
    }
    public JSONObject getRawJSON() {
        return rawJSON;
    }
    public String getTitle() {
        return title;
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
    public Boolean isPurchased() {
        return isPurchased;
    }
    public Boolean isFavorite() {
        return isFavorite;
    }
    public void setIsPurchased(Boolean isPurchased) {
        this.isPurchased = isPurchased;
    }
    public Boolean isSubcategory() {
        return isSubcategory;
    }
    public Boolean isPurchasable() {
        return isPurchasable;
    }

    private void setIsPurchasable() {
        isPurchasable = false;

        //sound board groups are purchasable
        if(category.equals("5") && isSubcategory) {
            isPurchasable = true;
        }

        //products that are not sound board items are purchasable
        if(!category.equals("5") && !isSubcategory) {
            isPurchasable = true;
        }

    }

    public Boolean doShowBackground() {
        if(category.equals("1")) {
            showBackground = false;
        }
        if(category.equals("5") && !isSubcategory) {
            showBackground = false;
        }
        return showBackground;
    }
    public Boolean doShowText() {
        if(category.equals("5") && !isSubcategory) {
            showText = false;
        }
        return showText;
    }


    @Override
    public String toString() {
        return title;
    }
}
