package com.thecodebuilders.babysbrilliant;

import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by aaronnicholson on 8/17/15.
 */
public class ThumbnailListAdapter extends RecyclerView.Adapter<ThumbnailListAdapter.ElementViewHolder> {
    private final String LOGVAR = "ThumbnailListAdapter";
    private ArrayList<ListItem> elements;
    private final Context appContext = ApplicationContextProvider.getContext();
    ArrayList<JSONArray> products = new ArrayList<JSONArray>();
    ArrayList<JSONObject> assetsList;

    private MediaPlayer mediaPlayer;

    public ThumbnailListAdapter(ArrayList listData) {
        assetsList = listData;
        configureListItems(assetsList.size());
    }

    private void configureListItems(int listLength) {
        elements = new ArrayList<ListItem>(listLength);
        products = new ArrayList<JSONArray>();

        for (int i=0; i < listLength; i++) {
            JSONObject rawJSON;
            String name;
            Boolean playInline = false;
            String imageResource;
            String price = appContext.getString(R.string.price_hard_coded);
            String category;
            String mediaFile = "";
            Boolean isPurchased = false;
            Boolean isFavorite = false;
            Boolean isSubcategory;

            try {
                //subcategories have a name field instead of a title field. We use that difference to determine if it is a product or subcategory item.
                //if it is a list of products
                if(assetsList.get(i).isNull("name")) {
                    name = assetsList.get(i).getString("title");
                    isSubcategory = false;

                //if it is a list of subcategories
                } else {
                    name = assetsList.get(i).getString("name");
                    if(!assetsList.get(i).isNull("products")) products.add(assetsList.get(i).getJSONArray("products"));
                    isSubcategory = true;

                }

                //soundboards are meant to play inside their thumbnail. Anything marked with the playInline attribute in the JSON will do so.
                if(!assetsList.get(i).isNull("playInline")) {
                    if(assetsList.get(i).getString("playInline").equals("true")) playInline = true;
                }

                //look through items in the PURCHASED list
                //if this item is in there, mark it as purchased
                for (int purchasedIndex = 0; purchasedIndex < MainActivity.purchasedItems.length(); purchasedIndex++) {
                    //if it has no SKU, skip it
                    if(!assetsList.get(i).isNull("SKU")) {
                        if(assetsList.get(i).getString("SKU").equals(MainActivity.purchasedItems.getJSONObject(purchasedIndex).getString("SKU"))) {
                            isPurchased = true;
                        }
                    }

                }

                //look through items in the FAVORITES list
                //if this item is in there, mark it as favorite
                for (int favoriteIndex = 0; favoriteIndex < MainActivity.favoriteItems.size(); favoriteIndex++) {
                    //if it has no SKU, skip it
                    if(!assetsList.get(i).isNull("SKU")) {
                        if(assetsList.get(i).getString("SKU").equals(MainActivity.favoriteItems.get(favoriteIndex).getString("SKU"))) {
                            isFavorite = true;
                        }
                    }
                }

                rawJSON = assetsList.get(i);
                imageResource = assetsList.get(i).getString("thumb");
                category = assetsList.get(i).getString("cat");

                //for products, use the file attribute. For subcategories, use the preview attribute.
                if(!assetsList.get(i).isNull("file")) {
                    mediaFile = assetsList.get(i).getString("file");
                } else if(!assetsList.get(i).isNull("preview")) {
                    mediaFile = assetsList.get(i).getString("preview");
                }

                ListItem listItem = new ListItem(rawJSON, name, playInline, imageResource, mediaFile, price, category, isSubcategory, isPurchased, isFavorite, appContext);

                elements.add(listItem);//TODO: make image dynamic
            }
            catch (Throwable t) {
                Log.e(LOGVAR, "JSON Error " + t.getMessage() + assetsList);
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

        viewHolder.previewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previewClicked(position);
            }
        });

        viewHolder.playlistIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playlistClicked(position, thisViewHolder);
            }
        });


    }

    private void configureListItemLook(ElementViewHolder viewHolder, ListItem listItem) {

        viewHolder.videoView.setVisibility(View.INVISIBLE);

        viewHolder.favoritesIcon.setVisibility(View.INVISIBLE);
        viewHolder.playlistIcon.setVisibility(View.INVISIBLE);

        //set text
        viewHolder.titleText.setText(listItem.getTitle());
        viewHolder.priceText.setText(listItem.getPrice());

        //set font
        viewHolder.titleText.setTypeface(MainActivity.proximaBold);
        viewHolder.priceText.setTypeface(MainActivity.proximaBold);
        viewHolder.previewIcon.setTypeface(MainActivity.fontAwesome);

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
        if (listItem.isSubcategory()) {
            viewHolder.titleText.setTextSize(16);
        } else {
            viewHolder.titleText.setTextSize(13);
        }

        //hide price on subcategories
        if (!listItem.isPurchasable()) {
            viewHolder.priceText.setVisibility(View.INVISIBLE);
        }

        //if it has been purchased already
        if (listItem.isPurchased()) {
            setLookToPurchased(viewHolder);

        }

        if(listItem.isFavorite()) {
            setLookToFavorite(viewHolder);
        } else {
            setLookToNotFavorite(viewHolder);
        }

        //TODO: Add check for null string of file name
        if(listItem.isSubcategory() && !listItem.isPurchasable()) {
            viewHolder.previewIcon.setVisibility(View.VISIBLE);
        } else {
            viewHolder.previewIcon.setVisibility(View.INVISIBLE);

        }

        viewHolder.playlistIcon.setColorFilter(Color.WHITE);

        //load image from assets folder
        try {
            InputStream stream = appContext.getAssets().open(listItem.getImageResource());
            Drawable drawable = Drawable.createFromStream(stream, null);
            viewHolder.thumbnailImage.setImageDrawable(drawable);
        } catch (Exception e) {
            Log.e("ListItem", e.getMessage());
        }

        viewHolder.itemView.setTag(listItem);
    }

    private void previewClicked(int position) {
        ListItem listItem = elements.get(position);
        if(!listItem.getMediaFile().equals("")) MainActivity.playVideo(listItem.getMediaFile());
    }

    private void favoritesClicked(int position, ElementViewHolder thisViewHolder) {
        ListItem listItem = elements.get(position);

        if(listItem.isFavorite()) {
            //TODO: remove from favorites
            MainActivity.removeFromFavorites(listItem.getRawJSON());
            setLookToNotFavorite(thisViewHolder);
        } else {
            MainActivity.addToFavorites(listItem.getRawJSON());
            setLookToFavorite(thisViewHolder);
        }
    }

    private void playlistClicked(int position, ElementViewHolder thisViewHolder) {
        ListItem listItem = elements.get(position);

        MainActivity.addToPlaylist("test list", listItem.getRawJSON());
    }

    private void thumbnailClicked(int position, ElementViewHolder thisViewHolder) {
        ListItem listItem = elements.get(position);

        //TODO: Handle for soundboard items

        if (listItem.isPurchasable()) {
            //if it has been purchased already
            if (listItem.isPurchased()) {
                playOrOpen(position, listItem, thisViewHolder);
                //if it has not been purchased already
            } else {
                listItem.setIsPurchased(true);
                setLookToPurchased(thisViewHolder);
                MainActivity.addToPurchased(listItem.getRawJSON());
            }
        } else {
            //send the list of products for the clicked subcategory to make a new view showing them
            playOrOpen(position, listItem, thisViewHolder);
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
    }

    private void playOrOpen(int position, ListItem listItem, ElementViewHolder viewHolder) {
        //if it's a soundboard category
        if(listItem.isSubcategory()) {
            //set up section title
            if(!assetsList.get(position).isNull("name")) {
                try {
                    MainActivity.setSectionTitle(assetsList.get(position).getString("name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            updateThumbnailList(position);
        } else {
            //if it has a file name, play it
            if(!listItem.getMediaFile().equals("")) {
                if(listItem.playInline()) {
                    //play inside the thumbnail
//                    MainActivity.playVideo(listItem.getMediaFile());
                    playInlineVideo(listItem.getMediaFile(), viewHolder);
                } else {
                    //play in the main player
                    MainActivity.playVideo(listItem.getMediaFile());
                }
            }
        }
    }

    public void playInlineVideo(String videoURL, final ElementViewHolder viewHolder) {
        String url = MainActivity.mediaURL + videoURL;
        viewHolder.videoView.setAlpha(0);
        viewHolder.videoView.setVisibility(View.VISIBLE);

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url); //this triggers the listener, which plays the video
        } catch (IOException e) {
            e.printStackTrace();
        }

        //TODO: show preloader

        //TODO: put this on a separate thread
        //TODO: download the video to local storage, then play
        viewHolder.videoView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                Surface s = new Surface(surface);

                try {
                    mediaPlayer.setSurface(s);
                    mediaPlayer.prepare();
//                    mediaPlayer.setOnBufferingUpdateListener(this);
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            viewHolder.videoView.setAlpha(1);
                            viewHolder.videoView.setVisibility(View.INVISIBLE);
                        }
                    });
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            viewHolder.videoView.setAlpha(1);
                        }
                    });
//                    mediaPlayer.setOnVideoSizeChangedListener(this);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.start();
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (SecurityException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }


            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });


    }

    private void updateThumbnailList(int position) {
        MainActivity.configureThumbnailList(products.get(position));
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
        private final TextView previewIcon;
        private final TextureView videoView;
        private final RelativeLayout listItemContainer;

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
        }

    }
}
