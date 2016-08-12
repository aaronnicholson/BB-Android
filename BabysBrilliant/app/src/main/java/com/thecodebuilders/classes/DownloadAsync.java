package com.thecodebuilders.classes;

/**
 * Created by admin on 19-May-16.
 */


import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.thecodebuilders.adapter.ElementViewHolder;
import com.thecodebuilders.adapter.ThumbnailListAdapter;
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
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class DownloadAsync extends AsyncTask<String, String, String> {

    private static final String TAG = "DownloadAsync";
    private static final int BYTE_SIZE = 512;
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

    public DownloadAsync(ThumbnailListAdapter.ElementViewHolder viewHolder, ListItem listItem, DownloadStatusListener listener,
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

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... urlString) {
        InputStream input = null;
        OutputStream output = null;
        HttpsURLConnection connection = null;
        URL url = null;

        try {
            url = new URL(mUrl);
            trustAllHosts();
            connection = (HttpsURLConnection) url.openConnection();
            connection.setHostnameVerifier(doNotVerifyHost);

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            // connection.setRequestProperty("Connection","Keep-Alive");
            connection.connect();
            connection.setConnectTimeout(CONNECT_TIME_OUT);
            connection.setReadTimeout(READ_TIME_OUT);
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), videoName);

            int lengthOfFile = connection.getContentLength();
            Log.d(TAG, "Length of file: " + lengthOfFile);
            PreferenceStorage.saveFileLength(context, videoName, lengthOfFile);

            input = new BufferedInputStream(url.openStream());
            output = new FileOutputStream(file);

            byte data[] = new byte[BYTE_SIZE];
            int count = 0;
            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress("" + (int) ((total * 100) / lengthOfFile));
                output.write(data, 0, count);
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (output != null) {
                    output.flush();
                    output.close();
                }
                if (input != null)
                    input.close();
                if (connection != null)
                    connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return null;
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
        if (progress.equalsIgnoreCase("100")) {
            if (downloadStatusListener.getClass().getSimpleName().equalsIgnoreCase("VideosAdapter")) {
                listItem.setIsDownloading(false);
                viewHolder.thumbnailImage.setClickable(true);
            } else if (downloadStatusListener.getClass().getSimpleName().equalsIgnoreCase("ThumbnailListAdapter")) {
                listItem.setIsDownloading(false);
                // elementViewHolder.thumbnailImage.setClickable(true);
            }


            downloadStatusListener.onDownloadComplete(position);
        }

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
