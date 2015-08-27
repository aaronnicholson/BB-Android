package com.thecodebuilders.babysbrilliant;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

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

        setMenuWidth();

        //do not show the action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        assetsURL = getString(R.string.assets_url);

        getJSON();

        setUpThumbnailList();


    }

    private void setMenuWidth() {
        HorizontalScrollView menuScrollView = (HorizontalScrollView) findViewById(R.id.menu_scroll_view);
        ImageView logoView = (ImageView) findViewById(R.id.bblogo);
        ImageView settingsView = (ImageView) findViewById(R.id.settings);

        //determine screen size in dp
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int density = (int) displayMetrics.density;

        int margin = (int) getResources().getDimension(R.dimen.small_margin)/density;//to give scroll area its own margin
        int margins = margin*4; //to account for left and right margins of logo, scroll view and settings icon

        int menuHorizontalRoom = (int) (dpWidth - logoView.getMaxWidth()/displayMetrics.density - settingsView.getMaxWidth()/displayMetrics.density - margins);

        int convertedWidth = (int) convertDpToPixel(menuHorizontalRoom, this);
        menuScrollView.getLayoutParams().width = convertedWidth;

        //TODO: create indicator when there are items to scroll to
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

    //TODO: move these to a utils class
    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }
}
