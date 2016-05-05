package com.thecodebuilders.utility;

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;

import com.thecodebuilders.babysbrilliant.ListItem;

import java.io.File;

/**
 * Created by aaronnicholson on 8/27/15.
 */
public class Utils {
    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    /**
     * This method check the downloaded file is present in the device and also check
     * its fully downloaded or not
     * @param context context to call the file location method
     * @param fileLocation file location of specific media file
     * @param videoUrl video name of specific media
     * @return  return true if file is present.
     */
    public static boolean checkFileExist(Context context, String fileLocation, String videoUrl){
        File file = new File(fileLocation);
        Log.d("File", "FILE location: " + fileLocation);
        int fileLength = PreferenceStorage.returnFileLength(context, videoUrl);
        Log.d("File","FileLength:"+fileLength+" == "+file.length());
        if(file.exists() && (file.length() == fileLength))
            return true;
        else
            return false;
    }



}
