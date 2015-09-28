package com.thecodebuilders.babysbrilliant;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import org.json.JSONArray;
import org.json.JSONException;
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
public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.ElementViewHolder> {
    private final String LOGVAR = "SectionAdapter";
    private ArrayList<ListItem> elements;
    private final Context appContext = ApplicationContextProvider.getContext();
    ArrayList<JSONArray> products = new ArrayList<JSONArray>();
    ArrayList<JSONObject> assetsList;
    MainActivity mainActivity;
    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;

    public SectionAdapter(ArrayList listData, MainActivity mainActivity) {
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
            String imageResource;
            String category = "";
            String mediaFile = "";
            Boolean isSection = true;
            Boolean playInline = false;
            String price = null;
            Boolean isPurchased = null;
            Boolean isPlaylistItem = null;
            Boolean isPlaylist  = null;
            Boolean isFavorite = null;

            try {
                JSONObject itemJSON = assetsList.get(i);
                rawJSON = itemJSON;
                name = itemJSON.getString("name");
                imageResource = itemJSON.getString("thumb");
                if (!itemJSON.isNull("cat")) category = itemJSON.getString("cat");

                if (!itemJSON.isNull("products"))
                    products.add(itemJSON.getJSONArray("products"));

                mediaFile = itemJSON.getString("preview");

                ListItem listItem = new ListItem(rawJSON, name, playInline, imageResource, mediaFile, price, category, isSection, isPurchased, isPlaylistItem, isPlaylist, isFavorite, appContext);

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

        viewHolder.previewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        viewHolder.itemView.setTag(listItem);
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

    private void previewClicked(int position) {
        ListItem listItem = elements.get(position);
        if (!listItem.getMediaFile().equals("")) mainActivity.playVideo(listItem.getMediaFile());
    }

    private void thumbnailClicked(int position, ElementViewHolder thisViewHolder) {
        updateThumbnailList(position);
    }

    private void updateThumbnailList(int position) {
        setSectionTitle(position);
        mainActivity.configureThumbnailList(products.get(position), "videos");
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
