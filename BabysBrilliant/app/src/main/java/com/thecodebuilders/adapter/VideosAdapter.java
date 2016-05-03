package com.thecodebuilders.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.thecodebuilders.application.ApplicationContextProvider;
import com.thecodebuilders.babysbrilliant.ListItem;
import com.thecodebuilders.babysbrilliant.MainActivity;
import com.thecodebuilders.babysbrilliant.R;
import com.thecodebuilders.model.Playlist;
import com.thecodebuilders.network.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by aaronnicholson on 8/17/15.
 */
public class VideosAdapter extends RecyclerView.Adapter<ElementViewHolder> {

    private final String LOGVAR = "VideosAdapter";
    private ArrayList<ListItem> elements;
    private final Context appContext = ApplicationContextProvider.getContext();
    ArrayList<JSONObject> assetsList;
    MainActivity mainActivity;
    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;
    public String mediaURL = appContext.getString(R.string.media_url);

    public VideosAdapter(ArrayList listData, MainActivity mainActivity) {
        volleySingleton = VolleySingleton.getInstance();
        imageLoader = volleySingleton.getImageLoader();
        assetsList = listData;
        this.mainActivity = mainActivity;
        parseListItems(assetsList.size());

        Log.d(LOGVAR, "VIDEO assets: " + assetsList);

    }

    //This gets the data ready for the adapter to use in order to set up the view for each list item.
    //It takes the data from the ArrayList that is passed in and parses it into the ListItem class.
    //Then it adds each ListItem instance to the elements ArrayList.
    private void parseListItems(int listLength) {
        elements = new ArrayList<ListItem>(listLength);

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

            try {
                JSONObject itemJSON = assetsList.get(i);

                name = itemJSON.getString("title");
                for (int purchasedIndex = 0; purchasedIndex < mainActivity.purchasedItems.length(); purchasedIndex++) {
                    //if it has no SKU, skip it
                    if (!itemJSON.isNull("SKU")) {
                        if (itemJSON.getString("SKU").equals(mainActivity.purchasedItems.getJSONObject(purchasedIndex).getString("SKU"))) {
                            isPurchased = true;
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

                mediaFile = itemJSON.getString("file");

                ListItem listItem = new ListItem(itemJSON, name, playInline, imageResource, mediaFile, price, category, isSection,
                        isPurchased, isPlaylistItem, isPlaylist, isFavorite, appContext);

                elements.add(listItem);//TODO: make image dynamic
            } catch (Throwable t) {
                //Log.e(LOGVAR, "JSON Error " + t.getMessage() + assetsList);
            }
        }

    }

    private void configureListItemListeners(ElementViewHolder viewHolder, final int position) {
        final ElementViewHolder thisViewHolder = viewHolder;
        viewHolder.thumbnailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thumbnailClicked(position, thisViewHolder);
            }
        });

        viewHolder.favoritesIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favoritesClicked(position, thisViewHolder);
            }
        });

        viewHolder.playlistIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playlistClicked(position, thisViewHolder);
            }
        });
        viewHolder.downloadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListItem listItem = elements.get(position);
                mainActivity.downloadVideo(listItem.getMediaFile());
            }
        });


    }

    private void configureListItemLook(final ElementViewHolder viewHolder, final ListItem listItem) {

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

        //default icon look
        viewHolder.playlistIcon.setColorFilter(Color.WHITE);
        viewHolder.favoritesIcon.setColorFilter(Color.WHITE);

        //hide text background for certain sections
        //for music, hide subcategory and product text background
        if (!listItem.doShowBackground()) {
            viewHolder.textBackground.setVisibility(View.INVISIBLE);
        }

        viewHolder.titleText.setTextSize(13);


        //if it has been purchased already
        if (listItem.isPurchased()) {
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

        CommonAdapterUtility.setThumbnailImage(listItem,viewHolder.thumbnailImage);

        viewHolder.itemView.setTag(listItem);
    }

    private void favoritesClicked(int position, ElementViewHolder thisViewHolder) {
        ListItem listItem = elements.get(position);

        if (listItem.isFavorite()) {
            //TODO: remove from favorites
            mainActivity.removeFromFavorites(listItem.getRawJSON());
            setLookToNotFavorite(thisViewHolder);
        } else {
            mainActivity.addToFavorites(listItem.getRawJSON());
            setLookToFavorite(thisViewHolder);
        }
    }

    private void playlistClicked(int position, ElementViewHolder thisViewHolder) {
        ListItem listItem = elements.get(position);
        mainActivity.addToPlaylist(listItem.getRawJSON());
    }

    private void thumbnailClicked(int position, ElementViewHolder thisViewHolder) {
        ListItem listItem = elements.get(position);

        if (listItem.isPurchasable() && listItem.isPurchased()) {
            String videoURL = listItem.getMediaFile();
            String fileLocation = appContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) +  "/" + videoURL;
            File file = new File(fileLocation);
            Log.d(LOGVAR, "FILE location: " + fileLocation);
//            Log.d(LOGVAR, "data dir:" + appContext.getApplicationInfo().dataDir);

            if(file.exists()) {
                Log.d(LOGVAR, "FILE EXISTS");
                mainActivity.playVideo(fileLocation);
            //otherwise, go get it
            } else {
                Log.d(LOGVAR, "FILE DOES NOT EXIST");
                //TODO: Stop multiple downloads of the same file
                alertForDownload(listItem.getMediaFile());
            }

        } else {
            //TODO: do actual purchase round trip here
            listItem.setIsPurchased(true);
            setLookToPurchased(thisViewHolder);
            try {
                mainActivity.addToPurchased(listItem.getRawJSON());
            } catch (JSONException e) {
                e.printStackTrace();
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

        configureListItemListeners(viewHolder, position);

    }

    @Override
    public int getItemCount() {
        return elements.size();
    }


    private void alertForDownload(final String videoName){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mainActivity);
        alertDialog.setTitle(mainActivity.getResources().getString(R.string.alert_download_title));
        alertDialog.setMessage(mainActivity.getResources().getString(R.string.alert_download_summary));
        alertDialog.setPositiveButton(mainActivity.getResources().getString(R.string.download), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mainActivity.downloadVideo(videoName);
            }
        });
        alertDialog.setNegativeButton(mainActivity.getResources().getString(R.string.cancel), null);
        AlertDialog builder = alertDialog.create();
        builder.show();
    }

}
