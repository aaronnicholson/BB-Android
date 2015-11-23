package com.thecodebuilders.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thecodebuilders.babysbrilliant.R;

import java.util.ArrayList;
import java.util.HashMap;

public class DownloadPurchaseContentAdapter extends BaseAdapter {


    private Context context;
    private ArrayList<HashMap<String, String>> postItems;


    public DownloadPurchaseContentAdapter(Context context,
                                          ArrayList<HashMap<String, String>> arraylist) {
        this.context = context;

        postItems = arraylist;


    }

    @Override
    public int getCount() {
        return postItems.size();
    }

    @Override
    public Object getItem(int position) {
        return postItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.row_download_purchase_content, null);

        }
        HashMap<String, String> map = new HashMap<String, String>();
        map = postItems.get(position);

        TextView txttitle = (TextView) convertView
                .findViewById(R.id.title);
        txttitle.setText(map.get("title"));

       /* TextView txtname = (TextView) convertView.findViewById(R.id.date);
        txtname.setText(map.get("date"));*/


        return convertView;
    }


}
