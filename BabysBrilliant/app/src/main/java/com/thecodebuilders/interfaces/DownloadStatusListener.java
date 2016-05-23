package com.thecodebuilders.interfaces;

/**
 * Created by admin on 23-May-16.
 */
public interface DownloadStatusListener {

    /**
     * Called when the video download is complete.
     * @param position listview item position
     */
    void onDownloadComplete (int position);
}
