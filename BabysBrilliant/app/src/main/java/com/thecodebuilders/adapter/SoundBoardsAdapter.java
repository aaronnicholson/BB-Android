package com.thecodebuilders.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.thecodebuilders.application.ApplicationContextProvider;
import com.thecodebuilders.babysbrilliant.ListItem;
import com.thecodebuilders.babysbrilliant.MainActivity;
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
public class SoundBoardsAdapter extends RecyclerView.Adapter<SoundBoardsAdapter.ElementViewHolder> {
    private final String LOGVAR = "SoundBoardsAdapter";
    private ArrayList<ListItem> elements;
    private final Context appContext = ApplicationContextProvider.getContext();
    ArrayList<JSONArray> products = new ArrayList<JSONArray>();
    ArrayList<JSONObject> assetsList;
    MainActivity mainActivity;
    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;
    private boolean isTappedSetting = true;


    public SoundBoardsAdapter(ArrayList listData, MainActivity mainActivity) {
        volleySingleton = VolleySingleton.getInstance();
        imageLoader = volleySingleton.getImageLoader();
        assetsList = listData;
        this.mainActivity = mainActivity;
        parseListItems(assetsList.size());

    }

    public SoundBoardsAdapter(ArrayList listData, MainActivity mainActivity, boolean isSettingTap) {
        volleySingleton = VolleySingleton.getInstance();
        imageLoader = volleySingleton.getImageLoader();
        assetsList = listData;
        this.isTappedSetting = isSettingTap;
        this.mainActivity = mainActivity;
        parseListItems(assetsList.size());

    }

    //This gets the data ready for the adapter to use in order to set up the view for each list item.
    //It takes the data from the ArrayList that is passed in and parses it into the ListItem class.
    //Then it adds each ListItem instance to the elements ArrayList.
    private void parseListItems(int listLength) {
        elements = new ArrayList<ListItem>(listLength);
        products = new ArrayList<JSONArray>();
        Log.d(LOGVAR, "Assets:" + assetsList);
        for (int i = 0; i < listLength; i++) {
            JSONObject rawJSON;
            String name;
            String imageResource;
            String category = "";
            String mediaFile = "";
            Boolean isSection = true;
            Boolean playInline = false;
            String price = null;
            Boolean isPurchased = null;
            Boolean isPlaylistItem = null;
            Boolean isPlaylist = null;
            Boolean isFavorite = null;

            try {
                JSONObject itemJSON = assetsList.get(i);
                rawJSON = itemJSON;
                name = StringEscapeUtils.unescapeJava(itemJSON.getString("name"));
                imageResource = itemJSON.getString("thumb");
                if (!itemJSON.isNull("cat")) category = itemJSON.getString("cat");
                JSONArray subcategories = itemJSON.getJSONArray("subSubCategories");
                //if (!itemJSON.isNull("products"))
                //for(int j = 0; j < subcategories.length(); j++) {
                //JSONObject object = subcategories.getJSONObject(j);
                products.add(itemJSON.getJSONArray("subSubCategories"));
                // }

                mediaFile = itemJSON.getString("preview");

                ListItem listItem = new ListItem(rawJSON, name, playInline, imageResource, mediaFile, price, category, isSection,
                        isPurchased, isPlaylistItem, isPlaylist, isFavorite, appContext, false, MainActivity.DOWNLOAD_ID);

                elements.add(listItem);//TODO: make image dynamic
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

    }

    private void configureListItemListeners(ElementViewHolder viewHolder, final int position) {
        final ElementViewHolder thisViewHolder = viewHolder;
        viewHolder.thumbnailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTappedSetting)
                    thumbnailClicked(position, thisViewHolder);
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

    private void configureListItemLook(final ElementViewHolder viewHolder, final ListItem listItem) {

        //set text
        viewHolder.titleText.setText(listItem.getTitle());

        //set font
        viewHolder.titleText.setTypeface(mainActivity.proximaBold);
        viewHolder.previewIcon.setTypeface(mainActivity.fontAwesome);

        //hide text background for certain sections
        //for music, hide subcategory and product text background
        if (!listItem.doShowBackground()) {
            viewHolder.textBackground.setVisibility(View.INVISIBLE);
        }

        //set text size differently if it is a section
        viewHolder.titleText.setTextSize(16);

        //TODO: Add check for null string of file name
        if (listItem.isSection() && !listItem.isPurchasable()) {
            viewHolder.previewIcon.setVisibility(View.VISIBLE);
        } else {
            viewHolder.previewIcon.setVisibility(View.INVISIBLE);

        }

        CommonAdapterUtility.setThumbnailImage(listItem, viewHolder.thumbnailImage);

        viewHolder.itemView.setTag(listItem);
    }


    private void previewClicked(int position) {
        ListItem listItem = elements.get(position);
        if (!listItem.getMediaFile().equals(""))
            mainActivity.playVideo(listItem.getMediaFile(), false);
    }

    private void thumbnailClicked(int position, ElementViewHolder thisViewHolder) {

        updateThumbnailList(position);
    }

    private void updateThumbnailList(int position) {
        setSectionTitle(position);
        mainActivity.configureThumbnailList(products.get(position), "");
    }

    private void setSectionTitle(int position) {
        //set up section title
        if (!assetsList.get(position).isNull("name")) {
            try {
                mainActivity.setSectionTitle(assetsList.get(position).getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
        private final RelativeLayout textBackground;
        private final TextView previewIcon;

        public ElementViewHolder(View itemView) {
            super(itemView);
            thumbnailImage = (ImageView) itemView.findViewById(R.id.thumbnailImage);
            titleText = (TextView) itemView.findViewById(R.id.titleText);
            textBackground = (RelativeLayout) itemView.findViewById(R.id.textBackground);
            previewIcon = (TextView) itemView.findViewById(R.id.preview_icon);
        }

    }
}
