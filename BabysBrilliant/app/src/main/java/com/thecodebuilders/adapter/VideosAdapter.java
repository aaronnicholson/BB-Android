package com.thecodebuilders.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.thecodebuilders.application.ApplicationContextProvider;
import com.thecodebuilders.babysbrilliant.ListItem;
import com.thecodebuilders.babysbrilliant.MainActivity;
import com.thecodebuilders.babysbrilliant.R;
import com.thecodebuilders.classes.DownloadAsync;
import com.thecodebuilders.classes.DownloadService;
import com.thecodebuilders.interfaces.DownloadStatusListener;
import com.thecodebuilders.model.Playlist;
import com.thecodebuilders.network.VolleySingleton;
import com.thecodebuilders.utility.PreferenceStorage;
import com.thecodebuilders.utility.Utils;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Target;
import java.util.ArrayList;


/**
 * Created by aaronnicholson on 8/17/15.
 */
public class VideosAdapter extends RecyclerView.Adapter<ElementViewHolder> implements DownloadStatusListener {

    private final String LOGVAR = "VideosAdapter";
    private ArrayList<ListItem> elements;
    private final Context appContext = ApplicationContextProvider.getContext();
    ArrayList<JSONObject> assetsList;
    MainActivity mainActivity;
    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;
    public String mediaURL = appContext.getString(R.string.media_url);
    public static String priceValue = "$0.00";
    private String purchased;
    public ElementViewHolder elementViewHolder;
    private boolean isTappedSetting = true;

    public VideosAdapter(ArrayList listData, MainActivity mainActivity, String purchased) {
        volleySingleton = VolleySingleton.getInstance();
        imageLoader = volleySingleton.getImageLoader();
        assetsList = listData;
        this.mainActivity = mainActivity;
        this.purchased = purchased;
        parseListItems(assetsList.size());
        //checkDownloadingProgress(assetsList.size());
        Log.d(LOGVAR, "VIDEO assets: " + assetsList);

    }

    public VideosAdapter(ArrayList listData, MainActivity mainActivity, String purchased,
                         boolean isSettingTapped) {
        volleySingleton = VolleySingleton.getInstance();
        imageLoader = volleySingleton.getImageLoader();
        assetsList = listData;
        this.mainActivity = mainActivity;
        this.isTappedSetting = isSettingTapped;
        this.purchased = purchased;
        parseListItems(assetsList.size());
        //checkDownloadingProgress(assetsList.size());
        Log.d(LOGVAR, "VIDEO assets: " + assetsList);

    }

    //This gets the data ready for the adapter to use in order to set up the view for each list item.
    //It takes the data from the ArrayList that is passed in and parses it into the ListItem class.
    //Then it adds each ListItem instance to the elements ArrayList.
    private void parseListItems(int listLength) {
        elements = new ArrayList<ListItem>(listLength);
        mainActivity.mediaFileNameArray = new ArrayList<>();
        mainActivity.previewFileNameArray = new ArrayList<>();
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
            Boolean isSection = false;
            Boolean isPlaylistItem = false;
            Boolean isPlaylist = false;
            String previewFile = "";

            try {
                JSONObject itemJSON = assetsList.get(i);
                name = StringEscapeUtils.unescapeJava(itemJSON.getString("title"));
                if (!itemJSON.isNull("price"))
                    price = "$" + itemJSON.getString("price");
                for (int purchasedIndex = 0; purchasedIndex < mainActivity.purchasedItems.length(); purchasedIndex++) {
                    //if it has no SKU, skip it
                    try {
                        if (!itemJSON.isNull("SKU")) {
                            if (itemJSON.getString("SKU").equals(mainActivity.purchasedItems.getJSONObject(purchasedIndex).getString("SKU"))) {
                                isPurchased = true;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (!itemJSON.isNull("SKU")) {
                            if (itemJSON.getString("SKU").equals(mainActivity.purchasedItems.getJSONObject(purchasedIndex).getString("SKU"))) {
                                isPurchased = true;
                            }

                        }
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


                imageResource = itemJSON.getString("thumb");
                if (!itemJSON.isNull("cat")) category = itemJSON.getString("cat");


                boolean isSoundBoard = false;
                if (!itemJSON.isNull("catT"))
                    if (purchased.equalsIgnoreCase("purchased"))
                        isSoundBoard = itemJSON.getString("catT").equalsIgnoreCase("Soundboard");
                mediaFile = itemJSON.getString("file");
                previewFile = itemJSON.getString("preview");
                if (!isSoundBoard && !purchased.equalsIgnoreCase("purchased"))
                    mainActivity.previewFileNameArray.add(previewFile);
                if (purchased.equalsIgnoreCase("purchased")) {
                    if (isPurchased && !isSoundBoard)
                        mainActivity.mediaFileNameArray.add(mediaFile);
                } else {
                    mainActivity.mediaFileNameArray.add(mediaFile);
                }
                if (!isSoundBoard) {
                    ListItem listItem = new ListItem(itemJSON, name, playInline, imageResource, mediaFile, price, category, isSection,
                            isPurchased, isPlaylistItem, isPlaylist, isFavorite, appContext, false, MainActivity.DOWNLOAD_ID, previewFile);

                    elements.add(listItem);//TODO: make image dynamic
                }
            } catch (Throwable t) {
                t.printStackTrace();
                //Log.e(LOGVAR, "JSON Error " + t.getMessage() + assetsList);
            }
        }

    }

    private void configureListItemListeners(final ElementViewHolder viewHolder, final int position) {
        final ElementViewHolder thisViewHolder = viewHolder;
        viewHolder.thumbnailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!elements.get(position).getIsDownloading()) {
                    if (isTappedSetting) {
                        thumbnailClicked(position, thisViewHolder);
                    } else {


                    }

                }
            }
        });

        viewHolder.favoritesIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!elements.get(position).getIsDownloading()) {
                    if (isTappedSetting)
                        favoritesClicked(position, thisViewHolder);
                }
            }
        });

        viewHolder.playlistIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!elements.get(position).getIsDownloading()) {
                    if (isTappedSetting) {
                        ListItem listItem = elements.get(position);
                        String fileLocation = mainActivity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + listItem.getMediaFile();
                        if (Utils.checkFileExist(mainActivity, fileLocation, listItem.getMediaFile())) {
                            playlistClicked(position, thisViewHolder);
                        } else {
                            alertForDownload(thisViewHolder, listItem, position);
                        }
                    }
                }

            }
        });
        viewHolder.downloadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTappedSetting) {
                    ListItem listItem = elements.get(position);
                    //mainActivity.downloadVideo(viewHolder, listItem);
                    new DownloadAsync(mainActivity, viewHolder, listItem, VideosAdapter.this, position, listItem.getMediaFile()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    viewHolder.progressBar.setVisibility(View.VISIBLE);
                    viewHolder.thumbnailImage.setClickable(false);
                    viewHolder.downloadIcon.setVisibility(View.GONE);
                }
            }
        });

        viewHolder.previewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTappedSetting)
                    previewClicked(position);
            }
        });
    }

    private void previewClicked(int position) {
        ListItem listItem = elements.get(position);
        mainActivity.indexOfCurrentlyPreviewVideo = mainActivity.previewFileNameArray.indexOf(listItem.getPreviewFile());
        if (!listItem.getPreviewFile().equals("")) {
            mainActivity.isVideoClose = false;
            mainActivity.playingVideos(listItem.getPreviewFile());
        }
        else
            Toast.makeText(mainActivity, mainActivity.getResources().getString(R.string.preview_not_available),
                    Toast.LENGTH_LONG).show();
    }

    private void configureListItemLook(final ElementViewHolder viewHolder, final ListItem listItem) {

        viewHolder.videoView.setVisibility(View.INVISIBLE);

        viewHolder.favoritesIcon.setVisibility(View.GONE);
        viewHolder.playlistIcon.setVisibility(View.GONE);
        viewHolder.downloadIcon.setVisibility(View.GONE);
        viewHolder.progressBar.setVisibility(View.INVISIBLE);
        viewHolder.priceText.setVisibility(View.VISIBLE);

        //set text
        viewHolder.titleText.setText(listItem.getTitle());
        viewHolder.priceText.setText(listItem.getPrice());

        //set font
        viewHolder.titleText.setTypeface(mainActivity.proximaBold);
        viewHolder.priceText.setTypeface(mainActivity.proximaBold);

        //default icon look
        viewHolder.playlistIcon.setColorFilter(Color.WHITE);
        viewHolder.favoritesIcon.setColorFilter(Color.WHITE);

        //hide text background for certain sections
        //for music, hide subcategory and product text background
        if (!listItem.doShowBackground()) {
            // viewHolder.textBackground.setVisibility(View.INVISIBLE);
            viewHolder.textBackground.setBackgroundColor(appContext.getResources().getColor(android.R.color.transparent));
        } else {
            viewHolder.textBackground.setBackgroundColor(appContext.getResources().getColor(R.color.red));
        }

        viewHolder.titleText.setTextSize(13);


        //if it has been purchased already or free
        if (listItem.isPurchased() || listItem.getPrice().equalsIgnoreCase(priceValue)
                || !listItem.isPurchasable()) {
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
        if (listItem.getCategory().equals("5") && !listItem.isSection()) {
            setFileDownloadedListItem(viewHolder, listItem);
        } else {
            if (listItem.isPurchased())
                setFileDownloadedListItem(viewHolder, listItem);
        }
        setFileDownloadingListItem(viewHolder, listItem);

        CommonAdapterUtility.setThumbnailImage(listItem, viewHolder.thumbnailImage);
        /*if(isCalledSettingOpen.equalsIgnoreCase("Yes")){
            Log.d("Call",".."+isViewClickable);
            if(isViewClickable)
                viewHolder.thumbnailImage.setClickable(true);
            else
                viewHolder.thumbnailImage.setClickable(false);
        }*/

        viewHolder.itemView.setTag(listItem);
        viewHolder.progressBar.setTag(listItem);

        viewHolder.previewIcon.setTypeface(mainActivity.fontAwesome);
        if (purchased.equalsIgnoreCase("purchased") || listItem.isPurchased())
            viewHolder.previewIcon.setVisibility(View.INVISIBLE);
        else
            viewHolder.previewIcon.setVisibility(View.VISIBLE);
    }

    private void favoritesClicked(int position, ElementViewHolder thisViewHolder) {
        ListItem listItem = elements.get(position);
        Log.e(LOGVAR, "Favourite:" + listItem.isFavorite());
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
        String favoriteString = mainActivity.favoriteItems.toString();
        PreferenceStorage.saveFavourites(appContext, PreferenceStorage.FAVOURITE_SAVE, favoriteString);
    }

    private void playlistClicked(int position, ElementViewHolder thisViewHolder) {
        ListItem listItem = elements.get(position);
        mainActivity.addToPlaylist(listItem.getRawJSON());
    }

    private void thumbnailClicked(int position, ElementViewHolder thisViewHolder) {
        ListItem listItem = elements.get(position);

        if ((listItem.isPurchasable() && listItem.isPurchased()) || listItem.getPrice().equalsIgnoreCase(priceValue)) {

            String videoURL = listItem.getMediaFile();
            String fileLocation = mainActivity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + videoURL;
            Log.e(LOGVAR, ".." + listItem.getMediaFile() + "::" + mainActivity.mediaFileNameArray.toString());
            if (Utils.checkFileExist(mainActivity, fileLocation, videoURL)) {
                //when playing video and we minimize the app and again open that time setOnPreparedListener set null
                //because of that set false to videoClose
                mainActivity.isVideoClose = false;
                Log.d(LOGVAR, "FILE EXISTS");
                mainActivity.indexOfCurrentlyPlayingVideo = mainActivity.mediaFileNameArray.indexOf(listItem.getMediaFile());
                Log.d(LOGVAR, "index:" + mainActivity.indexOfCurrentlyPlayingVideo);
                mainActivity.playVideo(fileLocation, false);
                //otherwise, go get it
            } else {
                Log.d(LOGVAR, "FILE DOES NOT EXIST");
                //TODO: Stop multiple downloads of the same file
                alertForDownload(thisViewHolder, listItem, position);
            }

        } else {
            //TODO: do actual purchase round trip here
            // listItem.setIsPurchased(true);
            //  setLookToPurchased(thisViewHolder);
            if (listItem.getCategory().equals("5") && !listItem.isSection()) {
                String videoURL = listItem.getMediaFile();
                String fileLocation = mainActivity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + videoURL;
                if (Utils.checkFileExist(mainActivity, fileLocation, videoURL)) {
                    Log.d(LOGVAR, "FILE EXISTS");
                    mainActivity.playVideo(fileLocation, false);
                    //otherwise, go get it
                } else {
                    Log.d(LOGVAR, "FILE DOES NOT EXIST");
                    //TODO: Stop multiple downloads of the same file
                    alertForDownload(thisViewHolder, listItem, position);
                }
            } else {
                try {
                    mainActivity.addToPurchased(listItem.getRawJSON(), listItem, thisViewHolder);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setLookToFavorite(ElementViewHolder viewHolder) {
        Log.d(LOGVAR, "SET LOOK FAV");
        viewHolder.favoritesIcon.setColorFilter(Color.YELLOW);
    }

    private void setLookToNotFavorite(ElementViewHolder viewHolder) {
        viewHolder.favoritesIcon.setColorFilter(Color.WHITE);
    }

    private void setLookToPurchased(ElementViewHolder viewHolder) {
        viewHolder.priceText.setVisibility(View.INVISIBLE);

        //TODO: hide these on soundboards?
        viewHolder.favoritesIcon.setVisibility(View.VISIBLE);
        viewHolder.playlistIcon.setVisibility(View.VISIBLE);
        viewHolder.downloadIcon.setVisibility(View.VISIBLE);
        //Log.d(LOGVAR, "set to visible");
    }

    private void setLookToPlaylistItem(ElementViewHolder viewHolder) {
//        viewHolder.priceText.setVisibility(View.INVISIBLE);
        viewHolder.playlistIcon.setColorFilter(Color.YELLOW);
    }

    private void setFileDownloadedListItem(ElementViewHolder viewHolder, ListItem listItem) {
        String videoURL = listItem.getMediaFile();
        String fileLocation = mainActivity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + videoURL;
        if (Utils.checkFileExist(mainActivity, fileLocation, videoURL))
            viewHolder.downloadIcon.setVisibility(View.GONE);
        else
            viewHolder.downloadIcon.setVisibility(View.VISIBLE);
    }

    private void setFileDownloadingListItem(ElementViewHolder viewHolder, ListItem listItem) {
        Log.d(LOGVAR, "setFileDownloadingListItem" + listItem.getIsDownloading() + ":" + listItem.getMediaFile());
        if (listItem.getIsDownloading()) {
            viewHolder.progressBar.setVisibility(View.VISIBLE);
            viewHolder.thumbnailImage.setClickable(false);
            viewHolder.downloadIcon.setVisibility(View.GONE);

        } else {
            viewHolder.progressBar.setVisibility(View.GONE);
            viewHolder.thumbnailImage.setClickable(true);
        }

    }

    private void setLookToNotPlaylistItem(ElementViewHolder viewHolder) {
//        viewHolder.priceText.setVisibility(View.INVISIBLE);
        viewHolder.playlistIcon.setColorFilter(Color.WHITE);
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
        configureListItemLook(viewHolder, rowData);
        Log.d("VideosAdapter", "onBindViewHolder");

        configureListItemListeners(viewHolder, position);
        elementViewHolder = viewHolder;
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }


    private void alertForDownload(final ElementViewHolder viewHolder, final ListItem listItem, final int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mainActivity);
        alertDialog.setTitle(mainActivity.getResources().getString(R.string.alert_download_title));
        alertDialog.setMessage(mainActivity.getResources().getString(R.string.alert_download_summary));
        alertDialog.setPositiveButton(mainActivity.getResources().getString(R.string.download), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                viewHolder.progressBar.setVisibility(View.VISIBLE);
                viewHolder.thumbnailImage.setClickable(false);
                viewHolder.downloadIcon.setVisibility(View.GONE);
                // mainActivity.downloadVideo(viewHolder, listItem);
                //mainActivity.downloadVideo(viewHolder, listItem);
              /*  Intent intent = new Intent(mainActivity, DownloadService.class);
                intent.putExtra("url", "https://s3-us-west-1.amazonaws.com/babysbrilliant-media/Gravityfinal.mp4");
                mainActivity.startService(intent);*/
                new DownloadAsync(mainActivity, viewHolder, listItem, VideosAdapter.this, position, listItem.getMediaFile()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                //download(listItem.getMediaFile());
            }
        });
        alertDialog.setNegativeButton(mainActivity.getResources().getString(R.string.cancel), null);
        AlertDialog builder = alertDialog.create();
        builder.show();
    }

    @Override
    public void onDownloadComplete(int position) {
        notifyDataSetChanged();
    }


}
