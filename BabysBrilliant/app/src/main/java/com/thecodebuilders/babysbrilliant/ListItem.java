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

    private String title = "TITLE";
    private String imageResource;

    public ListItem(String title, String imageResource, Context context) {
        super(context);
        this.title = title;
        this.imageResource = imageResource;


    }

    public String getTitle() {
        return title;
    }
    public String getImageResource() {
        return imageResource;
    }
    @Override
    public String toString() {
        return title;
    }
}
