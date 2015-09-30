package com.thecodebuilders.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.thecodebuilders.application.ApplicationContextProvider;
import com.thecodebuilders.babysbrilliant.ListItem;
import com.thecodebuilders.babysbrilliant.MainActivity;
import com.thecodebuilders.babysbrilliant.R;
import com.thecodebuilders.beans.Playlist;
import com.thecodebuilders.network.VolleySingleton;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


/**
 * Created by aaronnicholson on 8/17/15.
 */
public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.ElementViewHolder> {
    private final String LOGVAR = "VideosAdapter";
    private ArrayList<ListItem> elements;
    private final Context appContext = ApplicationContextProvider.getContext();
    ArrayList<JSONObject> assetsList;
    MainActivity mainActivity;
    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;

    public VideosAdapter(ArrayList listData, MainActivity mainActivity) {
        volleySingleton = VolleySingleton.getInstance();
        imageLoader = volleySingleton.getImageLoader();
        assetsList = listData;
        this.mainActivity = mainActivity;
        parseListItems(assetsList.size());

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

                ListItem listItem = new ListItem(itemJSON, name, playInline, imageResource, mediaFile, price, category, isSection, isPurchased, isPlaylistItem, isPlaylist, isFavorite, appContext);

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

        setThumbnailImage(viewHolder, listItem);

        viewHolder.itemView.setTag(listItem);
    }

    private void setThumbnailImage(ElementViewHolder viewHolder, ListItem listItem) {
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
                loadLocalSavedImage(fileName, viewHolder);
            } catch (FileNotFoundException localNotFoundError) {
                localNotFoundError.printStackTrace();
                //if that's not there, then get the real image from the server
                loadImageFromServer(viewHolder, listItem);
            }
        }
    }

    private void showPlaceHolderImage(ElementViewHolder viewHolder) {
        InputStream stream = null;
        try {
            stream = appContext.getAssets().open("thumb_placeholder.png");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        Drawable drawable = Drawable.createFromStream(stream, null);
        viewHolder.thumbnailImage.setImageDrawable(drawable);
    }

    private void loadImageFromServer(final ElementViewHolder viewHolder, final ListItem listItem) {

        final String fileName = listItem.getImageResource();
        if(fileName !=null) {

            String mediaURL = appContext.getResources().getString(R.string.media_url) + fileName;
            imageLoader.get(mediaURL, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean stillLoading) {
                    if(stillLoading) {
                        showPlaceHolderImage(viewHolder);
                    } else {
                        //show and save the bitmap
                        Bitmap loadedBitmap = imageContainer.getBitmap();
                        Bitmap savedBitmap = Bitmap.createBitmap(loadedBitmap);
                        viewHolder.thumbnailImage.setImageBitmap(loadedBitmap);
                        saveThumbToLocalFile(fileName, savedBitmap, viewHolder);
                    }

                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.e(LOGVAR, volleyError.getLocalizedMessage());
                    showPlaceHolderImage(viewHolder);
                }
            });
        }
    }

    private void saveThumbToLocalFile(String fileName, final Bitmap bitmap, ElementViewHolder viewHolder) {
        FileOutputStream fileOutputStream = null;
        if(bitmap == null) {
            Log.d(LOGVAR, "saving bitmap is null");
        } else {
            try {
                fileOutputStream = appContext.openFileOutput(fileName, Context.MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.PNG, 85, fileOutputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadLocalSavedImage(String fileName, ElementViewHolder viewHolder) throws FileNotFoundException {
        File file = new File(appContext.getFilesDir(), fileName);
        Bitmap loadedBitmap = BitmapFactory.decodeStream(new FileInputStream(file));
        viewHolder.thumbnailImage.setImageBitmap(loadedBitmap);
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
        //remove listIncrement and replace with playlist chooser
//        listIncrement++;
        mainActivity.addToPlaylist(listItem.getRawJSON());


    }

    private void thumbnailClicked(int position, ElementViewHolder thisViewHolder) {
        ListItem listItem = elements.get(position);

        if (listItem.isPurchasable() && listItem.isPurchased()) {
            //play in the main player
            mainActivity.playVideo(listItem.getMediaFile());
            //if it has not been purchased already
        } else {
            //TODO: do actual purchase round trip here
            listItem.setIsPurchased(true);
            setLookToPurchased(thisViewHolder);
            mainActivity.addToPurchased(listItem.getRawJSON());
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
        viewHolder.favoritesIcon.setVisibility(View.VISIBLE);
        viewHolder.playlistIcon.setVisibility(View.VISIBLE);
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

    //just sets java handles for the layout items configured in the xml doc.
    public class ElementViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleText;
        private final ImageView thumbnailImage;
        private final TextView priceText;
        private final RelativeLayout textBackground;
        private final ImageView favoritesIcon;
        private final ImageView playlistIcon;
        private final TextureView videoView;

        public ElementViewHolder(View itemView) {
            super(itemView);
            thumbnailImage = (ImageView) itemView.findViewById(R.id.thumbnailImage);
            titleText = (TextView) itemView.findViewById(R.id.titleText);
            priceText = (TextView) itemView.findViewById(R.id.priceText);
            textBackground = (RelativeLayout) itemView.findViewById(R.id.textBackground);
            favoritesIcon = (ImageView) itemView.findViewById(R.id.favorites_icon);
            playlistIcon = (ImageView) itemView.findViewById(R.id.playlist_icon);
            videoView = (TextureView) itemView.findViewById(R.id.video_view_inline);
        }

    }
}
