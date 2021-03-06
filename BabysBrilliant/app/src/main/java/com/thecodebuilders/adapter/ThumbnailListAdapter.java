package com.thecodebuilders.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.toolbox.ImageLoader;
import com.thecodebuilders.application.ApplicationContextProvider;
import com.thecodebuilders.babysbrilliant.ListItem;
import com.thecodebuilders.babysbrilliant.MainActivity;
import com.thecodebuilders.babysbrilliant.R;
import com.thecodebuilders.classes.DownloadAsync;
import com.thecodebuilders.interfaces.DownloadStatusListener;
import com.thecodebuilders.model.Playlist;
import com.thecodebuilders.network.VolleySingleton;
import com.thecodebuilders.utility.Utils;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


/**
 * Created by aaronnicholson on 8/17/15.
 */
public class ThumbnailListAdapter extends RecyclerView.Adapter<ThumbnailListAdapter.ElementViewHolder> implements DownloadStatusListener {
    private final String LOGVAR = "ThumbnailListAdapter";
    private ArrayList<ListItem> elements;
    private final Context appContext = ApplicationContextProvider.getContext();
    ArrayList<JSONArray> products = new ArrayList<JSONArray>();
    ArrayList<JSONObject> assetsList;
    MainActivity mainActivity;
    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;
    public static String priceValue = "$0.00";
    private boolean isMediaPlayingDone = true;
    private Toast toast;

    int listIncrement = 0; //for testing only

    private MediaPlayer mediaPlayer;
    private boolean isTappedSettings = true;

    public ThumbnailListAdapter(ArrayList listData, MainActivity mainActivity) {
        volleySingleton = VolleySingleton.getInstance();
        imageLoader = volleySingleton.getImageLoader();
        assetsList = listData;
        this.mainActivity = mainActivity;
        parseListItems(assetsList.size());
        toast = Toast.makeText(appContext, appContext.getResources().getString(R.string.another_media_play_progress), Toast.LENGTH_LONG);

    }

    public ThumbnailListAdapter(ArrayList listData, MainActivity mainActivity, boolean isTappedSettings) {
        volleySingleton = VolleySingleton.getInstance();
        imageLoader = volleySingleton.getImageLoader();
        assetsList = listData;
        this.mainActivity = mainActivity;
        this.isTappedSettings = isTappedSettings;
        parseListItems(assetsList.size());
        toast = Toast.makeText(appContext, appContext.getResources().getString(R.string.another_media_play_progress), Toast.LENGTH_LONG);

    }

    //This gets the data ready for the adapter to use in order to set up the view for each list item.
    //It takes the data from the ArrayList that is passed in and parses it into the ListItem class.
    //Then it adds each ListItem instance to the elements ArrayList.
    private void parseListItems(int listLength) {
        elements = new ArrayList<ListItem>(listLength);
        products = new ArrayList<JSONArray>();
        mainActivity.previewFileNameArray = new ArrayList<>();
        Log.d(LOGVAR, "Thumbnail:" + assetsList.toString());
        for (int i = 0; i < listLength; i++) {
            JSONObject rawJSON;
            String name;
            Boolean playInline = false;
            String imageResource;
            String price = appContext.getString(R.string.price_hard_coded);
            String category = "";
            String mediaFile = "";
            Boolean isPurchased = false;
            Boolean isFavorite = false;
            Boolean isSubcategory;
            Boolean isPlaylistItem = false;
            Boolean isPlaylist = false;

            try {
                JSONObject itemJSON = assetsList.get(i);
                //subcategories have a name field instead of a title field. We use that difference to determine if it is a product or subcategory item.
                //if it is a list of products

                if (!itemJSON.isNull("price"))
                    price = "$" + itemJSON.getString("price");
                if (itemJSON.isNull("name")) {
                    name = StringEscapeUtils.unescapeJava(itemJSON.getString("title"));
                    isSubcategory = false;
                    //if it is a list of subcategories
                } else {
                    name = itemJSON.getString("name");
                    if (!itemJSON.isNull("products"))
                        products.add(itemJSON.getJSONArray("products"));
                    isSubcategory = true;

                }

                //soundboards are meant to play inside their thumbnail. Anything marked with the playInline attribute in the JSON will do so.
                if (!itemJSON.isNull("playInline")) {
                    if (itemJSON.getString("playInline").equals("true")) playInline = true;
                }

                //look through items in the PURCHASED list
                //if this item is in there, mark it as purchased
                for (int purchasedIndex = 0; purchasedIndex < mainActivity.purchasedItems.length(); purchasedIndex++) {
                    //if it has no SKU, skip it
                    try {
                        if (!itemJSON.isNull("SKU")) {
                            //  if (!mainActivity.purchasedItems.getJSONObject(purchasedIndex).isNull("title")) {

                            if ((itemJSON.getString("SKU").equals(mainActivity.purchasedItems.getJSONObject(purchasedIndex).getString("SKU")))) {
                                isPurchased = true;
                            }
                            // }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }

                }

                //look through items in the FAVORITES list
                //if this item is in there, mark it as favorite
                for (int favoriteIndex = 0; favoriteIndex < mainActivity.favoriteItems.size(); favoriteIndex++) {
                    //if it has no SKU, skip it
                    if (!itemJSON.isNull("SKU")) {
                        if (itemJSON.getString("SKU").equals(mainActivity.favoriteItems.get(favoriteIndex).getString("SKU"))) {
                            isFavorite = true;
                        }
                    }
                }

                //look through each playlist on MainActivity
                for (int playlistIndex = 0; playlistIndex < mainActivity.playlists.size(); playlistIndex++) {
                    //look at each item in the playlist
                    Playlist playlist = mainActivity.playlists.get(playlistIndex);
                    for (int playlistItemIndex = 0; playlistItemIndex < playlist.playlistItems.size(); playlistItemIndex++) {
                        JSONObject playlistItem = playlist.playlistItems.get(playlistItemIndex);

                        //if our original item has no SKU, skip it
                        if (!itemJSON.isNull("SKU")) {
                            if (itemJSON.getString("SKU").equals(playlistItem.getString("SKU"))) {
                                //if this item matches the item in the playlist, mark it isPlaylistItem
                                isPlaylistItem = true;
                            }
                        }
                    }
                }

                rawJSON = itemJSON;
                imageResource = itemJSON.getString("thumb");
                if (!itemJSON.isNull("cat")) category = itemJSON.getString("cat");

                //for products, use the file attribute. For subcategories, use the preview attribute.
                if (!itemJSON.isNull("file")) {
                    mediaFile = itemJSON.getString("file");
                } else if (!itemJSON.isNull("preview")) {
                    mediaFile = itemJSON.getString("preview");
                }
                mainActivity.previewFileNameArray.add(mediaFile);
                //isPlaylist
//                if (!itemJSON.isNull("isPlaylist") && itemJSON.getString("isPlaylist").equals("true")) {
//                    isPlaylist = true;
//                }

                ListItem listItem = new ListItem(rawJSON, name, playInline, imageResource, mediaFile, price, category, isSubcategory,
                        isPurchased, isPlaylistItem, isPlaylist, isFavorite, appContext, false, MainActivity.DOWNLOAD_ID, mediaFile);

                elements.add(listItem);//TODO: make image dynamic
            } catch (Throwable t) {
                t.printStackTrace();
                //Log.e(LOGVAR, "JSON Error " + t.getMessage() + assetsList);
            }
        }

    }

    private void configureListItemListeners(ElementViewHolder viewHolder, final int position) {
        final ElementViewHolder thisViewHolder = viewHolder;
        viewHolder.thumbnailImage.setTag(position);
        viewHolder.thumbnailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTappedSettings)
                    thumbnailClicked(position, thisViewHolder);
            }
        });

        viewHolder.favoritesIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTappedSettings)
                    favoritesClicked(position, thisViewHolder);
            }
        });

        viewHolder.previewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTappedSettings)
                    previewClicked(position);
            }
        });

        viewHolder.playlistIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTappedSettings)
                    playlistClicked(position, thisViewHolder);
            }
        });


    }

    private void configureListItemLook(final ElementViewHolder viewHolder, final ListItem listItem, int pos) {

        viewHolder.videoView.setVisibility(View.INVISIBLE);

        viewHolder.favoritesIcon.setVisibility(View.INVISIBLE);
        viewHolder.playlistIcon.setVisibility(View.INVISIBLE);
        viewHolder.priceText.setVisibility(View.VISIBLE);

        //set text
        viewHolder.titleText.setText(listItem.getTitle());
        viewHolder.priceText.setText(listItem.getPrice());

        //set font
        viewHolder.titleText.setTypeface(mainActivity.proximaBold);
        viewHolder.priceText.setTypeface(mainActivity.proximaBold);
        viewHolder.previewIcon.setTypeface(mainActivity.fontAwesome);

        //hide text background for certain sections
        //for music, hide subcategory and product text background
        if (!listItem.doShowBackground()) {
            viewHolder.textBackground.setVisibility(View.INVISIBLE);
        }

        //for soundboards, hide product footer entirely
        if (!listItem.doShowText()) {
            viewHolder.titleText.setVisibility(View.INVISIBLE);
        }

        //set text size differently if it is a subcategory
        if (listItem.isSection()) {
            viewHolder.titleText.setTextSize(16);
        } else {
            viewHolder.titleText.setTextSize(13);
        }

        //hide price on subcategories
       /* if (!listItem.isPurchasable()) {
            viewHolder.priceText.setVisibility(View.INVISIBLE);
        }*/

        //if it has been purchased already
        if (listItem.isPurchased() || listItem.getPrice().equalsIgnoreCase(priceValue)) {
            setLookToPurchased(viewHolder);
        }

        if (listItem.isFavorite()) {
            setLookToFavorite(viewHolder);
        } else {
            setLookToNotFavorite(viewHolder);
        }

        if (listItem.isPlaylistItem()) {
            setLookToPlaylistItem(viewHolder);
        } else {
            setLookToNotPlaylistItem(viewHolder);
        }

        //TODO: Add check for null string of file name
        if (listItem.isSection() || !listItem.isPurchased()) {
            viewHolder.previewIcon.setVisibility(View.VISIBLE);
        } else {
            viewHolder.previewIcon.setVisibility(View.INVISIBLE);

        }

        //default icon look
        viewHolder.playlistIcon.setColorFilter(Color.WHITE);
        viewHolder.favoritesIcon.setColorFilter(Color.WHITE);

        if (listItem.isPlaylistItem()) {
            setLookToPlaylistItem(viewHolder);
        }

        if (listItem.isPlaylist()) {
            setLookToPlaylist(viewHolder);
        }
        setFileDownloadingListItem(viewHolder, listItem, pos);

        /*This section will first look for the thumbnail in the assets folder.
        * If it is not found there, it will look to see if it was previously downloaded and saved to internal memory.
        * If it is not found there, it will download it from the server, and save it to internal memory for next time*/

        //load image from assets folder
        String fileName = listItem.getImageResource();
        try {
            InputStream stream = appContext.getAssets().open(fileName);
            Drawable drawable = Drawable.createFromStream(stream, null);
            viewHolder.thumbnailImage.setImageDrawable(drawable);

            //but if it's not in there, get it from the server after displaying a placeholder
        } catch (Exception e) {
            //load the saved image and display it
            try {
                CommonAdapterUtility.loadLocalSavedImage(fileName, viewHolder.thumbnailImage);
            } catch (FileNotFoundException localNotFoundError) {
                localNotFoundError.printStackTrace();
                //if that's not there, then get the real image from the server
                CommonAdapterUtility.loadImageFromServer(listItem, viewHolder.thumbnailImage);
            }
        }
        if (listItem.getCategory().contains("5") && !listItem.isSection()) {
            viewHolder.priceText.setVisibility(View.GONE);
        }
        viewHolder.itemView.setTag(listItem);
    }

    private void previewClicked(int position) {
        ListItem listItem = elements.get(position);
        if (!listItem.getPreviewFile().equals("")) {
            mainActivity.isVideoClose = false;
            mainActivity.playingVideos(listItem.getPreviewFile());
        }
        else
            Toast.makeText(mainActivity, mainActivity.getResources()
                    .getString(R.string.preview_not_available), Toast.LENGTH_LONG).show();
    }

    private void favoritesClicked(int position, ElementViewHolder thisViewHolder) {
        ListItem listItem = elements.get(position);

        if (listItem.isFavorite()) {
            //TODO: remove from favorites
            mainActivity.removeFromFavorites(listItem.getRawJSON());
            listItem.isFavorite = false;
            setLookToNotFavorite(thisViewHolder);
        } else {
            listItem.isFavorite = true;
            mainActivity.addToFavorites(listItem.getRawJSON());
            setLookToFavorite(thisViewHolder);
        }
    }

    private void playlistClicked(int position, ElementViewHolder thisViewHolder) {
        ListItem listItem = elements.get(position);
        //remove listIncrement and replace with playlist chooser
//        listIncrement++;
        mainActivity.addToPlaylist(listItem.getRawJSON());


    }

    private void thumbnailClicked(int position, ElementViewHolder thisViewHolder) {
        ListItem listItem = elements.get(position);
        if (!elements.get(position).getIsDownloading()) {
            if (listItem.getCategory().contains("5") && !listItem.isSection()) {
                if (isMediaPlayingDone)
                    playOrOpen(position, listItem, thisViewHolder);
                else {
                    try {
                        if (!toast.getView().isShown())
                            toast.show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //Toast.makeText(appContext, appContext.getResources().getString(R.string.another_media_play_progress), Toast.LENGTH_LONG).show();
                }
            } else if ((listItem.isPurchasable() && listItem.isPurchased()) || listItem.getPrice().equalsIgnoreCase(priceValue)) {

                int checkAllFileExists = 0;
                for (int i = 0; i < products.get(position).length(); i++) {
                    try {
                        JSONArray array = new JSONArray("" + products.get(position));
                        JSONObject jsonObject = array.getJSONObject(i);
                        String fileLocation = mainActivity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + jsonObject.getString("file");

                        if (Utils.checkFileExist(mainActivity, fileLocation, jsonObject.getString("file"))) {
                            checkAllFileExists++;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                if (checkAllFileExists == products.get(position).length()) {
                    playOrOpen(position, listItem, thisViewHolder);
                } else {
                    alertForDownload(thisViewHolder, listItem, position);
                }

                // mainActivity.configureThumbnailList(products.get(position), "videos");
            } else {
                try {
                    mainActivity.addToPurchasedSoundBoards(listItem.getRawJSON(), listItem, thisViewHolder);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void setLookToFavorite(ElementViewHolder viewHolder) {
        viewHolder.favoritesIcon.setColorFilter(Color.YELLOW);
    }

    private void setLookToNotFavorite(ElementViewHolder viewHolder) {
        viewHolder.favoritesIcon.setColorFilter(Color.WHITE);
    }

    private void setLookToPurchased(ElementViewHolder viewHolder) {
        viewHolder.priceText.setVisibility(View.INVISIBLE);

        //TODO: hide these on soundboards?
        viewHolder.favoritesIcon.setVisibility(View.INVISIBLE);
        viewHolder.playlistIcon.setVisibility(View.INVISIBLE);
        //Log.d(LOGVAR, "set to visible");
    }

    private void setLookToPlaylistItem(ElementViewHolder viewHolder) {
        // viewHolder.priceText.setVisibility(View.INVISIBLE);
        viewHolder.playlistIcon.setColorFilter(Color.YELLOW);
    }

    private void setLookToNotPlaylistItem(ElementViewHolder viewHolder) {
        // viewHolder.priceText.setVisibility(View.INVISIBLE);
        viewHolder.playlistIcon.setColorFilter(Color.WHITE);

    }

    private void setLookToPlaylist(ElementViewHolder viewHolder) {
        viewHolder.previewIcon.setVisibility(View.INVISIBLE);
    }

    private void setFileDownloadingListItem(ThumbnailListAdapter.ElementViewHolder viewHolder, ListItem listItem, int position) {
        Log.d(LOGVAR, "setFileDownloadingListItem" + listItem.getIsDownloading() + ":" + listItem.getMediaFile());
        if (listItem.getIsDownloading()) {
            viewHolder.progressBar.setVisibility(View.VISIBLE);
            viewHolder.thumbnailImage.setClickable(false);

        } else {

            viewHolder.progressBar.setVisibility(View.GONE);
            viewHolder.thumbnailImage.setClickable(true);
        }

    }

    private void playOrOpen(int position, ListItem listItem, ElementViewHolder viewHolder) {
        //if it's a soundboard category
        if (listItem.isSection()) {
            //set up section title
            if (!assetsList.get(position).isNull("name")) {
                try {
                    mainActivity.setSectionTitle(assetsList.get(position).getString("name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            updateThumbnailList(position);
        } else {
            //if it has a file name, play it
            if (!listItem.getMediaFile().equals("")) {
                if (listItem.playInline()) {
                    //play inside the thumbnail
//                    mainActivity.playVideo(listItem.getMediaFile());
                    playInlineVideo(listItem.getMediaFile(), viewHolder, position
                    );
                } else {
                    //play in the main player
                    mainActivity.playVideo(listItem.getMediaFile(), false);
                }
            }
        }
    }

    public void playInlineVideo(String videoURL, final ElementViewHolder viewHolder, int position) {
        Log.d(LOGVAR, "play inline video");
        ListItem listItem = elements.get(position);

        String fileLocation = mainActivity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + listItem.getMediaFile();

        viewHolder.videoView.setVisibility(View.VISIBLE);

        isMediaPlayingDone = false;
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(fileLocation); //this triggers the listener, which plays the video

        } catch (IOException e) {
            e.printStackTrace();
        }


        //TODO: show preloader

        //TODO: put this on a separate thread
        //TODO: download the video to local storage, then play
        viewHolder.videoView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                final Surface s = new Surface(surface);
                Log.d(LOGVAR, "surface TRY block");

                try {
                    if (mediaPlayer != null) {
                        mediaPlayer.setSurface(s);
                        mediaPlayer.prepare();
                    }
//                    mediaPlayer.setOnBufferingUpdateListener(this);
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {


                            viewHolder.videoView.setAlpha(1);
                            if (mediaPlayer != null) {
                                mediaPlayer.stop();
                                mediaPlayer.release();
                                mediaPlayer = null;
                            }
                            isMediaPlayingDone = true;
                            Log.d(LOGVAR, "MediaPlayer Completion");
                            notifyDataSetChanged();
//                            viewHolder.videoView.setVisibility(View.INVISIBLE);
                        }
                    });
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            viewHolder.videoView.setAlpha(1);
                        }
                    });
//                    mediaPlayer.setOnVideoSizeChangedListener(this);
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            if (mediaPlayer != null) {
                                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                mediaPlayer.start();
                            }
                        }
                    });

                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (SecurityException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }


            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                Log.d(LOGVAR, "onSurfaceTextureDestroyed");
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });


    }

    private void updateThumbnailList(int position) {
        mainActivity.configureThumbnailList(products.get(position), "");
    }

    //attaches the xml layout doc to each menu item to configure it visually.
    @Override
    public ElementViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View rowView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_layout, viewGroup, false);

        return new ElementViewHolder(rowView);
    }

    //this happens when a new list is set up. The views are configured with the specific data for each menu item and click listeners are configured.
    @Override
    public void onBindViewHolder(ElementViewHolder viewHolder, final int position) {

        final ListItem rowData = elements.get(position);

        configureListItemLook(viewHolder, rowData, position);

        configureListItemListeners(viewHolder, position);


    }

    @Override
    public int getItemCount() {
        return elements.size();
    }


    @Override
    public void onDownloadComplete(int position) {

        int checkAllFileExists = 0;
        for (int i = 0; i < products.get(position).length(); i++) {
            try {
                JSONArray array = new JSONArray("" + products.get(position));
                JSONObject jsonObject = array.getJSONObject(i);
                String fileLocation = mainActivity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + jsonObject.getString("file");
                if (Utils.checkFileExist(mainActivity, fileLocation, jsonObject.getString("file"))) {
                    checkAllFileExists++;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        if (checkAllFileExists == products.get(position).length()) {
            notifyDataSetChanged();
        }
    }

    private void alertForDownload(final ThumbnailListAdapter.ElementViewHolder viewHolder, final ListItem listItem, final int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mainActivity);
        alertDialog.setTitle(mainActivity.getResources().getString(R.string.alert_download_title));
        alertDialog.setMessage(mainActivity.getResources().getString(R.string.alert_download_summary));
        alertDialog.setPositiveButton(mainActivity.getResources().getString(R.string.download), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        viewHolder.progressBar.setVisibility(View.VISIBLE);
                        viewHolder.thumbnailImage.setClickable(false);
                        for (int i = 0; i < products.get(position).length(); i++) {
                            try {
                                JSONArray array = new JSONArray("" + products.get(position));
                                JSONObject jsonObject = array.getJSONObject(i);
                                // mainActivity.downloadVideo(viewHolder, listItem);

                                String fileLocation = mainActivity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + jsonObject.getString("file");
                                if (!Utils.checkFileExist(mainActivity, fileLocation, jsonObject.getString("file"))) {
                                    new DownloadAsync(viewHolder, listItem, ThumbnailListAdapter.this, position, jsonObject.getString("file"), mainActivity).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    }
                }

        );
        alertDialog.setNegativeButton(mainActivity.getResources().

                        getString(R.string.cancel),

                null);
        AlertDialog builder = alertDialog.create();
        builder.show();
    }


    //just sets java handles for the layout items configured in the xml doc.
    public class ElementViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleText;
        public final ImageView thumbnailImage;
        public final TextView priceText;
        private final RelativeLayout textBackground;
        private final ImageView favoritesIcon;
        private final ImageView playlistIcon;
        private final TextView previewIcon;
        private final TextureView videoView;
        private final RelativeLayout listItemContainer;
        private final ProgressBar progressBar;

        private final RelativeLayout rel1;
        VideoView videoHolder;


        public ElementViewHolder(View itemView) {
            super(itemView);
            thumbnailImage = (ImageView) itemView.findViewById(R.id.thumbnailImage);
            titleText = (TextView) itemView.findViewById(R.id.titleText);
            priceText = (TextView) itemView.findViewById(R.id.priceText);
            textBackground = (RelativeLayout) itemView.findViewById(R.id.textBackground);
            favoritesIcon = (ImageView) itemView.findViewById(R.id.favorites_icon);
            playlistIcon = (ImageView) itemView.findViewById(R.id.playlist_icon);
            previewIcon = (TextView) itemView.findViewById(R.id.preview_icon);
            videoView = (TextureView) itemView.findViewById(R.id.video_view_inline);
            listItemContainer = (RelativeLayout) itemView.findViewById(R.id.list_item_container);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);


            rel1 = (RelativeLayout) itemView.findViewById(R.id.rel1);
            videoHolder = (VideoView) itemView.findViewById(R.id.videoView1);
        }

    }
}
