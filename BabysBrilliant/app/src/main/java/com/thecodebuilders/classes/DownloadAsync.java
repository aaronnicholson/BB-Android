package com.thecodebuilders.classes;

/**
 * Created by admin on 19-May-16.
 */


import android.content.Context;
import android.content.ContextWrapper;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;


import com.thecodebuilders.adapter.ElementViewHolder;
import com.thecodebuilders.babysbrilliant.ListItem;
import com.thecodebuilders.babysbrilliant.R;
import com.thecodebuilders.interfaces.DownloadStatusListener;
import com.thecodebuilders.utility.PreferenceStorage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;


public class DownloadAsync extends AsyncTask<String, String, String> {

    private static final String TAG = "DownloadAsync";
    private Context context;
    private ElementViewHolder viewHolder;
    private ListItem listItem;
    private String mUrl;
    private String videoName;
    private String progress;
    private DownloadStatusListener downloadStatusListener;
    private int position;

    public DownloadAsync(Context context, ElementViewHolder viewHolder, ListItem listItem, DownloadStatusListener listener,
                         int position, String videoName) {
        this.context = context;
        this.viewHolder = viewHolder;
        this.listItem = listItem;
        this.videoName = videoName;
        this.downloadStatusListener = listener;
        this.position = position;
        mUrl = context.getResources().getString(R.string.media_url) + videoName;
        if (listener.getClass().getSimpleName().equalsIgnoreCase("VideosAdapter"))
            listItem.setIsDownloading(true);

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... urlString) {
        try {

            URL url = new URL(mUrl);
            URLConnection connection = url.openConnection();
            connection.connect();
           /* File myDirectory = new File(
                    Environment.getExternalStorageDirectory(), "/" + context.getResources().getString(R.string.app_name));
            if (!myDirectory.exists())
                myDirectory.mkdir();*/
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),videoName);


            int lengthOfFile = connection.getContentLength();
            Log.d(TAG, "Length of file: " + lengthOfFile);
            PreferenceStorage.saveFileLength(context, videoName, lengthOfFile);

            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(file);

            byte data[] = new byte[1024];
            int count = 0;
            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress("" + (int) ((total * 100) / lengthOfFile));
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        Log.v(TAG, "Percentage::" + videoName + " :" + progress[0]+" : "+downloadStatusListener.getClass().getSimpleName());
        this.progress = progress[0];
        if (progress[0].equalsIgnoreCase("100")) {
            if (downloadStatusListener.getClass().getSimpleName().equalsIgnoreCase("VideosAdapter")) {
                listItem.setIsDownloading(false);
                viewHolder.thumbnailImage.setClickable(true);
            }
        } else if (downloadStatusListener.getClass().getSimpleName().equalsIgnoreCase("VideosAdapter"))
            listItem.setIsDownloading(true);

        //Log.e("Downlaoddd", ".." + listItem.getIsDownloading());

    }

    @Override
    protected void onPostExecute(String response) {
        if (progress.equalsIgnoreCase("100")) {
            if(downloadStatusListener.getClass().getSimpleName().equalsIgnoreCase("VideosAdapter")) {
                listItem.setIsDownloading(false);
                viewHolder.thumbnailImage.setClickable(true);
            }
            downloadStatusListener.onDownloadComplete(position);
        }

    }


}
