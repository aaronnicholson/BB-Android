package com.thecodebuilders.babysbrilliant;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
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
    JSONArray assetsJSON;


    public ThumbnailListAdapter(JSONArray jsonData) {
        assetsJSON = jsonData;

        configureListItems(assetsJSON.length());
    }

    private void configureListItems(int listLength) {
        elements = new ArrayList<ListItem>(listLength);
        products = new ArrayList<JSONArray>();

        for (int i=0; i< listLength; i++) {
            JSONObject rawJSON;
            String name;
            String thumb;
            String price = appContext.getString(R.string.price_hard_coded);
            String category;
            Boolean isPurchased = false;
            Boolean isSubcategory;

            try {
                //subcategories have a title field instead of a name field. We use that difference to determine if it is a product or subcategory item.
                //if it is a list of products
                if(assetsJSON.getJSONObject(i).isNull("name")) {
                    name = assetsJSON.getJSONObject(i).getString("title");
                    isSubcategory = false;

                //if it is a list of subcategories
                } else {
                    name = assetsJSON.getJSONObject(i).getString("name");
                    products.add(assetsJSON.getJSONObject(i).getJSONArray("products"));
                    isSubcategory = true;

                }

                //look through items in the purchased list
                //if this item is in there, mark it as purchased
                for (int purchasedIndex = 0; purchasedIndex < MainActivity.purchasedItems.length(); purchasedIndex++) {
                    //if it has no SKU, skip it
                    if(!assetsJSON.getJSONObject(purchasedIndex).isNull("SKU")) {
                        if(assetsJSON.getJSONObject(i).getString("SKU").equals(MainActivity.purchasedItems.getJSONObject(purchasedIndex).getString("SKU"))) {
                            isPurchased = true;
                        }
                    }

                }

                rawJSON = assetsJSON.getJSONObject(i);
                thumb = assetsJSON.getJSONObject(i).getString("thumb");
                category = assetsJSON.getJSONObject(i).getString("cat");


                elements.add(new ListItem(rawJSON, name, thumb, price, category, isSubcategory, isPurchased, appContext)); //TODO: make image dynamic, and rename all to lower case
            } catch (Throwable t) {
                Log.e(LOGVAR, "Could not parse malformed JSON");
            }
        }

    }

    private void configureListItemListeners(ElementViewHolder viewHolder, final int position) {
        final ElementViewHolder thisViewHolder = viewHolder;
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listItemClicked(position, thisViewHolder);
            }
        });
    }

    private void listItemClicked(int position, ElementViewHolder thisViewHolder) {
        ListItem listItem = elements.get(position);

        //TODO: Handle for soundboard items

        if (listItem.isPurchasable()) {
            //if it has been purchased already
            if (listItem.isPurchased()) {
                playOrOpen(position, listItem);
                //if it has not been purchased already
            } else {
                listItem.setIsPurchased(true);
                thisViewHolder.priceText.setVisibility(View.INVISIBLE);
                MainActivity.addToPurchased(listItem.getRawJSON());
            }
        } else {
            //send the list of products for the clicked subcategory to make a new view showing them
            playOrOpen(position, listItem);
        }
    }

    private void playOrOpen(int position, ListItem listItem) {
        //if it's a soundboard category
        if(listItem.isSubcategory()) {
            updateThumbnailList(position);
        } else {
            Toast.makeText(appContext, "That item has already been purchased", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateThumbnailList(int position) {
        MainActivity.configureThumbnailList(products.get(position));
    }

    private void configureListItemLook(ElementViewHolder viewHolder, ListItem listItem) {
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
            viewHolder.priceText.setVisibility(View.INVISIBLE);
        }

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

        public ElementViewHolder(View itemView) {
            super(itemView);
            thumbnailImage = (ImageView) itemView.findViewById(R.id.thumbnailImage);
            titleText = (TextView) itemView.findViewById(R.id.titleText);
            priceText = (TextView) itemView.findViewById(R.id.priceText);
            textBackground = (RelativeLayout) itemView.findViewById(R.id.textBackground);

        }

    }


}
