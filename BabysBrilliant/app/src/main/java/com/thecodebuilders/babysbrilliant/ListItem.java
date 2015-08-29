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
    private String category;
    private Boolean showBackground = true;
    private Boolean showText = true;
    private Boolean isSubcategory;



    private Boolean isPurchased;
    private String price;
    private JSONObject rawJSON;



    public ListItem(JSONObject rawJSON, String title, String imageResource, String price, String category, Boolean isSubcategory, Boolean isPurchased, Context context) {
        super(context);
        this.rawJSON = rawJSON;
        this.title = title;
        this.imageResource = imageResource;
        this.price = price;
        this.category = category;
        this.isSubcategory = isSubcategory;
        this.isPurchased = isPurchased;
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
    public String getCategory() {
        return category;
    }
    public String getPrice() {
        return price;
    }
    public Boolean isPurchased() {
        return isPurchased;
    }
    public void setIsPurchased(Boolean isPurchased) {
        this.isPurchased = isPurchased;
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
