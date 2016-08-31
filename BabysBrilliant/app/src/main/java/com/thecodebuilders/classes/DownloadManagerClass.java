package com.thecodebuilders.classes;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;

import com.thecodebuilders.adapter.ElementViewHolder;
import com.thecodebuilders.adapter.ThumbnailListAdapter;
import com.thecodebuilders.babysbrilliant.ListItem;
import com.thecodebuilders.babysbrilliant.R;
import com.thecodebuilders.interfaces.DownloadStatusListener;
import com.thecodebuilders.utility.PreferenceStorage;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by admin on 08-Aug-16.
 */
public class DownloadManagerClass {

    private static final String TAG = "DownloadManagerClass";
    private Context context;
    private ElementViewHolder viewHolder;
    private ThumbnailListAdapter.ElementViewHolder elementViewHolder;
    private ListItem listItem;
    private String mUrl;
    private String videoName;
    private String progress;
    private DownloadStatusListener downloadStatusListener;
    private int position;
    private long downloadID;

    public DownloadManagerClass(Context context, ElementViewHolder viewHolder, ListItem listItem, DownloadStatusListener listener,
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

    public DownloadManagerClass(ThumbnailListAdapter.ElementViewHolder viewHolder, ListItem listItem, DownloadStatusListener listener,
                         int position, String videoName, Context context) {
        this.context = context;
        this.elementViewHolder = viewHolder;
        this.listItem = listItem;
        this.videoName = videoName;
        this.downloadStatusListener = listener;
        this.position = position;
        mUrl = context.getResources().getString(R.string.media_url) + videoName;
        if (listener.getClass().getSimpleName().equalsIgnoreCase("ThumbnailListAdapter"))
            listItem.setIsDownloading(true);

    }

    private void callDownloadManager(){
        HttpURLConnection connection = null;
        try {
            URL url = new URL(mUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            int lengthOfFile = connection.getContentLength();
            Log.d(TAG, "Length of file: " + lengthOfFile);
            PreferenceStorage.saveFileLength(context, videoName, lengthOfFile);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if(connection != null)
                connection.disconnect();
        }

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(mUrl));
        //   request.setTitle("File Download");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        request.setVisibleInDownloadsUi(false);
        String nameOfFile = URLUtil.guessFileName(mUrl, null, MimeTypeMap.getFileExtensionFromUrl(mUrl));

        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, nameOfFile);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
        downloadID = downloadManager.enqueue(request);
        listItem.setDownloadId(downloadID);
        viewHolder.progressBar.setVisibility(View.VISIBLE);
        viewHolder.thumbnailImage.setClickable(false);
        viewHolder.downloadIcon.setVisibility(View.GONE);
        if (downloadStatusListener.getClass().getSimpleName().equalsIgnoreCase("VideosAdapter")
                || downloadStatusListener.getClass().getSimpleName().equalsIgnoreCase("ThumbnailListAdapter"))
            listItem.setIsDownloading(true);
    }

    public  BroadcastReceiver downloadStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            Log.d(TAG,"DownloadID:"+referenceId);
            downloadStatusListener.onDownloadComplete(0);
        }
    };
}
