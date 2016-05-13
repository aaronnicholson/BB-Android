package com.thecodebuilders.adapter;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thecodebuilders.babysbrilliant.ListItem;
import com.thecodebuilders.babysbrilliant.R;
import com.thecodebuilders.utility.PreferenceStorage;
import com.thecodebuilders.utility.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class DownloadPurchaseContentAdapter extends BaseAdapter {


    private Context context;
    private ArrayList<HashMap<String, String>> postItems;
    private HashMap<String, String> map;
    private ViewHolder holder = null;
    private ArrayList<Boolean> showProgressArray = new ArrayList<Boolean>();


    public DownloadPurchaseContentAdapter(Context context,
                                          ArrayList<HashMap<String, String>> arraylist) {
        this.context = context;

        postItems = arraylist;
        for (int i = 0; i < arraylist.size(); i++) {
            showProgressArray.add(i, false); // initializes all items value with false
        }


    }

    @Override
    public int getCount() {
        return postItems.size();
    }

    @Override
    public Object getItem(int position) {
        return postItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.row_download_purchase_content, null);
            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView
                    .findViewById(R.id.title);
            holder.txtDate = (TextView) convertView.findViewById(R.id.date);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
            holder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.relative_layout);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        map = new HashMap<String, String>();
        map = postItems.get(position);

        holder.txtDate.setVisibility(View.GONE);
        holder.txtTitle.setText(map.get("title"));
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String videoURL = map.get("file");
                String fileLocation = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + videoURL;
                if (Utils.checkFileExist(context, fileLocation, videoURL)) {
                    Log.d("DownloadPurchaseAdapter", "FILE EXISTS");
                } else {
                    Log.e("Position", "..." + position);
                    showProgressArray.set(position, true);
                    holder.progressBar.setVisibility(View.VISIBLE);
                    downloadVideo(videoURL, holder.progressBar, position);
                    notifyDataSetChanged();
                }
            }
        });
        Log.e("PositionCall", ".."+position+"::"+showProgressArray.get(position));
        if(showProgressArray.get(position))
            holder.progressBar.setVisibility(View.VISIBLE);
        else
            holder.progressBar.setVisibility(View.GONE);
        return convertView;
    }

    public static class ViewHolder {
        public TextView txtTitle;
        public TextView txtDate;
        public ProgressBar progressBar;
        public RelativeLayout relativeLayout;
    }

    public  void downloadVideo(final String videoUrl, final ProgressBar progressBar, final int position) {
        String mUrl;
        mUrl = context.getResources().getString(R.string.media_url) + videoUrl;
        // mUrl = "http://goo.gl/Mfyya";

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(mUrl));
        //   request.setTitle("File Download");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        request.setVisibleInDownloadsUi(false);
        String nameOfFile = URLUtil.guessFileName(mUrl, null, MimeTypeMap.getFileExtensionFromUrl(mUrl));

        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, nameOfFile);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
        final long downloadID = downloadManager.enqueue(request);
        // viewHolder.downloadProgressShow.startAnimation(animationBlink);

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean downloading = true;
                DownloadManager downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
                while (downloading) {
                    DownloadManager.Query q = new DownloadManager.Query();
                    q.setFilterById(downloadID);
                    Cursor cursor = downloadManager.query(q);
                    cursor.moveToFirst();
                    final int bytesDownloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    int bytesTotal = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    final int dl_progress = (int) ((bytesDownloaded * 100l) / bytesTotal);
                    Log.d("Adapter", "Download:" + dl_progress + ":" + bytesDownloaded + " Total Length:" + bytesTotal +":"+
                            cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)));
                    PreferenceStorage.saveFileLength(context, videoUrl, bytesTotal);
                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                        downloading = false;
                        Activity activity = (Activity)context;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                               showProgressArray.set(position, false);
                            }
                        });
                       // listItem.setIsDownloading(false);
                    }
                    else if(cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_RUNNING){
                       // listItem.setIsDownloading(true);
                    }

                }
            }
        }).start();


    }
}
