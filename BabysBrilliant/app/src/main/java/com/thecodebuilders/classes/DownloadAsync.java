package com.thecodebuilders.classes;

/**
 * Created by admin on 19-May-16.
 */


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.thecodebuilders.adapter.ElementViewHolder;
import com.thecodebuilders.adapter.ThumbnailListAdapter;
import com.thecodebuilders.babysbrilliant.ListItem;
import com.thecodebuilders.babysbrilliant.R;
import com.thecodebuilders.interfaces.DownloadStatusListener;
import com.thecodebuilders.utility.PreferenceStorage;
import com.thecodebuilders.utility.Utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class DownloadAsync extends AsyncTask<String, String, String> {

    private static final String TAG = "DownloadAsync";
    private static final int BYTE_SIZE = 4096;
    private static final int CONNECT_TIME_OUT = 5000;
    private static final int READ_TIME_OUT = 15000;
    private Context context;
    private ElementViewHolder viewHolder;
    private ThumbnailListAdapter.ElementViewHolder elementViewHolder;
    private ListItem listItem;
    private String mUrl;
    private String videoName;
    private String progress;
    private DownloadStatusListener downloadStatusListener;
    private int position;
    private Handler mHandler;

    public DownloadAsync(Context context, ElementViewHolder viewHolder, ListItem listItem, DownloadStatusListener listener,
                         int position, String videoName) {
        this.context = context;
        this.viewHolder = viewHolder;
        this.listItem = listItem;
        this.videoName = videoName;
        this.downloadStatusListener = listener;
        this.position = position;
        mHandler = new Handler(Looper.getMainLooper());
        mUrl = context.getResources().getString(R.string.media_url) + videoName;
        if (listener.getClass().getSimpleName().equalsIgnoreCase("VideosAdapter"))
            listItem.setIsDownloading(true);

    }

    public DownloadAsync(ThumbnailListAdapter.ElementViewHolder viewHolder, ListItem listItem, DownloadStatusListener listener,
                         int position, String videoName, Context context) {
        this.context = context;
        this.elementViewHolder = viewHolder;
        this.listItem = listItem;
        this.videoName = videoName;
        this.downloadStatusListener = listener;
        this.position = position;
        mHandler = new Handler(Looper.getMainLooper());
        mUrl = context.getResources().getString(R.string.media_url) + videoName;
        if (listener.getClass().getSimpleName().equalsIgnoreCase("ThumbnailListAdapter"))
            listItem.setIsDownloading(true);

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... urlString) {
        InputStream input = null;
        OutputStream output = null;
        URLConnection connection = null;
        URL url = null;
        String response = null;

       /* try {
            url = new URL(mUrl);
            trustAllHosts();
            connection = (HttpsURLConnection) url.openConnection();
            connection.setHostnameVerifier(doNotVerifyHost);

            // connection.setRequestProperty("Connection","Keep-Alive");
            connection.connect();
            //connection.setConnectTimeout(CONNECT_TIME_OUT);
            //connection.setReadTimeout(READ_TIME_OUT);

        } catch (Exception e) {
            e.printStackTrace();
        }
      */
        try {
            url = new URL(mUrl);
            connection = url.openConnection();
            connection.connect();

            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), videoName);

            int fileLength = connection.getContentLength();
            PreferenceStorage.saveFileLength(context, videoName, fileLength);
            input = new BufferedInputStream(connection.getInputStream());
            output = new FileOutputStream(file);

            byte data[] = new byte[BYTE_SIZE];
            long total = 0;
            int count = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress("" + (int) ((total * 100) / fileLength));
                output.write(data, 0, count);
            }


        } catch (SSLException sslexception) {
            sslexception.printStackTrace();
            response = "SSLException";
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (output != null) {
                    output.flush();
                    output.close();
                }
                if (input != null)
                    input.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return response;
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        Log.v(TAG, "Percentage::" + videoName + " :" + progress[0] + " : " + downloadStatusListener.getClass().getSimpleName());
        this.progress = progress[0];
        if (progress[0].equalsIgnoreCase("100")) {
            if (downloadStatusListener.getClass().getSimpleName().equalsIgnoreCase("VideosAdapter")) {
                listItem.setIsDownloading(false);
                viewHolder.thumbnailImage.setClickable(true);
            } else if (downloadStatusListener.getClass().getSimpleName().equalsIgnoreCase("ThumbnailListAdapter")) {
                listItem.setIsDownloading(false);
                //elementViewHolder.thumbnailImage.setClickable(true);
            }
        } else if (downloadStatusListener.getClass().getSimpleName().equalsIgnoreCase("VideosAdapter")
                || downloadStatusListener.getClass().getSimpleName().equalsIgnoreCase("ThumbnailListAdapter"))
            listItem.setIsDownloading(true);


    }

    @Override
    protected void onPostExecute(String response) {
        Log.d("Res", "..." + response);
        if (response.equalsIgnoreCase("SSLException"))
            Utils.showNetworkErrorDialog(context);
        if (progress != null) {
            if (progress.equalsIgnoreCase("100")) {
                if (downloadStatusListener.getClass().getSimpleName().equalsIgnoreCase("VideosAdapter")) {
                    listItem.setIsDownloading(false);
                    viewHolder.thumbnailImage.setClickable(true);
                } else if (downloadStatusListener.getClass().getSimpleName().equalsIgnoreCase("ThumbnailListAdapter")) {
                    listItem.setIsDownloading(false);
                    // elementViewHolder.thumbnailImage.setClickable(true);
                }
            } else {
                listItem.setIsDownloading(false);
                viewHolder.thumbnailImage.setClickable(true);
            }

        } else {
            listItem.setIsDownloading(false);
            viewHolder.thumbnailImage.setClickable(true);
        }
        if (downloadStatusListener != null)
            downloadStatusListener.onDownloadComplete(position);

    }


    final static HostnameVerifier doNotVerifyHost = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    /**
     * Trust every server - dont check for any certificate
     */
    private static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
