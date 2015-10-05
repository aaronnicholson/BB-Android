package com.thecodebuilders.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thecodebuilders.babysbrilliant.R;

/**
 * Created by aaronnicholson on 9/30/15.
 */ //just sets java handles for the layout items configured in the xml doc.
public class ElementViewHolder extends RecyclerView.ViewHolder {
    public final TextView titleText;
    public final ImageView thumbnailImage;
    public final TextView priceText;
    public final RelativeLayout textBackground;
    public final ImageView favoritesIcon;
    public final ImageView playlistIcon;
    public final TextView previewIcon;
    public final TextureView videoView;
    public final RelativeLayout listItemContainer;

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
