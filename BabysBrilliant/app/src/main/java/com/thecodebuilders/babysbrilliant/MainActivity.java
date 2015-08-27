package com.thecodebuilders.babysbrilliant;

import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

public class MainActivity extends AppCompatActivity {
    private static String LOGVAR = "MainActivity";
    private static String assetsURL;
    private RecyclerView thumbnailList;
    RequestQueue queue;
    String assetsString;
    JSONArray assetsJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //do not show the action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        assetsURL = getString(R.string.assets_url);

        getJSON();

        setUpThumbnailList();


    }

    private void setUpThumbnailList() {
        //setup recycler view
        thumbnailList = (RecyclerView) findViewById(R.id.thumbnail_list);
        thumbnailList.setHasFixedSize(true);//TODO: should this be set?

        //set the layout manager
        final LinearLayoutManager thumbnailListLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        thumbnailListLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        thumbnailListLayoutManager.scrollToPosition(0); //the index of the list item to start at
        thumbnailList.setLayoutManager(thumbnailListLayoutManager);


    }

    public void getJSON() {
        queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, assetsURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                assetsString = Uri.decode(response);
                Log.d(LOGVAR, "RESPONSE:" + assetsString);

                try {

                    JSONArray jsonData = new JSONArray(assetsString);
//
//                    for (int i = 0; i < jsonData.getJSONObject(0).length(); i++) {
//                        Log.d(LOGVAR, i + ": " + jsonData.getJSONObject(0).getJSONObject(i).toString());
//                    }

//                    Log.d(LOGVAR, jsonData.getJSONObject(0).getJSONObject("0").toString());

                    assetsJSON = jsonData;

                    //set the thumbnail list adapter so it will display the items
                    final ThumbnailListAdapter adapter = new ThumbnailListAdapter(getApplicationContext(), assetsJSON);
                    thumbnailList.setAdapter(adapter);
                } catch (Throwable t) {
                    Log.e(LOGVAR, "Could not parse malformed JSON");
                }

                //TODO: handle for no internet connection
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(LOGVAR, "VOLLEY ERROR: " + error.getMessage());
            }
        });

        queue.add(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
