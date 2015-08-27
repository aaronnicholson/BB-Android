package com.thecodebuilders.babysbrilliant;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    ThumbnailView customView;
    JSONArray assetsJSON;

    public ThumbnailListAdapter(Context context, JSONArray jsonData) {
        //pass in the application context for use in this code
        appContext = context;
        assetsJSON = jsonData;

        Log.d(LOGVAR, "data: " + assetsJSON);

        elements = new ArrayList<ListItem>(20);
        for (int i=0; i< 20; i++) {
            elements.add(new ListItem("SAMPLE TEXT " + i, "com.babybrilliant.babybrilliant.movie_animated01.png", appContext)); //TODO: make image dynamic, and rename all to lower case
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

        //TODO: Some how fix rounded corners. These were used to accomplish it through a custom view drawn but it created a memory leak.
//        customView = new ThumbnailView(appContext);
//        viewHolder.thumbnailImage.addView(customView);

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
                Log.d("recycler", "touched: " + position);
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

        public ElementViewHolder(View itemView) {
            super(itemView);
            thumbnailImage = (ImageView) itemView.findViewById(R.id.thumbnailImage);
            titleText = (TextView) itemView.findViewById(R.id.titleText);
        }

    }


}
