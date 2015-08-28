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

import org.json.JSONArray;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by aaronnicholson on 8/17/15.
 */
public class ThumbnailListAdapter extends RecyclerView.Adapter<ThumbnailListAdapter.ElementViewHolder> {
    private final String LOGVAR = "ThumbnailListAdapter";
    private final ArrayList<ListItem> elements;
    private final Context appContext;
    ArrayList<JSONArray> products = new ArrayList<JSONArray>();
    ThumbnailView customView;
    JSONArray assetsJSON;
    Boolean isSubcategory = false;
    String category = "";

    public ThumbnailListAdapter(Context context, JSONArray jsonData) {
        //pass in the application context for use in this code
        appContext = context;
        assetsJSON = jsonData;
        int listLength = assetsJSON.length();

        elements = new ArrayList<ListItem>(listLength);

        for (int i=0; i< listLength; i++) {
            String name = "";
            String thumb = "com.babybrilliant.babybrilliant.movie_animated01.png";
            String category = "";
            try {
                if(assetsJSON.getJSONObject(i).isNull("name")) {
                    name = assetsJSON.getJSONObject(i).getString("title");
                    isSubcategory = false;
                } else {
                    name = assetsJSON.getJSONObject(i).getString("name");
                    products.add(assetsJSON.getJSONObject(i).getJSONArray("products"));
                    isSubcategory = true;
                }

                thumb = assetsJSON.getJSONObject(i).getString("thumb");
                category = assetsJSON.getJSONObject(i).getString("cat");

            } catch (Throwable t) {
                Log.e(LOGVAR, "Could not parse malformed JSON");
            }
            elements.add(new ListItem(name, thumb, category, isSubcategory, appContext)); //TODO: make image dynamic, and rename all to lower case
        }
    }

    @Override
    public ElementViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View rowView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_layout, viewGroup, false);

        return new ElementViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(ElementViewHolder viewHolder, final int position) {
        final ListItem rowData = elements.get(position);

        viewHolder.titleText.setText(rowData.getTitle());

        Typeface typeFace=Typeface.createFromAsset(appContext.getAssets(), "fonts/ProximaNovaSoft-Bold.otf");
        viewHolder.titleText.setTypeface(typeFace);

        //hide text background for certain sections
        //for music, hide subcategory and product text background
        if(!rowData.doShowBackground()) {
            viewHolder.textBackground.setVisibility(View.INVISIBLE);
        }

        //for soundboards, hide product footer entirely
        if (!rowData.doShowText()) {
            viewHolder.titleText.setVisibility(View.INVISIBLE);
        }

        //set text size differently if it is a subcategory
        if(isSubcategory) {
            viewHolder.titleText.setTextSize(16);
        } else {
            viewHolder.titleText.setTextSize(13);
        }

        //load image from assets folder
        try {
            InputStream stream = appContext.getAssets().open(rowData.getImageResource());
            Drawable drawable = Drawable.createFromStream(stream, null);
            viewHolder.thumbnailImage.setImageDrawable(drawable);
        } catch(Exception e) {
            Log.e("ListItem", e.getMessage());
        }

        viewHolder.itemView.setTag(rowData);


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isSubcategory) {
                    MainActivity.displayProducts(products.get(position));
                }
                else {
                    Log.d(LOGVAR, "PRODUCT TAP");
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    public class ElementViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleText;
        private final ImageView thumbnailImage;
        private final RelativeLayout textBackground;

        public ElementViewHolder(View itemView) {
            super(itemView);
            thumbnailImage = (ImageView) itemView.findViewById(R.id.thumbnailImage);
            titleText = (TextView) itemView.findViewById(R.id.titleText);
            textBackground = (RelativeLayout) itemView.findViewById(R.id.textBackground);

        }

    }


}
