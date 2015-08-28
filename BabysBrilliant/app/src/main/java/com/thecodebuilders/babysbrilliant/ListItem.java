package com.thecodebuilders.babysbrilliant;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

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


    public ListItem(String title, String imageResource, String category, Boolean isSubcategory, Context context) {
        super(context);
        this.title = title;
        this.imageResource = imageResource;
        this.category = category;
        this.isSubcategory = isSubcategory;
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
