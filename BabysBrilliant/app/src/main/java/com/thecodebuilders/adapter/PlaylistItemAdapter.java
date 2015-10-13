package com.thecodebuilders.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thecodebuilders.application.ApplicationContextProvider;
import com.thecodebuilders.babysbrilliant.ListItem;
import com.thecodebuilders.babysbrilliant.MainActivity;
import com.thecodebuilders.babysbrilliant.R;
import com.thecodebuilders.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by aaronnicholson on 8/17/15.
 */
public class PlaylistItemAdapter extends RecyclerView.Adapter<ElementViewHolder> {
    private final String LOGVAR = "PlaylistItemAdapter";
    private ArrayList<ListItem> elements;
    private final Context appContext = ApplicationContextProvider.getContext();
    ArrayList<JSONArray> products = new ArrayList<JSONArray>();
    ArrayList<JSONObject> assetsList;
    MainActivity mainActivity;
    private VolleySingleton volleySingleton;

    public PlaylistItemAdapter(ArrayList listData, MainActivity mainActivity) {
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

        for (int i = 0; i < listLength; i++) {
            String name;
            Boolean playInline = false;
            String imageResource;
            String price = appContext.getString(R.string.price_hard_coded);
            String category = "";
            String mediaFile = "";
            Boolean isPurchased = false;
            Boolean isFavorite = false;
            Boolean isSection = false;
            Boolean isPlaylistItem = true;
            Boolean isPlaylist = false;

            try {
                JSONObject itemJSON = assetsList.get(i);

                name = itemJSON.getString("title");

                imageResource = itemJSON.getString("thumb");
                if (!itemJSON.isNull("cat")) category = itemJSON.getString("cat");

                //for products, use the file attribute. For subcategories, use the preview attribute.
                if (!itemJSON.isNull("file")) {
                    mediaFile = itemJSON.getString("file");
                } else if (!itemJSON.isNull("preview")) {
                    mediaFile = itemJSON.getString("preview");
                }

                ListItem listItem = new ListItem(itemJSON, name, playInline, imageResource, mediaFile, price, category, isSection, isPurchased, isPlaylistItem, isPlaylist, isFavorite, appContext);

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
        viewHolder.deletePlaylistItemIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.removeItemFromPlaylist(position);
            }
        });

    }

    private void configureListItemLook(final ElementViewHolder viewHolder, final ListItem listItem) {
        viewHolder.bumpLeftIcon.setVisibility(View.VISIBLE);
        viewHolder.bumpRightIcon.setVisibility(View.VISIBLE);
        viewHolder.deletePlaylistItemIcon.setVisibility(View.VISIBLE);

        //set text
        viewHolder.titleText.setText(listItem.getTitle());

        //set font
        viewHolder.titleText.setTypeface(mainActivity.proximaBold);
        viewHolder.bumpLeftIcon.setTypeface(mainActivity.fontAwesome);
        viewHolder.bumpRightIcon.setTypeface(mainActivity.fontAwesome);
        viewHolder.deletePlaylistItemIcon.setTypeface(mainActivity.fontAwesome);

        //hide text background for certain sections
        //for music, hide subcategory and product text background
        if (!listItem.doShowBackground()) {
            viewHolder.textBackground.setVisibility(View.INVISIBLE);
        }

        viewHolder.titleText.setTextSize(13);

        CommonAdapterUtility.setThumbnailImage(listItem, viewHolder.thumbnailImage);

        viewHolder.itemView.setTag(listItem);
    }

    private void thumbnailClicked(int position, ElementViewHolder thisViewHolder) {
        ListItem listItem = elements.get(position);

        mainActivity.playVideo(listItem.getMediaFile());

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
