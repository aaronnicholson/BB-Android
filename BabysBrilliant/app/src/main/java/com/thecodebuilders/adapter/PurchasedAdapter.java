package com.thecodebuilders.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.thecodebuilders.application.ApplicationContextProvider;
import com.thecodebuilders.babysbrilliant.ListItem;
import com.thecodebuilders.babysbrilliant.MainActivity;
import com.thecodebuilders.babysbrilliant.R;
import com.thecodebuilders.model.Playlist;
import com.thecodebuilders.network.InputStreamVolleyRequest;
import com.thecodebuilders.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by aaronnicholson on 8/17/15.
 */
public class PurchasedAdapter extends RecyclerView.Adapter<ElementViewHolder> {
    private final String LOGVAR = "PurchasedAdapter";
    private ArrayList<ListItem> elements;
    private final Context appContext = ApplicationContextProvider.getContext();
    ArrayList<JSONArray> products = new ArrayList<JSONArray>();
    ArrayList<JSONObject> assetsList;
    MainActivity mainActivity;
    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;
    InputStreamVolleyRequest request;

    int listIncrement = 0; //for testing only

    private MediaPlayer mediaPlayer;

    public PurchasedAdapter(ArrayList listData, MainActivity mainActivity) {
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
        products = new ArrayList<JSONArray>();

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
            Boolean isSection;
            Boolean isPlaylistItem = false;
            Boolean isPlaylist = false;

            try {
                JSONObject itemJSON = assetsList.get(i);

                name = itemJSON.getString("title");
                isSection = false;

                //soundboards are meant to play inside their thumbnail. Anything marked with the playInline attribute in the JSON will do so.
                if (!itemJSON.isNull("playInline")) {
                    if (itemJSON.getString("playInline").equals("true")) playInline = true;
                }

                //look through items in the PURCHASED list
                //if this item is in there, mark it as purchased
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

        viewHolder.downloadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ListItem listItem = elements.get(position);

                if (!listItem.getMediaFile().equals("")) {
                    downloadVideo(listItem.getMediaFile());

                }
            }
        });


    }

    private void configureListItemLook(final ElementViewHolder viewHolder, final ListItem listItem) {

        viewHolder.videoView.setVisibility(View.INVISIBLE);

        viewHolder.favoritesIcon.setVisibility(View.INVISIBLE);
        viewHolder.playlistIcon.setVisibility(View.INVISIBLE);

        //set text
        viewHolder.titleText.setText(listItem.getTitle());

        //set font
        viewHolder.titleText.setTypeface(mainActivity.proximaBold);

        //hide text background for certain sections
        //for music, hide subcategory and product text background
        if (!listItem.doShowBackground()) {
            viewHolder.textBackground.setVisibility(View.INVISIBLE);
        }

        //for soundboards, hide product footer entirely
        if (!listItem.doShowText()) {
            viewHolder.titleText.setVisibility(View.INVISIBLE);
        }


        viewHolder.titleText.setTextSize(13);

        viewHolder.priceText.setVisibility(View.INVISIBLE);


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

        viewHolder.previewIcon.setVisibility(View.INVISIBLE);


        //default icon look
        viewHolder.playlistIcon.setColorFilter(Color.WHITE);
        viewHolder.favoritesIcon.setColorFilter(Color.WHITE);


        CommonAdapterUtility.setThumbnailImage(listItem, viewHolder.thumbnailImage);

        viewHolder.itemView.setTag(listItem);
    }

    private void previewClicked(int position) {
        ListItem listItem = elements.get(position);
        if (!listItem.getMediaFile().equals("")) {
            //mainActivity.playVideo(listItem.getMediaFile());
        }
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

        //TODO: Handle for soundboard items - using "bundle" in JSON

        if (listItem.isPurchasable()) {
            //if it has been purchased already
            if (listItem.isPurchased()) {
                playOrOpen(position, listItem, thisViewHolder);
                //if it has not been purchased already
            } else {
                listItem.setIsPurchased(true);
                setLookToPurchased(thisViewHolder);
                try {
                    mainActivity.addToPurchased(listItem.getRawJSON());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
        viewHolder.downloadIcon.setVisibility(View.VISIBLE);
        //Log.d(LOGVAR, "set to visible");
    }

    private void setLookToPlaylistItem(ElementViewHolder viewHolder) {
        viewHolder.priceText.setVisibility(View.INVISIBLE);
        viewHolder.playlistIcon.setColorFilter(Color.YELLOW);
    }

    private void setLookToNotPlaylistItem(ElementViewHolder viewHolder) {
        viewHolder.priceText.setVisibility(View.INVISIBLE);
        viewHolder.playlistIcon.setColorFilter(Color.WHITE);

    }

    private void setLookToPlaylist(ElementViewHolder viewHolder) {
        viewHolder.previewIcon.setVisibility(View.INVISIBLE);
    }


    public void downloadVideo(final String videoName) {
        String mUrl;
        mUrl = "https://s3-us-west-1.amazonaws.com/babysbrilliant-media/" + videoName;

        request = new InputStreamVolleyRequest(Request.Method.GET, mUrl, new Response.Listener<byte[]>() {

            @Override
            public void onResponse(byte[] response) {

                HashMap<String, Object> map = new HashMap<String, Object>();
                try {
                    if (response != null) {

                        File myDirectory = new File(
                                Environment.getExternalStorageDirectory(), "BABYBRILLIANT");

                        if (!myDirectory.exists()) {
                            myDirectory.mkdirs();
                        }

                        //Read file name from headers


                        try {
                            long lenghtOfFile = response.length;

                            //covert reponse to input stream
                            InputStream input = new ByteArrayInputStream(response);

                            File file = new File(myDirectory, videoName + ".mp4");
                            //map.put("resume_path", file.toString());
                            OutputStream output = new FileOutputStream(file);
                            byte data[] = new byte[1024];

                            long total = 0;

                            int count;
                            while ((count = input.read(data)) != -1) {
                                total += count;
                                output.write(data, 0, count);

                            }

                            output.flush();

                            output.close();
                            input.close();
                        } catch (IOException e) {
                            e.printStackTrace();

                        }
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    Log.d("KEY_ERROR", "UNABLE TO DOWNLOAD FILE");
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {

                Log.d("KEY_ERROR", "UNABLE TO DOWNLOAD FILE");
            }
        }, null);

        request.setRetryPolicy(new DefaultRetryPolicy(
                1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue mRequestQueue = Volley.newRequestQueue(mainActivity,
                new HurlStack());
        mRequestQueue.add(request);
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
                    playInlineVideo(listItem.getMediaFile(), viewHolder);
                } else {
                    //play in the main player
                   // mainActivity.playVideo(listItem.getMediaFile());
                }
            }
        }
    }

    public void playInlineVideo(String videoURL, final ElementViewHolder viewHolder) {
        String url = mainActivity.mediaURL + videoURL;
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
        mainActivity.configureThumbnailList(products.get(position), "videos");
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

}
