package com.thecodebuilders.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.thecodebuilders.application.ApplicationContextProvider;
import com.thecodebuilders.babysbrilliant.ListItem;
import com.thecodebuilders.babysbrilliant.R;
import com.thecodebuilders.network.VolleySingleton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Android Developer on 9/30/2015.
 */
public class CommonAdapterUtility {

    private final static Context appContext = ApplicationContextProvider.getContext();
    private static VolleySingleton volleySingleton = VolleySingleton.getInstance();
    private static ImageLoader imageLoader = volleySingleton.getImageLoader();

    public static void saveThumbToLocalFile(String fileName, final Bitmap bitmap) {
        FileOutputStream fileOutputStream = null;
        if (bitmap == null) {
            Log.d("", "saving bitmap is null");
        } else {
            try {
                fileOutputStream = appContext.openFileOutput(fileName, Context.MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.PNG, 85, fileOutputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void loadLocalSavedImage(String fileName, ImageView imageView) throws FileNotFoundException {
        File file = new File(appContext.getFilesDir(), fileName);
        Bitmap loadedBitmap = BitmapFactory.decodeStream(new FileInputStream(file));
        imageView.setImageBitmap(loadedBitmap);
    }

    public static void showPlaceHolderImage(ImageView imageView) {
        InputStream stream = null;
        try {
            stream = appContext.getAssets().open("thumb_placeholder.png");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        Drawable drawable = Drawable.createFromStream(stream, null);
        imageView.setImageDrawable(drawable);
    }

    public static void loadImageFromServer(final ListItem listItem, final ImageView imageView) {

        final String fileName = listItem.getImageResource();
        if (fileName != null) {

            String mediaURL = appContext.getResources().getString(R.string.media_url) + fileName;
            imageLoader.get(mediaURL, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean stillLoading) {
                    if (stillLoading) {
                        CommonAdapterUtility.showPlaceHolderImage(imageView);
                    } else {
                        //show and save the bitmap
                        Bitmap loadedBitmap = imageContainer.getBitmap();
                        Bitmap savedBitmap = Bitmap.createBitmap(loadedBitmap);
                        imageView.setImageBitmap(loadedBitmap);
                        CommonAdapterUtility.saveThumbToLocalFile(fileName, savedBitmap);

                    }

                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                   // Log.e("", volleyError.getLocalizedMessage());
                    CommonAdapterUtility.showPlaceHolderImage(imageView);
                }
            });
        }
    }

    public static void setThumbnailImage( ListItem listItem, ImageView imageView) {
    /*This section will first look for the thumbnail in the assets folder.
    * If it is not found there, it will look to see if it was previously downloaded and saved to internal memory.
    * If it is not found there, it will download it from the server, and save it to internal memory for next time*/

        //load image from assets folder
        String fileName = listItem.getImageResource();
        try {
            InputStream stream = appContext.getAssets().open(fileName);
            Drawable drawable = Drawable.createFromStream(stream, null);
            imageView.setImageDrawable(drawable);

            //but if it's not in there, get it from the server after displaying a placeholder
        } catch (Exception e) {
            //load the saved image and display it
            try {
                CommonAdapterUtility.loadLocalSavedImage(fileName, imageView);
            } catch (FileNotFoundException localNotFoundError) {
                localNotFoundError.printStackTrace();
                //if that's not there, then get the real image from the server
                CommonAdapterUtility.loadImageFromServer(listItem,imageView);
            }
        }
    }

}
