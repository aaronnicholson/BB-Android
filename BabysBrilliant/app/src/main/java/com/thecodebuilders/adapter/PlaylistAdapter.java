package com.thecodebuilders.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thecodebuilders.application.ApplicationContextProvider;
import com.thecodebuilders.babysbrilliant.ListItem;
import com.thecodebuilders.babysbrilliant.MainActivity;
import com.thecodebuilders.babysbrilliant.ParentalChallengeScreen;
import com.thecodebuilders.babysbrilliant.R;
import com.thecodebuilders.network.VolleySingleton;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by aaronnicholson on 8/17/15.
 */
public class PlaylistAdapter extends RecyclerView.Adapter<ElementViewHolder> {
    private final String LOGVAR = "PlaylistAdapter";
    private ArrayList<ListItem> elements;
    private final Context appContext = ApplicationContextProvider.getContext();
    ArrayList<JSONArray> products = new ArrayList<JSONArray>();
    ArrayList<JSONObject> assetsList;
    MainActivity mainActivity;
    private VolleySingleton volleySingleton;


    public PlaylistAdapter(ArrayList listData, MainActivity mainActivity) {
        volleySingleton = VolleySingleton.getInstance();
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
        Log.e(LOGVAR, "Asset:" + assetsList);
        for (int i = 0; i < listLength; i++) {
            String name;
            Boolean playInline = false;
            String imageResource;
            String price = appContext.getString(R.string.price_hard_coded);
            String category = "";
            String mediaFile = "";
            Boolean isPurchased = false;
            Boolean isFavorite = false;
            Boolean isSection = true;
            Boolean isPlaylistItem = false;
            Boolean isPlaylist = true;

            try {
                JSONObject itemJSON = assetsList.get(i);


                name = StringEscapeUtils.unescapeJava(itemJSON.getString("name"));
                if (!itemJSON.isNull("products"))
                    products.add(itemJSON.getJSONArray("products"));

                imageResource = itemJSON.getString("thumb");
                if (!itemJSON.isNull("cat")) category = itemJSON.getString("cat");

                //for products, use the file attribute. For subcategories, use the preview attribute.
                if (!itemJSON.isNull("file")) {
                    mediaFile = itemJSON.getString("file");
                } else if (!itemJSON.isNull("preview")) {
                    mediaFile = itemJSON.getString("preview");
                }

                ListItem listItem = new ListItem(itemJSON, name, playInline, imageResource, mediaFile, price, category, isSection,
                        isPurchased, isPlaylistItem, isPlaylist, isFavorite, appContext, false, MainActivity.DOWNLOAD_ID);

                elements.add(listItem);//TODO: make image dynamic
            } catch (Throwable t) {
                Log.e(LOGVAR, "JSON Error " + t.getMessage() + assetsList);
            }
        }

    }

    private void configureListItemListeners(ElementViewHolder viewHolder, final int position) {
        final ElementViewHolder thisViewHolder = viewHolder;
        viewHolder.thumbnailImage.setTag(position);
        viewHolder.thumbnailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thumbnailClicked(position, thisViewHolder);
            }
        });
        viewHolder.deletePlaylistIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*Intent mainIntent1 = new Intent(mainIntent, ParentalChallengeScreen.class);
                mainIntent.putExtra("Key", "fav");*/
                mainActivity.startActivityForResult(new Intent(mainActivity, ParentalChallengeScreen.class).putExtra("Key", "PlayListAdapter").putExtra("pos", position), 1);
                // mainActivity.removePlaylist(position);
            }
        });
        viewHolder.editPlaylistIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editClicked(position);
            }
        });

    }

    private void configureListItemLook(final ElementViewHolder viewHolder, final ListItem listItem) {
        viewHolder.editPlaylistIcon.setVisibility(View.VISIBLE);
        viewHolder.deletePlaylistIcon.setVisibility(View.VISIBLE);

        //set text
        viewHolder.titleText.setText(listItem.getTitle());

        //set font
        viewHolder.titleText.setTypeface(mainActivity.proximaBold);
        viewHolder.deletePlaylistIcon.setTypeface(mainActivity.fontAwesome);

        viewHolder.editPlaylistIcon.setColorFilter(Color.WHITE);


        //hide text background for certain sections
        //for music, hide subcategory and product text background
        if (!listItem.doShowBackground()) {
            viewHolder.textBackground.setVisibility(View.INVISIBLE);
        }

        viewHolder.titleText.setTextSize(16);

        CommonAdapterUtility.setThumbnailImage(listItem, viewHolder.thumbnailImage);

        viewHolder.itemView.setTag(listItem);
    }

    private void thumbnailClicked(int position, ElementViewHolder thisViewHolder) {
        ListItem listItem = elements.get(position);
        JSONArray jsonArray = products.get(0);
        mainActivity.fileArrayList = new ArrayList<String>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                mainActivity.fileArrayList.add(jsonObject.getString("file"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mainActivity.toggle.isChecked())
            loopPlayList();
        /*String fileLocation = Environment.getExternalStorageDirectory()
                + "/" + mainActivity.getResources().getString(R.string.app_name) +
                "/" + mainActivity.fileArrayList.get(0);*/
        String fileLocation = mainActivity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)+"/"+ mainActivity.fileArrayList.get(0);

        mainActivity.playVideo(fileLocation, true);

        //TODO: play playlist

    }

    private void loopPlayList() {
        Log.d(LOGVAR,"loopPlayList");
        long time = 900000;

        if (mainActivity.checked1.isChecked()) {
            // 15 mints
            handlerDelayed(time);
        } else if (mainActivity.checked2.isChecked()) {
            // 30 mints
            handlerDelayed(time * 2);

        } else if (mainActivity.checked3.isChecked()) {
            //45 mints
            handlerDelayed(time * 3);

        } else if (mainActivity.checked4.isChecked()) {
            //60 mints
            handlerDelayed(time * 4);

        } else if (mainActivity.checked5.isChecked()) {
            //90 mints
            handlerDelayed(time * 6);
        }
    }

    private void handlerDelayed(long time) {
        Log.d(LOGVAR,"handlerDelayed"+time);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mainActivity.closeVideo();
            }
        }, time);

    }

    private void editClicked(int position) {
        //set up section title
        if (!assetsList.get(position).isNull("name")) {
            try {
                mainActivity.setSectionTitle(assetsList.get(position).getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mainActivity.activePlaylist = position;
        updateThumbnailList(position);
    }

    private void updateThumbnailList(int position) {
        mainActivity.configureThumbnailList(products.get(position), "playlistItems");
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
