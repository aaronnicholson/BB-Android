package com.thecodebuilders.babysbrilliant;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public ThumbnailListAdapter(ArrayList listData) {
        assetsList = listData;
        configureListItems(assetsList.size());
    }

    private void configureListItems(int listLength) {
        elements = new ArrayList<ListItem>(listLength);
        products = new ArrayList<JSONArray>();

        Log.d(LOGVAR, "length: " + listLength);

        for (int i=0; i < listLength; i++) {
            JSONObject rawJSON;
            String name;
            String imageResource;
            String price = appContext.getString(R.string.price_hard_coded);
            String category;
            String mediaFile = "";
            Boolean isPurchased = false;
            Boolean isFavorite = false;
            Boolean isSubcategory;

            try {
                //subcategories have a title field instead of a name field. We use that difference to determine if it is a product or subcategory item.
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

                if(!assetsList.get(i).isNull("file")) {
                    mediaFile = assetsList.get(i).getString("file");
                }

                elements.add(new ListItem(rawJSON, name, imageResource, mediaFile, price, category, isSubcategory, isPurchased, isFavorite, appContext)); //TODO: make image dynamic, and rename all to lower case
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

    private void thumbnailClicked(int position, ElementViewHolder thisViewHolder) {
        ListItem listItem = elements.get(position);

        //TODO: Handle for soundboard items

        if (listItem.isPurchasable()) {
            //if it has been purchased already
            if (listItem.isPurchased()) {
                playOrOpen(position, listItem);
                //if it has not been purchased already
            } else {
                listItem.setIsPurchased(true);
                setLookToPurchased(thisViewHolder);
                MainActivity.addToPurchased(listItem.getRawJSON());
            }
        } else {
            //send the list of products for the clicked subcategory to make a new view showing them
            playOrOpen(position, listItem);
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
        viewHolder.favoritesIcon.setVisibility(View.VISIBLE);
        viewHolder.playlistIcon.setVisibility(View.VISIBLE);
    }

    private void playOrOpen(int position, ListItem listItem) {
        //if it's a soundboard category
        if(listItem.isSubcategory()) {
            updateThumbnailList(position);
        } else {
            //if it has a file name, play it
            if(!listItem.getMediaFile().equals("")) MainActivity.playVideo(listItem.getMediaFile());
        }
    }

    private void updateThumbnailList(int position) {
        MainActivity.configureThumbnailList(products.get(position));
    }

    private void configureListItemLook(ElementViewHolder viewHolder, ListItem listItem) {
        viewHolder.favoritesIcon.setVisibility(View.INVISIBLE);
        viewHolder.playlistIcon.setVisibility(View.INVISIBLE);



        //set text
        viewHolder.titleText.setText(listItem.getTitle());
        viewHolder.priceText.setText(listItem.getPrice());

        //set font
        viewHolder.titleText.setTypeface(MainActivity.proximaBold);
        viewHolder.priceText.setTypeface(MainActivity.proximaBold);

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
        private final RelativeLayout listItemContainer;

        public ElementViewHolder(View itemView) {
            super(itemView);
            thumbnailImage = (ImageView) itemView.findViewById(R.id.thumbnailImage);
            titleText = (TextView) itemView.findViewById(R.id.titleText);
            priceText = (TextView) itemView.findViewById(R.id.priceText);
            textBackground = (RelativeLayout) itemView.findViewById(R.id.textBackground);
            favoritesIcon = (ImageView) itemView.findViewById(R.id.favorites_icon);
            playlistIcon = (ImageView) itemView.findViewById(R.id.playlist_icon);
            listItemContainer = (RelativeLayout) itemView.findViewById(R.id.list_item_container);
        }

    }


}
